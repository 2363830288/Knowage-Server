import { ITableWidgetColumnStyles, ITableWidgetConditionalStyles, ITawbleWidgetHeadersStyle, IWidgetBackgroundStyle, IWidgetBordersStyle, IWidgetExports, IWidgetInteractions, IWidgetPaddingStyle, IWidgetResponsive, IWidgetRowsStyle, IWidgetShadowsStyle, IWidgetTitle } from '../../Dashboard'

export interface IPivotTableSettings {
    updatable: boolean
    clickable: boolean
    conditionalStyles: ITableWidgetConditionalStyles // TODO - Darko see if needed or it needs to be changed to use another interface (probably)
    configuration: IPivotTableConfiguration
    interactions: IWidgetInteractions
    style: IPivotTableStyle
    responsive: IWidgetResponsive
}

export interface IPivotTableConfiguration {
    rows: IPivotRowsConfiguration
    columns: IPivotColumnsConfiguration
    exports: IWidgetExports
}

export interface IPivotTableStyle {
    title: IWidgetTitle
    headers: ITawbleWidgetHeadersStyle
    columns: ITableWidgetColumnStyles
    rows: IWidgetRowsStyle
    background: IWidgetBackgroundStyle
    borders: IWidgetBordersStyle
    padding: IWidgetPaddingStyle
    shadows: IWidgetShadowsStyle
}

export interface IPivotRowsConfiguration {
    grandTotal: boolean
    subTotal: boolean
}

export interface IPivotColumnsConfiguration {
    grandTotal: boolean
    subTotal: boolean
}
