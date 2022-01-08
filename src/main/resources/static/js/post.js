

$(document).ready(function () {
    Posts(5);
    let data = {
        comment: "너무귀엽당",
        insta: "ming._.i",
        like: [
            "ming._.i",
            "mmiinini"
        ],
        date: "2022-01-01T15:00:00.000+00:00",
        images: [
            "1407e9aa-741f-46b4-a09e-be56e79ac805_GUGU.jpg"
        ],
        comments: [
            {
                bundle: "",
                comment: "넘귀탱",
                depth: 0,
                insta: "ming._.i",
                like: [

                ],
                date: "2021-12-31T15:00:00.000+00:00",
                replies: null,
                post: "test"
            },
            {
                bundle: "random",
                comment: "너무귀엽당ㅋㅋㅋ",
                depth: 0,
                insta: "ming._.i",
                like: [
                    "ming._.i",
                    "mmiinini"
                ],
                date: "2022-01-02T15:00:00.000+00:00",
                replies: [
                    {
                        bundle: "random",
                        comment: "너무귀엽당2",
                        depth: 1,
                        insta: "ming._.i",
                        like: [
                            "ming._.i"
                        ],
                        date: "2022-01-01T15:00:00.000+00:00",
                        replies: null,
                        post: "test"
                    }
                ],
                post: "test"
            }
        ]
    }
    //setPost(data);
});
function Posts(num) {
    $.ajax({
        url: "/post",
        type: "POST",
        dataType: "json",
        data: {
            Date: new Date(),
            n: num
        },
        success: function (response) {
            for(let i = 0; i < response.data.length; i++){
                setPost(response.data[i]);
            }
            //console.log(response);
            //setPost(response.data);
        }
    })
}
function setPost(data){
    let post = $('<div class="post"></div>');
    // 게시물 헤더
    let postHeader = $('<div class="post_header"></div>');
        postHeader.append($('<div class="post_new_story_gradation"><img class="post_user_img" src="images/GUGU.jpg"></div>'));
        postHeader.append($('<span class="post_name">'+data["insta"]+'</span>'));
        postHeader.append($('<div class="background_logo2 post_dots"></div>'))
    post.append(postHeader);
    // 게시물 이미지 / 이미지 설정 슬라이드로 변경 해야됨;
    let postImage = $('<div class="post_image"></div>');
        let postImageSlider = $('<div class="post_image_slider"></div>')
            postImageSlider.append($('<button class="post_left_button" style="display: none" onclick="image_left_slide(this)"></button>'))
            let input = $('<input type="hidden" min = "0" value = "0">')
                input.attr("max", parseInt(data.images.length)-1);
            postImageSlider.append(input);
            let ul = $('<ul class="post_images"></ul>');
                for(let i = 0; i<data.images.length; i++){
                    ul.append('<li><img src="http://localhost:8080/static/images/' + data.images[i] + '" ></li>');
                }
            postImageSlider.append(ul);
            if(data.images.length != 1){
                postImageSlider.append($('<button class="post_right_button" onclick="image_right_slide(this)"></button>'))
            }
            postImageSlider.append()
        postImage.append(postImageSlider);

            let postImagesDots = $('<div class="post_images_dots"></div>');
                if(data.images.length != 1) {
                    for (let i = 0; i < data.images.length; i++) {
                        if (i == 0) {
                            postImagesDots.append('<div class="post_images_dot" style="background-color: #0095f6"></div>')
                        } else {
                            postImagesDots.append('<div class="post_images_dot"></div>')
                        }
                    }
                }
        postImage.append(postImagesDots);
    post.append(postImage);
    // 게시물 내용
    let postInfo = $('<div class="post_info"></div>')
        let postIcons = $('<div class="post_icons"></div>')
            let postIconsLeft = ($('<div class="post_icons_left"></div>'));
                postIconsLeft.append($('<div class="background_logo2 post_like_icon"></div>'));
                postIconsLeft.append($('<div class="background_logo2 post_comment_icon"></div>'));
                postIconsLeft.append($('<div class="background_logo2 post_share_icon"></div>'));
            postIcons.append(postIconsLeft);
            postIcons.append('<div class="background_logo2 post_bookmark_icon"></div>')
        postInfo.append(postIcons);
        // 좋아요 구현
        if(data["like"].length > 1){
            let postLike = $('<div class="post_like"></div>');
                postLike.append($('<a class="post_insta" href="">'+ randomLikeUser(data["like"]) +'</a>'));
                postLike.html(postLike.html() + "님&nbsp;");
                postLike.append($('<button class="post_comment_button">여러 명</button>'))
                postLike.html(postLike.html() + "이 좋아합니다");
            postInfo.append(postLike);
        }else if(data["like"].length == 1){
            let postLike = $('<div class="post_like"></div>');
                postLike.append($('<a class="post_insta" href="">'+ data["like"][0] +'</a>'));
                postLike.html(postLike.html() + "님이 좋아합니다");
            postInfo.append(postLike);
        }
        // 코멘트 구현
        let comments = getAllComments(data["comments"]);
        for(let i = 0; i < comments.length; i++){
            let postComments = $('<div class="post_comments"></div>')
                let c = comments[i];
                let span = $('<span></span>');
                    span.html('<a class="post_insta" href="">'+c["insta"]+'</a>\n')
                postComments.append(span)
                postComments.append(c["comment"]);
            postInfo.append(postComments);
        }
        // n 시간전 구현
        let postTime = $('<div class="post_time"></div>');
            postTime.html(time(data["date"]));
        postInfo.append(postTime);
    post.append(postInfo);
    // 댓글 달기 구현
    let postWriteComment = $('<div class="post_write_comment"></div>')
        postWriteComment.append($('<div class="post_emoticon"></div>'))
        let textarea = $('<textarea placeholder="댓글 달기..." class="post_write_comment_textarea"oninput="resize(this)" style="overflow-y: hidden"></textarea>')
            textarea.on('keydown', function (event){
                if(event.keyCode == 13){
                    if(!event.shiftKey){
                        alert("TODO : 댓글 달기 구현")
                        $(this).val('');
                        resize(this);
                        return false;
                    }
                }
            })
        postWriteComment.append(textarea);
        postWriteComment.append($('<button class="post_write_comment_button">게시</button>'));
    post.append(postWriteComment);
    $("#main_left").append(post);

}
function randomLikeUser(like){
    return like[Math.floor(Math.random() * like.length )];
}
function getAllComments(comments){
    let out = [];
    for(let i=0; i<comments.length; i++){
        let c = comments[i];
        out.push(jsonComment(c["insta"], Tag(c["comment"]), c["depth"]));
        if(c["replies"] != null){
            for(let j =0; j<c["replies"].length; j++){
                let r = c["replies"][j];
                out.push(jsonComment(r["insta"],Tag(r["comment"]), r["depth"]));
            }
        }
    }
    return out;
}
function jsonComment(insta, comment, depth){
    let json = {
        insta : insta,
        comment : comment,
        depth : depth
    }
    return json;
}
function Tag(comment){
    comment = replaceAll(comment);
    let out = $('<span></span>');
    let temp = "";
    let List = comment.split(" ");
    for(let i = 0; i < List.length; i++){
        if(List[i][0] == "@"){
            out.html(out.html() + temp);
            temp = "";
            out.html(out.html() + '\n<a class="tag">'+ List[i]+'</a>\n');
        }
        else{
            temp += List[i] + " ";
        }
    }
    out.html(out.html() + temp);
    return out;

}
function replaceAll(text){
    while(text.includes("\n")){
        text = text.replace("\n", "<br>");
    }
    return text;
}
function time(date){
    let now = new Date().getTime();
    let before = new Date(date);
    let pass = now - before.getTime();
    let second = Math.floor(pass / 1000);
    if (second < 60){
        return second + "초 전";
    }else{
        let minute = Math.floor(second / 60);
        if(minute < 60){
            return minute + "분 전";
        }else{
            let hour = Math.floor(minute / 60);
            if(hour < 24){
                return hour + "시 전";
            }
            else{
                let day = Math.floor(hour / 24);
                if(day < 8){
                    return day + "일 전";
                }
                else{
                    return before.getFullYear() + "년 "+
                        (before.getMonth() + 1) + "월 " +
                        before.getDate() + "일";
                }
            }
        }
    }
}
function image_right_slide(e){
    let val = parseInt($(e).prev().prev().val());
    let min = parseInt($(e).prev().prev().attr('min'));
    let max = parseInt($(e).prev().prev().attr('max'));
    $(e).prev().prev().val(val + 1);
    if(val+1 == max){
        $(e).css("display","none");
    }
    if(val == min){
        $(e).prev().prev().prev().css("display","block");
    }
    $(e).prev().animate({marginLeft: ((val+1) * -598) + "px"}, 300);
    $(e).next().next().animate({marginLeft: ((val-1) * -598) + "px"}, 300);
    let dots = $(e).parents().next(".post_images_dots");
    dots.children().css("background-color", "#a8a8a8");
    dots.children().eq(val+1).css("background-color", "#0095f6");
}
function image_left_slide(e){
    let val = parseInt($(e).next().val());
    let min = parseInt($(e).next().attr('min'));
    let max = parseInt($(e).next().attr('max'));
    $(e).next().val(val - 1);
    if(val-1 == min){
        $(e).css("display","none");
    }
    if(val == max){
        $(e).next().next().next().css("display","block");
    }
    $(e).next().next().animate({marginLeft: ((val-1) * -598) + "px"}, 300);
    let dots = $(e).parents().next(".post_images_dots");
    dots.children().css("background-color", "#a8a8a8");
    dots.children().eq(val-1).css("background-color", "#0095f6");
}
function resize(e){
    $(e).height("18px");
    if(e.scrollHeight >= 80){
        $(e).height(80);
        $(e).css("overflow-y", "scroll")
    }
    else{
        $(e).css("overflow-y", "hidden")
        $(e).height(e.scrollHeight);
    }
}