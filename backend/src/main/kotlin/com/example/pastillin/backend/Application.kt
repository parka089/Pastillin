package com.example.pastillin.backend

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ServerUser(
    val id: String,
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val age: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val emergencyContact: String = ""
)

@Serializable
data class ServerMedicationDto(
    val id: String,
    val name: String,
    val dose: String,
    val scheduleTime: String,
    val frequencyHours: String = "Cada 8 horas",
    val durationDays: String = "7 Días",
    val imageEmoji: String = "💊",
    val photoCaptured: Boolean = true,
    val instructions: String = "Tomar con abundante agua después de los alimentos.",
    val nextDoseTime: String = "Hoy, 08:00 PM"
)

@Serializable
data class ServerLoginRequest(
    val username: String,
    val passwordHash: String
)

@Serializable
data class ServerRegisterRequest(
    val username: String,
    val passwordHash: String,
    val fullName: String,
    val age: String = "",
    val bloodType: String = "",
    val allergies: String = "",
    val medicalConditions: String = "",
    val emergencyContact: String = ""
)

@Serializable
data class ServerAuthResponse(
    val success: Boolean,
    val token: String? = null,
    val user: ServerUser? = null,
    val errorMessage: String? = null
)

// In-Memory & JSON Server Database
object BackendUserDatabase {
    val usersJsonContent = """
        [
            {
                "id": "u1",
                "username": "roberto",
                "passwordHash": "roberto123",
                "fullName": "Don Roberto Pérez",
                "age": "72",
                "bloodType": "O+",
                "allergies": "Penicilina, Sulfa",
                "medicalConditions": "Hipertensión Arterial, Diabetes Tipo 2",
                "emergencyContact": "555-0192 (Hijo - Carlos)"
            }
        ]
    """.trimIndent()

    private val jsonConfig = Json { ignoreUnknownKeys = true }

    val usersList: MutableList<ServerUser> = jsonConfig.decodeFromString<List<ServerUser>>(usersJsonContent).toMutableList()

    val medicationsList: MutableList<ServerMedicationDto> = mutableListOf(
        ServerMedicationDto("m1", "Paracetamol", "1 Pastilla (500mg)", "08:00 AM", "Cada 8 horas", "7 Días", "💊", photoCaptured = true),
        ServerMedicationDto("m2", "Losartán", "1 Pastilla (50mg)", "10:00 AM", "Hora fija (Diaria)", "30 Días", "⚪", photoCaptured = true),
        ServerMedicationDto("m3", "Omeprazol", "1 Cápsula (20mg)", "08:00 PM", "Cada 12 horas", "14 Días", "💊", photoCaptured = true)
    )
}

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    println("🚀 Servidor Backend Ktor iniciando en http://0.0.0.0:$port ...")
    embeddedServer(CIO, port = port, host = "0.0.0.0", module = Application::module).start(wait = true)
}

fun Application.module() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }

    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Delete)
    }

    routing {
        get("/") {
            call.respondText("⚡ Pastillín Ktor Backend Server Activo (Port 8080)")
        }

        get("/api/v1/users") {
            call.respond(BackendUserDatabase.usersList)
        }

        post("/api/v1/login") {
            val req = call.receive<ServerLoginRequest>()
            val matchedUser = BackendUserDatabase.usersList.find {
                it.username.equals(req.username, ignoreCase = true) && it.passwordHash == req.passwordHash
            }

            if (matchedUser != null) {
                call.respond(
                    HttpStatusCode.OK,
                    ServerAuthResponse(
                        success = true,
                        token = "ktor_jwt_token_${matchedUser.id}",
                        user = matchedUser
                    )
                )
            } else {
                call.respond(
                    HttpStatusCode.Unauthorized,
                    ServerAuthResponse(
                        success = false,
                        errorMessage = "Credenciales incorrectas. Verifique usuario y contraseña."
                    )
                )
            }
        }

        post("/api/v1/register") {
            val req = call.receive<ServerRegisterRequest>()
            val existing = BackendUserDatabase.usersList.find {
                it.username.equals(req.username, ignoreCase = true)
            }

            if (existing != null) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ServerAuthResponse(
                        success = false,
                        errorMessage = "El usuario '${req.username}' ya existe. Elige otro."
                    )
                )
            } else {
                val newUser = ServerUser(
                    id = "u_${BackendUserDatabase.usersList.size + 1}",
                    username = req.username,
                    passwordHash = req.passwordHash,
                    fullName = req.fullName,
                    age = req.age,
                    bloodType = req.bloodType,
                    allergies = req.allergies,
                    medicalConditions = req.medicalConditions,
                    emergencyContact = req.emergencyContact
                )
                BackendUserDatabase.usersList.add(newUser)

                call.respond(
                    HttpStatusCode.Created,
                    ServerAuthResponse(
                        success = true,
                        token = "ktor_jwt_token_${newUser.id}",
                        user = newUser
                    )
                )
            }
        }

        // --- ENDPOINTS PARA GUARDAR Y ELIMINAR MEDICAMENTOS (PERSISTENCIA KTOR) ---
        get("/api/v1/medications") {
            call.respond(BackendUserDatabase.medicationsList)
        }

        post("/api/v1/medications") {
            val med = call.receive<ServerMedicationDto>()
            BackendUserDatabase.medicationsList.add(med)
            call.respond(HttpStatusCode.Created, med)
        }

        delete("/api/v1/medications/{id}") {
            val idParam = call.parameters["id"]
            if (idParam != null) {
                BackendUserDatabase.medicationsList.removeAll { it.id == idParam }
                call.respond(HttpStatusCode.OK, mapOf("status" to "deleted", "id" to idParam))
            } else {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "ID no especificado"))
            }
        }
    }
}
