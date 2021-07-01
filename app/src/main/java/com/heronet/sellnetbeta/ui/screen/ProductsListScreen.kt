package com.heronet.sellnetbeta.ui.screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.heronet.sellnetbeta.R
import com.heronet.sellnetbeta.model.Product
import com.heronet.sellnetbeta.util.DateParser
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel


@Composable
fun ProductsListScreen(
    productsViewModel: ProductsViewModel,
    navController: NavController
) {
    val products by remember { productsViewModel.products }
    val isLoading by remember { productsViewModel.isLoading }
    val loadError by remember { productsViewModel.loadError }
    val productsCount by remember { productsViewModel.productsCount }

    Surface(color = MaterialTheme.colors.background) {
        Scaffold(
            content = {
                if (isLoading && products.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (loadError.isNotBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(text = loadError)
                    }
                } else {
                    Column {
                        SearchBar(modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp))
                        LazyColumn(contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)) {
                            itemsIndexed(products) { index, product: Product ->
                                if (!isLoading) {
                                    if ((products.size < productsCount) && (index == products.size - 1)) {
                                        productsViewModel.getProducts()
                                    }
                                }
                                ItemCard(
                                    product = product,
                                    modifier = Modifier
                                        .padding(vertical = 4.dp)
                                        .clickable { navController.navigate("products/${product.id}") }
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        navController.navigate("add-product") {
                            launchSingleTop = true
                        }
                    },
                    text = { Text(text = "Add") },
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Add") }
                )
            }
        )
    }
}

@Composable
fun ItemCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    val date = DateParser.getFormattedDate(product.createdAt)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = 8.dp,
    ) {
        Row {
            Box(
                modifier = Modifier
                    .height(130.dp)
                    .width(130.dp)
            ) {
                val painter = rememberCoilPainter(
                    request = product.thumbnail.imageUrl,
                    fadeIn = true
                )
                Image(
                    painter = painter,
                    contentDescription = product.thumbnail.publicId,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .height(130.dp)
                        .width(130.dp)
                )
                when (painter.loadState) {
                    is ImageLoadState.Loading -> {
                        Image(
                            painter = rememberCoilPainter(request = R.drawable.placeholder),
                            contentDescription = product.thumbnail.publicId,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .height(130.dp)
                                .width(130.dp)
                        )
                    }
                    is ImageLoadState.Error -> {
                        // Display some content if the request fails
                    }
                    else -> {
                    }
                }
            }

            Column(Modifier.padding(8.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(text = product.category, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(text = "${product.price} TK")
                Text(text = "${product.city}, ${product.division}")
                Text(text = date)
            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    var search by remember { mutableStateOf("") }
    val context = LocalContext.current
    OutlinedTextField(
        value = search,
        onValueChange = {search = it},
        label = { Text(text = "Search") },
        placeholder = { Text(text = "What are you looking for?") },
        modifier = modifier.fillMaxWidth(),
        trailingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = null)
        },
        singleLine = true,
        keyboardActions = KeyboardActions(onDone = { Toast.makeText(context, search, Toast.LENGTH_SHORT).show()})
    )
}