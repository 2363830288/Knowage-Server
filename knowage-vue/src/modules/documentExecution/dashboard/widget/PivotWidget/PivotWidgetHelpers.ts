import { IDataset, IWidget } from "../../Dashboard"

export const getFormattedClickedValueForCrossNavigation = (cellEvent: any, pivotFields: any, dataFields: any) => {
    console.group('CELL CLICKED ---------------------', cellEvent.cellElement)
    console.log('event', cellEvent)
    console.log('pivotFields', pivotFields)
    console.log('this.dataFields[cellEvent.cell.dataIndex]', dataFields[cellEvent.cell.dataIndex])
    console.groupEnd()

    return {}
}

export const createPivotTableSelection = (cellEvent: any, widgetModel: IWidget, datasets: IDataset[]) => {
    if (!cellEvent || !cellEvent.area || !cellEvent.cell) return null
    if (cellEvent.area === 'column' && cellEvent.cell.dataIndex === undefined) {
        return createSelectionFromHeader(cellEvent, widgetModel, datasets, 'columnFields')
    } else if (cellEvent.area === 'row' && cellEvent.cell.dataIndex === undefined && !['T', 'GT'].includes(cellEvent.cell.type)) {
        return createSelectionFromHeader(cellEvent, widgetModel, datasets, 'rowFields')
    }

    return null
}

const createSelectionFromHeader = (cellEvent: any, widgetModel: IWidget, datasets: IDataset[], property: 'columnFields' | 'rowFields') => {
    const value = cellEvent.cell.text
    const index = cellEvent.cell.path.findIndex((pathValue: string | number) => value == pathValue)
    const columnName = index !== -1 && cellEvent[property] && cellEvent[property][index] ? cellEvent[property][index].caption : ''
    return createSelection([value], columnName, widgetModel, datasets)
}

const createSelection = (value: (string | number)[], columnName: string, widget: IWidget, datasets: IDataset[]) => {
    return { datasetId: widget.dataset as number, datasetLabel: getDatasetLabel(widget.dataset as number, datasets) as string, columnName: columnName, value: value, aggregated: false, timestamp: new Date().getTime() }
}

const getDatasetLabel = (datasetId: number, datasets: IDataset[]) => {
    const index = datasets.findIndex((dataset: IDataset) => dataset.id.dsId == datasetId)
    return index !== -1 ? datasets[index].label : ''
}