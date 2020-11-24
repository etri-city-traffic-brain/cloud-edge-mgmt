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
                        var privateTotal = 0;
                        var privateRunning = 0;
                        var privateStop = 0;
                        var privateEtc = 0;
                        var privateNetwork = 0;
                        var privateLoadBalancer = 0;
                        var privatePublicIp = 0;
                        var resource = 0;
                        var hypervisorVcpus = 0;
                        var hypervisorVcpusUsed = 0;
                        var hypervisorMemory = 0;
                        var hypervisorMemoryUsed = 0;
                        var hypervisorDisk = 0;
                        var hypervisorDiskUsed = 0;
                        _.each(collection.models, function(model, index, list) {
                            var m = self.credentialCollection.findWhere({id: model.get("id")});
                            if (m) {
                                if (m.get('cloudType') == "private") {
                                    privateTotal += model.get('totalServer');
                                    privateRunning += model.get('runningServer');
                                    privateStop += model.get('stoppedServer');
                                    privateEtc += model.get('etcServer');
                                    privateNetwork += model.get('network');
                                    privateLoadBalancer += model.get('loadbalancer');
                                    privatePublicIp += model.get('publicIp');
                                    resource += model.get('resources');
                                    hypervisorVcpus += model.get('hypervisorVcpus');
                                    hypervisorVcpusUsed += model.get('hypervisorVcpusUsed');
                                    if(m.get('type') == "openstack") {
                                        hypervisorMemory += (model.get('hypervisorMemory') * 1048576); //MB
                                        hypervisorDisk += (model.get('hypervisorDisk') * 1073741824); //GB
                                        hypervisorMemoryUsed += (model.get('hypervisorMemoryUsed') * 1048576); //MB
                                        hypervisorDiskUsed += (model.get('hypervisorDiskUsed') * 1073741824); //GB
                                    } else {
                                        hypervisorMemory += model.get('hypervisorMemory'); //byte
                                        hypervisorDisk += model.get('hypervisorDisk'); //byte
                                        hypervisorMemoryUsed += model.get('hypervisorMemoryUsed'); //byte
                                        hypervisorDiskUsed += model.get('hypervisorDiskUsed'); //byte
                                    }

                                    if(m.get('type') == "openstack") {
                                        $("#" + model.get('id') + "_account").toNumberSVG(model.get('account'), {unit: "<span>"+jQuery.i18n.prop('title.jqgrid.project')+"</span>"});
                                    } else if(m.get('type') == "vmware") {
                                        $("#" + model.get('id') + "_account").toNumberSVG(model.get('account'), {unit: "<span>"+jQuery.i18n.prop('title.jqgrid.physical-server')+"</span>"});
                                    } else if(m.get('type') == "uniq") {
                                        $("#" + model.get('id') + "_account").toNumberSVG(model.get('account'), {unit: "<span>tenants</span>"});
                                    }

                                    $("#" + model.get('id') + "_server").find(".num:eq(0)").toNumberSVG(model.get('runningServer'));
                                    $("#" + model.get('id') + "_server").find(".num:eq(1)").toNumberSVG(model.get('stoppedServer'));
                                    $("#" + model.get('id') + "_server").find(".num:eq(2)").toNumberSVG(model.get('etcServer'));

                                    $("#" + model.get('id') + "_network").find(".num:eq(0)").toNumberSVG(model.get('network'));
                                    $("#" + model.get('id') + "_network").find(".num:eq(1)").toNumberSVG(model.get('loadbalancer'));
                                    $("#" + model.get('id') + "_network").find(".num:eq(2)").toNumberSVG(model.get('publicIp'));

                                    $("#" + model.get('id') + "_hypervisor").find(".num:eq(0)").toNumberSVG(model.get('hypervisorVcpus'), {unit: '<span class="num_unit">ea</span>'});
                                    $("#" + model.get('id') + "_hypervisor").find(".num:eq(2)").toNumberSVG(model.get('hypervisorVcpusUsed'), {unit: '<span class="num_unit">ea</span>'});

                                    if(m.get('type') == "openstack") {
                                        if(model.get('hypervisorMemoryUsed') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG(model.get('hypervisorMemoryUsed'), {unit: '<span class="num_unit">MB</span>'});
                                        } else if(model.get('hypervisorMemoryUsed') < 1048576) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG((model.get('hypervisorMemoryUsed')/1024).toFixed(1), {unit: '<span class="num_unit">GB</span>', fixed: true});
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG((model.get('hypervisorMemoryUsed')/1048576).toFixed(1), {unit: '<span class="num_unit">TB</span>', fixed: true});
                                        }

                                        if (model.get('hypervisorMemory') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG(model.get('hypervisorMemory'), {unit: '<span class="num_unit">MB</span>'});
                                        } else if (model.get('hypervisorMemory') < 1024 * 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG((model.get('hypervisorMemory') / 1024).toFixed(1), {
                                                unit: '<span class="num_unit">GB</span>',
                                                fixed: true
                                            });
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG((model.get('hypervisorMemory') / (1024 * 1024)).toFixed(1), {
                                                unit: '<span class="num_unit">TB</span>',
                                                fixed: true
                                            });
                                        }

                                        if (model.get('hypervisorDisk') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG(model.get('hypervisorDisk'), {unit: '<span class="num_unit">GB</span>'});
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG((model.get('hypervisorDisk') / 1024).toFixed(1), {
                                                unit: '<span class="num_unit">TB</span>',
                                                fixed: true
                                            });
                                        }

                                        if (model.get('hypervisorDiskUsed') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG(model.get('hypervisorDiskUsed'), {unit: '<span class="num_unit">GB</span>'});
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG((model.get('hypervisorDiskUsed') / 1024).toFixed(1), {
                                                unit: '<span class="num_unit">TB</span>',
                                                fixed: true
                                            });
                                        }
                                    } else {

                                        if (model.get('hypervisorMemoryUsed') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG(model.get('hypervisorMemoryUsed'), {unit: '<span class="num_unit">Byte</span>'});
                                        } else if (model.get('hypervisorMemoryUsed') < 1048576) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG((model.get('hypervisorMemoryUsed') / 1024).toFixed(1), {
                                                unit: '<span class="num_unit">KB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorMemoryUsed') < 1073741824) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG((model.get('hypervisorMemoryUsed') / 1048576).toFixed(1), {
                                                unit: '<span class="num_unit">MB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorMemoryUsed') < 1099511627776) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG((model.get('hypervisorMemoryUsed') / 1073741824).toFixed(1), {
                                                unit: '<span class="num_unit">GB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorMemoryUsed') < 1125899906842624) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG((model.get('hypervisorMemoryUsed') / 1099511627776).toFixed(1), {
                                                unit: '<span class="num_unit">TB</span>',
                                                fixed: true
                                            });
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(5)").toNumberSVG((model.get('hypervisorMemoryUsed') / 1125899906842624).toFixed(1), {
                                                unit: '<span class="num_unit">PB</span>',
                                                fixed: true
                                            });
                                        }

                                        if (model.get('hypervisorMemory') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG(model.get('hypervisorMemory'), {unit: '<span class="num_unit">Byte</span>'});
                                        } else if (model.get('hypervisorMemory') < 1048576) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG((model.get('hypervisorMemory') / 1024).toFixed(1), {
                                                unit: '<span class="num_unit">KB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorMemory') < 1073741824) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG((model.get('hypervisorMemory') / 1048576).toFixed(1), {
                                                unit: '<span class="num_unit">MB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorMemory') < 1099511627776) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG((model.get('hypervisorMemory') / 1073741824).toFixed(1), {
                                                unit: '<span class="num_unit">GB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorMemory') < 1125899906842624) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG((model.get('hypervisorMemory') / 1099511627776).toFixed(1), {
                                                unit: '<span class="num_unit">TB</span>',
                                                fixed: true
                                            });
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(3)").toNumberSVG((model.get('hypervisorMemory') / 1125899906842624).toFixed(1), {
                                                unit: '<span class="num_unit">PB</span>',
                                                fixed: true
                                            });
                                        }


                                        if (model.get('hypervisorDisk') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG(model.get('hypervisorDisk'), {unit: '<span class="num_unit">Byte</span>'});
                                        } else if (model.get('hypervisorDisk') < 1048576) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG((model.get('hypervisorDisk') / 1024).toFixed(1), {
                                                unit: '<span class="num_unit">KB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorDisk') < 1073741824) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG((model.get('hypervisorDisk') / 1048576).toFixed(1), {
                                                unit: '<span class="num_unit">MB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorDisk') < 1099511627776) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG((model.get('hypervisorDisk') / 1073741824).toFixed(1), {
                                                unit: '<span class="num_unit">GB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorDisk') < 1125899906842624) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG((model.get('hypervisorDisk') / 1099511627776).toFixed(1), {
                                                unit: '<span class="num_unit">TB</span>',
                                                fixed: true
                                            });
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(6)").toNumberSVG((model.get('hypervisorDisk') / 1125899906842624).toFixed(1), {
                                                unit: '<span class="num_unit">PB</span>',
                                                fixed: true
                                            });
                                        }

                                        if (model.get('hypervisorDiskUsed') < 1024) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG(model.get('hypervisorDiskUsed'), {unit: '<span class="num_unit">Byte</span>'});
                                        } else if (model.get('hypervisorDiskUsed') < 1048576) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG((model.get('hypervisorDiskUsed') / 1024).toFixed(1), {
                                                unit: '<span class="num_unit">KB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorDiskUsed') < 1073741824) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG((model.get('hypervisorDiskUsed') / 1048576).toFixed(1), {
                                                unit: '<span class="num_unit">MB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorDiskUsed') < 1099511627776) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG((model.get('hypervisorDiskUsed') / 1073741824).toFixed(1), {
                                                unit: '<span class="num_unit">GB</span>',
                                                fixed: true
                                            });
                                        } else if (model.get('hypervisorDiskUsed') < 1125899906842624) {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG((model.get('hypervisorDiskUsed') / 1099511627776).toFixed(1), {
                                                unit: '<span class="num_unit">TB</span>',
                                                fixed: true
                                            });
                                        } else {
                                            $("#" + model.get('id') + "_hypervisor").find(".num:eq(8)").toNumberSVG((model.get('hypervisorDiskUsed') / 1125899906842624).toFixed(1), {
                                                unit: '<span class="num_unit">PB</span>',
                                                fixed: true
                                            });
                                        }
                                    }

                                    var resourceTemplate = _.template('');

                                    if(m.get('type') == "openstack") {

                                        resourceTemplate = _.template($("#openstack_resource_template").html());

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_server',
                                            name: jQuery.i18n.prop('title.tab.instance')
                                        }));
                                        $("#" + model.get('id') + "_resource_server").toNumberSVG(model.get('totalServer'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_image',
                                            name: jQuery.i18n.prop('title.tab.image')
                                        }));
                                        $("#" + model.get('id') + "_resource_image").toNumberSVG(model.get('image'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_keypair',
                                            name: jQuery.i18n.prop('title.tab.keypair')
                                        }));
                                        $("#" + model.get('id') + "_resource_keypair").toNumberSVG(model.get('keypair'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_flavor',
                                            name: jQuery.i18n.prop('title.jqgrid.flavor')
                                        }));
                                        $("#" + model.get('id') + "_resource_flavor").toNumberSVG(model.get('flavor'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_volume',
                                            name: jQuery.i18n.prop('title.tab.volume')
                                        }));
                                        $("#" + model.get('id') + "_resource_volume").toNumberSVG(model.get('volume'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_backup',
                                            name: jQuery.i18n.prop('title.tab.backup')
                                        }));
                                        $("#" + model.get('id') + "_resource_backup").toNumberSVG(model.get('backup'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_snapshot',
                                            name: jQuery.i18n.prop('title.tab.snapshot')
                                        }));
                                        $("#" + model.get('id') + "_resource_snapshot").toNumberSVG(model.get('snapshot'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_network',
                                            name: jQuery.i18n.prop('title.tab.network')
                                        }));
                                        $("#" + model.get('id') + "_resource_network").toNumberSVG(model.get('network'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_router',
                                            name:jQuery.i18n.prop('title.tab.router')
                                        }));
                                        $("#" + model.get('id') + "_resource_router").toNumberSVG(model.get('router'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'securitygroup',
                                            name: jQuery.i18n.prop('w.security-group')
                                        }));
                                        $("#" + model.get('id') + "_securitygroup").toNumberSVG(model.get('securityGroup'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_floatingip',
                                            name: jQuery.i18n.prop('title.tab.floatingIp')
                                        }));
                                        $("#" + model.get('id') + "_resource_floatingip").toNumberSVG(model.get('publicIp'));

                                    } else if(m.get('type') == "vmware") {

                                        resourceTemplate = _.template($("#vmware_resource_template").html());

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_server',
                                            name: jQuery.i18n.prop('title.tab.instance')
                                        }));
                                        $("#" + model.get('id') + "_resource_server").toNumberSVG(model.get('totalServer'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_datacenter',
                                            name: jQuery.i18n.prop('title.tab.datacenter')
                                        }));
                                        $("#" + model.get('id') + "_resource_datacenter").toNumberSVG(model.get('datacenter'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_cluster',
                                            name: jQuery.i18n.prop('title.tab.cluster')
                                        }));
                                        $("#" + model.get('id') + "_resource_cluster").toNumberSVG(model.get('cluster'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_host',
                                            name: jQuery.i18n.prop('title.jqgrid.physical-server')
                                        }));
                                        $("#" + model.get('id') + "_resource_host").toNumberSVG(model.get('host'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_datastore',
                                            name: jQuery.i18n.prop('title.tab.datastore')
                                        }));
                                        $("#" + model.get('id') + "_resource_datastore").toNumberSVG(model.get('datastore'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_network',
                                            name: jQuery.i18n.prop('title.tab.network')
                                        }));
                                        $("#" + model.get('id') + "_resource_network").toNumberSVG(model.get('network'));

                                    } else if(m.get('type') == "uniq") {

                                        resourceTemplate = _.template($("#uniq_resource_template").html());

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_server',
                                            name: jQuery.i18n.prop('title.jqgrid.server')
                                        }));
                                        $("#" + model.get('id') + "_resource_server").toNumberSVG(model.get('totalServer'));

                                        $("#private_resource_list").append(resourceTemplate({
                                            id:model.get('id'),
                                            resource: 'resource_image',
                                            name: jQuery.i18n.prop('title.jqgrid.image')
                                        }));
                                        $("#" + model.get('id') + "_resource_image").toNumberSVG(model.get('image'));
                                    }
                                }
                            }

                            if(index == list.length - 1) {

                                $("#private_resource_list li").sort(function(a, b) {
                                    var compA = $(a).attr('name').toString().toUpperCase();
                                    var compB = $(b).attr('name').toString().toUpperCase();
                                    return compA < compB? -1 : (compA > compB) ? 1: 0;
                                }).appendTo('#private_resource_list');

                                $("#private_total").toNumberSVG(privateTotal);
                                $("#private_running").toNumberSVG(privateRunning);
                                $("#private_stop").toNumberSVG(privateStop);
                                $("#private_etc").toNumberSVG(privateEtc);
                                $("#private_running_server").toNumberSVG(privateRunning);
                                $("#private_stop_server").toNumberSVG(privateStop);
                                $("#private_etc_server").toNumberSVG(privateEtc);
                                $("#private_network_network").toNumberSVG(privateNetwork);
                                $("#private_loadbalancer_network").toNumberSVG(privateLoadBalancer);
                                $("#private_publicip_network").toNumberSVG(privatePublicIp);
                                $("#private_resource").toNumberSVG(resource);
                                $(".last_updated_at").html("Last Updated : " + model.get('lastUpdatedAt'));

                                $("#private_hypervisor_vcpus").toNumberSVG(hypervisorVcpus, {unit: '<span class="num_unit">ea</span>'});
                                $("#private_hypervisor_vcpus_used").toNumberSVG(hypervisorVcpusUsed, {unit: '<span class="num_unit">ea</span>'});


                                if (hypervisorMemoryUsed < 1024) {
                                    $("#private_hypervisor_memory_used").toNumberSVG(hypervisorMemoryUsed, {unit: '<span class="num_unit">Byte</span>'});
                                } else if (hypervisorMemoryUsed < 1048576) {
                                    $("#private_hypervisor_memory_used").toNumberSVG((hypervisorMemoryUsed / 1024).toFixed(1), {
                                        unit: '<span class="num_unit">KB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorMemoryUsed < 1073741824) {
                                    $("#private_hypervisor_memory_used").toNumberSVG((hypervisorMemoryUsed / 1048576).toFixed(1), {
                                        unit: '<span class="num_unit">MB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorMemoryUsed < 1099511627776) {
                                    $("#private_hypervisor_memory_used").toNumberSVG((hypervisorMemoryUsed / 1073741824).toFixed(1), {
                                        unit: '<span class="num_unit">GB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorMemoryUsed < 1125899906842624) {
                                    $("#private_hypervisor_memory_used").toNumberSVG((hypervisorMemoryUsed / 1099511627776).toFixed(1), {
                                        unit: '<span class="num_unit">TB</span>',
                                        fixed: true
                                    });
                                } else {
                                    $("#private_hypervisor_memory_used").toNumberSVG((hypervisorMemoryUsed / 1125899906842624).toFixed(1), {
                                        unit: '<span class="num_unit">PB</span>',
                                        fixed: true
                                    });
                                }

                                if (hypervisorMemory < 1024) {
                                    $("#private_hypervisor_memory").toNumberSVG(hypervisorMemory, {unit: '<span class="num_unit">Byte</span>'});
                                } else if (hypervisorMemory < 1048576) {
                                    $("#private_hypervisor_memory").toNumberSVG((hypervisorMemory / 1024).toFixed(1), {
                                        unit: '<span class="num_unit">KB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorMemory < 1073741824) {
                                    $("#private_hypervisor_memory").toNumberSVG((hypervisorMemory / 1048576).toFixed(1), {
                                        unit: '<span class="num_unit">MB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorMemory < 1099511627776) {
                                    $("#private_hypervisor_memory").toNumberSVG((hypervisorMemory / 1073741824).toFixed(1), {
                                        unit: '<span class="num_unit">GB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorMemory < 1125899906842624) {
                                    $("#private_hypervisor_memory").toNumberSVG((hypervisorMemory / 1099511627776).toFixed(1), {
                                        unit: '<span class="num_unit">TB</span>',
                                        fixed: true
                                    });
                                } else {
                                    $("#private_hypervisor_memory").toNumberSVG((hypervisorMemory / 1125899906842624).toFixed(1), {
                                        unit: '<span class="num_unit">PB</span>',
                                        fixed: true
                                    });
                                }

                                if (hypervisorDisk < 1024) {
                                    $("#private_hypervisor_disk").toNumberSVG(hypervisorDisk, {unit: '<span class="num_unit">Byte</span>'});
                                } else if (hypervisorDisk < 1048576) {
                                    $("#private_hypervisor_disk").toNumberSVG((hypervisorDisk / 1024).toFixed(1), {
                                        unit: '<span class="num_unit">KB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorDisk < 1073741824) {
                                    $("#private_hypervisor_disk").toNumberSVG((hypervisorDisk / 1048576).toFixed(1), {
                                        unit: '<span class="num_unit">MB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorDisk < 1099511627776) {
                                    $("#private_hypervisor_disk").toNumberSVG((hypervisorDisk / 1073741824).toFixed(1), {
                                        unit: '<span class="num_unit">GB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorDisk < 1125899906842624) {
                                    $("#private_hypervisor_disk").toNumberSVG((hypervisorDisk / 1099511627776).toFixed(1), {
                                        unit: '<span class="num_unit">TB</span>',
                                        fixed: true
                                    });
                                } else {
                                    $("#private_hypervisor_disk").toNumberSVG((hypervisorDisk / 1125899906842624).toFixed(1), {
                                        unit: '<span class="num_unit">PB</span>',
                                        fixed: true
                                    });
                                }

                                if (hypervisorDiskUsed < 1024) {
                                    $("#private_hypervisor_disk_used").toNumberSVG(hypervisorDiskUsed, {unit: '<span class="num_unit">Byte</span>'});
                                } else if (hypervisorDiskUsed < 1048576) {
                                    $("#private_hypervisor_disk_used").toNumberSVG((hypervisorDiskUsed / 1024).toFixed(1), {
                                        unit: '<span class="num_unit">KB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorDiskUsed < 1073741824) {
                                    $("#private_hypervisor_disk_used").toNumberSVG((hypervisorDiskUsed / 1048576).toFixed(1), {
                                        unit: '<span class="num_unit">MB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorDiskUsed < 1099511627776) {
                                    $("#private_hypervisor_disk_used").toNumberSVG((hypervisorDiskUsed / 1073741824).toFixed(1), {
                                        unit: '<span class="num_unit">GB</span>',
                                        fixed: true
                                    });
                                } else if (hypervisorDiskUsed < 1125899906842624) {
                                    $("#private_hypervisor_disk_used").toNumberSVG((hypervisorDiskUsed / 1099511627776).toFixed(1), {
                                        unit: '<span class="num_unit">TB</span>',
                                        fixed: true
                                    });
                                } else {
                                    $("#private_hypervisor_disk_used").toNumberSVG((hypervisorDiskUsed / 1125899906842624).toFixed(1), {
                                        unit: '<span class="num_unit">PB</span>',
                                        fixed: true
                                    });
                                }
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
                        $("#private_server").empty();
                        $("#private_network").empty();
                        $("#private_resource_list").empty();
                        $("#private_hypervisor").empty();
                        _.each(collection.models, function(model, index, list) {
                            if(model.get('cloudType') == 'private') {
                                var template = _.template('');
                                var serverTemplate = _.template('');
                                var networkTemplate = _.template('');
                                var hypervisorTemplate = _.template('');
                                if(model.get('type') == "openstack") {
                                    template = _.template($("#openstack_logo_template").html());
                                    serverTemplate = _.template($("#openstack_server_template").html());
                                    networkTemplate = _.template($("#openstack_network_template").html());
                                    hypervisorTemplate = _.template($("#openstack_hypervisor_template").html());
                                } else if(model.get('type') == "vmware") {
                                    template = _.template($("#vmware_logo_template").html());
                                    serverTemplate = _.template($("#vmware_server_template").html());
                                    networkTemplate = _.template($("#vmware_network_template").html());
                                    hypervisorTemplate = _.template($("#vmware_hypervisor_template").html());
                                } else if(model.get('type') == "uniq") {
                                    template = _.template($("#uniq_logo_template").html());
                                    serverTemplate = _.template($("#uniq_server_template").html());
                                    networkTemplate = _.template($("#uniq_network_template").html());
                                    hypervisorTemplate = _.template($("#uniq_hypervisor_template").html());
                                }

                                $("#clouds").append(template(model.toJSON()));
                                $("#private_server").append(serverTemplate(model.toJSON()));
                                $("#private_network").append(networkTemplate(model.toJSON()));
                                $("#private_hypervisor").append(hypervisorTemplate(model.toJSON()));
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
