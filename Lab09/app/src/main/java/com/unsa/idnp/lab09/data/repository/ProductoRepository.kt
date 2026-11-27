package com.unsa.idnp.lab09.data.repository

import com.unsa.idnp.lab09.data.local.ProductoDao
import com.unsa.idnp.lab09.data.local.ProductoEntity
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val dao: ProductoDao) {
    val productos: Flow<List<ProductoEntity>> = dao.obtenerTodos()

    suspend fun insertar(producto: ProductoEntity) = dao.insertar(producto)

    suspend fun actualizar(producto: ProductoEntity) = dao.actualizar(producto)

    suspend fun eliminar(producto: ProductoEntity) = dao.eliminar(producto)
}
