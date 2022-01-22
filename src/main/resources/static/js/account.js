$(document).ready(function () {
    $(".edit_input").on("keydown",function (){
        if($("input[name=insta]").val() === ""){
            $('#edit_button').attr("disabled", true);
        }
        else{
            $('#edit_button').attr("disabled", false);
        }
    });
    $("#edit_button").click(Edit);
    $("#change_profile_image_button").click(function (){
        $('#profile_image_change_modal').css("display", "flex");
    })

})
function Edit(){
    $.ajax({
        url : "/edit",
        type : "POST",
        dataType : "json",
        data : $("#edit_form").serialize(),
        beforeSend : function(){

        },
        success : function (response){
            if(response.state === 200){

            }else{
                $("#edit_form")[0].reset();
                $('#edit_button').attr("disabled", true);
            }

            console.log(response)
        },
        complete : function (){

        }

    })
}