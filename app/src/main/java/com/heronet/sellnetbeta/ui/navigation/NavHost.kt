package com.heronet.sellnetbeta.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import com.heronet.sellnetbeta.ui.screen.AddProductScreen
import com.heronet.sellnetbeta.ui.screen.ProductDetailScreen
import com.heronet.sellnetbeta.ui.screen.ProductsListScreen
import com.heronet.sellnetbeta.ui.screen.authentication.LoginScreen
import com.heronet.sellnetbeta.ui.screen.authentication.RegisterScreen
import com.heronet.sellnetbeta.util.AuthStatus
import com.heronet.sellnetbeta.viewmodel.AuthViewModel
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel

@Composable
fun NavHostContainer(
    navController: NavHostController,
    productsViewModel: ProductsViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier,
) {
    NavHost(navController = navController, startDestination = "products", modifier = modifier) {
        composable("products") {
            ProductsListScreen(productsViewModel = productsViewModel, navController)
        }
        composable("products/{productId}",
            arguments = listOf(
                navArgument("productId"){ type = NavType.StringType }
            )
        ) { backStackEntry ->
            ProductDetailScreen(
                productsViewModel = productsViewModel,
                productId = backStackEntry.arguments?.getString("productId")!!
            )
        }
        composable("add-product") {
            when(authViewModel.authStatus.value) {
                is AuthStatus.Authenticated -> {
                    AddProductScreen(productsViewModel = productsViewModel, navController)
                }
                is AuthStatus.Unauthenticated -> {
                    LoginScreen(
                        navController = navController,
                        authViewModel = authViewModel
                    )
                }
            }
        }
        composable("login") {
            when(authViewModel.authStatus.value) {
                is AuthStatus.Unauthenticated -> {
                    LoginScreen(navController = navController, authViewModel = authViewModel)
                }
                is AuthStatus.Authenticated -> { // Prevent LoginScreen access if authenticated
                    navController.navigate("products") {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
        composable("register") {
            when(authViewModel.authStatus.value) {
                is AuthStatus.Unauthenticated -> {
                    RegisterScreen(navController = navController, authViewModel = authViewModel)
                }
                is AuthStatus.Authenticated -> { // Prevent RegisterScreen access if authenticated
                    navController.navigate("products") {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}