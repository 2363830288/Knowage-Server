export default function updateModel(oldModel, newModel) {
    newModel.type = "pie"

    if (oldModel.CHART.COLORPALETTE?.COLOR) {
        newModel.colors = oldModel.CHART.COLORPALETTE.COLOR.map(item => item.value)
    }

    //CATEGORIES
    newModel.settings.dimensions = [oldModel.CHART.VALUES.CATEGORY.name]

    //SERIE
    newModel.settings.values = [{
        name: oldModel.CHART.VALUES.SERIE[0].name,
        aggregation: oldModel.CHART.VALUES.SERIE[0].groupingFunction
    }]

    //LEGEND
    if (!newModel.options) newModel.options = {}
    if (oldModel.CHART.LEGEND?.show) {

        newModel.options.legend = {
            display: true,
            align: oldModel.CHART.LEGEND.style?.align || 'center',
            verticalAlign: oldModel.CHART.LEGEND.position,
            layout: 'horizontal',
        }
    } else newModel.options.legend = { display: false }

    return newModel
}
