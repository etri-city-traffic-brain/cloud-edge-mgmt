var UserUI = (function (options) {

    var
        modules = {},
        UserModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/auth/users',
            defaults: {
                id: null,
                newId: null,
                groupId: null,
                groupName: '',
                name: '',
                password: null,
                password2: null,
                enabled: false,
                createdAt: '',
                email: '',
                contract: '',
                login: '',
                loginCount: 0,
                description: '',
                admin: false,
                roleCount: 0
            },
            validate: function(attrs, options) {
                if(this.isNew()) {
                    if (!this.hasValue(attrs.newId)) {
                        return "아이디를 입력하세요.";
                    }
                    if (attrs.newId.length < 5 || attrs.newId.length > 15) {
                        return "아이디는 5자 이상 15자 미만이어야 합니다";
                    }
                    if (!attrs.newId.toString().match(this.patterns.id)) {
                        return "아이디는 영문과 숫자 조합만 가능합니다. \n첫글자는 영문이어합니다.";
                    }
                }
                if(!this.hasValue(attrs.name)) {
                    return "이름을 입력하세요.";
                }
                if(attrs.name.length < 1 || attrs.name.length > 45) {
                    return "이름은 45자 미만이어야 합니다.";
                }
                if(this.trim(attrs.email) !== '' && !attrs.email.toString().match(this.patterns.email)) {
                    return "이메일 형식이 잘못되었습니다.";
                }

                if(this.isNew() || (!this.isNew() && this.trim(attrs.password) !== '')) {
                    if (!this.hasValue(attrs.password)) {
                        return '비밀번호는 필수 항목입니다';
                    }
                    if (attrs.password.length < 8) {
                        return '비밀번호는 최소한 8자 이상 이여야 합니다.';
                    }
                    if (!this.hasValue(attrs.password2) || attrs.password !== attrs.password2) {
                        return '비밀번호를 다시 확인 해 주십시오';
                    }
                }
            }
        }),
        UserCollection = Backbone.Collection.extend({
            model: UserModel
        }),
        RoleModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/roles',
            url: function() {
                var url = "/auth/users/" + this.get("userId") + this.urlRoot;
                if(this.isNew()) {
                    return url
                } else {
                    return url + '/' + this.get(this.idAttribute);
                }
            },
            defaults: {
                id: null,
                name: '',
                description: '',
                createdAt: '',
                creator: '',
                userId: null
            }
        }),
        RoleCollection = Backbone.Collection.extend({
            model: RoleModel
        }),
        UserDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new UserModel(),
            template: _.template('<div class="detail_graph">\n    <ul class="detail_graph_role">\n        <li class="st3">\n            <strong class="value">{{=roleCount}}</strong>\n            <span class="">{{= jQuery.i18n.prop(\'title.jqgrid.roleCount\') }}</span>\n        </li>\n    </ul>\n</div>\n\n<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.groupId\') }}</th>\n            <td>{{= groupId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.group\') }}</th>\n            <td>{{= groupName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n              <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.email\') }}</th>\n            <td>{{= email }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.contract\') }}</th>\n            <td>{{= contract }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.lastLogin\') }}</th>\n            <td>{{= login }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.createdAt\') }}</th>\n            <td>{{= createdAt }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.description\') }}</th>\n            <td>{{= description }}</td>\n            <th></th>\n            <td></td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
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
            el: "#tab3",
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
                        $("#pager3").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');
                    }
                });
            }
        }),
        UserView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click #user_create": "create",
                "click #user_update": "update",
                "click #user_delete": "delete",
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
                        modules.detailRoleView.render(m);
                    } else if(tabIndex == 2) {
                        modules.detailHistoryView.render(m);
                    }
                } else {
                    var tabIndex = $('.detail_tab a.on').index();
                    if(tabIndex == 1) {
                        modules.detailRoleView.grid.resetSize();
                    }
                    else if(tabIndex == 2) {
                        modules.detailHistoryView.reset();
                    }
                }
            },
            clearDetail: function() {
                modules.detailView.model.reset();
                modules.detailRoleView.reset();
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
                    alert("User 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#user-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/auth/users',
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.group'),
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.description'),
                        jQuery.i18n.prop('title.jqgrid.email'),
                        jQuery.i18n.prop('title.jqgrid.contract'),
                        jQuery.i18n.prop('title.jqgrid.roleCount'),
                        // jQuery.i18n.prop('title.jqgrid.admin'),
                        // jQuery.i18n.prop('title.jqgrid.enabled'),
                        jQuery.i18n.prop('title.jqgrid.lastLogin'),
                        jQuery.i18n.prop('title.jqgrid.createdAt'),
                        jQuery.i18n.prop('title.jqgrid.groupId')
                    ],
                    colModel: [
                        {name: 'groupName'},
                        {name: 'id'},
                        {name: 'name'},
                        {name: 'description'},
                        {name: 'email'},
                        {name: 'contract'},
                        {name: 'roleCount'},
                        // {name: 'admin', formatter: getBooleanToYN},
                        // {name: 'enabled', formatter: getBooleanToYN},
                        {name: 'login'},
                        {name: 'createdAt'},
                        {name: 'groupId', hidden:true}
                    ],
                    altRows: true,
                    sortname: "name",
                    sortorder: "asc",
                    // loadonce: true,
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
                        var tabIndex = $('.detail_tab a.on').index();
                        // if ($("#detailBtn").hasClass("selected")) {
                        modules.detailView.model.set(m.toJSON());
                        // }
                        if (tabIndex == 1) {
                            modules.detailRoleView.render(m);
                        } else if(tabIndex == 2) {
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
                        $("#user-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new UserCollection();
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
                "click .btn_pop_close": "close"
            },
            groupCollection: new (Backbone.Collection.extend({
                url : '/auth/groups',
                parse: function(data) {
                    return data.rows;
                }
            })),
            groupTemplate: _.template('<option value="{{=id}}">{{=name}}</option>'),
            init: function () {
                var self = this;
                this.$el.find('.id').val('');
                this.$el.find('.name').val('');
                this.$el.find('.password').val('');
                this.$el.find('.password2').val('');
                this.$el.find('.email').val('');
                this.$el.find('.contract').val('');
                this.$el.find('.description').val('');
                this.$el.find("input:radio[name='admin']:radio[value='false']").prop('checked', true);
                this.$el.find('.group').empty();
                this.$el.find('.group').append(this.groupTemplate({id:'', name:'미지정'}));
                this.$el.find('.group').attr("disabled", "disabled").selectric('refresh');
                this.groupCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('.group').append(self.groupTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('.group').removeAttr("disabled").selectric('refresh');
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
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            save: function () {
                var self = this;

                var m = new UserModel({
                    newId: this.$el.find('.id').val(),
                    name: this.$el.find('.name').val(),
                    description: this.$el.find('.description').val(),
                    email: this.$el.find('.email').val(),
                    contract: this.$el.find('.contract').val(),
                    password: this.$el.find('.password').val(),
                    password2: this.$el.find('.password2').val(),
                    admin: this.$el.find('input:radio[name="admin"]:checked').val() === 'true',
                    groupId: this.$el.find('.group option:selected').val(),
                });
                m.on('invalid', ValidationUtil.invalid);


                if(m.isValid()){
                    ValidationUtil.hasUserId(m.get('newId'), {
                        exist: function () {
                            alert("이미 존재하는 아이디입니다.");
                        },
                        nexist: function () {
                            m.off('invalid');
                            m.unset('password2');
                            m.save(m.attributes,
                                {
                                    validate: false,
                                    success: function (model, response, options) {
                                        modules.view.collection.add(model);
                                        self.close();
                                    },
                                    error: function (model, response, options) {
                                        ValidationUtil.getServerError(response);
                                        self.close();
                                    }
                                });
                        }
                    });
                }
            }
        }),
        UpdateView = Backbone.View.extend({
            el: "#popupUpdate",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close"
            },
            init: function () {
                this.$el.find('.id').val(this.model.get('id'));
                this.$el.find('.name').val(this.model.get('name'));
                this.$el.find('.password').val('');
                this.$el.find('.password2').val('');
                this.$el.find('.email').val(this.model.get('email'));
                this.$el.find('.contract').val(this.model.get('contract'));
                this.$el.find('.description').val(this.model.get('description'));
                this.$el.find("input:radio[name='admin']:radio[value='"+this.model.get('admin')+"']").prop('checked', true);
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
                this.model.set(
                    {
                        name: this.$el.find('.name').val(),
                        description: this.$el.find('.description').val(),
                        email: this.$el.find('.email').val(),
                        contract: this.$el.find('.contract').val(),
                        password: this.$el.find('.password').val(),
                        password2: this.$el.find('.password2').val(),
                        admin: this.$el.find('input:radio[name="admin"]:checked').val() === 'true'
                    });

                this.model.on('invalid', ValidationUtil.invalid);

                if(this.model.isValid()){
                    this.model.off('invalid');
                    this.model.unset('password2');
                    this.model.save(this.model.attributes,
                        {
                            validate: false,
                            success: function (model, response, options) {
                                modules.view.collection.add(model, {merge: true});
                                self.close();
                            },
                            error: function (model, response, options) {
                                ValidationUtil.getServerError(response);
                                self.close();
                            }
                        });
                } else {
                    this.model.off('invalid');
                }
            }
        }),
        UserRoleView = Backbone.View.extend({
            el: "#tab2",
            events: {
                "click #user_role_create": "create",
                "click #user_role_delete": "delete",
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
                modules.createRoleView.open();
            },
            delete: function() {
                var m = this.currentSelRow();
                if(m) {
                    if(confirm("정말 삭제 하시겠습니까?")) {
                        var userId = m.get('userId');
                        m.destroy({
                            success: function(model, response, options) {
                                var userModel = modules.view.collection.findWhere({id:userId});
                                userModel.set({roleCount: (userModel.get('roleCount') - 1)});
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
                    alert("Role 정보가 선택되지 않았습니다.");
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
                    url: "/auth/users/" + model.get('id') + "/roles"
                }).trigger("reloadGrid");
            },
            initialize: function () {
                var self = this;

                this.collection = new RoleCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function(model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#role-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.id'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.description')
                    ],
                    colModel: [
                        {name: 'id', hidden: true},
                        {name: 'name'},
                        {name: 'description'}
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
        CreateRoleListItemView = Backbone.View.extend({
            tagName: 'tr',
            template: _.template('<td><input type="checkbox" id="{{=id}}"><label for="{{=id}}"></label></td>\n<td>{{=name}}</td>\n<td>{{=description}}</td>'),
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
        CreateRoleView = Backbone.View.extend({
            el: "#popupRoleCreate",
            dbCollection: new RoleCollection(),
            collection: new RoleCollection(),
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
                this.dbCollection.url = '/auth/users/' + modules.view.currentSelRow().get('id') + '/roles?not=true';
                this.dbCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model) {
                            self.$el.find('tbody').append(new CreateRoleListItemView({model: model}).render().el);
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

                        self.collection.url = '/auth/users/' + modules.view.currentSelRow().get('id') + '/roles';
                        self.collection.sync('create', self.collection, {
                            success: function (collection, message) {
                                modules.view.currentSelRow().set({roleCount: collection.length});
                                modules.detailRoleView.resetGrid();
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
        init = function (isAdmin) {
            modules.view = new UserView();
            modules.detailView = new UserDetailView();
            modules.createView = new CreateView();
            modules.updateView = new UpdateView();
            modules.detailRoleView = new UserRoleView();
            modules.createRoleView = new CreateRoleView();
            modules.detailHistoryView = new HistoryView();
        };

    return {
        init: init,
        modules: modules
    };
})(config);
