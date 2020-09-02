package com.vkeditor.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.util.Size
import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import com.vkcanvas.entity.TransformState
import com.vkcanvas.entity.VKCanvasObject
import com.vkeditor.App
import com.vkeditor.BuildConfig
import com.vkeditor.R
import com.vkeditor.entity.Background
import com.vkeditor.entity.Sticker
import com.vkeditor.entity.StickerObject
import com.vkeditor.model.CanvasModel
import com.vkeditor.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<App>().applicationContext

    val canvasModel = MutableLiveData<CanvasModel>()

    val canvasSize = MutableLiveData<Size>()

    val backgroundObject = map(canvasModel) {
        it.backgroundObject
    }

    private val stickerObjects = map(canvasModel) {
        it.stickerObjects
    }

    private val textObject = map(canvasModel) {
        it.textObject
    }

    private val text = map(canvasModel) {
        it.text
    }

    val allObjects = map(TripleTrigger(backgroundObject, stickerObjects, textObject)) {
        val resultList = mutableListOf<VKCanvasObject>()

        it.first?.let { bg ->
            resultList.add(bg)
        }

        it.second?.let { st ->
            resultList.addAll(st)
        }

        it.third?.let { txt ->
            resultList.add(txt)
        }

        resultList.toList()
    }

    val textStyles
            = App.serviceLocator!!.textStyleRepository.getStyles()

    private val appBackgrounds
            = App.serviceLocator!!.backgroundRepository.getBackgrounds()

    private val userBackground
            = App.serviceLocator!!.userBackgroundRepository.getUserBackground()

    val userBackgroundUploaded
            = App.serviceLocator!!.userBackgroundRepository.backgroundUploaded

    val backgrounds = map(DoubleTrigger(appBackgrounds, userBackground)) {
        val items = mutableListOf<Background>()

        it.first?.let {
            items.addAll(it)
        }

        it.second?.let {
            items.add(it)
        }

        items
    }

    private val _selectedBackgroundIndex = MutableLiveData(-1)
    val selectedBackgroundIndex: LiveData<Int>
        get() = _selectedBackgroundIndex

    private val isBackgroundLoading = MutableLiveData(false)

    private val isImageSaving = MutableLiveData(false)

    val isButtonEnabled = map(DoubleTrigger(isImageSaving, text)) {
        it.first != null && !it.first!!
                && it.second != null && it.second!!.trim().isNotEmpty()
    }

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toasMessage: LiveData<Event<String>>
        get() = _toastMessage

    fun setText(string: String) {
        canvasModel.value?.apply {
            text = string
            canvasModel.value = this
        }
    }

    fun applyNextTextStyle() {
        if (canvasModel.value == null
            || canvasModel.value!!.textStyle == null
            || textStyles.value == null) {
            return
        }

        val currentStyle = canvasModel.value!!.textStyle!!
        val styles = textStyles.value!!
        for (i in styles.indices) {
            if (styles[i] == currentStyle) {
                val nexIndex = (i+1) % styles.size
                canvasModel.value?.apply {
                    textStyle = styles[nexIndex]
                    canvasModel.value = this
                }
                break
            }
        }
    }

    fun setBackground(index: Int) {
        if (isBackgroundLoading.value == true) {
            return
        }

        isBackgroundLoading.value = true
        _selectedBackgroundIndex.value = index

        viewModelScope.launch(Dispatchers.Default) {
            backgrounds.value?.let { items ->
                canvasModel.value?.apply {
                    setBackground(items[index]) // Тут можем долго грузить картинку
                    canvasModel.postValue(this)
                }

                isBackgroundLoading.postValue(false)
            }
        }
    }

    fun addSticker(sticker: Sticker) {
        viewModelScope.launch(Dispatchers.Default) {
            canvasModel.value?.apply {
                addSticker(sticker)
                canvasModel.postValue(this)
            }
        }
    }

    fun updateStickerObject(stickerObject: StickerObject, newState: TransformState) {
        viewModelScope.launch(Dispatchers.Default) {
            canvasModel.value?.apply {
                updateStickerObject(stickerObject, newState)
                canvasModel.postValue(this)
            }
        }
    }

    fun removeSticker(stickerObject: StickerObject) {
        canvasModel.value?.apply {
            removeSticker(stickerObject)
            canvasModel.value = this
        }
    }

    fun setUserImageToBackground(uri: Uri, canvasSize: Size) {
        if (isBackgroundLoading.value == true) {
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            App.serviceLocator!!.userBackgroundRepository.saveUserImage(uri, canvasSize)
        }
    }

    fun saveCanvasImageToGallery() {
        isImageSaving.value = true

        viewModelScope.launch {
            canvasModel.value?.let {
                val bitmap = it.renderToBitmap(allObjects.value!!)

                saveBitmapToGallery(
                    context,
                    bitmap,
                    BuildConfig.APPLICATION_ID,
                    Bitmap.CompressFormat.JPEG,
                    70
                )

                isImageSaving.postValue(false)

                _toastMessage.postValue(
                    Event(context.getString(R.string.toast_saved))
                )
            }
        }
    }

}