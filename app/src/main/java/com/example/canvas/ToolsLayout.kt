package com.example.canvas

import android.content.Context
import android.util.AttributeSet
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class ToolsLayout @JvmOverloads constructor(  //кастомная вью, панель с ресайклером вью для размещения цветов/инструметов
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private var onClick: (Int) -> Unit = {}

    private val toolsList: RecyclerView by lazy { findViewById(R.id.rvTools) }

    private val adapterDelegate = ListDelegationAdapter(
        colorAdapterDelegate {
            onClick(it)
        },
        toolsAdapterDelegate {
            onClick(it)
        },
        sizeChangeAdapterDelegate {
            onClick(it)
        }
    )

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        toolsList.layoutManager = LinearLayoutManager(
            context,
            RecyclerView.HORIZONTAL,
            false
        ) // устанавливаем горизонтпльное направление элементов и реверс
        toolsList.setAdapterAndCleanupOnDetachFromWindow(adapterDelegate) // через функцию расширения устанавливаем адаптер
    }

    fun render(list: List<ToolItem>) { // метод установки данных для отображения в RV
        adapterDelegate.setData(list)
    }

    fun setOnClickListener(onClick: (Int) -> Unit) { // метод для установки слушателя нажатия для каждого элемента панели инструметов
        this.onClick = onClick
    }
}