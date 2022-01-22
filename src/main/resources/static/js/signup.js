let boxes = ["#page1", "#page2", "", "#TermsPage"];
let pages = 0;
let next = [false, false, false, false];
$(document).ready(function () {
    set_select();
    $("#test").click(next_pages);
    $("#test2").click(prev_pages);
    $("#id").on("keyup change paste input", function (){
        if($("#id").val() != ""){
            $($(".placeholder").get(0)).addClass("transition_label");
            $("#id").addClass("transition_input");

        }
        else{
            $($(".placeholder").get(0)).removeClass("transition_label");
            $("#id").removeClass("transition_input");
            $("#id_c").css("display", "none");
            $("#id_x").css("display", "none");
        }
    });
    $("#name").on("keyup change paste input", function (){
        if($("#name").val() != ""){
            $($(".placeholder").get(1)).addClass("transition_label");
            $("#name").addClass("transition_input");
        }
        else{
            $($(".placeholder").get(1)).removeClass("transition_label");
            $("#name").removeClass("transition_input");
            $("#name_c").css("display", "none");
            $("#name_x").css("display", "none");

        }
    });
    $("#insta").on("keyup change paste input", function (){
        if($("#insta").val() != ""){
            $($(".placeholder").get(2)).addClass("transition_label");
            $("#insta").addClass("transition_input");
        }
        else{
            $($(".placeholder").get(2)).removeClass("transition_label");
            $("#insta").removeClass("transition_input");
        }
    });
    $("#password").on("keyup change paste input", function (){
        if($("#password").val() != ""){
            $($(".placeholder").get(3)).addClass("transition_label");
            $("#password").addClass("transition_input");
            $(".password_button").css("display","block");
        }
        else{
            $($(".placeholder").get(3)).removeClass("transition_label");
            $("#password").removeClass("transition_input");
            $(".password_button").css("display","none");
        }
    });
    $("#phone_text").on("keyup change paste input", function(){
        if($("#phone_text").val() != ""){
            $($(".placeholder").get(4)).addClass("transition_label");
            $("#phone_text").addClass("transition_input");
        }
        else{
            $($(".placeholder").get(4)).removeClass("transition_label");
            $("#phone_text").removeClass("transition_input");
        }
    });
    $("#email_text").on("keyup change paste input", function(){
        if($("#email_text").val() != ""){
            $($(".placeholder").get(5)).addClass("transition_label");
            $("#email_text").addClass("transition_input");
        }
        else{
            $($(".placeholder").get(5)).removeClass("transition_label");
            $("#email_text").removeClass("transition_input");
        }
    });

    $("#id").change(function (){
        if($("#id").val() != "") {
            $.ajax({
                url: "/signup/username",
                data: {username: $("#id").val()},
                method: "POST",
                dataType: "json"
            })
                .done(function (out) {
                    if(out['exist'] =="true" || out['type'] == "none"){
                        $("#id_x").css("display", "block");
                        $("#id_c").css("display", "none");

                    }else{
                        $("#id_x").css("display", "none");
                        $("#id_c").css("display", "block");
                        if(out['type'] == "phone"){
                            $("#email").val("");
                            $("#phone").val($("#id").val());
                            boxes[2]= "#PhoneVerificationPage";
                            $("#phone_span").html($("#id").val()+"번으로 전송된 6자리 코드를 입력하세요");
                        }else{
                            $("#email").val($("#id").val());
                            $("#phone").val("");
                            boxes[2]= "#EmailVerificationPage";
                            $("#email_span").html($("#id").val()+" 주소로 전송된 인증 코드를 입력하세요");
                        }
                        next[0] = true;
                    }
                    check();
                })
        }else{
            $("#id_c").css("display", "none");
            $("#id_x").css("display", "none");
        }
    });
    $("#name").change(function (){
        if($("#name").val().length >= 2 && $("#name").val().length <= 30){
            $("#name_c").css("display", "block");
            $("#name_x").css("display", "none");
            next[1] = true;
        }else{
            $("#name_c").css("display", "none");
            $("#name_x").css("display", "block");
        }
        check();
    });
    $("#insta").change(function (){
        if($("#insta").val() != "") {
            $.ajax({
                url: "/signup/insta",
                data: { insta: $("#insta").val()},
                method: "POST",
                dataType: "json"
            })
                .done(function (out) {
                    if(out['exist'] =="true" || out['type'] == "none"){
                        $("#insta_x").css("display", "block");
                        $("#insta_c").css("display", "none");
                    }else{
                        $("#insta_x").css("display", "none");
                        $("#insta_c").css("display", "block");
                        next[2] = true;
                    }
                    check()
                })
        }
    });
    $("#password").change(function (){
       if($("#password").val() != ""){
            $.ajax({
                url:"/signup/password",
                data: {password: $("#password").val()},
                method: "POST",
                dataType: "json"
            })
                .done(function(out){
                    next[3] = out;
                    check();
                })
       }
    });

    $("#sel_month, #sel_year").change(change_date);
    $(".password_button").click(pw_button);
    $("#page1_button, #page2_button, #email_ver_button, #phone_ver_button").click(next_pages);
    $(".sel_days").click(function (){
        $("#birthday").val($("#sel_year").val() + "-"+$("#sel_month").val().split("월")[0] + "-" + $("#sel_date").val());
        $("#page2_button").attr("disabled", false);
    });
    $("#email_text").change(function (){
        $.ajax({
            url: "/signup/verification",
            data: {
                code: $("#email_text").val(),
                username: $("#id").val()
            },
            method: "POST",
            dataType: "json"
        })
                .done(function (out){
                    if(out){
                        $("#email_ver_button").attr("disabled", false);
                    }
                    else{
                        $("#email_ver_button").attr("disabled", true);
                    }

                })
    })
    $("#phone_text").change(function (){
        $.ajax({
            url: "/signup/verification",
            data: {
                code: $("#phone_text").val(),
                username: $("#id").val()
            },
            method: "POST",
            dataType: "json"
        })
            .done(function (out){
                if(out){
                    $("#phone_ver_button").attr("disabled", false);
                }
                else{
                    $("#phone_ver_button").attr("disabled", true);
                }

            })
    })

    $("#termall").click(function (){
        if($(this).is(":checked")){
            $("#term1").prop("checked", true);
            $("#term2").prop("checked", true);
            $("#term3").prop("checked", true);
            $("#term_button").attr("disabled", false);
        }else{
            $("#term1").prop("checked", false);
            $("#term2").prop("checked", false);
            $("#term3").prop("checked", false);
            $("#term_button").attr("disabled", true);
        }
    })
    $("#term1, #term2, #term3").click(function (){

        if($("#term1").is(":checked") && $("#term2").is(":checked") && $("#term3").is(":checked")){
            $("#term_button").attr("disabled", false);
            $("#termall").prop("checked", true);
        }else{
            $("#termall").prop("checked", false);
            $("#term_button").attr("disabled", true);
        }
    });

});

