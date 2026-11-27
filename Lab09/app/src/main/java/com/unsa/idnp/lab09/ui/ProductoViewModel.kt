package com.unsa.idnp.lab09.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.unsa.idnp.lab09.data.local.ProductoEntity
import com.unsa.idnp.lab09.data.repository.ProductoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProductoViewModel(private val repository: ProductoRepository) : ViewModel() {
    val productos: StateFlow<List<ProductoEntity>> = repository.productos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun agregarProducto(nombre: String, categoria: String, cantidad: Int, precio: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertar(
                ProductoEntity(
                    nombre = nombre,
                    categoria = categoria,
                    cantidad = cantidad,
                    precio = precio
                )
            )
        }
    }

    fun actualizarProducto(producto: ProductoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.actualizar(producto)
        }
    }

    fun eliminarProducto(producto: ProductoEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.eliminar(producto)
        }
    }
}

class ProductoViewModelFactory(
    private val repository: ProductoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
