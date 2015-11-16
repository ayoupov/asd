function getId(elem) {
    var rawId = elem.attr('id');
    if (typeof rawId == 'undefined')
        rawId = elem.parent().attr('id');
    return rawId.substr(rawId.indexOf("_") + 1);
}

$.fn.serializeObject = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name] !== undefined) {
            if (!o[this.name].push) {
                o[this.name] = [o[this.name]];
            }
            o[this.name].push(this.value || '');
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

function ddmmyy(d) {
    return prepad(d.getUTCDate()) + d.getUTCDate() + '/' +
        prepad(d.getUTCMonth() + 1) + (d.getUTCMonth() + 1) + '/' +
        d.getUTCFullYear() % 100;
}

function toEditorDate(d) {
    return d.getFullYear() +
        '-' + prepad(d.getUTCMonth() + 1) + (d.getUTCMonth() + 1) +
        '-' + prepad(d.getUTCDate()) + d.getUTCDate() + ' ' +
        prepad(d.getHours()) + d.getHours() + ':' +
        prepad(d.getMinutes()) + d.getMinutes() + ':' +
        prepad(d.getSeconds()) + d.getSeconds();
}

function datenow() {
    var d = new Date();
    return toEditorDate(d);
}

function prepad(val) {
    return (val < 10 ? '0' : '');
}

function removeHash() {
    history.replaceState("", document.title, window.location.pathname
        + window.location.search);
}

function getHeading(f1, l1, f2, l2) {
    var y = Math.sin(l2 - l1) * Math.cos(f2);
    var x = Math.cos(f1) * Math.sin(f2) -
        Math.sin(f1) * Math.cos(f2) * Math.cos(l2 - l1);
    return toDeg(Math.atan2(y, x));
}

function toRad(degree) {
    return (Math.PI * degree) / 180;
}

function toDeg(rad) {
    return rad * 180 / Math.PI;
}

function _debug(obj) {
    console.log(obj);
}

function _scrollTo($src, $target, speed, callback) {
    if ($target.length && $src.length) {
        $src.animate({
            scrollTop: $target.offset().top
        }, speed ? speed : 600, callback);
    }
}

function getNext(key, q) {
    var res = [];
    var pushed = 0;
    for (var i = 0; i < q; i++) {
        if (contentLeft(key) > 0) {
            var id = contentCache[key].ids[contentCache[key].idx + i];
            if (typeof id !== "undefined") {
                res.push(id[0]);
                pushed++;
            }
        }
        else
            break;
    }
    contentCache[key].idx += Math.min(q, pushed);
    contentCache[key].left = q - q % pushed;
    return res.join(",");
}

function getPrev(key, q) {
    var res = [];
    var pushed = 0;
    var thisCache = contentCache[key];
    var contentLength = thisCache.ids.length;
    var idx = thisCache.idx;
    var left = thisCache.left;
    //if (idx - q - dateStoriesFirst + 1 >= 0) {
    //if (idx - q  + 1 >= 0) {
        for (var i = q + left; i > left; i--) {
            //var id = thisCache.ids[idx - dateStoriesFirst - i];
            var id = thisCache.ids[idx - i];
            if (typeof id !== "undefined") {
                res.push(id[0]);
                pushed++;
            }
        }
        thisCache.idx -= Math.min(q, pushed);
        contentCache[key].left = q % pushed;
    //}
    return res.join(",");
}

function contentLeft(key) {
    var left = contentCache[key].ids.length - contentCache[key].idx;
    return left < 0 ? 0 : left;
}

function starsort(arr) {
    var starred = [];
    var notStarred = [];
    for (var item in arr) {
        if (arr[item][1])
            starred.push(arr[item]);
        else
            notStarred.push(arr[item]);
    }
    return shuffle(starred).concat(shuffle(notStarred));
}

function stringStartsWith (string, prefix) {
    return string.slice(0, prefix.length) == prefix;
}

function preload(arrayOfImages) {
    $(arrayOfImages).each(function(){
        $('<img/>')[0].src = this;
    });
}

// todo: apply list
preload([
    '/assets/images/button-close.png',
    '/assets/images/close_button.png',
    '/assets/images/logo.png',
    '/assets/images/notification_church_icon.png',
    '/assets/images/check.png',
    '/assets/images/uncheck.png',
    '/assets/images/uncheck_required.png',
    '/assets/images/fb_login_button.png',
    '/assets/images/arrow_selector_menu_golden.png',
    '/assets/images/story_control_down.png',
    '/assets/images/story_control_up.png',
    '/assets/images/article_cover_overlay.png',
    '/assets/images/gallery_thumb_down.png',
    '/assets/images/gallery_thumb_up.png',
    '/assets/images/photo_icon.png',
    '/assets/images/photo_icon_hv.png'
]);


// The de-facto unbiased shuffle algorithm is the Fisher-Yates (aka Knuth) Shuffle.
// from https://github.com/coolaj86/knuth-shuffle

function shuffle(array) {
    var currentIndex = array.length, temporaryValue, randomIndex;
    // While there remain elements to shuffle...
    while (0 !== currentIndex) {
        // Pick a remaining element...
        randomIndex = Math.floor(Math.random() * currentIndex);
        currentIndex -= 1;
        // And swap it with the current element.
        temporaryValue = array[currentIndex];
        array[currentIndex] = array[randomIndex];
        array[randomIndex] = temporaryValue;
    }
    return array;
}

function escapeName(s)
{
    return s.replace(/:/g, '\\:');
}

function updateOGTags(tagObject)
{
    $.each(tagObject, function(key, value){
        var escaped = escapeName(key);
        var $property = $("meta[property=" + escaped + "]");
        if (!$property.length)
            $property.appendTo($("head"));
        $property.attr("content", value);
    });
}

function removeOGTags(arr)
{
    $.each(arr, function(key, value){
        var escaped = escapeName(value);
        $("meta[property=" + escaped + "]").remove();
    });
}

function updateScrapeStatus(url, callback)
{
    $.post('https://graph.facebook.com', {
        id: url,
        scrape: true
    }, function(response) {
        // todo: remove
        console.log(response);
        if (callback)
          callback();
    });
}