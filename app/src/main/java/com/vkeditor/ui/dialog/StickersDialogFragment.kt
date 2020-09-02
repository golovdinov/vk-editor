package com.vkeditor.ui.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vkeditor.R
import com.vkeditor.entity.Sticker
import com.vkeditor.ui.adapters.StickersListAdapter
import com.vkeditor.ui.viewmodel.StickersViewModel


class StickersDialogFragment: DialogFragment() {

    interface SelectStickerListener {
        fun onStickerSelected(sticker: Sticker)
    }

    companion object {
        fun newInstance() = StickersDialogFragment()
    }

    private val viewModel: StickersViewModel by viewModels()
    private val stickersAdapter = StickersListAdapter()
    private val stickersLayoutManager = GridLayoutManager(context, 4)

    override fun getTheme() = R.style.Theme_App_Dialog

    override fun onAttach(context: Context) {
        super.onAttach(context)

        viewModel.stickerItems.observe(this, Observer {
            stickersAdapter.updateList(it)
        })

        stickersAdapter.onItemClick = { position ->
            viewModel.stickerItems.value?.let {
                (activity as SelectStickerListener).onStickerSelected(it[position])
                dismiss()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return layoutInflater.inflate(R.layout.dialog_stickers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<RecyclerView>(R.id.rvStickers).apply {
            adapter = stickersAdapter
            layoutManager = stickersLayoutManager

            val divider = view.findViewById<View>(R.id.divider)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    divider.visibility = if (recyclerView.canScrollVertically(-1)) View.VISIBLE else View.INVISIBLE
                }
            })
        }
    }

    override fun onResume() {
        dialog?.window?.attributes?.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.WRAP_CONTENT
            gravity = Gravity.BOTTOM
            horizontalMargin = 0F

            dialog?.window?.attributes = this
        }

        super.onResume()
    }

}