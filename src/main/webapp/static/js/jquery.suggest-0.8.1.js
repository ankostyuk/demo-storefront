/**
 * nullpointer jquery.suggest 0.8.1
 *
 * jQuery 1.4.2
 *
 * Copyright 2010, nullpointer
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * http://jquery.org/license
 * 
 * required:
 * jquery-1.4.2.js
 * query.ajaxmanager-3.05.js
 * 
 * @author ankostyuk
 * 
 */
/**
 * Tested
 * 
 * Windows XP SP2/SP3 RUS
 *  + @Deprecated IE 6 rus ("прожигание" другими контролами)
 *  + IE 7 rus
 *  + IE 8 rus
 *  + Chrome 4.0 rus
 *  + Safari 4.0 rus
 *  + FF 3.5 rus
 *  + FF 3.6 rus
 *  + Opera 10.10 rus
 *  
 * ? Windows Vista
 * ? Windows 7
 *  
 * Mac OS X 10.6.2 RUS
 *  + Chrome 5.0 beta rus
 *  + Safari 4.0 rus
 *  + FF 3.5 rus
 *  + Opera 10.10 rus
 *  - @Deprecated Opera 9.27 eng (позиционирование списка - jQuery.offset)
 * 
 * Ubuntu 9.10 RUS
 *  + Chrome 5.0 beta rus
 *  + FF 3.5 rus
 *  + Opera 10.10 rus
 *  + Konqueror 4.3.2 rus
 * 
 * iPhone OS 2.0/3.1.3 RUS
 *  + Safari rus
 * 
 */

// TODO сверстать подсказчик так, чтобы избавиться от позиционирования посредством $(window).resize()
// TODO input text cursor, selection
// TODO AJAX callback for user
// TODO @Deprecated IE fix (iframe) - IE6 only?
// TODO cache test
// TODO Что делать, если сервер долго не отвечает? Вводить параметр времени ожидания - по истечении не воспринимать ответ (убить запрос)?
// TODO optimize search highlight
// TODO последние запросы на поиск?
// TODO документировать, описать алгоритм
// TODO очистка памяти (timers, ajax, ...) при динамическом удалении $owner
// TODO URL Encoding [$.URLEncode, encodeURIComponent, encodeURI, или это серверная проблема, потому что jQuery вроде encod-ит?]

/*
 * Normal search strings pattern:
 * [word][space][word]..[word][space][word] // TODO RegExp
 * 
 */
