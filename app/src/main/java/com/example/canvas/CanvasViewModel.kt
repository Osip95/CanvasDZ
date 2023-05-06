package com.example.canvas

class CanvasViewModel : BaseViewModel<ViewState>() {
    override fun initialViewState(): ViewState =
        ViewState(
            colorList = enumValues<COLOR>().map { ToolItem.ColorModel(it.value) },
            toolsList = enumValues<TOOLS>().map { ToolItem.ToolModel(it) }, // распаковываем енам с инструментами в лист
            sizeList = enumValues<SIZE>().map { ToolItem.SizeModel(it) },
            isPaletteVisible = false,// флаг видимости плашки с палитрой
            canvasViewState = CanvasViewState( // состояние для холста
                color = COLOR.BLACK,
                size = SIZE.MEDIUM,
                tools = TOOLS.NORMAL
            ),
            isToolsVisible = false,
            isBrushSizeChangerVisible = false  // видимость панели инструментов
        )

    init { // при инициализации вьюмодели устанавливаем дефолтные значения: выбора первого инструмента и  выбор черного цвета
        processDataEvent(DataEvent.OnSetDefaultTools(tool = TOOLS.NORMAL, color = COLOR.BLACK))
    }

    override fun reduce(event: Event, previousState: ViewState): ViewState? {
        when (event) {

            is UiEvent.OnToolbarClicked -> { // обработка нажатия на кисточку
                return previousState.copy(
                    isToolsVisible = !previousState.isToolsVisible,
                    isPaletteVisible = false
                )
            }

            is UiEvent.OnToolsClick -> { // обработка нажатия на элемент панели инструментов
                when (event.index) { // индекс нажатого элемента. т.к. index соотоветствует порядку перечесления в енаме TOOLS,
                    TOOLS.PALETTE.ordinal -> { //то сравниваем с порядковым номером кнопки отвечающей за выбор цвета
                        return previousState.copy(isPaletteVisible = !previousState.isPaletteVisible)
                    }
                    TOOLS.SIZE.ordinal -> {
                        return previousState.copy(isBrushSizeChangerVisible = !previousState.isBrushSizeChangerVisible)
                    }

                    else -> { // если нажали на кнопку которая не подразумевает появление другой плашки

                        val toolsList = previousState.toolsList.mapIndexed() { index, model -> // преобразовываем лист инструментов,
                            if (index == event.index) { // в лист с измененым флагом нажатия на нужном элементе. Флаг нажатия внутри в ToolModel
                                model.copy(isSelected = true)
                            } else {
                                model.copy(isSelected = false)
                            }
                        }

                        return previousState.copy(
                            toolsList = toolsList, // возвращаем лист с выделенным элементом
                            canvasViewState = previousState.canvasViewState.copy(tools = TOOLS.values()[event.index])
                        )
                    }
                }
            }

            is UiEvent.OnPaletteClicked -> { // обработка нажатия на конкретный цвет
                val selectedColor = COLOR.values()[event.index] // выбранный цвет, берем по индексу из енама

                val toolsList = previousState.toolsList.map {// меняем нижнию плашку, перекрашивая модельку в соответсвии
                    if (it.type == TOOLS.PALETTE) { //с выбранным цветом в верхней плашке
                        it.copy(selectedColor = selectedColor)
                    } else {
                        it
                    }
                }

                return previousState.copy(
                    toolsList = toolsList,
                    canvasViewState = previousState.canvasViewState.copy(color = selectedColor)
                )
            }

            is DataEvent.OnSetDefaultTools -> { // установка дефолтных значений
                val toolsList = previousState.toolsList.map { model ->
                    if (model.type == event.tool) {
                        model.copy(isSelected = true, selectedColor = event.color)
                    } else {
                        model.copy(isSelected = false)
                    }
                }

                return previousState.copy(
                    toolsList = toolsList
                )
            }

            else -> return null
        }
    }
}