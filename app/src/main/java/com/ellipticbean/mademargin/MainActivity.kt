package com.ellipticbean.mademargin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ellipticbean.mademargin.ui.theme.MadeMarginTheme
import java.text.NumberFormat

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MadeMarginTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PricingCalculator(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

data class PricingResult(
    val materialsCost: Double,
    val laborCost: Double,
    val otherCosts: Double,
    val totalCost: Double,
    val sellingFees: Double,
    val profit: Double,
    val recommendedPrice: Double
)

@Composable
fun PricingCalculator(
    modifier: Modifier = Modifier
) {
    var productName by remember { mutableStateOf("") }
    var materialsCost by remember { mutableStateOf("") }
    var laborHours by remember { mutableStateOf("") }
    var hourlyRate by remember { mutableStateOf("") }
    var otherCosts by remember { mutableStateOf("") }
    var sellingFee by remember { mutableStateOf("") }
    var profitMargin by remember { mutableStateOf("") }

    var result by remember {
        mutableStateOf<PricingResult?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val numberKeyboard = KeyboardOptions(
        keyboardType = KeyboardType.Decimal
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "MadeMargin",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Price handmade products with your real costs in mind.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = productName,
            onValueChange = { productName = it },
            label = {
                Text("Product name")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = materialsCost,
            onValueChange = { materialsCost = it },
            label = {
                Text("Materials cost")
            },
            prefix = {
                Text("$")
            },
            keyboardOptions = numberKeyboard,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = laborHours,
            onValueChange = { laborHours = it },
            label = {
                Text("Labor hours")
            },
            keyboardOptions = numberKeyboard,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = hourlyRate,
            onValueChange = { hourlyRate = it },
            label = {
                Text("Hourly labor rate")
            },
            prefix = {
                Text("$")
            },
            keyboardOptions = numberKeyboard,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = otherCosts,
            onValueChange = { otherCosts = it },
            label = {
                Text("Other costs")
            },
            prefix = {
                Text("$")
            },
            keyboardOptions = numberKeyboard,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = sellingFee,
            onValueChange = { sellingFee = it },
            label = {
                Text("Selling fees")
            },
            suffix = {
                Text("%")
            },
            keyboardOptions = numberKeyboard,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = profitMargin,
            onValueChange = { profitMargin = it },
            label = {
                Text("Desired profit margin")
            },
            suffix = {
                Text("%")
            },
            keyboardOptions = numberKeyboard,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(
            modifier = Modifier.height(4.dp)
        )

        Button(
            onClick = {
                val materials =
                    materialsCost.toDoubleOrNull() ?: 0.0

                val hours =
                    laborHours.toDoubleOrNull() ?: 0.0

                val rate =
                    hourlyRate.toDoubleOrNull() ?: 0.0

                val extras =
                    otherCosts.toDoubleOrNull() ?: 0.0

                val feePercent =
                    sellingFee.toDoubleOrNull() ?: 0.0

                val marginPercent =
                    profitMargin.toDoubleOrNull() ?: 0.0

                if (
                    materials < 0 ||
                    hours < 0 ||
                    rate < 0 ||
                    extras < 0 ||
                    feePercent < 0 ||
                    marginPercent < 0
                ) {
                    errorMessage =
                        "Values cannot be negative."

                    result = null

                    return@Button
                }

                val combinedPercent =
                    feePercent + marginPercent

                if (combinedPercent >= 100) {
                    errorMessage =
                        "Selling fees and profit margin must total less than 100%."

                    result = null

                    return@Button
                }

                val laborCost =
                    hours * rate

                val totalCost =
                    materials + laborCost + extras

                val feeRate =
                    feePercent / 100.0

                val marginRate =
                    marginPercent / 100.0

                val recommendedPrice =
                    totalCost /
                            (1.0 - feeRate - marginRate)

                val fees =
                    recommendedPrice * feeRate

                val profit =
                    recommendedPrice -
                            totalCost -
                            fees

                result = PricingResult(
                    materialsCost = materials,
                    laborCost = laborCost,
                    otherCosts = extras,
                    totalCost = totalCost,
                    sellingFees = fees,
                    profit = profit,
                    recommendedPrice = recommendedPrice
                )

                errorMessage = null
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Calculate Price")
        }

        errorMessage?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }

        result?.let { pricing ->
            PricingResultCard(
                productName = productName,
                result = pricing
            )
        }

        Spacer(
            modifier = Modifier.height(20.dp)
        )
    }
}

@Composable
fun PricingResultCard(
    productName: String,
    result: PricingResult
) {
    val currency =
        NumberFormat.getCurrencyInstance()

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (productName.isBlank()) {
                    "Price Breakdown"
                } else {
                    productName
                },
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Materials: ${currency.format(result.materialsCost)}"
            )

            Text(
                text = "Labor: ${currency.format(result.laborCost)}"
            )

            Text(
                text = "Other costs: ${currency.format(result.otherCosts)}"
            )

            Text(
                text = "Total cost: ${currency.format(result.totalCost)}"
            )

            Text(
                text = "Estimated selling fees: ${currency.format(result.sellingFees)}"
            )

            Text(
                text = "Estimated profit: ${currency.format(result.profit)}"
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Text(
                text = "Recommended price",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = currency.format(result.recommendedPrice),
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}