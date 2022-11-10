import { ISelection, IVariable, IWidget } from '@/modules/documentExecution/dashboard/Dashboard'
import { formatSelectionForDisplay } from '../../../ActiveSelectionsWidget/ActiveSelectionsWidgetHelpers'
import deepcopy from 'deepcopy'
import { formatNumberWithLocale } from '@/helpers/commons/localeHelper'

const widgetIdRegex = /#\[kn-widget-id\]/g
const activeSelectionsRegex = /(?:\[kn-active-selection(?:=\'([a-zA-Z0-9\_\-]+)\')?\s?\])/g
const columnRegex = /(?:\[kn-column=\'([a-zA-Z0-9\_\-\s]+)\'(?:\s+row=\'(\d*)\')?(?:\s+aggregation=\'(AVG|MIN|MAX|SUM|COUNT_DISTINCT|COUNT|DISTINCT COUNT)\')?(?:\s+precision=\'(\d)\')?(\s+format)?\s?\])/g
const rowsRegex = /(?:\[kn-column=\'([a-zA-Z0-9\_\-\s]+)\'(?:\s+row=\'(\d+)\'){1}(?:\s+aggregation=\'(AVG|MIN|MAX|SUM|COUNT_DISTINCT|COUNT|DISTINCT COUNT)\')?(?:\s+precision=\'(\d)\')?(\s+format)?\s?\])/g
const noAggregationsExistRegex = /\[kn-column=\'[a-zA-Z0-9\_\-\s]+\'(?:\s+row=\'\d+\')?(?!\s+aggregation=\'(AVG|MIN|MAX|SUM|COUNT_DISTINCT|COUNT|DISTINCT COUNT)\')(?:\s+precision=\'(?:\d)\')?(?:\s+format)?\s?\]/g
const limitRegex = /<[\s\w\=\"\'\-\[\]]*(?!limit=)"([\-\d]+)"[\s\w\=\"\'\-\[\]]*>/g
const aggregationsRegex = /(?:\[kn-column=[\']{1}([a-zA-Z0-9\_\-\s]+)[\']{1}(?:\s+row=\'(\d*)\')?(?:\s+aggregation=[\']{1}(AVG|MIN|MAX|SUM|COUNT_DISTINCT|COUNT|DISTINCT COUNT)[\']{1}){1}(?:\s+precision=\'(\d)\')?(\s+format)?\])/g
const aggregationRegex = /(?:\[kn-column=[\']{1}([a-zA-Z0-9\_\-\s]+)[\']{1}(?:\s+row=\'(\d*)\')?(?:\s+aggregation=[\']{1}(AVG|MIN|MAX|SUM|COUNT_DISTINCT|COUNT|DISTINCT COUNT)[\']{1}){1}(?:\s+precision=\'(\d)\')?(\s+format)?\])/
const paramsRegex = /(?:\[kn-parameter=[\'\"]{1}([a-zA-Z0-9\_\-\s]+)[\'\"]{1}(\s+value)?\])/g
const calcRegex = /(?:\[kn-calc=\(([\[\]\w\s\-\=\>\<\"\'\!\+\*\/\%\&\,\.\|]*)\)(?:\s+min=\'(\d*)\')?(?:\s+max=\'(\d*)\')?(?:\s+precision=\'(\d)\')?(\s+format)?\])/g
const advancedCalcRegex = /(?:\[kn-calc=\{([\(\)\[\]\w\s\-\=\>\<\"\'\!\+\*\/\%\&\,\.\|]*)\}(?:\s+min=\'(\d*)\')?(?:\s+max=\'(\d*)\')?(?:\s+precision=\'(\d)\')?(\s+format)?\])/g
const repeatIndexRegex = /\[kn-repeat-index\]/g
const variablesRegex = /(?:\[kn-variable=\'([a-zA-Z0-9\_\-\s]+)\'(?:\s+key=\'([a-zA-Z0-9\_\-\s]+)\')?\s?\])/g
const i18nRegex = /(?:\[kn-i18n=\'([a-zA-Z0-9\_\-\s]+)\'\s?\])/g
const gt = /(\<.*kn-.*=["].*)(>)(.*["].*\>)/g
const lt = /(\<.*kn-.*=["].*)(<)(.*["].*\>)/g

let drivers = [] as any[]
let variables = [] as IVariable[]
let activeSelections = [] as ISelection[]
let widgetModel = null as IWidget | null
let translatedValues = {} as any
let widgetData = {} as any

import mockedData from './mockedData.json'
const aggregationDataset = null as any // TODO

export const parseText = (tempWidgetModel: IWidget, tempDrivers: any[], tempVariables: IVariable[], tempSelections: ISelection[], internationalization: any) => {
    drivers = tempDrivers
    variables = tempVariables
    activeSelections = tempSelections
    widgetModel = tempWidgetModel
    translatedValues = internationalization

    const unparsedText = widgetModel.settings.editor.text
    if (!unparsedText) return ''

    let parsedText = checkTextWidgetPlaceholders(unparsedText)
    parsedText = replaceTextFunctions(parsedText)

    return parsedText
}

const checkTextWidgetPlaceholders = (unparsedText: string) => {
    unparsedText = unparsedText.replace(paramsRegex, paramsReplacer)
    unparsedText = unparsedText.replace(variablesRegex, variablesReplacer)
    return unparsedText
}

const replaceTextFunctions = (parsedText: string) => {
    const parser = new DOMParser()
    const parsedHtml = parser.parseFromString(parsedText, 'text/html')
    let allElements = parsedHtml.getElementsByTagName('*')
    allElements = parseAttrs(allElements)
    return parsedHtml.firstChild ? (parsedHtml.firstChild as any).innerHTML : ''
}

export const parseHtml = (tempWidgetModel: IWidget, tempDrivers: any[], tempVariables: IVariable[], tempSelections: ISelection[], tempInternationalization: any, tempWidgetData: any) => {
    drivers = tempDrivers
    variables = tempVariables
    activeSelections = tempSelections
    widgetModel = tempWidgetModel
    translatedValues = tempInternationalization
    widgetData = tempWidgetData

    const css = widgetModel.settings.editor.css
    let trustedCss = ''

    if (css) {
        let placeholderResultCss = checkPlaceholders(css)
        placeholderResultCss = parseCalc(placeholderResultCss)
        trustedCss = placeholderResultCss
    }

    const html = widgetModel.settings.editor.html
    let trustedHtml = ''

    if (html) {
        let wrappedHtmlToRender = '<div>' + html + ' </div>'
        wrappedHtmlToRender = wrappedHtmlToRender.replace(gt, '$1&gt;$3')
        wrappedHtmlToRender = wrappedHtmlToRender.replace(lt, '$1&lt;$3')

        const parseHtmlFunctionsResult = parseHtmlFunctions(wrappedHtmlToRender)
        trustedHtml = parseHtmlFunctionsResult
    }

    return { html: trustedHtml, css: trustedCss }
}

const getColumnFromName = (columnName: string, datasetData: any, aggregation: any) => {
    for (var i in datasetData.metaData.fields) {
        if (typeof datasetData.metaData.fields[i].header != 'undefined' && datasetData.metaData.fields[i].header.toLowerCase() == (aggregation ? columnName + '_' + aggregation : columnName).toLowerCase()) {
            return { name: datasetData.metaData.fields[i].name, type: datasetData.metaData.fields[i].type }
        }
    }
}

const parseHtmlFunctions = (rawHtml: string) => {
    const parser = new DOMParser()
    const parsedHtml = parser.parseFromString(rawHtml, 'text/html')
    let allElements = parsedHtml.getElementsByTagName('*')
    allElements = parseRepeat(allElements)
    allElements = parseIf(allElements) // TODO - additional
    allElements = parseAttrs(allElements)
    const placeholderResultHtml = checkPlaceholders(parsedHtml.firstChild ? (parsedHtml.firstChild as any).innerHTML : '')
    const parseCalcResultHtml = parseCalc(placeholderResultHtml)
    return parseCalcResultHtml
}

// TODO
const parseAggregations = () => {
    // TODO
}

const parseRepeat = (allElements: any) => {
    let i = 0
    do {
        if (!allElements[i].innerHTML) allElements[i].innerHTML = ' '
        if (allElements[i] && allElements[i].hasAttribute('kn-repeat')) {
            if (eval(checkAttributePlaceholders(allElements[i].getAttribute('kn-repeat')))) {
                allElements[i].removeAttribute('kn-repeat')
                let limit = allElements[i].hasAttribute('limit') && allElements[i].hasAttribute('limit') <= mockedData.rows.length ? allElements[i].getAttribute('limit') : mockedData.rows.length
                if (allElements[i].hasAttribute('limit') && allElements[i].getAttribute('limit') == -1) limit = mockedData.rows.length
                if (allElements[i].hasAttribute('limit')) allElements[i].removeAttribute('limit')
                const repeatedElement = deepcopy(allElements[i])
                allElements[i].outerHTML = formatRepeatedElement(limit, repeatedElement)
            } else {
                allElements[i].outerHTML = ''
            }
        }
        i++
    } while (i < allElements.length)
    return allElements
}

const formatRepeatedElement = (limit: number, repeatedElement: any) => {
    let tempElement = null
    for (var j = 0; j < limit; j++) {
        const tempRow = deepcopy(repeatedElement)
        tempRow.innerHTML = tempRow.innerHTML.replace(columnRegex, function (match: string, columnName: string, row: string, c3: string, precision: string, format: string) {
            let precisionPlaceholder = ''
            let formatPlaceholder = ''
            if (format) formatPlaceholder = ' format'
            if (precision) precisionPlaceholder = " precision='" + precision + "'"
            return "[kn-column='" + columnName + "' row='" + (row || j) + "'" + precisionPlaceholder + formatPlaceholder + ']'
        })
        tempRow.innerHTML = tempRow.innerHTML.replace(repeatIndexRegex, j)
        j == 0 ? (tempElement = tempRow.outerHTML) : (tempElement += tempRow.outerHTML)
    }
    return tempElement
}

const parseIf = (allElements: any) => {
    var j = 0
    var nodesNumber = allElements.length
    do {
        if (allElements[j] && allElements[j].hasAttribute('kn-if')) {
            var condition = allElements[j].getAttribute('kn-if').replace(columnRegex, ifConditionReplacer)
            condition = condition.replace(activeSelectionsRegex, activeSelectionsReplacer)
            //  condition = condition.replace(paramsRegex, ifConditionParamsReplacer);  // TODO
            condition = condition.replace(calcRegex, calcReplacer)
            condition = condition.replace(variablesRegex, variablesReplacer)
            condition = condition.replace(i18nRegex, i18nReplacer)
            if (eval(condition)) {
                allElements[j].removeAttribute('kn-if')
            } else {
                allElements[j].parentNode.removeChild(allElements[j])
                j--
            }
        }
        j++
    } while (j < nodesNumber)
    return allElements
}

const parseAttrs = (allElements: any) => {
    // TODO - change function signatures
    let j = 0
    const nodesNumber = allElements.length
    do {
        if (allElements[j] && allElements[j].hasAttribute('kn-preview')) {
            allElements[j].classList.add('preview-class-temp')
        }
        if (allElements[j] && allElements[j].hasAttribute('kn-cross')) {
            allElements[j].classList.add('cross-nav-class-temp')
        }
        if (allElements[j] && allElements[j].hasAttribute('kn-selection-column')) {
            allElements[j].classList.add('select-class-temp')
        }
        j++
    } while (j < nodesNumber)
    return allElements
}

const parseCalc = (rawHtml: string) => {
    rawHtml = rawHtml.replace(advancedCalcRegex, calcReplacer)
    rawHtml = rawHtml.replace(calcRegex, calcReplacer)
    return rawHtml
}

const checkPlaceholders = (document: string) => {
    let resultHtml = document ?? ''

    resultHtml = resultHtml.replace(columnRegex, columnsReplacer)
    resultHtml = resultHtml.replace(columnRegex, columnsReplacer)

    resultHtml = resultHtml.replace(activeSelectionsRegex, activeSelectionsReplacer)
    resultHtml = resultHtml.replace(widgetIdRegex, '') // TODO - Check if needed
    resultHtml = resultHtml.replace(paramsRegex, paramsReplacer)
    resultHtml = resultHtml.replace(variablesRegex, variablesReplacer)
    resultHtml = resultHtml.replace(i18nRegex, i18nReplacer)

    return resultHtml
}

const activeSelectionsReplacer = (match: string, columnName: string) => {
    const index = activeSelections.findIndex((selection: ISelection) => selection.datasetId === widgetModel?.dataset && selection.columnName === columnName)
    return index !== -1 ? formatSelectionForDisplay(activeSelections[index]) : 'null'
}

const addSlashes = (value: string | null) => {
    return (value + '')
        .replace(/\"/g, '&quot;')
        .replace(/\'/g, '&apos;')
        .replace(/\u0000/g, '\\0')
}

const calcReplacer = (match: string, p1: string, min: string, max: string, precision: any, format: string) => {
    var result = eval(p1)
    if (min && result < min) result = min
    if (max && result > max) result = max
    if (format) return precision ? parseFloat(result).toFixed(precision) : result
    return precision && !isNaN(result) ? parseFloat(result).toFixed(precision) : result
}

// TODO - aggregationDataset ???
const ifConditionReplacer = (match: string, p1: any, row: string, aggr: string, precision: number) => {
    const columnInfo = getColumnFromName(p1, aggr ? aggregationDataset : mockedData, aggr)
    if (!columnInfo) return p1
    if (aggr) {
        p1 = aggregationDataset && aggregationDataset.rows[0] && aggregationDataset.rows[0][columnInfo.name] !== '' && typeof aggregationDataset.rows[0][columnInfo.name] != 'undefined' ? aggregationDataset.rows[0][columnInfo.name] : null
    } else if (mockedData && mockedData.rows[row || 0] && typeof mockedData.rows[row || 0][columnInfo.name] != 'undefined' && mockedData.rows[row || 0][columnInfo.name] !== '') {
        let columnValue = mockedData.rows[row || 0][columnInfo.name]
        if (typeof columnValue == 'string') columnValue = addSlashes(columnValue)
        p1 = columnInfo.type == 'string' ? "'" + columnValue + "'" : columnValue
    } else {
        p1 = null
    }
    return precision && !isNaN(p1) ? parseFloat(p1).toFixed(precision) : p1
}

// TODO
const ifConditionParamsReplacer = () => {
    // TODO
}

const columnsReplacer = (match, column, row, aggr, precision, format) => {
    console.log('COLUMNS REPLACER', match, column, row, aggr, precision, format)

    const columnInfo = getColumnFromName(column, aggr ? aggregationDataset : widgetData, aggr)
    console.log('%c columnInfo columnInfo columnInfo ', 'color: white; background-color: #61dbfb')
    console.log(columnInfo)

    if (!columnInfo) return column

    if (aggr) {
        column = aggregationDataset && aggregationDataset.rows[0] && aggregationDataset.rows[0][columnInfo.name] !== '' && typeof aggregationDataset.rows[0][columnInfo.name] != 'undefined' ? aggregationDataset.rows[0][columnInfo.name] : null
    } else if (widgetData && widgetData.rows[row || 0] && typeof widgetData.rows[row || 0][columnInfo.name] != 'undefined' && widgetData.rows[row || 0][columnInfo.name] !== '') {
        column = widgetData.rows[row || 0][columnInfo.name]
    } else {
        column = null
    }

    if ((column != null && columnInfo.type == 'int') || columnInfo.type == 'float') {
        if (format) column = precision ? formatNumberWithLocale(column, precision, null) : formatNumberWithLocale(column, undefined, null)
        else column = precision ? parseFloat(column).toFixed(precision) : parseFloat(column)
    }
    console.log('%c returned  column column ', 'color: white; background-color: #61dbfb')
    console.log(column)
    return column
}

export const paramsReplacer = (match: string, p1: string, p2: string) => {
    // TODO - Change when we finish drivers
    const index = drivers.findIndex((driver: any) => driver.urlName === p1)
    if (index === -1) return addSlashes(null)
    const result = p2 ? drivers[index].description : drivers[index].value
    return addSlashes(result)
}

export const variablesReplacer = (match: string, p1: string, p2: string) => {
    const index = variables.findIndex((variable: IVariable) => variable.name === p1)
    if (index === -1) return null
    const result = p2 && variables[index].pivotedValues ? variables[index].pivotedValues[p2] : variables[index].value
    return result || null
}

// TODO
const i18nReplacer = (match: string, p1: string) => {
    const result = translatedValues[p1] ? translatedValues[p1] : p1
    return result || null
}

const checkAttributePlaceholders = (rawAttribute: string) => {
    // TODO
    // let resultAttribute = rawAttribute.replace($scope.columnRegex, $scope.replacer); - TODO
    let resultAttribute = rawAttribute.replace(paramsRegex, paramsReplacer)
    return resultAttribute
}
