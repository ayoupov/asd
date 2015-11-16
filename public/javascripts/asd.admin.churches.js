var churchEditClick = function () {
    var id = getId($(this));
    $churchWrapper.empty();
    $requestForm = newRequestForm();
    $churchWrapper.append($requestForm);
    $churchWrapper.append($("<br>"));
    $churchForm = newChurchForm();
    $("#submit", $churchForm).api({
        on: 'click',
        action: 'post update church',
        method: 'POST',
        onSuccess: apiResult,
        urlData: {id: id},
        serializeForm: true,
        //beforeSend: function (settings) {
        //    settings.urlData.id = id;
        //    return settings;
        //}
    });
    $churchForm.appendTo($churchWrapper);
    $(".church-visible").show();
    if (id) {
        $churchForm.api({on: 'now', action: "get church passport", urlData: {id: id}, onSuccess: fillChurch});
        $requestForm.api({on: 'now', action: "get json requests", urlData: {id: id}, onSuccess: fillRequests});
    }
};

function newChurchForm() {
    return $("<div> Current church data: </div>" +
        "<form class='church-form ui form' method='post'>" +
        "<label for='name'></label><input id='name' name='name'/>" +
        "<label for='constructionStart'>construction start</label><input type='text' id='constructionStart' placeholder='Construction start' name='constructionStart'/>" +
        "<label for='constructionEnd'>construction end</label><input type='text' id='constructionEnd' placeholder='Construction end' name='constructionEnd'/>" +
        "<label for='architects'>architects</label><input type='text' id='architects' placeholder='Architects' name='architects'/>" +
        "<label for='website'>website</label><input type='text' id='website' placeholder='website' name='website'/>" +
        "<label for='address'>address</label><input type='text' id='address' placeholder='address' name='address'/>" +
        "<br>" +
        "<input id='submit' type='button' class='ui submit button' value='update'>" +
        "</form>");
}

function newRequestForm() {
    return $("<form class='request-form ui form' method='post'>" +
        "</form>");
}

function fillRequests(data) {
    if (data) {
        $requestForm.append("<div>Church has " + data.length + " requests</div>");
        $.each(data, function (a, item) {
            var $requestDiv = $("<div id='req-div_" + item.id + "' />");
            var $entry = $("<label for='req_" + item.id + "'>" + "On " +  item.field + " by " + item.suggestedBy.name + "</label>" +
                "<input id='req_" + item.id + "' value='" + item[item.field] + "' >");
            $requestDiv.append($entry);
            var $fixButton = $("<label for='req-fix_" + item.id + "'>Mark as fixed</label>" +
                "<input type=checkbox id='req-fix_" + item.id + "' class='req-fix'>");
            var $ignoreButton = $("<label for='req-ignore_" + item.id + "'>Ignore</label>" +
                "<input type=checkbox id='req-ignore_" + item.id + "'  class='req-ignore'><br>");
            $requestDiv.append($fixButton);
            $requestDiv.append($ignoreButton);
            $requestForm.append($requestDiv);
        });
        $(".req-fix").api({
            on: 'click',
            action: 'post fix suggestion',
            method: 'POST',
            onSuccess: removeFromRequestForm,
            beforeSend: function (settings) {
                settings.urlData.id = getId($(this));
                return settings;
            }
        });
        $(".req-ignore").api({
            on: 'click',
            action: 'post ignore suggestion',
            method: 'POST',
            onSuccess: removeFromRequestForm,
            beforeSend: function (settings) {
                settings.urlData.id = getId($(this));
                return settings;
            }
        });
    }
}

function removeFromRequestForm(data) {
    $("#req-div_" + data.id, $requestForm).remove();
}

function fillChurch(data) {
    $admpages.hide();
    if (data) {
        //fillRequests(data.requests);
        $("#name", $churchForm).val(data.name);
        $("#constructionStart", $churchForm).val(data.constructionStart);
        $("#constructionEnd", $churchForm).val(data.constructionEnd);
        var architectsArr = [];
        if (data.architects)
            $.each(data.architects, function (a, item) {
                architectsArr.push(item.name);
            });
        $("#architects", $churchForm).val(architectsArr.join(","));
        $("#website", $churchForm).val(data.website);
        $("#address", $churchForm).val((data.address) ? data.address.unfolded : "");
        fillGeo(data);
        fillImages(data.images);
        fillLinks(data.media);
    }
    $churchWrapper.show();
}

function fillGeo(data) {

}

function fillImages(images) {
    if (images)
        $.each(images, function (a, item) {

        });
}

function fillLinks(media) {
    if (media && !$(".related-media", $churchWrapper).length) {
        $churchWrapper.append($("<div class='related-media'>Related media:</div>"));
        $.each(media, function (a, item) {
            var $entry = $("<a href='/" + item.contentType.toLowerCase() + "/" + item.id + "' >" + item.title + "</a><br>");
            $entry.appendTo($churchWrapper);
        });
    }

}