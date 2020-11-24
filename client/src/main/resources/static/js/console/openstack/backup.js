var BackupUI = (function (options) {

    var
        modules = {},
        BackupModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/backups?id=' + id,
            defaults: {
                id: null,
                name: '',
                description: '',
                volumeId: null,
                volumeName: '',
                container: '',
                incremental: false,
                zone: '',
                state: '',
                size: 0,
                createdAt: '',
                failReason: '',
                objectCount: 0,
                dependent: false,
                snapshotId: null
            }
        }),
        BackupCollection = Backbone.Collection.extend({
            model: BackupModel
        }),
        BackupDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new BackupModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.description\') }}</th>\n            <td>{{= description }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.state\') }}</th>\n            <td>{{= state }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.volumeId\') }}</th>\n            <td>{{= volumeId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.volumeName\') }}</th>\n            <td>{{= volumeName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.container\') }}</th>\n            <td>{{= container }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.incremental\') }}</th>\n            <td>{{= incremental }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.size\') }}</th>\n            <td>{{= size }}GiB</td>\n            <th>{{= jQuery.i18n.prop(\'w.accessibility-domain\') }}</th>\n            <td>{{= zone }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.dependent\') }}</th>\n            <td>{{= dependent }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.createdAt\') }}</th>\n            <td>{{= createdAt }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.failReason\') }}</th>\n            <td>{{= failReason }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.objectCount\') }}</th>\n            <td>{{= objectCount }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.snapshotId\') }}</th>\n            <td>{{= snapshotId }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('name'));
                    self.render();
                });
            }
        }),
        BackupView = Backbone.View.extend({
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
                    // alert("Backup 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.backup'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#backup-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/backups?id=' + id,
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
                        jQuery.i18n.prop('title.jqgrid.container'),
                        jQuery.i18n.prop('title.jqgrid.incremental'),
                        // jQuery.i18n.prop('title.jqgrid.zone'),
                        jQuery.i18n.prop('w.accessibility-domain'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.failReason'),
                        jQuery.i18n.prop('title.jqgrid.objectCount'),
                        jQuery.i18n.prop('title.jqgrid.dependent'),
                        jQuery.i18n.prop('title.jqgrid.snapshotId')
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'description'},
                        {name: 'size', sorttype:'integer', formatter:function (cellVal, options, row) {
                                // return cellVal + " GiB";
                                return cellVal + " GB";
                            }},
                        {name: 'state'},
                        {name: 'volumeName'},
                        {name: 'id', hidden: true},
                        {name: 'volumeId', hidden: true},
                        {name: 'container', hidden: true},
                        {name: 'incremental', hidden: true},
                        {name: 'zone', hidden: true},
                        {name: 'createdAt', hidden: true},
                        {name: 'failReason', hidden: true},
                        {name: 'objectCount', hidden: true},
                        {name: 'dependent', hidden: true},
                        {name: 'snapshotId', hidden: true}
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
                        $("#backup-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new BackupCollection();
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
            modules.view = new BackupView();
            modules.detailView = new BackupDetailView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);
