$(function(){
    $('.js_list_select button').on('click', function () {
        $('.js_list_select').find('ul').slideToggle(150);
    });
    $('.js_list_select label').on('click', function () {
        $('.js_list_select').toggleClass("on");
        $('.js_list_select').find('ul').slideToggle(150);
        $('.js_list_select button span').text($(this).text());

        $('.js_list_select li label').removeClass('on');
        $(this).addClass('on');
        $('.js_list_select button').removeClass("open");
    });

    $('.js_arr_toggle').on('click', function(){
        $(this).toggleClass('open')
    });

    $('.js_nav_btn').on('click', function(){
        $('.js_nav_menu').slideToggle(150);
    });

    $('button.js_user_open').on('click', function(){
        $('.header_top_user_menu').slideToggle(100);
    });

    $('.js_tbclose_btn').on('click', function(){
        $(this).parent().slideUp(150);
        $('.tb_mypage_list').find('.tr_mypage_list_on').removeClass();
    });
    $('.js_tbopen_btn').on('click', function(){
        $(this).parent().parent().next().find('.cont_mypage_detail_inner').slideDown(150);
        $(this).parent().parent().addClass('tr_mypage_list_on');
    });
    $('.js_select_btn').on('click', function(){
        $('.cont_mypage_selectList ul').slideToggle(100);
    });

    // $('.btn_pop_close').on('click', function(){
    //     $('.popup').hide();
    // });

});