var console = window.console || {log:function(msg){}}; //For IE

var $myPlugin = {
	setPopupCenter: function(obj){
		// var pop_width = $(obj).width();
		var pop_height = $(obj).height();
		$(obj).css({
			top: ($('body').height()/2)-(pop_height/2),
			// marginLeft: -(pop_width/2)
		});

	}
};

(function($) {

    $.fn.selectOne = function(val, attrName) {

        this.each(function(index, item){
            if( $(item).attr(attrName) == val) {
                $(item).addClass('selected');
            }else{
                $(item).removeClass();
            }
        });
    };

}(jQuery));

$(function(){
	// var leftMenuHidden = $.cookie("leftMenuHidden");
	// if(leftMenuHidden && leftMenuHidden == ' closed'){
	// 	$('.leftSide').addClass('closed');
	// 	$('.leftBtn_close button').attr('title','Show');
	// }
	// $('.leftBtn_close button').on('click', function(){
	// 	$('.leftSide').toggleClass('closed');
	// 	if($(this).attr('title') == 'Hide'){
	// 		$(this).attr('title','Show');
	// 		$.cookie("leftMenuHidden", " closed", {path: '/'});
	// 	}else{
	// 		$(this).attr('title','Hide');
	// 		$.cookie("leftMenuHidden", "", {path: '/'});
	// 	};
	//
	// 	$(".ui-jqgrid-btable").each(function(i, element){$(element).resetSize();});
	// });
});

//tabMenu
$(function(){	
	// $('.tabMenu').each(function(){ //복수의 탭은 있느나 내용은 공통 인 경우라서 수정
	// 	$(this).find('button').on('click', function(){
	// 		$(this).addClass('selected');
	// 		$(this).siblings().removeClass('selected');
	// 		$(this).parent().next('.tabContainer').find('.tabContent').hide();
	//
	// 		$('.content').removeClass('contentSouth_min');
    //         $.cookie("tabMenuHidden", "", {path: '/'});
	//
	// 		var el = $(this).parent().next('.tabContainer').find('.tabContent:eq('+ $(this).index() +')');
	// 		if(el.length > 0) {
	// 			el.show();
	// 		    //el.find(".ui-jqgrid-btable").each(function(i, element){$(element).resetSize();});
	// 		}else {
	// 		    $(this).parent().next('.tabContainer').find('.tabContent:eq(0)').show();
	// 		}
	// 		$(".ui-jqgrid-btable").each(function(i, element){$(element).resetSize();});
	// 	});
	//
	//
	// 	var first =$(this).children(':first');
	// 	$(first).addClass('selected');
    //    	$(first).siblings().removeClass('selected');
    //     $(first).parent().next('.tabContainer').find('.tabContent').hide();
    //     $(first).parent().next('.tabContainer').find('.tabContent:eq(0)').show();
	// });
});

//action_option_frame
$(function(){	
	// $('.btn_action_min').on('click', function(){
	// 	$('.content').removeClass('contentSouth_max');
	// 	$('.content').addClass('contentSouth_min');
	// 	$(".ui-jqgrid-btable").each(function(i, element){$(element).resetSize();});
	// 	$.cookie("tabMenuHidden", " contentSouth_min", {path: '/'});
	// });
	//
	// $('.btn_action_max').on('click', function(){
	// 	$('.content').removeClass('contentSouth_min');
	// 	$('.content').addClass('contentSouth_max');
	// 	$(".ui-jqgrid-btable").each(function(i, element){$(element).resetSize();});
	// 	$.cookie("tabMenuHidden", " contentSouth_max", {path: '/'});
	// });
	//
	// $('.btn_action_default').on('click', function(){
	// 	$('.content').removeClass('contentSouth_min');
	// 	$('.content').removeClass('contentSouth_max');
	// 	$(".ui-jqgrid-btable").each(function(i, element){$(element).resetSize();});
	// 	$.cookie("tabMenuHidden", "", {path: '/'});
	// });
});

//popup
$(function(){
	$('.util .utilMenu > button').on('click', function(){
		$(this).parent().children('div').toggle();
		$(this).parent().children('div').mouseleave(function(){
		    $(this).hide();
		});
	});
});

//popup
$(function(){
	$('.popup_close button').on('click', function(){
		//$(this).parent().parent().parent().hide();		
	});
});

//btn_action
$(function(){	
	$('.btn_action_create').on('click', function(){
		$(this).toggleClass('selected');
	});
	
	$('.btn_action_select').on('click', function(){
		$(this).toggleClass('selected');
		$(this).next('.action_list').toggle();
	});
	
	$('.action_select .action_list').on('mouseover', function(){
		$('.action_select .btn_action_select').addClass('selected');
		$(this).show();
	});
	
	$('.action_select .action_list').on('mouseout', function(){
		$('.action_select .btn_action_select').removeClass('selected');
		$(this).hide();
	});
	
	$('.action_buttons .action_list').on('mouseover', function(){
		$(this).show();
	});
	
	$('.action_buttons .action_list').on('mouseout', function(){
		$(this).hide();
	});

	$('.action_select2 .action_list').on('mouseover', function(){
        $('.action_select2 .btn_action_select').addClass('selected');
        $(this).show();
    });

    $('.action_select2 .action_list').on('mouseout', function(){
        $('.action_select2 .btn_action_select').removeClass('selected');
        $(this).hide();
    });
});


$(document).ready(function(){
	// even-line color
	// $(".table_grid").each(function(){
	// 	$(this).find('tbody tr:nth-child(odd) td').css('backgroundColor','#ffffff');
	// 	$(this).find('tbody tr:nth-child(even) td').css('backgroundColor','#f7f9fc');
	// });
	//
	// $(".settingList").each(function(){
	// 	$(this).children('li:nth-child(odd)').css('backgroundColor','#ffffff');
	// 	$(this).children('li:nth-child(even)').css('backgroundColor','#f7f9fc');
	// });
	
	//select
//	$('.select_common, .select_search').selectBoxIt({
//		showEffect: "fadeIn",
//		showEffectSpeed: "fast",
//		hideEffect: "fadeOut",
//		hideEffectSpeed: "fast",
//	});
	
	//draggable
	$('.pop_wrap').draggable({handle: '.pop_tit'});
	$('.pop_action_buttons').draggable({handle: '.pop_s_title'});
	// $(".select_search").selectBoxIt();
	// $(document).on("click",".content_north .hiddenMsg p.closeBtn > button", function() {
	//     /*$(".content_north .hiddenMsg").fadeOut(700);*/
	//     $(this).parents(".hiddenMsg").fadeOut(700, function() {
	//         var nextMsg = $(this).prevAll('.hiddenMsg');
	//         $(this).remove();
	//
	//         $.each(nextMsg, function(index, value) {
	//             var msg = $(value);
	//             msg.css("top", msg.position().top - 38);
	//         });
	//     });
	// });
});

//Session 만료

$.ajaxSetup({
    complete : function(xhr, textStatus, errorThrown) {
        if (xhr.status == 200) {
            if(xhr.responseText != "" && xhr.responseText.indexOf("content=\"Login\"") > -1) {
                //alert("세션이 만료되었습니다.");
                document.location.href = "/";
            }
        }
    }
});


