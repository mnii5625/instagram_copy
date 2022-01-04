let httpRequest = new XMLHttpRequest();
$(document).ready(function () {

    win_resize();
    $(window).resize(win_resize);
    let count = 0;
    // 이미지 fade in / fade out 반복
    let images = $(".img").get();
    let show = setInterval(function (){
        $(".img_back").removeClass("img_back");
        let fadeIn = $(".img_fadeIn");
        fadeIn.addClass("img_back");
        fadeIn.removeClass("img_fadeIn");
        $(images[count]).addClass("img_fadeIn");

        count++
        if(count >= images.length){
            count -= images.length;
        }

    }, 5000);
    id_event()
    pw_event()
    //글자 입력되면 스케일 작아지고 위로
    $("#id").on("keyup change paste load", id_event)
    $("#password").on("keyup change paste load", pw_event);
    $("#login_button").click(function(){
        login();
    })

});
function win_resize(){
    if(window.innerWidth < 450){
        $(".box").css("border", "none");
        $(".box").css("background", "#fafafa");

        $(".iphone").css("display", "none");
    }else{
        $(".box").css("border", "1px solid gainsboro");
        $(".box").css("background", "white");
        if(window.innerWidth < 850){
            $(".iphone").css("display", "none");
        }
        else{
            $(".iphone").css("display", "block");
        }
    }
}
function id_event(){
    if($("#id").val() != ""){
        $($(".placeholder").get(0)).addClass("transition_label");
        $("#id").addClass("transition_input");
    }
    else{
        $($(".placeholder").get(0)).removeClass("transition_label");
        $("#id").removeClass("transition_input");
    }
}

function pw_event(){
    if($("#password").val() != ""){
        $($(".placeholder").get(1)).addClass("transition_label");
        $("#password").addClass("transition_input");
    }else{
        $($(".placeholder").get(1)).removeClass("transition_label");
        $("#password").removeClass("transition_input");
    }
}
function login(){
    $.ajax({
        url:"/",
        type : "POST",
        dataType : "json",
        data : {
            id : $("#id").val(),
            password : $("#password").val()
        },
        beforeSend : function (){
            $("#login_ajax_loading").css("display", "flex");
            $("#login_button").html("");

        },
        success : function (response){
            console.log(response);
            console.log(response['message']);
            if(response['state'] == 200){
                location.reload();
            }else{
                console.log("실패");
                $("#error").html(response['message']);
            }

        },
        complete: function(){
            $("#login_ajax_loading").css("display", "none");
            $("#login_button").html("로그인");
        },
        error : function(a,b,c){
            console.log(b + ": " + c);
        }
    })
}