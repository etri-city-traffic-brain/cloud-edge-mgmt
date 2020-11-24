var NetworkUI = (function (options) {

    var
        modules = {},
        NetworkModel = Backbone.Model.extend({
            idAttribute: 'id',
            urlRoot: '/private/openstack/networks?id=' + id,
            defaults: {
                id: null,
                name: '',
                neutronSubnets: [],
                shared: false,
                external: false,
                state: '',
                adminStateUp: false,
                visibilityZones: [],
                projectId: '',
                projectName: ''
            }
        }),
        NetworkCollection = Backbone.Collection.extend({
            model: NetworkModel
        }),
        SubnetModel = Backbone.Model.extend({
            idAttribute: 'id',
            defaults: {
                name: '',
                cidr: '',
                ipVersion: null,
                gateway: '',
                networkId: null,
                dhcpEnabled: false,
                dnsNames: {},
                allocationPools: {},
                hostRoutes: {}
            }
        }),
        SubnetCollection = Backbone.Collection.extend({
            model: SubnetModel
        }),
        NetworkDetailView = Backbone.View.extend({
            el: "#tab1",
            model: new NetworkModel(),
            template: _.template('<div class="detail_data">\n    <table class="tb_data">\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectID\') }}</th>\n            <td>{{= projectId }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.projectName\') }}</th>\n            <td>{{= projectName }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.id\') }}</th>\n            <td>{{= id }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.name\') }}</th>\n            <td>{{= name }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.subnetsAssociated\') }}</th>\n            <td>{{= getNeutronSubnetsText(neutronSubnets) }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.shared\') }}</th>\n            <td>{{= shared }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.external\') }}</th>\n            <td>{{= external }}</td>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.state\') }}</th>\n            <td>{{= state }}</td>\n        </tr>\n        <tr>\n            <th>{{= jQuery.i18n.prop(\'title.jqgrid.adminState\') }}</th>\n            <td>{{= adminStateUp }}</td>\n            <th>{{= jQuery.i18n.prop(\'w.accessibility-domain\') }}</th>\n            <td>{{= getVisibilityZonesText(visibilityZones) }}</td>\n        </tr>\n    </table>\n</div><!-- //detail_data -->'),
            initialize: function () {
                var self = this;
                this.model.on('change', function(model) {
                    $('.detail_tit').text(model.get('name'));

                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    self.render();

                    var model2 = new Backbone.Model({
                    });
                    model2.url = '/private/openstack/networks/' + m.get("id") + '?id=' + id;

                    model2.fetch(model2.attributes);
                });
            }
            //     events: {
            //         "click button.btn_action_refresh": "reload"
            //     }
        }),
        NetworkView = Backbone.View.extend({
            el: ".cont_wrap",
            events: {
                "click .cont_list .searchBtn": "search",
                "keyup .cont_list .input_search": "searchEnter",
                "click .cont_list .btn_control": "resetGrid",
                "click .detail_label_btn": "clearDetail",
                "click .detail_tab a": "detailView",
                "click #network_create": "create",
                "click #network_delete": "networkDelete",
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
                modules.subnetView.reset();
                this.closeDetail();
            },
            detailView: function () {
                var m = this.currentSelRow();
                if (m) {
                    var tabIndex = $('.detail_tab a.on').index();
                    if (tabIndex == 1) {
                        modules.subnetView.render(m);
                    }
                } else {
                    var tabIndex = $('.detail_tab a.on').index();
                    if (tabIndex == 1) {
                        modules.subnetView.reset();
                    }
                }
            },
            create: function () {
                modules.createView.open();
            },
            networkDelete: function () {
                var self = this;
                // if (confirm("네트워크를 삭제 하시겠습니까?")) {
                if (confirm(i18n('s.t.would-like-to', 'w.delete'))) {
                    var m = modules.view.currentSelRow();
                    if (!m) return;

                    var model = new Backbone.Model({
                        action: "DELETE"
                    });
                    model.url = '/private/openstack/networks/' + m.get('id') + '/delete?id=' + id;
                    // model.url = '/private/openstack/networks/' + m.get('id') + '?id=' + id;

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
                    alert("Network 정보가 선택되지 않았습니다.");
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;
                this.gridId = "#network-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "json",
                    url: '/private/openstack/networks?id=' + id,
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        jQuery.i18n.prop('title.jqgrid.projectName'),
                        jQuery.i18n.prop('title.jqgrid.name'),
                        jQuery.i18n.prop('title.jqgrid.subnetsAssociated'),
                        jQuery.i18n.prop('title.jqgrid.shared'),
                        jQuery.i18n.prop('title.jqgrid.external'),
                        jQuery.i18n.prop('title.jqgrid.state'),
                        jQuery.i18n.prop('title.jqgrid.adminState'),
                        // jQuery.i18n.prop('title.jqgrid.zone'),
                        jQuery.i18n.prop('w.accessibility-domain'),
                        jQuery.i18n.prop('title.jqgrid.projectId'),
                        jQuery.i18n.prop('title.jqgrid.id')
                    ],
                    colModel: [
                        {name: 'projectName'},
                        {name: 'name'},
                        {name: 'neutronSubnets', formatter: getNeutronSubnetsText, sorttype: function(cell, row) {
                                if(cell.length > 0) {
                                    return cell[0]['name'];
                                }
                                return cell;
                            }},
                        {name: 'shared'},
                        {name: 'external'},
                        {name: 'state', formatter: stateIconFormatter},
                        {name: 'adminStateUp'},
                        {name: 'visibilityZones', formatter: getVisibilityZonesText, sorttype: function(cell, row) {
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
                        var tabIndex = $('.detail_tab a.on').index();
                        modules.detailView.model.set(m.toJSON());
                        if (tabIndex == 1) {
                            modules.subnetView.render(m);
                        }
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
                        $("#network-grid tr:eq(1)").trigger('click');

                    }
                });

                this.collection = new NetworkCollection();
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
            },
            zoneCollection: new (Backbone.Collection.extend({
                url: '/private/openstack/zones?type=network&id=' + id,
                parse: function (data) {
                    return data.rows;
                }
            })),
            zoneTemplate: _.template('<option value="{{=zoneName}}">{{=zoneName}}</option>'),
            init: function () {
                var self = this;
                this.$el.find('.name').val('');
                this.$el.find('#adminStateUp').prop('checked', true);
                this.$el.find('#shared').prop('checked', false);
                this.$el.find('#availabilityZone').empty();
                this.$el.find('#availabilityZone').attr("disabled", "disabled").selectric('refresh');
                this.zoneCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function (model, index, list) {
                            if (model.get('resource') == "network") {
                                self.$el.find('#availabilityZone').append(self.zoneTemplate(model.toJSON()));
                            }
                            if (index == list.length - 1) {
                                self.$el.find('#availabilityZone').removeAttr("disabled").selectric('refresh');
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
            save: function () {
                var self = this;

                if (this.$el.find('.name').val().trim() == "") {
                    // alert('이름을 입력하세요.');
                    alert(i18n('s.t.input','w.name'));
                    return;
                }

                var model = new Backbone.Model({
                    name: this.$el.find('.name').val(),
                    adminStateUp: this.$el.find('#adminStateUp').is(":checked"),
                    shared: this.$el.find('#shared').is(":checked"),
                    availabilityZone: this.$el.find('#availabilityZone').val()
                });

                model.url = '/private/openstack/networks?id=' + id;

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
        SubnetView = Backbone.View.extend({
            el: "#tab2",
            events: {
                "click .sub_search_btn": "search",
                "keyup .sub_search": "searchEnter",
                "click .btn_control": "resetGrid",
                "click #network_subnet_create": "create",
                "click #network_subnet_delete": "delete"
            },
            search: function () {
                this.grid.search('name', this.$el.find('.sub_search').val());
            },
            searchEnter: function (e) {
                if (e.keyCode == 13) {
                    this.grid.search('name', this.$el.find('.sub_search').val());
                }
            },
            resetGrid: function () {
                this.$el.find(".sub_search").val('');
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    postData: {
                        filters: '{"groupOp":"AND","rules":[]}'
                    }
                }).trigger("reloadGrid");
            },
            reset: function () {
                this.collection.reset();
                this.grid.setGridParam({
                    datatype: "local",
                    page: 1,
                    url: ''
                });
                this.grid.clearGridData();
            },
            render: function (model) {
                this.collection.reset();
                this.grid.clearGridData();
                this.grid.setGridParam({
                    datatype: "json",
                    page: 1,
                    url: "/private/openstack/networks/" + model.get('id') + "/subnets?id=" + id
                }).trigger("reloadGrid");
            },
            create: function () {
                modules.createSubnetView.open();
            },
            delete: function () {
                var m = this.currentSelRow();
                if (m) {
                    // if (confirm("서브넷을 삭제 하시겠습니까?")) {
                    if (confirm(i18n('s.t.would-like-to', 'w.delete'))) {
                        var m2 = modules.view.currentSelRow();
                        if (!m2) return;

                        var model = new Backbone.Model({
                            action: "SUBNET_DELETE"
                        });
                        model.url = '/private/openstack/networks/' + m2.get('id') + '/subnets/' + m.get('id') + '?id=' + id;

                        showLoadingUI(true, i18n('s.t.please-wait'));
                        model.save(model.attributes, {
                            success: function (model, response, options) {
                                showLoadingUI(false);
                                modules.subnetView.collection.remove(model, {merge: true});
                            },
                            error: function (model, response, options) {
                                showLoadingUI(false);
                                ValidationUtil.getServerError(response);
                            }
                        });
                    }
                }
            },
            currentSelRow: function () {
                var selRow = this.grid.getGridParam("selrow");
                if (!selRow) {
                    // alert("서브넷 정보가 선택되지 않았습니다.");
                    alert(i18n('s.t.not-selected','w.subnet'));
                    return null;
                }
                return this.collection.get(selRow);
            },
            initialize: function () {
                var self = this;

                this.collection = new SubnetCollection();
                this.collection.on("add", function (model) {
                    self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
                });
                this.collection.on("remove", function (model) {
                    self.grid.delRowData(model.get('id'));
                });

                this.gridId = "#subnet-grid";
                this.grid = $(this.gridId).jqGrid({
                    datatype: "local",
                    jsonReader: {
                        repeatitems: false,
                        id: "id"
                    },
                    colNames: [
                        i18n('w.name'),
                        i18n('w.t.multi','w.network','w.address'),
                        i18n('w.t.multi','IP','w.version'),
                        i18n('w.t.multi','w.gateway','IP'),
                        "ID"
                    ],
                    colModel: [
                        {name: 'name'},
                        {name: 'cidr'},
                        {name: 'ipVersion'},
                        {name: 'gateway'},
                        {name: 'id', hidden: true}
                    ],
                    altRows: true,
                    sortname: "name",
                    sortorder: "asc",
                    loadonce: true,
                    autowidth: true,
                    width: 910,
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
                        $("#pager2").pager(data);
                        // $("#group-grid tr:eq(1)").trigger('click');

                    }
                });
            }
        }),
        CreateSubnetView = Backbone.View.extend({
            el: "#popupCreateSubnet",
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
            },
            init: function () {
                var self = this;
                // this.$el.find('.name').val('');
                // this.$el.find('#adminStateUp').prop('checked', true);
                // this.$el.find('#shared').prop('checked', false);
                // this.$el.find('#availabilityZone').empty();
                // this.$el.find('#availabilityZone').attr("disabled", "disabled").selectric('refresh');
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
            save: function () {
                var self = this;

                // if(this.$el.find('.name').val().trim() == "") {
                //     alert('이름을 입력하세요.');
                //     return;
                // }
                //
                // var model = new Backbone.Model({
                //     name: this.$el.find('.name').val(),
                //     adminStateUp: this.$el.find('#adminStateUp').is(":checked"),
                //     shared: this.$el.find('#shared').is(":checked"),
                //     availabilityZone: this.$el.find('#availabilityZone').val()
                // });
                //
                // model.url = '/private/openstack/networks?id=' + id;
                //
                // model.save(model.attributes, {
                //     success: function (model, response, options) {
                //         modules.view.collection.add(model, {merge: true});
                //         self.close();
                //     },
                //     error: function (model, response, options) {
                //         ValidationUtil.getServerError(response);
                //         self.close();
                //     }
                // });
            }
        }),
        init = function (isAdmin) {
            modules.view = new NetworkView();
            modules.detailView = new NetworkDetailView();
            modules.createView = new CreateView();
            modules.subnetView = new SubnetView();
            modules.createSubnetView = new CreateSubnetView();

        };

    return {
        init: init,
        modules: modules
    };
})(config);