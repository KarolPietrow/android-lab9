package pl.karolpietrow.kp9.ui.theme

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@Composable
fun AppScreen(onLogOut: () -> Unit) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val firestore = Firebase.firestore
    val auth = FirebaseAuth.getInstance()

    val sensorManager = remember {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    val proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    val magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    var accelerometerValues by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }
    var lightValue by remember { mutableStateOf(0f) }
    var proximityValue by remember { mutableStateOf(0f) }
    var magneticValues by remember { mutableStateOf(floatArrayOf(0f, 0f, 0f)) }

    val sensorEventListener = remember {
        object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let {
                    when (it.sensor.type) {
                        Sensor.TYPE_ACCELEROMETER -> {
                            accelerometerValues = it.values.clone()
                        }
                        Sensor.TYPE_LIGHT -> {
                            lightValue = it.values[0]
                        }
                        Sensor.TYPE_PROXIMITY -> {
                            proximityValue = it.values[0]
                        }
                        Sensor.TYPE_MAGNETIC_FIELD -> {
                            magneticValues = it.values.clone()
                        }
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Nie jest wymagane do prostych zastosowań
            }
        }
    }

    LaunchedEffect(Unit) {
        accelerometer?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        lightSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximitySensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        magneticFieldSensor?.let {
            sensorManager.registerListener(sensorEventListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Sensory",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Akcelerometr",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "x = ${accelerometerValues[0]}",
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "y = ${accelerometerValues[1]}",
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "z = ${accelerometerValues[2]}",
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Czujnik światła",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "${lightValue}",
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Czujnik zbliżeniowy",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "${proximityValue}",
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "Magnetometr",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "x = ${magneticValues[0]}",
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "y = ${magneticValues[1]}",
            fontSize = 20.sp,
        )
        Text(
            modifier = Modifier.padding(10.dp),
            text = "z = ${magneticValues[2]}",
            fontSize = 20.sp,
        )

        Button(
            onClick = {
                val user = auth.currentUser
                if (user != null) {
                    val sensorData = mapOf(
                        "accelerometer" to accelerometerValues.toList(),
                        "light" to lightValue,
                        "proximity" to proximityValue,
                        "magnetic" to magneticValues.toList(),
                        "timestamp" to System.currentTimeMillis()
                    )
                    firestore
                        .collection("users")
                        .document(user.uid)
                        .collection("sensors")
                        .add(sensorData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Udany zapis do Firestore", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Błąd podczas zapisywania do Firestore", Toast.LENGTH_LONG).show()
                        }
                }
            }
        ) {
            Text("Zapisz dane")
        }
        Button(
            onClick = {
                auth.signOut()
                onLogOut()
                Toast.makeText(context, "Wylogowano", Toast.LENGTH_LONG).show()
            }
        ) {
            Text("Wyloguj się")
        }
    }

}