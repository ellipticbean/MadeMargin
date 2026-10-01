package com.ellipticbean.mademargin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background
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
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // =====================================================
        // HEADER
        // =====================================================

        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "MadeMargin",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Price handmade products with your real costs in mind.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // =====================================================
        // PRODUCT
        // =====================================================

        FormSection(
            title = "PRODUCT",
            description = "Give this calculation a name."
        ) {
            MadeMarginTextField(
                value = productName,
                onValueChange = {
                    productName = it
                },
                label = "Product name"
            )
        }

        // =====================================================
        // COSTS
        // =====================================================

        FormSection(
            title = "COSTS",
            description = "Include everything it takes to make one item."
        ) {
            MadeMarginTextField(
                value = materialsCost,
                onValueChange = {
                    materialsCost = it
                },
                label = "Materials cost",
                prefix = "$",
                keyboardOptions = numberKeyboard
            )

            MadeMarginTextField(
                value = laborHours,
                onValueChange = {
                    laborHours = it
                },
                label = "Labor hours",
                keyboardOptions = numberKeyboard
            )

            MadeMarginTextField(
                value = hourlyRate,
                onValueChange = {
                    hourlyRate = it
                },
                label = "Hourly labor rate",
                prefix = "$",
                keyboardOptions = numberKeyboard
            )

            MadeMarginTextField(
                value = otherCosts,
                onValueChange = {
                    otherCosts = it
                },
                label = "Other costs",
                prefix = "$",
                keyboardOptions = numberKeyboard
            )
        }

        // =====================================================
        // PRICING
        // =====================================================

        FormSection(
            title = "PRICING",
            description = "Account for selling fees and the margin you want to keep."
        ) {
            MadeMarginTextField(
                value = sellingFee,
                onValueChange = {
                    sellingFee = it
                },
                label = "Selling fees",
                suffix = "%",
                keyboardOptions = numberKeyboard
            )

            MadeMarginTextField(
                value = profitMargin,
                onValueChange = {
                    profitMargin = it
                },
                label = "Desired profit margin",
                suffix = "%",
                keyboardOptions = numberKeyboard
            )
        }

        // =====================================================
        // CALCULATE
        // =====================================================

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
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "Calculate Price",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        errorMessage?.let { message ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(14.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // =====================================================
        // RESULTS
        // =====================================================

        result?.let { pricing ->
            PricingResultCard(
                productName = productName,
                result = pricing
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )
    }
}

@Composable
fun FormSection(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            content()
        }
    }
}

@Composable
fun MadeMarginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    prefix: String? = null,
    suffix: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        prefix = if (prefix != null) {
            {
                Text(prefix)
            }
        } else {
            null
        },
        suffix = if (suffix != null) {
            {
                Text(suffix)
            }
        } else {
            null
        },
        keyboardOptions = keyboardOptions,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor =
                MaterialTheme.colorScheme.primary,

            focusedLabelColor =
                MaterialTheme.colorScheme.primary,

            cursorColor =
                MaterialTheme.colorScheme.primary,

            unfocusedBorderColor =
                MaterialTheme.colorScheme.outline
        )
    )
}

@Composable
fun PricingResultCard(
    productName: String,
    result: PricingResult
) {
    val currency =
        NumberFormat.getCurrencyInstance()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // =================================================
            // RESULT TITLE
            // =================================================

            Column(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = if (productName.isBlank()) {
                        "PRICE BREAKDOWN"
                    } else {
                        productName.uppercase()
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Based on your costs, fees, and target margin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // =================================================
            // RECOMMENDED PRICE
            // =================================================

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = 18.dp,
                        vertical = 20.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "RECOMMENDED PRICE",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    Text(
                        text = currency.format(
                            result.recommendedPrice
                        ),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // =================================================
            // COST BREAKDOWN
            // =================================================

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ResultRow(
                    label = "Materials",
                    value = currency.format(
                        result.materialsCost
                    )
                )

                ResultRow(
                    label = "Labor",
                    value = currency.format(
                        result.laborCost
                    )
                )

                ResultRow(
                    label = "Other costs",
                    value = currency.format(
                        result.otherCosts
                    )
                )

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.outline.copy(
                        alpha = 0.45f
                    )
                )

                ResultRow(
                    label = "Total cost",
                    value = currency.format(
                        result.totalCost
                    ),
                    bold = true
                )

                ResultRow(
                    label = "Selling fees",
                    value = currency.format(
                        result.sellingFees
                    )
                )

                ResultRow(
                    label = "Profit",
                    value = currency.format(
                        result.profit
                    ),
                    bold = true,
                    useSecondaryColor = true
                )
            }
        }
    }
}

@Composable
fun ResultRow(
    label: String,
    value: String,
    bold: Boolean = false,
    useSecondaryColor: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (bold) {
                FontWeight.Bold
            } else {
                FontWeight.Medium
            },
            color = if (useSecondaryColor) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            textAlign = TextAlign.End
        )
    }
}