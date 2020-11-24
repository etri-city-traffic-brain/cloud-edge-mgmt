var ImageUI = (function (options) {

    var
        modules = {},
        ImageModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/images?id=' + id,
            defaults: {
                id: null,
                name: '',
                type: '',
                state: '',
                visibility: '',
                isProtected: '',
                diskFormat: '',
                containerFormat: '',
                size: 0,
                minDisk: 0,
                minRam: 0,
                createdAt: '',
                updatedAt: '',
                file: '',
                schema: '',
                tag: [],
                virtualSize: 0,
                owner: '',
                checksum: '',
                instanceUuid: null
            }
        }),
        ImageCollection = Backbone.Collection.extend({
            model: ImageModel
        }),
        ImageDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new ImageModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.type\') }}</th>\n            <td>{{= type }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.state\') }}</th>\n            <td>{{= state }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.size\') }}</th>\n            <td>{{= byteSizeFormatter(size) }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.minimumDisk\') }}</th>\n            <td>{{= minDisk }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.minimumRam\') }}</th>\n            <td>{{= minRam }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.diskFormat\') }}</th>\n            <td>{{= diskFormat }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.containerFormat\') }}</th>\n            <td>{{= containerFormat }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.createdAt\') }}</th>\n            <td>{{= createdAt }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.updatedAt\') }}</th>\n            <td>{{= updatedAt }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.file\') }}</th>\n            <td>{{= file }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.schema\') }}</th>\n            <td>{{= schema }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.tag\') }}</th>\n            <td>{{= tag }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.virtualSize\') }}</th>\n            <td>{{= virtualSize }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.ownerId\') }}</th>\n            <td>{{= owner }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.visibility\') }}</th>\n            <td>{{= visibility }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.protected\') }}</th>\n            <td>{{= isProtected }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.checksum\') }}</th>\n            <td>{{= checksum }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('name'));
                    self.render();
                });
            }
            // events: {
            //     "click button.btn_action_refresh": "reload"
            // }
        }),
        ImageView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"clearDetail",
                "click #create_image": "createImage",
                "click #image_delete": "deleteImage"
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
                    // alert("Image 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.image'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            createImage: function () {
                modules.createView.open();
            },
            deleteImage: function () {
                var self = this;
                if (confirm(i18n('s.t.would-like-to', 'w.delete'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "DELETE"
                    });
                    model.url = '/private/openstack/images/' + m.get('id') + '/delete?id=' + id;

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
            initialize: function () {
                var self = this;
                this.gridId = "#image-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/images?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.type'),
                        jQuery.i18n.prop('title.jqgrid.state'),
                        jQuery.i18n.prop('title.jqgrid.visibility'),
                        jQuery.i18n.prop('title.jqgrid.protected'),
                        jQuery.i18n.prop('title.jqgrid.diskFormat'),
                        jQuery.i18n.prop('title.jqgrid.size'),
                        jQuery.i18n.prop('title.jqgrid.minimumDisk'),
                        jQuery.i18n.prop('title.jqgrid.minimumRam'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.containerFormat'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.updatedAt'),
                        jQuery.i18n.prop('title.jqgrid.file'),
                        jQuery.i18n.prop('title.jqgrid.schema'),
                        jQuery.i18n.prop('title.jqgrid.tag'),
                        jQuery.i18n.prop('title.jqgrid.virtualSize'),
                        jQuery.i18n.prop('title.jqgrid.ownerId'),
                        jQuery.i18n.prop('title.jqgrid.checksum'),
                        jQuery.i18n.prop('title.jqgrid.instanceUuid')
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'type'},
                        {name: 'state'},
                        {name: 'visibility'},
                        {name: 'isProtected'},
                        {name: 'diskFormat'},
                        {name: 'size', sorttype:'integer', formatter:byteSizeFormatter},
                        {name: 'minDisk', sorttype:'integer', hidden: true},
                        {name: 'minRam', sorttype:'integer', hidden: true},
                        {name: 'id', hidden: true},
                        {name: 'containerFormat', hidden: true},
                        {name: 'createdAt', hidden: true},
                        {name: 'updatedAt', hidden: true},
                        {name: 'file', hidden: true},
                        {name: 'schema', hidden: true},
                        {name: 'tag', hidden: true},
                        {name: 'virtualSize', hidden: true},
                        {name: 'owner', hidden: true},
                        {name: 'checksum', hidden: true},
                        {name: 'instanceUuid', hidden: true}
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
                        $("#image-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new ImageCollection();
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
        CreateImageView = Backbone.View.extend({
            el: "#popupCreateImage",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            init: function () {
                this.$el.find('input[name="name"]').val('');
                this.$el.find('input[name="url"]').val('');
                this.$el.find('input[name="minDisk"]').val('0');
                this.$el.find('input[name="minRam"]').val('0');
                this.$el.find('select[name="format"] option').eq(0).prop('selected', true);
                this.$el.find('.select_wrap select').selectric('refresh');
                this.$el.find('input[name="visibility"]').eq(0).prop('checked', true);
                this.$el.find('input[name="protected"]').eq(1).prop('checked', true);
            },
            open: function () {
                this.init();
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
                this.$el.find('.name').focus();
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            save: function () {
                var self = this;

                var name = this.$el.find('input[name="name"]').val();
                var url = this.$el.find('input[name="url"]').val();
                var minDisk = this.$el.find('input[name="minDisk"]').val();
                var minRam = this.$el.find('input[name="minRam"]').val();
                var format = this.$el.find('select[name="format"]').val();
                var visibility = this.$el.find('input[name="visibility"]:checked').val();
                var protect = this.$el.find('input[name="protected"]:checked').val();

                if(ValidationUtil.trim(name) === '') {
                    return alert(i18n('s.t.input', 'w.name'));
                }

                if(ValidationUtil.trim(url) === '') {
                    return alert(i18n('s.t.input', "URL"));
                }

                if(ValidationUtil.trim(url) !== '' && url.toString().match(ValidationUtil.patterns.url) == null) {
                    return alert(i18n('s.t.not-valid', "URL"));
                }

                if(ValidationUtil.trim(minDisk) === '') {
                    return alert(i18n('s.t.input', i18n('w.multi','w.min','w.disk')));
                }

                if(parseInt(ValidationUtil.trim(minDisk)) < 0) {
                    return alert(i18n('s.t.not-valid', i18n('w.multi','w.min','w.disk')));
                }

                if(ValidationUtil.trim(minRam) === '') {
                    return alert(i18n('s.t.input', i18n('w.multi','w.min','w.memory')));
                }

                if(parseInt(ValidationUtil.trim(minRam)) < 0) {
                    return alert(i18n('s.t.not-valid', i18n('w.multi','w.min','w.memory')));
                }

                var isProtected = protect == "yes"? true: false;

                var model = new Backbone.Model({
                    name: name,
                    url: url,
                    format: format,
                    minDisk: minDisk,
                    minRam: minRam,
                    visibility: visibility,
                    protect: isProtected
                });
                model.url = '/private/openstack/images?id=' + id;

                showLoadingUI(true, i18n('s.t.please-wait'), i18n('s.t.task-long-time', i18n('w.t.create', 'w.image')));
                model.save(model.attributes, {
                    success: function (model, response, options) {
                        showLoadingUI(false);
                        modules.view.collection.add(model, {merge: true});
                        self.close();
                    },
                    error: function (model, response, options) {
                        showLoadingUI(false);
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),
        init = function (isAdmin) {
            modules.view = new ImageView();
            modules.detailView = new ImageDetailView();
            modules.createView = new CreateImageView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);