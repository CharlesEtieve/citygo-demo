package com.eurosportdemo.app.domain.model

import androidx.annotation.StringRes

interface ErrorEvent {

    @StringRes
    fun getErrorResource(): Int
}