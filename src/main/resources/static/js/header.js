let searched = false;
let firstRecently = false;
$(document).ready(function () {

    $("#search_input").focus(search_focus);
    $("html").click(function(e){
        if(!$(e.target).hasClass("search")){
            search_blur()
        }
        if(!$(e.target).hasClass("header_user_img")){
            $(".header_dropdown").css("display", "none");
        }
    })

    $("#search_input").on("keyup", search);

    $(".user_icon").click(function (){
        $(".header_dropdown").css("display", "flex");
    });

    $('#new_post_icon').on('click', function (e){
        console.log("클뤽")
        $(this).hide();
        $('#new_post_icon_select').show();
        $('.modal_background_post').show();
        $('body').css('overflow', 'hidden');
    })
});

function search_focus(){
    $("#search_result").removeAttr("style");
    $("#search_icon").css("display","none");
    $("#search_input").css("color", "black");
    $("#search_x").css("display", "block");
    $("#search_result").css("display","flex");
    recently();
}
function search_blur(){
    $("#search_icon").css("display","block");
    $("#search_input").css("color", "#8e8e8e");
    $("#search_x").css("display", "none");
    //$("#search_result").delay(10000).css("display","none");
    $("#search_result").animate({top: 38, opacity: "0"}, 100, function(){
        $("#search_result").css('display', 'none');
    });
}
function search(){
    if($("#search_input").val() == ""){
        $("#recently_Users").parent().css("display","flex")
        $("#search_list").css("display", "none");
        $("#search_no_list").css("display", "none");
        searched = false;
    }else {
        $("#search_loading").css("display", "block");
        $.ajax({
            url: "/header/search",
            type: "POST",
            dataType: "json",
            data: {
                word: $("#search_input").val()
            },
            beforeSend: function () {
                $("#search_x").css("display", "none");
                $("#search_no_list").css("display", "none");
                if (searched) {
                    $("#search_loading_box").css("display", "flex");
                }
            },
            success: function (response) {
                if(response.data.length != 0){
                    setUser("search", response);
                }else{
                    $("#search_recently").css("display", "none");
                    $("#search_no_list").css("display", "flex");
                }

                searched = true;
            },
            complete: function () {
                $("#search_x").css("display", "block");
                $("#search_loading").css("display", "none")
            }

        })
    }
}
function setUser(msg, response){
    console.log(msg);
    console.log(response);
    if(msg == "search"){
        $("#search_list").children().remove();

    }
    for(let i = 0; i < response.data.length; i++){
        let user = response.data[i]
        let box = $('<a class="search_recently_user" href="/'+user.insta +'"></a>')
        box.append($('<img class="search_recently_user_img" src = "http://minstagram.kro.kr/static/images/'+user.profile_image +'">'))
        let userInfo = $('<div class="search_recently_user_text"></div>')
        userInfo.append($('<span class="font_bold">'+ user.insta +'</span>'))
        userInfo.append($('<span class="color_8e8e8e">'+ user.name+' </span>'))
        box.append(userInfo)
        if( msg == "recently"){
            let button = $('<button class="background_logo1 del_x_icon search" onclick="delRecently(this)"></button>')
            button.click(function (event){
                event.preventDefault();
            })
            box.append(button);
            $("#recently_Users").append(box);
        }
        else if(msg == "search"){
            $("#search_list").append(box);

        }
    }
    if (msg == "recently"){
        $("#recently_Users").parent().css("display","flex")
        $("#search_list").css("display", "none");
    } else if(msg == "search"){
        $("#recently_Users").parent().css("display","none")
        $("#search_list").css("display", "flex");
    }
    $("#search_loading_box").css("display", "none");
}
function recently(){
    if(!firstRecently){

        $.ajax({
            url : "/header/recently",
            type : "POST",
            dataType : "json",
            beforeSend : function (){
                $("#search_loading_box").css("display", "flex");
                $("#search_no_list").css("display", "none");
            },
            success : function (response){
                console.log(response)
                if(response.data.length != 0){

                    setUser("recently", response);
                }else{
                    $("#search_no_recently").css("display", "flex");
                    $("#search_loading_box").css("display", "none");
                }
                firstRecently = true;
            }
        })
    }
}

function setRecentlyUser(response){
    for(let i = 0; i < response.data.length; i++){
        let user = response.data[i]
        let box = $('<a class="search_recently_user" href=""></a>')
            box.append($('<img class="search_recently_user_img" src = "http://minstagram.kro.kr//static/images/'+user.profile_image +'">'))
            let userInfo = $('<div class="search_recently_user_text"></div>')
                userInfo.append($('<span class="font_bold">'+ user.insta +'</span>'))
                userInfo.append($('<span class="color_8e8e8e">'+ user.name+' </span>'))
            box.append(userInfo)
            let button = $('<button class="background_logo1 del_x_icon search" onclick="delRecently(this)"></button>')
                button.click(function (event){
                    event.preventDefault();
                })
            box.append(button);
        $("#recently_Users").append(box);
    }
    $("#search_loading_box").css("display", "none");
}

function delRecently(e){
    let insta = $(e).prev().children().first().html();
    $(e).parent().attr("disabled", true);
    $.ajax({
        url:"/header/recently/delete",
        type: "POST",
        dataType : "json",
        data : {
            insta : insta
        },
        success : function (response){
            if(response.state == 200){
                $(e).parent().remove();
                console.log($("#recently_Users").children('a').length)
                if($("#recently_Users").children('a').length == 0){
                    $("#search_no_recently").css("display", "flex");
                }
            }
        }
    })

}

function delRecentlyAll(e){
    $.ajax({
        url:"/header/recently/deleteAll",
        type: "POST",
        dataType : "json",
        success : function (response){

            if(response.state == 200){
                console.log(response)
                $(e).parent().next().children('a').remove();
                $(e).parent().next().children().first().css("display","flex");
            }
        }
    })
}