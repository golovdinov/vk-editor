package com.vkeditor.ui.activity

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

open class BaseActivity: AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_PERMISSION = 101;
    }

    class PermissionCallback private constructor(
        val permission: String,
        val onGranted: (() -> Unit)?,
        val onDenied: (() -> Unit)?
    ) {
        class Builder(private val permission: String) {
            private var _onGranted: (() -> Unit)? = null
            private var _onDenied: (() -> Unit)? = null

            fun onGranted(onGranted: (() -> Unit)): Builder {
                _onGranted = onGranted
                return this
            }

            fun onDenied(onDenied: (() -> Unit)): Builder {
                _onDenied = onDenied
                return this
            }

            fun build(): PermissionCallback {
                return PermissionCallback(permission, _onGranted!!, _onDenied!!)
            }
        }
    }

    private val pendingPermissionCallbacks: MutableList<PermissionCallback> = mutableListOf()

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE_PERMISSION -> {
                pendingPermissionCallbacks
                    .filter { it.permission == permissions[0] }
                    .map { callback ->
                        when {
                            grantResults.isNotEmpty()
                                    && grantResults[0] == PackageManager.PERMISSION_GRANTED -> {
                                callback.onGranted?.invoke()
                            }
                            else -> {
                                callback.onDenied?.invoke()
                            }
                        }
                        pendingPermissionCallbacks.remove(callback)
                    }
            }
        }
    }

    fun requestPermission(callback: PermissionCallback) {
        when (ContextCompat.checkSelfPermission(this, callback.permission)) {
            PackageManager.PERMISSION_GRANTED -> {
                callback.onGranted?.invoke()
            }
            else -> {
                pendingPermissionCallbacks.add(callback)

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(callback.permission),
                    REQUEST_CODE_PERMISSION
                )
            }
        }
    }

}