package com.heronet.sellnetbeta.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heronet.sellnetbeta.data.UserRepository
import com.heronet.sellnetbeta.dataStore
import com.heronet.sellnetbeta.model.AuthData
import com.heronet.sellnetbeta.util.AuthStatus
import com.heronet.sellnetbeta.util.Constants.ID
import com.heronet.sellnetbeta.util.Constants.NAME
import com.heronet.sellnetbeta.util.Constants.ROLE
import com.heronet.sellnetbeta.util.Constants.TOKEN
import com.heronet.sellnetbeta.util.Resource
import com.heronet.sellnetbeta.web.LoginDto
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserRepository,
    @ApplicationContext private val context: Context
): ViewModel() {
    var authStatus = mutableStateOf<AuthStatus>(AuthStatus.Unauthenticated())
    var isLoading = mutableStateOf(false)
    var isRefreshing = mutableStateOf(false)
    var authErrorText = mutableStateOf("")

    init {
        refreshToken()
    }

    fun loginUser(email: String, password: String) {
        isLoading.value = true
        val loginDto = LoginDto(email, password)
        viewModelScope.launch {
            val resource = repository.loginUser(loginDto)
            setAuthData(resource)
        }
    }
    private fun refreshToken() {
        isRefreshing.value = true
        viewModelScope.launch {
            val dataStore = context.dataStore.data.first()
            val token = dataStore[TOKEN]
            if (token == null) {
                isRefreshing.value = false
            } else {
                val authData = AuthData(
                    id = dataStore[ID]!!,
                    name = dataStore[NAME]!!,
                    token = token,
                    roles = listOf(dataStore[ROLE]!!)
                )
                val resource = repository.refreshToken(token, authData)
                setAuthData(resource)
                isRefreshing.value = false
            }
        }
    }
    private suspend fun setAuthData(resource: Resource<AuthData>) {
        when(resource) {
            is Resource.Error -> {
                isLoading.value = false
                authStatus.value = AuthStatus.Unauthenticated()
                authErrorText.value = resource.message!!
            }
            is Resource.Success -> {
                isLoading.value = false
                authErrorText.value = ""
                authStatus.value = AuthStatus.Authenticated(authData = resource.data!!)

                context.dataStore.edit { authData ->
                    authData[TOKEN] = authStatus.value.authData!!.token
                    authData[ID] = authStatus.value.authData!!.id
                    authData[NAME] = authStatus.value.authData!!.name
                    authData[ROLE] = authStatus.value.authData!!.roles[0]
                }

            }
            is Resource.Loading -> {}
        }
    }
}