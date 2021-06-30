package com.heronet.sellnetbeta.ui.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.heronet.sellnetbeta.ui.navigation.Screen
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: ProductsViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val navDestinations = remember {
        listOf(
            Screen.Products
        )
    }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
                IconButton(onClick = { coroutineScope.launch { scaffoldState.drawerState.open() } }) {
                    Icon(imageVector = Icons.Rounded.Menu, contentDescription = "Drawer")
                }
                Text(text = "Sellnet", fontSize = 20.sp)
            }
        },
        content = { padding ->
            NavHost(navController = navController, startDestination = "products", modifier = Modifier.padding(padding)) {
                composable("products") {
                    ProductsListScreen(viewModel = viewModel, navController)
                }
                composable("products/{productId}",
                    arguments = listOf(
                        navArgument("productId"){ type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    ProductDetailScreen(
                        viewModel = viewModel,
                        productId = backStackEntry.arguments?.getString("productId")!!
                    )
                }
                composable("add-product") {
                    AddProductScreen(viewModel = viewModel, navController)
                }
            }
        },
        drawerContent = {
            DrawerScreen()
        },
        bottomBar = {
            BottomNavigation {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                navDestinations.forEach { screen ->
                    BottomNavigationItem(
                        icon = { Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Cart Icon"
                        )},
                        label = { Text(text = stringResource(id = screen.resourceId)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    )
}