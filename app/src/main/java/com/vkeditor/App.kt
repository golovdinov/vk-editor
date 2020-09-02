package com.vkeditor

import android.app.Application
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType

class App: Application() {

    companion object {
        var serviceLocator: ServiceLocator? = null
    }

    override fun onCreate() {
        super.onCreate()
        serviceLocator = ServiceLocator(applicationContext)

        val config = ImageLoaderConfiguration
            .Builder(applicationContext)
            .diskCacheSize(5 * 1024 * 1024)
            .build()
        ImageLoader.getInstance().init(config)
    }

}