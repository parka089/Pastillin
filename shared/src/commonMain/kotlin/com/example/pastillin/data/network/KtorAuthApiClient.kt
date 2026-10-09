package com.example.pastillin.data.network

import com.example.pastillin.data.datasource.LocalUserDataSource
import com.example.pastillin.data.model.AuthResponse
import com.example.pastillin.data.model.LoginRequest
import com.example.pastillin.data.model.RegisterRequest
import com.example.pastillin.data.model.UserDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class KtorAuthApiClient {

    private val jsonConfig = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    val httpClient = HttpClient(MockEngine) {
        install(ContentNegotiation) {
            json(jsonConfig)
        }
        engine {
            addHandler { request ->
                when (request.url.encodedPath) {
                    "/api/v1/login" -> {
                        val requestBodyText = when (val body = request.body) {
                            is OutgoingContent.ByteArrayContent -> body.bytes().decodeToString()
                            else -> ""
                        }
                        val loginReq = jsonConfig.decodeFromString<LoginRequest>(requestBodyText)

                        val users = LocalUserDataSource.getUsers()
                        val matchedUser = users.find {
                            it.username.equals(loginReq.username, ignoreCase = true) &&
                                    it.passwordHash == loginReq.passwordHash
                        }

                        if (matchedUser != null) {
                            val authResponse = AuthResponse(
                                success = true,
                                token = "ktor_jwt_token_${matchedUser.id}",
                                user = matchedUser
                            )
                            respond(
                                content = jsonConfig.encodeToString(AuthResponse.serializer(), authResponse),
                                status = HttpStatusCode.OK,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        } else {
                            val authResponse = AuthResponse(
                                success = false,
                                errorMessage = "Credenciales incorrectas. Verifique usuario y contraseña."
                            )
                            respond(
                                content = jsonConfig.encodeToString(AuthResponse.serializer(), authResponse),
                                status = HttpStatusCode.Unauthorized,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }
                    }

                    "/api/v1/register" -> {
                        val requestBodyText = when (val body = request.body) {
                            is OutgoingContent.ByteArrayContent -> body.bytes().decodeToString()
                            else -> ""
                        }
                        val regReq = jsonConfig.decodeFromString<RegisterRequest>(requestBodyText)

                        val existingUser = LocalUserDataSource.getUsers().find {
                            it.username.equals(regReq.username, ignoreCase = true)
                        }

                        if (existingUser != null) {
                            val authResponse = AuthResponse(
                                success = false,
                                errorMessage = "El nombre de usuario '${regReq.username}' ya existe. Elige otro."
                            )
                            respond(
                                content = jsonConfig.encodeToString(AuthResponse.serializer(), authResponse),
                                status = HttpStatusCode.BadRequest,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        } else {
                            val newUser = UserDto(
                                id = "u_${LocalUserDataSource.getUsers().size + 1}",
                                username = regReq.username,
                                passwordHash = regReq.passwordHash,
                                fullName = regReq.fullName,
                                age = regReq.age,
                                bloodType = regReq.bloodType,
                                allergies = regReq.allergies,
                                medicalConditions = regReq.medicalConditions,
                                emergencyContact = regReq.emergencyContact
                            )
                            LocalUserDataSource.addUser(newUser)

                            val authResponse = AuthResponse(
                                success = true,
                                token = "ktor_jwt_token_${newUser.id}",
                                user = newUser
                            )
                            respond(
                                content = jsonConfig.encodeToString(AuthResponse.serializer(), authResponse),
                                status = HttpStatusCode.Created,
                                headers = headersOf(HttpHeaders.ContentType, "application/json")
                            )
                        }
                    }

                    else -> {
                        respond(
                            content = """{"error": "Ruta no encontrada"}""",
                            status = HttpStatusCode.NotFound,
                            headers = headersOf(HttpHeaders.ContentType, "application/json")
                        )
                    }
                }
            }
        }
    }

    suspend fun performLogin(username: String, passwordHash: String): AuthResponse {
        return try {
            val requestJson = jsonConfig.encodeToString(
                LoginRequest.serializer(),
                LoginRequest(username = username, passwordHash = passwordHash)
            )
            val response = httpClient.post("https://api.pastillin.com/api/v1/login") {
                headers.append(HttpHeaders.ContentType, "application/json")
                setBody(requestJson)
            }
            response.body<AuthResponse>()
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                errorMessage = "Error en comunicación Ktor: ${e.message}"
            )
        }
    }

    suspend fun performRegister(request: RegisterRequest): AuthResponse {
        return try {
            val requestJson = jsonConfig.encodeToString(
                RegisterRequest.serializer(),
                request
            )
            val response = httpClient.post("https://api.pastillin.com/api/v1/register") {
                headers.append(HttpHeaders.ContentType, "application/json")
                setBody(requestJson)
            }
            response.body<AuthResponse>()
        } catch (e: Exception) {
            AuthResponse(
                success = false,
                errorMessage = "Error al crear cuenta con Ktor: ${e.message}"
            )
        }
    }
}
