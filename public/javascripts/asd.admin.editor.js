var toolData = {
    header: {
        tooltip: 'header, header2, header3',
        insert: '[header]![/header]'
    },
    padder: {
        tooltip: 'full width line',
        insert: '[padder/]'
    },
    bold: {
        tooltip: 'make characters bold !and switch to different style in afterwords',
        insert: '[b]![/b]'
    },
    italic: {
        tooltip: 'make characters italic !and switch to different style in quote',
        insert: '[i]![/i]'
    },
    p: {
        tooltip: 'add new paragraph',
        insert: '[p]![/p]'
    },
    br: {
        tooltip: 'new line',
        insert: '[br/]'
    },
    gap: {
        tooltip: 'gap in px',
        insert: '[gap=?]'
    },
    numlist: {
        tooltip: 'numbered list',
        insert: '[list=1]![/list]'
    },
    alphalist: {
        tooltip: 'letter list',
        insert: '[list=a]![/list]'
    },
    unorderedlist: {
        tooltip: 'bullet list',
        insert: '[list]![/list]'
    },
    listitem: {
        tooltip: 'list item',
        insert: '[item]![/item]'
    },
    link: {
        tooltip: 'external link',
        insert: '[link=?]![/link]'
    },
    excerpt: {
        tooltip: 'add an excerpt',
        insert: '[excerpt]![/excerpt]'
    },
    quote: {
        tooltip: 'add quote. to apply second style use italic',
        insert: '[quote]![/quote]'
    },
    afterwords: {
        tooltip: 'add afterwords. to apply second style use bold',
        insert: '[afterwords]![/afterwords]'
    },
    image: {
        tooltip: 'insert image (image=fullwidth ignores from and to)',
        insert: '[image src={?} from=? to=?]!Caption[/image]'
    },
    coub: {
        tooltip: 'insert coub (coub=fullwidth ignores from and to), src is the coub id',
        insert: '[coub src={?} from=? to=? height=? autostart=? originalsize=? muted=? allowfullscreen=? hd=?]!Caption[/coub]'
    },
    gsv: {
        tooltip: 'insert google street view (gsv=fullwidth ignores from and to), src is coordinates',
        insert: '[gsv src={?} from=? to=? height=? heading=?]!Caption[/gsv]'
    },
    biblink: {
        tooltip : 'insert superscript link to bibliography',
        insert: '[biblink]![/biblink]'
    },
    biblink_rev: {
        tooltip : 'insert superscript link to return back',
        insert: '[biblink=reverse]![/biblink]'
    }
};

var $currentEditor = null;

function newToolbar(editor) {
    $currentEditor = $(editor);
    return "<div class='editor-toolbar'>" +
        "<span class='editor-tool' data-ref='header'>header</span>" +
        "<span class='editor-tool' data-ref='padder'>padder</span>" +
        "<span class='editor-tool' data-ref='bold'><b>bold</b></span>" +
        "<span class='editor-tool' data-ref='italic'><i>italic</i></span>" +
        "<span class='editor-tool' data-ref='p'>paragraph</span>" +
        "<span class='editor-tool' data-ref='br'>newline</span>" +
        "<span class='editor-tool' data-ref='gap'>gap</span>" +
        "<span class='editor-tool' data-ref='numlist'>numlist</span>" +
        "<span class='editor-tool' data-ref='alphalist'>alphalist</span>" +
        "<span class='editor-tool' data-ref='unorderedlist'>unorderedlist</span>" +
        "<span class='editor-tool' data-ref='listitem'>listitem</span>" +
        "<br/>" +
        "<span class='editor-tool' data-ref='excerpt'>excerpt</span>" +
        "<span class='editor-tool' data-ref='quote'>quote</span>" +
        "<span class='editor-tool' data-ref='afterwords'>afterwords</span>" +
        "<span class='editor-tool' data-ref='link'>link</span>" +
        "<span class='editor-tool' data-ref='image'>image</span>" +
        "<span class='editor-tool' data-ref='coub'>coub</span>" +
        "<span class='editor-tool' data-ref='gsv'>gsv</span>" +
        "<span class='editor-tool' data-ref='biblink'>biblink</span>" +
        "<span class='editor-tool' data-ref='biblink_rev'>biblink_rev</span>" +
        "</div>";
}

