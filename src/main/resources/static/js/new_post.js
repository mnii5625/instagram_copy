let files = [];
let order = [];
let page = 0;
let drag = false;
$(document).ready(function () {
    addEvent();
    modal_resize();

});
function addEvent(){
    // 모달창 클릭시 창 닫힘 방지
    $('.modal_container_post').on('click', function (e){
        e.stopPropagation();
    })
    // 드래그 앤 드랍 구현
    $('#image_pick').on("dragenter", function (e){
        e.preventDefault();
        e.stopPropagation();
    }).on("dragover", function(e){
        e.preventDefault();
        e.stopPropagation();
        $('.new_post_form_svg').attr("color", "#0095f6");
        console.log("dragover")
    }).on("dragleave", function (e){
        e.preventDefault();
        e.stopPropagation();
        $('.new_post_form_svg').attr("color", "#262626");
        console.log("dragleave")
    }).on("drop", function (e){
        e.preventDefault();

        let drop_files = e.originalEvent.dataTransfer.files;
        console.log(URL.createObjectURL(drop_files[0]));
        if(drop_files != null && drop_files != undefined){
            add_images(drop_files);
        }
    })
    //컴퓨터에서 선택 버튼
    $('.modal_image_input_button').click(function (){
        $('#modal_image_input').click();
    })
    $('.img_slider_gallery_add').on('click', function (){
        $('#modal_image_input').click();
    })
    //컴퓨터에서 선택
    $('#modal_image_input').change(function (){
        add_images($('#modal_image_input')[0].files);
    })
    // 백그라운드 클릭시 모달 닫힘
    $('.modal_background_post').on('click', modal_close);
    // 모달창 리사이즈
    $(window).resize(modal_resize);
    $(document).on('click', function (e){
        drag = false;
    })
    // 이미지 슬라이드 다음 버튼
    $($('.img_slider_icon')[0]).on('click', prev_img);
    $($('.img_slider_icon')[1]).on('click', next_img);
    $('.img_cut').children().on("click",cut_img);
    $('.zoom_input').on('input',zoom_img);
    $('.zoom_input').on('mouseup', function (e){
        e.stopPropagation()
        e.stopImmediatePropagation()
        e.preventDefault()
    })
    $('.modal_background_post').on("selectstart", function(){
        return false;
    });
    $('#img_slider_icon_mini_next').on('click', mini_next);
    $('#img_slider_icon_mini_prev').on('click', mini_prev);

    $('.img_slider_icon').eq(2).one('click', dis_cut);
    $('.img_slider_icon').eq(3).one('click', dis_zoom);
    $('.img_slider_icon').eq(4).one('click', dis_mini);

}
function Drag(e){

    let move = $(this);

    let slider = $('.modal_image_slider');
    let lastOffset = move.data('last');
    let newX = 0;
    let newY = 0;
    let lastOffsetX = lastOffset ? lastOffset.dx : 0,
        lastOffsetY = lastOffset ? lastOffset.dy : 0,
        lastScale = lastOffset ? lastOffset.scale : 1;
    let moveWidth = move.width() * lastScale;
    let moveHeight = move.height() * lastScale;
    let startX = e.pageX - lastOffsetX, startY = e.pageY - lastOffsetY;
    move.css("cursor", "grabbing");
    drag = true;
    function onMouseMove(e){
        $('body').css("cursor", "grabbing");
        newX = e.pageX - startX;
        newY = e.pageY - startY;

        if((slider.offset().left - move.offset().left) < 0){
            newX = (moveWidth / 2 - slider.width() /2) + (newX-(moveWidth / 2 - slider.width() /2))/6
        }
        if(((moveWidth + move.offset().left) - (slider.width() + slider.offset().left)) < 0){
            newX = ((moveWidth / 2 - slider.width() /2) * -1) + (newX+(moveWidth / 2 - slider.width() /2))/6;
        }
        if((slider.offset().top - move.offset().top < 0)){
            newY = (moveHeight / 2 - slider.height() /2)+ (newY-(moveHeight / 2 - slider.height() /2))/6
        }
        if(((moveHeight + move.offset().top) - (slider.height() + slider.offset().top)) < 0){
            newY = ((moveHeight / 2 - slider.height() /2) * -1) + (newY+(moveHeight / 2 - slider.height() /2))/6;
        }
        move.data('last', {dx:newX, dy:newY, scale:lastScale});
        let mini =  $($('.drag_mini')[move.index()]);
        TweenLite.to(move, 0.3, {
            css:{
                transform : 'translate(' + newX + 'px, '+newY+'px) scale('+ lastScale +','+ lastScale+')'
            }
        })
        let miniX = newX/move.width()*mini.width()/2;
        let miniY = newY/move.width()*mini.width()/2;
        TweenLite.to(mini, 0.3, {
            css:{
                transform : 'translate(' + miniX + 'px, '+miniY+'px) scale('+ lastScale +','+ lastScale+')'
            }
        })
    }

    $(document).mousemove(onMouseMove);
    $(document).on('mouseup', function (){

        //커서 오토로 변경
        $('body').css("cursor", "auto");
        // 커서 그랩으로 변경
        move.css("cursor", "grab");
        // 마우스 move 이벤트 해제
        $(document).off("mousemove");
        // 영역 넘어가는거 확인

        lastOffset = move.data('last');
        lastScale = lastOffset ? lastOffset.scale : 1;
        if((slider.offset().left - move.offset().left) < 0){
            newX = moveWidth / 2 - slider.width() /2;
        }
        if(((moveWidth + move.offset().left) - (slider.width() + slider.offset().left)) < 0){
            newX = (moveWidth / 2 - slider.width() /2) * -1;
        }
        if((slider.offset().top - move.offset().top < 0)){
            newY = moveHeight / 2 - slider.height() /2;
        }
        if(((moveHeight + move.offset().top) - (slider.height() + slider.offset().top)) < 0){
            newY = (moveHeight / 2 - slider.height() /2) * -1;
        }
        move.data('last', {dx:newX, dy:newY, scale: lastScale});

        TweenLite.to(move, 0.2, {
            css:{
                transform : 'translate(' + newX + 'px, '+newY+'px) scale('+ lastScale +','+ lastScale+')'
            }
        })
        let mini =  $($('.drag_mini')[move.index()]);
        let miniX = newX/move.width()*mini.width()/2;
        let miniY = newY/move.width()*mini.width()/2;
        TweenLite.to(mini, 0.3, {
            css:{
                transform : 'translate(' + miniX + 'px, '+miniY+'px) scale('+ lastScale +','+ lastScale+')'
            }
        })
        $(document).off('mouseup')
    })
}
function modal_close(){
    if(!drag) {
        $(".modal_background_post").css("display", "none");
    }
}
function modal_resize(){
    let window_width = window.innerWidth;
    let window_height = window.innerHeight;
    let side = 0;
    if(window_width > window_height){
        side = window_height * 0.7;

        $('.modal_image_container').width(side).height(side);

    }else{
        side = window_width * 0.7;

        $('.modal_image_container').width(side).height(side);
    }
}
function set_images(image_files){

    $('#nth-img').attr("max", image_files.length-1);
    if(image_files.length == 1){
        $('.modal_image_slider').next().css("display", "none")
        $('.img_slider_dots').css("display", "none")
    }else{
        $('.modal_image_slider').next().css("display", "flex")
        $('.img_slider_dots').css("display", "flex")
    }
    $('#img_slider_gallery_list').width((94 * image_files.length + 12*(image_files.length-1))+12);
    for(let i = 0; i<image_files.length; i++){
        let file = image_files[i];
        let dots = $('.img_slider_dots');
        let dot = $('<div class="img_slider_dot"></div>');
        order.push(i);
        if(i === 0){
            dot.css("background-color", "#0095f6")
        }
        dots.append(dot);


        let img = new Image();
        let src = URL.createObjectURL(file);
        img.src = src
        img.onload = function (){
            let slider = $('.modal_image_slider');
            let fileWidth = img.width;
            let fileHeight = img.height;
            let gallery = $('.img_slider_gallery_list');
            let galleryImg = $('<div class="img_slider_gallery_img"></div>');
                galleryImg.on("click", drag_mini_click);

            let d = $('<div class="drag">');
            d.css("background-image", 'url('+ src +')');
            if(i !== 0){
                d.css("display", "none");
            }else{
                galleryImg.addClass('img_slider_gallery_img_focus');
            }
            let dragMini = $('<div class="drag_mini"></div>');
            dragMini.css("background-image" , "url('"+ src +"')")
            if(fileWidth < fileHeight){
                d.width(slider.width());
                d.height(slider.width() * fileHeight / fileWidth);
                dragMini.width(94);
                dragMini.height(94 * fileHeight/fileWidth);
            }else{
                d.height(slider.height());
                d.width(slider.height() * fileWidth / fileHeight);
                dragMini.height(94);
                dragMini.width(94 * fileWidth / fileHeight);
            }
            d.on("mousedown", Drag);
            $('.modal_image_slider').append(d);

            TweenLite.to(galleryImg, 0, {
                css:{
                    transform : 'translate(' + (i*106) + 'px, 0px)'
                }
            })
            galleryImg.data('last', {dx: i*106});
            galleryImg.data('index', i);
            dragMini.on("mousedown", drag_mini);

            galleryImg.append(dragMini);
            gallery.append(galleryImg);
            files.push({"file":image_files[i], "width": fileWidth, "height": fileHeight, "url":src, "drag": galleryImg});
        }


    }
    //$('#img_slider_gallery_list').sortable();

    next();
}
function next(){
    switch (page){
        case 0:
            $("#image_pick").css("display", "none");
            $("#image_edit").css("display", "flex");
            page++;
            break
        case 1:
            break
    }

}
function back(){
    switch (page){
        case 0:
            break
        case 1:
            $("#image_edit").css("display", "none");
            $("#image_pick").css("display", "flex");
            files = [];
            page--;
            break
    }
}
function next_img(e){
    let n = $('#nth-img');
    let next = String(parseInt(n.val())+1)

    let index = order[n.val()];
    let nextIndex = order[next];
    let view = $($('.drag')[index]);
    let nextView = $($('.drag')[nextIndex]);

    view.css("display", "none");
    nextView.css("display", "flex");

    $('#img_slider_icon_prev').css('display', 'flex');

    let dots = $('.img_slider_dot');
        dots.removeAttr('style');
        $(dots[next]).css('background-color', '#0095f6');
    if(next === n.attr("max")){
        $(this).css('display', 'none');
    }

    let images = $('.img_slider_gallery_img');
        images.removeClass('img_slider_gallery_img_focus');
    for(let image of images){
        if(String($(image).data('last').dx/106) === next){
            $(image).addClass('img_slider_gallery_img_focus');
        }
    }
    n.val(next);

    /*let n = $('#nth-img');
    let i = $($('.drag')[n.val()]);
    i.css("display","none");
    i.next().css("display","flex");

    $(this).prev().prev().css("display", "flex");
    n.val(parseInt(n.val())+1);
    $('.img_slider_dot').removeAttr('style');
    $($('.img_slider_dot')[n.val()]).css("background-color", "#0095f6");
    if(n.val() === n.attr("max")){
        $(this).css("display","none");
    }*/
}
function prev_img(e){
    let n = $('#nth-img');
    let prev = String(parseInt(n.val())-1)

    let index = order[n.val()];
    let prevIndex = order[prev];

    let view = $($('.drag')[index]);
    let prevView = $($('.drag')[prevIndex]);

    view.css("display", "none");
    prevView.css("display", "flex");

    $('#img_slider_icon_next').css('display', 'flex');

    let dots = $('.img_slider_dot');
        dots.removeAttr('style');
        $(dots[prev]).css('background-color', '#0095f6');
    if(prev === "0"){
        $(this).css('display', 'none');
    }
    let images = $('.img_slider_gallery_img');
    images.removeClass('img_slider_gallery_img_focus');
    for(let image of images){
        if(String($(image).data('last').dx/106) === prev){
            $(image).addClass('img_slider_gallery_img_focus');
        }
    }

    n.val(prev);
    /*let n = $('#nth-img');
    let i = $($('.drag')[n.val()]);
    i.css("display", "none");
    i.prev().css("display", "flex");
    $(this).next().next().css("display", "flex");
    n.val(parseInt(n.val())-1);
    $('.img_slider_dot').removeAttr('style');
    $($('.img_slider_dot')[n.val()]).css("background-color", "#0095f6");
    if(n.val() === "0"){
        $(this).css("display", "none");
    }*/
}
function cut_img(e){
    let side = $('.modal_image_container').width();
    let slider = $('.modal_image_slider');
    let index = $(this).index()
    switch (index){
        case 0:
            // 1:1
            slider.animate({width: '100%', height: '100%'});
            $($(this).parent().children().children().get()).css({fill: "#8e8e8e", color: "#8e8e8e"})
            $($(this).children().get()).css({fill: "white", color: "white"})

            break;
        case 1:
            // 4:5
            let width = side / 5 * 4;
            slider.animate({width: width, height: '100%'});
            $($(this).parent().children().children().get()).css({fill: "#8e8e8e", color: "#8e8e8e"})
            $($(this).children().get()).css({fill: "white", color: "white"})
            break;
        case 2:
            // 16:9
            let height = side / 16 * 9;
            slider.animate({width: '100%', height: height});
            $($(this).parent().children().children().get()).css({fill: "#8e8e8e", color: "#8e8e8e"})
            $($(this).children().get()).css({fill: "white", color: "white"})
            break;
    }
    resize_img(index);
    return false;
}
function resize_img(n){

    let d = $('.drag');
    let side = $('.modal_image_container').width();
    for(let i = 0; i<d.length; i++){
        let t = $(d[i]);


        let height = files[i].height;
        let width = files[i].width;
        switch (n){
            case 0:
                if(width < height){
                    t.width(side);
                    t.height(side * height / width);
                }else{
                    t.height(side);
                    t.width(side * width / height);
                }
                break;
            case 1:
                if(width/height < 4/5){
                    t.width(side)
                    t.height(side * height/width)
                }else{
                    t.height(side)
                    t.width(side * width /height)
                }
                break;
            case 2:

                if(width/height < 16/9){
                    t.animate({width : side, height : side * height / width},0);
                    /*d.width(slider.width());
                    d.height(slider.width() * height / width);*/
                }else{
                    t.animate({width : side * width / height, height : side},0);

                    /*d.width(slider.height() * width / height);
                    d.height(slider.height());*/
                }
                break;
        }
    }
}
function zoom_img(e){
    let value = 1 + (parseInt($(this).val())/100);
    let index = $("#nth-img").val();
    let move = $($('.drag')[order[index]]);
    let lastOffset = move.data('last');
    let lastOffsetX = lastOffset ? lastOffset.dx : 0,
        lastOffsetY = lastOffset ? lastOffset.dy : 0,
        lastScale = lastOffset ? lastOffset.scale : 1;
    move.data('last', {dx:lastOffsetX, dy:lastOffsetY, scale: value});
    //move.css("transform", 'translate(' + lastOffsetX + 'px, '+lastOffsetY+'px) scale('+ value +','+ value+')')
    TweenLite.to(move, 0, {
        css:{
            transform : 'translate(' + lastOffsetX + 'px, '+lastOffsetY+'px) scale('+ value +','+ value+')'
        }


    })
    move.scale = value;
}
function drag_mini(e){
    // this = .drag_mini
    let mini = $(this);
    // p = .gallery_img
    let p = mini.parent();
    let dx = p.data('last').dx;
    let index = Math.ceil((dx-47) / 106);
    let imgs = $('.img_slider_gallery_img');
    let startX = e.pageX - dx;
    $('.modal_background_post').off("click");

    TweenLite.to(p, 0.3, {
        css:{
            transform : 'translate(' + dx + 'px, 0px) scale(1.2)'
        }
    })
    $(window).on('mousemove', function(e){
        p.css("z-index", 100);
        $('body').css("cursor", "pointer")
        $('.drag').css('cursor', "pointer");
        let newX = e.pageX - startX;
        p.data('last', {dx: newX})
        TweenLite.to(p, 0.1, {
            transform : 'translate(' + newX + 'px, 0px) scale(1.2)'
        })
        let newIndex = Math.ceil((newX-47) / 106);
        if(newIndex !== index){
            for(let i of imgs){
                if(p[0] !== i && Math.ceil(($(i).data('last').dx-47) / 106) === newIndex ){

                    TweenLite.to($(i), 0.1, {
                        transform : 'translate(' + (index*106) + 'px, 0px) scale(1)'
                    })
                    $(i).data('last', {dx:index*106})
                    index = newIndex;
                    break;
                }
            }
        }

    })
    $(window).on('mouseup', function(){
        p.css("z-index", "auto");
        dx = p.data('last').dx;
        let x = Math.ceil((dx-47) / 106) * 106;
        let min = 0;
        let max = $('.img_slider_gallery_slider').attr("style").substr(7).split("px")[0]-106
        if(x < min){
            x = min
        }
        else if( x > max){
            x = max
        }
        TweenLite.to(p, 0.1, {
            css:{
                transform : 'translate(' + x + 'px, 0px) scale(1)'
            }
        })
        p.data('last', {dx: x})
        $(window).off("mousemove");
        $(window).off('mouseup');

        $('body').css("cursor", "auto")
        $('.drag').css('cursor', "grab");
        for(let img of imgs){
            order[$(img).data('last').dx / 106] = $(img).data('index');
        }
        $('#nth-img').val(p.data('last').dx / 106);
        if($('#nth-img').val() === "0"){
            $("#img_slider_icon_prev").css('display','none');
            $('#img_slider_icon_next').css('display','flex');
        }else if($('#nth-img').val() === $('#nth-img').attr('max')){
            $('#img_slider_icon_next').css('display','none');
            $("#img_slider_icon_prev").css('display','flex');
        }else{
            $("#img_slider_icon_prev").css('display','flex');
            $('#img_slider_icon_next').css('display','flex');
        }

        p.click();
        return false;
    })
    $(window).on('click', function (){
        $('.modal_background_post').on("click", modal_close);
        $(window).off('click');
        return false;
    })

}
function drag_mini_click(e){

    let Focused = $('.img_slider_gallery_img_focus');
        Focused.removeClass('img_slider_gallery_img_focus');
    $(this).addClass("img_slider_gallery_img_focus");
    let index = $(this).data('index');

    let drags = $('.drag');
        drags.css('display', 'none');
        $(drags[index]).css('display', 'flex');
    let dots = $('.img_slider_dot');
        dots.removeAttr('style');
        $(dots[$('#nth-img').val()]).css('background-color', '#0095f6');
}
function add_images(image_files){
    let nInput = $('#nth-img');
    let first = nInput.val() === "-1";

    for(let i = 0; i < image_files.length; i++){
        let file = image_files[i];
        let src = URL.createObjectURL(file);
        let img = new Image();
            img.src = src
            img.onload = function(){
                // 큰 이미지
                let slider = $('.modal_image_slider');
                let drag = $('<div class="drag">');
                    drag.css({
                        "background-image" : 'url('+ src +')',
                        "display" : "none"
                    })
                    drag.on("mousedown", Drag);
                // 작은 이미지
                let gallery = $('.img_slider_gallery_list');
                    let galleryImg = $('<div class="img_slider_gallery_img"></div>');
                        galleryImg.on("click", drag_mini_click);
                    gallery.append(galleryImg);
                let dragMini = $('<div class="drag_mini"></div>');
                    dragMini.css("background-image",'url('+ src +')');
                    dragMini.on("mousedown", drag_mini);
                // 너비 높이 translate 설정
                if(img.width < img.height){
                    drag.width(slider.width());
                    drag.height(slider.width() * img.height / img.width);
                    dragMini.width(94);
                    dragMini.height(94 * img.height / img.width);
                }else{
                    drag.height(slider.height());
                    drag.width(slider.height() * img.width / img.height);
                    dragMini.height(94);
                    dragMini.width(94 * img.width / img.height);
                }
                TweenLite.to(galleryImg, 0, {
                    css:{
                        transform : 'translate(' + (galleryImg.index()*106) + 'px, 0px)'
                    }
                })
                galleryImg.data('last', {dx: galleryImg.index()*106});
                galleryImg.data('index', galleryImg.index());
                order.push(galleryImg.index());
                // 추가

                slider.append(drag);
                galleryImg.append(dragMini);
                $('.img_slider_dots').append($('<div class="img_slider_dot"></div>'));
                files.push({"file": file})
                // for 끝나고 실행

                if(i === image_files.length-1){

                    // input file 초기화
                    $($('#modal_image_input')[0]).val("")
                    // input max 값 수정
                    nInput.attr('max', files.length-1)
                    // gallery list width 수정
                    $('.img_slider_gallery_slider').width((94 * order.length + 12*(order.length-1))+12);
                    // mini next, prev 버튼 생성

                    if($('#img_slider_gallery_slider').width() !== $('#img_slider_gallery_list').width()){
                        $('#img_slider_icon_mini_next').css('display', 'flex');
                    }
                    // 처음 이미지 추가
                    if(first){
                        // files push 후 설정
                        // input max 수정

                        $('.img_slider_dot').eq(0).css("background-color", "#0095f6");
                        $('.drag').eq(0).css('display', "flex");
                        $('.img_slider_gallery_img').eq(0).addClass("img_slider_gallery_img_focus");
                        nInput.val('0');
                        if(files.length === 1){
                            $('#img_slider_icon_next').css('display','none');
                        }


                    }
                }
            }
    }
    if(first) next()
}
function mini_next(e){
    let slider = $('#img_slider_gallery_slider');
    let list = $('#img_slider_gallery_list');
    if(slider.width() === list.width()){
        return;
    }
    let n = Math.floor(slider.width()/106);
    let min = slider.width() - list.width();
    let nowLeft = list.data('left') ? list.data('left') : 0;
    let left = nowLeft - (n * 106)  < min ? min : nowLeft - (n * 106);
    if(left <= min){
        left = min
        $(this).css('display', 'none');
    }
    list.data('left', left)
    list.animate({
        left: left
    }, 500)
    $('#img_slider_icon_mini_prev').css('display', 'flex');
}
function mini_prev(e){
    let slider = $('#img_slider_gallery_slider');
    let list = $('#img_slider_gallery_list');
    if(slider.width() === list.width()){
        return;
    }
    let n = Math.floor(slider.width()/106);
    let max = 0;
    let nowLeft = list.data('left') ? list.data('left') : 0;

    let left = nowLeft + (n * 106)// > max ? max : nowLeft + (n * 106);
    if(left >= max){
        left = max;
        $(this).css('display', 'none');
    }
    list.data('left', left)
    list.animate({
        left: left
    }, 500)
    $('#img_slider_icon_mini_next').css('display', 'flex');
}
function dis_cut(){
    let target = $('#img_cut');
    console.log("foo")
    target.css('opacity',"0");
    target.css('display','flex');
    target.animate({
        bottom: 48,
        opacity: "1"
    }, 100)
    //$(this).children().eq(1).fadeIn(300);
    $(window).on('mousedown', function(e){
        let target = $('#img_cut');
        if(target.has(e.target).length === 0){

            target.animate({
                bottom: 28,
                opacity: "0"
            }, 100, function(){
                console.log("완료");
                console.log(target);
                $('.img_slider_icon').eq(2).one('click', dis_cut);
                target.removeAttr("style");
            })
            $(this).off(e);
        }
    })
}
function dis_zoom(){
    $(this).children().eq(1).css('display','flex');
    $(window).on('mousedown', function(e){
        let target = $('#zoom_div');
        if(target.has(e.target).length === 0){
            target.hide();
            $(this).off(e);
        }
    })
}
function dis_mini(){
    $(this).next().css('display','flex');
    if($('#img_slider_gallery_slider').width() !== $('#img_slider_gallery_list').width()){
        $('#img_slider_icon_mini_next').css('display', 'flex');
    }
    $(window).on('mousedown', function(e){
        let target = $('#img_slider_gallery');
        if(target.has(e.target).length === 0){
            target.hide();
            $(this).off(e);
        }
    })

}

