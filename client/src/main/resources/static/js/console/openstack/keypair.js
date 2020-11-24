var KeyPairUI = (function (options) {

    var
        modules = {},
        KeyPairModel = Backbone.Model.extend({
            idAttribute: 'name',
            urlRoot: '/private/openstack/keypairs?id=' + id,
            defaults: {
                name: '',
                fingerprint: '',
                publicKey: '',
                privateKey: ''
            }
        }),
        KeyPairCollection = Backbone.Collection.extend({
            model: KeyPairModel
        }),
        KeyPairDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new KeyPairModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.fingerprint\') }}</th>\n            <td>{{= fingerprint }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.publicKey\') }}</th>\n            <td colspan="3" style="word-break: break-all;">{{= publicKey }}</td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
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
        KeyPairView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"clearDetail",
                "click #keypair_create": "create",
                "click #keypair_upload": "upload",
                "click #keypair_delete": "delete"
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
                    // alert("KeyPair 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.keypair'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            create: function () {
                modules.createView.open();
            },
            upload: function () {
                modules.uploadView.open();
            },
            delete: function () {
                var self = this;
                if (confirm(i18n('s.t.would-like-to', 'w.delete'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "DELETE"
                    });
                    model.url = '/private/openstack/keypairs/' + encodeURI(m.get('name')) + '/delete?id=' + id;

                    showLoadingUI(true, i18n('s.t.please-wait'));
                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            var modelInfo = modules.view.collection.findWhere({name:model.get('name')});
                            modules.view.collection.remove(modelInfo, {merge: true});
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
                this.gridId = "#keypair-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/keypairs?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "name"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.fingerprint'),
                        jQuery.i18n.prop('title.jqgrid.publicKey')
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'fingerprint'},
                        {name: 'publicKey', hidden: true}
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
                        $("#keypair-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new KeyPairCollection();
                this.collection.on("add", function (model) {
                    // self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                    self.grid.addRowData(model.attributes.name, model.toJSON(), "first");
                });
                this.collection.on("change", function (model) {
                    // self.grid.setRowData(model.attributes.id, model.toJSON());
                    self.grid.setRowData(model.attributes.name, model.toJSON());
                    modules.detailView.model.set(model.toJSON());
                });
                this.collection.on("remove", function(model) {
                    // self.grid.delRowData(model.get('id'));
                    self.grid.delRowData(model.get('name'));
                    modules.detailView.model.reset();
                });
            }
        }),
        CreateKeyPairView = Backbone.View.extend({
            el: "#popupCreate",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            init: function () {
                this.$el.find('input[name="name"]').val('');
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
                // console.log("input name is : " + name);

                if(ValidationUtil.trim(name) === '') {
                    return alert(i18n('s.t.input', 'w.name'));
                }
                var m = modules.view.collection.get(name);
                // console.log("m is : " + m);
                if(m != undefined) {
                    return alert(i18n('s.t.subject-did', 'w.name', 'w.duplication'));
                }
                if(name.toString().match(/^[a-z|A-Z|]+[a-z|A-Z|0-9|\-|\s]*$/) == null) {
                    return alert(i18n('s.t.not-valid', 'w.name'));
                }
                var model = new Backbone.Model({
                    name: name
                });
                model.url = '/private/openstack/keypairs?id=' + id;

                showLoadingUI(true, i18n('s.t.please-wait'));
                model.save(model.attributes, {
                    success: function (model, response, options) {
                        showLoadingUI(false);
                        modules.view.collection.add(new KeyPairModel(model.toJSON()), {merge: true});
                        downloadTextFile(model.get('privateKey'), model.get('name')+'.pem');
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
        UploadKeyPairView = Backbone.View.extend({
            el: "#popupUpload",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "change input:file": "displayFile",
                "keyup textarea": "checkSize"
            },
            init: function () {
                this.$el.find('input[name="name"]').val('');
                this.$el.find("textarea[name='publicKey']").val('');

                this.$el.find("tbody td span").css("color", "#667285");
                this.$el.find("tbody span").html("Content size: 0 bytes of 16.00 KB");
            },
            open: function () {
                this.init();
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
                this.$el.find('.name').focus();
            },
            displayFile: function (e) {
                var self = this;
                var fileList = this.$el.find("input:file")[0].files;

                // 읽기
                var reader = new FileReader();
                reader.readAsText(fileList [0]);

                //로드 한 후
                reader.onload = function () {
                    self.$el.find("textarea").val(reader.result);
                    self.$el.find("input:file").val('');
                    self.checkSize();
                };
            },
            checkSize: function (e) {
                var textFileAsBlob = new Blob([this.$el.find("textarea").val()], {type: 'text/plain'});
                var size = textFileAsBlob.size;

                if (size > 16384) {
                    this.$el.find("tbody td span").css("color", "#ff0000");
                } else {
                    this.$el.find("tbody td span").css("color", "#667285");
                }

                this.$el.find("tbody span").html("Content size: " + this.getSizeToFixed(size) + " of 16.00 KB");
            },
            getSizeToFixed: function (size) {
                if (size < 1024) {
                    return size + ' bytes';
                } else if (size < 1048576) {
                    return (size / 1024).toFixed(2) + ' KB';
                } else if (size < 1073741824) {
                    return (size / 1048576).toFixed(2) + ' MB';
                } else if (size < 1099511627776) {
                    return (size / 1073741824).toFixed(2) + ' GB';
                } else if (size < 1125899906842624) {
                    return (size / 1099511627776).toFixed(2) + ' TB';
                } else if (size < 4503599627370496) {
                    return (size / 1125899906842624).toFixed(2) + ' PB';
                }
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            save: function () {
                var self = this;

                var name = this.$el.find('input[name="name"]').val();
                var publicKey = this.$el.find("textarea[name='publicKey']").val();

                if (ValidationUtil.trim(name) === '') {
                    return alert(i18n('s.t.input', 'w.name'));
                }
                var m = modules.view.collection.get(name);
                if(m != undefined) {
                    return alert(i18n('s.t.subject-did', 'w.name', 'w.duplication'));
                }
                if(name.toString().match(/^[a-z|A-Z|]+[a-z|A-Z|0-9|\-|\s]*$/) == null) {
                    return alert(i18n('s.t.not-valid', 'w.name'));
                }
                if (ValidationUtil.trim(publicKey) === '') {
                    return alert(i18n('s.t.input', i18n('w.t.multi','w.public','w.key')));
                }

                var textFileAsBlob = new Blob([publicKey], {type: 'text/plain'});
                var size = textFileAsBlob.size;

                if (size > 16384) {
                    alert(i18n('s.t.check-size', i18n('w.t.multi','w.public','w.key')));
                    return;
                }

                var model = new Backbone.Model({
                    name: name,
                    publicKey: publicKey
                });
                model.url = '/private/openstack/keypairs?id=' + id;

                showLoadingUI(true, i18n('s.t.please-wait'));
                model.save(model.attributes, {
                    success: function (model, response, options) {
                        showLoadingUI(false);
                        modules.view.collection.add(new KeyPairModel(model.toJSON()), {merge: true});
                        self.close();
                    },
                    error: function (model, response, options) {
                        showLoadingUI(false);
                        if(response.responseText !== undefined && response.responseText.indexOf('Keypair data is invalid') > -1) {
                            alert(i18n("s.t.not-valid", i18n('w.t.multi','w.public','w.key')));
                        } else {
                            ValidationUtil.getServerError(response);
                        }
                        self.close();
                    }
                });
            }
        }),
        init = function (isAdmin) {
            modules.view = new KeyPairView();
            modules.detailView = new KeyPairDetailView();
            modules.createView = new CreateKeyPairView();
            modules.uploadView = new UploadKeyPairView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);