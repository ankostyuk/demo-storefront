/**
 * nullpointer storefront helper
 *
 * Служебные, общие функции и классы для storefront
 *
 * Copyright 2010, nullpointer
 * Dual licensed under the MIT or GPL Version 2 licenses.
 * 
 * required:
 * jquery-1.4.2.js
 * jquery.cookie.js
 * 
 * @author ankostyuk
 * 
 */

/*
// TODO Проверить и, по возможности, переписать на jsHelper.isOuterClick():
$(document).click(function(e) {
    var target = $(e.target).parents().andSelf();
    if (!target.is("selector-1") && !target.is("selector-N")) {
        dropdownClose();
    }
});
*/

/*
// TODO jQuery Tools 1.2.4 tooltip bug http://github.com/jquerytools/jquerytools/issues/issue/157
- Отслеживать fix.
+ fixed:
Использовать fix-версию http://github.com/jquerytools/jquerytools/raw/69d52693ee27b06f5effa5faa10e446b08fdf609/src/tooltip/tooltip.js.
Или дождаться выхода 1.2.X cо всеми fix-ами.

Или при выходе jqueryui Tooltip http://wiki.jqueryui.com/Tooltip, по возможности, перевести подсказки на jqueryui Tooltip.
*/

var storefront = {
    helper : {
        window: $(window), // TODO заменить везде $(window) на jsHelper.window
        document: $(document), // TODO заменить везде $(document) на jsHelper.document
        effectSpeed: 150,
        transferEffectSpeed: 300,
        steppedEffectSpeed: [100, 50],
        popupShowDelay: 2000,
        tooltipShowDelay: 500,
        tooltipShowPredelayFast: 0,
        tooltipShowPredelayNormal: 500,
        marginRight: 30,
        dateFormat: "dd.mm.yy",
        dateSep: ".",
        //
        // Не делать некоторые "фишки", если меньше IE8 (см. TODO) - меньше гемора, кто под IE7 и ниже - сам виноват :)
        isLessIE8: function() {
            if ($.browser.msie
                && ($.browser.version == "6.0" || $.browser.version == "7.0")) {
                return true;
            }
            return false;
        },
        //
        clearUrl: function(url) {
            return url.replace(/;[^\?]*/ig, "");
        },
        //
        // TODO Chrome bug (click for label + input -> event.pageX = 0, event.pageY = 0 )
        isOuterClick: function(el, event) {
            if (el.is(":hidden")) {
                return false;
            }
            var offset = el.offset();
            if (event.pageX < offset.left || event.pageX > offset.left + el.outerWidth(false)
                || event.pageY < offset.top || event.pageY > offset.top + el.outerHeight(false)) {
                return true;
            }
            return false;
        },
        //
        buildEscapeEvent: function(el, callback, ignoreClickSelector) {
            jsHelper.document.click(function(e) {
                // TODO Проверить везде, где вызывается <code>buildEscapeEvent</code>,
                // возможно надо передавать <code>ignoreClickSelector</code>
                // для избежания несвоевременного закрытия.
                if (ignoreClickSelector) {
                    if ($(e.target).is(ignoreClickSelector)) {
                        return;
                    }
                }
                if (jsHelper.isOuterClick(el, e)) {
                    callback.call(el);
                }
            }).keydown(function(e) {
                if (e.keyCode === 27) { // ESC key
                    callback.call(el);
                }
            });
            // TODO ?
            //jsHelper.window.scroll(function (){
            //    callback.call(el);
            //}).resize(function (){ // не всегда генерится!
            //    callback.call(el);
            //});
        },
        //
        // TODO перенести в <code>buildRegionSuggest</code>
        regionSuggest: {
            data: {
                itemsField: "list",
                searchField: "name",
                paging: {
                    pageCountField: "pageCount",
                    pageNumberField: "pageNumber",
                    pageSizeField: "pageSize",
                    totalField: "total"
                }
            },
            formatItem: function(itemElement, index, itemData) {
                var html = itemElement.html();
                html += "<p><small>";
                for (var i = itemData.path.length - 1; i >= 0; i--) {
                    html += itemData.path[i].name + (i == 0 ? "" : ", ");
                }
                html += "</small></p>";
                itemElement.html(html);
            },
            selectSelected: false
        },
        //
        /*
            options: {
                url: "",
                nameInputSelector: "",
                idInputSelector: "",
                regionPathSelector: "",
                onRegionSelect: function(region) {}
            }
         */
        buildRegionSuggest: function(options) {
            jsHelper.buildSuggest({
                selector: options.nameInputSelector,
                    suggestOptions: {
                    request: {
                        url: options.url,
                        queryParamName: "text",
                        pageParamName: "page"
                    },
                    data: jsHelper.regionSuggest.data,
                    formatItem: jsHelper.regionSuggest.formatItem,
                    selectSelected: jsHelper.regionSuggest.selectSelected,
                    onSelected: function(itemData) {
                        if ($.isFunction(options.onRegionSelect)) {
                            options.onRegionSelect.call(this, itemData);
                        } else {
                            $(options.idInputSelector).val(itemData.id);
                        }
                        if (options.regionPathSelector) {
                            var startIndex = itemData.path.length - 1;
                            var path = startIndex >= 0 ? itemData.name : "";
                            for (var i = startIndex; i >= 0; i--) {
                                path += (i == startIndex ? " (" : "") + itemData.path[i].name + (i == 0 ? "" : ", ") + (i == 0 ? ")" : "");
                            }
                            $(options.regionPathSelector).html(path);
                        }
                    },
                    onBlurWithChanged: function() {
                        $(options.idInputSelector).val("");
                        if (options.regionPathSelector) {
                            $(options.regionPathSelector).html("");
                        }
                    }
                }
            });
        },
        //
        // TODO make plugin
        /*
            options: {
                pseudoLinkSelector: "",
                pseudoLinkClass: "",
                copyLink: true|false,
                requestData: {},
                requestType: "POST",
                withConfirm: false,
                confirmMessage: "",
                dropdownContentSelector: "",
                dropdownClass: "",
                success: function(data, textStatus, XMLHttpRequest){}
            }
         */
        buildPseudoLink: function(options) {
                
            var link = null;
            var dropdown = null;

            if (options.dropdownContentSelector || options.withConfirm === true) {
                dropdown = $(
                    '<div class="pseudo-link-dropdown"><div class="dropshadow"></div><table class="content-box shadow-wrap" cellpadding="0" cellspacing="0"><tbody>' +
                        '<tr><td class="link-box"><a class="pseudo replacement" href="#"></a></td><td class="tools"><span class="close"></span></td></tr>' +
                        (options.dropdownContentSelector ? '<tr><td class="content" colspan="2"></td></tr>' : "") +
                        (options.confirmMessage ? '<tr><td class="confirm-message" colspan="2"></td></tr>' : "") +
                        (options.withConfirm === true ? '<tr><td class="confirm" colspan="2"><a class="pseudo act yes">Да</a><span class="sep">&nbsp;</span><a class="pseudo act no" href="#">Нет</a></td></tr>' : "") +
                    '</tbody></table></div>'
                ).appendTo("body");

                if (options.dropdownContentSelector) {
                    dropdown.find(".content").append($(options.dropdownContentSelector).show());
                }
                if (options.confirmMessage) {
                    dropdown.find(".confirm-message").html(options.confirmMessage);
                }
                if (options.withConfirm === true) {
                    dropdown.find("a.yes").click(function(e){
                        e.preventDefault();
                        dropdownClose();
                        ajaxPost(this);
                    });
                }

                dropdown.addClass(options.dropdownClass).find("a.replacement, .close, a.no").click(function(e){
                    e.preventDefault();
                    dropdownClose();
                });

                jsHelper.buildEscapeEvent(dropdown, dropdownClose, options.pseudoLinkSelector);
            }
            
            var links = $(options.pseudoLinkSelector).addClass(options.pseudoLinkClass).click(function(e){
                e.preventDefault();

                link = $(this);

                if (dropdown) {
                    if (options.copyLink) {
                        dropdown.data("link", link).find("a.replacement")
                            .addClass(link.attr("class"))
                            .html(link.html());
                    } else {
                        dropdown.data("link", link).find("a.replacement")
                            .css("color", link.css("color")) // Наверное, должно совпадать с цветом изначальной ссылки
                            .text(link.text());
                    }

                    dropdown.find("a.yes").attr("href", link.attr("href"));

                    link.before(dropdown);
                    
                    dropdown.show();
                } else {
                    ajaxPost(this);
                }
            });

            if (dropdown) {
                links.wrap("<div class='pseudo-link-wrapper'></div>");
            }

            function dropdownClose() {
                dropdown.hide();
            }

            function ajaxPost(link) {
                $.ajax({
                    context: link,
                    url: $(link).attr("href"),
                    data: options.requestData,
                    type: (options.requestType || "POST"),
                    success: options.success
                });
            }

            function getLink() {
                return link;
            }
            
            function hideDropdown() {
                if (dropdown) {
                    dropdownClose();
                }
            }

            // public
            return {
                getLink: getLink,
                hideDropdown: hideDropdown
            }
        },
        //
        /*
            options: {
                noError: function(html) - override default. Through scope.
                beforeNoError: function(html) - Through scope.
            }
         */
        buildPseudoLinkSuccessCallback: function(options) {
            options = options || {};
            return function(data){
                var link = $(this);
                var html = $(data);
                if (html.is(".action-error-info")) {
                    link.addClass("action-error");
                    jsHelper.globalMessage.showMessage(html);
                } else {
                    if ($.isFunction(options.noError)) {
                        options.noError.call(link, html);
                    } else {
                        if ($.isFunction(options.beforeNoError)) {
                            options.beforeNoError.call(link, html);
                        }
                        jsHelper.replaceContent(link, html);
                    }
                }
            }
        },
        //
        replaceContent: function(content, newContent) {
            content.fadeOut(jsHelper.steppedEffectSpeed[0], function(){
                $(this).replaceWith(newContent).fadeIn(jsHelper.steppedEffectSpeed[1]);
            });
        },
        //
        removeContent: function(content) {
            content.slideUp(jsHelper.effectSpeed, function(){
                $(this).detach();
            });
        },
        // Глобальные сообщения
        // id="global-message"
        globalMessage: null,
        buildGlobalMessage: function() {
            var contentBox = $('<div class="content-box shadow-wrap"></div>');
            var globalMessage = $('<div id="global-message"><div class="dropshadow"></div></div>').append(contentBox).appendTo("body");
            var closeTimeout = null;

            jsHelper.buildEscapeEvent(globalMessage, close);

            function open(withShowDelay) {
                globalMessage.slideDown(jsHelper.effectSpeed, function(){
                    if (closeTimeout !== null) {
                        window.clearTimeout(closeTimeout);
                        closeTimeout = null;
                    }
                    if (withShowDelay) {
                        closeTimeout = window.setTimeout(function(){
                            close();
                        }, jsHelper.popupShowDelay);
                    }
                });
            }

            function close(callback) {
                globalMessage.slideUp(jsHelper.effectSpeed, callback);
            }

            function showMessage(content, withShowDelay) {
                close(function(){
                    contentBox.html(content);
                    open(withShowDelay);
                });
            }

            // public
            var globalMessageObj = {
                showServerErrorMessage: function(){
                    showMessage('<p>Извините, но на сервере произошла ошибка</p>', true);
                },
                showMessage: showMessage
            }

            jsHelper.globalMessage = globalMessageObj;

            return globalMessageObj;
        },
        //
        // Подсказка по параметрам
        // id="param-tooltip"
        /*
            options: {
                selector: "",
                paramIdPrefix: "",
                categoryName: "",
                categoryTermUrl: "",
                right: true
            }
         */
        // TODO IE7 zoom fix:
        // - уплывание контента,
        // - нет вывода при масштабе > 100% (не всегда).
        buildParamTooltip: function(options) {
            if (jsHelper.isLessIE8()) {
                return false;
            }

            options.categoryTermUrl = jsHelper.clearUrl(options.categoryTermUrl);

            var waitMessage = $('<p class="info">Подождите...</p>');
            var htmlBox = $('<div></div>');
            var contentBox = $('<div class="content-box"><p class="term-page-link"><a href="' + options.categoryTermUrl + '">Словарь категории «' + options.categoryName + '»</a></p></div>').prepend(htmlBox).prepend(waitMessage);
            var paramTooltip =
                $('<div id="param-tooltip" class="' + (options.right ? "right" : "left") + '"><div class="top-box"><span class="close"></span></div></div>')
                .append(contentBox)
                .append('<div class="bottom-box"></div>')
                .appendTo("body");
            
            var ajaxParam = $.manageAjax.create("ajaxParam", {
                queue: true,
                maxRequests: 1,
                abortOld: true,
                cacheResponse: true,
                preventDoubbleRequests: true
            });

            paramTooltip.mouseenter(function(){
                return false;
            }).click(function(e){
                e.stopPropagation();
            }).find("div.top-box span.close").click(function(e){
                e.preventDefault();
                tooltipClose();
            });
            jsHelper.buildEscapeEvent(paramTooltip, tooltipClose, options.selector);
            
            $(options.selector).each(function(){
                var target = $(this).wrap("<div class='param-tooltip-wrapper'><div></div></div>"); // TODO может в остальных подобных случаях (pseudo-link, ...) тоже обернуть в 2 div-а, первй display: inline, второй display: inline-block (см. CSS)
                target.click(function(e){
                        e.preventDefault();
                        var t = $(this);
                        t.before(paramTooltip);
                        htmlBox.empty();
                        waitMessage.show();
                        ajaxParam.add({
                            url: options.categoryTermUrl + "/" + t.attr("id").substr(options.paramIdPrefix.length),
                            dataType: "html",
                            complete: function() {
                            },
                            error: function() {
                                tooltipClose();
                            },
                            success: function(data, textStatus, XMLHttpRequest) {
                                if (textStatus === "success") {
                                    waitMessage.hide();
                                    htmlBox.html(data);
                                } else {
                                    tooltipClose();
                                }
                            }
                        });
                        paramTooltip.show();
                    });
            });

            function tooltipClose() {
                ajaxParam.abort();
                paramTooltip.hide();
            }

            return true;
        },
        // Системная подсказка
        // id="system-tooltip"
        // TODO IE7 zoom fix:
        // - уплывание контента,
        // - неправильное позиционирование при масштабе != 100%.
        /**
         * Селектор для системных подсказок.
         * Обязательно наличие атрибута "title", "alt" - не учитывается, надо?
         * События появления подсказки:
         * - для abbr - "click",
         * - для остальных - "mouseenter".
         */
        systemTooltipSelector: "abbr[title]",//, a[title], #userbar span.region-name[title]",
        buildSystemTooltip: function() {
            if (jsHelper.isLessIE8()) {
                return false;
            }

            var contentBox = $('<div class="content-box shadow-wrap"></div>');
            $('<div id="system-tooltip"><div class="dropshadow"></div></div>').append(contentBox).appendTo("body");

            // Инициализация подсказки. При первом "mouseenter"
            $(jsHelper.systemTooltipSelector).live("mouseenter", function(e){
                var trigger = $(this);
                if (!trigger.data("system-tooltip-html")) {
                    var isClickTrigger = trigger.is("abbr");

                    trigger.data("system-tooltip-html", trigger.attr("title"))
                    .attr("title", "") // Именно пустая строка для атрибута "title", а не удаление "title", т.к. IE8 при первом "mouseenter" и удалении "title" в контексте события "mouseenter", показывает свою системную подсказку, а при при пустой строке - нет. Удаляется "title" потом, на всякий случай, при показе (см. <code>onBeforeShow</code>). Сначала пустая строка, а потом сразу удаление "title" - не прокатывает.
                    .tooltip({
                        cancelDefault: false,
                        //effect: "fade", // TODO fix IE fade bug
                        position: "bottom right",
                        offset: [5, 0],
                        predelay: isClickTrigger ? jsHelper.tooltipShowPredelayFast : jsHelper.tooltipShowPredelayNormal,
                        events: {
                            def: isClickTrigger ? "click,mouseleave" : "mouseenter,mouseleave",
                            tooltip: "mouseenter,mouseleave"
                        },
                        onBeforeShow: function() {
                            contentBox.html(this.getTrigger().removeAttr("title").data("system-tooltip-html"));
                        },
                        onShow: function() {
                            // fix выхода за горизонтальный размер окна // TODO За вертикальный размер
                            var tooltip = this.getTip();
                            var left = tooltip.offset().left;
                            var d = jsHelper.window.width() + jsHelper.window.scrollLeft() - (left + tooltip.outerWidth() + jsHelper.marginRight);
                            if (d <= 0) {
                                tooltip.css("left", left + d + "px");
                            }
                        },
                        tip: "#system-tooltip"
                    });

                    if (isClickTrigger) {
                        // Принудительно установить [cursor: pointer] для "mouseclick" элементов
                        trigger.css("cursor", "pointer");
                    } else {
                        // Принудительно показать tooltip для "mouseenter" элементов
                        trigger.mouseenter();
                    }
                }
            });
            
            return true;
        },
        //
        // "Ползунки"
        /*
            options: {
                selector: "",
                cssClass: "",
                optionRangeMap: {}
            }
         */
        buildSlider: function(options) {
            $(options.selector).each(function(){
                var sliderHTML = '<div class="' + options.cssClass + '"><div class="corner-left"><div class="value">{g1}</div></div><div class="guide g2"><div class="value">{g2}</div></div><div class="guide g3 next"><div class="value">{g3}</div></div><div class="guide g4 next"><div class="value">{g4}</div></div><div class="guide g5 next"><div class="value">{g5}</div></div><div class="corner-right"><div class="value">&nbsp;</div></div></div>';
                //
                var container = $(this);
                var option = container.attr("id").substr("option-".length);
                //
                var o = options.optionRangeMap[option];
                var rangeProperties = $.fn.customSlider.getRangeProperties(o.rangeMin, o.rangeMax, 100, 10);

                var guideStep = (rangeProperties.max - rangeProperties.min)/4;

                // Не более 2-ух знаков после запятой
                sliderHTML = sliderHTML.replace("{g1}", parseFloat((rangeProperties.min).toFixed(2)));
                sliderHTML = sliderHTML.replace("{g2}", parseFloat((rangeProperties.min + guideStep).toFixed(2)));
                sliderHTML = sliderHTML.replace("{g3}", parseFloat((rangeProperties.min + 2*guideStep).toFixed(2)));
                sliderHTML = sliderHTML.replace("{g4}", parseFloat((rangeProperties.min + 3*guideStep).toFixed(2)));
                sliderHTML = sliderHTML.replace("{g5}", parseFloat((rangeProperties.max).toFixed(2)));
                //
                var slider = $(sliderHTML).customSlider({
                    min: rangeProperties.min,
                    max: rangeProperties.max,
                    step: rangeProperties.step,
                    values: [rangeProperties.min, rangeProperties.max],
                    range: true,
                    stop: function(event, ui) {
                        pushSliderValues($(this), ui.values[0], ui.values[1]);
                    },
                    slide: function(event, ui) {
                        setInputValues($(this), ui.values[0], ui.values[1]);
                    }
                }).appendTo(container).show();
                //
                var inputMin = container.find("#" + option + "-min");
                var inputMax = container.find("#" + option + "-max");
                inputMin.attr("autocomplete", "off").data("slider", slider).data("sliderRangeIndex", 0);
                inputMax.attr("autocomplete", "off").data("slider", slider).data("sliderRangeIndex", 1);
                slider.data("inputs", [inputMin, inputMax]);
                setSliderValues(slider, inputMin.val(), inputMax.val());
            });

            function popSliderValues(slider) {
                return slider.data("values");
            }

            function pushSliderValues(slider, val0, val1) {
                slider.data("values", [val0, val1]);
            }

            function setSliderValues(slider, val0, val1) {
                var values = slider.data("values");

                // TODO locale parse
                var f0 = parseFloat(val0);
                var f1 = parseFloat(val1);

                f0 = isNaN(f0) ? (val0 == "" ? slider.customSlider("option", "min") : values[0]) : f0; // TODO min
                f1 = isNaN(f1) ? (val1 == "" ? slider.customSlider("option", "max") : values[1]) : f1; // TODO max

                if (val0 != null) {
                    if (f0 > f1) {
                        f0 = f1;
                    }
                    slider.customSlider("values", 0, f0);
                }
                if (val1 != null) {
                    if (f1 < f0) {
                        f1 = f0;
                    }
                    slider.customSlider("values", 1, f1);
                }

                pushSliderValues(slider, f0, f1);
            }

            function setInputValues(slider, val0, val1) {
                var inputs = slider.data("inputs");
                var min = slider.customSlider("option", "min");
                var max = slider.customSlider("option", "max");
                inputs[0].val(val0 <= min || val0 >= max ? "" : val0);
                inputs[1].val(val1 >= max || val1 <= min ? "" : val1);
            }

            return {
                setSliderValues: setSliderValues
            }
        },
        //
        // "Подсказчик"
        /*
            options: {
                selector: "",
                suggestOptions: {}
            }
         */
        buildSuggest: function(options) {
            var suggest = $(options.selector).suggest(options.suggestOptions).suggest();
            var suggestWidget = suggest.widget;
            suggestWidget.children().wrapAll('<div class="shadow-wrap"></div>');
            suggestWidget.prepend('<div class="dropshadow"></div>');
            return suggest;
        },
        //
        // Редактирование региона
        /*
            options: {
                pseudoLinkSingleSelector: "",
                linkText: "",
                ignoreEscapeEventSelector: "",
                pseudoLinkClass: "",
                regionSuggestURL: "",
                postURL: "",
                regionName: "",
                regionAware: true|false
            }
         */
        buildRegionInlineSet: function(options) {
            var dropdown = $('<div class="region-inline-dropdown"><div class="dropshadow"></div><form id="inline-region-form" action="' + options.postURL + '" method="post"><table class="content-box shadow-wrap" cellpadding="0" cellspacing="0"><tbody><tr><td class="link-box"><a class="pseudo replacement" href="#"></a></td><td class="region-box"><input id="inline-region-name" name="inline-region-name" type="text" value="' + options.regionName + '" /></td><td class="tools"><span class="close"></span></td></tr><tr><td class="region-aware-box" colspan="3"><label><input id="inline-region-aware" name="inline-region-aware" type="checkbox"' + (options.regionAware ? ' checked="checked"' : '') + ' />&nbsp;не показывать предложения других регионов</label></td></tr><tr><td class="region-submit-box" colspan="3"><input type="submit" value="Установить" /><input id="inline-region-id" name="inline-region-id" type="hidden" /></td></tr></tbody></table></form></div>');
            var links = $(options.pseudoLinkSingleSelector).wrap("<div class='pseudo-link-wrapper'></div>");
            
            dropdown.appendTo("body");
            
            var regionInput = dropdown.find("#inline-region-name");
            var awareInput = dropdown.find("#inline-region-aware");
                
            dropdown.find("a.replacement, .close").click(function(e){
                e.preventDefault();
                dropdownClose();
            });

            links.addClass(options.pseudoLinkClass).click(function(e){
                e.preventDefault();
                //
                var link = $(this);

                regionInput.val(options.regionName);
                if (awareInput.data("regionAware")) {
                    awareInput.attr({checked: awareInput.data("regionAware")});
                } else {
                    awareInput.removeAttr("checked");
                }
                //
                dropdown.find("a.replacement").text(link.children().length > 0 ? options.linkText : link.text());
                link.before(dropdown);
                dropdown.show();
            });

            awareInput.data("regionAware", awareInput.attr("checked"));

            jsHelper.buildRegionSuggest({
                url: options.regionSuggestURL,
                nameInputSelector: "#inline-region-name",
                idInputSelector: "#inline-region-id"
            });

            $("#inline-region-form").ajaxForm({
                dataType: "json",
                success: function(result) {
                    window.location.reload(true);
                }
            });

            jsHelper.buildEscapeEvent(dropdown, dropdownClose, (options.ignoreEscapeEventSelector ? (options.ignoreEscapeEventSelector + ", ") : "") + "div.region-inline-dropdown form *");

            function dropdownClose() {
                dropdown.hide();
            }
        },
        //
        // Всплывающее окно изображения
        /*
            options: {
                title: "",
                imageUrl: "",
                pseudoLinkSelector: "",
                pseudoLinkClass: ""
            }
         */
        // TODO При необходимости схожей функциональности (иной контент внутри окна)
        // взять за основу диалог из данного кода и расширить код
        buildImagePopup: function(options) {
            var popupContent  = $('<div><img alt="" src="' + options.imageUrl + '" /></div>');

            popupContent.dialog({
                autoOpen: false,
                title: options.title,
                width: "auto",
                height: "auto",
                position: ["left", "top"],
                resizable: false,
                closeText: "Закрыть",
                closeOnEscape: false,
                open: function(event, ui) {
                    var dialog = popupContent.dialog("widget");
                    dialog.css("width", dialog.width() + "px");
                    popupContent.dialog("option", "position", "center");
                }
            });

            var dialog = popupContent.dialog("widget");
            dialog.children().wrapAll('<div class="shadow-wrap"></div>');
            dialog.prepend('<div class="dropshadow"></div>');

            jsHelper.buildEscapeEvent(dialog, popupClose, options.pseudoLinkSelector);

            $(options.pseudoLinkSelector).addClass(options.pseudoLinkClass).click(function(e){
                e.preventDefault();
                popupContent.dialog("open");
            });

            function popupClose() {
                popupContent.dialog("close");
            }
        },
        //
        // Всплывающий календарь
        /*
            options: {
                inputSelector: ""
            }
         */
        buildDatepicker: function(options) {
            var contentBox = $('<div class="content-box shadow-wrap"></div>');
            var dropdown = $('<div class="datepicker-dropdown"><div class="dropshadow"></div></div>').append(contentBox).appendTo("body");

            var prolongationTools = $('<div class="datepicker-prolongation-box"><table class="datepicker-prolongation"><tbody><tr><td class="t-1"><a href="#">+2 месяца</a></td><td class="t-2"><a href="#">+3 месяца</a></td><td class="t-3"><a href="#">+6 месяцев</a></td></tr></tbody></table></div>');
            
            prolongationTools.find(".t-1 a").click(function(e){
                e.preventDefault();
                monthProlongation(2);
            });
            prolongationTools.find(".t-2 a").click(function(e){
                e.preventDefault();
                monthProlongation(3);
            });
            prolongationTools.find(".t-3 a").click(function(e){
                e.preventDefault();
                monthProlongation(6);
            });

            contentBox.append(prolongationTools).datepicker({
                onSelect: function(dateText, inst) {
                    dropdown.hide().data("input").val(dateText);
                }
            }).find("*[title]").removeAttr("title");

            $(options.inputSelector).attr("autocomplete", "off").wrap("<div class='datepicker-input-wrapper'></div>").click(function(){
                var input = $(this);
                dropdown.data("input", input).css("margin-left", input.css("margin-left"));
                input.after(dropdown);
                contentBox.datepicker("setDate" , parseDate(input.val()));
                dropdown.show();
            });

            $("#ui-datepicker-div").remove();

            jsHelper.buildEscapeEvent(dropdown, dropdownClose, options.inputSelector);

            function dropdownClose() {
                dropdown.hide();
            }

            function parseDate(value) {
                var dmy = value.split(jsHelper.dateSep);
                var day = parseInt(dmy[0], 10);
                var month = parseInt(dmy[1], 10);
                var year = parseInt(dmy[2], 10);
                var currentDate = new Date();
                var date = new Date(currentDate.getTime());
                date.setDate(1);
                if (!isNaN(year)) {
                    date.setFullYear(year);
                }
                if (!isNaN(month)) {
                    date.setMonth(month - 1);
                }
                if (!isNaN(day)) {
                    date.setDate(day);
                    return date;
                }
                return currentDate;
            }

            function monthProlongation(monthCount) {
                var date = contentBox.datepicker("getDate");
                var initialDay = date.getDate();
                date.setMonth(date.getMonth() + monthCount, 1);
                var dayCount = new Date(date.getFullYear(), date.getMonth() + 1, 0).getDate();
                if (dayCount < initialDay) {
                    date.setDate(dayCount);
                } else {
                    date.setDate(initialDay);
                }
                dropdown.hide().data("input").val($.datepicker.formatDate(jsHelper.dateFormat, date));
            }
        },
        //
        // Сворачивание/разворачивание контента
        /*
            options: {
                container: "",
                collapsedClass: "",
                addClass: "",
                click: "",
                collapsable: ""
            }
         */
        buildCollapsedContent: function(options) {
            $(options.container).addClass(options.addClass).find(options.click).click(function(){
                var container = $(this).parents(options.container);
                var collapsable = container.find(options.collapsable);
                if (container.hasClass(options.collapsedClass)) {
                    container.removeClass(options.collapsedClass);
                    collapsable.slideDown(jsHelper.effectSpeed);
                } else {
                    collapsable.slideUp(jsHelper.effectSpeed, function(){
                        container.addClass(options.collapsedClass);
                    });
                }
            });
        },
        //
        // @Deprecated
        // Фильтр каталога
        /*
            options: {
                catalogFilterBox: "",
                pseudoLinkSelector: "",
                pseudoLinkClass: "",
                filterOptionsSelector: "",
                filterOptionCurrentClass: "",
                filterOptionApplyClass: "",
                filterOptionAllClass: "",
                defaultFilterOptionSelector: "",
                catalogTreeItemSelector: "",
                catalogTreeApplySelector: ""
            }
         */
        // TODO ссылки фильтра: href, cursor
        buildCatalogFilter: function(options) {
            $(options.pseudoLinkSelector).addClass(options.pseudoLinkClass);
            $(options.filterOptionsSelector).click(function(e){
                e.preventDefault();
                doFilter($(this));
            });

            function doFilter(link, force) {
                if (link.hasClass(options.filterOptionApplyClass)) {
                    if (force || !link.parent().hasClass(options.filterOptionCurrentClass)) {
                        showApply();
                    }
                    // TODO Callback: показать все предложения
                    // TODO сделать неактивной ссылку при показе всех предложений
                } else
                if (force || (link.hasClass(options.filterOptionAllClass) && !link.parent().hasClass(options.filterOptionCurrentClass))) {
                    showAll();
                }

                if (!link.parent().hasClass(options.filterOptionCurrentClass)) {
                    $(options.catalogFilterBox + " ." + options.filterOptionCurrentClass).removeClass(options.filterOptionCurrentClass);
                    link.parent().addClass(options.filterOptionCurrentClass);
                }
            }

            function showApply() {
                $(options.catalogTreeItemSelector + options.catalogTreeApplySelector).hide();
            }
            function showAll() {
                $(options.catalogTreeItemSelector + options.catalogTreeApplySelector).show();
            }

            if ($.browser.msie) { // IE8(7?) fix first showMyCategories()
                $(options.catalogTreeItemSelector).hide().show();
            }

            doFilter($(options.defaultFilterOptionSelector), true);
        },
        //
        // Дерево со сворачиваемыми/разворачиваемыми узлами
        /*
            options: {
                treeSelector: "",
                treeClass: "",
                collapseByNameClickSelector: "",
                initiallyOpenIds: [],
                stateToCookie: true|false,
                stateFromCookie: true|false,
                cookieName: "",
                cookieOptions: {}
            }
         */
        buildCollapsableTree: function(options) {
            var initiallyOpenIds = mergeWithCookie(options.initiallyOpenIds);

            var catalogTree = $(options.treeSelector).addClass(options.treeClass);

            catalogTree.find("li:has(> ul)").each(function(){
                var item = $(this);
                item.addClass("has-collapse").prepend('<div class="node-collapse-tool"></div>');
                if ($.inArray(item.attr("id"), initiallyOpenIds) >= 0) {
                    item.addClass("expanded");
                } else {
                    item.find("> ul").hide();
                }
            });

            toCookie();

            catalogTree.find("div.node-collapse-tool" + (options.collapseByNameClickSelector ? ", " +  options.collapseByNameClickSelector : "")).click(function(){
                doCollapse($(this).parent());
            });

            function doCollapse(item) {
                var subList = item.find("> ul");
                if (item.hasClass("expanded")) {
                    item.removeClass("expanded");
                    item.addClass("do-collapse");
                    subList.slideUp(jsHelper.effectSpeed, function(){
                        item.removeClass("do-collapse");
                        toCookie();
                    });
                } else {
                    item.addClass("expanded");
                    subList.slideDown(jsHelper.effectSpeed, function(){
                        item.removeClass("do-collapse");
                        toCookie();
                    });
                }
            }

            function toCookie() {
                if (!options.stateToCookie) {
                    return;
                }
                var ids = $.cookie(options.cookieName) || "";
                catalogTree.find("li.has-collapse").each(function(){
                    var item = $(this);
                    var id = item.attr("id") + ",";
                    var exist = ids.indexOf(id) >= 0;
                    if (item.hasClass("expanded")) {
                        if (!exist) {
                            ids += id;
                        }
                    } else {
                        if (exist) {
                            ids = ids.replace(id, "");
                        }

                    }
                });
                $.cookie(options.cookieName, ids, options.cookieOptions);
            }

            function mergeWithCookie(array) {
                if (!options.stateFromCookie) {
                    return array;
                }
                if (!$.isArray(array)) {
                    return null;
                }
                var cookie = $.cookie(options.cookieName);
                if (cookie) {
                    var ids = cookie.split(",");
                    for (var i = 0; i < ids.length; i++) {
                        var id = ids[i];
                        if (id != "" && $.inArray(id, array) < 0) {
                            array.push(id);
                        }
                    }
                }
                return array;
            }
        },
        //
        // Подсказчик категорий
        /*
            options: {
                afterSelector: "",
                categoryNodeSelector: "",
                catalogItemNameSelector: "",
                parentNodeSelector: "",
                blurText: "",
                focusClass: "",
                cache: true|false,
                onSelected: function(nodeProperties) {}
            }
         */
        buildСategorySuggest: function(options) {
            options.blurText = options.blurText || "подобрать категорию...";
            options.focusClass = options.focusClass || "focus";

            $(options.afterSelector).before('<div class="category-suggest-box"><input class="category-suggest" name="categorySuggest" type="text" /></div>');
            var inputSelector = "div.category-suggest-box input[name='categorySuggest']";
            var categorySuggest = $(inputSelector);
            var isCache = false;
            var cacheNodes = [];
            var maxItems = 10;

            categorySuggest.val(options.blurText).focus(function(){
                if (!suggest.isListFocused()) {
                    categorySuggest.val("").addClass(options.focusClass);
                }
            }).blur(function(){
                if (!suggest.isListFocused()) {
                    categorySuggest.removeClass(options.focusClass).val(options.blurText);
                }
            });

            var suggest = jsHelper.buildSuggest({
                selector: inputSelector,
                suggestOptions: {
                    data: {
                        searchField: "name"
                    },
                    highlightClass: null,
                    listClass: "jquery-suggest-list category-suggest",
                    localRequest: function(query) {
                        var result = [];

                        if (isCache) {
                            for (var i = 0; i < cacheNodes.length; i++) {
                                if (!addResult(result, query, cacheNodes[i])) {
                                    break;
                                }
                            }
                        } else {
                            $(options.categoryNodeSelector).each(function(){
                                var node = $(this);
                                var keywords = "";
                                var path = [];
                                var parents = node.parents(options.parentNodeSelector);
                                for (var i = parents.length - 1; i >= 0; i--) {
                                    var parent = $(parents[i]);
                                    var parentName = parent.find(options.catalogItemNameSelector).text();
                                    keywords += parentName + " ";
                                    path.push(parentName);
                                }
                                var name = node.find(options.catalogItemNameSelector).text();
                                keywords += name;
                                path.push(name);

                                var nodeProperties = {
                                    node: node,
                                    keywords: keywords,
                                    path: path,
                                    name: ""
                                };

                                addResult(result, query, nodeProperties);

                                if (options.cache && !isCache) {
                                    cacheNodes.push(nodeProperties);
                                }
                            });
                            if (options.cache) {
                                isCache = true;
                            }
                        }

                        return result;
                    },
                    formatItem: function(itemElement, index, nodeProperties) {
                        var last = nodeProperties.path.length - 1;
                        var html = '';
                        for (var i = 0; i <= last; i++) {
                            html += '<span class="path-ci-name' + (i === last ? ' last' : '') + (i === 0 ? '">' : '">&nbsp; ') + nodeProperties.path[i] + '</span>';
                        }
                        itemElement.html(html);
                    },
                    onSelected: function(nodeProperties) {
                        if ($.isFunction(options.onSelected)) {
                            options.onSelected.call(this, nodeProperties);
                        }
                    }
                }
            });

            function addResult(result, query, nodeProperties) {
                if (result.length === maxItems) {
                    return false;
                }

                var words = query.split(/\s+/);
                var test = 0;
                for (var i = 0; i < words.length; i++) {
                    if (new RegExp("^" + words[i] + "|[\\s+\\(\\)«»'\",\\-]" + words[i], "i").test(nodeProperties.keywords)) {
                        test++;
                    }
                }
                if (test === words.length) {
                    nodeProperties.name = query;
                    result.push(nodeProperties);
                }

                return true;
            }

            return suggest;
        },
        //
        // Подсказчик поиска
        /*
            options: {
                inputSelector: "",
                searchUrl: "",
                formSelector: "",
                contextUrl: ""
            }
         */
        buildSearchSuggest: function(options) {
            jsHelper.buildSuggest({
                selector: options.inputSelector,
                suggestOptions: {
                    //hoverClass: "jquery-suggest-list-item-hover as-link",
                    request: {
                        url: options.searchUrl,
                        queryParamName: "text"
                    },
                    data: {
                        searchField: "name"
                    },
                    selectSelected: true,
                    formatItem: function(itemElement, index, itemData) {
                        itemElement.wrapInner('<span class="as-link" />');
                    },
                    onUserSelect: function(itemData){
                        window.location = options.contextUrl.substring(0, options.contextUrl.lastIndexOf("/")) + itemData.url;
                    },
                    onInputEnter: function(isUserSelected){
                        if (!isUserSelected) {
                            $(options.formSelector).submit();
                        }
                    }
                }
            });
        },
        //
        // Fix ширины футера при имеющейся горизонтальной прокрутке (сравнение предложений - длинный горизонтальный список, ...)
        // TODO все-таки решить с помощью HTML/CSS
        footerWidthFix: function() {
            if ($.browser.msie) {
                return;
            }

            var footer = $("#footer");
            var footerContent = $("#footer .footer-content");
            var wrapper = $("#wrapper");

            jsHelper.window.bind("resize scroll", function (){
                check();
            });

            check();

            function check() {
                footer.css("width", "auto");
                footerContent.css("width", wrapper.width() + "px");
                var docWidth = jsHelper.document.width();
                if (footer.width() !== docWidth) {
                    footer.css("width", docWidth + "px");
                }
            }
        },
        //
        correctSearchBarWidth: function() {
            if ($.browser.msie) {
                return;
            }

            var snapCol = $("table.catalog-item-list > tbody > tr:first-child > td::nth-child(3)");
            var submit = null;
            var offsetSnap = null;
            var offsetSubmit = null;
            var searchbar = null;

            if (snapCol.length === 1) {
                submit = $("#searchbar .submit-box");
                searchbar = $("#searchbar");
                jsHelper.window.resize(function(){
                    check();
                });
                check();
            }

            function check() {
                offsetSnap = snapCol.offset();
                offsetSubmit = submit.offset();
                if (offsetSnap.left !== offsetSubmit.left) {
                    searchbar.css("width", offsetSnap.left - 230 + "px"); // 230 = 300 - 70
                }
            }
        }
    }
};
//
var jsHelper = storefront.helper;
//
jsHelper.document.ready(function(){
    $.tools.tooltip.conf.delay = jsHelper.tooltipShowDelay;
    $.tools.tooltip.conf.fadeInSpeed = jsHelper.effectSpeed;
    $.tools.tooltip.conf.fadeOutSpeed = jsHelper.effectSpeed;

    jsHelper.buildSystemTooltip();
    jsHelper.buildGlobalMessage();

    $.ajaxSetup({
        cache: false,
        error: function(){
            jsHelper.globalMessage.showServerErrorMessage();
        }
    });

    $.datepicker.setDefaults({
        prevText: "",
        nextText: "",
        closeText: "",
        currentText: "",
        weekHeader: "",
        dayNames:["","","","","","",""],
        dayNamesShort:["","","","","","",""],
        dayNamesMin:["Вс","Пн","Вт","Ср","Чт","Пт","Сб"],
        monthNamesShort: ["","","","","","","","","","","",""],
        monthNames: ["Январь","Февраль","Март","Апрель","Май","Июнь","Июль","Август","Сентябрь","Октябрь","Ноябрь","Декабрь"],
        showOtherMonths: true,
        selectOtherMonths: true,
        duration: 0,
        firstDay: 1,
        dateFormat: jsHelper.dateFormat
    });
    
    $.fn.suggest.defaults.editClass = "";
    $.fn.suggest.defaults.tools = {
        toolsClass: "jquery-suggest-list-tools",
        actionClass: "action",
        enabledClass: "enabled",
        disabledClass: "disabled",
        separatorClass: "separator",
        paging: {
            clazz: "jquery-suggest-list-tools-paging",
            htmlPrev: '<span class="prev"></span>',
            htmlNext: '<span class="next"></span>'
        },
        close: {
            clazz: "jquery-suggest-list-tools-close",
            html: '<span class="close"></span>'
        }
    };

    jsHelper.footerWidthFix();

    jsHelper.correctSearchBarWidth();
});
