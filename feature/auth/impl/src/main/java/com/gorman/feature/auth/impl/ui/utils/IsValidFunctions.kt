package com.gorman.feature.auth.impl.ui.utils

import android.util.Patterns

fun isEmailValid(email: String): Boolean {
    return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun isPasswordValid(password: String): Boolean {
    return password.isNotEmpty() && password.length >= 6
}
