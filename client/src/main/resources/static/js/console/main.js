// var MyInfoModel = AnonModel.extend({
// 	idAttribute: "id",
// 	urlRoot: '/users/myinfo',
// 	validation: {
// 	    user : [ {
//             required : true,
//             msg : "사용자를 입력하세요."
//         }, {
//             rangeLength : [ 1, 45 ],
//             msg : "사용자는 45자 미만이어야 합니다."
//         }],
// 		affiliation: [{
//         	required: true,
//             msg: "소속을 입력하여 주세요."
//         },
//         {
//         	rangeLength: [1, 45],
//             msg: "소속은 45자 미만이어야 합니다."
//         }],
//         email: {
//             pattern: 'email',
//             msg: "E-mail 형식이 잘못되었습니다."
//         },
//         currentPassword: [{
//         	required: true,
//         	msg: "현재 Password를 입력하여 주세요."
//         }],
//         password: [{
//         	required: function(value, attr, computedState) {
//         		if(value == null || value == "") {
//         			return false;
//         		}
//
//         		return true;
//         	},
//         	msg: "새로운 Password를 입력하여 주세요."
//         },
//         {
// 			minLength : 8,
// 			msg : '패스워드는 최소한 8자 이상 이여야 합니다.'
// 		}],
// 		password2: {
// 			equalTo: 'password',
// 			msg: "새로운 Password와 Password 확인이 일치하지 않습니다."
// 		}
// 	},
// 	defaults: {
// 		id: null,
// 		user: '',
// 		affiliation: '',
// 		email: '',
// 		currentPassword: '',
// 		password: '',
// 		password2: ''
// 	}
// });
//
// /** MyInfo Change Popup */
//
// var MyInfoUpdatePopupView = Backbone.View.extend({
// 	el : "#pop_myInfo",
// 	events : {
// 		"click .buttons .popupBtn_common" : "cancelButton",
// 		"click .popup_close button" : "cancelButton",
// 		"click .buttons .popupBtn_submit" : "saveButton"
// 	},
// 	initialize: function(){
// 	    $('#pop_myinfo_update_user').val("");
// 		$('#pop_myinfo_update_affiliation').val("");
// 		$('#pop_myinfo_update_email').val("");
// 		$('#pop_myinfo_update_password_current').val("");
// 		$('#pop_myinfo_update_password').val("");
// 		$('#pop_myinfo_update_password2').val("");
// 		_.bindAll(this,"keyHandler");
//     },
//     keyHandler: function(e) {
//         if(e.which == 27) {
//             this.cancelButton();
//         }
//     },
//     show: function() {
//     	var self = this;
//     	this.model = new MyInfoModel();
//     	this.model.fetch({
//     		success: function(model) {
//     		    $('#pop_myinfo_update_user').val(model.attributes.id);
//     			$('#pop_myinfo_update_affiliation').val(model.attributes.affiliation);
//     			$('#pop_myinfo_update_email').val(model.attributes.email);
//     			$('#pop_myinfo_update_password_current').val("");
//     			$('#pop_myinfo_update_password').val("");
//     			$('#pop_myinfo_update_password2').val("");
//     			self.$el.show();
//     	        $myPlugin.getPopup(self.el);
//     		}
//     	});
//     	$(document).on("keyup", this.keyHandler);
//     },
// 	cancelButton : function() {
// 		$('#pop_myinfo_update_user').val("");
//         $('#pop_myinfo_update_affiliation').val("");
//         $('#pop_myinfo_update_email').val("");
// 		$('#pop_myinfo_update_password_current').val("");
// 		$('#pop_myinfo_update_password').val("");
// 		$('#pop_myinfo_update_password2').val("");
// 		this.$el.hide({ effect: 'fade' , complete: function() { $(this).find('.popup_wrap').removeAttr('style'); }});
// 		$(document).off("keyup", this.keyHandler);
// 	},
// 	saveButton : function() {
// 		var model = new MyInfoModel();
//
// 		model.set({
// 		    user: $('#pop_myinfo_update_user').val(),
// 		    affiliation: $('#pop_myinfo_update_affiliation').val(),
// 			email: $('#pop_myinfo_update_email').val(),
// 			currentPassword: $('#pop_myinfo_update_password_current').val(),
// 			password: $('#pop_myinfo_update_password').val(),
// 			password2: $('#pop_myinfo_update_password2').val()
// 		});
//
// 		if(model.validate()) {
// 			model.getError();
//         }else {
//     	   var self = this;
//     	   this.model.unset('password2');
//     	   this.model.set({
// 	   			user: $('#pop_myinfo_update_user').val(),
//                 affiliation: $('#pop_myinfo_update_affiliation').val(),
//                 email: $('#pop_myinfo_update_email').val(),
// 	   			currentPassword: $('#pop_myinfo_update_password_current').val(),
// 	   			password: $('#pop_myinfo_update_password').val()
// 	   	   });
//     	   this.model.save(this.model.attributes, {
// 				success : function(model, response, options) {
// 					$("#main_user_name").html(model.get('id') + " (" + model.get('user') + ")");
// 					self.cancelButton();
// 				},
// 				error: function(model, response, options) {
// 					alert(JSON.parse(response.responseText).message);
// 				}
// 			});
//        }
// 	}
// });
// /* MyInfo Change Popup **/
//
// var myInfoUpdatePopupView = new MyInfoUpdatePopupView();

