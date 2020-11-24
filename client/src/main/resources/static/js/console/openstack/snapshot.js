var SnapshotUI = (function (options) {

    var
        modules = {},
        SnapshotModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/snapshots?id=' + id,
            defaults: {
                id: null,
                name: '',
                description: '',
                volumeId: null,
                volumeName: '',
                state: '',
                size: 0,
                createdAt: '',
                metaData: {}
            }
        }),
        SnapshotCollection = Backbone.Collection.extend({
            model: SnapshotModel
        }),
        SnapshotDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new SnapshotModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.description\') }}</th>\n            <td>{{= description }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.state\') }}</th>\n            <td>{{= state }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.volumeId\') }}</th>\n            <td>{{= volumeId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.volumeName\') }}</th>\n            <td>{{= volumeName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.size\') }}</th>\n            <td>{{= size }}GiB</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.createdAt\') }}</th>\n            <td>{{= createdAt }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.metaData\') }}</th>\n            <td>{{= objToString(metaData) }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('name'));
                    self.render();
                });
            }
        }),
        SnapshotView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click #delete": "delete",
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"clearDetail"
            },
            delete: function () {
                var self = this;
                //confirm('스냅샷을 삭제하시겠습니까?')
                if (confirm(i18n('s.t.would-like-to', 'w.delete'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model();

                    model.url = '/private/openstack/snapshots/' + m.get('id') + '/delete?id=' + id;

                    showLoadingUI(true, i18n('s.t.please-wait'));
                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            modules.view.collection.remove(model, {merge: true});
                            showLoadingUI(false);
                            self.clearDetail();
                        },
                        error: function (model, response, options) {
                            showLoadingUI(false);
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
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
                    // alert("Snapshot 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.snapshot'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#snapshot-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/snapshots?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.description'),
                        jQuery.i18n.prop('title.jqgrid.size'),
                        jQuery.i18n.prop('title.jqgrid.state'),
                        jQuery.i18n.prop('title.jqgrid.volumeName'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.volumeId'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.metaData')
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'description'},
                        {name: 'size', sorttype:'integer', formatter:function (cellVal, options, row) {
                                return cellVal + " GB";
                                /*return cellVal + "GiB";*/
                            }},
                        {name: 'state'},
                        {name: 'volumeName'},
                        {name: 'id', hidden: true},
                        {name: 'volumeId', hidden: true},
                        {name: 'createdAt', hidden: true},
                        {name: 'metaData', hidden: true}
                    ],
                    altRows: true,
                    sortname: "name",
                    sortorder: "asc",
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
                        $("#snapshot-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new SnapshotCollection();
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
            modules.view = new SnapshotView();
            modules.detailView = new SnapshotDetailView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);
