package com.vkeditor.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.vkeditor.App

class StickersViewModel: ViewModel() {

    val stickerItems
            = App.serviceLocator!!.stickerRepository.getStickers()

}