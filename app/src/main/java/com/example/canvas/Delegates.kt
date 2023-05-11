package com.example.canvas

import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateLayoutContainer

//делегаты, подтянуты из сторонней библиотеки для сокращения кода написания адаптера

fun colorAdapterDelegate(
    onClick: (Int) -> Unit
): AdapterDelegate<List<Item>> =
    adapterDelegateLayoutContainer<ToolItem.ColorModel, Item>(
        R.layout.item_palette
    ) {

        val color: ImageView = findViewById(R.id.color)
        itemView.setOnClickListener { onClick(adapterPosition) }

        bind { list ->
            color.setColorFilter(
                context.resources.getColor(item.color, null),
                PorterDuff.Mode.SRC_IN
            )
        }
    }

fun sizeChangeAdapterDelegate(
    onSizeClick: (Int) -> Unit
): AdapterDelegate<List<Item>> = adapterDelegateLayoutContainer<ToolItem.SizeModel, Item>(
    R.layout.item_size
) {

    val tvSize: TextView = findViewById(R.id.tvSizeText)
    bind { list ->
        tvSize.text = item.size.value.toString()
        itemView.setOnClickListener {
            onSizeClick(adapterPosition)
        }
    }
}

fun toolsAdapterDelegate(
    onToolsClick: (Int) -> Unit
): AdapterDelegate<List<Item>> = adapterDelegateLayoutContainer<ToolItem.ToolModel, Item>(
    R.layout.item_tools
) {

    val ivTool: ImageView = findViewById(R.id.ivTool)
    val tvToolsText: TextView = findViewById(R.id.tvToolsText)

    bind { list ->
        ivTool.setImageResource(item.type.value)
        if (tvToolsText.visibility == View.VISIBLE) {
            tvToolsText.visibility = View.GONE
        }
        when (item.type) {

            TOOLS.SIZE -> {
                tvToolsText.visibility = View.VISIBLE
                tvToolsText.text = item.selectedSize.value.toString()
            }

            TOOLS.PALETTE -> {
                ivTool.setColorFilter(
                    context.resources.getColor(item.selectedColor.value, null),
                    PorterDuff.Mode.SRC_IN
                )
            }

            else -> {
                if (item.isSelected) {
                    ivTool.setBackgroundResource(R.drawable.bg_selected)
                } else {
                    ivTool.setBackgroundResource(android.R.color.transparent)
                }
            }
        }

        itemView.setOnClickListener {
            onToolsClick(adapterPosition)
        }
    }
}