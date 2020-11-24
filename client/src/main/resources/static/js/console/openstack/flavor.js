var FlavorUI = (function (options) {

    var
        modules = {},
        FlavorModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/flavors?id=' + id,
            defaults: {
                name: '',
                vcpus: 0,
                ram: 0,
                disk: 0,
                ephemeral: 0,
                swap: 0,
                rxtxFactor: 0,
                isPublic: false,
                id: null
            }
        }),
        FlavorCollection = Backbone.Collection.extend({
            model: FlavorModel
        }),
        FlavorDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new FlavorModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.vcore\') }}</th>\n            <td>{{= vcpus }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.ram\') }}</th>\n            <td>{{= getSizeToMB(ram) }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.rootDisk\') }}</th>\n            <td>{{= getSizeToGB(disk) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.tempDisk\') }}</th>\n            <td>{{= getSizeToGB(ephemeral) }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.swapDisk\') }}</th>\n            <td>{{= getSizeToMB(swap) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.rxtx\') }}</th>\n            <td>{{= getToFixedOne(rxtxFactor) }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.public\') }}</th>\n            <td>{{= isPublic }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->\n            '),
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
        FlavorView = Backbone.View.extend({
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
                    // alert("Flavor 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.spec'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#flavor-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/flavors?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.name'),
                        // jQuery.i18n.prop('title.jqgrid.vcpus'),
                        jQuery.i18n.prop('title.jqgrid.vcore'),
                        // jQuery.i18n.prop('title.jqgrid.ram'),
                        jQuery.i18n.prop('w.memory'),
                        jQuery.i18n.prop('title.jqgrid.rootDisk'),
                        // jQuery.i18n.prop('title.jqgrid.ephemeralDisk'),
                        jQuery.i18n.prop('title.jqgrid.tempDisk'),
                        jQuery.i18n.prop('title.jqgrid.swapDisk'),
                        jQuery.i18n.prop('title.jqgrid.rxtx'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.public')
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'vcpus', sorttype:'integer'},
                        {name: 'ram', sorttype:'integer', formatter: getSizeToMB},
                        {name: 'disk', sorttype:'integer', formatter:getSizeToGB},
                        {name: 'ephemeral', formatter:getSizeToGB},
                        {name: 'swap', formatter:getSizeToMB},
                        {name: 'rxtxFactor', sorttype:'float', formatter:getToFixedOne},
                        {name: 'id'},
                        {name: 'isPublic'}
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
                        $("#flavor-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new FlavorCollection();
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
            modules.view = new FlavorView();
            modules.detailView = new FlavorDetailView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);