function initToolbar() {
    $(".editor-tool", $(".editor-toolbar")).each(function (a, item) {
        var ref = $(item).data('ref');
        if (toolData[ref]) {
            $(item).popup(
                {
                    content : toolData[ref].tooltip,
                    position: 'right center',
                    target: $currentEditor
                }
            );
            var insertText = toolData[ref].insert;
            var insertPos = insertText.indexOf("!");
            if (insertPos == -1)
                insertPos = insertText.length;
            insertText.replace("!", "");
            $(item).on('click', function () {
                $currentEditor.focus();
                var curpos = getCursorPos($currentEditor[0]);
                if (curpos != -1)
                    curpos = curpos.start;
                var ins = insertText.replace("!", "");
                //console.log(curpos + " : " + ins + " : " + insertPos);
                insertAtCaret($currentEditor.attr('id'), ins);
                setCaretToPos($currentEditor[0], curpos + insertPos);
            });
        }
    });
}


function insertAtCaret(areaId, text) {
    var txtarea = document.getElementById(areaId);
    var scrollPos = txtarea.scrollTop;
    var strPos = 0;
    var br = ((txtarea.selectionStart || txtarea.selectionStart == '0') ?
        "ff" : (document.selection ? "ie" : false ) );
    if (br == "ie") {
        txtarea.focus();
        var range = document.selection.createRange();
        range.moveStart('character', -txtarea.value.length);
        strPos = range.text.length;
    }
    else if (br == "ff") strPos = txtarea.selectionStart;

    var front = (txtarea.value).substring(0, strPos);
    var back = (txtarea.value).substring(strPos, txtarea.value.length);
    txtarea.value = front + text + back;
    strPos = strPos + text.length;
    if (br == "ie") {
        txtarea.focus();
        var range = document.selection.createRange();
        range.moveStart('character', -txtarea.value.length);
        range.moveStart('character', strPos);
        range.moveEnd('character', 0);
        range.select();
    }
    else if (br == "ff") {
        txtarea.selectionStart = strPos;
        txtarea.selectionEnd = strPos;
        txtarea.focus();
    }
    txtarea.scrollTop = scrollPos;
}

function getCursorPos(input) {
    if ("selectionStart" in input && document.activeElement == input) {
        return {
            start: input.selectionStart,
            end: input.selectionEnd
        };
    }
    else if (input.createTextRange) {
        var sel = document.selection.createRange();
        if (sel.parentElement() === input) {
            var rng = input.createTextRange();
            rng.moveToBookmark(sel.getBookmark());
            for (var len = 0;
                 rng.compareEndPoints("EndToStart", rng) > 0;
                 rng.moveEnd("character", -1)) {
                len++;
            }
            rng.setEndPoint("StartToStart", input.createTextRange());
            for (var pos = {start: 0, end: len};
                 rng.compareEndPoints("EndToStart", rng) > 0;
                 rng.moveEnd("character", -1)) {
                pos.start++;
                pos.end++;
            }
            return pos;
        }
    }
    return -1;
}

function setSelectionRange(input, selectionStart, selectionEnd) {
    if (input.setSelectionRange) {
        input.focus();
        input.setSelectionRange(selectionStart, selectionEnd);
    }
    else if (input.createTextRange) {
        var range = input.createTextRange();
        range.collapse(true);
        range.moveEnd('character', selectionEnd);
        range.moveStart('character', selectionStart);
        range.select();
    }
}

function setCaretToPos(input, pos) {
    setSelectionRange(input, pos, pos);
}