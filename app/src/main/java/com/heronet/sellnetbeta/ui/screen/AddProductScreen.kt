package com.heronet.sellnetbeta.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.google.accompanist.coil.rememberCoilPainter
import com.heronet.sellnetbeta.ui.navigation.Screen
import com.heronet.sellnetbeta.viewmodel.AuthViewModel
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel

@Composable
fun AddProductScreen(
    productsViewModel: ProductsViewModel,
    authViewModel: AuthViewModel,
    navController: NavController
) {
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        var name by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var imageUris by remember { mutableStateOf(listOf<Uri?>()) }
        var category by remember { mutableStateOf("") }
        var categoryExpanded by remember { mutableStateOf(false) }
        val isLoading by remember { productsViewModel.isLoading }
        var uploadFinished by remember { productsViewModel.uploadFinished }

        val categories = remember {
            listOf(
                "Phones",
                "Clothes",
                "PC Parts",
                "Humans",
                "Antiques",
                "Museum Steals",
                "Ships",
                "Kidneys",
                "Cars",
                "Bikes",
                "Real Estates",
                "Others"
            )
        }
        val selectorLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) {
            imageUris = it
        }
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(8.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                label = { Text("Product Title")},
                placeholder = { Text("Add an easily guessable name")},
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = price,
                singleLine = true,
                label = { Text("Price")},
                placeholder = { Text("Good prices, good sells")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = { value ->
                    if (value.length <= 9) {
                        price = value.filter { it.isDigit() }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description")},
                placeholder = { Text("Describe your item as good as you can")},
                modifier = Modifier.fillMaxWidth()
            )
            if (category.isNotBlank()) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { /* Do Nothing */ },
                    enabled = false,
                    label = { Text("Category")},
                    modifier = Modifier.fillMaxWidth()
                )
            }
            DropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                categories.forEach { cat ->
                    DropdownMenuItem(onClick = { category = cat; categoryExpanded = false }) {
                        Text(text = cat)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { categoryExpanded = !categoryExpanded }, enabled = !isLoading, modifier = Modifier.fillMaxWidth(0.5f)) {
                    Text(text = "Select Category")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { selectorLauncher.launch("image/*") }, enabled = !isLoading, modifier = Modifier.fillMaxWidth()) {
                    Text(text = "Add Photos")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val token = authViewModel.authStatus.value.authData!!.token
                    productsViewModel.addProduct(name, price, description, category, imageUris, token)
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isLoading)
                    Text(text = "Submit Product")
                else
                    Text(text = "Please Wait...")
            }
            Spacer(modifier = Modifier.height(8.dp))
            PhotosPreview(imageUris, modifier = Modifier.fillMaxWidth())
        }
        if (uploadFinished) {
            // Reset upload status for using with subsequent uploads.
            productsViewModel.resetUploadStatus()
            // Go to ProductsScreen
            navController.navigate(Screen.Products.route) {
                popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun PhotosPreview(images: List<Uri?>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(images) { image ->
            Image(
                painter = rememberCoilPainter(request = image),
                contentDescription = null,
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .shadow(8.dp),
                contentScale = ContentScale.Crop
            )
        }
    }
}