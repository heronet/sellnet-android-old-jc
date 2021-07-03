package com.heronet.sellnetbeta.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.heronet.sellnetbeta.R
import com.heronet.sellnetbeta.model.Product
import com.heronet.sellnetbeta.util.DateParser
import com.heronet.sellnetbeta.viewmodel.AuthViewModel
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel
import com.heronet.sellnetbeta.web.Location


@Composable
fun ProductsListScreen(
    productsViewModel: ProductsViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    val products by remember { productsViewModel.products }
    val isLoading by remember { productsViewModel.isLoading }
    val loadError by remember { productsViewModel.errorMessage }
    val productsCount by remember { productsViewModel.productsCount }
    val categories = remember {
        listOf(
            "All",
            "Phones",
            "Cars",
            "Clothes",
            "PC Parts",
            "Humans",
            "Antiques",
            "Museum Steals",
            "Ships",
            "Kidneys",
            "Bikes",
            "Real Estates"
        )
    }
    val sortOrders = remember {
        listOf(
            "Price: Low to High",
            "Price: High to Low",
            "Date: Old to New",
            "Date: New to Old"
        )
    }
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var sortBy by remember { mutableStateOf("") }
    var division by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var filterVisible by remember { mutableStateOf(false) }
    val location by remember { authViewModel.locations }
    val isLocationLoading by remember { authViewModel.isLocationsLoading }

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
                    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = {name = it},
                            label = { Text(text = "Search") },
                            placeholder = { Text(text = "What are you looking for?") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            trailingIcon = {
                                Icon(imageVector = Icons.Default.Search, contentDescription = null)
                            },
                            singleLine = true,
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    productsViewModel.resetProducts()
                                    val category = if (selectedCategory != "All") selectedCategory else null
                                    productsViewModel.getProducts(name, city, division, category, sortBy, isFiltering = true)
                                }
                            )
                        )
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(bottom = 8.dp)) {
                            items(categories, key = {category -> category}) { category ->
                                Button(
                                    onClick = {
                                        selectedCategory = category
                                        productsViewModel.resetProducts()
                                        val reqCat = if (selectedCategory != "All") selectedCategory else null
                                        productsViewModel.getProducts(name, city, division, reqCat, sortBy, isFiltering = true)
                                    },
                                    enabled = selectedCategory != category
                                ) {
                                    Text(text = category)
                                }
                            }
                        }
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            contentPadding = PaddingValues(vertical = 4.dp),
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                        .fillMaxWidth()
                                        .background(
                                            MaterialTheme.colors.primary,
                                            RoundedCornerShape(topStart = 6.dp, bottomEnd = 6.dp)
                                        )
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.background(MaterialTheme.colors.primary)
                                    ) {
                                        Text(
                                            text = "$productsCount results found",
                                            color = MaterialTheme.colors.onPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        OutlinedButton(
                                            onClick = {
                                                filterVisible = !filterVisible
                                                authViewModel.getLocations()
                                            },
                                            modifier = Modifier.padding(4.dp)
                                        ) {
                                            Text(text = "Filter")
                                        }
                                    }
                                }
                            }
                            itemsIndexed(items = products, key = { _: Int, item: Product -> item.id }) { index, product: Product ->
                                if (!isLoading) {
                                    if ((products.size < productsCount) && (index == products.size - 1)) {
                                        val category = if (selectedCategory != "All") selectedCategory else null
                                        productsViewModel.getProducts(name, city, division, category, sortBy)
                                    }
                                }
                                ItemCard(
                                    product = product,
                                    modifier = Modifier
                                        .clickable { navController.navigate("products/${product.id}") }
                                )
                            }
                        }
                        if (filterVisible) {
                            Dialog(onDismissRequest = { filterVisible = false }) {
                                if (isLocationLoading) {
                                    Column(
                                        modifier = Modifier.width(100.dp).height(100.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                } else {
                                    Column(
                                        modifier = Modifier
                                            .background(
                                                MaterialTheme.colors.background,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .padding(16.dp)
                                            .width(300.dp),
                                    ) {
                                        Text(text = "Filter")
                                        OutlinedTextField(
                                            value = selectedCategory,
                                            onValueChange = {selectedCategory = it},
                                            label = { Text(text = "Category") },
                                            modifier = Modifier.fillMaxWidth(),
                                            readOnly = true
                                        )
                                        OutlinedTextField(
                                            value = sortBy,
                                            onValueChange = {sortBy = it},
                                            label = { Text(text = "Sort By") },
                                            modifier = Modifier.fillMaxWidth(),
                                            readOnly = true
                                        )
                                        OutlinedTextField(
                                            value = city,
                                            onValueChange = {city = it},
                                            label = { Text(text = "City") },
                                            modifier = Modifier.fillMaxWidth(),
                                            readOnly = true
                                        )
                                        OutlinedTextField(
                                            value = division,
                                            onValueChange = {division = it},
                                            label = { Text(text = "Division") },
                                            modifier = Modifier.fillMaxWidth(),
                                            readOnly = true
                                        )

                                        var categoryExpanded by remember { mutableStateOf(false) }
                                        var cityExpanded by remember { mutableStateOf(false) }
                                        var divisionExpanded by remember { mutableStateOf(false) }
                                        var sortByExpanded by remember { mutableStateOf(false) }

                                        DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                                            categories.forEach { category ->
                                                DropdownMenuItem(onClick = { selectedCategory = category; categoryExpanded = false }) {
                                                    Text(text = category)
                                                }
                                            }
                                        }
                                        DropdownMenu(expanded = cityExpanded, onDismissRequest = { cityExpanded = false }) {
                                            location?.cities!!.forEach { ct ->
                                                DropdownMenuItem(onClick = { city = ct; cityExpanded = false }) {
                                                    Text(text = ct)
                                                }
                                            }
                                        }
                                        DropdownMenu(expanded = divisionExpanded, onDismissRequest = { divisionExpanded = false }) {
                                            location?.divisions!!.forEach { dv ->
                                                DropdownMenuItem(onClick = { division = dv; divisionExpanded = false }) {
                                                    Text(text = dv)
                                                }
                                            }
                                        }
                                        DropdownMenu(expanded = sortByExpanded, onDismissRequest = { sortByExpanded = false }) {
                                            sortOrders.forEach { so ->
                                                DropdownMenuItem(onClick = { sortBy = so; sortByExpanded = false }) {
                                                    Text(text = so)
                                                }
                                            }
                                        }
                                        Row(modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Button(onClick = { categoryExpanded = !categoryExpanded }, modifier = Modifier.fillMaxWidth(0.5f)) {
                                                Text(text = "Category")
                                            }
                                            Button(onClick = { sortByExpanded = !sortByExpanded }, modifier = Modifier.fillMaxWidth()) {
                                                Text(text = "Sort By")
                                            }
                                        }
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Button(onClick = { cityExpanded = !cityExpanded }, modifier = Modifier.fillMaxWidth(0.5f)) {
                                                Text(text = "City")
                                            }
                                            Button(onClick = { divisionExpanded = !divisionExpanded }, modifier = Modifier.fillMaxWidth()) {
                                                Text(text = "Division")
                                            }
                                        }
                                        Button(
                                            onClick = {
                                                productsViewModel.resetProducts()
                                                val category = if (selectedCategory != "All") selectedCategory else null
                                                productsViewModel.getProducts(name, city, division, category, sortBy, isFiltering = true)
                                                filterVisible = false
                                            },
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(text = "Filter")
                                        }
                                    }
                                }
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
