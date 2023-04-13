var MeterServerUI = (function (options) {

    var
        modules = {},
        // gridData = [],
        MeterServerModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/meter/servers?id=' + id,
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
        MeterServerCollection = Backbone.Collection.extend({
            model: MeterServerModel
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
        BillingModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/meter/servers?id='+id,
            defaults: {
                id: null,
                credentialId: '',
                cloudType: '',
                cloudName: '',
                projectId: '',
                instanceId: '',
                instanceName: '',
                ImageId: '',
                flavorId: '',
                flavorName: '',
                flavorVcpu: '',
                flavorRam: '',
                flavordisk: '',
                meterDuration: '',
                meterStartTime: '',
                meterEndTime: '',
                createdAt: '',
                updatedAt: '',
                cloudTarget: '',
                billing: '',
                state: ''
            }
        }),
        BillingCollection = Backbone.Collection.extend({
            model: BillingModel
        }),
        MeterServerDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new MeterServerModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.instanceId\') }}</th>\n            <td>{{= instanceId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.instanceName\') }}</th>\n            <td>{{= instanceName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.flavorId\') }}</th>\n            <td>{{= flavorId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.spec\') }}</th>\n            <td>{{= flavorName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.measureStartDate\') }}</th>\n            <td>{{= meterStartTime }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.measureEndDate\') }}</th>\n            <td>{{= meterEndTime }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.usageTime\') }}</th>\n            <td>{{= meterDuration }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.billing\') }}</th>\n            <td>{{= billing }}원</td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('instanceId'));
                    self.render();
                });
            }
            // events: {
            //     "click button.btn_action_refresh": "reload"
            // }
        }),
        MeterServerView = Backbone.View.extend({

            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_tab a": "detailView",
                "click .detail_label_btn":"clearDetail",
                "click .btn1" : "billing"
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
            detailView: function() {
                var m = this.currentSelRow();
                if(m) {
                    var tabIndex = $('.detail_tab a.on').index();
                    if(tabIndex == 1) {
                        modules.detailServerView.render(m);
                    }
                } else {
                    var tabIndex = $('.detail_tab a.on').index();
                    if(tabIndex == 1) {
                        modules.detailServerView.reset();
                    }
                }
            },
            clearDetail: function() {
                // modules.detailView.model.reset();
                // modules.detailServerView.reset();
                // this.closeDetail();
            },
            billing: function (e) {
                var rowId = e.currentTarget.id.replace("btn", "");
                // console.log(this.grid.getGridParam("data"));
                var billingData = this.grid.getGridParam("data");
                modules.billingPage.show(rowId, billingData);
                modules.billingPage.initialize(billingData);
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    // alert("MeterServer 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected', 'Meter Server'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#meterServer-grid";
                this.grid = $(this.gridId).jqGrid({
                    idAttribute: 'id',
                    datatype: "json",
                    url: '/private/openstack/meter/servers?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.instanceName'),
                        jQuery.i18n.prop('title.jqgrid.instanceId'),
                        jQuery.i18n.prop('title.jqgrid.flavorId'),
                        // jQuery.i18n.prop('title.jqgrid.flavorName'),
                        jQuery.i18n.prop('title.jqgrid.spec'),
                        jQuery.i18n.prop('title.jqgrid.measureStartDate'),
                        jQuery.i18n.prop('title.jqgrid.measureEndDate'),
                        jQuery.i18n.prop('title.jqgrid.usageTime'),
                        jQuery.i18n.prop('title.jqgrid.billing'),
                        '청구서 확인',
                        jQuery.i18n.prop('title.jqgrid.projectID'),
                        jQuery.i18n.prop('title.jqgrid.id')
                    ],
                    colModel: [
                        {name: 'instanceName'},
                        {name: 'instanceId'},
                        {name: 'flavorId', hidden: true},
                        {name: 'flavorName'},
                        {name: 'meterStartTime'},
                        {name: 'meterEndTime'},
                        {name: 'meterDuration', formatter: dateFormatter, sorttype: 'integer'},
                        // {name: 'billing', formatter: fn_numberFormat},
                        {name: 'billing', sorttype:'integer', formatter:function (cellVal, options, row) {
                                return cellVal + "원";
                            }},
                        {name:'btn1', index:'btn1', width:80, align: "center", formatter:formatOpt1, sortable: false},
                        {name: 'projectId', hidden: true},
                        {name: 'id', hidden: true}
                    ],
                    altRows: true,
                    sortname: "id",
                    sortorder: "asc",
                    loadonce: true,
                    autowidth: true,
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
                        // if ($("#detailBtn").hasClass("selected")) {
                        var tabIndex = $('.detail_tab a.on').index();
                        modules.detailView.model.set(m.toJSON());
                        if(tabIndex == 1) {
                            modules.detailServerView.render(m);
                        }
                        // }
                        $('.content').addClass('detail_on');
                        setTimeout(function() {
                            self.grid.resetSize()
                        }, options.gridReSizeTime);
                    },
                    loadComplete: function (data) {
                        self.collection.reset(data.rows);
                        data.gridId = self.gridId;
                        // data.getPageParam = function (data) {
                        //     return {
                        //         'q0': $(".select_search option:selected").val(),
                        //         'q1': $(".input_search").val()
                        //     }
                        // };
                        gridData = $(this).jqGrid('getGridParam', 'data');
                        console.log("gridData ", gridData);
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager1").pager(data);
                        $("#meterServer-grid tr:eq(1)").trigger('click');

                    }
                });

                function formatOpt1(cellvalue, options, rowObject){
                    var str = "";
                    var row_id = options.rowId;
                    var idx = rowObject.idx;

                    // str += "<div class=\"btn-group\">";
                    str += "<button id='btn"+row_id+"' type='button' class='btn1 btn-default btn-sm'>확인</button>"
                    // str += "</div>";
                    str += "<script>$('#btn"+row_id+"').click(function(){ var row_id = "+row_id+"; console.log("+row_id+"); });</script>";

                    return str;
                };

                this.collection = new MeterServerCollection();
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
            }
        }),

        BillingPage = Backbone.View.extend({
            model: new BillingModel(),
            el : "#pop_billing",
            events : {
                "click .pop_billing_di .pop_btn": "close",
                "click .btn_pop_close": "close"
            },
            initialize: function (billingData) {
                var self = this;
                // console.log(billingData);
                this.gridId = '#meterServer-popGrid';
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/meter/servers?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        'ID',
                        '인스턴스 이름',
                        '인스턴스 ID'
                    ],
                    colModel: [
                        {name: 'id'},
                        {name: 'instanceName'},
                        {name: 'instanceId'},
                    ],

                    gridComplete: function () {
                        $(this).resetSize();
                    },
                });
            },
            show : function (rowId, billingData) {
                // var gridData = this;
                var gridData = billingData.filter(data => data.id === rowId);
                this.grid.setGridParam({
                    datatype: "local",
                    page: 1,
                    data: gridData,
                }).trigger("reloadGrid");
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close : function(){
                this.$el.fadeOut(100);
            },
        }),

        ServerView = Backbone.View.extend({
            el: "#tab2",
            events: {
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control": "resetGrid"
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
                    url: "/private/openstack/meter/servers/" + model.get('instanceId') + "?id=" + id
                }).trigger("reloadGrid");
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

                this.collection = new ServerCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#server-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.instanceId'),
                        jQuery.i18n.prop('title.jqgrid.flavorId'),
                        jQuery.i18n.prop('title.jqgrid.status'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.id')
                    ],
                    colModel: [
                        {name: 'instanceId'},
                        {name: 'flavorId'},
                        {name: 'status', formatter: stateIconFormatter},
                        {name: 'createdAt'},
                        {name: 'id', hidden: true}
                    ],
                    altRows: true,
                    sortname: "id",
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
                        // data.getPageParam = function (data) {
                        //     return {
                        //         'q0': 'name',
                        //         'q1': self.$el.find('.sub_search')
                        //     }
                        // };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager2").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');

                    }
                });
            }
        }),
        init = function (isAdmin) {
            modules.view = new MeterServerView();
            modules.detailView = new MeterServerDetailView();
            modules.detailServerView = new ServerView();
            modules.billingPage = new BillingPage();
        };

    return {
        init: init,
        modules: modules
    };
})(config);