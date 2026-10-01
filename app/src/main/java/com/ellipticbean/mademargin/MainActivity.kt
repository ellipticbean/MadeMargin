package com.ellipticbean.mademargin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ellipticbean.mademargin.data.MadeMarginDatabase
import com.ellipticbean.mademargin.data.SavedProduct
import com.ellipticbean.mademargin.data.SavedProductDao
import com.ellipticbean.mademargin.ui.theme.MadeMarginTheme
import java.text.NumberFormat
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            MadeMarginTheme {
                MadeMarginApp()
            }
        }
    }
}

enum class MadeMarginScreen {
    CALCULATOR,
    SAVED
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
fun MadeMarginApp() {
    val context = LocalContext.current

    val database = remember {
        MadeMarginDatabase.getDatabase(context)
    }

    val savedProductDao = remember {
        database.savedProductDao()
    }

    val savedProducts by savedProductDao
        .getAllProducts()
        .collectAsState(initial = emptyList())

    val snackbarHostState =
        remember { SnackbarHostState() }

    val coroutineScope =
        rememberCoroutineScope()

    var currentScreen by remember {
        mutableStateOf(MadeMarginScreen.CALCULATOR)
    }

    var productToLoad by remember {
        mutableStateOf<SavedProduct?>(null)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ScreenNavigation(
                currentScreen = currentScreen,
                savedCount = savedProducts.size,
                onCalculatorClick = {
                    currentScreen =
                        MadeMarginScreen.CALCULATOR
                },
                onSavedClick = {
                    currentScreen =
                        MadeMarginScreen.SAVED
                }
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                when (currentScreen) {

                    MadeMarginScreen.CALCULATOR -> {
                        PricingCalculator(
                            savedProductDao = savedProductDao,
                            snackbarHostState = snackbarHostState,
                            productToLoad = productToLoad,
                            onProductLoaded = {
                                productToLoad = null
                            }
                        )
                    }

                    MadeMarginScreen.SAVED -> {
                        SavedProductsScreen(
                            products = savedProducts,
                            onOpenProduct = { product ->
                                productToLoad = product

                                currentScreen =
                                    MadeMarginScreen.CALCULATOR
                            },
                            onDeleteProduct = { product ->
                                coroutineScope.launch {
                                    savedProductDao
                                        .deleteProduct(product)

                                    snackbarHostState
                                        .showSnackbar(
                                            "${product.productName} deleted."
                                        )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ScreenNavigation(
    currentScreen: MadeMarginScreen,
    savedCount: Int,
    onCalculatorClick: () -> Unit,
    onSavedClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            if (
                currentScreen ==
                MadeMarginScreen.CALCULATOR
            ) {
                Button(
                    onClick = onCalculatorClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Calculator")
                }
            } else {
                OutlinedButton(
                    onClick = onCalculatorClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Calculator")
                }
            }

            if (
                currentScreen ==
                MadeMarginScreen.SAVED
            ) {
                Button(
                    onClick = onSavedClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        if (savedCount > 0) {
                            "Saved ($savedCount)"
                        } else {
                            "Saved"
                        }
                    )
                }
            } else {
                OutlinedButton(
                    onClick = onSavedClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        if (savedCount > 0) {
                            "Saved ($savedCount)"
                        } else {
                            "Saved"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PricingCalculator(
    savedProductDao: SavedProductDao,
    snackbarHostState: SnackbarHostState,
    productToLoad: SavedProduct? = null,
    onProductLoaded: () -> Unit
) {
    var productName by remember {
        mutableStateOf("")
    }

    var materialsCost by remember {
        mutableStateOf("")
    }

    var laborHours by remember {
        mutableStateOf("")
    }

    var hourlyRate by remember {
        mutableStateOf("")
    }

    var otherCosts by remember {
        mutableStateOf("")
    }

    var sellingFee by remember {
        mutableStateOf("")
    }

    var profitMargin by remember {
        mutableStateOf("")
    }

    var result by remember {
        mutableStateOf<PricingResult?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    val coroutineScope =
        rememberCoroutineScope()

    val numberKeyboard = KeyboardOptions(
        keyboardType = KeyboardType.Decimal
    )

    LaunchedEffect(productToLoad?.id) {
        productToLoad?.let { product ->

            productName =
                product.productName

            materialsCost =
                product.materialsCost.toInputText()

            laborHours =
                product.laborHours.toInputText()

            hourlyRate =
                product.hourlyRate.toInputText()

            otherCosts =
                product.otherCosts.toInputText()

            sellingFee =
                product.sellingFeePercent.toInputText()

            profitMargin =
                product.profitMarginPercent.toInputText()

            result = PricingResult(
                materialsCost =
                    product.materialsCost,

                laborCost =
                    product.laborCost,

                otherCosts =
                    product.otherCosts,

                totalCost =
                    product.totalCost,

                sellingFees =
                    product.sellingFees,

                profit =
                    product.profit,

                recommendedPrice =
                    product.recommendedPrice
            )

            errorMessage = null

            onProductLoaded()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(
                horizontal = 18.dp,
                vertical = 18.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {
        Column(
            verticalArrangement =
                Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = "MadeMargin",
                style =
                    MaterialTheme.typography
                        .headlineLarge,
                fontWeight = FontWeight.Bold,
                color =
                    MaterialTheme.colorScheme
                        .onBackground
            )

            Text(
                text =
                    "Price handmade products with your real costs in mind.",
                style =
                    MaterialTheme.typography
                        .bodyLarge,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }

        FormSection(
            title = "PRODUCT",
            description =
                "Give this calculation a name."
        ) {
            MadeMarginTextField(
                value = productName,
                onValueChange = {
                    productName = it
                },
                label = "Product name"
            )
        }

        FormSection(
            title = "COSTS",
            description =
                "Include everything it takes to make one item."
        ) {
            MadeMarginTextField(
                value = materialsCost,
                onValueChange = {
                    materialsCost = it
                },
                label = "Materials cost",
                prefix = "$",
                keyboardOptions =
                    numberKeyboard
            )

            MadeMarginTextField(
                value = laborHours,
                onValueChange = {
                    laborHours = it
                },
                label = "Labor hours",
                keyboardOptions =
                    numberKeyboard
            )

            MadeMarginTextField(
                value = hourlyRate,
                onValueChange = {
                    hourlyRate = it
                },
                label = "Hourly labor rate",
                prefix = "$",
                keyboardOptions =
                    numberKeyboard
            )

            MadeMarginTextField(
                value = otherCosts,
                onValueChange = {
                    otherCosts = it
                },
                label = "Other costs",
                prefix = "$",
                keyboardOptions =
                    numberKeyboard
            )
        }

        FormSection(
            title = "PRICING",
            description =
                "Account for selling fees and the margin you want to keep."
        ) {
            MadeMarginTextField(
                value = sellingFee,
                onValueChange = {
                    sellingFee = it
                },
                label = "Selling fees",
                suffix = "%",
                keyboardOptions =
                    numberKeyboard
            )

            MadeMarginTextField(
                value = profitMargin,
                onValueChange = {
                    profitMargin = it
                },
                label =
                    "Desired profit margin",
                suffix = "%",
                keyboardOptions =
                    numberKeyboard
            )
        }

        Button(
            onClick = {
                val materials =
                    materialsCost
                        .toDoubleOrNull()
                        ?: 0.0

                val hours =
                    laborHours
                        .toDoubleOrNull()
                        ?: 0.0

                val rate =
                    hourlyRate
                        .toDoubleOrNull()
                        ?: 0.0

                val extras =
                    otherCosts
                        .toDoubleOrNull()
                        ?: 0.0

                val feePercent =
                    sellingFee
                        .toDoubleOrNull()
                        ?: 0.0

                val marginPercent =
                    profitMargin
                        .toDoubleOrNull()
                        ?: 0.0

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
                    feePercent +
                            marginPercent

                if (combinedPercent >= 100) {
                    errorMessage =
                        "Selling fees and profit margin must total less than 100%."

                    result = null

                    return@Button
                }

                val laborCost =
                    hours * rate

                val totalCost =
                    materials +
                            laborCost +
                            extras

                val feeRate =
                    feePercent / 100.0

                val marginRate =
                    marginPercent / 100.0

                val recommendedPrice =
                    totalCost /
                            (
                                    1.0 -
                                            feeRate -
                                            marginRate
                                    )

                val fees =
                    recommendedPrice *
                            feeRate

                val profit =
                    recommendedPrice -
                            totalCost -
                            fees

                result = PricingResult(
                    materialsCost =
                        materials,

                    laborCost =
                        laborCost,

                    otherCosts =
                        extras,

                    totalCost =
                        totalCost,

                    sellingFees =
                        fees,

                    profit =
                        profit,

                    recommendedPrice =
                        recommendedPrice
                )

                errorMessage = null
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape =
                RoundedCornerShape(18.dp)
        ) {
            Text(
                text = "Calculate Price",
                style =
                    MaterialTheme.typography
                        .titleMedium,
                fontWeight =
                    FontWeight.Bold
            )
        }

        errorMessage?.let { message ->
            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(12.dp),
                color =
                    MaterialTheme.colorScheme
                        .errorContainer
            ) {
                Text(
                    text = message,
                    modifier =
                        Modifier.padding(14.dp),
                    color =
                        MaterialTheme.colorScheme
                            .onErrorContainer
                )
            }
        }

        result?.let { pricing ->
            PricingResultCard(
                productName =
                    productName,
                result = pricing
            )

            OutlinedButton(
                onClick = {
                    if (
                        productName.isBlank()
                    ) {
                        coroutineScope.launch {
                            snackbarHostState
                                .showSnackbar(
                                    "Enter a product name before saving."
                                )
                        }

                        return@OutlinedButton
                    }

                    val product =
                        SavedProduct(
                            productName =
                                productName.trim(),

                            materialsCost =
                                materialsCost
                                    .toDoubleOrNull()
                                    ?: 0.0,

                            laborHours =
                                laborHours
                                    .toDoubleOrNull()
                                    ?: 0.0,

                            hourlyRate =
                                hourlyRate
                                    .toDoubleOrNull()
                                    ?: 0.0,

                            otherCosts =
                                otherCosts
                                    .toDoubleOrNull()
                                    ?: 0.0,

                            sellingFeePercent =
                                sellingFee
                                    .toDoubleOrNull()
                                    ?: 0.0,

                            profitMarginPercent =
                                profitMargin
                                    .toDoubleOrNull()
                                    ?: 0.0,

                            laborCost =
                                pricing.laborCost,

                            totalCost =
                                pricing.totalCost,

                            sellingFees =
                                pricing.sellingFees,

                            profit =
                                pricing.profit,

                            recommendedPrice =
                                pricing.recommendedPrice
                        )

                    coroutineScope.launch {
                        savedProductDao
                            .insertProduct(product)

                        snackbarHostState
                            .showSnackbar(
                                "${product.productName} saved."
                            )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape =
                    RoundedCornerShape(18.dp),
                border =
                    BorderStroke(
                        1.5.dp,
                        MaterialTheme
                            .colorScheme
                            .secondary
                    ),
                colors =
                    ButtonDefaults
                        .outlinedButtonColors(
                            contentColor =
                                MaterialTheme
                                    .colorScheme
                                    .secondary
                        )
            ) {
                Text(
                    text = "Save Product",
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )
    }
}

@Composable
fun SavedProductsScreen(
    products: List<SavedProduct>,
    onOpenProduct: (SavedProduct) -> Unit,
    onDeleteProduct: (SavedProduct) -> Unit
) {
    var productPendingDelete by remember {
        mutableStateOf<SavedProduct?>(null)
    }

    val currency =
        NumberFormat.getCurrencyInstance()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.padding(
                start = 18.dp,
                end = 18.dp,
                top = 18.dp,
                bottom = 10.dp
            )
        ) {
            Text(
                text = "Saved Products",
                style =
                    MaterialTheme.typography
                        .headlineLarge,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    "Reopen previous calculations whenever you need them.",
                style =
                    MaterialTheme.typography
                        .bodyLarge,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }

        if (products.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                shape =
                    RoundedCornerShape(20.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme
                                .surface
                    )
            ) {
                Column(
                    modifier =
                        Modifier.padding(24.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text =
                            "Nothing saved yet",
                        style =
                            MaterialTheme.typography
                                .titleLarge,
                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "Calculate a product price and tap Save Product to keep it here.",
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier =
                    Modifier.fillMaxSize(),
                contentPadding =
                    androidx.compose.foundation.layout
                        .PaddingValues(
                            start = 18.dp,
                            end = 18.dp,
                            top = 6.dp,
                            bottom = 24.dp
                        ),
                verticalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = products,
                    key = { product ->
                        product.id
                    }
                ) { product ->
                    Card(
                        modifier =
                            Modifier.fillMaxWidth(),
                        shape =
                            RoundedCornerShape(
                                20.dp
                            ),
                        colors =
                            CardDefaults
                                .cardColors(
                                    containerColor =
                                        MaterialTheme
                                            .colorScheme
                                            .surface
                                ),
                        elevation =
                            CardDefaults
                                .cardElevation(
                                    defaultElevation =
                                        1.dp
                                )
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(
                                    16.dp
                                ),
                            verticalArrangement =
                                Arrangement.spacedBy(
                                    12.dp
                                )
                        ) {
                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                            ) {
                                Column(
                                    modifier =
                                        Modifier
                                            .weight(1f)
                                ) {
                                    Text(
                                        text =
                                            product
                                                .productName,
                                        style =
                                            MaterialTheme
                                                .typography
                                                .titleLarge,
                                        fontWeight =
                                            FontWeight
                                                .Bold
                                    )

                                    Text(
                                        text =
                                            "Recommended price",
                                        style =
                                            MaterialTheme
                                                .typography
                                                .bodySmall,
                                        color =
                                            MaterialTheme
                                                .colorScheme
                                                .onSurfaceVariant
                                    )
                                }

                                Text(
                                    text =
                                        currency.format(
                                            product
                                                .recommendedPrice
                                        ),
                                    style =
                                        MaterialTheme
                                            .typography
                                            .titleLarge,
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )
                            }

                            HorizontalDivider(
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .outline
                                        .copy(
                                            alpha =
                                                0.35f
                                        )
                            )

                            ResultRow(
                                label =
                                    "Total cost",
                                value =
                                    currency.format(
                                        product
                                            .totalCost
                                    ),
                                bold = true
                            )

                            ResultRow(
                                label =
                                    "Profit",
                                value =
                                    currency.format(
                                        product.profit
                                    ),
                                bold = true,
                                useSecondaryColor =
                                    true
                            )

                            Row(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement
                                        .spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        onOpenProduct(
                                            product
                                        )
                                    },
                                    modifier =
                                        Modifier
                                            .weight(1f),
                                    shape =
                                        RoundedCornerShape(
                                            14.dp
                                        )
                                ) {
                                    Text("Open")
                                }

                                OutlinedButton(
                                    onClick = {
                                        productPendingDelete =
                                            product
                                    },
                                    modifier =
                                        Modifier
                                            .weight(1f),
                                    shape =
                                        RoundedCornerShape(
                                            14.dp
                                        )
                                ) {
                                    Text("Delete")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    productPendingDelete?.let { product ->
        AlertDialog(
            onDismissRequest = {
                productPendingDelete = null
            },
            title = {
                Text("Delete product?")
            },
            text = {
                Text(
                    "Delete ${product.productName} from your saved products?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteProduct(product)
                        productPendingDelete =
                            null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        productPendingDelete =
                            null
                    }
                ) {
                    Text("Cancel")
                }
            }
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
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(20.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 1.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(16.dp),
            verticalArrangement =
                Arrangement.spacedBy(12.dp)
        ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme
                            .primary
                )

                Text(
                    text = description,
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
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
    keyboardOptions: KeyboardOptions =
        KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        prefix =
            if (prefix != null) {
                {
                    Text(prefix)
                }
            } else {
                null
            },
        suffix =
            if (suffix != null) {
                {
                    Text(suffix)
                }
            } else {
                null
            },
        keyboardOptions =
            keyboardOptions,
        modifier =
            Modifier.fillMaxWidth(),
        singleLine = true,
        shape =
            RoundedCornerShape(14.dp),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor =
                    MaterialTheme
                        .colorScheme
                        .primary,

                focusedLabelColor =
                    MaterialTheme
                        .colorScheme
                        .primary,

                cursorColor =
                    MaterialTheme
                        .colorScheme
                        .primary,

                unfocusedBorderColor =
                    MaterialTheme
                        .colorScheme
                        .outline
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
        modifier =
            Modifier.fillMaxWidth(),
        shape =
            RoundedCornerShape(22.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    MaterialTheme
                        .colorScheme
                        .surface
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 2.dp
            )
    ) {
        Column(
            modifier =
                Modifier.padding(18.dp),
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text =
                        if (
                            productName.isBlank()
                        ) {
                            "PRICE BREAKDOWN"
                        } else {
                            productName.uppercase()
                        },
                    style =
                        MaterialTheme.typography
                            .labelLarge,
                    fontWeight =
                        FontWeight.Bold,
                    color =
                        MaterialTheme
                            .colorScheme
                            .primary
                )

                Text(
                    text =
                        "Based on your costs, fees, and target margin",
                    style =
                        MaterialTheme.typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }

            Surface(
                modifier =
                    Modifier.fillMaxWidth(),
                shape =
                    RoundedCornerShape(18.dp),
                color =
                    MaterialTheme
                        .colorScheme
                        .primaryContainer
            ) {
                Column(
                    modifier =
                        Modifier.padding(
                            horizontal =
                                18.dp,
                            vertical =
                                20.dp
                        ),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            4.dp
                        )
                ) {
                    Text(
                        text =
                            "RECOMMENDED PRICE",
                        style =
                            MaterialTheme
                                .typography
                                .labelMedium,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimaryContainer
                    )

                    Text(
                        text =
                            currency.format(
                                result
                                    .recommendedPrice
                            ),
                        style =
                            MaterialTheme
                                .typography
                                .displaySmall,
                        fontWeight =
                            FontWeight.Bold,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onPrimaryContainer
                    )
                }
            }

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                ResultRow(
                    label = "Materials",
                    value =
                        currency.format(
                            result.materialsCost
                        )
                )

                ResultRow(
                    label = "Labor",
                    value =
                        currency.format(
                            result.laborCost
                        )
                )

                ResultRow(
                    label = "Other costs",
                    value =
                        currency.format(
                            result.otherCosts
                        )
                )

                HorizontalDivider(
                    color =
                        MaterialTheme
                            .colorScheme
                            .outline
                            .copy(
                                alpha = 0.45f
                            )
                )

                ResultRow(
                    label = "Total cost",
                    value =
                        currency.format(
                            result.totalCost
                        ),
                    bold = true
                )

                ResultRow(
                    label =
                        "Selling fees",
                    value =
                        currency.format(
                            result.sellingFees
                        )
                )

                ResultRow(
                    label = "Profit",
                    value =
                        currency.format(
                            result.profit
                        ),
                    bold = true,
                    useSecondaryColor =
                        true
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
        modifier =
            Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            modifier =
                Modifier.weight(1f),
            style =
                MaterialTheme.typography
                    .bodyMedium,
            fontWeight =
                if (bold) {
                    FontWeight.SemiBold
                } else {
                    FontWeight.Normal
                },
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
        )

        Text(
            text = value,
            style =
                MaterialTheme.typography
                    .bodyMedium,
            fontWeight =
                if (bold) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                },
            color =
                if (
                    useSecondaryColor
                ) {
                    MaterialTheme
                        .colorScheme
                        .secondary
                } else {
                    MaterialTheme
                        .colorScheme
                        .onSurface
                },
            textAlign =
                TextAlign.End
        )
    }
}

fun Double.toInputText(): String {
    return if (
        this % 1.0 == 0.0
    ) {
        toLong().toString()
    } else {
        toString()
    }
}