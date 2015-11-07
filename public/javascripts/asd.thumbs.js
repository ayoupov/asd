function createMediaThumb(selectorClass, id, cover, title, desc, hover, lead, alt) {
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
    if (alt)
        $thumb.data('alt', alt);
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
        var type = item.contentType.toLowerCase();
        var alt = item.alt;
        var $item = createMediaThumb(selectorClass + " " + type, id, cover, title, desc, hover, lead, alt);
        //var $item = createMediaThumb(selectorClass, id, cover, title, desc, hover, lead);
        $container.append($item);
        $container.isotope('appended', $item);
        lastItem = $item;
    });
    return lastItem;
}

function bindThumbEvents($container, selectorClass)
{
    // todo: ohwell, hack. invent something more clever
    if (selectorClass == 'related')
        selectorClass = $(this).hasClass('story') ? 'story' : 'article';
    $('.' + selectorClass + ':not(.extra-' + selectorClass +')', $container).off('click').on('click', function () {
        window.location.href = '/' + selectorClass + '/' + ($(this).data('alt') ? $(this).data('alt') : getId($(this)));
    });
}