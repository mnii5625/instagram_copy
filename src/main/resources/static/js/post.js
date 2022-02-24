

$(document).ready(function () {
    Posts(5, "", new Date());

    $('.post_right_button').on('click', image_right_slide);
    $('.post_left_button').on('click', image_left_slide);
    $('.post_write_comment_button').on('click', uploadComment);
    $('.post_like_icon').on('click', post_like);
    $('.post_unlike_icon').on('click', post_unlike);
    $('.post_comment_icon').on('click', show_post);
});
function Posts(n, insta, date) {
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
            for(let i = 0; i < response.data.length; i++){
                setP(response.data[i]);
            }
            //console.log(response);
            //setPost(response.data);
        }
    })
}
function setPost(data){
    console.log(data)
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
                    ul.append('<li><img src="http://minstagram.kro.kr/static/images/' + data.images[i] + '" ></li>');
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
                postLike.append($('<a class="post_insta" href=/'+ data["insta"] +'>'+ randomLikeUser(data["like"]) +'</a>'));
                postLike.html(postLike.html() + "님&nbsp;");
                postLike.append($('<button class="post_comment_button">여러 명</button>'))
                postLike.html(postLike.html() + "이 좋아합니다");
            postInfo.append(postLike);
        }else if(data["like"].length == 1){
            let postLike = $('<div class="post_like"></div>');
                postLike.append($('<a class="post_insta" href=/'+ data["like"][0] +'>'+ data["like"][0] +'</a>'));
                postLike.html(postLike.html() + "님이 좋아합니다");
            postInfo.append(postLike);
        }
        // 코멘트 구현
        let comments = getAllComments(data["comments"]);
        for(let i = 0; i < comments.length; i++){
            let postComments = $('<div class="post_comments"></div>')
                let c = comments[i];
                let span = $('<span></span>');
                    span.html('<a class="post_insta" href=/'+c["insta"]+'>'+c["insta"]+'</a>\n')
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
            out.html(out.html() + '\n<a class="tag" href=/'+ List[i].substring(1) +'>'+ List[i]+'</a>\n');
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
                return hour + "시간 전";
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
    let post = $(this).parents('.post');

    let input = post.find('input')
    console.log(post)
    let n = Number(input.val());
    let max = Number(input.attr('max'));
    let slider = post.find('.post_images');

    if(n+1 === max){
        $(this).hide();
    }
    post.find('.post_left_button').show();
    let left = -598 * (n+1)
    slider.animate({
        left: left
    },300)
    let dots = post.find('.post_images_dots');
    dots.children().css("background-color", "#a8a8a8");
    dots.children().eq(n+1).css("background-color", "#0095f6");
    input.val(n+1);


}
function image_left_slide(e){
    let post = $(this).parents('.post');

    let input = post.find('input')
    console.log(post)
    let n = Number(input.val());
    let slider = post.find('.post_images');

    if(n-1 == 0){
        $(this).hide();
    }
    post.find('.post_right_button').show();
    let left = -598 * (n-1)
    slider.animate({
        left: left
    },300)
    let dots = post.find('.post_images_dots');
    dots.children().css("background-color", "#a8a8a8");
    dots.children().eq(n-1).css("background-color", "#0095f6");
    input.val(n-1);
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
function setP(data){
    console.log(data);
    let clone = $('#post_clone').clone(true);
        clone.data('id', data.id);
        clone.find('.post_user_img').attr('src', "http://minstagram.kro.kr/static/images/" + data.profileImage);
        clone.find('.post_user_img').src = 'http://minstagram.kro.kr/static/images/' + data.profileImage;
        clone.find('.post_name').html(data.insta);
        clone.find('.post_name').attr('href', '/'+data.insta);

        let post_images = clone.find('.post_images')

        let post_images_dots = clone.find('.post_images_dots');
            if(data.images.length === 1){
                post_images_dots.hide();
                clone.find('.post_right_button').hide();
            }
            for(let i = 0; i< data.images.length; i++){
                let dot = $('<div class="post_images_dot"></div>')
                if(i === 0){
                    dot.css("background-color", "#0095f6");
                }
                post_images_dots.append(dot);
                let img = $('<img>');
                let src = 'http://minstagram.kro.kr/static/images/' + data.images[i];
                    img[0].src = src
                post_images.append(img);
            }
        let input = clone.find('input');
            input.attr('max', data.images.length-1);
        if(data.like.includes($('#insta').val())){
            clone.find('.post_unlike_icon').show();
        }
        if(data.like.length > 1){
            let postLike = clone.find('.post_like');
            let user = randomLikeUser(data["like"]);
            postLike.append($('<a class="post_insta" href=/'+ user +'>'+ user +'</a>'));
            postLike.html(postLike.html() + "님&nbsp;");
            postLike.append($('<button class="post_comment_button">여러 명</button>'))
            postLike.html(postLike.html() + "이 좋아합니다");
        }else if(data.like.length === 1){
            let postLike = clone.find('.post_like');
            postLike.append($('<a class="post_insta" href=/'+ data["like"][0] +'>'+ data["like"][0] +'</a>'));
            postLike.html(postLike.html() + "님이 좋아합니다");
        }
        // 글 내용
        let post_comment = clone.find('#post_comment');
            let post_insta = post_comment.find('.post_insta');
                post_insta.html(data.insta);
                post_insta.attr('href', '/' + data.insta);
            let span = post_comment.children('span');
                span.append('\n')
                span.append(Tag(data.comment));

        // 댓글
        let comments = getAllComments(data.comments);
        for(let i =0; i<comments.length; i++){
            let postComments = $('<div class="post_comment"></div>')
            let c = comments[i];
            let span = $('<span></span>')
            span.html('<a class="post_insta" href=/'+c["insta"]+'>'+c["insta"]+'</a>\n')
            span.append(c["comment"]);
            postComments.append(span)
            //postComments.append(c["comment"]);
            clone.find('.post_comments').append(postComments);
        }
        clone.find(".post_time").html(time(data.date));
        clone.removeAttr('style');
        $('#main_left').append(clone);
}
function uploadComment(e){
    let post = $(this).parents('.post')
    let id = post.data('id');
    let textarea = post.find('.post_write_comment_textarea');
    let comment = textarea.val();
    if(comment !== ""){
        $.ajax({
            url: '/comment',
            data: {
                id : id,
                comment : comment,
            },
            method : "POST",
            dataType: "Json",
            success : function(data){
                console.log('성공', data);
                if(data.state === 200){
                    let postComments = $('<div class="post_comment"></div>')
                    let span = $('<span></span>')
                    span.html('<a class="post_insta" href=/'+ $("#insta").val() +'>'+ $("#insta").val() +'</a>\n')
                    span.append(comment);
                    postComments.append(span)
                    post.find('.post_comments').append(postComments);
                    textarea.val("");
                }
            }
        })
    }
}
function post_like(){
    let post = $(this).parents('.post')
    let docId = post.data('id');
    $.ajax({
        url:'/like',
        method: 'POST',
        dataType : 'JSON',
        data : {
            type : "post",
            id : docId
        },
        success : function (response){
            console.log(response);
            if(response.state === 200){
                post.find('.post_unlike_icon').show();
            }
        }
    })
}
function post_unlike(){
    let post = $(this).parents('.post')
    let docId = post.data('id');
    $.ajax({
        url:'/unlike',
        method: 'POST',
        dataType : 'JSON',
        data : {
            type : "post",
            id : docId
        },
        success : function (response){
            console.log(response);
            if(response.state === 200){
                post.find('.post_unlike_icon').hide();
            }
        }
    })
    return false;
}
function show_post(){
    let post = $(this).parents('.post')
    let docId = post.data('id');
    get_post(docId);
}

