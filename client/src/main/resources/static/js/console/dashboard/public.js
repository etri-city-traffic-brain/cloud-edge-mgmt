var DashboardUI = (function (options) {
    var
        modules = {},
        ServiceDashboardModel = Backbone.Model.extend({
            idAttribute: 'id',
            defaults: {
                id: null,
                type: '',
                totalServer: 0,
                runningServer: 0,
                stoppedServer: 0,
                etcServer: 0,
                account: 0,
                lastUpdatedAt: ''
            }
        }),
        ServiceDashboardCollection = Backbone.Collection.extend({
            url: '/dashboard/resources',
            model: ServiceDashboardModel
        }),
        ServiceDashboardView = Backbone.View.extend({
            el: ".cont_wrap",
            credentialCollection: new (Backbone.Collection.extend({
                url: '/management/credentials/names'
            })),
            events: {
            },
            serverDataLoad: function () {
                var self = this;
                this.collection.fetch({
                    success: function (collection, response, options) {
                        var publicTotal = 0;
                        var publicRunning = 0;
                        var publicStop = 0;
                        var publicEtc = 0;
                        var publicNetwork = 0;
                        var publicLoadBalancer = 0;
                        var publicPublicIp = 0;
                        var resource = 0;
                        var dbCount = 0;
                        var dbUsage = 0;
                        var storageCount = 0;
                        var diskCount = 0;
                        var diskUsage = 0;
                        _.each(collection.models, function(model, index, list) {
                            var m = self.credentialCollection.findWhere({id: model.get("id")});
                            if (m) {
                                if (m.get('cloudType') == "public") {

                                    publicTotal += model.get('totalServer');
                                    publicRunning += model.get('runningServer');
                                    publicStop += model.get('stoppedServer');
                                    publicEtc += model.get('etcServer');
                                    publicNetwork += model.get('network');
                                    publicLoadBalancer += model.get('loadbalancer');
                                    publicPublicIp += model.get('publicIp');
                                    resource += model.get('resources');
                                    dbCount += model.get('databaseCount');
                                    dbUsage += model.get('databaseUsage');
                                    storageCount += model.get('storageCount');
                                    diskCount += model.get('diskCount');
                                    diskUsage += model.get('diskUsage');



                                    $("#" + model.get('id') + "_server").find(".num:eq(0)").toNumberSVG(model.get('runningServer'));
                                    $("#" + model.get('id') + "_server").find(".num:eq(1)").toNumberSVG(model.get('stoppedServer'));
                                    $("#" + model.get('id') + "_server").find(".num:eq(2)").toNumberSVG(model.get('etcServer'));

                                    $("#" + model.get('id') + "_network").find(".num:eq(0)").toNumberSVG(model.get('network'));
                                    $("#" + model.get('id') + "_network").find(".num:eq(1)").toNumberSVG(model.get('loadbalancer'));
                                    $("#" + model.get('id') + "_network").find(".num:eq(2)").toNumberSVG(model.get('publicIp'));

                                    $("#" + model.get('id') + "_db").find(".num:eq(0)").toNumberSVG(model.get('databaseCount'), {unit: '<span class="num_unit">ea</span>'});

                                    $("#" + model.get('id') + "_db").find(".num:eq(1)").toNumberSVG(model.get('storageCount'), {unit: '<span class="num_unit">ea</span>'});
                                    $("#" + model.get('id') + "_db").find(".num:eq(4)").toNumberSVG(model.get('diskCount'), {unit: '<span class="num_unit">ea</span>'});
                                    if(model.get('diskUsage') < 1024) {
                                        $("#" + model.get('id') + "_db").find(".num:eq(2)").toNumberSVG(model.get('diskUsage'), {unit: '<span class="num_unit">GB</span>'});
                                    } else {
                                        $("#" + model.get('id') + "_db").find(".num:eq(2)").toNumberSVG((model.get('diskUsage') / 1024).toFixed(1), {
                                            unit: '<span class="num_unit">TB</span>',
                                            fixed: true
                                        });                                    }

                                    var resourceTemplate = _.template('');

                                }
                            }

                            if(index == list.length - 1) {

                                $("#public_resource_list li").sort(function(a, b) {
                                    var compA = $(a).attr('name').toString().toUpperCase();
                                    var compB = $(b).attr('name').toString().toUpperCase();
                                    return compA < compB? -1 : (compA > compB) ? 1: 0;
                                }).appendTo('#public_resource_list');

                                $("#public_total").toNumberSVG(publicTotal);
                                $("#public_running").toNumberSVG(publicRunning);
                                $("#public_stop").toNumberSVG(publicStop);
                                $("#public_etc").toNumberSVG(publicEtc);
                                $("#public_running_server").toNumberSVG(publicRunning);
                                $("#public_stop_server").toNumberSVG(publicStop);
                                $("#public_etc_server").toNumberSVG(publicEtc);
                                $("#public_network_network").toNumberSVG(publicNetwork);
                                $("#public_loadbalancer_network").toNumberSVG(publicLoadBalancer);
                                $("#public_publicip_network").toNumberSVG(publicPublicIp);
                                $("#public_resource").toNumberSVG(resource);
                                $(".last_updated_at").html("Last Updated : " + model.get('lastUpdatedAt'));

                                $("#public_db_count").toNumberSVG(dbCount, {unit: '<span class="num_unit">ea</span>'});
                                $("#public_storage_count").toNumberSVG(storageCount, {unit: '<span class="num_unit">ea</span>'});
                                $("#public_disk_count").toNumberSVG(diskCount, {unit: '<span class="num_unit">ea</span>'});
                                if(diskUsage < 1024) {
                                    $("#public_disk_usage").toNumberSVG(diskUsage, {unit: '<span class="num_unit">GB</span>'});
                                } else {
                                    $("#public_disk_usage").toNumberSVG((diskUsage / 1024).toFixed(1), {
                                        unit: '<span class="num_unit">TB</span>',
                                        fixed: true
                                    });                                }
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            initialize: function () {
                var self = this;
                this.collection = new ServiceDashboardCollection();

                this.credentialCollection.fetch({
                    success: function (collection, response, options) {
                        $("#clouds").empty();
                        $("#public_server").empty();
                        $("#public_network").empty();
                        $("#public_resource_list").empty();
                        $("#public_db").empty();
                        _.each(collection.models, function(model, index, list) {
                            if(model.get('cloudType') == 'public') {
                                var template = _.template('');
                                var serverTemplate = _.template('');
                                var networkTemplate = _.template('');
                                var dbTemplate = _.template('');
                            }

                            if(index == list.length - 1) {
                                self.serverDataLoad();
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            }
        }),
        init = function (isAdmin) {
            modules.view = new ServiceDashboardView();
        };

    return {
        init: init,
        modules: modules
    };
})(config);
