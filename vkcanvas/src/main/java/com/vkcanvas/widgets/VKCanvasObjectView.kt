package com.vkcanvas.widgets

interface VKCanvasObjectView {

    var isTouchedForTransform: Boolean

    // Помогает для определения верхнего объекта,
    // когда во время касания, под пальцем их было несколько.
    // Пример установки см. в StickerObjectView
    // Пример использования в VKCanvasView.getTouchedObjectView
    var touchId: Int

}