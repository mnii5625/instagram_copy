let files = [];
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
            set_images(drop_files);
        }
    })
    //컴퓨터에서 선택 버튼
    $('.modal_image_input_button').click(function (){
        $('#modal_image_input').click();
    })
    //컴퓨터에서 선택
    $('#modal_image_input').change(function (){
        console.log($('#modal_image_input')[0].files);
        set_images($('#modal_image_input')[0].files);
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

}
function Drag(e){
    console.log("Drag")
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
        console.log(slider.offset().left - move.offset().left);
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
        console.log("mouseUp")
        //커서 오토로 변경
        $('body').css("cursor", "auto");
        // 커서 그랩으로 변경
        move.css("cursor", "grab");
        // 마우스 move 이벤트 해제
        $(document).off("mousemove");
        // 영역 넘어가는거 확인
        console.log("up", move.data('last'));
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
        console.log(side)
        $('.modal_image_container').width(side).height(side);

    }else{
        side = window_width * 0.7;
        console.log(side)
        $('.modal_image_container').width(side).height(side);
    }
}
function set_images(image_files){
    console.log(image_files)
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
        if(i ===0){
            dot.css("background-color", "#0095f6")
        }
        dots.append(dot);

        console.log("file", file);
        let img = new Image();
        let src = URL.createObjectURL(file);
        img.src = src
        img.onload = function (){
            let slider = $('.modal_image_slider');
            let fileWidth = img.width;
            let fileHeight = img.height;
            files.push({"file":image_files[i], "width": fileWidth, "height": fileHeight, "url":src});
            let d = $('<div class="drag">');
            d.css("background-image", 'url('+ src +')');
            if(i !== 0){
                d.css("display", "none");
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
            let gallery = $('.img_slider_gallery_list');
            let galleryImg = $('<div class="img_slider_gallery_img"></div>');
            TweenLite.to(galleryImg, 0, {
                css:{
                    transform : 'translate(' + (i*106) + 'px, 0px)'
                }
            })
            galleryImg.data('last', {dx: i*106})
            dragMini.on("mousedown", drag_mini);

            galleryImg.append(dragMini);
            gallery.append(galleryImg);

        }


    }
    //$('#img_slider_gallery_list').sortable();
    console.log(files);
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
    let i = $($('.drag')[n.val()]);
    i.css("display","none");
    i.next().css("display","flex");

    $(this).prev().prev().css("display", "flex");
    n.val(parseInt(n.val())+1);
    $('.img_slider_dot').removeAttr('style');
    $($('.img_slider_dot')[n.val()]).css("background-color", "#0095f6");
    if(n.val() === n.attr("max")){
        $(this).css("display","none");
    }
}
function prev_img(e){
    let n = $('#nth-img');
    let i = $($('.drag')[n.val()]);
    i.css("display", "none");
    i.prev().css("display", "flex");
    $(this).next().next().css("display", "flex");
    n.val(parseInt(n.val())-1);
    $('.img_slider_dot').removeAttr('style');
    $($('.img_slider_dot')[n.val()]).css("background-color", "#0095f6");
    if(n.val() === "0"){
        $(this).css("display", "none");
    }
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
}
function resize_img(n){

    let d = $('.drag');
    let side = $('.modal_image_container').width();
    for(let i = 0; i<d.length; i++){
        let t = $(d[i]);
        console.log(t);
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
                console.log("here")
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
    let move = $($('.drag')[index]);
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
    let mini = $(this);
    console.log("this", this);
    let p = mini.parent();
    let dx = p.data('last').dx;
    let index = Math.ceil((dx-47) / 106);
    let dragMini = $('.img_slider_gallery_img');
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
            for(let d of dragMini){
                if(this.parentElement !== d && Math.ceil(($(d).data('last').dx-47) / 106) === newIndex ){
                    console.log(d);
                    TweenLite.to($(d), 0.1, {
                        transform : 'translate(' + (index*106) + 'px, 0px) scale(1)'
                    })
                    $(d).data('last', {dx:index*106})
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
        console.log(dx);
        console.log(x);
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
        $('.modal_background_post').on("click", modal_close);
        return false;
    })

}