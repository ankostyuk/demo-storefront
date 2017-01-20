<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="_catalogItem" />
<tiles:useAttribute name="actionUrl" />
<tiles:useAttribute name="settings" />
<tiles:useAttribute name="sorting" />
<tiles:useAttribute name="regionAware" />
<tiles:useAttribute name="matchFilter" />
<tiles:useAttribute name="priceInterval" />
<tiles:useAttribute name="unit" ignore="true" />
<tiles:useAttribute name="filterUrlParams" ignore="true" />
<tiles:useAttribute name="brandList" ignore="true" />
<tiles:useAttribute name="paramModel" ignore="true" />
<tiles:useAttribute name="searchText" ignore="true" />
<tiles:useAttribute name="searchCatalogItemId" ignore="true" />

<form id="match-filter-form" action="<spring:url value="${actionUrl}" />" method="get">
    <div class="match-filter">
        <c:if test="${not empty filterUrlParams}">
            <div class="reset">
                <a class="dce" href="<spring:url value="${actionUrl}">
                       <spring:param name="sort" value="${sorting.alias}" />
                   </spring:url>">Сбросить параметры</a>
            </div>
        </c:if>
        <div class="price">
            <h3>Цена</h3>
            <div id="option-price" class="options number">
                <%-- для ползунков ${priceInterval.min} - ${priceInterval.max} --%>
                <input id="price-min" name="price-min" value="<fmt:formatNumber value="${matchFilter.price.min}" maxFractionDigits="2" groupingUsed="false" />" type="text" />
                &mdash;
                <input id="price-max" name="price-max" value="<fmt:formatNumber value="${matchFilter.price.max}" maxFractionDigits="2" groupingUsed="false" />" type="text" />
                <span><spring:message code="currency.default.abbr" />
                <c:if test="${not empty unit}">
                    за <abbr title="<c:out value="${unit.name}" />">${fn2:htmlformula(unit.abbreviation)}</abbr></span>
                </c:if>
            </div>
        </div>
        <c:set var="brandListSize" value="${fn:length(brandList)}"/>
        <c:if test="${brandListSize > 0}">
            <div class="brands">
                <h3>Бренды</h3>
                <div class="options">
                    <%-- TODO Условие перехода на двухколоночный вариант --%>
                    <c:if test="${brandListSize <= 5}"> <%--Одноколоночный вариант--%>
                        <ul>
                            <c:forEach var="brand" items="${brandList}">
                                <li>
                                    <input class="checkbox" id="brand${brand.id}" name="brand" value="${brand.id}" type="checkbox" <c:if test="${fn2:contains(matchFilter.brandIdSet, brand.id)}">checked="checked"</c:if> />
                                    <label for="brand${brand.id}"><c:out value="${brand.name}" /></label>
                                </li>
                            </c:forEach>
                        </ul>
                    </c:if>
                    <c:if test="${brandListSize > 5}"> <%--Двухколоночный вариант --%>
                        <table>
                            <tbody>
                                <tr>
                                    <c:forEach var="brand" items="${brandList}" varStatus="status">
                                        <c:set var="rest" value="${status.count % 2}" />
                                        <td <c:if test="${rest == 0}">class="second"</c:if> >
                                            <input class="checkbox" id="brand${brand.id}" name="brand" value="${brand.id}" type="checkbox" <c:if test="${fn2:contains(matchFilter.brandIdSet, brand.id)}">checked="checked"</c:if> />
                                            <label for="brand${brand.id}"><c:out value="${brand.name}" /></label>
                                        </td>
                                        <c:if test="${!status.last && rest == 0}"></tr><tr></c:if>
                                        <c:if test="${status.last && rest != 0}"><td></td></c:if>
                                    </c:forEach>
                                </tr>
                            </tbody>
                        </table>
                    </c:if>
                </div>
            </div>
        </c:if>
        <c:if test="${not empty paramModel}">
            <div class="params">
                <ul class="param-group">
                    <c:forEach var="p" items="${paramModel.paramList}">
                        <%-- символ &nbsp; между <span class="name"/> и <span class="description"/> для IE7, который margin считает за пространоство элемента --%>
                        <%-- символ &nbsp; в <span class="description"/> для отображения background-image - "знака вопроса" --%>
                        <li class="param"><h3><span class="name"><c:out value="${p.name}" /></span><c:if test="${not empty p.description}">&nbsp;<a id="param-tooltip-p${p.id}" class="param-description" href="<spring:url value='/category/{id}/term'><spring:param name='id' value='${_catalogItem.id}' /></spring:url>#${fn2:xmlid(p.id)}">&nbsp;</a></c:if></h3>
                            <c:choose>
                                <c:when test="${p.type == 'BOOLEAN'}">
                                    <c:set var="booleanValue" value="${matchFilter.paramValueMap[p.id]}" />
                                    <ul class="options boolean">
                                        <li>
                                            <input class="radio" id="p${p.id}-true" name="p${p.id}" value="1" type="radio" <c:if test="${booleanValue == true}">checked="checked"</c:if> />
                                            <label for="p${p.id}-true"><c:out value="${p.trueName}" /></label>
                                        </li>
                                        <li>
                                            <input class="radio" id="p${p.id}-false" name="p${p.id}" value="0" type="radio" <c:if test="${booleanValue == false}">checked="checked"</c:if> />
                                            <label for="p${p.id}-false"><c:out value="${p.falseName}" /></label>
                                        </li>
                                        <li>
                                            <input class="radio" id="p${p.id}-null" name="p${p.id}" value="" type="radio" <c:if test="${empty booleanValue}">checked="checked"</c:if> />
                                            <label for="p${p.id}-null" class="null">неважно</label>
                                        </li>
                                    </ul>
                                </c:when>
                                <c:when test="${p.type == 'NUMBER'}">
                                    <c:set var="numberValue" value="${matchFilter.paramValueMap[p.id]}" />
                                    <div id="option-p${p.id}" class="options number">
                                        <input id="p${p.id}-min" name="p${p.id}-min" value="<fmt:formatNumber value="${numberValue.min}" maxFractionDigits="${p.precision}" groupingUsed="false" />" type="text" />
                                        &mdash;
                                        <input id="p${p.id}-max" name="p${p.id}-max" value="<fmt:formatNumber value="${numberValue.max}" maxFractionDigits="${p.precision}" groupingUsed="false" />" type="text" />
                                        <c:set var="paramUnit" value="${paramModel.unitMap[p.unitId]}" />
                                        <abbr title="<c:out value="${paramUnit.name}" />">${fn2:htmlformula(paramUnit.abbreviation)}</abbr>
                                    </div>
                                </c:when>
                                <c:when test="${p.type == 'SELECT'}">
                                    <c:set var="selectValue" value="${matchFilter.paramValueMap[p.id]}" />
                                    <c:set var="selectValue" value="${matchFilter.paramValueMap[p.id]}" />
                                    <ul class="options">
                                        <c:forEach var="option" items="${paramModel.selectOptionMap[p.id]}">
                                            <li>
                                                <input class="checkbox" id="p${p.id}-${option.id}" name="p${p.id}" value="${option.id}" type="checkbox" <c:if test="${fn2:contains(selectValue, option.id)}">checked="checked"</c:if> />
                                                <label for="p${p.id}-${option.id}"><c:out value="${option.name}" /></label>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                            </c:choose>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <div class="submit">
            <input type="submit" value="Подобрать" />
            <input type="hidden" name="sort" value="${sorting.alias}" />
            <c:if test="${not empty searchText}">
                <input type="hidden" name="text" value="${searchText}" />
            </c:if>
            <c:if test="${not empty searchCatalogItemId}">
                <input type="hidden" name="item" value="${searchCatalogItemId}" />
            </c:if>
        </div>
    </div>
