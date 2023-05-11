package com.example.canvas

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.core.view.isVisible
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    companion object { // констаны - индексы панелей, сделаны для наглядности
        private const val PALETTE_VIEW = 0
        private const val TOOLS_VIEW = 1
        private const val SIZE_VIEW = 2
    }

    private val viewModel: CanvasViewModel by viewModel()

    private var toolsList: List<ToolsLayout> = listOf() // список панелей
    private val paletteLayout: ToolsLayout by lazy { findViewById(R.id.paletteLayout) } // панель с цветами
    private val toolsLayout: ToolsLayout by lazy { findViewById(R.id.toolLayout) } // панель с инструментами
    private val ivTools: ImageView by lazy { findViewById(R.id.ivTools) } // кнопка отображения/скрытия панели инструментов
    private val drawView: DrawView by lazy { findViewById(R.id.viewDraw) } // холст
    private val sizeLayout: ToolsLayout by lazy { findViewById(R.id.sizeLayout) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolsList = listOf(paletteLayout, toolsLayout, sizeLayout)
        viewModel.viewState.observe(this, ::render)

        paletteLayout.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnPaletteClicked(it))
        }

        toolsLayout.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnToolsClick(it))
        }

        ivTools.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnToolbarClicked)
        }

        sizeLayout.setOnClickListener {
            viewModel.processUiEvent(UiEvent.OnSizeClick(it))
        }
    }

    private fun render(viewState: ViewState) {

        with(toolsList[PALETTE_VIEW]) {
            render(viewState.colorList) // добавляем данные в панель с цветами
            isVisible = viewState.isPaletteVisible // устанавливаем видимость панели с цветами
        }

        with(toolsList[TOOLS_VIEW]) { // тоже самое для панели с мнструментами
            render(viewState.toolsList)
            isVisible = viewState.isToolsVisible
        }

        with(toolsList[SIZE_VIEW]) {
            render(viewState.sizeList)
            isVisible = viewState.isBrushSizeChangerVisible
        }

        drawView.render(viewState.canvasViewState) // устанавливаем для холста параметры размер/тип кисти,цвет
    }
}