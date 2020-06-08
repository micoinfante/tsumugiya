package com.ortech.shopapp.Models

data class AdapterItem<out T>(val value: T?, val viewType: Int, var checked: Boolean = false)