package com.unsa.idnp.lab09

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.unsa.idnp.lab09.data.local.AppDatabase
import com.unsa.idnp.lab09.data.repository.ProductoRepository
import com.unsa.idnp.lab09.ui.ProductoScreen
import com.unsa.idnp.lab09.ui.ProductoViewModel
import com.unsa.idnp.lab09.ui.ProductoViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = ProductoRepository(database.productoDao())
        val viewModelFactory = ProductoViewModelFactory(repository)

        setContent {
            MaterialTheme {
                val viewModel: ProductoViewModel = viewModel(factory = viewModelFactory)
                Scaffold { innerPadding ->
                    ProductoScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
