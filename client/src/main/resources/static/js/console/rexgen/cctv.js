var CctvUI = (function (options) {

    var
        modules = {},
        CctvModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/cctv/cctvs2',
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
            render: function(model) {
                this.collection.reset();
                this.grid.clearGridData();
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
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

                this.collection = new CctvCollection();
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
            modules.view = new ServerView()
            modules.detailView = new CctvDetailView();
        };

    return {
        init: init,
        modules: modules
    };
})(config);

stompUtil.connect();