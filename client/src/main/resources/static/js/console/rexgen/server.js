// var stateFormatter = function(cellVal, options, row) {
//     if(options == null || options == "") {
//         if(row.powerState != null && row.provisioningState != null){
//             return row.powerState + " ("+row.provisioningState+")";
//         } else if (row.powerState == null && row.provisioningState != null){
//             return row.provisioningState;
//         } else if (row.powerState != null && row.provisioningState == null){
//             return row.powerState;
//         } else {
//             return "-";
//         }
//     } else {
//         var state = "";
//         if(row.powerState != null) {
//             state = row.powerState;
//         } else if (row.provisioningState != null) {
//             state = row.provisioningState;
//         } else {
//             state = "";
//         }
//         return azureStateIconFormatter(state, options, row);
//     }
// }

var ServerUI = (function (options) {

    var
        ischeck=false,
        modules = {},
        ServerModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/edge/rexgen/servers?id=' + id,
            defaults: {
                name: '',
                powerState: '',
                provisioningState:'',
                resourceGroupName: '',
                location: '',
                type: '',
                size: '',
                osType: '',
                primaryPrivateIP: '',
                primaryPublicIPAddress: '',
                subscriptionId: '',
                subscriptionDisplayName: '',
                tags: '',
                createId: null, // 최초 서버 생성 시 상태 값 체크를 위한 createId (서버 생성 시에만 사용)
                id: null,       // serverId
            }
        }),
        ServerCollection = Backbone.Collection.extend({
            url: '/edge/rexgen/servers?id=' + id,
            model: ServerModel
        }),


        ServerView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"closeDetail",
                "click .detail_tab a": "detailView",
                "click #server_create": "serverCreate",
                "click #server_start": "serverStart",
                "click #server_stop": "serverStop",
                "click #server_reboot": "serverReBoot",
                "click #server_delete": "serverDelete",
                "click #server_monitoring_enabled": "serverMonitoringEnabled",
                "click #server_monitoring_disabled": "serverMonitoringDisabled",
                "click #tab2 .cont_top_search" : "serverMonitoringReload",
                "click #tab2 .detail_monitoring_tit button" : "serverDetailMonitoringDisplay",
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
            },
            detailView: function() {
                var m = this.currentSelRow();
                if(m) {
                    var tabIndex = $('.detail_tab a.on').index();
                    switch (tabIndex) {
                        case 0 : modules.detailView.model.set(m.toJSON()); break;
                        case 1 :
                            MonitoringUI.modules.loadingEfftect("on");
                            MonitoringUI.modules.reload(m);
                            break;
                        case 2 :
                            modules.detailHistoryView.render(m);
                            break;
                    }

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
            serverCreate : function(){
                modules.createView.open();
            },
            serverStart: function () {
                if(confirm("서버를 시작 하시겠습니까?")) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    if(!(m.get('powerState') == "stopped" || m.get('powerState') == "deallocated")) {
                        alert("서버 시작은 서버 상태가 Stopped 이어야 합니다.");
                        return false;
                    }

                    m.set({powerState: "starting", provisioningState: null});

                    var model = new Backbone.Model({
                        action: "START",
                        serverId : m.get("id")
                    });
                    model.url = '/edge/rexgen/servers/' + btoa(m.get('id')) + '/action?id=' + id;

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
                if(confirm("서버를 중지 하시겠습니까?")) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    if(m.get('powerState') != "running") {
                        alert("서버 중지는 서버 상태가 Running 이어야 합니다.");
                        return false;
                    }

                    m.set({powerState: "deallocating", provisioningState: null});

                    var model = new Backbone.Model({
                        action: "STOP",
                        serverId : m.get("id")
                    });
                    model.url = '/edge/rexgen/servers/' + btoa(m.get('id')) + '/action?id=' + id;

                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                            sleep(1000);
                            location.href = location.href;
                        },
                        error: function (model, response, options) {
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverReBoot: function () {
                if(confirm("서버를 재시작 하시겠습니까?")) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    if(m.get('powerState') != "running") {
                        alert("재시작은 서버 상태가 Running 이어야 합니다.");
                        return false;
                    }

                    m.set({powerState: "stopping", provisioningState: null});

                    var model = new Backbone.Model({
                        action: "REBOOT",
                        serverId : m.get("id")
                    });
                    model.url = '/edge/rexgen/servers/' + btoa(m.get('id')) + '/action?id=' + id;

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

                    m.set({powerState: "deallocating", provisioningState: null});

                    var model = new Backbone.Model({
                        action: "DELETE",
                        serverId : m.get("id")
                    });
                    model.url = '/edge/rexgen/servers/' + btoa(m.get('id')) + '/action?id=' + id;

                    showLoadingUI(true, i18n('s.t.please-wait'));
                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.remove(model, {merge: true});
                            setTimeout(function() {
                                showLoadingUI(false);
                                self.clearDetail();
                                location.reload();
                            }, 500);
                        },
                        error: function (model, response, options) {
                            setTimeout(function() {
                                showLoadingUI(false);
                                location.reload();
                            }, 500);
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            serverMonitoringEnabled: function () {
                var self = this;
                if(confirm("세부 모니터링을 활성화 하시겠습니까?")) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "MONITORING"
                    });
                    model.url = '/edge/rexgen/servers/' + m.get('id') + '/action?id=' + id;

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
                if(confirm("세부 모니터링을 비활성화 하시겠습니까?")) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "UNMONITORING"
                    });
                    model.url = '/edge/rexgen/servers/' + m.get('id') + '/action?id=' + id;

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
                    datatype: "local",
                    // url: '/edge/etri/servers?id=' + id,
                    data: [{host:'101.79.1.105/27',type:'Windows Server 2019',ip:'100.100.100.15/24',cpu:'80',memory:'128GB',disk:'3.6TB',state:'active',approTime:'2020-11-17 14:00:00'}],
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
                    sortname: "name",
                    sortorder: "asc",
                    loadonce: true,
                    // width:1618,
                    autowidth: true,
                    // gridComplete: function () {
                    //     $(this).resetSize();
                    // },
                    // // multiSort: true,
                    // scrollOffset: 0,
                    // rowNum: setRowNum(15, self.gridId),
                    // loadtext: "",
                    // autoencode: true,
                    // onSelectRow: function (id) {
                    //     var m = self.collection.get(id);
                    //     var tabIndex = $('.detail_tab a.on').index();
                    //     modules.detailView.model.set(m.toJSON());
                    //     if(tabIndex === 1){
                    //         MonitoringUI.modules.loadingEfftect("on");
                    //         MonitoringUI.modules.reload(m);
                    //     }  else if(tabIndex === 2) {
                    //         modules.detailHistoryView.render(m);
                    //     }
                    //
                    //     $('.content').addClass('detail_on');
                    //     setTimeout(function() {
                    //         self.grid.resetSize()
                    //     }, options.gridReSizeTime);
                    //
                    //     if(modules.detailView.model.get('powerState') === "starting") {
                    //         modules.detailView.$el.find(".server_id").css("display", "none");
                    //     } else {
                    //         modules.detailView.$el.find(".server_id").css("display", "");
                    //     }
                    // },
                    // loadComplete: function (data) {
                    //     self.collection.reset(data.rows);
                    //     data.gridId = self.gridId;
                    //     data.getPageParam = function (data) {
                    //         return {
                    //             'q0': $(".select_search option:selected").val(),
                    //             'q1': $(".input_search").val()
                    //         }
                    //     };
                    //     data.rowNum = $(this).getGridParam("rowNum");
                    //     data.reccount = $(this).getGridParam("reccount");
                    //     $("#pager1").pager(data);
                    //     $("#server-grid tr:eq(1)").trigger('click');
                    //
                    // }
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
            $("#test1").text("REXGEN_EDGE")
            modules.view = new ServerView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);

stompUtil.connect();