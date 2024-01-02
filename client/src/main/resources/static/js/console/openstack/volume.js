var VolumeUI = (function (options) {

    var
        modules = {},
        VolumeModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/volumes?id=' + id,
            defaults: {
                name: '',
                description: '',
                state: '',
                size: 0,
                zone: '',
                bootable: false,
                createdAt: '',
                id: null,
                projectId: '',
                projectName: '',
                attachmentInfos: [],
                metaData: {},
                volumeType: ''
            }
        }),
        VolumeCollection = Backbone.Collection.extend({
            model: VolumeModel
        }),
        VolumeDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new VolumeModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectId\') }}</th>\n            <td>{{= projectId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectName\') }}</th>\n            <td>{{= projectName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.description\') }}</th>\n            <td>{{= description }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.state\') }}</th>\n            <td>{{= state }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.size\') }}</th>\n            <td>{{= size }}GiB</td>\n            <th>{{= jQuery.i18n.prop(\'w.accessibility-domain\') }}</th>\n            <td>{{= zone }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.bootable\') }}</th>\n            <td>{{= bootable }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.createdAt\') }}</th>\n            <td>{{= createdAt }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.attachedTo\') }}</th>\n            <td>{{= getAttachmentsText(attachmentInfos) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.metaData\') }}</th>\n            <td>{{= objToString(metaData) }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.volumeType\') }}</th>\n            <td>{{= volumeType }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    if(model.get('name') == "") {
                        $('.detail_tit').text(model.get('id'));
                    } else {
                        $('.detail_tit').text(model.get('name'));
                    }

                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    self.render();

                    var model2 = new Backbone.Model({
                    });
                    model2.url = '/private/openstack/volumes/' + m.get("id") + '?id=' + id;

                    model2.fetch(model2.attributes);
                });
            }
        }),
        VolumeView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"clearDetail",
                "click #volume_create": "create",
                "click #volume_delete": "volumeDelete",
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
            create: function() {
                modules.createView.open();
            },
            volumeDelete: function() {
                var self = this;
                // if(confirm("볼륨을 삭제 하시겠습니까?")) {
                if (confirm(i18n('s.t.would-like-to', 'w.delete'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({});
                    model.url = '/private/openstack/volumes/' + m.get('id') + '/delete?id=' + id;
                    // model.url = '/private/openstack/volumes/' + m.get('id') + '?id=' + id;

                    showLoadingUI(true, i18n('s.t.please-wait'));
                    model.save(model.attributes, {
                        success: function (model, response, options) {
                            showLoadingUI(false);
                            modules.view.collection.remove(model, {merge: true});
                            self.clearDetail();
                        },
                        error: function (model, response, options) {
                            showLoadingUI(false);
                            ValidationUtil.getServerError(response);
                        }
                    });
                }
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    // alert("Volume 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.volume'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#volume-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/volumes?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.projectName'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.description'),
                        jQuery.i18n.prop('title.jqgrid.size'),
                        jQuery.i18n.prop('title.jqgrid.state'),
                        jQuery.i18n.prop('title.jqgrid.attachedTo'),
                        // jQuery.i18n.prop('title.jqgrid.zone'),
                        jQuery.i18n.prop('w.accessibility-domain'),
                        jQuery.i18n.prop('title.jqgrid.bootable'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.projectId'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.metaData'),
                        jQuery.i18n.prop('title.jqgrid.volumeType')
                    ],
                    colModel: [
                        {name: 'projectName'},
                        {name: 'name'},
                        {name: 'description'},
                        {name: 'size', sorttype:'integer', formatter:function (cellVal, options, row) {
                                return cellVal + " GB";
                                /*return cellVal + " GiB";*/
                            }},
                        {name: 'state'},
                        {name: 'attachmentInfos', formatter: getAttachmentsText, sorttype: function (cell, row) {
                                if(cell.length > 0) {
                                    return cell[0]["serverName"];
                                }
                                return cell;
                            }},
                        {name: 'zone'},
                        {name: 'bootable'},
                        {name: 'createdAt', hidden: true},
                        {name: 'projectId', hidden: true},
                        {name: 'id', hidden: true},
                        {name: 'metaData', hidden: true},
                        {name: 'volumeType', hidden: true}
                    ],
                    altRows: true,
                    sortname: "createdAt",
                    sortorder: "desc",
                    loadonce: true,
                    autowidth: true,
                    // width:1618,
                    gridComplete: function () {
                        console.log($(this));
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
                        $("#volume-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new VolumeCollection();
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
        CreateView = Backbone.View.extend({
            el: "#popupCreate",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "change #sourceType": "changeSourceType",
            },
            zoneCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/zones?type=volume&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            zoneTemplate: _.template('<option value="{{=zoneName}}">{{=zoneName}}</option>'),
            imageCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/images?active=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            imageTemplate: _.template('<option value="{{=id}}" source-size="{{=size}}">{{=name}} ({{=byteSizeFormatter(size)}})</option>'),
            volumeCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/volumes?available=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            volumeTemplate: _.template('<option value="{{=id}}" source-size="{{=size}}">{{=name}} ({{=size}} GB)</option>'),
            /*volumeTemplate: _.template('<option value="{{=id}}" source-size="{{=size}}">{{=name}} ({{=size}}GiB)</option>'),*/
            snapshotCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/snapshots?available=true&id=' + id,
                parse: function(data) {
                    return data.rows;
                }
            })),
            snapshotTemplate: _.template('<option value="{{=id}}" source-size="{{=size}}">{{=name}} ({{=size}} GB)</option>'),
            /*snapshotTemplate: _.template('<option value="{{=id}}" source-size="{{=size}}">{{=name}} ({{=size}}GiB)</option>'),*/
            volumeTypeCollection: new (Backbone.Collection.extend({
                url : '/private/openstack/volumeTypes?id=' + id,
                /*parse: function(data) {
                    return data.rows;
                }*/
            })),
            volumeTypeTemplate: _.template('<option value="{{=id}}">{{=name}}</option>'),
            init: function () {
                var self = this;
                this.$el.find('.name').val('');
                this.$el.find('.description').val('');
                this.$el.find('.size').val('1');
                this.$el.find('#sourceType option:eq(0)').prop("selected", true);
                this.$el.find('#sourceType').selectric('refresh');
                this.changeSourceType();
                this.$el.find('#availabilityZone').empty();
                this.$el.find('#availabilityZone').attr("disabled", "disabled").selectric('refresh');
                this.zoneCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#availabilityZone').append(self.zoneTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#availabilityZone').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.$el.find('#image').empty();
                // this.$el.find('#image').append("<option value=''>이미지를 선택하세요</option>");
                this.$el.find('#image').append("<option value=''>" + i18n('s.t.select','w.image') + "</option>");
                this.$el.find('#image').attr("disabled", "disabled").selectric('refresh');
                this.imageCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#image').append(self.imageTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#image').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.$el.find('#volume').empty();
                // this.$el.find('#volume').append("<option value=''>볼륨을 선택하세요</option>");
                this.$el.find('#volume').append("<option value=''>" + i18n('s.t.select','w.volume') + "</option>");
                this.$el.find('#volume').attr("disabled", "disabled").selectric('refresh');
                this.volumeCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#volume').append(self.volumeTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#volume').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.$el.find('#snapshot').empty();
                // this.$el.find('#snapshot').append("<option value=''>스냅샷을 선택하세요</option>");
                this.$el.find('#snapshot').append("<option value=''>" + i18n('s.t.select','w.snapshot') + "</option>");
                this.$el.find('#snapshot').attr("disabled", "disabled").selectric('refresh');
                this.snapshotCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#snapshot').append(self.snapshotTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#snapshot').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.$el.find('#type').empty();
                // this.$el.find('#type').append("<option value='' > 볼륨 타입 없음</option>");
                this.$el.find('#type').append("<option value='' >" + i18n('w.no-volume-type') + "</option>");
                this.$el.find('#type').attr("disabled", "disabled").selectric('refresh');
                this.volumeTypeCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#type').append(self.volumeTypeTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#type').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            open: function () {
                this.init();
                // this.$el.draggable({handle: '.pop_tit', 'revert': true});
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
                this.$el.find('.name').focus();
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            changeSourceType: function() {
                var sourceType = this.$el.find('#sourceType option:selected').val();

                this.$el.find('#snapshotTr').hide();
                this.$el.find('#imageTr').hide();
                this.$el.find('#volumeTr').hide();
                this.$el.find('#typeTr').hide();
                this.$el.find('#zoneTr').hide();

                if(sourceType == "image") {
                    this.$el.find('#imageTr').show();
                    this.$el.find('#typeTr').show();
                    this.$el.find('#zoneTr').show();
                } else if(sourceType == "volume") {
                    this.$el.find('#volumeTr').show();
                } else if(sourceType == "snapshot") {
                    this.$el.find('#snapshotTr').show();
                } else {
                    this.$el.find('#typeTr').show();
                    this.$el.find('#zoneTr').show();
                }
            },
            save: function () {
                var self = this;

                if(this.$el.find('.name').val().trim() == "") {
                    // alert('이름을 입력하세요.');
                    alert(i18n('s.t.input','w.name'));
                    return;
                }

                var sourceType = this.$el.find('#sourceType option:selected').val();
                var type = this.$el.find('#type option:selected').val();
                var snapshot = this.$el.find('#snapshot option:selected').val();
                var volume = this.$el.find('#volume option:selected').val();
                var image = this.$el.find('#image option:selected').val();
                var availabilityZone = this.$el.find('#availabilityZone option:selected').val();
                var size = parseInt(this.$el.find('.size').val());
                var sourceSize = 0;

                var model = new Backbone.Model({
                    name: this.$el.find('.name').val(),
                    description: this.$el.find('.description').val(),
                    sourceType: sourceType
                });

                if(sourceType == "empty") {
                    model.set({
                        type: type,
                        size: size,
                        availabilityZone: availabilityZone
                    });
                } else if(sourceType == "snapshot") {

                    if(snapshot == "") {
                        // alert("스냅샷을 선택하세요.");
                        alert(i18n('s.t.select','w.snapshot'));
                        return;
                    }

                    sourceSize = parseInt($('#snapshot option:selected').attr('source-size'));

                    if(size < sourceSize) {
                        size = sourceSize;
                        this.$el.find('.size').val(sourceSize);
                    }

                    model.set({
                        sourceId: snapshot,
                        size: size
                    });
                } else if(sourceType == "volume") {

                    if(volume == "") {
                        // alert("볼륨을 선택하세요.");
                        // alert(i18n('s.t.select','w.volume'));
                        return;
                    }

                    sourceSize = parseInt($('#volume option:selected').attr('source-size'));

                    if(size < sourceSize) {
                        size = sourceSize;
                        this.$el.find('.size').val(sourceSize);
                    }

                    model.set({
                        sourceId: volume,
                        size: size
                    });
                } else if(sourceType == "image") {

                    if(image == "") {
                        // alert("이미지를 선택하세요.");
                        alert(i18n('s.t.select','w.image'));
                        return;
                    }

                    var sizeText = byteSizeFormatter($('#image option:selected').attr('source-size'));
                    var temp = sizeText.split(" ");
                    if(temp[1] == 'B' || temp[1] == 'KB' || temp[1] == 'MB') {
                        sourceSize = 1;
                    } else {
                        if(temp[1] == 'TB') {
                            sourceSize = (parseInt(temp[0].split('.')[0]) * 1024) + 1;
                        } else {
                            sourceSize = parseInt(temp[0].split('.')[0]) + 1;
                        }
                    }

                    if(size < sourceSize) {
                        size = sourceSize;
                        this.$el.find('.size').val(sourceSize);
                    }

                    model.set({
                        sourceId: image,
                        type: type,
                        size: size,
                        availabilityZone: availabilityZone
                    });
                }

                if(sourceType != 'empty') {
                    model.set({
                        sourceSize: sourceSize
                    });
                }

                model.url = '/private/openstack/volumes?id=' + id;

                showLoadingUI(true, i18n('s.t.please-wait'));
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
            modules.view = new VolumeView();
            modules.detailView = new VolumeDetailView();
            modules.createView = new CreateView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);
