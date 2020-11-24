var ServerUI = (function (options) {

    var
        modules = {},
        ServerModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/servers?id=' + id,
            defaults: {
                name: '',
                state: '',
                host: '',
                addresses: [],
                imageName: '',
                imageId: '',
                cpu: '',
                createdAt: '',
                memory: '',
                disk: '',
                powerState: '',
                flavorName: '',
                flavorId: '',
                id: null,
                projectId: '',
                projectName: '',
                metaData: {},
                keyName: '',
                taskState: ''
            }
        }),
        ServerCollection = Backbone.Collection.extend({
            model: ServerModel
        }),
        VolumeModel = Backbone.Model.extend({
            idAttribute: 'id',
            defaults: {
                name: '',
                description: '',
                state: '',
                size: 0,
                zone: '',
                bootable: false,
                createdAt: '',
                id: null,
                projectId: '',
                projectName: '',
                attachmentInfos: [],
                metaData: {},
                volumeType: ''
            }
        }),
        VolumeCollection = Backbone.Collection.extend({
            model: VolumeModel
        }),
        ServerLogModel = Backbone.Model.extend({
            idAttribute: 'requestId',
            defaults: {
                action: '',
                requestId: null,
                instanceUuid: null,
                message: '',
                projectId: null,
                userId: null,
                startDate: ''
            }
        }),
        ServerLogCollection = Backbone.Collection.extend({
            model: ServerLogModel
        }),
        ServerDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new ServerModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectId\') }}</th>\n            <td>{{= projectId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectName\') }}</th>\n            <td>{{= projectName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.state\') }}</th>\n            <td>{{= stateIconFormatter(state, \'state\', {\'state\':state}) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.host\') }}</th>\n            <td>{{= host }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.vcpus\') }}</th>\n            <td>{{= cpu }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.ram\') }}</th>\n            <td>{{= getSizeToMB(memory) }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.disk\') }}</th>\n            <td>{{= getSizeToGB(disk) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.imageId\') }}</th>\n            <td>{{= imageId }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.imageName\') }}</th>\n            <td>{{= imageName }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.createdAt\') }}</th>\n            <td>{{= createdAt }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.flavorId\') }}</th>\n            <td>{{= flavorId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.flavorName\') }}</th>\n            <td>{{= flavorName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.ipAddress\') }}</th>\n            <td>{{= getIpAddressText(addresses) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.powerState\') }}</th>\n            <td>{{= getOpenStackPowerState(powerState) }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.metaData\') }}</th>\n            <td>{{= objToString(metaData) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.keyName\') }}</th>\n            <td>{{= keyName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.taskState\') }}</th>\n            <td>{{= taskState!=null?taskState:\'None\' }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('name'));

                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    self.render();

                    var model2 = new Backbone.Model({
                    });
                    model2.url = '/private/openstack/servers/' + m.get("id") + '?id=' + id;

                    model2.fetch(model2.attributes);
                });
            }
            // events: {
            //     "click button.btn_action_refresh": "reload"
            // }
        }),
        HistoryModel = Backbone.Model.extend({
            idAttribute: 'id',
            defaults: {
                id : '',
                tenantId : '',
                content : '',
                actionCode	: '',
                result : '',
                createdAt : '',
                updatedAt : '',
                resultDetail : '',
                userId : '',
                targetId : '',
                targetName : ''
            }
        }),
        HistoryCollection = Backbone.Collection.extend({
            model: HistoryModel
        }),
        HistoryView = Backbone.View.extend({
            el: "#tab7",
            events: {
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control": "resetGrid"
            },
            search: function() {
                this.grid.search('actionCode', this.$el.find('.sub_search').val());
            },
            searchEnter: function(e) {
                if(e.keyCode == 13) {
                    this.grid.search('actionCode', this.$el.find('.sub_search').val());
                }
            },
            resetGrid: function() {
                this.$el.find(".sub_search").val('');
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    // postData: {
                    //     filters: '{"groupOp":"AND","rules":[]}'
                    // }
                    postData: { q0: '', q1: ''}
                }).trigger("reloadGrid");
            },
            reset: function() {
                this.collection.reset();
                this.grid.setGridParam({
                    datatype: "local",
                    page: 1,
                    url: ''
                });
                this.grid.clearGridData();
            },
            render: function(model) {
                this.collection.reset();
                this.grid.clearGridData();
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    //url: "/actions/openstack/servers/" + model.get('id') + "/volumes?id=" + id
                    url: "/usage/actions/" + model.get('id')
                }).trigger("reloadGrid");
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("History 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;

                this.collection = new HistoryCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#history-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.group'),
                        'Type',
                        jQuery.i18n.prop('title.jqgrid.user'),
                        jQuery.i18n.prop('title.jqgrid.target'),
                        jQuery.i18n.prop('title.jqgrid.action'),
                        jQuery.i18n.prop('title.jqgrid.result'),
                        jQuery.i18n.prop('title.jqgrid.detail'),
                        jQuery.i18n.prop('title.jqgrid.time'),
                        jQuery.i18n.prop('title.jqgrid.content'),
                        jQuery.i18n.prop('title.jqgrid.updated'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.targetName'),
                        jQuery.i18n.prop('title.jqgrid.userId')
                    ],
                    colModel: [
                        {name: 'groupId', hidden: true, admin: false, align: 'left'},
                        {name: 'type', hidden:true, align: 'left'},
                        {name: 'userName', align: 'left', width:'100px'},
                        {name: 'targetId', hidden: true, align: 'left', formatter: targetFormatter, width:'270px'},
                        {name: 'actionCode', align: 'left', width:'250px'},
                        {name: 'result', align: 'left', formatter: stateIconFormatter, width:'150px'},
                        {name: 'resultDetail', align: 'left', width: '400px'},
                        {name: 'createdAt'},
                        {name: 'content', hidden:true, align: 'left'},
                        {name: 'updatedAt', hidden:true, align: 'left'},
                        {name: 'id', hidden:true, align: 'left'},
                        {name: 'targetName', hidden:true, align: 'left'},
                        {name: 'userId', hidden:true, align: 'left'}
                    ],
                    altRows: true,
                    sortname: "createdAt",
                    sortorder: "desc",
                    // loadonce: true,
                    autowidth: true,
                    width:910,
                    gridComplete: function () {
                        $(this).resetSize();
                    },
                    // multiSort: true,
                    scrollOffset: 0,
                    rowNum: setRowNum(10, self.gridId),
                    loadtext: "",
                    autoencode: true,
                    onSelectRow: function (id) {
                    },
                    loadComplete: function (data) {
                        self.collection.reset(data.rows);
                        data.gridId = self.gridId;
                        // data.getPageParam = function (data) {
                        //     return {
                        //         'q0': 'name',
                        //         'q1': self.$el.find('.sub_search')
                        //     }
                        // };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager6").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');
                    }
                });
            }
        }),
        ServerView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"closeDetail",
                "click .detail_tab a": "detailView",
                "click #server_start": "serverStart",
                "click #server_stop": "serverStop",
                "click #server_reboot": "serverReBoot",
                "click #server_hard_reboot": "serverHardReBoot",
                "click #server_delete": "serverDelete",
                "click #server_pause": "serverPause",
                "click #server_unpause": "serverUnPause",
                "click #server_lock": "serverLock",
                "click #server_unlock": "serverUnLock",
                "click #server_suspend": "serverSuspend",
                "click #server_resume": "serverResume",
                "click #server_rescue": "serverRescue",
                "click #server_unrescue": "serverUnRescue",
                "click #server_snapshot": "serverSnapshot",
                "click #server_create": "create",
                "click #server_interface_attach": "attachInterface",
                "click #server_interface_detach": "detachInterface",
                "click #server_floatingip_connect": "connectFloatingIp",
                "click #server_floatingip_disconnect": "disconnectFloatingIp",
                "click #tab6 .cont_top_search" : "serverMonitoringReload",
                "click #tab6 .detail_monitoring_tit button" : "serverDetailMonitoringDisplay"
            },
            search: function() {
                this.grid.search();
                this.clearDetail();
            },
            searchEnter: function(e) {
                if(e.keyCode == 13) {
                    this.grid.search();
                    this.clearDetail();
                }
            },
            resetGrid: function() {
                this.$el.find(".cont_list .input_search").val('');
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    postData: {
                        filters: '{"groupOp":"AND","rules":[]}'
                    }
                }).trigger("reloadGrid");
                this.clearDetail();
            },
            closeDetail: function() {
                var self = this;
                $('.content').removeClass('detail_on');
                setTimeout(function() {
                    self.grid.resetSize();
                }, options.gridReSizeTime);
            },
            clearDetail: function() {
                modules.detailView.model.reset();
                modules.detailServerVolumeView.reset();
                modules.detailServerLogView.clear();
                modules.detailServerConsoleView.clear();
                modules.detailServerActionView.reset();
                modules.detailHistoryView.reset();
            },
            detailView: function() {
                var m = this.currentSelRow();
                if(m) {
                    var tabIndex = $('.detail_tab a.on').index();
                    if(tabIndex == 1) {
                        modules.detailServerVolumeView.render(m);
                    } else if(tabIndex == 2) {
                        modules.detailServerLogView.render(m);
                    } else if(tabIndex == 3) {
                        modules.detailServerConsoleView.render(m);
                    } else if(tabIndex == 4) {
                        modules.detailServerActionView.render(m);
                    } else if(tabIndex == 5) {
                        MonitoringUI.modules.loadingEfftect("on");
                        MonitoringUI.modules.reload(m);
                    } else if(tabIndex == 6) {
                        modules.detailHistoryView.render(m);
                    }
                } else {
                    var tabIndex = $('.detail_tab a.on').index();
                    if(tabIndex == 1) {
                        modules.detailServerVolumeView.reset();
                    } else if(tabIndex == 2) {
                        modules.detailServerLogView.clear();
                    } else if(tabIndex == 3) {
                        modules.detailServerConsoleView.clear();
                    } else if(tabIndex == 4) {
                        modules.detailServerActionView.reset();
                    } else if(tabIndex == 5) {
                        MonitoringUI.modules.loadingEfftect("on");
                        MonitoringUI.modules.reload();
                    } else if(tabIndex == 6) {
                        modules.detailHistoryView.reset();
                    }
                }
            },
            serverStart: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(!(m.get('state') == "shutoff" || m.get('state') == "stopped")) {
                    alert("중지 상태가 아닙니다.");
                    return;
                }
                if(confirm("서버를 시작 하시겠습니까?")) {
                    var model = new Backbone.Model({
                        action: "START"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverStop: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(m.get('state') != "active") {
                    alert("실행 상태가 아닙니다.");
                    return;
                }
                if(confirm("서버를 중지 하시겠습니까?")) {
                    var model = new Backbone.Model({
                        action: "STOP"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverReBoot: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(m.get('state') != "active") {
                    alert("서버가 실행 상태가 아닙니다.");
                    return;
                }

                if(confirm("서버를 재시작 하시겠습니까?")) {
                    var model = new Backbone.Model({
                        action: "REBOOT_SOFT"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverHardReBoot: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(!(m.get('state') == "active" || m.get('state') == "shutoff" || m.get('state') == "rescued" || m.get('taskState') == "resize verify" || m.get('taskState') == "unset")) {
                    alert("서버 상태를 확인하세요.");
                    return;
                }

                if(confirm("서버를 강제 재시작 하시겠습니까?")) {
                    var model = new Backbone.Model({
                        action: "REBOOT_HARD"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverDelete: function () {
                var self = this;
                if(confirm("서버를 삭제 하시겠습니까?")) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "DELETE"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.remove(model, {merge: true});
                            self.clearDetail();
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverPause: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(!(m.get('state') == "active" || m.get('state') == "shutoff" || m.get('state') == "rescued" || m.get('taskState') == "resize verify" || m.get('taskState') == "unset")) {
                    alert("서버 상태를 확인하세요.");
                    return;
                }

                if(confirm("서버를 일시중지 하시겠습니까?")) {

                    var model = new Backbone.Model({
                        action: "PAUSE"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverUnPause: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(m.get('state') != "paused") {
                    alert("일시중지 상태가 아닙니다.");
                    return;
                }

                if(confirm("서버의 일시중지를 해제 하시겠습니까?")) {
                    var model = new Backbone.Model({
                        action: "UNPAUSE"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverLock: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                var model = new Backbone.Model({
                    action: "LOCK"
                });
                model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                model.save(model.attributes, {
                    success: function (model, response, options) {
                        modules.view.collection.add(model, {merge: true});
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            serverUnLock: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                var model = new Backbone.Model({
                    action: "UNLOCK"
                });
                model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                model.save(model.attributes, {
                    success: function (model, response, options) {
                        modules.view.collection.add(model, {merge: true});
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            serverSuspend: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(!(m.get('state') == "active" || m.get('state') == "shutoff" )) {
                    alert("서버 상태를 확인하세요.");
                    return;
                }

                if(confirm("서버를 일시중단 하시겠습니까?")) {

                    var model = new Backbone.Model({
                        action: "SUSPEND"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverResume: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(m.get('state') != "suspended") {
                    alert("일시중단 상태가 아닙니다.");
                    return;
                }
                if(confirm("서버의 일시중단을 해제 하시겠습니까?")) {
                    var model = new Backbone.Model({
                        action: "RESUME"
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverSnapshot: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(!(m.get('state') == "active" || m.get('state') == "shutoff")) {
                    alert("실행 또는 중지 상태가 아닙니다.");
                    return;
                }

                modules.createServerSnapshotView.open();
            },
            create: function () {
                modules.createView.open();
            },
            attachInterface: function () {
                modules.attachInterfaceView.open();
            },
            detachInterface: function () {
                modules.detachInterfaceView.open();
            },
            connectFloatingIp: function () {
                var m = modules.view.currentSelRow();
                if(!m) return;
                var exist = false;
                _.each(m.get('addresses'), function(ip) {
                    if(ip.type == "floating") {
                        exist = true;
                        return false;
                    }
                });

                if(exist) {
                    alert('Floating IP가 존재합니다.');
                    return;
                }

                modules.connectFloatingIpView.open();
            },
            disconnectFloatingIp: function () {
                var m = modules.view.currentSelRow();
                if (!m) return;
                var floatingIp = '';
                _.each(m.get('addresses'), function (ip) {
                    if (ip.type == "floating") {
                        floatingIp = ip.addr;
                        return false;
                    }
                });

                if (floatingIp == '') {
                    alert('Floating IP가 없습니다.');
                    return;
                }

                if(confirm("Floating IP를 연결 해제 하시겠습니까?")) {
                    var model = new Backbone.Model({
                        action: "DISCONNECT_FLOATING_IP",
                        floatingIp: floatingIp,
                        projectId: m.get('projectId')
                    });
                    model.url = '/private/openstack/servers/' + m.get('id') + '/floatingip?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverMonitoringReload:function(){
                var m = this.currentSelRow();
                if(m) {
                    MonitoringUI.modules.loadingEfftect("on");
                    MonitoringUI.modules.reload(m);
                }
            },
            serverDetailMonitoringDisplay: function(e){
                MonitoringDetailUI.modules.display($(e.currentTarget).parents().eq(1).find(".detail_monitoring_convas").attr("id"));
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("Server 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#server-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/servers?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.projectName'),
                        jQuery.i18n.prop('title.jqgrid.host'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.imageName'),
                        jQuery.i18n.prop('title.jqgrid.imageId'),
                        jQuery.i18n.prop('title.jqgrid.ipAddress'),
                        jQuery.i18n.prop('title.jqgrid.flavorName'),
                        jQuery.i18n.prop('title.jqgrid.flavorId'),
                        jQuery.i18n.prop('title.jqgrid.vcpus'),
                        jQuery.i18n.prop('title.jqgrid.ram'),
                        jQuery.i18n.prop('title.jqgrid.disk'),
                        jQuery.i18n.prop('title.jqgrid.keyName'),
                        jQuery.i18n.prop('title.jqgrid.state'),
                        jQuery.i18n.prop('title.jqgrid.taskState'),
                        jQuery.i18n.prop('title.jqgrid.powerState'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.projectId'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.metaData')
                    ],
                    colModel: [
                        {name: 'projectName'},
                        {name: 'host'},
                        {name: 'name'},
                        {name: 'imageName'},
                        {name: 'imageId', hidden: true},
                        {name: 'addresses', formatter: getIpAddressText, sorttype: function(cell, row) {
                                if(cell.length > 0) {
                                    return cell[0]["addr"];
                                }
                                return cell
                            }},
                        {name: 'flavorName'},
                        {name: 'flavorId', hidden: true},
                        {name: 'cpu', sorttype:'integer'},
                        {name: 'memory', sorttype:'integer', formatter: getSizeToMB},
                        {name: 'disk', sorttype:'integer', formatter: getSizeToGB},
                        {name: 'keyName'},
                        {name: 'state', formatter: stateIconFormatter},
                        {name: 'taskState', formatter: function(cellval, options, row) {
                                if(cellval == null || cellval == "") {
                                    return "None";
                                } else {
                                    return cellval;
                                }
                            }},
                        {name: 'powerState', formatter: getOpenStackPowerState},
                        {name: 'createdAt'},
                        {name: 'projectId', hidden: true},
                        {name: 'id', hidden: true},
                        {name: 'metaData', hidden: true},
                    ],
                    altRows: true,
                    sortname: "createdAt",
                    sortorder: "desc",
                    loadonce: true,
                    autowidth: true,
                    // width:1618,
                    gridComplete: function () {
                        $(this).resetSize();
                    },
                    // multiSort: true,
                    scrollOffset: 0,
                    rowNum: setRowNum(15, self.gridId),
                    loadtext: "",
                    autoencode: true,
                    onSelectRow: function (id) {
                        var m = self.collection.get(id);
                        var tabIndex = $('.detail_tab a.on').index();
                        modules.detailView.model.set(m.toJSON());
                        if(tabIndex == 1) {
                            modules.detailServerVolumeView.render(m);
                        } else if(tabIndex == 2) {
                            modules.detailServerLogView.render(m);
                        } else if(tabIndex == 3) {
                            modules.detailServerConsoleView.render(m);
                        } else if(tabIndex == 4) {
                            modules.detailServerActionView.render(m);
                        } else if(tabIndex == 5) {
                            MonitoringUI.modules.loadingEfftect("on");
                            MonitoringUI.modules.reload(m);
                        } else if(tabIndex == 6) {
                            modules.detailHistoryView.render(m);
                        }

                        $('.content').addClass('detail_on');
                        setTimeout(function() {
                            self.grid.resetSize()
                        }, options.gridReSizeTime);
                    },
                    loadComplete: function (data) {
                        self.collection.reset(data.rows);
                        data.gridId = self.gridId;
                        data.getPageParam = function (data) {
                            return {
                                'q0': $(".select_search option:selected").val(),
                                'q1': $(".input_search").val()
                            }
                        };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager1").pager(data);
                         $("#server-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new ServerCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("change", function (model) {
                    self.grid.setRowData(model.attributes.id, model.toJSON());
                    modules.detailView.model.set(model.toJSON());
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                    modules.detailView.model.reset();
                });

                $("#tab6 .select_wrap ul li").on("click", function(){
                    self.serverMonitoringReload();
                });
            }
        }),
        CreateServerSnapshotView = Backbone.View.extend({
            el: "#popupCreateServerSnapshot",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            init: function () {
                this.$el.find('.name').val('');
            },
            open: function () {
                this.init();
                // this.$el.draggable({handle: '.pop_tit', 'revert': true});
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
                this.$el.find('.name').focus();
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            save: function () {
                var self = this;
                var m = modules.view.currentSelRow();
                if (!m) return;

                if(this.$el.find('.name').val().trim() == "") {
                    alert('이름을 입력하세요.');
                    return;
                }

                var model = new Backbone.Model({
                    name: this.$el.find('.name').val()
                });
                model.url = '/private/openstack/servers/' + m.get('id') + '/snapshot?id=' + id;

                model.save(model.attributes, {
                    success: function (model, response, options) {
                        self.close();
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),
        ConnectFloatingIpView = Backbone.View.extend({
            el: "#popupFloatingIpConnect",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            floatingIpCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            floatingIpTemplate: _.template('<option value="{{=floatingIpAddress!=null?floatingIpAddress:\'\'}}">{{=floatingIpAddress!=null?floatingIpAddress:name}}</option>'),
            fixedIpTemplate: _.template('<option value="{{=addr!=null?addr:\'\'}}">{{=addr!=null?addr:name}}</option>'),
            init: function (m) {
                var self = this;
                this.$el.find('#connect_ip_addr').empty();
                this.$el.find('#connect_ip_addr').append(this.floatingIpTemplate({floatingIpAddress:null, name: "IP 주소를 선택하세요"}));
                this.$el.find('#connect_ip_addr').attr("disabled", "disabled").selectric('refresh');
                this.floatingIpCollection.url = '/private/openstack/floatingips?down=true&projectId='+m.get("projectId")+'&id=' + id;
                this.floatingIpCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#connect_ip_addr').append(self.floatingIpTemplate(model.toJSON()));
                            if (index == list.length - 1) {
                                self.$el.find('#connect_ip_addr').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.$el.find('#connect_port').empty();
                this.$el.find('#connect_port').append(this.fixedIpTemplate({addr:null, name: "Port를 선택하세요"}));
                this.$el.find('#connect_port').attr("disabled", "disabled").selectric('refresh');
                _.each(m.get('addresses'), function(ip, index, list) {
                    if(ip.type != "floating") {
                        self.$el.find('#connect_port').append(self.fixedIpTemplate(ip));
                    }
                    if (index == list.length - 1) {
                        self.$el.find('#connect_port').removeAttr("disabled").selectric('refresh');
                    }
                });
            },
            open: function () {
                var m = modules.view.currentSelRow();
                this.init(m);
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
                this.floatingIpCollection.reset();
            },
            save: function () {
                var self = this;
                var floatingIp = this.$el.find('#connect_ip_addr option:selected').val();
                var interfaceIp = this.$el.find('#connect_port option:selected').val();
                if(!ValidationUtil.hasValue(floatingIp)) {
                    alert("Floating IP를 선택하세요");
                    return;
                }
                if(!ValidationUtil.hasValue(interfaceIp)) {
                    alert("Port를 선택하세요");
                    return;
                }
                var m2 = modules.view.currentSelRow();
                if (!m2) return;

                var model = new Backbone.Model({
                    action: "CONNECT_FLOATING_IP",
                    floatingIp: floatingIp,
                    interfaceIp: interfaceIp,
                    projectId: m2.get('projectId')
                });
                model.url = '/private/openstack/servers/' + m2.get('id') + '/floatingip?id=' + id;

                model.save(model.attributes, {
                    success: function (model, response, options) {
                        modules.view.collection.add(model, {merge: true});
                        self.close();
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),
        AttachInterfaceView = Backbone.View.extend({
            el: "#popupInterfaceAttach",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            networkCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/networks?id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            networkTemplate: _.template('<option value="{{=id}}">{{=name}} {{=getNeutronSubnetsCidrText(neutronSubnets)}}</option>'),
            init: function (m) {
                var self = this;
                this.$el.find('select').empty();
                this.$el.find('select').append(this.networkTemplate({id:"", name: "네트워크를 선택하세요", neutronSubnets:[]}));
                this.$el.find('select').attr("disabled", "disabled").selectric('refresh');
                this.networkCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            if(model.get("projectId") == m.get("projectId") || model.get("shared") == true) {
                                self.$el.find('select').append(self.networkTemplate(model.toJSON()));
                            }
                            if (index == list.length - 1) {
                                self.$el.find('select').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            open: function () {
                var m = modules.view.currentSelRow();
                if(!m) return;
                this.init(m);
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
                this.networkCollection.reset();
            },
            save: function () {
                var self = this;
                var networkId = this.$el.find('select option:selected').val();
                if(!ValidationUtil.hasValue(networkId)) {
                    alert("네트워크를 선택하세요");
                    return;
                }
                var m2 = modules.view.currentSelRow();
                if (!m2) return;

                var model = new Backbone.Model({
                    action: "ATTACH_INTERFACE",
                    networkId: networkId,
                    projectId: m2.get('projectId')
                });
                model.url = '/private/openstack/servers/' + m2.get('id') + '/interface?id=' + id;

                model.save(model.attributes, {
                    success: function (model, response, options) {
                        modules.view.collection.add(model, {merge: true});
                        self.close();
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),
        DetachInterfaceView = Backbone.View.extend({
            el: "#popupInterfaceDetach",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            interfaceCollection: new (Backbone.Collection.extend({
            })),
            interfaceTemplate: _.template('<option value="{{=port_id}}">{{=fixed_ips==null?name:fixed_ips.length>0?fixed_ips[0].ip_address:\'\'}}</option>'),
            init: function (m) {
                var self = this;
                this.$el.find('select').empty();
                this.$el.find('select').append(this.interfaceTemplate({port_id:"", name:"네트워크를 선택하세요", fixed_ips:null}));
                this.$el.find('select').attr("disabled", "disabled").selectric('refresh');
                this.interfaceCollection.url = '/private/openstack/servers/'+m.get('id')+'/interface?id=' + id;
                this.interfaceCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('select').append(self.interfaceTemplate(model.toJSON()));
                            if (index == list.length - 1) {
                                self.$el.find('select').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            open: function () {
                var m = modules.view.currentSelRow();
                if(!m) return;
                this.init(m);
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
                this.interfaceCollection.reset();
            },
            save: function () {
                var self = this;
                var portId = this.$el.find('select option:selected').val();
                if(!ValidationUtil.hasValue(portId)) {
                    alert("포트를 선택하세요");
                    return;
                }
                var m2 = modules.view.currentSelRow();
                if (!m2) return;

                var model = new Backbone.Model({
                    action: "DETACH_INTERFACE",
                    portId: portId,
                    projectId: m2.get('projectId')
                });
                model.url = '/private/openstack/servers/' + m2.get('id') + '/interface?id=' + id;

                model.save(model.attributes, {
                    success: function (model, response, options) {
                        modules.view.collection.add(model, {merge: true});
                        self.close();
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),
        CreateView = Backbone.View.extend({
            el: "#popupCreate",
            zoneCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/zones?type=compute&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            zoneTemplate: _.template('<option value="{{=zoneName}}">{{=zoneName}}</option>'),
            imageCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/images?active=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            imageTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= updatedAt }}</td>\n    <td>{{= byteSizeFormatter(size) }}</td>\n    <td>{{= type }}</td>\n    <td>{{= visibility }}</td>\n</tr>'),
            volumeCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/volumes?bootable=true&available=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            volumeTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= description }}</td>\n    <td>{{= size }}GiB</td>\n    <td>{{= volumeType }}</td>\n    <td>{{= zone }}</td>\n</tr>'),
            flavorCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/flavors?id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            flavorTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= vcpus }}</td>\n    <td>{{= getSizeToMB(ram) }}</td>\n    <td>{{= getSizeToGB(disk+ephemeral) }}</td>\n    <td>{{= getSizeToGB(disk) }}</td>\n    <td>{{= getSizeToGB(ephemeral) }}</td>\n    <td>{{= isPublic }}</td>\n</tr>'),
            networkCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/networks?project=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            networkTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= getNeutronSubnetsText(neutronSubnets) }}</td>\n    <td>{{= shared }}</td>\n    <td>{{= adminStateUp }}</td>\n    <td>{{= state }}</td>\n</tr>'),
            securityGroupCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/securitygroups?project=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            securityGroupTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= description }}</td>\n</tr>'),
            keyPairCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/keypairs?id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            keyPairTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= name }}" value="{{= name }}"><label for="{{= name }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= fingerprint }}</td>\n</tr>'),
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "click .btn_next": "next",
                "change #pop_tab2 select": "sourceSelect",
                "click #newVolume": "newVolumeClick",
                "click td": "tdClick",
                "click #pop_tab2 tbody input:checkbox": "oneClick",
                "click #pop_tab3 tbody input:checkbox": "oneClick",
                "click #pop_tab4 tbody input:checkbox": "multiClick",
                "click #pop_tab5 tbody input:checkbox": "multiClick",
                "click #pop_tab6 tbody input:checkbox": "oneClick",
                "click thead input:checkbox": "allCheck",
                "change #pop_tab7 tbody input:file": "displayFile",
                "keyup #pop_tab7 tbody textarea": "checkSize"
            },
            init: function () {
                var self = this;
                this.$el.find('.name').val('');
                this.$el.find('.pop_tab a:eq(0)').click();
                this.$el.find('#pop_tab1 select').empty();
                this.$el.find('#pop_tab1 select').attr("disabled", "disabled").selectric('refresh');
                this.$el.find('#pop_tab2 select option:eq(0)').prop("selected", true);
                this.$el.find('#pop_tab2 select').selectric('refresh');

                this.$el.find('#deleteOnTermination').prop('checked', false);
                this.$el.find('#newVolume').prop('checked', false);
                this.$el.find('#volumeSize').val('1');
                this.$el.find('#newVolumeTr').css('display', '');
                this.$el.find('#deleteOnTerminationTr').css('display', 'none');
                this.$el.find('#volumeSizeTr').css('display', 'none');
                this.$el.find('#pop_tab2 table:eq(1)').show();
                this.$el.find('#pop_tab2 table:eq(1) tbody').empty();
                this.$el.find('#pop_tab2 table:eq(2)').hide();
                this.$el.find('#pop_tab2 table:eq(2) tbody').empty();
                this.$el.find('#pop_tab3 table tbody').empty();
                this.$el.find('#pop_tab4 table tbody').empty();
                this.$el.find('#pop_tab5 table tbody').empty();
                this.$el.find('#pop_tab6 table tbody').empty();
                this.$el.find("#pop_tab7 tbody textarea").val('');
                this.$el.find('#pop_tab7 input:checkbox').prop('checked', false);
                this.zoneCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab1 select').append(self.zoneTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#pop_tab1 select').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.imageCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab2 table:eq(1) tbody').append(self.imageTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.volumeCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab2 table:eq(2) tbody').append(self.volumeTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.flavorCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab3 table tbody').append(self.flavorTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.networkCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab4 table tbody').append(self.networkTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.securityGroupCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab5 table tbody').append(self.securityGroupTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.keyPairCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab6 table tbody').append(self.keyPairTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });

                // this.$el.find('select option:selected').val()
            },
            open: function () {
                this.init();
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            next: function () {
                var currentIndex = this.$el.find('.pop_tab a.on').index();
                var tabLength = this.$el.find('.pop_tab a').length;
                if(currentIndex != tabLength - 1) {
                    $('.pop_tab a:eq(' + (currentIndex + 1) + ')').click();
                } else {
                    alert('마지막 페이지 입니다.');
                }
            },
            tdClick: function (e) {
                if(e.target.tagName === 'TD') this.$el.find(e.currentTarget).parent().children('td:eq(0)').children('input:checkbox').click();
            },
            sourceSelect: function () {
                var source = this.$el.find('#pop_tab2 select option:selected').val();
                if(source == 'image') {
                    this.$el.find('#pop_tab2 table:eq(1)').show();
                    this.$el.find('#pop_tab2 table:eq(2)').hide();
                    this.$el.find('#newVolumeTr').css('display', '');
                    this.$el.find('#deleteOnTerminationTr').css('display', 'none');
                    this.$el.find('#volumeSizeTr').css('display', 'none');
                } else {
                    this.$el.find('#pop_tab2 table:eq(2)').show();
                    this.$el.find('#pop_tab2 table:eq(1)').hide();
                    this.$el.find('#newVolumeTr').css('display', 'none');
                    this.$el.find('#deleteOnTerminationTr').css('display', '');
                    this.$el.find('#volumeSizeTr').css('display', 'none');
                }
                $("#newVolume").prop('checked', false);
                $("#deleteOnTermination").prop('checked', false);
                $("#volumeSize").val('1');
            },
            newVolumeClick: function (e) {
                var checked = $("#newVolume").is(':checked');
                if(checked) {
                    $("#newVolume").prop('checked', true);
                    this.$el.find('#newVolumeTr').css('display', '');
                    this.$el.find('#deleteOnTerminationTr').css('display', '');
                    this.$el.find('#volumeSizeTr').css('display', '');
                } else {
                    this.$el.find('#newVolumeTr').css('display', '');
                    this.$el.find('#deleteOnTerminationTr').css('display', 'none');
                    this.$el.find('#volumeSizeTr').css('display', 'none');
                }
            },
            oneClick: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    this.$el.find(e.currentTarget).parents('.tb_wrap').find('input:checkbox').prop('checked', false);
                    $(e.currentTarget).prop('checked', true);
                }
            },
            multiClick: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(!checked) {
                    this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', false);
                } else {
                    var checkboxLength =  this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox').length;
                    var checkedLength =  this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:checked').length;

                    if(checkboxLength == checkedLength) {
                        this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', true);
                    } else {
                        this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', false);
                    }
                }
            },
            allCheck: function(e) {
                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:not(:checked)').click();
                } else {
                    this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:checked').click();
                }
            },
            displayFile: function(e) {
                var self = this;
                var fileList = this.$el.find("#pop_tab7 tbody input:file")[0].files ;

                // 읽기
                var reader = new FileReader();
                reader.readAsText(fileList [0]);

                //로드 한 후
                reader.onload = function  () {
                    self.$el.find("#pop_tab7 tbody textarea").val(reader.result);
                    self.$el.find("#pop_tab7 tbody input:file").val('');
                    self.checkSize();
                };
            },
            checkSize: function(e) {
                var textFileAsBlob = new Blob([ this.$el.find("#pop_tab7 tbody textarea").val() ], { type: 'text/plain' });
                var size = textFileAsBlob.size;

                if(size > 16384) {
                    this.$el.find("#pop_tab7 tbody td span").css("color", "#ff0000");
                } else {
                    this.$el.find("#pop_tab7 tbody td span").css("color", "#667285");
                }

                this.$el.find("#pop_tab7 tbody span").html("Content size: "+ this.getSizeToFixed(size) +" bytes of 16.00 KB");
            },
            getSizeToFixed: function(size) {
                if(size < 1024) {
                    return size + ' bytes';
                } else if(size < 1048576) {
                    return (size/1024).toFixed(2) + ' KB';
                } else if(size < 1073741824) {
                    return (size/1048576).toFixed(2) + ' MB';
                } else if(size < 1099511627776) {
                    return (size/1073741824).toFixed(2) + ' GB';
                } else if(size < 1125899906842624) {
                    return (size/1099511627776).toFixed(2) + ' TB';
                } else if(size < 4503599627370496) {
                    return (size/1125899906842624).toFixed(2) + ' PB';
                }
            },
            save: function () {
                var self = this;
                var networks = [];
                var securityGroups = [];

                var networkSelects = this.$el.find('#pop_tab4 tbody input:checkbox:checked');
                var securityGroupSelects = this.$el.find('#pop_tab5 tbody input:checkbox:checked');
                if(networkSelects.length > 0) {
                    _.each(networkSelects, function(input) {
                        networks.push($(input).val());
                    });
                }
                if(securityGroupSelects.length > 0) {
                    _.each(securityGroupSelects, function(input) {
                        securityGroups.push($(input).val());
                    });
                }

                var createDate = {
                    name: this.$el.find('.name').val(),
                    zone: this.$el.find('#pop_tab1 select option:selected').val(),
                    sourceType: this.$el.find('#pop_tab2 select option:selected').val(),
                    sourceId: this.$el.find('#pop_tab2 .tb_wrap tbody input:checkbox:checked').val(),
                    deleteOnTermination: this.$el.find('#deleteOnTermination').is(":checked"),
                    newVolume: this.$el.find('#newVolume').is(":checked"),
                    size: parseInt(this.$el.find('#volumeSize').val()),
                    flavorId: this.$el.find('#pop_tab3 tbody input:checkbox:checked').val(),
                    networks: networks,
                    securityGroups: securityGroups,
                    keyPair: this.$el.find('#pop_tab6 tbody input:checkbox:checked').val(),
                    configDrive: this.$el.find('#pop_tab7 input:checkbox').is(':checked')
                };

                if(!ValidationUtil.hasValue(createDate.name)) {
                    alert('이름을 입력하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.sourceId)) {
                    alert('Source 를 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.flavorId)) {
                    alert('Flavor 를 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.networks)) {
                    alert('Network 를 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.securityGroups)) {
                    alert('Security Group 을 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.keyPair)) {
                    alert('Key Pair 를 선택하세요.');
                    return;
                }
                if(ValidationUtil.hasValue(this.$el.find("#pop_tab7 tbody textarea").val())) {
                    var textFileAsBlob = new Blob([ this.$el.find("#pop_tab7 tbody textarea").val() ], { type: 'text/plain' });
                    var size = textFileAsBlob.size;

                    if(size > 16384) {
                        alert('사용자 정의 스트립트의 용량이 너무 큽니다.');
                        return;
                    } else {
                        createDate.script = this.$el.find("#pop_tab7 tbody textarea").val();
                    }
                }

                var m = new (Backbone.Model.extend({
                    urlRoot: '/private/openstack/servers?id=' + id
                }));
                m.save(createDate, {
                    success: function(model, response, options) {
                        modules.view.collection.add(model, {merge: true});
                        self.close();
                    },
                    error :function (model, response, options) {
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),
        ServerVolumeView = Backbone.View.extend({
            el: "#tab2",
            events: {
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control": "resetGrid",
                "click #server_volume_attach": "attach",
                "click #server_volume_detach": "detach"
            },
            search: function() {
                this.grid.search('name', this.$el.find('.sub_search').val());
            },
            searchEnter: function(e) {
                if(e.keyCode == 13) {
                    this.grid.search('name', this.$el.find('.sub_search').val());
                }
            },
            resetGrid: function() {
                this.$el.find(".sub_search").val('');
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    postData: {
                        filters: '{"groupOp":"AND","rules":[]}'
                    }
                }).trigger("reloadGrid");
            },
            reset: function() {
                this.collection.reset();
                this.grid.setGridParam({
                    datatype: "local",
                    page: 1,
                    url: ''
                });
                this.grid.clearGridData();
            },
            render: function(model) {
                this.collection.reset();
                this.grid.clearGridData();
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    url: "/private/openstack/servers/" + model.get('id') + "/volumes?id=" + id
                }).trigger("reloadGrid");
            },
            attach: function() {
                modules.attachVolumeView.open();
            },
            detach: function() {
                var m = this.currentSelRow();
                if(m) {
                    if(m.get('bootable') == true) {
                        alert('Root 볼륨은 해제할 수 없습니다.');
                        return;
                    }
                    if(confirm("볼륨을 해제 하시겠습니까?")) {
                        var m2 = modules.view.currentSelRow();
                        if (!m2) return;

                        var model = new Backbone.Model({
                            action: "DETACH_VOLUME"
                        });
                        model.url = '/private/openstack/servers/' + m2.get('id') + '/volumes/'+m.get('id')+'?id=' + id;

                        model.save(model.attributes, {
                            success: function (model, response, options) {
                                modules.detailServerVolumeView.collection.remove(model, {merge: true});
                            },
                            error: function (model, response, options) {
                                ValidationUtil.getServerError(response);
                            }
                        });
                    }
                }
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("Volume 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;

                this.collection = new VolumeCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#volume-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.description'),
                        jQuery.i18n.prop('title.jqgrid.size'),
                        jQuery.i18n.prop('title.jqgrid.state'),
                        jQuery.i18n.prop('title.jqgrid.attachedTo'),
                        jQuery.i18n.prop('title.jqgrid.zone'),
                        jQuery.i18n.prop('title.jqgrid.bootable'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.projectId'),
                        jQuery.i18n.prop('title.jqgrid.projectName'),
                        jQuery.i18n.prop('title.jqgrid.metaData'),
                        jQuery.i18n.prop('title.jqgrid.volumeType')
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'description'},
                        {name: 'size', sorttype:'integer', formatter:function (cellVal, options, row) {
                                return cellVal + "GiB";
                            }},
                        {name: 'state'},
                        {name: 'attachmentInfos', formatter: getAttachmentsText, sorttype: function (cell, row) {
                                if(cell.length > 0) {
                                    return cell[0]["serverName"];
                                }
                                return cell;
                            }},
                        {name: 'zone'},
                        {name: 'bootable'},
                        {name: 'id', hidden: true},
                        {name: 'createdAt', hidden: true},
                        {name: 'projectId', hidden: true},
                        {name: 'projectName', hidden: true},
                        {name: 'metaData', hidden: true},
                        {name: 'volumeType', hidden: true}
                    ],
                    altRows: true,
                    sortname: "name",
                    sortorder: "asc",
                    loadonce: true,
                    autowidth: true,
                    width:910,
                    gridComplete: function () {
                        $(this).resetSize();
                    },
                    // multiSort: true,
                    scrollOffset: 0,
                    rowNum: setRowNum(10, self.gridId),
                    loadtext: "",
                    autoencode: true,
                    onSelectRow: function (id) {
                    },
                    loadComplete: function (data) {
                        self.collection.reset(data.rows);
                        data.gridId = self.gridId;
                        data.getPageParam = function (data) {
                            return {
                                'q0': 'name',
                                'q1': self.$el.find('.sub_search')
                            }
                        };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager2").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');

                    }
                });
            }
        }),
        AttachVolumeView = Backbone.View.extend({
            el: "#popupVolumeAttach",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            volumeCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/volumes?available=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            volumeTemplate: _.template('<option value="{{=id}}">{{=name}} {{=id==\'\'?\'\':\'(\'+id+\')\'}}</option>'),
            init: function (m) {
                var self = this;
                this.$el.find('select').empty();
                this.$el.find('select').append(this.volumeTemplate({id:"", name:"볼륨을 선택하세요"}));
                this.$el.find('select').attr("disabled", "disabled").selectric('refresh');
                this.volumeCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            if(model.get("projectId") == m.get("projectId")) {
                                self.$el.find('select').append(self.volumeTemplate(model.toJSON()));
                            }
                            if (index == list.length - 1) {
                                self.$el.find('select').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            open: function () {
                var m = modules.view.currentSelRow();
                if(!m) return;
                this.init(m);
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
                this.volumeCollection.reset();
            },
            save: function () {
                var self = this;
                var volumeId = this.$el.find('select option:selected').val();
                if(!ValidationUtil.hasValue(volumeId)) {
                    alert("볼륨을 선택하세요");
                    return;
                }
                var m2 = modules.view.currentSelRow();
                if (!m2) return;

                var model = new Backbone.Model({
                    action: "ATTACH_VOLUME"
                });
                model.url = '/private/openstack/servers/' + m2.get('id') + '/volumes/'+volumeId+'?id=' + id;

                model.save(model.attributes, {
                    success: function (model, response, options) {
                        modules.detailServerVolumeView.collection.add(model, {merge: true});
                        self.close();
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });

            }
        }),
        ServerLogView =  Backbone.View.extend({
            el: "#tab3",
            line: 35,
            model: new Backbone.Model(),
            events: {
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter"
            },
            search: function() {
                this.line = this.$el.find('.sub_search').val();
                this.render();
            },
            searchEnter: function(e) {
                if(e.keyCode == 13) {
                    this.line = this.$el.find('.sub_search').val();
                    this.render();
                }
            },
            render: function(m) {
                if(m) {
                    this.model.set({
                        id: m.get('id')
                    });
                } else {
                    var m2 = modules.view.currentSelRow();
                    if(!m2) return;
                    this.model.set({
                        id: m2.get('id')
                    });
                }
                this.clear();
                var self = this;
                this.model.url = '/private/openstack/servers/'+this.model.get('id')+'/log?id=' + id + '&line='+this.line;
                this.model.fetch({
                    success: function(model, response, options) {
                        self.$el.find('pre').text(model.get('log'));
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            clear: function() {
                this.$el.find('pre').text('');
            },
            initialize: function () {

            }
        }),
        ServerConsoleView =  Backbone.View.extend({
            el: "#tab4",
            model: new Backbone.Model(),
            events: {
                "click #server_console_only": "newWindows"
            },
            newWindows: function () {
                var m = modules.view.currentSelRow();
                if(!m) return;
                if(this.model.get('url')) {
                    window.open(this.model.get('url'));
                } else {
                    this.model.url = '/private/openstack/servers/' + m.get('id') + '/console?id=' + id;
                    this.model.fetch({
                        success: function (model, response, options) {
                            window.open(model.get('url'));
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            render: function(m) {
                if(m) {
                    this.model.set({
                        id: m.get('id')
                    });
                }
                this.clear();
                var self = this;
                this.model.url = '/private/openstack/servers/'+this.model.get('id')+'/console?id=' + id;
                this.model.fetch({
                    success: function(model, response, options) {
                        self.$el.find('.tb_wrap').append($("<iframe>").attr('src', model.get('url')).attr('height', '531px').attr('width', '910px'));
                    },
                    error: function (model, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            clear: function() {
                this.$el.find('.tb_wrap').empty();
            },
            initialize: function () {

            }
        }),
        ServerActionView = Backbone.View.extend({
            el: "#tab5",
            events: {
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control":"resetGrid"
            },
            search: function() {
                this.grid.search('action', this.$el.find('.sub_search').val());
            },
            searchEnter: function(e) {
                if(e.keyCode == 13) {
                    this.grid.search('action', this.$el.find('.sub_search').val());
                }
            },
            resetGrid: function() {
                this.$el.find(".sub_search").val('');
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    postData: {
                        filters: '{"groupOp":"AND","rules":[]}'
                    }
                }).trigger("reloadGrid");
            },
            reset: function() {
                this.collection.reset();
                this.grid.setGridParam({
                    datatype: "local",
                    page: 1,
                    url: ''
                });
                this.grid.clearGridData();
            },
            render: function(model) {
                this.collection.reset();
                this.grid.clearGridData();
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    url: "/private/openstack/servers/" + model.get('id') + "/action?id=" + id
                }).trigger("reloadGrid");
            },
            initialize: function () {
                var self = this;

                this.collection = new ServerLogCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#action-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "requestId"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.requestId'),
                        jQuery.i18n.prop('title.jqgrid.action'),
                        jQuery.i18n.prop('title.jqgrid.startDate'),
                        jQuery.i18n.prop('title.jqgrid.instanceUuid'),
                        jQuery.i18n.prop('title.jqgrid.message'),
                        jQuery.i18n.prop('title.jqgrid.projectId'),
                        jQuery.i18n.prop('title.jqgrid.userId')
                    ],
                    colModel: [
                        {name: 'requestId'},
                        {name: 'action'},
                        {name: 'startDate'},
                        {name: 'instanceUuid', hidden:true},
                        {name: 'message', hidden:true},
                        {name: 'projectId', hidden:true},
                        {name: 'userId', hidden:true}
                    ],
                    altRows: true,
                    sortname: "startDate",
                    sortorder: "desc",
                    loadonce: true,
                    autowidth: true,
                    width:910,
                    gridComplete: function () {
                        $(this).resetSize();
                    },
                    // multiSort: true,
                    scrollOffset: 0,
                    rowNum: setRowNum(10, self.gridId),
                    loadtext: "",
                    autoencode: true,
                    onSelectRow: function (id) {
                    },
                    loadComplete: function (data) {
                        self.collection.reset(data.rows);
                        data.gridId = self.gridId;
                        data.getPageParam = function (data) {
                            return {
                                'q0': 'action',
                                'q1': self.$el.find('.sub_search')
                            }
                        };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager5").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');

                    }
                });
            }
        }),
        init = function (isAdmin) {
            modules.view = new ServerView();
            modules.createView = new CreateView();
            modules.createServerSnapshotView = new CreateServerSnapshotView();
            modules.connectFloatingIpView = new ConnectFloatingIpView();
            modules.attachInterfaceView = new AttachInterfaceView();
            modules.detachInterfaceView = new DetachInterfaceView();
            modules.detailView = new ServerDetailView();
            modules.detailServerVolumeView = new ServerVolumeView();
            modules.attachVolumeView = new AttachVolumeView();
            modules.detailServerLogView = new ServerLogView();
            modules.detailServerConsoleView = new ServerConsoleView();
            modules.detailServerActionView = new ServerActionView();
            modules.detailHistoryView = new HistoryView();

            stompUtil.addListener('/topic/openstack/' + options.userId, function (msg) {
                var payload = null;
                try {
                    payload = JSON.parse(msg.body).payload;
                } catch (e) {
                    console.log(e);
                    return;
                }
                console.log("Payload (/topic/openstack) :", payload);
                switch (payload.action) {
                    case "ATTACH_VOLUME":
                    case "DETACH_VOLUME":
                        if (payload.id) {
                            var m = modules.detailServerVolumeView.collection.get(payload.object.id);
                            if (m) {
                                if(payload.object.state == "available") {
                                    modules.detailServerVolumeView.collection.remove(m, {merge: true});
                                } else {
                                    m.set(payload.object);
                                }
                            }
                        } else {
                            stompUtil.getError(payload);
                        }
                        break;
                    default:
                        if (payload.id) {
                            var m = modules.view.collection.get(payload.object.id);
                            if (m) {
                                m.set(payload.object);
                            }
                        } else {
                            stompUtil.getError(payload);
                        }
                        break;
                }
            });

        };

    return {
        init: init,
        modules: modules
    };
})(config);

stompUtil.connect();