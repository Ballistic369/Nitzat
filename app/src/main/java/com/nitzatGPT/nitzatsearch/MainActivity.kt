package com.nitzatGPT.nitzatsearch

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nitzatGPT.nitzatsearch.ui.ProductCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: ProductViewModel = viewModel()
            val products by viewModel.products.collectAsState()
            val loading by viewModel.loading.collectAsState()

            var searchText by remember { mutableStateOf("") }

            val scanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val code = result.data?.getStringExtra("barcode")
                    if (!code.isNullOrBlank()) {
                        searchText = code
                        viewModel.search(code)
                    }
                }
            }

            Scaffold(
                topBar = { TopAppBar(title = { Text("Nitzat Search") }) }
            ) { padding ->
                Column(Modifier.padding(padding).padding(16.dp)) {
                    OutlinedTextField(
                        value = searchText,
                        onValueChange = { searchText = it },
                        label = { Text("Search by name or barcode") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(Modifier.padding(top = 8.dp)) {
                        Button(onClick = { viewModel.search(searchText) }) { Text("Search") }
                        Spacer(Modifier.width(8.dp))
                        Button(onClick = {
                            val i = Intent(this@MainActivity, BarcodeScannerActivity::class.java)
                            scanLauncher.launch(i)
                        }) { Text("Scan Barcode") }
                    }

                    Spacer(Modifier.height(16.dp))

                    if (loading) Text("Loading...")

                    products.forEach { p ->
                        ProductCard(p)
                    }
                }
            }
        }
    }
}
