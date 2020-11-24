var FloatingIpUI = (function (options) {

    var
        modules = {},
        FloatingIpModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/floatingips?id=' + id,
            defaults: {
                id: null,
                routerId: null,
                tenantId: null,
                projectName: '',
                floatingNetworkId: null,
                floatingIpAddress: '',
                fixedIpAddress: '',
                portId: null,
                status: '',
                serverName: '',
                networkName: '',
                instanceId: '',
                pool: ''
            }
        }),
        FloatingIpCollection = Backbone.Collection.extend({
            model: FloatingIpModel
        }),
        FloatingIpDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new FloatingIpModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectId\') }}</th>\n            <td>{{= tenantId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectName\') }}</th>\n            <td>{{= projectName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.networkId\') }}</th>\n            <td>{{= floatingNetworkId }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.routerId\') }}</th>\n            <td>{{= routerId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.portId\') }}</th>\n            <td>{{= portId }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.status\') }}</th>\n            <td>{{= status }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.mappedFixedIp\') }}</th>\n            <td>{{=serverName}} {{=fixedIpAddress}}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.networkName\') }}</th>\n            <td>{{= networkName }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('floatingIpAddress'));
                    self.render();
                });
            }
        }),
        FloatingIpView = Backbone.View.extend({
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
            clearDetail: function() {
                modules.detailView.model.reset();
                this.closeDetail();
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("FloatingIp 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#floatingip-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/floatingips?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.projectName'),
                        jQuery.i18n.prop('title.jqgrid.ipAddress'),
                        jQuery.i18n.prop('title.jqgrid.mappedFixedIp'),
                        jQuery.i18n.prop('title.jqgrid.networkName'),
                        jQuery.i18n.prop('title.jqgrid.status'),
                        jQuery.i18n.prop('title.jqgrid.networkId'),
                        jQuery.i18n.prop('title.jqgrid.routerId'),
                        jQuery.i18n.prop('title.jqgrid.portId'),
                        jQuery.i18n.prop('title.jqgrid.mappedFixedIp'),
                        jQuery.i18n.prop('title.jqgrid.server'),
                        jQuery.i18n.prop('title.jqgrid.projectId'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.instanceUuid'),
                        jQuery.i18n.prop('title.jqgrid.pool')
                    ],
                    colModel: [
                        {name: 'projectName'},
                        {name: 'floatingIpAddress'},
                        {name: 'mapping', formatter:function (cellVal, options, row) {
                                if(row.serverName != null && row.fixedIpAddress != null) {
                                    return row.serverName + ' ' + row.fixedIpAddress;
                                } else {
                                    return "";
                                }
                            }, sorttype: function(cell, row) {
                                return row.serverName;
                            }},
                        {name: 'networkName'},
                        {name: 'status', formatter: stateIconFormatter},
                        {name: 'floatingNetworkId', hidden: true},
                        {name: 'routerId', hidden: true},
                        {name: 'portId', hidden: true},
                        {name: 'fixedIpAddress', hidden: true},
                        {name: 'serverName', hidden: true},
                        {name: 'tenantId', hidden: true},
                        {name: 'id', hidden: true},
                        {name: 'instanceId', hidden: true},
                        {name: 'pool', hidden: true}
                    ],
                    altRows: true,
                    sortname: "floatingIpAddress",
                    sortorder: "asc",
                    loadonce: true,
                    autowidth: true,
                    //width:1618,
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
                        modules.detailView.model.set(m.toJSON());
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
                        $("#floatingip-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new FloatingIpCollection();
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
            modules.view = new FloatingIpView();
            modules.detailView = new FloatingIpDetailView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);
