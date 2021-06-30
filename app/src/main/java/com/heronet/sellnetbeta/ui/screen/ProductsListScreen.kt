package com.heronet.sellnetbeta.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.heronet.sellnetbeta.R
import com.heronet.sellnetbeta.model.Product
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel
import java.text.SimpleDateFormat


@Composable
fun ProductsListScreen(
    viewModel: ProductsViewModel,
    navController: NavController
) {
    val products by remember { viewModel.products }
    val isLoading by remember { viewModel.isLoading }
    val loadError by remember { viewModel.loadError }
    val productsCount by remember { viewModel.productsCount }

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
                }
                else {
                    LazyColumn(contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)) {
                        itemsIndexed(products) { index, product: Product ->
                            if (!isLoading) {
                                if((products.size < productsCount) && (index == products.size - 1)) {
                                    viewModel.getProducts()
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate("add-product") {
                            launchSingleTop = true
                        }
                    },
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                }
            }
        )
    }
}

@Composable
fun ItemCard(
    product: Product,
    modifier: Modifier = Modifier
) {
    val parser = remember {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    }
    val formatter = remember {
        SimpleDateFormat("dd-MM-yyyy HH:mm a")
    }
    val date = formatter.format(parser.parse(product.createdAt)!!)
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        elevation = 8.dp,
    ) {
        Row {
            Image(
                painter = rememberCoilPainter(
                    request = product.thumbnail.imageUrl,
                    fadeIn = true
                ),
                contentDescription = "Item Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(130.dp)
                    .width(130.dp)
            )

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