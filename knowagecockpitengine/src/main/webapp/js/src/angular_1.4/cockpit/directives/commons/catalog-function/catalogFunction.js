/*
Knowage, Open Source Business Intelligence suite
Copyright (C) 2016 Engineering Ingegneria Informatica S.p.A.
Knowage is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.
Knowage is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.
You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
(function(){

	angular.module('cockpitModule').directive('catalogFunction',function(){
		return{
			template:   '<button class="md-button md-knowage-theme" ng-click="addNewCatalogFunction()" ng-class="{\'md-icon-button\':selectedItem && !insideMenu}">'+
						'	<md-icon md-font-icon="fa fa-calculator" ng-if="selectedItem"></md-icon>'+
						'	<span ng-if="!selectedItem">{{::translate.load("sbi.cockpit.widgets.table.catalogFunctions.add")}}</span>'+
						'	<span ng-if="selectedItem && insideMenu">{{::translate.load("sbi.cockpit.widgets.table.catalogFunctions.edit")}}</span>'+
						'	<md-tooltip ng-if="selectedItem && !insideMenu" md-delay="500">{{::translate.load("sbi.cockpit.widgets.table.catalogFunctions.edit")}}</md-tooltip>'+
						'</button>',
			replace: true,
			scope:{
				ngModel: "=",
				selectedItem : "=?",
				callbackUpdateGrid : "&?",
				callbackUpdateAlias : "&?",
				insideMenu : "=?",
				additionalInfo: "=?",
				// A function with no params that return the list
				// of available features
				measuresListFunc : "&?",
				// A function with the new CF to add to the list
				// of fields
				callbackAddTo : "&?"
			},
			controller: catalogFunctionController,
		}
	});

	function catalogFunctionController($scope,sbiModule_translate,sbiModule_restServices,$q,$mdDialog,cockpitModule_datasetServices,$mdToast){
		$scope.translate = sbiModule_translate;
		sbiModule_restServices.restToRootProject();
		sbiModule_restServices.get("2.0/functions-catalog", "").then(
				function(result) {
					$scope.ngModel.allCatalogFunctions = result.data.functions;
				}, function () {
					$scope.ngModel.allCatalogFunctions = {};
				}
		)

		$scope.addNewCatalogFunction = function(){
			var deferred = $q.defer();
			var promise ;
			$mdDialog.show({
				templateUrl:  baseScriptPath+ '/directives/cockpit-columns-configurator/templates/cockpitCatalogFunctionTemplate.html',
				parent : angular.element(document.body),
				clickOutsideToClose:true,
				escapeToClose :true,
				preserveScope: true,
				autoWrap:false,
				locals: {
					promise: deferred,
					model:$scope.ngModel,
					actualItem : $scope.currentRow,
					callbackUpdateGrid: $scope.callbackUpdateGrid,
					callbackUpdateAlias: $scope.callbackUpdateAlias,
					additionalInfo: $scope.additionalInfo,
					measuresListFunc: $scope.measuresListFunc,
					callbackAddTo: $scope.callbackAddTo
				},
				fullscreen: true,
				controller: catalogFunctionDialogController
			}).then(function() {
				deferred.promise.then(function(result){
					$scope.selectedFunctionConfiguration = result;
				});
			}, function() {
			});
			promise =  deferred.promise;
		}
	}

	function catalogFunctionDialogController($scope,sbiModule_translate,cockpitModule_template,sbiModule_restServices,$mdDialog,$q,promise,model,actualItem,callbackUpdateGrid,callbackUpdateAlias,additionalInfo,measuresListFunc,callbackAddTo,cockpitModule_datasetServices,cockpitModule_generalOptions,$timeout, cockpitModule_properties){
		$scope.translate=sbiModule_translate;
		$scope.model = model;

		$scope.model.functionsGrid = {
		        enableColResize: false,
		        enableFilter: true,
		        enableSorting: true,
		        onGridReady: resizeFunctions,
		        onGridSizeChanged: resizeFunctions,
		        rowSelection: "single",
		        onRowSelected: selectFunction,
		        pagination: true,
		        paginationAutoPageSize: true,
		        columnDefs: [
		        	{headerName: $scope.translate.load('sbi.cockpit.widgets.table.catalogFunctions.function.name'), field:'name', headerTooltip:'description'},
		        	{headerName: $scope.translate.load('sbi.cockpit.widgets.table.catalogFunctions.function.type'), field:'type', cellRenderer: languageRenderer, cellStyle: {'display': 'inline-flex', 'justify-content':'center', 'align-items':'center'}}],
		        rowData: $scope.model.allCatalogFunctions
		}

		function resizeFunctions(){
			$scope.model.functionsGrid.api.sizeColumnsToFit();
		}

		function selectFunction(props){
			$scope.selectFunction(props.data);
		}

		function languageRenderer(props){
			return "<i class='fa fa-circle'></i>";
		}

		$scope.model.columnsGrid = {
			angularCompileRows: true,
			domLayout :'autoHeight',
	        enableColResize: false,
	        enableFilter: false,
	        enableSorting: false,
	        onGridReady : resizeColumns,
	        onCellEditingStopped: refreshRowForColumns,
	        singleClickEdit: true,
	        columnDefs: [
	        	{headerName: $scope.translate.load('sbi.cockpit.widgets.table.catalogFunctions.inputColumn.name'), field:'name'},
	        	{headerName: $scope.translate.load('sbi.cockpit.widgets.table.catalogFunctions.inputColumn.type'), field:'type'}]
		}

		function refreshRowForColumns(cell){
			$scope.model.columnsGrid.api.redrawRows({rowNodes: [$scope.columnsGrid.api.getDisplayedRowAtIndex(cell.rowIndex)]});
		}

		function resizeColumns(){
			$scope.model.columnsGrid.api.sizeColumnsToFit();
		}

		$scope.model.variablesGrid = {
			angularCompileRows: true,
			domLayout :'autoHeight',
	        enableColResize: false,
	        enableFilter: false,
	        enableSorting: false,
	        onGridReady : resizeVariables,
	        onCellEditingStopped: refreshRowForVariables,
	        singleClickEdit: true,
	        columnDefs: [
	        	{headerName: $scope.translate.load('sbi.cockpit.widgets.table.catalogFunctions.inputVariable.name'), field:'name'},
	        	{headerName: $scope.translate.load('sbi.cockpit.widgets.table.catalogFunctions.inputVariable.type'), field:'type'}]
		}

		function refreshRowForVariables(cell){
			$scope.model.variablesGrid.api.redrawRows({rowNodes: [$scope.variablesGrid.api.getDisplayedRowAtIndex(cell.rowIndex)]});
		}

		function resizeVariables(){
			$scope.model.variablesGrid.api.sizeColumnsToFit();
		}

		$scope.selectFunction=function(func){
			$scope.model.selectedFunction = func;
			$scope.model.columnsGrid.api.setRowData(func.inputColumns);
			$scope.model.variablesGrid.api.setRowData(func.inputVariables);
		}

		$scope.saveColumnConfiguration=function(){
			promise.resolve($scope.model.selectedFunction);
			$mdDialog.hide();
		}

		$scope.cancelConfiguration=function(){
			$mdDialog.cancel();
		}

		$scope.isSolrDataset = function() {
			if($scope.model.dataset.dsId != undefined) {
				if (cockpitModule_datasetServices.getDatasetById($scope.model.dataset.dsId).type == "SbiSolrDataSet") {
					return true;
				}

			}
			return false;
		}
	}

})();