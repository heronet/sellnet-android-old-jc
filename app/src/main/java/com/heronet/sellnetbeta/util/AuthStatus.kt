package com.heronet.sellnetbeta.util

import com.heronet.sellnetbeta.model.AuthData

sealed class AuthStatus(val authData: AuthData? = null) {
    class Authenticated(authData: AuthData): AuthStatus(authData)
    class Unauthenticated: AuthStatus(null)
}