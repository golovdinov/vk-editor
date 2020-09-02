package com.vkeditor.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Size
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkcanvas.widgets.VKCanvasTextObjectView
import com.vkcanvas.widgets.VKCanvasView
import com.vkeditor.R
import com.vkeditor.entity.Background
import com.vkeditor.entity.Sticker
import com.vkeditor.model.CanvasModel
import com.vkeditor.ui.adapters.BackgroundsListAdapter
import com.vkeditor.ui.adapters.CanvasAdapter
import com.vkeditor.ui.dialog.StickersDialogFragment
import com.vkeditor.ui.viewmodel.MainViewModel
import com.vkeditor.ui.widgets.TrashView
import com.vkeditor.utils.EventObserver
import com.vkeditor.utils.isNull
import kotlin.math.min

class MainActivity: BaseActivity(), StickersDialogFragment.SelectStickerListener {

    companion object {
        const val REQUEST_CODE_PICK_FILE = 203
    }

    private val viewModel: MainViewModel by viewModels()

    private val canvasAdapter = CanvasAdapter()
    private val backgroundsAdapter = BackgroundsListAdapter()
    private val backgroundsLayoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

    private val canvasSize by lazy {
        if (viewModel.canvasSize.value != null) {
            viewModel.canvasSize.value!!
        } else {
            val size = generateCanvasSize()
            viewModel.canvasSize.value = size
            size
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initCanvasModel()
        initCanvasView()
        initTrashView()
        initBackground()
        initStickers()
        initStyles()
        initSaveButton()

        viewModel.toasMessage.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_CODE_PICK_FILE -> {
                if (resultCode == Activity.RESULT_OK && data != null && data.data != null) {
                    viewModel.setUserImageToBackground(data.data!!, canvasSize)
                }
            }
        }
    }

    private fun initCanvasModel() {
        viewModel.canvasSize.observe(this, Observer {
            canvasAdapter.canvasSize = it
        })

        viewModel.canvasModel.value.isNull {
            val canvasModel = CanvasModel(
                canvasSize,
                getString(R.string.text_placeholder)
            )

            viewModel.canvasModel.value = canvasModel
        }
    }

    private fun initCanvasView() {
        viewModel.allObjects.observe(this, Observer {
            canvasAdapter.updateObjects(it)
        })

        canvasAdapter.onTextChanged = {
            viewModel.setText(it)
        }

        canvasAdapter.onStickerStateChanged = { stickerObject, newState ->
            viewModel.updateStickerObject(stickerObject, newState)
        }

        canvasAdapter.onStickerRemoved = {
            viewModel.removeSticker(it)
        }

        findViewById<VKCanvasView>(R.id.vkCanvasView).let { canvasView ->
            canvasView.layoutParams.let { lp ->
                lp.width = canvasSize.width
                lp.height = canvasSize.height
                canvasView.layoutParams = lp
            }

            canvasView.onCanvasClickListener = {
                // Юзер кликнул на canvas (не было перемещения объектов).
                // Здесь мы могли бы создать новый объект с текстом,
                // но мы берём существующий и будем его редактировать

                if (canvasView.childCount > 1) {
                    val probablyTextView = canvasView.getChildAt(canvasView.childCount-2)
                    (probablyTextView as? VKCanvasTextObjectView)?.requestFocusAndOpenKeyboard()
                }
            }

            canvasView.adapter = canvasAdapter
        }
    }

    private fun initTrashView() {
        viewModel.backgroundObject.observe(this, Observer { it ->
            val style = when(it?.id) {
                Background.ID_WHITE -> TrashView.Style.WithBorder
                else -> TrashView.Style.WithShadow
            }
            canvasAdapter.setTrashViewStyle(style)
        })

        val size = 256
        val trashViewBounds = Rect(
            (canvasSize.width - size) / 2,
            canvasSize.height - size,
            (canvasSize.width - size) / 2 + size,
            canvasSize.height
        )

        canvasAdapter.setTrashViewBounds(trashViewBounds)
    }

    private fun initBackground() {
        viewModel.backgrounds.observe(this, Observer {
            backgroundsAdapter.updateList(it)

            if (viewModel.selectedBackgroundIndex.value == -1) {
                viewModel.setBackground(0)
            }
        })

        viewModel.selectedBackgroundIndex.observe(this, Observer {
            backgroundsAdapter.selectedPosition = it
        })

        viewModel.userBackgroundUploaded.observe(this, EventObserver {
            viewModel.backgrounds.value?.let {
                viewModel.setBackground(it.lastIndex)
            }
        })

        backgroundsAdapter.onItemClickListener = { index ->
            viewModel.setBackground(index)
        }

        backgroundsAdapter.onAddNewClickListener = {
            requestPermission(
                PermissionCallback.Builder(Manifest.permission.READ_EXTERNAL_STORAGE)
                    .onGranted {
                        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        intent.type = "image/*";
                        startActivityForResult(intent, REQUEST_CODE_PICK_FILE)
                    }
                    .onDenied {
                        Toast.makeText(this, getString(R.string.error_load_image_permission), Toast.LENGTH_SHORT).show()
                    }
                    .build()
            )
        }

        findViewById<RecyclerView>(R.id.rvBackgrounds).apply {
            adapter = backgroundsAdapter
            layoutManager = backgroundsLayoutManager
        }
    }

    private fun initStyles() {
        // Когда загрузятся стили, устанавливаем первый
        viewModel.textStyles.observe(this, Observer { styles ->
            viewModel.canvasModel.value?.textStyle.isNull {
                viewModel.canvasModel.value?.apply {
                    textStyle = styles[0]
                    viewModel.canvasModel.value = this
                }
            }
        })

        findViewById<ImageView>(R.id.ivTextStyle).setOnClickListener {
            viewModel.applyNextTextStyle()
        }
    }

    private fun initStickers() {
        findViewById<ImageView>(R.id.ivStickers).setOnClickListener {
            val dialogFragment = StickersDialogFragment.newInstance()
            dialogFragment.show(supportFragmentManager, null)
        }
    }

    private fun initSaveButton() {
        val btn = findViewById<Button>(R.id.btnSave)

        viewModel.isButtonEnabled.observe(this, Observer {
            btn.isEnabled = it
        })

        btn.setOnClickListener {
            requestPermission(
                PermissionCallback.Builder(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .onGranted {
                        viewModel.saveCanvasImageToGallery()
                    }
                    .onDenied {
                        Toast.makeText(this, getString(R.string.error_save_image_permission), Toast.LENGTH_SHORT).show()
                    }
                    .build()
            )
        }
    }

    // StickersDialogFragment.SelectStickerListener
    override fun onStickerSelected(sticker: Sticker) {
        viewModel.addSticker(sticker)
    }

    private fun generateCanvasSize(): Size {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var width = min(displayMetrics.widthPixels, displayMetrics.heightPixels)
        width = min(1600, width)
        return Size(width, width)
    }

}