package com.ellipticbean.mademargin.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_products")
data class SavedProduct(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val productName: String,

    val materialsCost: Double,
    val laborHours: Double,
    val hourlyRate: Double,
    val otherCosts: Double,

    val sellingFeePercent: Double,
    val profitMarginPercent: Double,

    val laborCost: Double,
    val totalCost: Double,
    val sellingFees: Double,
    val profit: Double,
    val recommendedPrice: Double,

    val createdAt: Long = System.currentTimeMillis()
)