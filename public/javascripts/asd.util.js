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

function ddmmyy(d)
{
    return prepad(d.getUTCDate()) + d.getUTCDate() + '/' +
        prepad(d.getUTCMonth() + 1) + (d.getUTCMonth() + 1) + '/' +
        d.getUTCFullYear() % 100;
}

function toEditorDate(d)
{
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

function _scrollTo($src, $target, speed) {
    if ($target.length && $src.length) {
        $src.animate({
            scrollTop: $target.offset().top
        }, speed ? speed : 600);
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
    _debug(res.join(','));
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
