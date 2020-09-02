package com.vkeditor.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vkcanvas.util.scaleCenterCrop
import com.vkeditor.entity.Background
import com.vkeditor.entity.BitmapBackground
import com.vkeditor.utils.Event
import com.vkeditor.utils.getBitmapByUri
import com.vkeditor.utils.saveBitmapToStream
import java.io.File
import java.io.FileOutputStream

class UserBackgroundRepository(private val context: Context) {

    companion object {
        const val fileName = "user_background.jpg"
    }

    private val userBackground by lazy {
        val liveData = MutableLiveData<Background>()

        val file = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            fileName
        )

        if (file.exists()) {
            liveData.postValue(
                BitmapBackground(
                    System.currentTimeMillis().toString(),
                    "file://${file.absolutePath}",
                    "file://${file.absolutePath}"
                )
            )
        }

        liveData
    }

    private val _backgroundUploaded = MutableLiveData<Event<Boolean>>()
    val backgroundUploaded: LiveData<Event<Boolean>>
        get() = _backgroundUploaded

    fun getUserBackground(): LiveData<Background> = userBackground

    fun saveUserImage(uri: Uri, canvasSize: Size) {
        getBitmapByUri(context, uri, canvasSize)?.let { bitmap ->
            bitmap.scaleCenterCrop(Size(canvasSize.width, canvasSize.height)).let { bitmap ->
                val file = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    fileName
                )

                if (file.exists()) {
                    file.delete()
                }

                file.createNewFile()

                saveBitmapToStream(
                    bitmap,
                    FileOutputStream(file),
                    Bitmap.CompressFormat.JPEG,
                    90
                )

                userBackground.postValue(
                    BitmapBackground(
                        System.currentTimeMillis().toString(),
                        "file://${file.absolutePath}",
                        "file://${file.absolutePath}"
                    )
                )

                _backgroundUploaded.postValue(Event(true))
            }
        }
    }

}