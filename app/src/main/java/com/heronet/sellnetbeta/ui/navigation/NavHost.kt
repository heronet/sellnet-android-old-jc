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
    NavHost(navController = navController, startDestination = Screen.Products.route, modifier = modifier) {
        composable(Screen.Products.route) {
            ProductsListScreen(productsViewModel = productsViewModel, navController)
        }
        composable("${Screen.Products.route}/{productId}",
            arguments = listOf(
                navArgument("productId"){ type = NavType.StringType }
            )
        ) { backStackEntry ->
            ProductDetailScreen(
                productsViewModel = productsViewModel,
                productId = backStackEntry.arguments?.getString("productId")!!
            )
        }
        composable(Screen.AddProduct.route) {
            when(authViewModel.authStatus.value) {
                is AuthStatus.Authenticated -> {
                    AddProductScreen(productsViewModel, authViewModel, navController)
                }
                is AuthStatus.Unauthenticated -> {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.AddProduct.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
        composable(Screen.Login.route) {
            when(authViewModel.authStatus.value) {
                is AuthStatus.Unauthenticated -> {
                    LoginScreen(navController = navController, authViewModel = authViewModel)
                }
                is AuthStatus.Authenticated -> { // Prevent LoginScreen access if authenticated
                    navController.navigate(Screen.Products.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
        composable(Screen.Register.route) {
            when(authViewModel.authStatus.value) {
                is AuthStatus.Unauthenticated -> {
                    RegisterScreen(navController = navController, authViewModel = authViewModel)
                }
                is AuthStatus.Authenticated -> { // Prevent RegisterScreen access if authenticated
                    navController.navigate(Screen.Products.route) {
                        popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }
}