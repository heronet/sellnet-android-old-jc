package com.heronet.sellnetbeta.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.heronet.sellnetbeta.ui.navigation.NavHostContainer
import com.heronet.sellnetbeta.ui.navigation.Screen
import com.heronet.sellnetbeta.util.AuthStatus
import com.heronet.sellnetbeta.viewmodel.AuthViewModel
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel

@Composable
fun MainScreen(productsViewModel: ProductsViewModel, authViewModel: AuthViewModel) {
    val scaffoldState = rememberScaffoldState()
    val navController = rememberNavController()
    val navDestinations = remember {
        listOf(
            Screen.Products,
            Screen.Login
        )
    }
    val navIcons = remember {
        mapOf(
            Screen.Products.route to Icons.Filled.ShoppingCart,
            Screen.Login.route to Icons.Filled.Login,
        )
    }
    val isTokenRefreshing by remember { authViewModel.isRefreshing }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                when(navBackStackEntry?.destination?.route) {
                    Screen.Products.route -> {
                        Text(
                            text = stringResource(id = Screen.Products.resourceId),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    "${Screen.Products.route}/{productId}" -> {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(text = stringResource(id = Screen.Products.resourceId), fontSize = 20.sp)
                    }
                    Screen.AddProduct.route -> {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(text = stringResource(id = Screen.AddProduct.resourceId), fontSize = 20.sp)
                    }
                    Screen.Login.route -> {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(text = stringResource(id = Screen.Login.resourceId), fontSize = 20.sp)
                    }
                    Screen.Register.route -> {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null)
                        }
                        Text(text = stringResource(id = Screen.Register.resourceId), fontSize = 20.sp)
                    }
                    else -> {
                        Text(text = "Sellnet", fontSize = 20.sp, modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
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
        bottomBar = {
            if (showBottomNav(navController = navController)) {
                BottomNavigation {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    navDestinations.filter {
                        when(authViewModel.authStatus.value) {
                            is AuthStatus.Authenticated -> {
                                it.route != Screen.Login.route
                            }
                            else -> it == it
                        }
                    }.forEach { screen ->
                        BottomNavigationItem(
                            icon = { Icon(
                                imageVector = navIcons[screen.route]!!,
                                contentDescription = null
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
    return currentDestination?.hierarchy?.any { it.route == Screen.Products.route || it.route == Screen.Login.route } == true
}