function set_select(){
    let date = new Date();
    let year = date.getFullYear();
    let month = date.getMonth()+1;
    let dow = date.getDate();
    let total_date = new Date(year, month, 0 ).getDate();
    for(let i = 0; i<100; i++){
        if(i == 1){
            $("#sel_year").append("<option selected>"+(year-i) + "</option>");
        }
        else{
            $("#sel_year").append("<option>"+(year-i) + "</option>");
        }
    }
    for (let i = 1; i<=12; i++){
        if(i == month){
            $("#sel_month").append("<option selected>"+i + "월</option>");
        }else{
            $("#sel_month").append("<option>"+i + "월</option>");
        }
    }
    for (let i = 1; i<=total_date; i++){
        if(i == dow){
            $("#sel_date").append("<option selected>"+i + "</option>");
        }else{
            $("#sel_date").append("<option>"+i + "</option>");
        }
    }
}
function change_date(){
    let year = $("#sel_year").val();
    let month = $("#sel_month").val().split("월")[0];
    let total_date = new Date(year, month, 0 ).getDate();
    let len = $("#sel_date option").length;
    if (len < total_date){
        for (let i = len+1; i<=total_date; i++){
            $("#sel_date").append("<option>"+i + "</option>");
        }
    }else if(len > total_date){
        $("#sel_date").val(total_date).attr("selected", "selected");
        for (let i = 0; i<len - total_date; i++){
            $("#sel_date option:last").remove();
        }
    }


}
function next_pages(){
    $(boxes[pages]).css("display","none");
    pages++;
    $(boxes[pages]).css("display","flex");
    if(pages == 2){
        if(boxes[2] == "#EmailVerificationPage"){
            $.ajax({
                url: "/signup/email",
                data: { email: $("#id").val() },
                method: "POST"
            });
        }else if(boxes[2] == "#PhoneVerificationPage"){
            $.ajax({
                url: "signup/phone",
                data: { phone: $("#id").val() },
                method: "POST"
            })
        }
    }
}
function prev_pages(){
    $(boxes[pages]).css("display","none");
    pages--;
    $(boxes[pages]).css("display","flex");
}
function pw_button(){
    if($(".password_button").html()=="숨기기"){
        $(".password_button").html("비밀번호 표시");
        $("#password").css("width", "170px")
        $("#password").attr("type", "password");
    }
    else{
        $(".password_button").html("숨기기");
        $("#password").css("width", "215px")
        $("#password").attr("type", "text");
    }

}
function check(){
    if(next[0] & next[1] & next[2] & next[3]){
        $("#page1_button").attr("disabled", false);
    }else{
        $("#page1_button").attr("disabled", true);
    }
}