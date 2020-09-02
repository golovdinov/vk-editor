package com.vkeditor.utils

fun Any?.isNull(f: () -> Unit) {
    if (this == null) {
        f()
    }
}