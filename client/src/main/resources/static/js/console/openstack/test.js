var MeterServerUI = (function (options) {
    var
        modules = {},
        BillingServerModel = Backbone.Model.extend({
            urlRoot: '/private/openstack/meter/servers',
            defaults: {
                id: null,
                projectId: '',
                instanceId: '',
                flavorId: '',
                flavorName: '',
                instanceName: '',
                meterDuration: '',
                meterStartTime: '',
                meterEndTime: '',
                billing: ''
            }
        }),
        BillingServerCollection = Backbone.Collection.extend({
            model: BillingServerModel
        }),
        ServerModel = Backbone.Model.extend({
            idAttribute: 'id',
            defaults: {
                instanceId: '',
                status: '',
                createdAt: '',
                id: null
            }
        }),
        ServerCollection = Backbone.Collection.extend({
            model: ServerModel
        }),
        BillingServerDetailView = Backbone.View.extend({

        })
}