import { HighchartsPieChartModel } from "@/modules/documentExecution/dashboard/interfaces/highcharts/DashboardHighchartsPieChartWidget"
import { KnowageHighcharts } from "./KnowageHihgcharts"
import { updatePieChartModel } from "./updater/HighchartsPieChartUpdater"

export class HighchartsPieChart extends KnowageHighcharts {
    constructor(model: HighchartsPieChartModel, range: any[]) {
        super(model, range)
    }

    updateModel(oldModel: any) {
        this.model = updatePieChartModel(oldModel, this.model)
    }

    setData(data: any, drillDownLevel = 0) {
        const categoryColumnName = data.metaData.fields.filter((i) => i.header === this.model.settings.categories[drillDownLevel])[0].name
        this.model.series.map((item, serieIndex) => {
            const dataColumn = item.groupingFunction ? item.name + '_' + item.groupingFunction : item.name
            this.range[serieIndex] = { serie: item.name }
            const dataColumnName = data.metaData.fields.filter((i) => i.header === dataColumn)[0].name
            item.data = []
            data.rows.forEach((row: any, index: number) => {
                let serieElement = {
                    "id": row.id,
                    "name": row[categoryColumnName],
                    "y": row[dataColumnName],
                    drilldown: false,
                    color: '' as any
                }
                this.range[serieIndex].min = this.range[serieIndex].min ? Math.min(this.range[serieIndex].min, row[dataColumnName]) : row[dataColumnName]
                this.range[serieIndex].max = this.range[serieIndex].max ? Math.max(this.range[serieIndex].max, row[dataColumnName]) : row[dataColumnName]
                if (this.model.settings.drilldown) serieElement.drilldown = true
                if (this.model.settings.colorPalette?.COLOR[index]?.value) serieElement.color = this.model.settings.colorPalette.COLOR[index].value
                item.data.push(serieElement)
            })

        })
        return this.model.series
    }
}
