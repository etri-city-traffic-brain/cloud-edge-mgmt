var RouterUI = (function (options) {

    var
        modules = {},
        RouterModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/routers?id=' + id,
            defaults: {
                id: null,
                name: '',
                state: '',
                networkId: '',
                networkName: '',
                adminStateUp: '',
                visibilityZones: '',
                projectId: '',
                projectName: ''
            }
        }),
        RouterCollection = Backbone.Collection.extend({
            model: RouterModel
        }),
        RouterDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new RouterModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectId\') }}</th>\n            <td>{{= projectId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectName\') }}</th>\n            <td>{{= projectName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.state\') }}</th>\n            <td>{{= state }}</td>\n            <th>{{= jQuery.i18n.prop(\'w.accessibility-domain\') }}</th>\n            <td>{{= visibilityZones }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.networkId\') }}</th>\n            <td>{{= networkId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.networkName\') }}</th>\n            <td>{{= networkName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.adminState\') }}</th>\n            <td>{{= adminStateUp }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('name'));
                    self.render();
                });
            }
            //     events: {
            //         "click button.btn_action_refresh": "reload"
            //     }
        }),
        RouterView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"clearDetail"
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
            clearDetail: function () {
                modules.detailView.model.reset();
                this.closeDetail();
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("Router 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#router-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/routers?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.projectName'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.state'),
                        jQuery.i18n.prop('title.jqgrid.networkId'),
                        jQuery.i18n.prop('title.jqgrid.networkName'),
                        jQuery.i18n.prop('title.jqgrid.adminState'),
                        // jQuery.i18n.prop('title.jqgrid.zone'),
                        jQuery.i18n.prop('w.accessibility-domain'),
                        jQuery.i18n.prop('title.jqgrid.projectId'),
                        jQuery.i18n.prop('title.jqgrid.id')
                    ],
                    colModel: [
                        {name: 'projectName'},
                        {name: 'name'},
                        {name: 'state', formatter: stateIconFormatter},
                        {name: 'networkId', hidden: true},
                        {name: 'networkName'},
                        {name: 'adminStateUp'},
                        {name: 'visibilityZones', formatter:getVisibilityZonesText, sorttype: function(cell, row) {
                                if(cell.length > 0) {
                                    return cell[0]
                                }
                            }},
                        {name: 'projectId', hidden: true},
                        {name: 'id', hidden: true}
                    ],
                    altRows: true,
                    sortname: "name",
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
                        modules.detailView.model.set(m.toJSON());
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
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager1").pager(data);
                        $("#router-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new RouterCollection();
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
        init = function (isAdmin) {
            modules.view = new RouterView();
            modules.detailView = new RouterDetailView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);