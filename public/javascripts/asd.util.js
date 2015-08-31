function getId(elem) {
    var rawId = elem.attr('id');
    if (typeof rawId == 'undefined')
        rawId = elem.parent().attr('id');
    return rawId.substr(rawId.indexOf("_") + 1);
}

$.fn.serializeObject = function()
{
    var o = {};
    var a = this.serializeArray();
    $.each(a, function() {
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

function datenow()
{
    var d = new Date();
    return d.getFullYear() +
        '-' + prepad(d.getUTCMonth() + 1) + (d.getUTCMonth() + 1) +
        '-' + prepad(d.getUTCDate()) + d.getUTCDate() + ' ' +
        prepad(d.getHours()) + d.getHours() + ':' +
        prepad(d.getMinutes()) + d.getMinutes() + ':' +
        prepad(d.getSeconds()) + d.getSeconds();
}

function prepad(val)
{
    return (val <10 ? '0' : '');
}