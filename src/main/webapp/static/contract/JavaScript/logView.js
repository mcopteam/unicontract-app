/**
 * Created by lz on 2017/6/20.
 */

var myalert = function(){
    // alert("服务器提出了一个问题，请找运维寻求答案！")
    console.info("服务器提出了一个问题，请找运维寻求答案！")
};
var logView = function(){
    // setInterval(timer,1000);
    $.ajax({
        url: LOGVIEW_URL1,
        type: 'GET',
        success: function(data){
            console.info(data)
            if(data != 500){
                for(var i=0;i<data.length;i++){
                    $('.log_view1').append('<p class="son-log">'+data[i]+'</p>');
                }
                var container = $('.log_view1'), scrollTo = $('.son-log:last');
                container.scrollTop(
                    scrollTo.offset().top - container.offset().top + container.scrollTop()
                );
                var t = setInterval(function () {
                    $.ajax({
                        url: LOGVIEW_URL1,
                        type: 'GET',
                        success: function(data){
                            if(data != 500){
                                $('.log_view1').empty();
                                for(var i=0;i<data.length;i++){
                                    $('.log_view1').append('<p class="son-log">'+data[i]+'</p>');
                                }
                                $('.log_view1').append('<p>|</p>');
                                // var container = $('#logview'), scrollTo = $('.son-log:last');
                                // container.scrollTop(
                                //     scrollTo.offset().top - container.offset().top + container.scrollTop()
                                // );
                                if ($('.log_view').css('display') == 'none'){
                                    clearInterval(t);
                                }
                            }else{
                                clearInterval(t);
                            }
                        },
                        error: function(data) {
                            myalert();
                            return false;
                        }
                    });
                },1000);
            }else{
                myalert();
            }
        },
        error: function(data) {
            myalert();
            return false;
        }
    });
    $.ajax({
        url: LOGVIEW_URL2,
        type: 'GET',
        success: function(data){
            console.info(data)
            if(data != 500){
                for(var i=0;i<data.length;i++){
                    $('.log_view2').append('<p class="son-log2">'+data[i]+'</p>');
                }
                var container = $('.log_view2'), scrollTo = $('.son-log2:last');
                container.scrollTop(
                    scrollTo.offset().top - container.offset().top + container.scrollTop()
                );
                var t2 = setInterval(function () {
                    $.ajax({
                        url: LOGVIEW_URL2,
                        type: 'GET',
                        success: function(data){
                            if(data != 500){
                                $('.log_view2').empty();
                                for(var i=0;i<data.length;i++){
                                    $('.log_view2').append('<p class="son-log2">'+data[i]+'</p>');
                                }
                                $('.log_view2').append('<p>|</p>');
                                // var container = $('#logview'), scrollTo = $('.son-log:last');
                                // container.scrollTop(
                                //     scrollTo.offset().top - container.offset().top + container.scrollTop()
                                // );
                                if ($('.log_view').css('display') == 'none'){
                                    clearInterval(t2);
                                }
                            }else{
                                clearInterval(t2);
                            }
                        },
                        error: function(data) {
                            myalert();
                            return false;
                        }
                    });
                },1000);
            }else{
                myalert();
            }
        },
        error: function(data) {
            myalert();
            return false;
        }
    });
};