// TODO minChars bug
// TODO unused var
;(function($) {
    //
    $.suggest = function(owner, options) {

        var KEY = {
            UP: 38,
            DOWN: 40,
            RETURN: 13,
            ESC: 27
        };

        var opts = $.extend(true, {}, $.fn.suggest.defaults, options || {});
        opts.request.queryParamName = opts.request.queryParamName || $.fn.suggest.defaults.request.queryParamName;

        var domElement = owner;
        var listContainer = null;
        var listElement = null;
        var itemsElements = [];
        var preCreateItemCount = 5;

        var localRequest = $.isFunction(opts.localRequest) ? opts.localRequest : null;

        var suggestData = opts.data;
        var dataIsObject = opts.data.itemsField ? true : false;
        suggestData.itemsField = dataIsObject ? suggestData.itemsField : "items";
        suggestData[suggestData.itemsField] = suggestData[suggestData.itemsField];

        var $owner = $(owner).attr("autocomplete", "off").addClass(opts.editClass);

        var suggestText = $owner.val();
        var actualText = $owner.val();
        var isControl = false;
        var timerControlId = null;
        var ajaxSearchName = "ajax-search";
        var ajaxSearch = opts.request.url ? $.manageAjax.create(ajaxSearchName, {
            queue: true,
            maxRequests: 1,
            abortOld: true,
            cacheResponse: false,
            preventDoubbleRequests: true
        }) : null;
        opts.request.params = $.extend(true, {}, opts.request.params || {});
        opts.request.params[opts.request.queryParamName] = "";
        var quietCount = opts.sensitivity.quiet + 1; // 0;

        var isOwnerFocus = false;
        var isListFocus = false;
        var isListShow = false;
        var isSuggestComplete = false;
        var listItemsCount = 0;
        var selectedPos = 0;

        var paging = null;
        var isPageRequest = false;
        var requestedPage = 0;
        if (suggestData.paging) {
            paging = {};
            paging[suggestData.paging.pageCountField] = 0;
            paging[suggestData.paging.pageNumberField] = 0;
            paging[suggestData.paging.pageSizeField] = 0;
            paging[suggestData.paging.totalField] = 0;
            requestedPage = 1;
            clearPaging();
        }
        var pageItemSelected = 0;
        var toolPagingPrev = null;
        var toolPagingNext = null;

        var isSelected = true;

        //
        init();
        //

        /*
         * 
         */
        function init() {
            $owner.keydown(function(event) {
                if (event.keyCode === KEY.UP) {
                    powerStopEvent(event);
                    if (isListShow) {
                        selectUp();
                    }
                } else if (event.keyCode === KEY.DOWN) {
                    powerStopEvent(event);
                    if (testNoMinChars(suggestText)) {
                        hideList();
                    } else {
                        if (isListShow) {
                            selectDown();
                        } else {
                            if (localRequest && isSelected) {
                                doLocalRequest();
                            } else
                            if (ajaxSearch && isSelected) {
                                clearPaging();
                                doRequest();
                            } else {
                                doList();
                            }
                        }
                    }
                }else if (event.keyCode === KEY.RETURN) {
                    powerStopEvent(event);
                    var userSelect = false;
                    if (isListShow) {
                        if (!opts.selectSelected) {
                            doSelected(null);
                        }
                        hideList();
                        userSelect = doUserSelect(null);
                    }
                    if ($.isFunction(opts.onInputEnter)) {
                        opts.onInputEnter.call($owner, userSelect);
                    }
                } else if (event.keyCode === KEY.ESC) {
                    powerStopEvent(event);
                    hideList();
                }
            }).keyup(function(event) {
                if (event.keyCode === KEY.UP || event.keyCode === KEY.DOWN || event.keyCode === KEY.RETURN || event.keyCode === KEY.ESC) {
                    powerStopEvent(event);
                }
            }).keypress(function(event) {
                if (event.keyCode === KEY.UP || event.keyCode === KEY.DOWN || event.keyCode === KEY.RETURN || event.keyCode === KEY.ESC) {
                    powerStopEvent(event);
                }
            }).focus(function() {
                isOwnerFocus = true;
                stopControl();
                startControl();
            }).blur(function() {
                isOwnerFocus = false;
                stopControl();
                if (!isListFocus) {
                    hideList();
                }
                if (!isActualText()) {
                    if ($.isFunction(opts.onBlurWithChanged)) {
                        opts.onBlurWithChanged.call($owner);
                    }
                }
            });

            createListElements();

            // @Deprecated
            $(window).resize(function() {
                if (isListShow) {
                    var pos = $owner.offset();
                    setListPosition();
                }
            });
        }

        function powerStopEvent(event) {
            event.stopPropagation();
            event.stopImmediatePropagation();
            event.preventDefault();
        }

        function testNoMinChars(str) {
            return str.length < opts.minChars;
        }

        function trim(str) {
            return str.replace(/^\s+|\s+$/g, "");
        }
        function normalize(str) {
            return str.replace(/^\s+|\s+$/g, "").replace(/\s+/g, " ");
        }

        /*
         * 
         */
        function createListElements() {
            listContainer = $("<div/>").hide().addClass(opts.listClass).css( {
                "position": "absolute",
                "z-index": "99999"
            }).click(function(event) {
                powerStopEvent(event);
                $owner.focus();
            }).mousedown(function(event) {
                powerStopEvent(event);
                $owner.focus();
            }).mouseup(function(event) {
                powerStopEvent(event);
                $owner.focus();
            }).mouseover(function() {
                isListFocus = true;
            }).mouseleave(function() {
                isListFocus = false;
                if (!isOwnerFocus) {
                    hideList();
                }
            });
            listElement = $("<ul/>").appendTo(listContainer);
            createListItemsElements(preCreateItemCount);
            if (opts.tools) {
                var toolsElement = $("<div/>").addClass(opts.tools.toolsClass).appendTo(listContainer);

                var toolTable = $("<table/>").attr( {
                    cellpadding: "0",
                    cellspacing: "0"
                }).appendTo(toolsElement);
                var toolTableRow = $("<tr/>").appendTo(toolTable);
                if (paging && opts.tools.paging) {
                    var toolTableColPaging = $("<td/>").addClass(opts.tools.paging.clazz).appendTo(toolTableRow);
                    toolPagingPrev = $("<span/>").click(function() {
                        if (toolPagingPrev.hasClass(opts.tools.enabledClass)) {
                            pagePrev(true);
                        }
                    }).addClass(opts.tools.actionClass).addClass(opts.tools.disabledClass).html(opts.tools.paging.htmlPrev).appendTo(toolTableColPaging);
                    var toolPagingSep = $("<span/>").addClass(opts.tools.separatorClass).appendTo(toolTableColPaging);
                    toolPagingNext = $("<span/>").click(function(event) {
                        if (toolPagingNext.hasClass(opts.tools.enabledClass)) {
                            pageNext(true);
                        }
                    }).addClass(opts.tools.actionClass).addClass(opts.tools.disabledClass).html(opts.tools.paging.htmlNext).appendTo(toolTableColPaging);
                }
                if (opts.tools.close) {
                    var toolTableColClose = $("<td/>").addClass(opts.tools.close.clazz).appendTo(toolTableRow);
                    var toolClose = $("<span/>").click(function() {
                        clearPaging();
                        stopControl();
                        hideList();
                    // $owner.focus();
                    }).addClass(opts.tools.actionClass).addClass(opts.tools.enabledClass).html(opts.tools.close.html).appendTo(toolTableColClose);
                }
            }
            listContainer.appendTo("body");
        }

        function createListItemsElements(itemCount) {
            var addCount = (opts.maxListItems ? opts.maxListItems : itemCount) - itemsElements.length;
            if (addCount > 0) {
                for ( var i = 0; i < addCount; i++) {
                    itemsElements.push($("<li/>").click(function() {
                        doSelected($(this));
                        hideList();
                        // $owner.focus(); // !!!
                        doUserSelect($(this));
                    }).mouseenter(function() {
                        $(this).addClass(opts.hoverClass);
                    }).mouseleave(function() {
                        $(this).removeClass(opts.hoverClass);
                    }).appendTo(listElement));
                }
            }
        }

        function getHighlightHTML(str) {
            return "<span class='" + opts.highlightClass + "'>" + str + "</span>";
        }

        /*
         * 
         */
        function startControl() {
            timerControlId = window.setInterval(function() {
                var text = normalize($owner.val());
                if (testNoMinChars(text)) {
                    suggestText = text;
                    isSuggestComplete = false;
                    if (ajaxSearch) {
                        ajaxSearch.abort();
                    }
                    hideList();
                    return;
                }

                quietCount = suggestText === text ? quietCount + 1 : 0;
                suggestText = text;

                if (quietCount === opts.sensitivity.quiet) {
                    if (localRequest) {
                        doLocalRequest();
                    } else
                    if (ajaxSearch) {
                        if (paging) {
                            clearPaging();
                        }
                        doRequest();
                    } else {
                        isSuggestComplete = false;
                        doList();
                    }
                } else if (quietCount > opts.sensitivity.quiet) {
                    quietCount = opts.sensitivity.quiet + 1;
                }
            }, opts.sensitivity.delay);
            isControl = true;
        }

        function doRequest() {
            isSuggestComplete = false;
            opts.request.params[opts.request.queryParamName] = suggestText;
            if (paging) {
                opts.request.params[opts.request.pageParamName] = requestedPage;
            }
            ajaxSearch.abort();
            ajaxSearch.add( {
                url: opts.request.url,
                dataType: 'json',
                data: opts.request.params,
                complete: function(XMLHttpRequest, textStatus) {
                //
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) {
                //
                },
                success: function(data, textStatus, XMLHttpRequest) {
                    if (textStatus === "success") {
                        suggestData[suggestData.itemsField] = dataIsObject ? data[suggestData.itemsField] : data;
                        doList();
                        if (paging) {
                            paging[suggestData.paging.pageCountField] = data[suggestData.paging.pageCountField];
                            paging[suggestData.paging.pageNumberField] = data[suggestData.paging.pageNumberField];
                            // paging[suggestData.paging.pageSizeField] =
                            // data[suggestData.paging.pageSizeField];
                            // paging[suggestData.paging.totalField] =
                            // data[suggestData.paging.totalField];
                            setPaging();
                        }
                    }
                }
            });
        }

        /*
         * 
         */
        function stopControl() {
            isControl = false;
            if (timerControlId !== null) {
                window.clearInterval(timerControlId);
                timerControlId = null;
            }
            if (ajaxSearch) {
                if (!isPageRequest) {
                    ajaxSearch.abort();
                }
            }
        }

        /*
         * 
         */
        function doList() {
            if (!isControl && !isPageRequest) {
                hideList();
            } else {
                if (!isSuggestComplete) {
                    doSelect(0);
                }
                if ((listItemsCount = isSuggestComplete ? listItemsCount : suggestItems()) > 0) {
                    showList();
                } else {
                    hideList();
                }
            }
        }

        /*
         * 
         */
        function suggestItems() {
            isSuggestComplete = true;
            var ret = 0;
            var dataItems = suggestData[suggestData.itemsField];
            if ($.isArray(dataItems)) {
                createListItemsElements(dataItems.length);
                var searchStr = suggestText;
                if (testNoMinChars(searchStr)) {
                    return 0;
                }
                var itemElement;
                var elementCount = 0;
                var contains = opts.search.matchCase ? searchStr : searchStr.toLowerCase();
                for ( var i = 0; i < dataItems.length; i++) {
                    if (elementCount > itemsElements.length - 1) {
                        break;
                    }
                    var itemData = dataItems[i];
                    var fieldStr = suggestData.searchField ? itemData[suggestData.searchField] : itemData;
                    var str = opts.search.matchCase ? fieldStr : fieldStr.toLowerCase();
                    var index = str.indexOf(contains); // !!!
                    if (!opts.search.on || (opts.search.matchStart ? index == 0 : index >= 0)) {
                        itemElement = itemsElements[elementCount];
                        itemElement.data(opts.itemDataKey, itemData);
                        // display item
                        if (opts.highlightClass) {
                            var html = "";
                            if (opts.search.matchStart) {
                                html = getHighlightHTML(fieldStr.substr(0, contains.length)) + fieldStr.substring(contains.length);
                            } else {
                                var split = str.split(contains); // TODO регулярное выражение для объединения подряд идущих searchStr?
                                var si = 0;
                                for ( var s = 0; s < split.length; s++) {
                                    html += fieldStr.substr(si, split[s].length) + (s === split.length - 1 ? "" : getHighlightHTML(fieldStr.substr(si + split[s].length, contains.length)));
                                    si += (split[s].length + contains.length);
                                }
                            }
                            itemElement.html(html);
                        } else {
                            itemElement.text(fieldStr);
                        }
                        if ($.isFunction(opts.formatItem)) {
                            opts.formatItem.call($owner, itemElement, elementCount, itemData);
                        }
                        elementCount++;
                        itemElement.show();
                    //
                    }
                }
                for ( var e = elementCount; e < itemsElements.length; e++) {
                    itemElement = itemsElements[e];
                    // no display item
                    itemElement.hide();
                //
                }
                ret = elementCount;
            }
            return ret;
        }

        /*
         * 
         */
        function showList() {
            setListPosition();
            listContainer.show();
            isListShow = true;
        }

        function setListPosition() {
            var thisOffset = $owner.offset(); // jQuery opera 9 bug
            listContainer.css( {
                top: (thisOffset.top + $owner.outerHeight()) + "px",
                left: thisOffset.left + "px",
                width: parseInt((opts.outerWidth ? $owner.outerWidth() : $owner.innerWidth()) + opts.extraWidth) + "px"
            });
        }

        /*
         * 
         */
        function hideList() {
            listContainer.hide();
            isListShow = false;
        }

        /*
         * 
         */
        function selectUp() {
            if (paging) {
                if (selectedPos == 1) {
                    var pageCount = paging[suggestData.paging.pageCountField];
                    var pageNumber = paging[suggestData.paging.pageNumberField];
                    if (pageNumber > 1) {
                        pageItemSelected = -1;
                        pagePrev();
                        return;
                    }
                }
            }
            doSelect(selectedPos <= 1 ? listItemsCount : selectedPos - 1);
        }
        function selectDown() {
            if (paging) {
                if (selectedPos == listItemsCount) {
                    var pageCount = paging[suggestData.paging.pageCountField];
                    var pageNumber = paging[suggestData.paging.pageNumberField];
                    if (pageNumber < pageCount) {
                        pageItemSelected = 1;
                        //alert();
                        pageNext();
                        return;
                    }
                }
            }
            doSelect(selectedPos >= listItemsCount ? 1 : selectedPos + 1);
        }

        /*
         * 
         */
        function doSelect(pos) {
            selectedPos = pos;
            for ( var i = 0; i < listItemsCount; i++) {
                itemElement = itemsElements[i];
                if (selectedPos === i + 1) {
                    if (!itemElement.hasClass(opts.selectClass)) {
                        itemElement.addClass(opts.selectClass);
                        if ($.isFunction(opts.selectItem)) {
                            opts.selectItem.call($owner, itemElement, i, itemElement.data(opts.itemDataKey));
                        }
                        if (opts.selectSelected) {
                            doSelected(itemElement);
                        }
                    }
                } else {
                    if (itemElement.hasClass(opts.selectClass)) {
                        itemElement.removeClass(opts.selectClass);
                    }
                }
            }
        }

        /*
         * 
         */
        function doSelected(itemElement) {
            isSelected = false;
            isSuggestComplete = false;
            if (!itemElement) {
                if (selectedPos < 1 || selectedPos > listItemsCount) {
                    return;
                } else {
                    itemElement = itemsElements[selectedPos - 1];
                }
            }
            isSelected = true;
            if (ajaxSearch) {
                ajaxSearch.abort();
            }
            suggestText = suggestData.searchField ? itemElement.data(opts.itemDataKey)[suggestData.searchField] : itemElement.data(opts.itemDataKey);
            $owner.val(suggestText);
            actualText = suggestText;
            if ($.isFunction(opts.onSelected)) {
                opts.onSelected.call($owner, itemElement.data(opts.itemDataKey));
            }
        }

        function doUserSelect(itemElement) {
            if (!itemElement) {
                if (selectedPos < 1 || selectedPos > listItemsCount) {
                    return false;
                } else {
                    itemElement = itemsElements[selectedPos - 1];
                }
            }
            if ($.isFunction(opts.onUserSelect)) {
                opts.onUserSelect.call($owner, itemElement.data(opts.itemDataKey));
            }
            return true;
        }

        function setPaging() {
            var pageCount = paging[suggestData.paging.pageCountField];
            var pageNumber = paging[suggestData.paging.pageNumberField];
            //var pageSize = paging[suggestData.paging.pageSizeField];
            //var total = paging[suggestData.paging.totalField];

            if (pageCount && pageNumber) {
                if (pageCount <= 1) {
                    toolPagingPrev.hide();
                    toolPagingNext.hide();
                } else if (pageNumber == 1) {
                    toolPagingPrev.show();
                    toolPagingPrev.removeClass(opts.tools.enabledClass).addClass(opts.tools.disabledClass);
                    toolPagingNext.show();
                    toolPagingNext.removeClass(opts.tools.disabledClass).addClass(opts.tools.enabledClass);
                } else if (pageNumber == pageCount) {
                    toolPagingPrev.show();
                    toolPagingPrev.removeClass(opts.tools.disabledClass).addClass(opts.tools.enabledClass);
                    toolPagingNext.show();
                    toolPagingNext.removeClass(opts.tools.enabledClass).addClass(opts.tools.disabledClass);
                } else {
                    toolPagingPrev.show();
                    toolPagingPrev.removeClass(opts.tools.disabledClass).addClass(opts.tools.enabledClass);
                    toolPagingNext.show();
                    toolPagingNext.removeClass(opts.tools.disabledClass).addClass(opts.tools.enabledClass);
                }

                if (pageItemSelected > 0) {
                    doSelect(1);
                } else if (pageItemSelected < 0) {
                    doSelect(listItemsCount);
                }
            } else {
                toolPagingPrev.hide();
                toolPagingNext.hide();
            }
        }

        function pagePrev(clearSelected) {
            if (clearSelected) {
                pageItemSelected = 0;
            }
            var pageNumber = paging[suggestData.paging.pageNumberField];
            requestedPage = pageNumber <= 1 ? 1 : pageNumber - 1;
            doPage();
        }

        function pageNext(clearSelected) {
            if (clearSelected) {
                pageItemSelected = 0;
            }
            var pageNumber = paging[suggestData.paging.pageNumberField];
            var pageCount = paging[suggestData.paging.pageCountField];
            requestedPage = pageNumber >= pageCount ? pageCount : pageNumber + 1;
            doPage();
        }

        function doPage() {
            isPageRequest = true;
            if (ajaxSearch) {
                doRequest();
            }
        }

        function clearPaging() {
            isPageRequest = false;
            requestedPage = 1;
            pageItemSelected = 0;
        }

        function isActualText() {
            return actualText == $owner.val();
        }

        function doLocalRequest(){
            isSuggestComplete = false;
            suggestData[suggestData.itemsField] = localRequest.call($owner, suggestText);
            doList();
        }

        // Public
        function getOwner() {
            return $owner;
        }

        function setRequestURL(url) {
            opts.request.url = url;
        }

        function setRequestParams(params) {
            opts.request.params = params;
        }

        function isListFocused() {
            return isListFocus;
        }

        // Suggest object
        return { // TODO doc
            opts: opts,
            widget: listContainer,
            getOwner: getOwner,
            setRequestURL: setRequestURL,
            setRequestParams: setRequestParams,
            isListFocused: isListFocused
        }
    };
    //
    $.fn.suggest = function(options) {
        if ( options === undefined ) {
            return this.data($.fn.suggest.accessName);
        } else {
            return this.each(function() {
                var suggest = new $.suggest(this, options);
                suggest.getOwner().data($.fn.suggest.accessName, suggest);
            });
        }
    };
    //
    $.fn.suggest.accessName = "suggest";
    //
    $.fn.suggest.defaults = {
        request: {
            url: null,
            queryParamName: "q",
            pageParamName: null,
            params: {}
        },
        data: {
            items: [],
            itemsField: null,
            searchField: null,
            paging: null /*{
                pageCountField: null,
                pageNumberField: null,
                pageSizeField: null,
                totalField: null
            }*/
        },
        editClass: "jquery-suggest-edit",
        listClass: "jquery-suggest-list",
        hoverClass: "jquery-suggest-list-item-hover",
        selectClass: "jquery-suggest-list-item-select",
        highlightClass: "jquery-suggest-search-highlight",
        tools: {
            toolsClass: "jquery-suggest-list-tools",
            actionClass: "action",
            enabledClass: "enabled",
            disabledClass: "disabled",
            separatorClass: "separator",
            paging: {
                clazz: "jquery-suggest-list-tools-paging",
                htmlPrev: "<span>&#8592;</span>",
                htmlNext: "<span>&#8594;</span>"
            },
            close: {
                clazz: "jquery-suggest-list-tools-close",
                html: "<span>&#215;</span>"
            }
        },
        search: {
            /**
             * Если on = true, то используется поиск по данным.
             */
            on: false,
            /**
             * Если matchCase = true, то учитывать регистр, иначе не
             * учитывать регистр.
             */
            matchCase: false,
            /**
             * Если matchStart = true, то поиск от начала, иначе вхождение в
             * любом месте.
             */
            matchStart: false
        },
        selectSelected: false,
        formatItem: function(itemElement, index, itemData) {
        }, // TODO doc (scope, params, ...)
        selectItem: function(itemElement, index, itemData) {
        }, // TODO doc (scope, params, ...)
        onSelected: function(itemData) {
        }, // TODO doc (scope, params, ...)
        onUserSelect: function(itemData) {
        }, // TODO doc (scope, params, ...)
        onBlurWithChanged: function() {
        }, // TODO doc (scope, params, ...)
        onInputEnter: function(isUserSelected) {
        }, // TODO doc (scope, params, ...)
        localRequest: null, //function(query) {return [resultObj, ...]}, // TODO doc (scope, params, ...)
        minChars: 1,
        /**
         * Если maxListItems = null, то отображаются все даные.
         */
        maxListItems: null,
        itemDataKey: "itemData",
        outerWidth: false,
        extraWidth: 0,
        sensitivity: {
            delay: 5,
            quiet: 10
        }
    };
    //
})(jQuery);