package com.vkeditor.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Size
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

fun getBitmapByUri(context: Context, uri: Uri, targetSize: Size): Bitmap? {
    // Шаг 1: Определяем поворот

    val matrix = Matrix()

    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val exif = ExifInterface(inputStream)
        val orientation: Int = exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )

        val rotate = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }

        matrix.postRotate(rotate.toFloat())

        inputStream.close()
    }

    // Шаг 2: Рассчитываем inJustDecodeBounds, чтобы не грузить жирную картинку

    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true

    context.contentResolver.openInputStream(uri)?.use { inputStream ->
        BitmapFactory.decodeStream(inputStream, null, options)
        options.inSampleSize = getInSampleSize(options, targetSize)
        inputStream.close()
    }

    options.inJustDecodeBounds = false

    // Шаг 3: Грузим и поворачиваем картинку

    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        val bitmap = BitmapFactory.decodeStream(inputStream, null, options) ?: return null
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

fun saveBitmapToGallery(
    context: Context,
    bitmap: Bitmap,
    folderName: String,
    format: Bitmap.CompressFormat,
    quality: Int
) {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/$folderName")
        values.put(MediaStore.Images.Media.IS_PENDING, true)

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        if (uri != null) {
            saveBitmapToStream(
                bitmap,
                context.contentResolver.openOutputStream(uri),
                format,
                quality
            )
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, values, null, null)
        }
    } else {
        val directory = File(Environment.getExternalStorageDirectory().toString() + File.separator + folderName)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, System.currentTimeMillis().toString() + ".png")

        saveBitmapToStream(
            bitmap,
            FileOutputStream(file),
            format,
            quality
        )

        values.put(MediaStore.Images.Media.DATA, file.absolutePath)
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
}

fun saveBitmapToStream(
    bitmap: Bitmap,
    outputStream: OutputStream?,
    format: Bitmap.CompressFormat,
    quality: Int
) {
    if (outputStream != null) {
        try {
            bitmap.compress(format, quality, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun getInSampleSize(
    options: BitmapFactory.Options,
    targetSize: Size
): Int {
    val height = options.outHeight
    val width = options.outWidth
    var inSampleSize = 1
    if (height > targetSize.height || width > targetSize.width) {
        val halfHeight = height / 2
        val halfWidth = width / 2

        while (halfHeight / inSampleSize > targetSize.height
            && halfWidth / inSampleSize > targetSize.width
        ) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}