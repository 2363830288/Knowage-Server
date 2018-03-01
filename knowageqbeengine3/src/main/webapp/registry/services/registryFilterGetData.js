(function () {
    angular.module('BlankApp')
    	   .factory('regFilterGetData', ['sbiModule_action_builder', 'registryConfigService', '$filter', 
    			   function(sbiModule_action_builder, registryConfigService, $filter) {

    		   var registryConfiguration = registryConfigService.getRegistryConfig();

    		   var data = {};
    		   data.action = sbiModule_action_builder;
    		   data.getData = function(filterField){

       		   	var entity = registryConfiguration.entity;
   	            var SubEntity='';
   	            var foreignKey='';

   	        var column =  $filter('filter')(registryConfiguration.columns,{field:filterField}, true)[0];
    	   	        		SubEntity = column.subEntity;
    	   	        		foreignKey= column.foreignKey;
    	    		   var ENTITY = entity + '::' + SubEntity + '(' + foreignKey + ')' + ':' + filterField;

    			   var loadRegistryAction = this.action.getActionBuilder('POST');
    			   loadRegistryAction.actionName = 'GET_FILTER_VALUES_ACTION';
    			   loadRegistryAction.formParams.ENTITY_ID= ENTITY;
    			   loadRegistryAction.formParams.ORDER_ENTITY= ENTITY;
    			   var promise =  loadRegistryAction.executeAction();
    			   return promise;
    		   };

    		   return data;
    	   }]);
})();