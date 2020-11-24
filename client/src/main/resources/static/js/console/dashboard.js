var DashboardUI = (function (options) {
    var
        modules = {},
        ServiceDashboardModel = Backbone.Model.extend({
            idAttribute: 'id',
            defaults: {
                id: null,
                type: '',
                totalServer: 0,
                runningServer: 0,
                stoppedServer: 0,
                etcServer: 0,
                account: 0,
                lastUpdatedAt: ''
            }
        }),
        ServiceDashboardCollection = Backbone.Collection.extend({
            url: '/dashboard/resources',
            model: ServiceDashboardModel
        }),
        ServiceDashboardView = Backbone.View.extend({
            el: ".cont_wrap",
            serverTemplate: _.template('<li class="swiper-slide" id="{{= id }}">\n    <div class="dashboard_circle_wrap">\n        <div class="dashboard_circle">\n            <div class="circle">\n                <div class="value">\n                    <div class="num">\n                        <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n                                            <path d="M33.4,34.7c0,4.8-1.6,8.7-4.8,11.5c-2.9,2.6-6.6,3.9-11,3.9c-4.4,0-8-1.3-11-3.9c-3.2-2.8-4.8-6.6-4.8-11.5V15.3\n                                                            c0-4.8,1.6-8.7,4.8-11.5C9.5,1.3,13.2,0,17.5,0c4.4,0,8,1.3,11,3.9c3.2,2.8,4.8,6.6,4.8,11.5V34.7z M22,34.7V15.3\n                                                            c0-1.7-0.4-2.9-1.2-3.8c-0.8-0.9-1.9-1.3-3.2-1.3c-1.3,0-2.4,0.4-3.2,1.3c-0.9,0.9-1.3,2.1-1.3,3.8v19.4c0,1.7,0.4,2.9,1.3,3.8\n                                                            c0.9,0.9,1.9,1.3,3.2,1.3c1.3,0,2.4-0.4,3.2-1.2C21.6,37.7,22,36.4,22,34.7z"/>\n                                        </svg>\n                    </div>\n                    <div  class="name">{{= jQuery.i18n.prop(\'title.jqgrid.server\') }}</div>\n                </div>\n            </div>\n        </div>\n        <ul class="dashboard_circle_info">\n            <li>\n                <div class="name">{{= jQuery.i18n.prop(\'w.running\') }}</div>\n                <div class="num">\n                    <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n                                        <path d="M33.4,34.7c0,4.8-1.6,8.7-4.8,11.5c-2.9,2.6-6.6,3.9-11,3.9c-4.4,0-8-1.3-11-3.9c-3.2-2.8-4.8-6.6-4.8-11.5V15.3\n                                                                    c0-4.8,1.6-8.7,4.8-11.5C9.5,1.3,13.2,0,17.5,0c4.4,0,8,1.3,11,3.9c3.2,2.8,4.8,6.6,4.8,11.5V34.7z M22,34.7V15.3\n                                                                    c0-1.7-0.4-2.9-1.2-3.8c-0.8-0.9-1.9-1.3-3.2-1.3c-1.3,0-2.4,0.4-3.2,1.3c-0.9,0.9-1.3,2.1-1.3,3.8v19.4c0,1.7,0.4,2.9,1.3,3.8\n                                                                    c0.9,0.9,1.9,1.3,3.2,1.3c1.3,0,2.4-0.4,3.2-1.2C21.6,37.7,22,36.4,22,34.7z"/>\n                                    </svg>\n                </div>\n            </li>\n            <li>\n                <div class="name">{{= jQuery.i18n.prop(\'w.stop\') }}</div>\n                <div class="num">\n                    <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n                                                                <path d="M33.4,34.7c0,4.8-1.6,8.7-4.8,11.5c-2.9,2.6-6.6,3.9-11,3.9c-4.4,0-8-1.3-11-3.9c-3.2-2.8-4.8-6.6-4.8-11.5V15.3\n                                                                c0-4.8,1.6-8.7,4.8-11.5C9.5,1.3,13.2,0,17.5,0c4.4,0,8,1.3,11,3.9c3.2,2.8,4.8,6.6,4.8,11.5V34.7z M22,34.7V15.3\n                                                                c0-1.7-0.4-2.9-1.2-3.8c-0.8-0.9-1.9-1.3-3.2-1.3c-1.3,0-2.4,0.4-3.2,1.3c-0.9,0.9-1.3,2.1-1.3,3.8v19.4c0,1.7,0.4,2.9,1.3,3.8\n                                                                c0.9,0.9,1.9,1.3,3.2,1.3c1.3,0,2.4-0.4,3.2-1.2C21.6,37.7,22,36.4,22,34.7z"/>\n                                    </svg>\n                </div>\n            </li>\n            <li>\n                <div class="name">{{= jQuery.i18n.prop(\'w.etc\') }}</div>\n                <div class="num">\n                    <svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" x="0px" y="0px" viewBox="0 0 35 50" style="enable-background:new 0 0 35 50;" xml:space="preserve">\n                                                                <path d="M33.4,34.7c0,4.8-1.6,8.7-4.8,11.5c-2.9,2.6-6.6,3.9-11,3.9c-4.4,0-8-1.3-11-3.9c-3.2-2.8-4.8-6.6-4.8-11.5V15.3\n                                                                c0-4.8,1.6-8.7,4.8-11.5C9.5,1.3,13.2,0,17.5,0c4.4,0,8,1.3,11,3.9c3.2,2.8,4.8,6.6,4.8,11.5V34.7z M22,34.7V15.3\n                                                                c0-1.7-0.4-2.9-1.2-3.8c-0.8-0.9-1.9-1.3-3.2-1.3c-1.3,0-2.4,0.4-3.2,1.3c-0.9,0.9-1.3,2.1-1.3,3.8v19.4c0,1.7,0.4,2.9,1.3,3.8\n                                                                c0.9,0.9,1.9,1.3,3.2,1.3c1.3,0,2.4-0.4,3.2-1.2C21.6,37.7,22,36.4,22,34.7z"/>\n                                    </svg>\n                </div>\n            </li>\n        </ul>\n    </div>\n</li>'),
            credentialCollection: new (Backbone.Collection.extend({
                url: '/management/credentials/names'
            })),
            events: {
            },
            serverDataLoad: function () {
                var self = this;
                this.collection.fetch({
                    success: function (collection, response, options) {

                        var publicTotal = 0;
                        var publicRunning = 0;
                        var publicStop = 0;
                        var publicEtc = 0;
                        var privateTotal = 0;
                        var privateRunning = 0;
                        var privateStop = 0;
                        var privateEtc = 0;
                        _.each(collection.models, function(model, index, list) {
                            var m = self.credentialCollection.findWhere({id: model.get("id")});
                            console.log(model)
                            if (m) {
                                if (m.get('cloudType') == "public") {
                                } else if(m.get('cloudType') == "edge"){
                                    publicTotal += model.get('totalServer');
                                    publicRunning += model.get('runningServer');
                                    publicStop += model.get('stoppedServer');
                                    publicEtc += model.get('etcServer');
                                }else {
                                    privateTotal += model.get('totalServer');
                                    privateRunning += model.get('runningServer');
                                    privateStop += model.get('stoppedServer');
                                    privateEtc += model.get('etcServer');
                                }

                                $("#" + model.get('id')).find('.num').each(function (num) {
                                    var data = 0;
                                    switch(num) {
                                        case 0:
                                            data = model.get('totalServer');
                                            break;
                                        case 1:
                                            data = model.get('runningServer');
                                            break;
                                        case 2:
                                            data = model.get('stoppedServer');
                                            break;
                                        case 3:
                                            data = model.get('etcServer');
                                            break;
                                    }
                                    $("#" + model.get('id')).find(".num:eq("+num+")").toNumberSVG(data);
                                });
                            }

                            if(index == list.length - 1) {
                                $("#public_total").toNumberSVG(publicTotal);
                                $("#public_running").toNumberSVG(publicRunning);
                                $("#public_stop").toNumberSVG(privateStop);
                                $("#public_etc").toNumberSVG(publicEtc);
                                $("#private_total").toNumberSVG(privateTotal);
                                $("#private_running").toNumberSVG(privateRunning);
                                $("#private_stop").toNumberSVG(privateStop);
                                $("#private_etc").toNumberSVG(privateEtc);
                                $(".last_updated_at").html(i18n('w.t.multi', 'w.last', 'w.update-en') + " : " + model.get('lastUpdatedAt'));
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });

            },
            initialize: function () {
                var self = this;
                this.collection = new ServiceDashboardCollection();

                console.log(this.collection)
                this.credentialCollection.fetch({
                    success: function (collection, response, options) {
                        var privates = [];
                        var publics = [];
                        var swipers = [];

                        _.each(collection.models, function(model) {
                            if(model.get('cloudType') == 'public') {
                                //publics.push(model.get('name'));
                            } else if(model.get('cloudType') == 'edge') {
                                console.log(model)
                                publics.push(model.get('name'));
                            }else{
                                privates.push(model.get('name'));
                            }
                        });



                        swipers.push(new Swiper('#public',{
                            pagination: {
                                el: '.swiper-pagination',
                                clickable: true,
                                renderBullet: function (index, className) {
                                    return '<button type="button" class="' + className + '">' + publics[index] + '</button>';
                                }
                            },
                            navigation: {
                                nextEl: '.btn_swiper_next',
                                prevEl: '.btn_swiper_prev'
                            },
                            on: {
                                slideChange: function () {
                                    var on = $("#public").find('.swiper-pagination-bullet-active');
                                    var target = $("#public").find('.tabs_inner');
                                    var left = on.position().left - 8;

                                    if(-left > target.position().left) {
                                        target.css('left', -left);
                                    } else if(on.position().left + on.width() > target.parent().width() - 20) {
                                        left = on.position().left - target.parent().width() + on.width() + 20;
                                        target.css('left', -left );
                                    }
                                }
                            }
                        }));

                        swipers.push(new Swiper('#private',{
                            pagination: {
                                el: '.swiper-pagination',
                                clickable: true,
                                renderBullet: function (index, className) {
                                    return '<button type="button" class="' + className + '">' + privates[index] + '</button>';
                                }
                            },
                            navigation: {
                                nextEl: '.btn_swiper_next',
                                prevEl: '.btn_swiper_prev'
                            },
                            on: {
                                slideChange: function () {
                                    var on = $("#private").find('.swiper-pagination-bullet-active');
                                    var target = $("#private").find('.tabs_inner');
                                    var left = on.position().left - 8;

                                    if(-left > target.position().left) {
                                        target.css('left', -left);
                                    } else if(on.position().left + on.width() > target.parent().width() - 20) {
                                        left = on.position().left - target.parent().width() + on.width() + 20;
                                        target.css('left', -left );
                                    }
                                }
                            }
                        }));

                        _.each(collection.models, function(model, index, list) {
                            if(model.get('cloudType') == 'public') {
                                // console.log(model.toJSON())
                                // swipers[0].appendSlide(self.serverTemplate(model.toJSON()));
                            } else if(model.get('cloudType') == 'edge') {
                                console.log(model.toJSON())
                                swipers[0].appendSlide(self.serverTemplate(model.toJSON()));
                            }else {
                                swipers[1].appendSlide(self.serverTemplate(model.toJSON()));
                            }
                            if(index == list.length - 1) {
                                self.serverDataLoad();
                            }
                        });
                    },
                    error: function (collection, response, options) {
                        ValidationUtil.getServerError(response);
                    }
                });
            }
        }),

        init = function (isAdmin) {
            modules.view = new ServiceDashboardView();


        };

    return {
        init: init,
        modules: modules
    };
})(config);