</form>

<div id="match-filter-tooltip"><table cellpadding="0" cellspacing="0"><tbody><tr>
    <td class="left-box"><div></div></td>
    <td class="content-box"><span class="no-match">Ничего не подобрано</span><span class="match" style="display: none;">Подобрано <a href="#">предложений&nbsp;&mdash;&nbsp;<span class="match-count">521</span></a></span></td>
    <td class="right-box"><div></div></td>
</tr></tbody></table></div><!-- filter-tooltip -->

<script type="text/javascript">
// <![CDATA[
    jsHelper.document.ready(function(){
<%-- NetBeans компилирует JavaScript не учитывая JSP-раметку, поэтому в блоке якобы ошибки в JavaScript --%>
        <%-- map options number --%>
        var optionRangeMap = {
            price: {
                rangeMin: ${priceInterval.min},
                rangeMax: ${priceInterval.max}
            }
<c:if test="${not empty paramModel}">
    <c:forEach var="p" items="${paramModel.paramList}">
        <c:if test="${p.type == 'NUMBER'}">
            ,p${p.id}: {
                rangeMin: ${p.minValue},
                rangeMax: ${p.maxValue}
            }
        </c:if>
    </c:forEach>
</c:if>
        };

<%-- Отобразить только когда есть параметры, иначе гугл индексирует ссылки /category/{id}/term --%>
 <c:if test="${not empty paramModel}">
        <%-- Подсказка по параметрам --%>
        jsHelper.buildParamTooltip({
            selector: "div.match-filter ul.param-group li.param .param-description",
            paramIdPrefix: "param-tooltip-p",
            categoryName: "<c:out value="${_catalogItem.name}" />",
            categoryTermUrl: "<spring:url value='/category/{id}/term'><spring:param name='id' value='${_catalogItem.id}' /></spring:url>",
            right: false
        });
        
        <%--
        // Сворачивание/разворачивание параметров
        // TODO Реализовать "подварачивание" параметров, не вмещающихся в размер окна,
        // но реализовать только вместе с сохранением свернутых/развернутых параметров
        // при работе фильтра, перезагрузке, редиректах, ...
        // Возможно, и само сворачивание/разворачивание параметров надо включить только после реализации сохранения.
        --%>
        jsHelper.buildCollapsedContent({
            container: "div.match-filter ul.param-group li.param",
            collapsedClass: "collapsed",
            addClass: "js",
            click: ".name",
            collapsable: ".options"
        });
</c:if>

        <%-- "Ползунки" --%>
        var slidersAPI = jsHelper.buildSlider({
            selector: "div.match-filter div.price div.options.number",
            cssClass: "slider-large",
            optionRangeMap: optionRangeMap
        });
        jsHelper.buildSlider({
            selector: "div.match-filter div.params div.options.number",
            cssClass: "slider-small",
            optionRangeMap: optionRangeMap
        });

        <%-- Подсказка фильтра --%>
        var matchFilterTooltip = $("#match-filter-tooltip").appendTo("body");

        var matchFilterFocusedEl = null;

        var ajaxMatchFilter = $.manageAjax.create("ajaxMatchFilter", {
            queue: true,
            maxRequests: 1,
            abortOld: true,
            cacheResponse: false,
            preventDoubbleRequests: true
        });

        var matchFilterForm = $("#match-filter-form");

        matchFilterForm.ajaxForm({
            beforeSubmit: function(arr, $form, options) {
                $form[0].submit();
                return false;
            }
        });

        matchFilterTooltip.find(".content-box .match a").click(function(e) {
            e.preventDefault();
            matchFilterForm[0].submit();
        });

        matchFilterForm.find(":text,:checkbox,:radio, div.ui-customslider").listenerchange({
            getValue: function(el) {
                if (el.is(".ui-customslider")) {
                    return el.data("values");
                }
            },
            equalValue: function(el, prev, current) {
                if (el.is(".ui-customslider")) {
                    return (prev[0] == current[0] && prev[1] == current[1]);
                }
            },
            onChange: function(changed) {
                abortTooltip(false);
                if (matchFilterFocusedEl) {
                    for (var i = 0; i < changed.length; i++) {
                        var el = changed[i];
                        if (el[0] == matchFilterFocusedEl[0]) {
                            if (el.is(":text")) {
                                inputToSlider(el);
                            }
                            showWaitTooltip();
                            ajaxMatchFilter.add({
                                url: "<spring:url value="${actionUrl}/prefilter" />",
                                dataType: "json",
                                data: matchFilterForm.formSerialize(),
                                complete: function() {
                                },
                                error: function() {
                                    hideTooltip();
                                },
                                success: function(data, textStatus, XMLHttpRequest) {
                                    if (textStatus === "success") {
                                        showTooltip(data);
                                    } else {
                                        hideTooltip();
                                    }
                                }
                            });
                        }
                    }
                } else {
                    hideTooltip();
                }
            }
        }).focus(function(e){
            onListenedFocus($(this));
        }).focusin(function(e){
            onListenedFocus($(this));
        });

        jsHelper.buildEscapeEvent(matchFilterTooltip, hideTooltip);

        function posTooltip() {
            if (matchFilterFocusedEl) {
                matchFilterTooltip.css({
                    left: matchFilterForm.offset().left - matchFilterTooltip.outerWidth() + 20 + "px",
                    top: matchFilterFocusedEl.offset().top - 23 + matchFilterFocusedEl.outerHeight()/2 - (matchFilterFocusedEl.is(".ui-customslider") ? 8 : 0) + "px"
                });
            }
        };

        function showWaitTooltip() {
            matchFilterTooltip.addClass("wait");
        }

        function showTooltip(matchCount) {
            matchFilterTooltip.removeClass("wait");
            if (matchCount) {
                matchFilterTooltip.find(".content-box .no-match").hide();
                matchFilterTooltip.find(".content-box .match .match-count").text(matchCount);
                matchFilterTooltip.find(".content-box .match").show();
            } else {
                matchFilterTooltip.find(".content-box .match").hide();
                matchFilterTooltip.find(".content-box .no-match").show();
            }
            matchFilterTooltip.show();
            posTooltip();
        }

        function hideTooltip() {
            matchFilterTooltip.hide();
        }

        function abortTooltip(hide) {
            ajaxMatchFilter.abort();
            if (hide) {
                hideTooltip();
            }
        }

        function inputToSlider(input) {
            var slider = input.data("slider");
            if (slider) {
                var index = input.data("sliderRangeIndex");
                slidersAPI.setSliderValues(slider, (index == 0 ? input.val() : null), (index == 1 ? input.val() : null));
            }
        }

        function onListenedFocus(el) {
            matchFilterFocusedEl = el;
        }
        
        jsHelper.window.resize(function() {
            posTooltip();
        });
    });
// ]]>
</script>
