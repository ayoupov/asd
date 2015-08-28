function fillInRevs(id) {
    $revisions.api(
        {
            on: 'now',
            onSuccess: renderRevisions,
            urlData: {id: id},
            action: 'get church revisions'
        }
    );
}
var churchRevisionClick = function () {
    var id = getId($(this));
    fillInRevs(id);
    $revisions.show();
};

var renderRevisions = function (data) {
    $revisions.empty();
    // iterate thru versions till last approved
    $(data).each(function (a, item) {
        $revisions.append($("<div>").html(item));
    });
};

function fillRevisions(data) {
    $churchEditor.html(data.text);
}
