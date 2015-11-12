function fileInputTweak() {

    $(document).on('change', '.btn-file :file', function () {
        var input = $(this);

        if (navigator.appVersion.indexOf("MSIE") != -1) { // IE
            var label = input.val();

            input.trigger('fileselect', [1, label, 0]);
        } else {
            var label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
            var numFiles = input.get(0).files ? input.get(0).files.length : 1;
            var size = input.get(0).files[0].size;

            input.trigger('fileselect', [numFiles, label, size]);
        }
    });

    //$(document).on('fileselect', '.btn-file :file', fileselect);
    $('.btn-file :file').on('fileselect', fileselect);
}

var fileselect = function (event, numFiles, label, size) {
    var fileExtensionRange = '.png .jpg .jpeg .gif';
    var MAX_SIZE = 4; // MB

    var $attachmentName = $(event.target);
    $attachmentName.attr('name', 'image_' + getId($attachmentName)); // allow upload.

    var postfix = label.substr(label.lastIndexOf('.'));
    if (fileExtensionRange.indexOf(postfix.toLowerCase()) > -1) {
        if (size > 1024 * 1024 * MAX_SIZE) {
            notifyError('Za duży plik! Zdjęcie nie powinno być większe, niż <strong>' + MAX_SIZE + '</strong> MB.');

            $attachmentName.removeAttr('name'); // cancel upload file.
        } else {
            $('#_' + $attachmentName.attr('id')).val(label).attr('disabled', 'disabled');
            var $thisForm = $attachmentName.closest('.ui.form');
            addNewImageBlock($thisForm);
            $(".not-filled", $thisForm).removeClass('not-filled');
        }
    } else {
        notifyError('Prześlij plik w formacie <strong>png, jpg, jpeg</strong> lub <strong>gif</strong>');

        $attachmentName.removeAttr('name'); // cancel upload file.
    }
};

function resetFileInputs($elem) {
    $(".image-block-generated", $elem).remove();
    updateLabels($elem);
}

function updateLabels($elem) {
    var $descLabels = $('label.image-label-desc', $elem), $fnameLabels = $("label.image-label-filename", $elem);
    $descLabels.html('');
    $fnameLabels.html('');
    $fnameLabels.last().html('Dodaj zdjęcie');
    $descLabels.last().html('Podpis pod zdjęciem');
    if ($elem.data('entity') == 'images') {
        if ($(".image-block", $elem).length > 1)
        {
            $(".entity-submit").val("DODAJ SWOJE ZDJĘCIA");
        } else
        {
            $(".entity-submit").val("DODAJ SWOJE ZDJĘCIE");
        }
    }
}

function addNewImageBlock($elem) {
    var $insertElem = $elem.find(".insert-image-after").eq(0);
    if (!$insertElem.length)
        $insertElem = $elem.find(".image-block:last");
    var $newImageBlock = getNextImageBlockElem($elem.data('image-part'));
    $('.btn-file :file', $newImageBlock).on('fileselect', fileselect);
    $newImageBlock.hide().insertAfter($insertElem);
    $newImageBlock.slideDown('slow');
    $insertElem.removeClass("insert-image-after");
    updateLabels($elem);
}

var currentImageCounts = {};

function getNextImageBlockElem(part) {
    if (!currentImageCounts[part]) {
        currentImageCounts[part] = 1;
    }

    var currentImageCount = currentImageCounts[part];
    currentImageCount++;
    currentImageCounts[part] = currentImageCount;

    function createId() {
        return part + '-image_' + currentImageCount;
    }

    function createDescId() {
        return part + '-description_' + currentImageCount;
    }

    function createDescName() {
        return 'description_' + currentImageCount;
    }

    function gnibeLabel() {
        return '<label class="image-label image-label-filename" for="_' + createId() + '">Dodaj kolejne zdjęcie</label>';
    }

    function gnibeTextInput() {
        return '<input type="text" id="_' + createId() + '" class="image-name-input" placeholder="Podziel się archiwalnymi zdjęciami i grafikami">';
    }

    function gnibeFileLabelPart() {
        return '<label for="' + createId() + '" class="ui icon button btn-file">';
    }

    function gnibeFileInput() {
        return '<input type="file" id="' + createId() + '" style="display: none;">'
    }

    return $('<div class="image-block image-block-generated  insert-image-after"><div class="gapper-less clearfix"></div><div class="col-3 rightaligned inlinemiddle">' + gnibeLabel() +
        '</div><div class="col-4"><div class="ui action input">' + gnibeTextInput() +
        gnibeFileLabelPart() + '<i class="attach big icon"></i>' + gnibeFileInput() + '</label></div></div>' +
        '<div class="col-1"></div><div class="gapper-less clearfix"></div><div class="col-3 rightaligned inlinemiddle">' +
        '<label class="image-label image-label-desc" for="' + createDescId() + '">Podpis pod zdjęciem</label></div>' +
        '<div class="col-4"><input id="' + createDescId() + '" type="text" value="" name="' + createDescName() + '" placeholder="Co jest na zdjęciu? Kto jest autorem? Kiedy zostało zrobione?"></div><div class="col-1"></div></div>');
}
