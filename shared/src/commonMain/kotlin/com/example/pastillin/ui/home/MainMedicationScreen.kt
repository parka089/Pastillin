package com.example.pastillin.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pastillin.domain.model.User

data class MedicationItem(
    val id: String,
    val name: String,
    val dose: String,
    val scheduleTime: String,
    val frequencyHours: String = "Cada 8 horas",
    val durationDays: String,
    val imageEmoji: String = "💊",
    val instructions: String = "Tomar con abundante agua después de los alimentos.",
    val nextDoseTime: String = "Hoy, 08:00 PM"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MainMedicationScreen(
    user: User,
    onLogout: () -> Unit
) {
    // Registered medications
    var medications by remember {
        mutableStateOf(
            listOf(
                MedicationItem("m1", "Paracetamol", "1 Pastilla (500mg)", "08:00 AM", "Cada 8 horas", "7 Días", "💊"),
                MedicationItem("m2", "Losartán", "1 Pastilla (50mg)", "10:00 AM", "Hora fija (Diaria)", "30 Días", "⚪"),
                MedicationItem("m3", "Omeprazol", "1 Cápsula (20mg)", "08:00 PM", "Cada 12 horas", "14 Días", "💊")
            )
        )
    }

    var showAddMedDialog by remember { mutableStateOf(false) }
    var activeAlarmMed by remember { mutableStateOf<MedicationItem?>(null) }
    var detailModalMed by remember { mutableStateOf<MedicationItem?>(null) }
    var medicationToDelete by remember { mutableStateOf<MedicationItem?>(null) }

    // Registration Form State
    var newMedName by remember { mutableStateOf("") }
    var newMedDose by remember { mutableStateOf("") }
    var newMedScheduleMode by remember { mutableStateOf(0) } // 0 = Hora Fija, 1 = Intervalo por horas

    // Fixed Time Pickers
    var fixedHour by remember { mutableStateOf("10") }
    var fixedMinute by remember { mutableStateOf("00") }
    var fixedAmPm by remember { mutableStateOf("AM") } // AM or PM

    // Interval Pickers
    var newIntervalHours by remember { mutableStateOf("8") } // 2, 4, 6, 8, 10, 12, etc.
    var startHour by remember { mutableStateOf("08") }
    var startMinute by remember { mutableStateOf("00") }
    var startAmPm by remember { mutableStateOf("AM") }

    var newMedDuration by remember { mutableStateOf("") }
    var selectedEmoji by remember { mutableStateOf("💊") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("💊 Pastillín", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                        Text("Hola, ${user.fullName}", fontSize = 13.sp)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E88E5),
                    titleContentColor = Color.White
                ),
                actions = {
                    TextButton(onClick = onLogout) {
                        Text("Cerrar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddMedDialog = true },
                containerColor = Color(0xFF1E88E5),
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("➕ Registrar Medicamento", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF5F7FA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Patient Clinical Profile Card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "📋 Expediente Clínico del Usuario",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D47A1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (user.age.isNotBlank()) Text("Edad: ${user.age} años", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        if (user.bloodType.isNotBlank()) Text("🩸 Sangre: ${user.bloodType}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFC62828))
                    }

                    if (user.allergies.isNotBlank()) {
                        Text("⚠️ Alergias: ${user.allergies}", fontSize = 14.sp, color = Color(0xFFD32F2F))
                    }
                    if (user.medicalConditions.isNotBlank()) {
                        Text("🏥 Diagnósticos: ${user.medicalConditions}", fontSize = 14.sp, color = Color(0xFF37474F))
                    }
                    if (user.emergencyContact.isNotBlank()) {
                        Text("📞 Contacto Emergencia: ${user.emergencyContact}", fontSize = 14.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Test Lock-Screen Ringing Alarm Button
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFD32F2F)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🚨 Probador de Alarma con Teléfono Bloqueado",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFFC62828)
                    )
                    Text(
                        text = "Haz clic abajo para simular la alarma en pantalla completa con foto y botón de parar:",
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                    Button(
                        onClick = {
                            activeAlarmMed = medications.firstOrNull() ?: MedicationItem("demo", "Paracetamol", "1 Pastilla", "08:00 AM", "Cada 8 horas", "7 Días")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("🔔 PROBAR ALARMA AHORA", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }

            Text(
                text = "💊 Mis Medicamentos Programados",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            if (medications.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("💊", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No tienes medicamentos registrados actualmente.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF546E7A),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Presiona '➕ Registrar Medicamento' para agregar uno nuevo.",
                            fontSize = 13.sp,
                            color = Color(0xFF78909C),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            } else {
                // Medication Cards List
                medications.forEach { med ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .background(Color(0xFFE1F5FE), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = med.imageEmoji, fontSize = 36.sp)
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = med.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0D47A1)
                                )
                                Text(
                                    text = "Dosis: ${med.dose}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF37474F)
                                )
                                Text(
                                    text = "⏰ Hora: ${med.scheduleTime}",
                                    fontSize = 14.sp,
                                    color = Color(0xFF546E7A)
                                )
                                Text(
                                    text = "🔄 ${med.frequencyHours} | 📅 ${med.durationDays}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF2E7D32)
                                )
                            }

                            Row {
                                IconButton(onClick = { detailModalMed = med }) {
                                    Text("ℹ️", fontSize = 22.sp)
                                }
                                IconButton(onClick = { medicationToDelete = med }) {
                                    Text("🗑️", fontSize = 22.sp)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(70.dp))
        }
    }

    // --- DELETE CONFIRMATION DIALOG ---
    medicationToDelete?.let { med ->
        AlertDialog(
            onDismissRequest = { medicationToDelete = null },
            title = {
                Text(
                    text = "🗑️ ¿Eliminar Medicamento?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFC62828)
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro de que deseas eliminar '${med.name}' de tu lista? Ya no sonarán las alarmas asociadas a este medicamento.",
                    fontSize = 15.sp,
                    color = Color(0xFF37474F)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        medications = medications.filter { it.id != med.id }
                        medicationToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text("Sí, Eliminar", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { medicationToDelete = null }) {
                    Text("Cancelar", fontSize = 15.sp)
                }
            }
        )
    }

    // --- REGISTRATION DIALOG ---
    if (showAddMedDialog) {
        AlertDialog(
            onDismissRequest = { showAddMedDialog = false },
            title = {
                Text(
                    text = "Registrar Nuevo Medicamento",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    Text("Ingresa los datos de tu medicina:", fontSize = 13.sp)

                    OutlinedTextField(
                        value = newMedName,
                        onValueChange = { newMedName = it },
                        label = { Text("Nombre del medicamento (ej: Ibuprofeno)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = newMedDose,
                        onValueChange = { newMedDose = it },
                        label = { Text("Dosis específica (ej: 1 Pastilla de 400mg)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // TWO OPTIONS FOR SCHEDULING
                    Text("Selecciona cómo tomar este medicamento:", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = (newMedScheduleMode == 0),
                            onClick = { newMedScheduleMode = 0 },
                            label = { Text("⏰ Hora Fija", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = (newMedScheduleMode == 1),
                            onClick = { newMedScheduleMode = 1 },
                            label = { Text("🔄 Cada X horas", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (newMedScheduleMode == 0) {
                        // OPTION 1: FIXED TIME (HOURS + MINUTES + AM/PM TOGGLE)
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Opción 1: Escoger Hora Fija Diaria", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = fixedHour,
                                        onValueChange = { fixedHour = it },
                                        label = { Text("Hora") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(":", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    OutlinedTextField(
                                        value = fixedMinute,
                                        onValueChange = { fixedMinute = it },
                                        label = { Text("Min") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Text("Selecciona AM o PM:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FilterChip(
                                        selected = (fixedAmPm == "AM"),
                                        onClick = { fixedAmPm = "AM" },
                                        label = { Text("☀️ AM", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChip(
                                        selected = (fixedAmPm == "PM"),
                                        onClick = { fixedAmPm = "PM" },
                                        label = { Text("🌙 PM", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    } else {
                        // OPTION 2: INTERVAL IN HOURS (2, 4, 6, 8, 10, 12, etc. + START TIME + AM/PM)
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Opción 2: Repetir Cada Cuántas Horas", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))

                                Text("Escoge cada cuántas horas:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    listOf("2", "4", "6", "8", "10", "12", "24").forEach { hrs ->
                                        FilterChip(
                                            selected = (newIntervalHours == hrs),
                                            onClick = { newIntervalHours = hrs },
                                            label = { Text("$hrs hrs", fontSize = 12.sp) }
                                        )
                                    }
                                }

                                Text("Hora de la primera toma:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = startHour,
                                        onValueChange = { startHour = it },
                                        label = { Text("Hora") },
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(":", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    OutlinedTextField(
                                        value = startMinute,
                                        onValueChange = { startMinute = it },
                                        label = { Text("Min") },
                                        modifier = Modifier.weight(1f)
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    FilterChip(
                                        selected = (startAmPm == "AM"),
                                        onClick = { startAmPm = "AM" },
                                        label = { Text("☀️ AM", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChip(
                                        selected = (startAmPm == "PM"),
                                        onClick = { startAmPm = "PM" },
                                        label = { Text("🌙 PM", fontSize = 14.sp, fontWeight = FontWeight.Bold) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newMedDuration,
                        onValueChange = { newMedDuration = it },
                        label = { Text("Duración del tratamiento (ej: 10 Días)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("Selecciona una foto/icono para la medicina:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("💊", "⚪", "🧪", "💧", "💉").forEach { emoji ->
                            FilterChip(
                                selected = (selectedEmoji == emoji),
                                onClick = { selectedEmoji = emoji },
                                label = { Text(emoji, fontSize = 20.sp) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newMedName.isNotBlank() && newMedDose.isNotBlank()) {
                            val computedTime = if (newMedScheduleMode == 0) "$fixedHour:$fixedMinute $fixedAmPm" else "$startHour:$startMinute $startAmPm"
                            val computedFreq = if (newMedScheduleMode == 0) "Hora fija (Diaria)" else "Cada $newIntervalHours horas"

                            val newItem = MedicationItem(
                                id = "m_${medications.size + 1}",
                                name = newMedName,
                                dose = newMedDose,
                                scheduleTime = computedTime,
                                frequencyHours = computedFreq,
                                durationDays = if (newMedDuration.isBlank()) "7 Días" else newMedDuration,
                                imageEmoji = selectedEmoji
                            )
                            medications = medications + newItem
                            newMedName = ""
                            newMedDose = ""
                            newMedDuration = ""
                            showAddMedDialog = false
                        }
                    }
                ) {
                    Text("Guardar Medicamento", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMedDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // --- LOCK-SCREEN ALARM MODAL ---
    activeAlarmMed?.let { alarmMed ->
        Dialog(
            onDismissRequest = { /* Prevent accidental dismissal without confirming */ },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFB71C1C)) // Alert red background
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp)
                ) {
                    // Alert Header
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Surface(
                            color = Color.Yellow,
                            shape = RoundedCornerShape(30.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = "🔔 ALARMA DE MEDICAMENTO 🔔",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }

                        Text(
                            text = "¡HORA DE TOMAR TU MEDICINA!",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Medicine Photo & Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(24.dp)
                                .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .background(Color(0xFFE3F2FD), CircleShape)
                                    .border(4.dp, Color(0xFF1E88E5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = alarmMed.imageEmoji, fontSize = 72.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = alarmMed.name,
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0D47A1),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "DOSIS: ${alarmMed.dose}",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFC62828),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "🔄 FRECUENCIA: ${alarmMed.frequencyHours}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2E7D32),
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = { detailModalMed = alarmMed },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("ℹ️ Ver detalles e instrucciones", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // HUGE STOP & CONFIRM BUTTON
                    Button(
                        onClick = { activeAlarmMed = null },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(20.dp),
                        elevation = ButtonDefaults.buttonElevation(8.dp)
                    ) {
                        Text(
                            text = "🛑 DETENER Y CONFIRMAR TOMA",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }

    // --- DETAIL MODAL ---
    detailModalMed?.let { med ->
        AlertDialog(
            onDismissRequest = { detailModalMed = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(med.imageEmoji, fontSize = 32.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(med.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("💊 Dosis a tomar: ${med.dose}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D47A1))
                    Text("⏰ Hora programada: ${med.scheduleTime}", fontSize = 16.sp)
                    Text("🔄 Frecuencia: ${med.frequencyHours}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                    Text("📅 Duración: ${med.durationDays}", fontSize = 16.sp)
                    Text("📝 Instrucciones: ${med.instructions}", fontSize = 16.sp)
                    HorizontalDivider()
                    Text("🕒 Próxima Dosis: ${med.nextDoseTime}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                }
            },
            confirmButton = {
                Button(onClick = { detailModalMed = null }) {
                    Text("Entendido", fontSize = 16.sp)
                }
            }
        )
    }
}