var MainUI = (function (options) {
	var
		modules = {},
		LanguageModel = Backbone.Model.extend({
			id: "locale",
			urlRoot: "/locale",
			defaults: {
				locale: "ko"
			}
		}),
		MainView = Backbone.View.extend({
			el : "body",
			events : {
				"click #setting_language": "settingLanguage"
			},
			settingLanguage: function() {
				modules.languageView.show();
			}
		}),
		LanguageView = Backbone.View.extend({
			el : "#pop_locale",
			events : {
				"click .btn_action": "saveButton",
				"click .btn_pop_close": "close"
			},
			initialize: function(){
			},
			show : function(){
				var locale = $.cookie('locale');
				$('input:radio[name="localeMyConfig"]:input[value="'+ locale +'"]').prop('checked', true);

				$myPlugin.setPopupCenter(this.el);
				this.$el.fadeIn(100);
			},
			close : function(){
				this.$el.fadeOut(100);
			},
			saveButton: function(){
				var model = new LanguageModel({
					locale : $('input:radio[name="localeMyConfig"]:checked').val()
				});

				model.save(model.attributes, {
					success : function(model, response) {
						var config = $.cookie("locale");
						if(config == model.get('locale')) {
							if(getSessionStorage("message")) {
								$.i18n.map = getSessionStorage("message");
							} else {
								$.i18n.properties({
									name:'message',
									path:'/properties/',
									mode:'map',
									language:'',
									callback: function(){
										saveSessionStorage("message", $.i18n.map);
									}
								});

							}
						} else {
							var config = model.get('locale');
							$.cookie("locale", config, {path: '/'});

							$.i18n.properties({
								name:'message',
								path:'/properties/',
								mode:'map',
								language:'',
								callback: function(){
									saveSessionStorage("message", $.i18n.map);
								}
							});
						}

						//alert(jQuery.i18n.prop('message_0160'));
						window.location.reload();

					},
					error: function(m, resp){
						ValidationUtil.getServerError(resp);
					}
				});
			}
		}),
		MessageItemView = Backbone.View.extend({
			tagName: "div",
			template: _.template('<div class="pop_msg_inner">\n    <p>{{=message}}</p>\n</div>\n<button type="button" class="pop_msg_close"><span class="ico_close">Close</span></button>'),
			events: {
				"click .pop_msg_close": "delete"
			},
			initialize: function () {

			},
			delete: function () {
				this.remove();
				modules.messageView.decreaseCount();
			},
			render: function (message) {
				this.$el.addClass('pop_msg_item');
				this.$el.append(this.template({message:message.replace(/(\n|\r\n)/g, '<br>')}));

				return this;
			}
		}),
		MessageView = Backbone.View.extend({
			el: ".pop_msg",
			initialize: function () {
				this.count = 0;
				this.$el.find('.count').hide();
			},
			decreaseCount: function () {
				if(this.count > 0) {
					this.count = this.count - 1;
					this.$el.find('.pop_msg_item').last().fadeIn(1000);
				}
				if(this.count <= 0) {
					this.count = 0;
					$('.count').text(this.count);
				}
				this.displayCount();
			},
			increaseCount: function () {
				this.count = this.count + 1;
				this.displayCount();
			},
			displayCount: function () {
				if(this.count > 1) {
					this.$el.find('.count').show();
				} else {
					this.$el.find('.count').hide();
				}
				if($('.pop_msg_item').length > 0) {
					var countDiv = $('.count').detach();
					countDiv.text(this.count);
					$('.pop_msg_item').last().after(countDiv);
				}
			},
			toastMessage: function (message) {
				// this.$el.hide();
				this.$el.append(new MessageItemView().render(message).el);
				this.$el.find('.pop_msg_item').hide().last().fadeIn(1000);

				this.increaseCount();
			}
		}),
		init = function (isAdmin) {
			modules.view = new MainView();
			modules.languageView = new LanguageView();
			modules.messageView = new MessageView();
		},
		toastMessage = function (message) {
			modules.messageView.toastMessage(message);
		};

	return {
		init: init,
		modules: modules,
		toastMessage: toastMessage
	}
})(config);

MainUI.init();
