$(document).ready(function () {

    $("#search_input").focus(search_focus);
    $("html").click(function(e){
        if(!$(e.target).hasClass("search")){
            search_blur()
        }
    })

    $("#search_input").on("keyup change paste load", search);
});

function search_focus(){
    $("#search_icon").css("display","none");
    $("#search_input").css("color", "black");
    $("#search_x").css("display", "block");
    $("#search_result").css("display","flex");
}
function search_blur(){
    $("#search_icon").css("display","block");
    $("#search_input").css("color", "#8e8e8e");
    $("#search_x").css("display", "none");
    $("#search_result").css("display","none");
}
function search(){
    $.ajax({
        url:"",
        type : "POST",
        dataType : "json",
        data : {
            value : $("#search_input").val()
        },
        beforeSend : function (){
            $("#search_x").css("display", "none");
            $("#search_loading").css("display", "block");
        },
        complete : function(){
            $("#search_x").css("display", "block");
            $("#search_loading").css("display", "none")
        }

    })
    //ajax로 검색 구현
}
