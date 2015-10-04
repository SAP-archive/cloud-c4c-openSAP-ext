sap.ui.controller("view.Opportunity", {

	oDataC4CServiceURL: "/destinations/c4codata",
	oDataHCPServiceUrl: "/destinations/c4cext.svc/Opportunities/Risk",

	/**
	 * Called when a controller is instantiated and its View controls (if available) are already created.
	 * Can be used to modify the View before it is displayed, to bind event handlers and do other one-time initialization.
	 * @memberOf view.Opportunity
	 */
	onInit: function() {
		var oDataModel = new sap.ui.model.odata.ODataModel(this.oDataC4CServiceURL);
		oDataModel.attachRequestCompleted({}, function(){
			var oList = this.getView().byId("opportunitiesList");
			oList.setSelectedIndex(0);
		}, this);
		this.getView().setModel(oDataModel);

		this.getView().setModel(new sap.ui.model.json.JSONModel(), "riskModel");
	},

	onListItemPress: function(evt) {

		var ctx = evt.getParameter("rowContext");
		if (ctx){
			var oForm = this.getView().byId("opportunityDetails");
			oForm.bindElement(ctx.getPath());

			//fetch the Risk value using the RiskAssessment application
			var riskModel = this.getView().getModel("riskModel");
			riskModel.loadData(this.oDataHCPServiceUrl + "?opportunityID=" + ctx.getObject().OpportunityID);
		}
	}
});