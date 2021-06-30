package com.heronet.sellnetbeta.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.heronet.sellnetbeta.ui.navigation.NavHostContainer
import com.heronet.sellnetbeta.ui.navigation.Screen
import com.heronet.sellnetbeta.viewmodel.AuthViewModel
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(productsViewModel: ProductsViewModel, authViewModel: AuthViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val navDestinations = remember {
        listOf<Screen>(
            Screen.Products
        )
    }
    val isTokenRefreshing by remember { authViewModel.isRefreshing }

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
        content = { innerPadding ->
            when {
                isTokenRefreshing -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                // Let users in even if token refresh fails. No internet will be handled by other Screens.
                else -> {
                    NavHostContainer(
                        navController = navController,
                        productsViewModel = productsViewModel,
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        },
        drawerContent = {
            DrawerScreen()
        },
        bottomBar = {
            if (showBottomNav(navController = navController)) {
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

        }
    )
}
@Composable
fun showBottomNav(navController: NavHostController): Boolean {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    return currentDestination?.hierarchy?.any { it.route == Screen.Products.route } == true
}