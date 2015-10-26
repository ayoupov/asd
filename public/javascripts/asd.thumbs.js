function createMediaThumb(selectorClass, id, cover, title, desc, hover, lead) {
    var $thumb = $('<div/>')
        .addClass(selectorClass + ' thumb')
        .attr('id', selectorClass + '_' + id)
        .append(
        $('<div/>').addClass('image face-content').append(
            $('<div/>').addClass('face-image').css('background-image', 'url(' + cover + ')')
        )
    ).append(
        $('<div/>').addClass('content face-content').append(
            $('<div/>').addClass('header').html(title)
        ).append(
            $('<div/>').addClass('description').html(desc ? desc :  (lead) ? lead : '')
        )
    );
    if (hover)
        $thumb.append(
            $('<div/>').addClass('hover-content').append(
                $('<div/>').addClass('cover')
            ).append(
                $('<div/>').addClass('back-image').css('background-image', 'url(' + hover + ')')
            ).append(
                $('<div/>').addClass('hover-icon').append(
                    $("<img/>").attr('src', '/assets/images/hover-icon.png')
                )
            ).append(
                $('<div/>').addClass('more-button').html('Czytaj')
            )
        );
    $thumb.append(
        $('<div/>').addClass('thumb-stroke face-content')
    );
    if (hover)
        $thumb.append(
            $('<div/>').addClass('thumb-stroke hover-content')
        );
    return $thumb;
}

/*
story:     $(data.data).each(function (a, item) {
 var cover = (item.coverThumbPath) ? item.coverThumbPath : "";
 var title = item.title;
 var lead = item.lead;
 var id = item.id;
 var desc = (item.coverDescription) ? item.coverDescription : "";
 var $item = $("<div/>").addClass('story thumb').attr('id', 'story_' + id)
 .append(
 $("<div/>").addClass('image').css({
 background: 'transparent url(' + cover + ') center center no-repeat'
 }))
 .append(
 $("<div/>").addClass('content').append(
 $("<div/>").addClass('header').html(title)
 ).append(
 $("<div/>").addClass('description').html(desc)
 )
 ).append(
 $("<div/>").addClass('golden shameful-underline')
 );

 $stories.append($item);
 $stories.isotope('appended', $item);
 lastItem = $item;
 });

article:
 //$(data.data).each(function (a, item) {
 //    var cover = (item.coverThumbPath) ? item.coverThumbPath : "";
 //    var hover = (item.hoverThumbPath) ? item.hoverThumbPath : "";
 //    var title = item.title;
 //    var desc = item.coverDescription;
 //    var id = item.id;
 //    var $item = $('<div/>').addClass('article thumb').attr('id', 'article_' + id
 //    )
 //        .append(
 //        $('<div/>').addClass('image face-content').append(
 //            $('<div/>').addClass('face-image').css('background-image', 'url(' + cover + ')')
 //        )
 //    ).append(
 //        $('<div/>').addClass('content face-content').append(
 //            $('<div/>').addClass('header').html(title)
 //        ).append(
 //            $('<div/>').addClass('description').html(desc)
 //        )
 //    ).append(
 //        $('<div/>').addClass('hover-content').append(
 //            $('<div/>').addClass('cover')
 //        ).append(
 //            $('<div/>').addClass('back-image').css('background-image', 'url(' + hover + ')')
 //        ).append(
 //            $('<div/>').addClass('hover-icon').append(
 //                $("<img/>").attr('src', '/assets/images/hover-icon.png')
 //            )
 //        ).append(
 //            $('<div/>').addClass('more-button').html('Czytaj')
 //        )
 //    ).append(
 //        $('<div/>').addClass('shameful-underline violet face-content')
 //    ).append(
 //        $('<div/>').addClass('shameful-underline white hover-content')
 //    );
 //    $articles.append($item);
 //    $articles.isotope('appended', $item);
 //    lastItem = $item;
 //});
 */

function inhabitThumbs($container, selectorClass, data) {
    var lastItem = null;
    //$container.empty();
    $container.isotope(
        {
            itemSelector: '.' + selectorClass,
            layoutMode: 'fitRows'
        });

    $(data).each(function (a, item) {
        var cover = (item.coverThumbPath) ? item.coverThumbPath : "";
        var hover = (item.hoverThumbPath) ? item.hoverThumbPath : "";
        var title = item.title;
        var lead = item.lead;
        var desc = item.coverDescription;
        var id = item.id;
        var $item = createMediaThumb(selectorClass, id, cover, title, desc, hover, lead);
        $container.append($item);
        $container.isotope('appended', $item);
        lastItem = $item;
    });
    return lastItem;
}

function bindThumbEvents($container, selectorClass)
{
    $('.' + selectorClass + ':not(.extra-' + selectorClass +')', $container).off('click').on('click', function () {
        window.location.href = '/' + selectorClass + '/' + getId($(this));
    });
}