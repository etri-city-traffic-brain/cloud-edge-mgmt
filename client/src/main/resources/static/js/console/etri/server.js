var ServerUI = (function (options) {

    var
        modules = {},
        ServerModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/edge/etri/servers?id=' + id,
            defaults: {
                name: '',
                state: '',
                imageId: '',
                flavorId: '',
                cpu: '',
                privateIp: '',
                publicIp: '',
                keyName: '',
                availabilityZone: '',
                monitoring: '',
                privateDns: '',
                publicDns: '',
                securityGroups: '',
                createdAt: '',
                id: null,
            }
        }),
        ServerCollection = Backbone.Collection.extend({
            model: ServerModel
        }),

        ServerView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"clearDetail",
                "click .detail_tab a": "detailView",
                "click #server_start": "serverStart",
                "click #server_stop": "serverStop",
                "click #server_reboot": "serverReBoot",
                "click #server_delete": "serverDelete",
                "click #server_monitoring_enabled": "serverMonitoringEnabled",
                "click #server_monitoring_disabled": "serverMonitoringDisabled",
                "click #tab2 .cont_top_search" : "serverMonitoringReload",
                "click #tab2 .detail_monitoring_tit button" : "serverDetailMonitoringDisplay",
                "click #server_create": "create"
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
                modules.detailHistoryView.reset();
                this.closeDetail();
            },
            detailView: function() {
                var m = this.currentSelRow();
                if(m) {
                    var tabIndex = $('.detail_tab a.on').index();
                    switch (tabIndex) {
                        case 0 :
                            modules.detailView.model.set(m.toJSON());
                            break;
                        case 1 :
                            MonitoringUI.modules.loadingEfftect("on");
                            MonitoringUI.modules.reload(m);
                            break;
                        case 2 :
                            modules.detailHistoryView.render(m);
                            break;
                    }

                    /*if(tabIndex == 1) {
                        modules.detailServerVolumeView.render(m);
                    } else if(tabIndex == 2) {
                        modules.detailServerLogView.render(m);
                    } else if(tabIndex == 3) {
                        modules.detailServerConsoleView.render(m);
                    } else if(tabIndex == 4) {
                        modules.detailServerActionView.render(m);
                    }*/
                } else {
                    var tabIndex = $('.detail_tab a.on').index();
                    if(tabIndex == 1) {
                        MonitoringUI.modules.loadingEfftect("on");
                        MonitoringUI.modules.reload();
                    } else if(tabIndex == 2) {
                        modules.detailHistoryView.reset();
                    }
                }
            },
            serverStart: function () {
                // if(confirm("서버를 시작 하시겠습니까?")) {
                if (confirm(i18n('s.t.would-like-to', 'w.start'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    if (m.get('state') == "terminated") {
                        // alert("서버 상태가 Terminated 는 시작 할 수 없습니다.");
                        alert(i18n('s.t.can-not','Terminated','w.start'));
                        return;
                    }

                    var model = new Backbone.Model({
                        action: "START"
                    });
                    model.url = '/edge/etri/servers/' + m.get('id') + '/action?id=' + id;

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
                // if(confirm("서버를 중지 하시겠습니까?")) {
                if (confirm(i18n('s.t.would-like-to', 'w.stop'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    if (m.get('state') == "terminated") {
                        // alert("서버 상태가 Terminated 는 중지 할 수 없습니다.");
                        alert(i18n('s.t.can-not','Terminated','w.stop'));
                        return;
                    }

                    var model = new Backbone.Model({
                        action: "STOP"
                    });
                    model.url = '/edge/etri/servers/' + m.get('id') + '/action?id=' + id;

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
                // if(confirm("서버를 재시작 하시겠습니까?")) {
                if (confirm(i18n('s.t.would-like-to', 'w.restart'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    if (m.get('state') != "running") {
                        // alert("재시작은 서버 상태가 Running 이어야 합니다.");
                        alert(i18n('s.t.do-it-when','w.restart','Running'));
                        return;
                    }
                    var model = new Backbone.Model({
                        action: "REBOOT"
                    });
                    model.url = '/edge/etri/servers/' + m.get('id') + '/action?id=' + id;

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
                // if(confirm("서버를 삭제 하시겠습니까?")) {
                if (confirm(i18n('s.t.would-like-to', 'w.delete'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    if (m.get('state') == "terminated") {
                        // alert("서버 상태가 Terminated 는 삭제 할 수 없습니다.");
                        alert(i18n('s.t.can-not','Terminated','w.delete'));
                        return;
                    }

                    var model = new Backbone.Model({
                        action: "DELETE"
                    });
                    model.url = '/edge/etri/servers/' + m.get('id') + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            // modules.view.collection.add(model, {merge: true});
                            modules.view.collection.findWhere({id:model.get('id')}).set({state:'shutting-down'});
                            self.clearDetail();
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverMonitoringEnabled: function () {
                var self = this;
                // if(confirm("세부 모니터링을 활성화 하시겠습니까?")) {
                if (confirm(i18n('s.t.subject-would-like-to', 'w.detail-monitoring', 'w.enable'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "MONITORING"
                    });
                    model.url = '/edge/etri/servers/' + m.get('id') + '/action?id=' + id;

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
            serverMonitoringDisabled: function () {
                var self = this;
                // if(confirm("세부 모니터링을 비활성화 하시겠습니까?")) {
                if (confirm(i18n('s.t.subject-would-like-to', 'w.detail-monitoring', 'w.disable'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "UNMONITORING"
                    });
                    model.url = '/edge/etri/servers/' + m.get('id') + '/action?id=' + id;

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
            create: function () {
                modules.createView.open();
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
                    // alert("Server 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.server'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#server-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    // url: '/edge/etri/servers?id=' + id,
                    data: [{host:'101.79.1.1.104/27',type:'CentOS 7.7',ip:'100.100.100.14/24',cpu:'80',memory:'128GB',disk:'3.6TB',state:'active',approTime:'2020-11-17 14:00:00'}],
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        '호스트',
                        'OS type',
                        'IP 주소',
                        'CPU',
                        '메모리',
                        '디스크',
                        '상태',
                        '등록시간'
                    ],
                    colModel: [
                        {name: 'host'},
                        {name: 'type'},
                        {name: 'ip'},
                        {name: 'cpu'},
                        {name: 'memory'},
                        {name: 'disk'},
                        {name: 'state',formatter: stateIconFormatter},
                        {name: 'approTime'}
                    ],
                    altRows: true,
                    sortname: "createdAt",
                    sortorder: "desc",
                    loadonce: true,

                    autowidth: true,
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

                $("#tab2 .select_wrap ul li").on("click", function(){
                    self.serverMonitoringReload();
                });
            }
        }),
        init = function (isAdmin) {
            $("#test1").text("ETRI_EDGE")
            modules.view = new ServerView();
        };

    return {
        init: init,
        modules: modules
    };
})(config);

stompUtil.connect();