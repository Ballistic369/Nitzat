package com.nitzatGPT.nitzatsearch.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.nitzatGPT.nitzatsearch.model.Product
import androidx.compose.ui.platform.LocalContext

@Composable
fun ProductCard(product: Product) {
    val context = LocalContext.current
    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
        .clickable {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(product.link))
            context.startActivity(i)
        }) {
        Column(Modifier.padding(12.dp)) {
            product.imageUrl?.let {
                AsyncImage(model = it, contentDescription = product.name, modifier = Modifier
                    .height(150.dp)
                    .fillMaxWidth())
            }
            Text(text = product.name)
            Text(text = "Price: ${product.price}")
            product.discountPrice?.let { Text(text = "Discount: $it") }
            product.description?.let { Text(text = it) }
        }
    }
}
