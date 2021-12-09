var ServerUI = (function (options) {

    var
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
            model: ServerModel
        }),

        CctvModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/cctv/cctvs',
            defaults: {
                cctv_id: '',
                cctv_parent_id: '',
                cctv_cam_nm: '',
                cctv_ip: '',
                cctv_login_id: '',
                cctv_login_pw: '',
                cctv_rtsp_url: '',
                cctv_rtsp_port: '',
                cctv_http_port: '',
                crsrd_id: '',
                lght_use_yn: '',
                connect_svr: '',
                id: null,
            }
        }),
        CctvCollection = Backbone.Collection.extend({
            model: CctvModel
        }),

        CctvDetailView = Backbone.View.extend({
            el: "#tab1",
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
                    url: "/cctv/cctvs2",
                }).trigger("reloadGrid");
            },
            initialize: function () {
                var self = this;

                this.collection = new CctvCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#cctv-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "cctv_id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.cctvId'),
                        jQuery.i18n.prop('title.jqgrid.cctvParentId'),
                        jQuery.i18n.prop('title.jqgrid.cctvCamNm'),
                        jQuery.i18n.prop('title.jqgrid.cctvIp'),
                        jQuery.i18n.prop('title.jqgrid.cctvLoginId'),
                        jQuery.i18n.prop('title.jqgrid.cctvLoginPw'),
                        jQuery.i18n.prop('title.jqgrid.cctvRtspUrl'),
                        jQuery.i18n.prop('title.jqgrid.cctvRtspPort'),
                        jQuery.i18n.prop('title.jqgrid.cctvHttpPort'),
                        jQuery.i18n.prop('title.jqgrid.crsrdId'),
                        jQuery.i18n.prop('title.jqgrid.lghtUseYn'),
                        jQuery.i18n.prop('title.jqgrid.connectSvr')
                    ],
                    colModel: [
                        {name: 'cctv_id', admin: false, align: 'left'},
                        {name: 'cctv_parent_id',align: 'left'},
                        {name: 'cctv_cam_nm', align: 'left'},
                        {name: 'cctv_ip', align: 'left'},
                        {name: 'cctv_login_id', align: 'left'},
                        {name: 'cctv_login_pw', align: 'left'},
                        {name: 'cctv_rtsp_url', align: 'left'},
                        {name: 'cctv_rtsp_port', align: 'left'},
                        {name: 'cctv_http_port', align: 'left'},
                        {name: 'crsrd_id', align: 'left'},
                        {name: 'lght_use_yn', align: 'left'},
                        {name: 'connect_svr', align: 'left'},
                    ],
                    altRows: true,
                    sortname: "cctv_id",
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
                        $("#cctvpager").pager(data);
                        // $("#cctv-grid tr:eq(1)").trigger('click');
                    }
                });
            }
        }),

        ServerView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"closeDetail",
                "click .detail_tab a": "detailView",
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
                    if(tabIndex == 0){
                        modules.cctvdetailview.render(m);
                    }
                    else if(tabIndex == 1) {
                        MonitoringUI.modules.loadingEfftect("on");
                        MonitoringUI.modules.reload();
                    } else if(tabIndex == 2) {
                        modules.detailHistoryView.reset();
                    }
                } else {
                    var selRow = this.grid.getGridParam("selrow");
                    if (!selRow) {
                        return null;
                    }else{
                        var tabIndex = $('.detail_tab a.on').index();
                        if(tabIndex == 0){
                            modules.cctvdetailview.render(m);
                        }
                        else if(tabIndex == 1) {
                        }
                    }
                }
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("Edge Server를 선택해주세요");
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
                    autowidth: true,
                    scrollOffset: 0,
                    rowNum: setRowNum(15, self.gridId),
                    loadtext: "",
                    autoencode: true,
                    onSelectRow: function (id) {
                        var m = self.collection.get(id);
                        var tabIndex = $('.detail_tab a.on').index();
                        if(tabIndex == 0){
                            modules.cctvdetailview.render(m);
                        }
                        else if(tabIndex == 1) {
                            MonitoringUI.modules.loadingEfftect("on");
                            MonitoringUI.modules.reload();
                        } else if(tabIndex == 2) {
                            modules.detailHistoryView.reset();
                        }

                        $('.content').addClass('detail_on');
                        setTimeout(function() {
                            self.grid.resetSize()
                        }, options.gridReSizeTime);
                    },
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
            modules.cctvdetailview = new CctvDetailView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);

stompUtil.connect();