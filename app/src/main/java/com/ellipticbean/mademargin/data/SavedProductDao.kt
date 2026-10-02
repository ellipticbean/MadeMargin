package com.ellipticbean.mademargin.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedProductDao {

    @Query(
        """
        SELECT *
        FROM saved_products
        ORDER BY createdAt DESC
        """
    )
    fun getAllProducts(): Flow<List<SavedProduct>>

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertProduct(
        product: SavedProduct
    ): Long

    @Update
    suspend fun updateProduct(
        product: SavedProduct
    )

    @Delete
    suspend fun deleteProduct(
        product: SavedProduct
    )

    @Query(
        """
        DELETE FROM saved_products
        """
    )
    suspend fun deleteAllProducts()
}