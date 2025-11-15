package com.example.compose_workshop_1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose_workshop_1.ui.theme.Compose_Workshop_1Theme

// -- UI --

@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel
) {
    val state = viewModel.state
    val buttonSpacing = 8.dp

    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        // Display card
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = state.number1.ifEmpty { "0" } +
                            (state.operation?.symbol ?: "") +
                            state.number2,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Right,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = buttonSpacing),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(symbol = "C", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Clear) }
            CalculatorButton(symbol = "Del", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Delete) }
            CalculatorButton(symbol = "/", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Divide)) }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = buttonSpacing),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(symbol = "7", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(7)) }
            CalculatorButton(symbol = "8", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(8)) }
            CalculatorButton(symbol = "9", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(9)) }
            CalculatorButton(symbol = "*", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Multiply)) }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = buttonSpacing),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(symbol = "4", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(4)) }
            CalculatorButton(symbol = "5", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(5)) }
            CalculatorButton(symbol = "6", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(6)) }
            CalculatorButton(symbol = "-", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Subtract)) }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = buttonSpacing),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(symbol = "1", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(1)) }
            CalculatorButton(symbol = "2", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(2)) }
            CalculatorButton(symbol = "3", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Number(3)) }
            CalculatorButton(symbol = "+", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Operation(CalculatorOperation.Add)) }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton(symbol = "0", modifier = Modifier.weight(2f)) { viewModel.onAction(CalculatorAction.Number(0)) }
            CalculatorButton(symbol = ".", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Decimal) }
            CalculatorButton(symbol = "=", modifier = Modifier.weight(1f)) { viewModel.onAction(CalculatorAction.Calculate) }
        }
    }
}

@Composable
fun RowScope.CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 4.dp)
    ) {
        Text(text = symbol, style = MaterialTheme.typography.titleMedium)
    }
}

// -- Activity & Preview --

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Compose_Workshop_1Theme {
                Surface {
                    Scaffold { innerPadding ->
                        val vm: CalculatorViewModel = viewModel()
                        CalculatorScreen(modifier = Modifier.padding(innerPadding), viewModel = vm)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    Compose_Workshop_1Theme {
        val previewVm = remember { CalculatorViewModel().apply {
            onAction(CalculatorAction.Number(1))
            onAction(CalculatorAction.Number(2))
            onAction(CalculatorAction.Operation(CalculatorOperation.Add))
            onAction(CalculatorAction.Number(3))
        } }
        CalculatorScreen(viewModel = previewVm)
    }
}
