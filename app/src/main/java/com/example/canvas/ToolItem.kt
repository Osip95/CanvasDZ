package com.example.canvas

import androidx.annotation.ColorRes

sealed class ToolItem : Item {  // единица элемента списка
    data class ColorModel(@ColorRes val color: Int) : ToolItem() // цыет, элемент  инструментов
    data class SizeModel(val size:SIZE) : ToolItem()
    data class ToolModel( //
        val type: TOOLS, // тип инструмента
        val selectedTool: TOOLS = TOOLS.NORMAL, // выбраннй инструмент
        val isSelected: Boolean = false, // флаг нажатия
       val selectedSize: SIZE = SIZE.SMALL, // выбранный размер
        val selectedColor: COLOR = COLOR.BLACK // выбранный цвет
    ) : ToolItem()
}