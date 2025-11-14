package com.example.compose_workshop_1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose_workshop_1.ui.theme.Compose_Workshop_1Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Compose_Workshop_1Theme {
                Demo()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Demo() {
    var text by remember { mutableStateOf("") }
    Scaffold {innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.shapes.medium
                )
        ) {
            TextField(
                value = text,
                onValueChange = {
                    text = it
                    Log.d("Demo", "textField: $it")
                },
            )
        }
    }
}