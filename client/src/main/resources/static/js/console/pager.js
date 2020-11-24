(function($){
    /*
        TODO
        메모리 leak 수정
        재사용 가능하도록 수정
        i18n 적용
    */

	function render(options) {

        var root = $("<div class='pagenation_inner'/>");
        var beforeHtml = '<button type="button" class="pagenation_btn first" title="{{= i18n(\'w.t.page\', \'w.first\') }}" data-page="first">' +
			'<span>First</span></button><button type="button" class="pagenation_btn prev" title="{{= i18n(\'w.t.page\', \'w.before\') }}" data-page="before">' +
			'<span>Prev</span></button>';
        var nextHtml = '<button type="button" class="pagenation_btn next" title="{{= i18n(\'w.t.page\', \'w.next\') }}" data-page="next">' +
			'<span>Next</span></button><button type="button" class="pagenation_btn last" title="{{= i18n(\'w.t.page\', \'w.last\') }}" data-page="last">' +
			'<span>Last</span></button>';
        var currentHtml = '<div class="pagenation_current"><strong class="current">{{=from}} - {{=to}}</strong>of' +
			'<span class="total">{{=records}}</span></div>';

		if(options){

			options.el.empty();
			root.append(_.template(beforeHtml));

            var endPage = options.pageSize ? options.pageSize : options.endPage;
            var startPage = options.startPage ? options.startPage: 1;
            var base = parseInt(options.page, 10) - 1;
            if(base < 0) base = 0;
            base = base * parseInt(options.rowNum, 10);
            var from = base + 1;
            var to = base + options.reccount;
            if(to > options.records) {
            	to = options.records;
			}

            if(options.total == 0){
                options.total = 1;
            }

			// root.append(_.template(currentHtml, {page: options.page, total: options.total}));
			root.append(_.template(currentHtml, {from: from, to: to, records: options.records}));

			root.append(_.template(nextHtml));

			root.appendTo(options.el);

			options.el.find("button").on("click", function(e){
			
				var datapage = $(this).attr('data-page');
				
				if(datapage) {
                    switch(datapage){
                        case "next":
                        	nextPage(options);
                        	break;
                        case "before":
                            beforePage(options);
                            break;
                        case "first":
                        	firstPage(options);
                        	break;
                        case "last":
                        	lastPage(options);
                        	break;
                    }

				}else{
					var page = $(this).attr('data-page-val');
					reloadPage(options, page);
				}
			});
		}
	};

    function reloadPage(options, page) {

        if(options.getPageParam == undefined){
            $(options.gridId).setGridParam({ page:page }).trigger("reloadGrid");
        }else if(typeof options.getPageParam == 'function'){
            $(options.gridId).setGridParam({ page:page, postData: options.getPageParam() }).trigger("reloadGrid");
        }
    };

    function nextPage(arg) {
    	 if(arg.page < arg.total){
             reloadPage(arg, arg.page+1);
    	 }
    };
    
    function beforePage(arg) {
    	if(arg.page > 1 && arg.page <= arg.total){
             reloadPage(arg, arg.page-1);
    	 }    	
    };
    
    function lastPage(arg) {
    	 if(arg.page < arg.total){
             reloadPage(arg, arg.total);
    	 }    	
    };
    
    function firstPage(arg) {
    	if(arg.page > 1){
            reloadPage(arg, 1);
   	 	}
    };
    

	var methods = {
        options: function() {
            return options;
        },
		init: function(argument) {
			var self = this;
			var options = $.extend({ el: self }, argument);
			render(options);
		}
	};

	$.fn.pager = function(method) {
		if(methods[method]) {
			return methods[method].apply( this, Array.prototype.slice.call( arguments, 1 ));
		}else if( typeof method === 'object' || !method) {
			return methods.init.apply( this, arguments );
		}else {
			$.error('Method ' +  method + ' does not exist on jQuery.pager');
		}

	};

})(jQuery);