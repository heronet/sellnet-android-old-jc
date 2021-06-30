package com.heronet.sellnetbeta.ui.screen.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.heronet.sellnetbeta.util.AuthStatus
import com.heronet.sellnetbeta.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    Surface(color = MaterialTheme.colors.background) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisibility by remember { mutableStateOf(false) }
        val isLoading by remember { authViewModel.isLoading }
        val authStatus by remember { authViewModel.authStatus }
        val authErrorText by remember { authViewModel.authErrorText }

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)) {
            OutlinedTextField(
                value = email,
                placeholder = { Text(text =  "Your Email") },
                label = { Text(text = "Email") },
                onValueChange = {email = it},
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth(),
                isError = authErrorText.contains("Email")
            )
            OutlinedTextField(
                value = password,
                placeholder = { Text(text =  "Your Password") },
                visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
                label = { Text(text = "Password") },
                onValueChange = {password = it},
                enabled = !isLoading,
                isError = authErrorText.contains("Password"),
                trailingIcon = {
                    val image = if (passwordVisibility) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisibility = !passwordVisibility }
                    ) {
                        Icon(imageVector  = image, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(4.dp))
            Button(
                onClick = {
                    if(email.isNotBlank() && password.isNotBlank())
                        authViewModel.loginUser(email, password)
                },
                enabled = !isLoading
            ) {
                if (!isLoading)
                    Text(text = "Login")
                else
                    Text(text = "Please Wait...")
            }
        }
        when(authStatus) {
            is AuthStatus.Authenticated -> {
                navController.navigate("products")
            }
        }

    }


}