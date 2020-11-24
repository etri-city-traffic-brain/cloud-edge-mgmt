
var HistoryModel = Backbone.Model.extend({
    idAttribute: "id",
    urlRoot: '/actions',
    defaults:{
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
});

var HistoryCollection = Backbone.Collection.extend({
    model : HistoryModel
});

var historyCollection = new HistoryCollection();

/** jqGrid */
$("#history-grid").jqGrid({
    datatype: "local",
    url:"/actions",
    jsonReader: {
        repeatitems: false,
        id: "id"
    },
    colNames: [
        jQuery.i18n.prop('title.jqgrid.tenant'),
        'Type',
        jQuery.i18n.prop('title.jqgrid.user'),
        jQuery.i18n.prop('title.jqgrid.target'),
        jQuery.i18n.prop('title.jqgrid.action'),
        jQuery.i18n.prop('title.jqgrid.result'),
        jQuery.i18n.prop('title.jqgrid.detail'),
        jQuery.i18n.prop('title.jqgrid.time'),
        'Content',
        'Updated',
        jQuery.i18n.prop('title.jqgrid.id'),
        'targetName',
        'userId'
    ],
    colModel: [
        {name: 'tenantId', hidden: true, admin: false, align: 'left'},
        {name: 'type', hidden:true, align: 'left'},
        {name: 'userName', align: 'left', width:'100px'},
        {name: 'targetId', align: 'left', formatter: targetFormatter, width:'270px'},
        {name: 'actionCode', align: 'left', width:'150px'},
        {name: 'result', align: 'left', formatter: stateIconFormatter, width:'80px'},
        {name: 'resultDetail', align: 'left', width: '450px'},
        {name: 'createdAt', align: 'left', width:'120px'},
        {name: 'content', hidden:true, align: 'left'},
        {name: 'updatedAt', hidden:true, align: 'left'},
        {name: 'id', hidden:true, align: 'left'},
        {name: 'targetName', hidden:true, align: 'left'},
        {name: 'userId', hidden:true, align: 'left'}
    ],
    rowNum: setRowNum(10, "#history-grid"),
    sortname:"createdAt",
    sortorder:"desc",
    autowidth: true,
    gridComplete: function() { $(this).resetSize(); },
    scrollOffset:0,
    autoencode: true,
    loadtext: "",
    afterInsertRow: function(rowid, rowdata, options) {
        userNameFormatter(rowdata.userId, rowdata.userName, this, rowid);
    },
    loadComplete: function(data){
        self.collection.reset(data.rows);
        data.gridId = self.gridId;
        data.jump = true;
        data.getPageParam = function (data) {
            return {
                'q0': 'createdAt',
                'q1': self.$el.find('.sub_search')
            }
        };

        $("#pager2").pager(data);
        //$(".select_search").selectBoxIt().data("selectBox-selectBoxIt").selectOption($("#historyGrid").getGridParam('postData')['q0']);
    },
    onSelectRow: function(index) {
    }


});

/* jqGrid **/
/*
var HistoryUI = (function(options) {
    var
    modules = {},
    HistoryModel = Backbone.Model.extend({
        idAttribute: 'id',
        url: "/actions",
        defaults: {
            id : null,
            user_id : '',
            user_name : '',
            action : '',
            result : '',
            content : '',
            target : '',
            createdAt : '',
            ip : ''
        }
    }),
    HistoryCollection = Backbone.Collection.extend({
        model: HistoryModel
    }),
    HistoryView = Backbone.View.extend({
        el: "#history-view",
        events: {
        },
        initialize: function() {
            this.grid = "#history-grid";
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
            this.gridId = "#history-grid";
            this.grid = $(this.gridId).jqGrid({
                datatype: "json",
                url: '/actions',
                jsonReader: {
                    repeatitems: false,
                    id: "id"
                },
                colNames: [
                    jQuery.i18n.prop('title.jqgrid.tenant'),
                    'Type',
                    jQuery.i18n.prop('title.jqgrid.user'),
                    jQuery.i18n.prop('title.jqgrid.target'),
                    jQuery.i18n.prop('title.jqgrid.action'),
                    jQuery.i18n.prop('title.jqgrid.result'),
                    jQuery.i18n.prop('title.jqgrid.detail'),
                    jQuery.i18n.prop('title.jqgrid.time'),
                    'Content',
                    'Updated',
                    jQuery.i18n.prop('title.jqgrid.id'),
                    'targetName',
                    'userId'
                ],
                colModel: [
                    {name: 'tenantId', hidden: true, admin: false, align: 'left'},
                    {name: 'type', hidden:true, align: 'left'},
                    {name: 'userName', align: 'left', width:'100px'},
                    {name: 'targetId', align: 'left', formatter: targetFormatter, width:'270px'},
                    {name: 'actionCode', align: 'left', width:'150px'},
                    {name: 'result', align: 'left', formatter: stateIconFormatter, width:'80px'},
                    {name: 'resultDetail', align: 'left', width: '450px'},
                    {name: 'createdAt', align: 'left', width:'120px'},
                    {name: 'content', hidden:true, align: 'left'},
                    {name: 'updatedAt', hidden:true, align: 'left'},
                    {name: 'id', hidden:true, align: 'left'},
                    {name: 'targetName', hidden:true, align: 'left'},
                    {name: 'userId', hidden:true, align: 'left'}
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
                    // $("#user-grid tr:eq(1)").trigger('click');

                }
            });

            this.collection = new HistoryCollection();
            this.collection.on("add", function (model) {
                self.grid.addRowData(model.attributes.id, model.toJSON(), "first");
            });
            this.collection.on("change", function (model) {
                self.grid.setRowData(model.attributes.id, model.toJSON());
            });
            this.collection.on("remove", function(model) {
                self.grid.delRowData(model.get('id'));
            });
        }
    }),
    init = function (isAdmin) {
        modules.view = new HistoryView();
    };

    return {
        init: init,
        modules: modules
    };
})(config);

HistoryUI.init();*/
