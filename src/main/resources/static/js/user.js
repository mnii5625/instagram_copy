$(document).ready(function () {
    Posts(-1, $("#insta").html(), new Date());
    console.log($('#insta').html())
    $(".follow_button").click(follow);
    $(".f4f_button").click(follow);
    $(".unfollow_button").click(function (){
        $("#unfollow_modal").css("display", "flex");
    })
    $('.open_modal_block').click(function (){
        $('#block_modal').css("display", "flex");
    })
});
function Posts(n, insta, date){
    $.ajax({
        url: "/post",
        type: "POST",
        dataType: "json",
        data: {
            Date: date,
            insta : insta,
            n: n
        },
        success: function (response) {
            console.log(response)
            setPostGrid(response.data);
        }
    })
}
function setPostGrid(data){
    let posts = $(".posts");
    let length = data.length;
    let imgPath = "http://minstagram.kro.kr/static/images/"
    for(let i = 0; i<Math.ceil(length / 3)*3; i++){
        console.log(i);
        if((i % 3) === 0){
            let postLine = $('<div class="post_line"></div>')
                posts.append(postLine)
        }
        let post = $('<div class="post"></div>');
        if(i >= length){
            console.log("넘음");
            $($('.post_line').get(Math.floor(i/3))).append(post);
        }else{
            console.log("안넘음");
            let postA = $('<a class="post_a"></a>');
                postA.attr("href", "#");
                let img = $('<img>')
                    img.attr("src", imgPath + data[i].images[0])
                postA.append(img);
            post.append(postA);
            $($('.post_line').get(Math.floor(i/3))).append(post);

        }

    }


}
function follow(){
    $.ajax({
        url: "/follow",
        type: "POST",
        dataType: "json",
        data :{
            follow : $('#insta').html()
        },
        success: function (response){
            console.log(response);
        }
    });
}
