package com.solutions.se.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solutions.se.model.entity.PedidoEntity

@Dao
interface PedidoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity)

    @Query("SELECT * FROM pedidos ORDER BY idPedido DESC")
    suspend fun getAllPedidos(): List<PedidoEntity>

    @Query("DELETE FROM pedidos")
    suspend fun clearPedidos()
}
