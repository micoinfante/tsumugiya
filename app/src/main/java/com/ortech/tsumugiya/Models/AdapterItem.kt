package com.ortech.tsumugiya.Models

data class AdapterItem<out T>(val value: T?, val viewType: Int, var checked: Boolean = false)