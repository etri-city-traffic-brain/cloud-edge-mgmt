var RoleUI = (function (options) {

    var
        modules = {},
        RoleModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/auth/roles',
            defaults: {
                id: null,
                name: '',
                description: '',
                createdAt: '',
                creator: '',
                userCount: 0,
                permissionCount: 0
            }
        }),
        RoleCollection = Backbone.Collection.extend({
            model: RoleModel
        }),
        UserModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/users',
            url: function() {
                var url = "/auth/roles/" + this.get("roleId") + this.urlRoot;
                if(this.isNew()) {
                    return url
                } else {
                    return url + '/' + this.get(this.idAttribute);
                }
            },
            defaults: {
                id: null,
                newId: null,
                groupId: null,
                groupName: '',
                name: '',
                enabled: false,
                createdAt: '',
                email: '',
                contract: '',
                login: '',
                loginCount: 0,
                description: '',
                roleCount: 0
            }
        }),
        UserCollection = Backbone.Collection.extend({
            model: UserModel,
            parse: function(data) {
                return data.rows;
            }
        }),
        PermissionModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/permissions',
            url: function() {
                var url = "/auth/roles/" + this.get("roleId") + this.urlRoot;
                if(this.isNew()) {
                    return url
                } else {
                    return url + '/' + this.get(this.idAttribute);
                }
            },
            defaults: {
                id: null,
                type: '',
                roleId: null,
                createdAt: ''
            }
        }),
        PermissionCollection = Backbone.Collection.extend({
            model: PermissionModel
        }),
        RoleDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new RoleModel(),
            template: _.template('<div class="detail_graph">\n    <ul class="detail_graph_role">\n        <li class="st3">\n            <strong class="value">{{=userCount}}</strong>\n            <span class="">{{= jQuery.i18n.prop(\'title.jqgrid.userCount\') }}</span>\n        </li>\n        <li>\n            <strong class="value">{{=permissionCount}}</strong>\n            <span class="">{{= jQuery.i18n.prop(\'title.jqgrid.permissionCount\') }}</span>\n        </li>\n    </ul>\n</div>\n\n<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.description\') }}</th>\n            <td>{{= description }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.creator\') }}</th>\n            <td>{{= creator }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.createdAt\') }}</th>\n            <td>{{= createdAt }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('name'));
                    self.render();
                });
            }
        }),
        HistoryModel = Backbone.Model.extend({
            idAttribute: 'id',
            defaults: {
                id : '',
                tenantId : '',
                content : '',
                actionCode	: '',
                result : '',
                createdAt : '',
                updatedAt : '',
                resultDetail : '',
                userId : '',
                targetId : '',
                targetName : ''
            }
        }),
        HistoryCollection = Backbone.Collection.extend({
            model: HistoryModel
        }),
        HistoryView = Backbone.View.extend({
            el: "#tab4",
            events: {
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control": "resetGrid"
            },
            search: function() {
                this.grid.search('actionCode', this.$el.find('.sub_search').val());
            },
            searchEnter: function(e) {
                if(e.keyCode == 13) {
                    this.grid.search('actionCode', this.$el.find('.sub_search').val());
                }
            },
            resetGrid: function() {
                this.$el.find(".sub_search").val('');
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    // postData: {
                    //     filters: '{"groupOp":"AND","rules":[]}'
                    // }
                    postData: { q0: '', q1: ''}
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
                    //url: "/actions/openstack/servers/" + model.get('id') + "/volumes?id=" + id
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
                        $("#pager4").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');
                    }
                });
            }
        }),
        RoleView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click #role_create": "create",
                "click #role_update": "update",
                "click #role_delete": "delete",
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control":"resetGrid",
                "click .detail_label_btn":"closeDetail",
                "click .detail_tab a": "detailView"
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
                    page:1,
                    postData: { q0: '', q1: ''}
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
                        modules.detailUserView.render(m);
                    } else if(tabIndex == 2) {
                        modules.detailPermissionView.render(m);
                    } else if(tabIndex == 3) {
                        modules.detailHistoryView.render(m);
                    }
                } else {
                    var tabIndex = $('.detail_tab a.on').index();
                    if(tabIndex == 1) {
                        modules.detailUserView.grid.resetSize();
                    } else if(tabIndex == 2) {
                        modules.detailPermissionView.grid.resetSize();
                    } else if(tabIndex == 3) {
                        modules.detailHistoryView.reset();
                    }
                }
            },
            clearDetail: function() {
                modules.detailView.model.reset();
                modules.detailUserView.reset();
                modules.detailPermissionView.reset();
                modules.detailHistoryView.reset();
            },
            create: function() {
                modules.createView.open();
            },
            update: function() {
                var m = this.currentSelRow();
                if(m) {
                    modules.updateView.open(m);
                }
            },
            delete: function() {
                var self = this;
                var m = this.currentSelRow();
                if(m) {
                    if(confirm("정말 삭제 하시겠습니까?")) {
                        m.destroy({
                            success: function (model, response, options) {
                                self.clearDetail();
                            },
                            error :function (model, response, options) {
                                ValidationUtil.getServerError(response);
                            }
                        });
                    }
                }
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("Role 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#role-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/auth/roles',
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.description'),
                        jQuery.i18n.prop('title.jqgrid.userCount'),
                        jQuery.i18n.prop('title.jqgrid.permissionCount'),
                        jQuery.i18n.prop('title.jqgrid.creator'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.id')
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'description'},
                        {name: 'userCount', sorttype:'integer'},
                        {name: 'permissionCount', sorttype:'integer'},
                        {name: 'creator'},
                        {name: 'createdAt'},
                        {name: 'id', hidden: true}
                    ],
                    altRows: true,
                    sortname: "name",
                    sortorder: "asc",
                    // loadonce: true,
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
                        var tabIndex = $('.detail_tab a.on').index();
                        // if ($("#detailBtn").hasClass("selected")) {
                        modules.detailView.model.set(m.toJSON());
                        // }
                        if (tabIndex == 1) {
                            modules.detailUserView.render(m);
                        } else if (tabIndex == 2) {
                            modules.detailPermissionView.render(m);
                        } else if(tabIndex == 3) {
                            modules.detailHistoryView.render(m);
                        }
                        $('.content').addClass('detail_on');
                        setTimeout(function() {
                            self.grid.resetSize()
                        }, options.gridReSizeTime);
                    },
                    loadComplete: function (data) {
                        self.collection.reset(data.rows);
                        data.gridId = self.gridId;
                        data.getPageParam = function (data) {
                            return {
                                'q0': $(".select_search option:selected").val(),
                                'q1': $(".input_search").val()
                            }
                        };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager1").pager(data);
                        $("#role-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new RoleCollection();
                this.collection.on("add", function (model) {
                    modules.view.grid.addRowData(model.attributes.id, model.toJSON(), "first");
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
                "click .btn_pop_close": "close"
            },
            init: function () {
                this.$el.find('.name').val('');
                this.$el.find('.description').val('');
            },
            open: function () {
                this.init();
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            save: function () {
                var self = this;
                new RoleModel().save({
                        name: this.$el.find('.name').val(),
                        description: this.$el.find('.description').val()
                    },
                    {
                        success: function(model, response, options) {
                            modules.view.collection.add(model);
                            self.close();
                        },
                        error :function (model, response, options) {
                            ValidationUtil.getServerError(response);
                            self.close();
                        }
                    });
            }
        }),
        UpdateView = Backbone.View.extend({
            el: "#popupUpdate",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            init: function () {
                this.$el.find('.name').val(this.model.get('name'));
                this.$el.find('.description').val(this.model.get('description'));
            },
            open: function (model) {
                this.model = model;
                this.init();
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            save: function () {
                var self = this;

                this.model.save(
                    {
                        name: this.$el.find('.name').val(),
                        description: this.$el.find('.description').val()
                    },
                    {
                        success: function(model, response, options) {
                            modules.view.collection.add(model, {merge: true});
                            self.close();
                        },
                        error :function (model, response, options) {
                            ValidationUtil.getServerError(response);
                            self.close();
                        }
                    });
            }
        }),
        RoleUserView = Backbone.View.extend({
            el: "#tab2",
            events: {
                "click #role_user_create": "create",
                "click #role_user_delete": "delete",
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control":"resetGrid"
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
                    page:1,
                    postData: { q0: '', q1: ''}
                }).trigger("reloadGrid");
            },
            create: function() {
                modules.createUserView.open();
            },
            delete: function() {
                var m = this.currentSelRow();
                if(m) {
                    if(confirm("정말 삭제 하시겠습니까?")) {
                        var roleId = m.get('roleId');
                        m.destroy({
                            success: function(model, response, options) {
                                var roleModel = modules.view.collection.findWhere({id:roleId});
                                roleModel.set({userCount: (roleModel.get('userCount') - 1)});
                            },
                            error: function (model, response, options) {
                                ValidationUtil.getServerError(response);
                            }
                        });
                    }
                }
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("User 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
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
                    url: "/auth/roles/" + model.get('id') + "/users"
                }).trigger("reloadGrid");
            },
            initialize: function () {
                var self = this;

                this.collection = new UserCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#user-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.description'),
                        jQuery.i18n.prop('title.jqgrid.email'),
                        jQuery.i18n.prop('title.jqgrid.contract')
                    ],
                    colModel: [
                        {name: 'id'},
                        {name: 'name'},
                        {name: 'description'},
                        {name: 'email'},
                        {name: 'contract'}
                    ],
                    altRows: true,
                    sortname: "name",
                    sortorder: "asc",
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
                        data.getPageParam = function (data) {
                            return {
                                'q0': 'name',
                                'q1': self.$el.find('.sub_search')
                            }
                        };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager2").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');

                    }
                });
            }
        }),
        CreateUserListItemView = Backbone.View.extend({
            tagName: 'tr',
            template: _.template('<td><input type="checkbox" id="{{=id}}"><label for="{{=id}}"></label></td>\n<td>{{=id}}</td>\n<td>{{=name}}</td>\n<td>{{=description}}</td>\n<td>{{email}}</td>\n<td>{{contract}}</td>'),
            events: {
                "click input:checkbox": "change",
                "click td": "tdClick"
            },
            tdClick: function (e) {
                if(e.target.tagName === 'TD') this.$el.find("input:checkbox").click();
            },
            change: function () {
                var checked = this.$el.find("input:checkbox").is(':checked');
                this.model.set({checked: checked});
            },
            render: function () {
                this.model.set({
                    checked: false
                });
                this.$el.append( this.template(this.model.toJSON()));

                return this;
            }
        }),
        CreateUserView = Backbone.View.extend({

            el: "#popupUserCreate",
            dbCollection: new UserCollection(),
            collection: new UserCollection(),
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "click #checkboxAll": "allCheck",
                "click tbody input:checkbox": "change"
            },
            change: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(!checked) {
                    this.$el.find('#checkboxAll').prop('checked', false);
                } else {
                    var checkboxLength =  this.$el.find('tbody input:checkbox').length;
                    var checkedLength =  this.$el.find('tbody input:checkbox:checked').length;

                    if(checkboxLength == checkedLength) {
                        this.$el.find('#checkboxAll').prop('checked', true);
                    } else {
                        this.$el.find('#checkboxAll').prop('checked', false);
                    }
                }
            },
            allCheck: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    this.$el.find('tbody input:checkbox:not(:checked)').click();
                } else {
                    this.$el.find('tbody input:checkbox:checked').click();
                }
            },
            init: function () {
                var self = this;
                this.$el.find('#checkboxAll').prop('checked', false);
                this.$el.find("tbody").empty();
                this.dbCollection.url = '/auth/roles/' + modules.view.currentSelRow().get('id') + '/users?not=true';
                this.dbCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model) {
                            self.$el.find('tbody').append(new CreateUserListItemView({model: model}).render().el);
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            open: function () {
                var m = modules.view.currentSelRow();
                if(!m) return;
                this.init();
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
                this.dbCollection.reset();
                this.collection.reset();
            },
            save: function () {
                var self = this;
                var models = this.dbCollection.models;

                if(models.length == 0) {
                    this.close();
                    return;
                }

                _.each(models, function(model, index, list) {
                    if(model.get('checked') == true) {
                        var m = model.clone();
                        m.unset('checked');
                        self.collection.add(m);
                    }
                    if(index == (list.length - 1)) {
                        if(self.collection.length == 0) {
                            self.close();
                            return;
                        }

                        self.collection.url = '/auth/roles/' + modules.view.currentSelRow().get('id') + '/users';
                        self.collection.sync('create', self.collection, {
                            success: function (collection, message) {
                                modules.view.currentSelRow().set({userCount: collection.length});
                                modules.detailUserView.resetGrid();
                                self.close();
                            },
                            error: function (response, message) {
                                ValidationUtil.getServerError(response);
                                self.close();
                            }
                        });
                    }
                });
            }
        }),
        RolePermissionView = Backbone.View.extend({
            el: "#tab3",
            events: {
                "click #role_permission_create": "create",
                "click #role_permission_delete": "delete",
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control":"resetGrid"
            },
            search: function() {
                this.grid.search('type', this.$el.find('.sub_search').val());
            },
            searchEnter: function(e) {
                if(e.keyCode == 13) {
                    this.grid.search('type', this.$el.find('.sub_search').val());
                }
            },
            resetGrid: function() {
                this.$el.find(".sub_search").val('');
                this.grid.setGridParam({
                    page:1,
                    postData: { q0: '', q1: ''}
                }).trigger("reloadGrid");
            },
            create: function() {
                modules.createPermissionView.open();
            },
            delete: function() {
                var m = this.currentSelRow();
                if(m) {
                    if(confirm("정말 삭제 하시겠습니까?")) {
                        var roleId = m.get('roleId');
                        m.destroy({
                            success: function(model, response, options) {
                                var roleModel = modules.view.collection.findWhere({id:roleId});
                                roleModel.set({permissionCount: (roleModel.get('permissionCount') - 1)});
                            },
                            error: function (model, response, options) {
                                ValidationUtil.getServerError(response);
                            }
                        });
                    }
                }
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    alert("Permission 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
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
                    url: "/auth/roles/" + model.get('id') + "/permissions"
                }).trigger("reloadGrid");
            },
            initialize: function () {
                var self = this;

                this.collection = new PermissionCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#permission-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.createdAt')
                    ],
                    colModel: [
                        {name: 'id', hidden: true},
                        {name: 'type'},
                        {name: 'createdAt'}
                    ],
                    altRows: true,
                    sortname: "type",
                    sortorder: "asc",
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
                        data.getPageParam = function (data) {
                            return {
                                'q0': 'type',
                                'q1': self.$el.find('.sub_search')
                            }
                        };
                        data.rowNum = $(this).getGridParam("rowNum");
                        data.reccount = $(this).getGridParam("reccount");
                        $("#pager3").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');

                    }
                });
            }
        }),
        CreatePermissionListItemView = Backbone.View.extend({
            tagName: 'tr',
            template: _.template('<td><input type="checkbox" id="{{=type}}"><label for="{{=type}}"></label></td>\n<td>{{=type}}</td>'),
            events: {
                "click input:checkbox": "change",
                "click td": "tdClick"
            },
            tdClick: function (e) {
                if(e.target.tagName === 'TD') this.$el.find("input:checkbox").click();
            },
            change: function () {
                var checked = this.$el.find("input:checkbox").is(':checked');
                var m = modules.createPermissionView.collection.findWhere({type: this.model.get('type')});
                if(checked) {
                    if(!m) modules.createPermissionView.collection.add(this.model);
                } else {
                    if(!m) modules.createPermissionView.collection.remove(m);
                }
            },
            render: function () {
                this.$el.append( this.template(this.model.toJSON()));

                return this;
            }
        }),
        CreatePermissionView = Backbone.View.extend({
            el: "#popupPermissionCreate",
            collection: new PermissionCollection(),
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "click #checkboxAll_permission": "allCheck",
                "click tbody input:checkbox": "change"
            },
            permissions: [
                "CREDENTIAL_READ",
                "CREDENTIAL_WRITE",
                "CLOUD_READ",
                "CLOUD_WRITE",
                "GROUP_READ",
                "GROUP_WRITE",
                "USER_READ",
                "USER_WRITE",
                "ROLE_READ",
                "ROLE_WRITE"
            ],
            change: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(!checked) {
                    this.$el.find('#checkboxAll_permission').prop('checked', false);
                } else {
                    var checkboxLength =  this.$el.find('tbody input:checkbox').length;
                    var checkedLength =  this.$el.find('tbody input:checkbox:checked').length;

                    if(checkboxLength == checkedLength) {
                        this.$el.find('#checkboxAll_permission').prop('checked', true);
                    } else {
                        this.$el.find('#checkboxAll_permission').prop('checked', false);
                    }
                }
            },
            allCheck: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    this.$el.find('tbody input:checkbox:not(:checked)').click();
                } else {
                    this.$el.find('tbody input:checkbox:checked').click();
                }
            },
            init: function () {
                var self = this;
                this.$el.find('#checkboxAll_permission').prop('checked', false);
                this.$el.find("tbody").empty();
                var existPermissions = _.pluck(modules.detailPermissionView.collection.toJSON(), 'type');
                var newPermissions = _.difference(this.permissions, existPermissions);

                _.each(newPermissions, function(type) {
                    self.$el.find('tbody').append(new CreatePermissionListItemView({model: new PermissionModel({type: type})}).render().el);
                });

            },
            open: function () {
                var m = modules.view.currentSelRow();
                if(!m) return;
                this.init();
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
                this.collection.reset();
            },
            save: function () {
                var self = this;
                var models = this.collection.models;

                if(models.length == 0) {
                    this.close();
                    return;
                }

                this.collection.url = '/auth/roles/' + modules.view.currentSelRow().get('id') + '/permissions';
                this.collection.sync('create', this.collection, {
                    success: function (collection, message) {
                        modules.view.currentSelRow().set({permissionCount: collection.length});
                        modules.detailPermissionView.resetGrid();
                        self.close();
                    },
                    error: function (response, message) {
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            },
        }),
        init = function (isAdmin) {
            modules.view = new RoleView();
            modules.detailView = new RoleDetailView();
            modules.createView = new CreateView();
            modules.updateView = new UpdateView();
            modules.detailUserView = new RoleUserView();
            modules.createUserView = new CreateUserView();
            modules.detailPermissionView = new RolePermissionView();
            modules.createPermissionView = new CreatePermissionView();
            modules.detailHistoryView = new HistoryView();
        };

    return {
        init: init,
        modules: modules
    };
})(config);
