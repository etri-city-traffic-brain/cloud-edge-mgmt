var createServerUI = (function (options) {
    var
        modules = {},

        credentialId = Backbone.Model.extend({
            defaults:{
                etriId: null,
                rexgenId: null,
                toastId: null
            }
        }),

        rexgenServerModel = Backbone.Model.extend({
        }),
        rexgenServerCollection = Backbone.Collection.extend({
            url: '/edge/rexgen/servers?',
            model: rexgenServerModel
        }),

        etriServerModel = Backbone.Model.extend({
        }),
        etriServerCollection = Backbone.Collection.extend({
            url: '/edge/etri/servers?',
            model: etriServerModel
        }),

        toastServerModel = Backbone.Model.extend({
        }),
        toastServerCollection = Backbone.Collection.extend({
            url: '/public/toast/servers?',
            model: toastServerModel
        }),

        createServer = Backbone.View.extend({
            el: ".cont_wrap",
            credentialCollection: new (Backbone.Collection.extend({
                url: '/management/credentials/names'
            })),
            events: {
                "click .createServer": "create",
                "change #regionType": "setRegion"
            },

            regionValue: null,

            etriSize: 0,
            rexgenSize: 0,
            toastSize: 0,


            initialize: function(){
                this.$el.find('#regionType option:eq(0)').prop("selected", true);
                this.regionValue = this.$el.find("#regionType option:selected").val();
            },

            setRegion: function(){
                this.regionValue = this.$el.find("#regionType option:selected").val();
            },

            setId:function(){
                var mId = modules.credentialId;
                this.credentialCollection.fetch({
                    async: false,
                    success: function (collection){
                        collection.each(function (model) {
                            if (model.get("type") === "etri") {
                                mId.set({etriId: model.get("id")});
                            }
                            if (model.get("type") === "rexgen") {
                                mId.set({rexgenId: model.get("id")});
                            }
                            if (model.get("type") === "toast") {
                                mId.set({toastId: model.get("id")});
                            }
                        })
                    }
                })
            },

            setSize: function(){
                var self = this;
                var mId = modules.credentialId;

                var etriCollection = new etriServerCollection();
                etriCollection.fetch({
                    async: false,
                    data:{id: mId.get("etriId")},
                    processData: true,
                    context: etriCollection
                }).done(function () {
                    self.etriSize = parseInt(this.length);
                })

                var rexgenCollection = new rexgenServerCollection();
                rexgenCollection.fetch({
                    async: false,
                    data:{id: mId.get("rexgenId")},
                    processData: true,
                    context: rexgenCollection
                }).done(function () {
                    self.rexgenSize = parseInt(this.length);
                })

                var toastCollection = new toastServerCollection();
                toastCollection.fetch({
                    async: false,
                    data:{id: mId.get("toastId")},
                    processData: true,
                    context: toastCollection
                }).done(function () {
                    self.toastSize = parseInt(this.length);
                })

            },

            create: function () {
                var self = this;
                var mId = modules.credentialId;
                this.setId();
                this.setSize();

                var minServerValue = Math.min(self.etriSize, self.rexgenSize, self.toastSize);

                // 분류 알고리즘
                if(this.regionValue === "rexgen"){
                    this.rexgenServerCreate(mId.get("rexgenId"));
                }else{
                    if(minServerValue === parseInt(this.etriSize)){
                        this.etriServerCreate(mId.get("etriId"));
                    }else if(minServerValue === parseInt(this.rexgenSize)){
                        this.rexgenServerCreate(mId.get("rexgenId"));
                    }else{
                        this.toastServerCreate(mId.get("toastId"));
                    }
                }
            },

            etriServerCreate: function (etriId) {
                modules.etriCreateView.open(etriId);
            },

            rexgenServerCreate: function (rexgenId) {
                modules.rexgenCreateView.open(rexgenId);
            },

            toastServerCreate: function (toastId) {
                modules.toastCreateView.open(toastId);
            },
        }),

        etriCreateView = Backbone.View.extend({

            sid: null,

            el: "#popupCreate_etri",
            imageCollection: new (Backbone.Collection.extend({
                url : '/edge/etri/images?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            imageTemplate: _.template('<tr>\n    <td><input type="checkbox" class="image" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= imageName }}</td>\n    <td>{{= osType }}</td>\n    <td>{{= architecture }}</td>\n    <td>{{= hypervisor }}</td>\n    <td>{{= virtualizationType }}</td>\n    <td>{{= rootDeviceType }}</td>\n</tr>'),
            publicImageCollection: new (Backbone.Collection.extend({
                url : '/edge/etri/publicimages?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            publicImageTemplate: _.template('<tr>\n    <td><input type="checkbox" class="image" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= osType }}</td>\n    <td>{{= architecture }}</td>\n    <td>{{= hypervisor }}</td>\n    <td>{{= virtualizationType }}</td>\n    <td>{{= rootDeviceType }}</td>\n</tr>'),
            flavorCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            flavorTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= instanceType }}" value="{{= instanceType }}"><label for="{{= instanceType }}"></label></td>\n    <td>{{= instanceFamily }}</td>\n    <td>{{= instanceType }}</td>\n    <td>{{= vcpu }}</td>\n    <td>{{= memory }}</td>\n    <td>{{= storage }}</td>\n    <td>{{= networkPerformance }}</td>\n</tr>'),
            securityGroupCollection: new (Backbone.Collection.extend({
                url : '/edge/etri/securitygroups?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            securityGroupTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= groupId }}" value="{{= groupName }}"><label for="{{= groupId }}"></label></td>\n    <td>{{= groupId }}</td>\n    <td>{{= groupName }}</td>\n    <td>{{= description }}</td>\n</tr>'),
            keypairCollection: new (Backbone.Collection.extend({
                url : '/edge/etri/keypairs?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            keypairTemplate: _.template('<option value="{{=name}}">{{=name}}</option>'),
            networkCollection: new (Backbone.Collection.extend({
                url : '/edge/etri/vpcs?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            networkTemplate: _.template('<option value="{{=id}}">{{=id}}</option>'),
            subnetCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            subnetTemplate: _.template('<option value="{{=id}}">{{=id}}</option>'),

            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "click .btn_next": "next",
                "click td": "tdClick",
                "change #image_etri": "imageSelect",
                "change #network_etri": "networkSelect",
                "click #pop_tab_etri1 tbody input:checkbox": "oneClick",
                "click #pop_tab_etri2 tbody input:checkbox": "oneClick",
                "click #pop_tab_etri3 tbody input:checkbox": "oneClick",
                "click #pop_tab_etri4 tbody input:checkbox": "oneClick",
                "change #pop_tab_etri5 tbody input:file": "displayFile",
                "keyup #pop_tab_etri5 tbody textarea": "checkSize",
                "click .pop_tab_etri a:eq(1)" : "imageTypeTabClick"
            },

            initialize: function(){
                var self = this;
                var btn = $('#popupCreate_etri .pop_btns').children();
                this.$el.find('.pop_tab_etri a').on('click', function () {
                    if($(this).index() == self.$el.find('.pop_tab_etri a').length - 1) {
                        btn.eq(0).show();
                        btn.eq(1).hide();
                    }
                    else {
                        btn.eq(0).hide();
                        btn.eq(1).show();
                    }
                });
            },

            init: function (etriId) {
                var self = this;
                this.$el.find('.name').val('');
                this.$el.find('.instanceCount').val('');
                this.$el.find('#pop_tab_etri1 table tbody').empty();
                this.$el.find('#pop_tab_etri2 table tbody').empty();
                this.$el.find('#pop_tab_etri3 table tbody').empty();
                this.$el.find('.pop_tab_etri a:eq(0)').click();
                this.$el.find('#pop_tab_etri4 select').empty();
                this.$el.find('#pop_tab_etri1 select option:eq(0)').prop("selected", true);
                this.$el.find('#pop_tab_etri1 select').selectric('refresh');
                this.$el.find('#pop_tab_etri2 input:checkbox').prop('checked', false);
                this.$el.find('#pop_tab_etri3 input:checkbox').prop('checked', false);
                this.$el.find('#network_etri').attr("disabled", "disabled").selectric('refresh');
                this.$el.find('#subnet_etri').attr("disabled", "disabled").selectric('refresh');
                this.$el.find('#keypair_etri').attr("disabled", "disabled").selectric('refresh');
                this.$el.find("input:radio[name='monitoring']:radio[value='false']").prop('checked', true);
                this.$el.find("#pop_tab_etri5 tbody textarea").val('');
                this.$el.find('#pop_tab_etri5 input:checkbox').prop('checked', false);
                this.$el.find("#pop_tab_etri1 #custom_img_table_etri").hide();
                this.$el.find("#pop_tab_etri1 #public_img_table_etri").show();

                self.sid = etriId;
                this.imageCollection.url = '/edge/etri/images?id=' + self.sid;
                this.imageCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_etri1 table:eq(0) tbody').append(self.imageTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.publicImageCollection.url = '/edge/etri/publicimages?id=' + self.sid;
                this.publicImageCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_etri1 table:eq(1) tbody').append(self.publicImageTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.securityGroupCollection.url = '/edge/etri/securitygroups?id=' + self.sid;
                this.securityGroupCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_etri3 table:eq(0) tbody').append(self.securityGroupTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.keypairCollection.url = '/edge/etri/keypairs?id=' +self.sid;
                this.keypairCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#keypair_etri').append(self.keypairTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#keypair_etri').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.networkCollection.url = '/edge/etri/vpcs?id=' +self.sid;
                this.networkCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#network_etri').append(self.networkTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#network_etri').removeAttr("disabled").selectric('refresh');
                            }
                        });

                        self.subnetCollection.url = "/edge/etri/subnets/" + self.$el.find('#network_etri option:selected').val() + "?id=" + self.sid;

                        self.subnetCollection.fetch({
                            success: function (collection, response, options) {
                                self.$el.find('#subnet_etri').append(self.subnetTemplate({id:"Default Subnet"}));
                                _.each(collection.models, function(model, index, list) {
                                    self.$el.find('#subnet_etri').append(self.subnetTemplate(model.toJSON()));
                                    if(index == list.length - 1) {
                                        self.$el.find('#subnet_etri').removeAttr("disabled").selectric('refresh');
                                    }
                                });
                            },
                            error: function (collection, response, options) {
                                ValidationUtil.getServerError(response);
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                // this.$el.find('select option:selected').val()
            },

            open: function (etriId) {
                this.init(etriId);
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },

            close: function () {
                this.$el.fadeOut(100);
            },

            next: function () {
                var currentIndex = this.$el.find('.pop_tab_etri a.on').index();
                var tabLength = this.$el.find('.pop_tab_etri a').length;

                if(currentIndex == 0) {
                    var checkedVal = this.$el.find('#pop_tab_etri1 tbody input:checkbox:checked').val();
                    if(!ValidationUtil.hasValue(checkedVal)) {
                        alert('Image를 선택하세요.');
                        return;
                    }
                }

                if(currentIndex != tabLength - 1) {
                    $('.pop_tab_etri a:eq(' + (currentIndex + 1) + ')').click();
                } else {
                    // alert('마지막 페이지 입니다.');
                    alert(i18n('s.last-page'));
                }
            },

            imageTypeTabClick: function() {
                var checkedVal = this.$el.find('#pop_tab_etri1 tbody input:checkbox:checked').val();
                if(!ValidationUtil.hasValue(checkedVal)) {
                    // alert('Image를 선택하세요.');
                    alert(i18n('s.t.select','w.image'));
                    $('.pop_tab_etri a:eq(0)').click();
                    return;
                }
            },

            tdClick: function (e) {
                if(e.target.tagName === 'TD') this.$el.find(e.currentTarget).parent().children('td:eq(0)').children('input:checkbox').click();
            },

            imageSelect: function() {
                var self = this;
                var selImageType = self.$el.find("#image_etri option:selected").val();
                if(selImageType == "public") {
                    self.$el.find("#custom_img_table_etri").hide();
                    self.$el.find("#public_img_table_etri").show();
                } else if(selImageType == "custom") {
                    self.$el.find("#public_img_table_etri").hide();
                    self.$el.find("#custom_img_table_etri").show();
                }
            },

            networkSelect: function () {
                var self = this;
                this.subnetCollection.url = "/edge/etri/subnets/" + this.$el.find('#network_etri option:selected').val() + "?id=" + self.sid;
                this.subnetCollection.fetch({
                    success: function (collection, response, options) {
                        self.$el.find('#subnet_etri').empty();
                        //self.$el.find('subnet').append(self.subnetTemplate({id:""}));
                        self.$el.find('#subnet_etri').attr("disabled", "disabled").selectric('refresh');
                        self.$el.find('#subnet_etri').append(self.subnetTemplate({id:"Default Subnet"}));
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#subnet_etri').append(self.subnetTemplate(model.toJSON()));
                            if(index == list.length - 1) {
                                self.$el.find('#subnet_etri').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },

            oneClick: function (e) {
                var self = this;

                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    self.$el.find(e.currentTarget).parents('.tb_wrap').find('input:checkbox').prop('checked', false);
                    $(e.currentTarget).prop('checked', true);
                }

                // 이미지 선택 시 이미지 OS를 기준으로 Flavor 목록 가져오기
                if($(e.currentTarget).hasClass("image")) {
                    if(checked) {
                        var checkedRow = $(e.currentTarget).parent().parent();
                        var osType = checkedRow.children().eq(2).text();
                        self.getFlavor(osType);
                    } else {
                        self.$el.find('#pop_tab_etri2 table:eq(0) tbody').empty();
                    }
                }
            },

            multiClick: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(!checked) {
                    this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', false);
                } else {
                    var checkboxLength =  this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox').length;
                    var checkedLength =  this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:checked').length;

                    if(checkboxLength == checkedLength) {
                        this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', true);
                    } else {
                        this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', false);
                    }
                }
            },

            allCheck: function(e) {
                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:not(:checked)').click();
                } else {
                    this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:checked').click();
                }
            },

            getFlavor: function(osType) {
                var self = this;
                self.flavorCollection.url = "/edge/etri/flavors?osType="+osType+"&id="+self.sid;
                self.flavorCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_etri2 table:eq(0) tbody').append(self.flavorTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },

            displayFile: function(e) {
                var self = this;
                var fileList = this.$el.find("#pop_tab_etri5 tbody input:file")[0].files;

                // 읽기
                var reader = new FileReader();
                reader.readAsText(fileList [0]);

                //로드 한 후
                reader.onload = function  () {
                    self.$el.find("#pop_tab_etri5 tbody textarea").val(reader.result);
                    self.$el.find("#pop_tab_etri5 tbody input:file").val('');
                    self.checkSize();
                };
            },

            checkSize: function(e) {
                var textFileAsBlob = new Blob([ this.$el.find("#pop_tab_etri5 tbody textarea").val() ], { type: 'text/plain' });
                var size = textFileAsBlob.size;

                if(size > 16384) {
                    this.$el.find("#pop_tab_etri5 tbody td span").css("color", "#ff0000");
                } else {
                    this.$el.find("#pop_tab_etri5 tbody td span").css("color", "#667285");
                }

                this.$el.find("#pop_tab_etri5 tbody span").html("Content size: "+ this.getSizeToFixed(size) +" bytes of 16.00 KB");
            },

            getSizeToFixed: function(size) {
                if(size < 1024) {
                    return size + ' bytes';
                } else if(size < 1048576) {
                    return (size/1024).toFixed(2) + ' KB';
                } else if(size < 1073741824) {
                    return (size/1048576).toFixed(2) + ' MB';
                } else if(size < 1099511627776) {
                    return (size/1073741824).toFixed(2) + ' GB';
                } else if(size < 1125899906842624) {
                    return (size/1099511627776).toFixed(2) + ' TB';
                } else if(size < 4503599627370496) {
                    return (size/1125899906842624).toFixed(2) + ' PB';
                }
            },

            save: function () {
                var self = this;
                var networks = [];
                var securityGroups = [];

                var networkSelects = this.$el.find('#pop_tab_etri4 tbody input:checkbox:checked');
                var securityGroupSelects = this.$el.find('#pop_tab_etri5 tbody input:checkbox:checked');
                if(networkSelects.length > 0) {
                    _.each(networkSelects, function(input) {
                        networks.push($(input).val());
                    });
                }
                if(securityGroupSelects.length > 0) {
                    _.each(securityGroupSelects, function(input) {
                        securityGroups.push($(input).val());
                    });
                }

                var createDate = {
                    name: this.$el.find('.name').val(),
                    instanceCount: this.$el.find('.instanceCount').val(),
                    imageId: this.$el.find('#pop_tab_etri1 tbody input:checkbox:checked').val(),
                    instanceType: this.$el.find('#pop_tab_etri2 tbody input:checkbox:checked').val(),
                    securityGroup: this.$el.find('#pop_tab_etri3 tbody input:checkbox:checked').val(),
                    subnet: this.$el.find('#subnet_etri option:selected').val(),
                    keypair: this.$el.find('#keypair_etri option:selected').val(),
                    monitoringEnabled: this.$el.find('input:radio[name="monitoring"]:checked').val() === 'true',
                    subnetId: this.$el.find('#subnet_etri option:selected').val(),
                    base64Encoded: this.$el.find('#pop_tab_etri5 input:checkbox').is(':checked')
                };

                if(!ValidationUtil.hasValue(createDate.imageId)) {
                    alert(i18n('s.t.select','w.image'));
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.instanceType)) {
                    alert(i18n('s.t.select',i18n('w.t.multi','w.instance','w.type')));
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.securityGroup)) {
                    alert(i18n('s.t.select','w.security-group'));
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.name)) {
                    alert(i18n('s.t.input','w.name'));
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.instanceCount)) {
                    alert(i18n('s.t.input',i18n('w.t.number','w.instance')));
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.keypair)) {
                    alert(i18n('s.t.select','w.keypair'));
                    return;
                }
                if(ValidationUtil.hasValue(this.$el.find("#pop_tab_etri5 tbody textarea").val())) {
                    var textFileAsBlob = new Blob([ this.$el.find("#pop_tab_etri5 tbody textarea").val() ], { type: 'text/plain' });
                    var size = textFileAsBlob.size;

                    if(size > 16384) {
                        alert(i18n('s.custom-script-over'));
                        return;
                    } else {
                        createDate.script = this.$el.find("#pop_tab_etri5 tbody textarea").val();
                    }
                }
                var m = new (Backbone.Model.extend({
                }));
                m.url = "/edge/etri/servers?id=" + self.sid;
                m.save(createDate, {
                    async: false,
                    success: function(model, response, options) {
                        alert(i18n('etri 서버 생성 완료.'));
                        // modules.view.collection.add(model, {merge: true});
                        self.close();
                    },
                    error :function (model, response, options) {
                        alert(i18n('etri 서버 생성 실패.'));
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),

        rexgenCreateView = Backbone.View.extend({
            el: "#popupCreate_rexgen",
            sid: null,
            regionCollection: new (Backbone.Collection.extend({
                url: '/edge/rexgen/regions?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            regionTemplate: _.template('<option value="{{=name}}">{{=label}}</option>'),
            resourceGroupCollection : new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            resourceGroupTemplate: _.template('<option value="{{=name}}">{{=name}}</option>'),
            imageCollection: new (Backbone.Collection.extend({
                url : '/edge/rexgen/images?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            imageTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= osState }}</td>\n    <td>{{= osType }}</td>\n    <td>{{= diskSizeGB }}GB</td>\n    <td>{{= storageAccountType }}</td>\n</tr>'),
            publicImageCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            publicImageTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= osType }}</td>\n    <td>{{= publisher }}</td>\n</tr>'),
            sizeCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            sizeTemplate: _.template('<tr>\n    <td><input type="checkbox" id="{{= offering }}_{{= vmSize }}" value="{{= offering }}_{{= vmSize }}"><label for="{{= offering }}_{{= vmSize }}"></label></td>\n    <td>{{= offering }}</td>\n    <td>{{= vmSize }}</td>\n    <td>{{= family }}</td>\n    <td>{{= vcpus }}</td>\n    <td>{{= memoryGB }}</td>\n    <td>{{= maxDataDiskCount }}</td>\n    <td>{{= premiumIO }}</td>\n</tr>'),
            networkCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            networkTemplate: _.template('<option value="{{=id}}">{{=name}} ({{=addressSpaces[0]}})</option>'),
            publicIPCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            publicIPTemplate: _.template('<option value="{{=id}}">{{=ipAddress}} ({{=name}})</option>'),
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "click .btn_next": "next",
                "click td": "tdClick",
                "click #pop_tab_rexgen2 input:radio":"resourceGroupType",
                "click #pop_tab_rexgen3 tbody input:checkbox": "oneClick",
                "click #pop_tab_rexgen4 tbody input:checkbox": "oneClick",
                "click #pop_tab_rexgen5 tr:eq(0) input:radio":"networkType",
                "click #pop_tab_rexgen5 tr:eq(3) input:radio":"publicIPType",
                "click #pop_tab_rexgen5 tr:eq(6) input:radio":"inboundPortType",
                "click thead input:checkbox": "allCheck",
                "change #region_rexgen": "regionChange",
                "change #resourcegroup_list_rexgen": "getNetworks",
                "change #image_rexgen": "imageSelect",
                "change #pop_tab_rexgen6 tbody input:file": "displayFile",
                "keyup #pop_tab_rexgen6 tbody textarea": "checkSize"
            },
            initialize: function() {
                var self = this;
                var button = $('#popupCreate_rexgen .pop_btns').children();
                this.$el.find('.pop_tab_rexgen a').on('click', function () {
                    if($(this).index() === self.$el.find('.pop_tab_rexgen a').length - 1) {
                        button.eq(0).show();
                        button.eq(1).hide();
                    }
                    else {
                        button.eq(0).hide();
                        button.eq(1).show();
                    }
                });
            },
            init: function (rexgenId) {
                var self = this;
                this.$el.find('.name').val('');
                this.$el.find('.username').val('');
                this.$el.find('.password').val('');
                this.$el.find('.pop_tab_rexgen a:eq(0)').click();
                this.$el.find('#pop_tab_rexgen2 table tr:eq(0) input:first').click();
                this.$el.find('#pop_tab_rexgen2 select').empty();
                this.$el.find('#pop_tab_rexgen3 table tbody').empty();
                this.$el.find('#pop_tab_rexgen4 table tbody input').prop("checked", false);
                this.$el.find('#pop_tab_rexgen5 table tr:eq(0) input:first').click();
                this.$el.find('#pop_tab_rexgen5 table tr:eq(3) input:first').click();
                this.$el.find('#pop_tab_rexgen5 table tr:eq(6) input:first').click();
                this.$el.find('#pop_tab_rexgen5 select').empty();
                this.$el.find("#pop_tab_rexgen3 #custom_img_table_rexgen").hide();
                self.sid = rexgenId;

                this.regionCollection.fetch({
                    async: false,
                    data: {id: self.sid},
                    processData: true,
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_rexgen1 select').append(self.regionTemplate(model.toJSON()));
                            if(index === list.length - 1 && self.$el.find("#pop_tab_rexgen1 select").val("koreacentral").prop("selected", !0)) {
                                self.$el.find('#pop_tab_rexgen1 select').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                this.imageCollection.fetch({
                    async: false,
                    data: {id: self.sid},
                    processData: true,
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_rexgen3 table:eq(0) tbody').append(self.imageTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
                /*this.networkCollection.fetch({
                    success: function (collection, response, options) {
                        var resourceGroup = self.$el.find("#pop_tab_etri2 select option:selected").val();
                        _.each(collection.models, function(model, index, list) {
                            if(model.get('resourceGroupName') == resourceGroup) {
                                self.$el.find('#pop_tab_etri5 table select:eq(0)').append(self.networkTemplate(model.toJSON()));
                            }
                            if(index == list.length - 1) {
                                self.$el.find('#pop_tab_etri5 table select:eq(0)').selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });*/
                /*this.publicIPCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            if(model.get("associatedTo") == null){
                                self.$el.find('#pop_tab_etri5 table select:eq(1)').append(self.publicIPTemplate(model.toJSON()));
                            }
                            if(index == list.length - 1) {
                                self.$el.find('#pop_tab_etri5 table select:eq(1)').removeAttr("disabled").selectric('refresh');
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });*/
                self.getResourceGroups();
                self.getPublicImages("koreacentral");
                self.getSizes("koreacentral");
                self.getNetworks();
            },
            open: function (rexgenId) {
                this.init(rexgenId);
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            pw_check : function(){
                ischeck=false;
                var pw=this.$el.find('.password').val();
                if (pw.length>=6 && pw.length<72 ){
                    var pwformat = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*(_|[^\w])).+$/;
                    if(!pw.match(pwformat)){
                        alert("비밀번호는 대문자,소문자,숫자,특수문자로 구성해야합니다.");
                        this.$el.find('.password').focus();
                        ischeck=false;
                    }
                    else{
                        ischeck=true;
                    }
                }
                else{
                    alert("최소 6자리 이상 72자리미만 입력하세요");
                    ischeck=false;
                    this.$el.find('.password').focus();
                }
            },
            next: function () {
                var currentIndex = this.$el.find('.pop_tab_rexgen a.on').index();
                var tabLength = this.$el.find('.pop_tab_rexgen a').length;
                if(currentIndex===0){
                    this.pw_check();
                }
                if(currentIndex === 1) {
                    //this.$el.find('#pop_tab_etri1 table tbody input').prop("checked");
                }
                if(currentIndex !== tabLength - 1) {
                    if(ischeck) {
                        $('.pop_tab_rexgen a:eq(' + (currentIndex + 1) + ')').click();
                    }
                } else {
                    alert(i18n("s.last-page"));
                }
            },
            resourceGroupType : function(e){
                if(this.$el.find(e.currentTarget).val() === "exist"){
                    this.$el.find('#pop_tab_rexgen2 table tr:eq(1) input').val("");
                    this.$el.find('#pop_tab_rexgen2 table tr:eq(1)').hide();
                    this.$el.find('#pop_tab_rexgen2 table tr:eq(2)').show();
                    this.$el.find('#pop_tab_rexgen2 table tr:eq(2) select').attr("disabled", false).selectric('refresh');
                }else{
                    this.$el.find('#pop_tab_rexgen2 table tr:eq(1)').show();
                    this.$el.find('#pop_tab_rexgen2 table tr:eq(2)').hide();
                    this.$el.find('#pop_tab_rexgen2 table tr:eq(2) select').attr("disabled", true).selectric('refresh');
                }
            },
            networkType : function(e){
                if(this.$el.find(e.currentTarget).val() === "exist"){
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(1) input').val("");
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(1)').hide();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(2)').show();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(2) select').attr("disabled", false).selectric('refresh');
                }else{
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(1)').show();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(2)').hide();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(2) select').attr("disabled", true).selectric('refresh');
                }
            },
            publicIPType : function(e){
                if(this.$el.find(e.currentTarget).val() === "exist"){
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(4) input').val("");
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(4)').hide();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(5)').show();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(5) select').attr("disabled", false).selectric('refresh');
                }else if(this.$el.find(e.currentTarget).val() === "new"){
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(4)').show();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(5)').hide();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(5) select').attr("disabled", true).selectric('refresh');
                }else{
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(4)').hide();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(5)').hide();
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(5) select').attr("disabled", false).selectric('refresh');
                }
            },
            inboundPortType: function(e){
                if(this.$el.find(e.currentTarget).val() === "none") {
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(7) div').hide();
                } else {
                    this.$el.find('#pop_tab_rexgen5 table tr:eq(7) div').show();
                }
            },
            tdClick: function (e) {
                if(e.target.tagName === 'TD') this.$el.find(e.currentTarget).parent().children('td:eq(0)').children('input:checkbox').click();
            },
            oneClick: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    this.$el.find(e.currentTarget).parents('.tb_wrap').find('input:checkbox').prop('checked', false);
                    $(e.currentTarget).prop('checked', true);
                }
            },
            multiClick: function (e) {
                var checked = $(e.currentTarget).is(':checked');
                if(!checked) {
                    this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', false);
                } else {
                    var checkboxLength =  this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox').length;
                    var checkedLength =  this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:checked').length;

                    if(checkboxLength === checkedLength) {
                        this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', true);
                    } else {
                        this.$el.find(e.currentTarget).parents('table').find('thead input:checkbox').prop('checked', false);
                    }
                }
            },
            allCheck: function(e) {
                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:not(:checked)').click();
                } else {
                    this.$el.find(e.currentTarget).parents('table').find('tbody input:checkbox:checked').click();
                }
            },
            displayFile: function(e) {
                var self = this;
                var fileList = this.$el.find("#pop_tab_rexgen6 tbody input:file")[0].files ;

                // 읽기
                var reader = new FileReader();
                reader.readAsText(fileList [0]);

                //로드 한 후
                reader.onload = function  () {
                    self.$el.find("#pop_tab_rexgen6 tbody textarea").val(reader.result);
                    self.$el.find("#pop_tab_rexgen6 tbody input:file").val('');
                    self.checkSize();
                };
            },
            checkSize: function(e) {
                var textFileAsBlob = new Blob([ this.$el.find("#pop_tab_rexgen6 tbody textarea").val() ], { type: 'text/plain' });
                var size = textFileAsBlob.size;

                if(size > 16384) {
                    this.$el.find("#pop_tab_rexgen6 tbody td span").css("color", "#ff0000");
                } else {
                    this.$el.find("#pop_tab_rexgen6 tbody td span").css("color", "#667285");
                }

                this.$el.find("#pop_tab_rexgen6 tbody span").html("Content size: "+ this.getSizeToFixed(size) +" bytes of 16.00 KB");
            },
            getSizeToFixed: function(size) {
                if(size < 1024) {
                    return size + ' bytes';
                } else if(size < 1048576) {
                    return (size/1024).toFixed(2) + ' KB';
                } else if(size < 1073741824) {
                    return (size/1048576).toFixed(2) + ' MB';
                } else if(size < 1099511627776) {
                    return (size/1073741824).toFixed(2) + ' GB';
                } else if(size < 1125899906842624) {
                    return (size/1099511627776).toFixed(2) + ' TB';
                } else if(size < 4503599627370496) {
                    return (size/1125899906842624).toFixed(2) + ' PB';
                }
            },
            save: function () {
                var self = this;
                var name = this.$el.find('.name').val();
                var username = this.$el.find('.username').val();
                var password = this.$el.find('.password').val();
                var region = this.$el.find('#pop_tab_rexgen1 select option:selected').val();
                var resourceGroupType = this.$el.find('#pop_tab_rexgen2 input:radio').val();
                var resourceGroupName = resourceGroupType === "exist" ?
                    this.$el.find('#pop_tab_rexgen2 select option:selected').val() : this.$el.find('#pop_tab_rexgen2 input:text').val();
                var imageType = self.$el.find("#image_rexgen option:selected").val();
                var imageOS, imageId;
                if(imageType === "public") {
                    imageOS = self.$el.find('#public_img_table_rexgen tbody input:checkbox:checked').parent().parent().find("td:eq(2)").text();
                    imageId = self.$el.find('#public_img_table_rexgen tbody input:checkbox:checked').val();
                } else if(imageType === "custom") {
                    imageOS = self.$el.find('#custom_img_table_rexgen tbody input:checkbox:checked').parent().parent().find("td:eq(3)").text();
                    imageId = self.$el.find('#custom_img_table_rexgen tbody input:checkbox:checked').val();
                }

                var size = this.$el.find('#pop_tab_rexgen4 tbody input:checkbox:checked').parent().parent().find("td:eq(1)").text()
                    +"_"+this.$el.find('#pop_tab_rexgen4 tbody input:checkbox:checked').parent().parent().find("td:eq(2)").text();

                var networkType = this.$el.find('#pop_tab_rexgen5 tr:eq(0) input:radio:checked').val();
                var network = networkType === "exist" ?
                    this.$el.find('#pop_tab_rexgen5 tr:eq(2) select option:selected').val() : this.$el.find('#pop_tab_rexgen5 tr:eq(1) input:text').val();

                var publicIpType = this.$el.find('#pop_tab_rexgen5 tr:eq(3) input:radio:checked').val();
                var publicIp = publicIpType === "exist" ?
                    this.$el.find('#pop_tab_rexgen5 tr:eq(5) select option:selected').val() :
                    publicIpType == "new" ? this.$el.find('#pop_tab_rexgen5 tr:eq(4) input:text').val() : "";

                var inboundPortType =  this.$el.find('#pop_tab_rexgen5 tr:eq(6) input:checked').val();
                var inboundPorts = [];
                var inboundPortSelects = this.$el.find('#pop_tab_rexgen5 tr:eq(7) input:checked');
                if(inboundPortSelects.length > 0) {
                    _.each(inboundPortSelects, function(input) {
                        inboundPorts.push($(input).val());
                    });
                }

                var script =  this.$el.find("#pop_tab_rexgen6 tbody textarea").val();

                var createId = generateGUID();

                var createDate = {
                    createId: createId,

                    name: name,
                    username : username,
                    password : password,
                    region: region,
                    location: region,

                    resourceGroupType: resourceGroupType,
                    resourceGroupName: resourceGroupName,

                    //customImage: customImage,
                    size : size,
                    imageType: imageType,
                    imageOS: imageOS,
                    imageId: imageId,

                    subnet: "default",
                    networkType:  networkType,      // exist, new
                    network : network,              // exist=networkid, new=name
                    publicIpType : publicIpType,    // exist, new, none
                    publicIp : publicIp,            // exist=publicIpid, new=name, none=''

                    inboundPortType: inboundPortType,
                    inboundPort: inboundPorts,

                    script: script
                };


                if(!ValidationUtil.hasValue(createDate.name)) {
                    alert('인스턴스 이름을 입력하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.username)) {
                    alert('사용자 이름을 입력하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.password)) {
                    alert('인스턴스 비밀번호를 입력하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.region)) {
                    alert('지역을 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.resourceGroupType)) {
                    alert('리소스 그룹 유형을 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.resourceGroupName)) {
                    alert('리소스 그룹 이름을 입력하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.imageId)) {
                    alert('이미지를 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.size)) {
                    alert('사양을 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.networkType)) {
                    alert('네트워크 유형을 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.network)) {
                    alert('네트워크 선택 또는 입력하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(createDate.publicIpType)) {
                    alert('퍼블릭 아이피 유형을 선택하세요.');
                    return;
                }
                if(publicIpType !== "none" && !ValidationUtil.hasValue(createDate.publicIp)) {
                    alert('퍼블릭 아이피 선택 또는 입력하세요.');
                    return;
                }

                var m = new (Backbone.Model.extend({
                }));
                m.url = '/edge/rexgen/servers?id=' + self.sid;
                m.save(createDate, {
                    success: function(model, response, options) {
                        //modules.view.collection.add(model, {merge: true});
                        alert(i18n('rexgen 서버 생성 완료.'));
                        self.close();
                    },
                    error :function (model, response, options) {
                        alert(i18n('rexgen 서버 생성 실패.'));
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
                self.close();

                // 최초 VM 생성 시 초기값 설정
                createDate.id = createId;
                createDate.createId = createId;
                createDate.powerState = 'starting';
                createDate.provisioningState = 'Creating';
                // modules.view.collection.add(createDate, {merge: true});
            },
            regionChange: function() {
                var self = this;
                var region = self.$el.find("#pop_tab_rexgen1 select option:selected").val();
                self.$el.find("#pop_tab_rexgen2 select:eq(0)").empty();
                _.each(self.resourceGroupCollection.models, function(model, index, list) {
                    if(model.get('location') === region) {
                        self.$el.find("#pop_tab_rexgen2 select").append(self.resourceGroupTemplate(model.toJSON()));
                    }
                    if(index === list.length - 1) {
                        self.$el.find("#pop_tab_rexgen2 select").removeAttr("disabled").selectric('refresh');
                    }
                });

                self.getSizes(region);
                self.getResourceGroups(region);
                self.getPublicImages(region);
            },
            getSizes: function(region) {
                var self = this;

                self.$el.find("#size_list_rexgen").empty();

                // 리전을 기준으로 VM Size 목록 가져오기
                self.sizeCollection.url = "/edge/rexgen/sizes?region="+region+"&id=" + self.sid;
                self.sizeCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_rexgen4 table:eq(0) tbody').append(self.sizeTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            getResourceGroups: function (region) {
                var self = this;
                if((region || 'null') === 'null') region = "koreacentral";

                self.$el.find("#resourcegroup_list_rexgen").empty();
                self.$el.find("#resourcegroup_list_rexgen").selectric('destroy');

                // 리전을 기준으로 리소스 그룹 목록 가져오기
                self.resourceGroupCollection.url = "/edge/rexgen/resourcegroups?region="+region+"&id=" + self.sid;
                self.resourceGroupCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find("#resourcegroup_list_rexgen").append(self.resourceGroupTemplate(model.toJSON()));
                        });
                        self.$el.find("#resourcegroup_list_rexgen").selectric();
                        self.getNetworks();
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            getPublicImages: function(region) {
                var self = this;
                if((region || 'null') === 'null') region = "koreacentral";

                // 리전을 기준으로 퍼블릭 이미지 목록 가져오기
                self.publicImageCollection.url = "/edge/rexgen/publicimages?region="+region+"&id=" + self.sid;
                self.publicImageCollection.fetch({
                    success: function (collection, response, options) {
                        self.$el.find('#pop_tab_rexgen3 table:eq(1) tbody').empty();
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find('#pop_tab_rexgen3 table:eq(1) tbody').append(self.publicImageTemplate(model.toJSON()));
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            getNetworks: function() {
                var self = this;
                var region = self.$el.find("#pop_tab_rexgen1 select option:selected").val();
                var resourceGroup = self.$el.find("#pop_tab_rexgen2 select option:selected").val();

                self.$el.find("#network_list_rexgen").empty();
                self.$el.find("#network_list_rexgen").selectric('destroy');

                // 리전, 리소스 그룹을 기준으로 네트워크 목록 가져오기
                self.networkCollection.url = "/edge/rexgen/networks?region="+region+"&resourceGroup="+resourceGroup+"&id=" + self.sid;
                self.networkCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find("#network_list_rexgen").append(self.networkTemplate(model.toJSON()));
                        });
                        self.$el.find("#network_list_rexgen").selectric();
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });

                self.getPublicIps();
            },
            imageSelect: function() {
                var self = this;
                var selImageType = self.$el.find("#image_rexgen option:selected").val();
                if(selImageType === "public") {
                    self.$el.find("#custom_img_table_rexgen").hide();
                    self.$el.find("#public_img_table_rexgen").show();
                } else if(selImageType === "custom") {
                    self.$el.find("#public_img_table_rexgen").hide();
                    self.$el.find("#custom_img_table_rexgen").show();
                }
            },
            getPublicIps: function() {
                var self = this;
                var region = self.$el.find("#pop_tab_rexgen1 select option:selected").val();
                var resourceGroup = self.$el.find("#pop_tab_rexgen2 select option:selected").val();

                self.$el.find("#publicip_list_rexgen").empty();
                self.$el.find("#publicip_list_rexgen").selectric('destroy');

                // 리전, 리소스 그룹을 기준으로 네트워크 목록 가져오기
                self.publicIPCollection.url = "/edge/rexgen/publicips?region="+region+"&resourceGroup="+resourceGroup+"&id=" + self.sid;
                self.publicIPCollection.fetch({
                    success: function (collection, response, options) {
                        _.each(collection.models, function(model, index, list) {
                            self.$el.find("#publicip_list_rexgen").append(self.publicIPTemplate(model.toJSON()));
                        });
                        self.$el.find("#publicip_list_rexgen").selectric();
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            }
        }),

        toastCreateView = Backbone.View.extend({
            el: "#popupCreate_toast",
            sid: null,
            publicImageCollection: new (Backbone.Collection.extend({
                url : '/public/toast/images?',
                parse: function(data) {
                    return data.rows;
                }
            })),
            publicImageTemplate: _.template('<tr>\n    <td><input type="checkbox" class="image" id="{{= id }}" value="{{= id }}"><label for="{{= id }}"></label></td>\n    <td>{{= name }}</td>\n    <td>{{= description }}</td>\n    <td>{{= os_locale }}</td>\n    <td>{{= min_disk }} GB</td>\n</tr>'),
            availabilityZoneCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            availabilityZoneTemplate: _.template('<option value="{{=zoneName}}">{{=zoneName}}</option>'),
            instanceTypeCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            instanceTypeTemplate: _.template('<option value="{{=id}}">Name = {{=name}}, RAM = {{=ram}}, vCPU = {{=vcpus}}</option>'),
            keyPairCollection: new (Backbone.Collection.extend({
                parse: function(data) {
                    return data.rows;
                }
            })),
            keyPairTemplate: _.template('<option value="{{=name}}">{{=name}}</option>'),
            events: {
                "click .btn_action": "save",
                "click .btn_pop_close": "close",
                "change #volumeType": "changeVolumeType",
                "click .btn_next": "next",
                "click td": "tdClick",
                "change #image": "imageSelect",
                "click #pop_toast_tab1 tbody input:checkbox": "oneClick",
                // "change #availabilityZone_list": "getAvailabilityZone",
            },
            oneClick: function (e) {
                var self = this;

                var checked = $(e.currentTarget).is(':checked');
                if(checked) {
                    self.$el.find(e.currentTarget).parents('.tb_wrap').find('input:checkbox').prop('checked', false);
                    $(e.currentTarget).prop('checked', true);
                }
            },
            initialize: function() {
                var self = this;
                var btn = $('#popupCreate .pop_btns').children();
                this.$el.find('.pop_toast_tab a').on('click', function () {
                    if($(this).index() == self.$el.find('.pop_toast_tab a').length - 1) {
                        btn.eq(0).show();
                        btn.eq(1).hide();
                    }
                    else {
                        btn.eq(0).hide();
                        btn.eq(1).show();
                    }
                });
            },
            init: function (toastId) {
                var self=this;
                this.$el.find('#pop_toast_tab1 table tbody').empty();
                this.$el.find('#pop_toast_tab1 select option:eq(0)').prop("selected", true);
                this.$el.find('#pop_toast_tab1 select').selectric('refresh');
                this.$el.find("#pop_toast_tab1 #custom_img_table").hide();
                self.sid = toastId;

                // this.$el.find('#availability_zone').selectric('refresh');

                this.publicImageCollection.fetch({
                    async: false,
                    data: {id: self.sid},
                    processData: true,
                    success: function (collection, response, options) {
                        // alert("success");
                        // alert(JSON.stringify(collection));
                        // alert(JSON.stringify(response));

                        _.each(collection.models, function(model, index, list) {
                            // _.each(collection, function(model, index, list) {
                            self.$el.find('#pop_toast_tab1 table:eq(1) tbody').append(self.publicImageTemplate(model.toJSON()));
                            // self.$el.find('#pop_toast_tab1 table:eq(1) tbody').append(self.publicImageTemplate(this[model]));
                        });
                    },
                    error: function (collection, response, options) {
                        alert("fail");
                        ValidationUtil.getServerError(response);
                    }
                });

                self.getAvailabilityZone();
                self.getInstanceType();
                self.getKeyPair();

            },
            getAvailabilityZone: function() {
                var self = this;

                self.$el.find("#availabilityZone_list").empty();
                self.$el.find("#availabilityZone_list").selectric('destroy');

                self.availabilityZoneCollection.url = "/public/toast/zones?id=" + self.sid;
                self.availabilityZoneCollection.fetch({
                    success: function (collection, response, options) {
                        // alert(JSON.stringify(collection));
                        _.each(collection.models, function(model, index, list) {
                            // alert("zone Success2");
                            self.$el.find("#availabilityZone_list").append(self.availabilityZoneTemplate(model.toJSON()));
                        });
                        self.$el.find("#availabilityZone_list").selectric();
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            getInstanceType: function() {
                var self = this;

                self.$el.find("#InstanceType_list").empty();
                self.$el.find("#InstanceType_list").selectric('destroy');

                self.instanceTypeCollection.url = "/public/toast/flavors?id=" + self.sid;
                self.instanceTypeCollection.fetch({
                    success: function (collection, response, options) {
                        // alert(JSON.stringify(collection));
                        _.each(collection.models, function(model, index, list) {
                            // alert("zone Success2");
                            self.$el.find("#InstanceType_list").append(self.instanceTypeTemplate(model.toJSON()));
                        });
                        self.$el.find("#InstanceType_list").selectric();
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            getKeyPair: function() {
                var self = this;

                self.$el.find("#key_name").empty();
                self.$el.find("#key_name").selectric('destroy');

                self.keyPairCollection.url = "/public/toast/keypairs?id=" + self.sid;
                self.keyPairCollection.fetch({
                    success: function (collection, response, options) {
                        // alert(JSON.stringify(collection));
                        _.each(collection.models, function(model, index, list) {
                            // alert("zone Success2");
                            self.$el.find("#key_name").append(self.keyPairTemplate(model.toJSON()));
                        });
                        self.$el.find("#key_name").selectric();
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            },
            next: function () {
                var currentIndex = this.$el.find('.pop_toast_tab a.on').index();
                var tabLength = this.$el.find('.pop_toast_tab a').length;

                if(currentIndex == 0) {
                    var checkedVal = this.$el.find('#pop_toast_tab1 tbody input:checkbox:checked').val();
                    if(!ValidationUtil.hasValue(checkedVal)) {
                        alert('Image를 선택하세요.');
                        return;
                    }
                }

                if(currentIndex != tabLength - 1) {
                    $('.pop_toast_tab a:eq(' + (currentIndex + 1) + ')').click();
                } else {
                    // alert('마지막 페이지 입니다.');
                    alert(i18n('s.last-page'));
                }
            },
            open: function (toastId) {
                this.init(toastId);
                $myPlugin.setPopupCenter(this.el);
                this.$el.fadeIn(100);
            },
            close: function () {
                this.$el.fadeOut(100);
            },
            save: function () {
                var self = this;

                var imageRef = this.$el.find('#pop_toast_tab1 tbody input:checkbox:checked').val();
                var instance_name = this.$el.find('.instance_name').val();
                var flavorRef = this.$el.find('#pop_toast_tab2 tr:eq(1) select option:selected').val();
                var instance_count = this.$el.find('.instance_count').val();
                var availabilityZone_list = this.$el.find('#pop_toast_tab2 tr:eq(3) select option:selected').val();
                var network_subnetId = this.$el.find('.network_subnetId').val();
                var key_name = this.$el.find('#pop_toast_tab2 tr:eq(5) select option:selected').val();
                var securityGroup = this.$el.find('.securityGroup').val();

                var checkedVal = this.$el.find('#pop_toast_tab1 tbody input:checkbox:checked').val();
                if(!ValidationUtil.hasValue(checkedVal)) {
                    alert('Image를 선택하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(instance_name)) {
                    alert('인스턴스 이름을 입력하세요.');
                    return;
                }
                if(!ValidationUtil.hasValue(instance_count)) {
                    alert('인스턴스 수를 입력하세요.');
                    return;
                }

                console.log("key_name = " + key_name);

                var model = new Backbone.Model({
                    imageRef: imageRef,
                    name: instance_name,
                    flavorRef: flavorRef,
                    max_count: instance_count,
                    availability_zone: availabilityZone_list,
                    networks: network_subnetId,
                    key_name: key_name,
                    security_groups: securityGroup,
                    uuid: imageRef,
                });
                // alert("inst = " + instance_name);
                // alert("model = " + model);
                // alert(JSON.stringify(model));
                // model.url = '/edge/etri/volumes?id=' + id;
                model.url = '/public/toast/servers?id=' + self.sid;

                showLoadingUI(true, i18n('s.t.please-wait'));
                model.save(model.attributes, {
                    success: function (model, response, options) {
                        showLoadingUI(false);
                        // modules.view.collection.add(model, {merge: true});
                        self.close();
                        location.href=location.href;
                    },
                    error: function (model, response, options) {
                        // alert("생성 실패");
                        showLoadingUI(false);
                        ValidationUtil.getServerError(response);
                        self.close();
                    }
                });
            }
        }),
        init = function (isAdmin) {
            modules.view = new ServerView();
            modules.detailView = new ServerDetailView();
            modules.createView = new CreateView();
            // modules.detailHistoryView = new HistoryView();

            stompUtil.addListener('/topic/toast/' + options.userId, function (msg) {
                var payload = null;
                try {
                    payload = JSON.parse(msg.body).payload;
                } catch (e) {
                    console.log(e);
                    return;
                }
                console.log("Payload (/topic/toast) :", payload);
                switch (payload.action) {
                    default:
                        if (payload.id) {
                            var m = modules.view.collection.get(payload.object.id);
                            if (m) {
                                m.set(payload.object);
                            }
                        } else {
                            stompUtil.getError(payload);
                        }
                        break;
                }
            });
        };

        // Create Instance
        init = function (isAdmin) {
            modules.createServer = new createServer();
            modules.credentialId = new credentialId();

            modules.etriCreateView = new etriCreateView();
            modules.rexgenCreateView = new rexgenCreateView();
            modules.toastCreateView = new toastCreateView();

        };

    // call by init_Function
    return {
        init: init,
        modules: modules
    };
})(config);
