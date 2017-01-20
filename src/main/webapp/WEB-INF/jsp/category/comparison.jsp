<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="usf" uri="http://www.nullpointer.ru/usf/tags" %>
<%@ taglib prefix="pfn" uri="http://www.nullpointer.ru/pfn/tags" %>

<div id="content-no-sidebar">
    <tiles:insertDefinition name="catalog-item-path">
        <tiles:putAttribute name="catalogItemPath" value="${path}" />
        <tiles:putAttribute name="_noLastLink" value="${false}" />
    </tiles:insertDefinition>
    <h2>Сравнение предложений</h2>
    <c:if test="${fn:length(matchList) == 0}">
        <p class="info">
            Список сравнения пуст
        </p>
    </c:if>
    <c:if test="${fn:length(matchList) > 0}">
        <%-- TODO протащить URL с параметром для фильтра "везде" --%>
        <c:set var="baseUrl" value="/category/${category.id}/comparison?ids=${usf:getComparisonUrlParam(userSession, category.id)}" />
        <c:if test="${fn:length(matchList) == 1}">
            <%-- Принудительно переустановить filterName в Filter.all при наличии только одного элемента сравнений --%>
            <c:set var="filterName" value="${'all'}" />
        </c:if>
        <c:set var="redirect" value="${baseUrl}&filter=${filterName}" />
        <div id="comparison-box">
            <table id="comparison-base" class="comparison-table" cellpadding="0" cellspacing="0">
                <thead>
                    <tr class="top">
                        <th rowspan="3" class="first-col filter"><div class="cell"><div>
                                <ul id="comparison-filter" class="v-list inside items">
                                    <%-- Значения параметра filter должны совпадать с наименованиями Filter.xxx --%>
                                    <%-- Суффикс id должны совпадать с наименованиями Filter.xxx --%>
                                    <c:choose>
                                        <%--<spring:url value='${baseUrl}'><spring:param name='filter' value='different' /></spring:url>--%>
                                        <c:when test="${filterName == 'different'}">
                                            <li><span><a id="filter-option-all" class="act filter-option" rel="nofollow" href="<spring:url value='${baseUrl}' htmlEscape='true' />">Все параметры</a></span></li>
                                            <c:if test="${fn:length(matchList) > 1}">
                                                <li><span class="current"><a id="filter-option-different" class="act filter-option"><span class="one">Разли</span><span class="two">чаю</span><span class="three">щиеся</span></a></span></li>
                                            </c:if>
                                            <li><span class="disabled"><a id="filter-option-selected" class="act filter-option" style="display: none;">Отмеченные</a></span></li>
                                        </c:when>
                                        <c:otherwise>
                                            <li><span class="current"><a id="filter-option-all" class="act filter-option">Все параметры</a></span></li>
                                            <c:if test="${fn:length(matchList) > 1}">
                                                <li><span><a id="filter-option-different" class="act filter-option" rel="nofollow" href="<spring:url value='${baseUrl}' htmlEscape='true'><spring:param name='filter' value='different' /></spring:url>"><span class="one">Разли</span><span class="two">чаю</span><span class="three">щиеся</span></a></span></li>
                                            </c:if>
                                            <li><span class="disabled"><a id="filter-option-selected" class="act filter-option" style="display: none;">Отмеченные</a></span></li>
                                        </c:otherwise>
                                    </c:choose>
                                </ul>
                        </div></div></th>
                        <th class="scroll-effect fade-v"></th>
                        <c:forEach var="match" items="${matchList}">
                            <c:set var="matchResult" value="${matchResultMap[match]}" />
                            <th class="data compare-item match-image"><div class="cell"><div>
                                <c:choose>
                                    <c:when test="${matchResult.type == 'OFFER'}">
                                        <c:if test="${not empty matchResult.offer.image}">
                                            <a class="hover-src-o${matchResult.offer.id}" href="<spring:url value='/offer/{id}'>
                                                   <spring:param name='id' value='${matchResult.offer.id}' />
                                               </spring:url>"><img alt="" src="<cdn:url container="offer_mini" key="${matchResult.offer.image}" />" /></a>
                                        </c:if>
                                    </c:when>
                                    <c:when test="${matchResult.type == 'MODEL'}">
                                        <c:if test="${not empty matchResult.model.image}">
                                            <a class="hover-src-m${matchResult.model.id}" href="<spring:url value='/model/{id}'>
                                                   <spring:param name='id' value='${matchResult.model.id}' />
                                               </spring:url>"><img alt="" src="<cdn:url container="model_mini" key="${matchResult.model.image}" />" /></a>
                                        </c:if>
                                    </c:when>
                                </c:choose>
                            </div></div></th>
                        </c:forEach>
                    </tr>
                    <tr class="top-next">
                        <th class="scroll-effect fade-v"></th>
                        <c:forEach var="match" items="${matchList}">
                            <c:set var="matchResult" value="${matchResultMap[match]}" />
                            <th class="data compare-item name"><div class="cell"><div>
                                <c:choose>
                                    <c:when test="${matchResult.type == 'OFFER'}">
                                        <a class="hover-dst-o${matchResult.offer.id}" href="<spring:url value='/offer/{id}'>
                                               <spring:param name='id' value='${matchResult.offer.id}' />
                                           </spring:url>"><c:out value="${matchResult.offer.name}" /></a>
                                    </c:when>
                                    <c:when test="${matchResult.type == 'MODEL'}">
                                        <a class="hover-dst-m${matchResult.model.id}" href="<spring:url value='/model/{id}'>
                                               <spring:param name='id' value='${matchResult.model.id}' />
                                           </spring:url>"><c:out value="${matchResult.model.name}" /></a>
                                    </c:when>
                                </c:choose>
                            </div></div></th>
                        </c:forEach>
                    </tr>
                    <tr class="top-next">
                        <th class="scroll-effect fade-v"></th>
                        <c:forEach var="match" items="${matchList}">
                            <c:set var="inComparison" value="${usf:isMatchInComparison(userSession, match)}" />
                            <c:set var="matchResult" value="${matchResultMap[match]}" />
                            <th class="data compare-item actions"><div class="cell"><div>
                                <c:choose>
                                    <c:when test="${matchResult.type == 'OFFER'}">
                                        <ul class="h-list">
                                            <li>
                                                <c:set var="deleteCSS" value="${'inComparison'}" />
                                                <%-- Если в списке сравнения - убрать из списка --%>
                                                <c:if test="${inComparison}">
                                                    <spring:url var="deleteUrl" value="/category/{categoryId}/comparison/delete" htmlEscape="true">
                                                        <spring:param name="categoryId" value="${category.id}" />
                                                        <spring:param name="id" value="o${matchResult.offer.id}" />
                                                        <spring:param name="redirect" value="/category/${category.id}/comparison?ids=${usf:getComparisonUrlParamEx(matchList, null)}&filter=${filterName}" />
                                                        <spring:param name="success" value="/category/${category.id}/comparison?ids=${usf:getComparisonUrlParamEx(matchList, match)}&filter=${filterName}" />
                                                    </spring:url>
                                                </c:if>
                                                <%-- Если нет в списке сравнения - просто убрать из URL --%>
                                                <c:if test="${not inComparison}">
                                                    <c:set var="deleteCSS" value="${'notInComparison'}" />
                                                    <spring:url var="deleteUrl" value="/category/${category.id}/comparison?ids=${usf:getComparisonUrlParamEx(matchList, match)}" />
                                                </c:if>
                                                <a class="dce comparison-item-delete ${deleteCSS}" rel="nofollow" href="${deleteUrl}" title="Удалить из сравнения">Удалить</a>
                                            </li>
                                            <li>
                                                <tiles:insertDefinition name="match-cart-info">
                                                    <tiles:putAttribute name="_id" value="o${matchResult.offer.id}" />
                                                    <tiles:putAttribute name="_cartId" value="${usf:getOfferCartId(userSession, matchResult.offer.id)}" />
                                                    <tiles:putAttribute name="_redirect" value="${redirect}" />
                                                </tiles:insertDefinition>
                                            </li>
                                        </ul>
                                    </c:when>
                                    <c:when test="${matchResult.type == 'MODEL'}">
                                        <ul class="h-list actions">
                                            <li>
                                                <c:set var="deleteCSS" value="${'inComparison'}" />
                                                <%-- Если в списке сравнения - убрать из списка --%>
                                                <c:if test="${inComparison}">
                                                    <spring:url var="deleteUrl" value="/category/{categoryId}/comparison/delete" htmlEscape="true">
                                                        <spring:param name="categoryId" value="${category.id}" />
                                                        <spring:param name="id" value="m${matchResult.model.id}" />
                                                        <spring:param name="redirect" value="/category/${category.id}/comparison?ids=${usf:getComparisonUrlParamEx(matchList, null)}&filter=${filterName}" />
                                                        <spring:param name="success" value="/category/${category.id}/comparison?ids=${usf:getComparisonUrlParamEx(matchList, match)}&filter=${filterName}" />
                                                    </spring:url>
                                                </c:if>
                                                <%-- Если нет в списке сравнения - просто убрать из URL --%>
                                                <c:if test="${not inComparison}">
                                                    <c:set var="deleteCSS" value="${'notInComparison'}" />
                                                    <spring:url var="deleteUrl" value="/category/${category.id}/comparison?ids=${usf:getComparisonUrlParamEx(matchList, match)}" />
                                                </c:if>
                                                <a class="dce comparison-item-delete ${deleteCSS}" rel="nofollow" href="${deleteUrl}" title="Удалить из сравнения">Удалить</a>
                                            </li>
                                            <li>
                                                <tiles:insertDefinition name="match-cart-info">
                                                    <tiles:putAttribute name="_id" value="m${matchResult.model.id}" />
                                                    <tiles:putAttribute name="_cartId" value="${usf:getModelCartId(userSession, matchResult.model.id)}" />
                                                    <tiles:putAttribute name="_redirect" value="${redirect}" />
                                                </tiles:insertDefinition>
                                            </li>
                                        </ul>
                                    </c:when>
                                </c:choose>
                            </div></div></th>
                        </c:forEach>
                    </tr>
                    <tr class="scroll-effect shadow-h">
                        <th class="" colspan="${fn:length(matchList) + 2}"></th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td class="first-col"><div class="cell"><div>
                            Цена за <abbr title="<c:out value="${unit.name}" />">${fn2:htmlformula(unit.abbreviation)}</abbr>
                        </div></div></td>
                        <td class="scroll-effect shadow-v"></td>
                        <c:forEach var="match" items="${matchList}">
                            <td class="data"><div class="cell"><div class="price-info">
                                <c:set var="matchResult" value="${matchResultMap[match]}" />
                                <c:choose>
                                    <c:when test="${matchResult.type == 'OFFER'}">
                                        <div>
                                            <%-- argumentSeparator для того чтобы spring:message не считал запятую в значении разделителем --%>
                                            <span class="price"><spring:message code="currency.default.format" arguments="${pfn:formatPrice(matchResult.offer.unitPrice, true)}" argumentSeparator="|" /></span>
                                        </div>
                                        <c:if test="${userSession.settings.priceType == 'EXTRA_CURRENCY'}">
                                            <div>
                                                <span class="user-price">(<spring:message code="currency.${userSession.settings.extraCurrency}.format" arguments="${pfn:formatPrice(matchResult.offer.unitPrice * userSession.settings.extraCurrencyMultiplier, true)}" argumentSeparator="|" />)</span>
                                            </div>
                                        </c:if>
                                        <c:if test="${matchResult.offer.available}">в наличии</c:if>
                                        <c:if test="${not matchResult.offer.available}">на заказ</c:if>
                                        у
                                        <a href="<spring:url value='/company/{id}'><spring:param name='id' value='${matchResult.company.id}' /></spring:url>"><c:out value="${matchResult.company.name}" /></a>,
                                        <c:out value="${matchResult.companyRegion.name}" />
                                    </c:when>
                                    <c:when test="${matchResult.type == 'MODEL'}">
                                        <c:if test="${not empty matchResult.modelInfo}">
                                            <c:if test="${matchResult.modelInfo.offerCount == 1}">
                                                <div>
                                                    <span class="price"><spring:message code="currency.default.format" arguments="${pfn:formatPrice(matchResult.modelInfo.minPrice, true)}" argumentSeparator="|" /></span>
                                                </div>
                                            </c:if>
                                            <c:if test="${matchResult.modelInfo.offerCount > 1}">
                                                <div>
                                                    <span class="price">${pfn:formatPrice(matchResult.modelInfo.minPrice, true)}</span>&nbsp;&mdash;
                                                    <span class="price"><spring:message code="currency.default.format" arguments="${pfn:formatPrice(matchResult.modelInfo.maxPrice, true)}" argumentSeparator="|" /></span>
                                                </div>
                                            </c:if>
                                            <c:if test="${userSession.settings.priceType == 'EXTRA_CURRENCY'}">
                                                <div>
                                                    <c:if test="${matchResult.modelInfo.offerCount == 1}">
                                                        <span class="user-price">(<spring:message code="currency.${userSession.settings.extraCurrency}.format" arguments="${pfn:formatPrice(matchResult.modelInfo.minPrice * userSession.settings.extraCurrencyMultiplier, true)}" argumentSeparator="|" />)</span>
                                                    </c:if>
                                                    <c:if test="${matchResult.modelInfo.offerCount > 1}">
                                                        <span class="user-price">
                                                            (${pfn:formatPrice(matchResult.modelInfo.minPrice * userSession.settings.extraCurrencyMultiplier, true)}
                                                            &mdash;
                                                            ${pfn:formatPrice(matchResult.modelInfo.maxPrice * userSession.settings.extraCurrencyMultiplier, true)}&nbsp;<spring:message code="currency.${userSession.settings.extraCurrency}.abbr" />)
                                                        </span>
                                                    </c:if>
                                                </div>
                                            </c:if>
                                            <div>
                                                <a class="offer-count" href="<spring:url value='/model/{id}/offers'><spring:param name='id' value='${matchResult.model.id}' /></spring:url>">
                                                    <c:out value="${matchResult.modelInfo.offerCount} ${fn2:ruplural(matchResult.modelInfo.offerCount, 'предложение/предложения/предложений')}" />
                                                </a>
                                            </div>
                                        </c:if>
                                        <c:if test="${empty matchResult.modelInfo}">
                                            <div>
                                                <span class="not-available">Нет в продаже<c:if test="${regionAware}"> в вашем регионе</c:if></span>
                                            </div>
                                        </c:if>
                                    </c:when>
                                </c:choose>
                            </div></div></td>
                        </c:forEach>
                    </tr>
                    <c:set var="prevGroupId" value="${null}" />
                    <c:forEach var="p" items="${paramModel.paramList}">
                        <c:set var="showParam" value="${false}" />
                        <c:set var="paramHideCSS" value="${' hide'}" />
                        <c:set var="paramFilterCSS" value="${''}" />
                        <c:forEach var="f" items="${filterParamMap[p.id]}">
                            <%-- Формировать HTML по параметру, только если параметр принадлежит фильрту Filter.all --%>
                            <c:if test="${f == 'all'}">
                                <c:set var="showParam" value="${true}" />
                            </c:if>
                            <%-- Показать параметры принадлежащие текущему фильтру --%>
                            <c:if test="${f == filterName}">
                                <c:set var="paramHideCSS" value="${''}" />
                            </c:if>
                            <%-- Сформировать CSS параметра по принадлежности к фильтрам (как название фильтра) --%>
                            <c:set var="paramFilterCSS" value="${paramFilterCSS} ${f}" />
                        </c:forEach>

                        <c:if test="${showParam}">
                            <c:if test="${p.paramGroupId != prevGroupId}">

                                <c:set var="paramGroupHideCSS" value="${' hide'}" />
                                <c:set var="paramGroupFilterCSS" value="${''}" />
                                <c:forEach var="f" items="${filterParamGroupMap[p.paramGroupId]}">
                                    <%-- Показать наименование группы, если в группе есть параметры принадлежащие текущему фильтру --%>
                                    <c:if test="${f == filterName}">
                                        <c:set var="paramGroupHideCSS" value="${''}" />
                                    </c:if>
                                    <%-- Сформировать CSS наименования группы параметров по принадлежности параметров к фильтрам (как название фильтра) --%>
                                    <c:set var="paramGroupFilterCSS" value="${paramGroupFilterCSS} ${f}" />
                                </c:forEach>

                                <c:set var="prevGroupId" value="${p.paramGroupId}" />
                                <tr class="group-${p.paramGroupId} group-name filterable${paramGroupFilterCSS}${paramGroupHideCSS}">
                                    <td class="first-col"><div class="cell"><div>
                                        <h4><c:out value="${paramModel.groupMap[p.paramGroupId].name}" /></h4>
                                    </div></div></td>
                                    <td class="scroll-effect shadow-v">&nbsp;</td>
                                    <td class="data" colspan="${fn:length(matchList)}">&nbsp;</td>
                                </tr>
                            </c:if>
                            <tr class="param-${p.id} param-values filterable${paramFilterCSS}${paramHideCSS} no-js">
                                <td class="first-col param-title"><div class="cell"><div>
                                    <span class="title"><c:out value="${p.name}" /></span><c:if test="${p.type == 'NUMBER'}"><c:set var="paramUnit" value="${paramModel.unitMap[p.unitId]}" />,&nbsp;<abbr title="<c:out value="${paramUnit.name}" />">${fn2:htmlformula(paramUnit.abbreviation)}</abbr></c:if><c:if test="${not empty p.description}">&nbsp;<a id="param-tooltip-p${p.id}" class="param-description" href="<spring:url value='/category/{id}/term'><spring:param name='id' value='${catalogItem.id}' /></spring:url>#${fn2:xmlid(p.id)}">&nbsp;</a>   </c:if>
                                </div></div></td>
                                <td class="scroll-effect shadow-v"></td>
                                <c:forEach var="match" items="${matchList}">
                                    <td class="data"><div class="cell"><div>
                                        <c:set var="matchResult" value="${matchResultMap[match]}" />
                                        <c:choose>
                                            <c:when test="${matchResult.type == 'OFFER'}">
                                                <c:if test="${matchResult.offer.parametrized || matchResult.offer.modelLinked}">
                                                    <c:set var="paramValue" value="${matchParamValueMap[match][p.id]}" />
                                                </c:if>
                                            </c:when>
                                            <c:when test="${matchResult.type == 'MODEL'}">
                                                <c:set var="paramValue" value="${matchParamValueMap[match][p.id]}" />
                                            </c:when>
                                        </c:choose>
                                        <c:choose>
                                            <c:when test="${p.type == 'BOOLEAN'}">
                                                <c:if test="${!empty paramValue && paramValue == true}"><img src="<cdn:url container="css" key="${'/i/param-value-true.png'}" />" alt="<c:out value="${p.trueName}" />" /></c:if>
                                                <c:if test="${!empty paramValue && paramValue == false}"><img src="<cdn:url container="css" key="${'/i/param-value-false.png'}" />" alt="<c:out value="${p.falseName}" />" /></c:if>
                                            </c:when>
                                            <c:when test="${p.type == 'NUMBER'}">
                                                <fmt:formatNumber value="${paramValue}" minFractionDigits="${p.precision}" maxFractionDigits="${p.precision}" />
                                            </c:when>
                                            <c:when test="${p.type == 'SELECT'}">
                                                <c:forEach var="option" items="${paramModel.selectOptionMap[p.id]}">
                                                    <c:if test="${paramValue == option.id}">
                                                        <c:out value="${option.name}" />
                                                    </c:if>
                                                </c:forEach>
                                            </c:when>
                                        </c:choose>
                                    </div></div></td>
                                </c:forEach>
                            </tr>
                        </c:if>
                    </c:forEach>
                </tbody>
            </table>
        </div>
        <tiles:insertDefinition name="match-tools">
            <tiles:putAttribute name="_redirect" value="${redirect}" />
        </tiles:insertDefinition>
    </c:if>

    <c:if test="${fn:length(matchList) > 0}">
    <script type="text/javascript">
        // <![CDATA[
        var windowPage = jsHelper.window;

        windowPage.load(function(){ // именно <code>$(window).load()</code> - для полной загрузки страницы с изображениями.

            $("#filter-option-selected").show();

            <%-- Прокрутка --%>
            var tableBase = $("#comparison-base");

            tableBase.find("tr").each(function(){
                var row = $(this);
                if (!row.hasClass("scroll-effect")) {
                    var separateRow = true;
                    if (row.hasClass("top")) {
                        var col = row.find("th.first-col");
                        if (col.outerHeight() === row.outerHeight()) {
                            separateRow = false;

                            var col = $(this);
                            var height = row.outerHeight() + 5; <%-- 5px - запас :) --%>
                            tableBase.find("thead tr.top-next").each(function(){
                                height -= $(this).outerHeight();
                            });
                            col.css({
                                height: height + "px",
                                "min-height": height + "px",
                                "max-height": height + "px"
                            });
                        }
                    }
                    if (separateRow) {
                        var height = row.outerHeight() + 1; <%-- 1px - запас :) --%>
                        var css = {
                            height: height + "px",
                            "min-height": height + "px",
                            "max-height": height + "px"
                        };
                        row.css(css).children().css(css);
                    }
                }
            });

            // left col
            var leftCol = tableBase.clone(true).removeAttr("id").addClass("fixable").css({
                left: "auto",
                top: "auto",
                "z-index": "101",
                position: "absolute"
            });
            leftCol.find("th.data, td.data").remove();

            // top row
            var topRow = tableBase.clone(true).removeAttr("id").addClass("fixable").css({
                left: "auto",
                top: "auto",
                "z-index": "102",
                position: "absolute"
            });
            topRow.find("tbody").remove();

            // leftTop
            var leftTop = topRow.clone(true).css({
                left: "auto",
                top: "auto",
                "z-index": "103",
                position: "absolute"
            });
            leftTop.find("th.data").remove();

            leftCol.find("thead tr.scroll-effect").remove();
            leftTop.find("thead tr.scroll-effect").remove();
            tableBase.find("thead tr.scroll-effect").remove();

            leftCol.find("th.first-col div.cell").empty();
            topRow.find("th.first-col div.cell").empty();
            tableBase.find("th.first-col div.cell, td.first-col div.cell, thead th.data div.cell").empty();

            tableBase.before(leftCol);
            tableBase.before(topRow);
            tableBase.before(leftTop);

            windowPage.scroll(function (){
                checkScroll();
            }).resize(function (){ // не всегда генерится!
                checkScroll(true);
            });

            var absoluteAllState = function() {
                this.css({
                    left: "auto",
                    top: "auto",
                    position: "absolute"
                });
                tableBase.before(this);
            };

            var fixedLeftState = function(offset, position, scroll) {
                this.css({
                    left: "0",
                    top: offset.top - scroll.top + "px",
                    position: "fixed"
                }).appendTo("body");
            };

            var absoluteLeftState = function(offset, position, scroll) {
                this.css({
                    left: "auto",
                    top: position.top - offset.top + scroll.top + "px",
                    position: "absolute"
                });
                tableBase.before(this);
            };

            var fixedTopState = function(offset, position, scroll) {
                this.css({
                    left: offset.left - scroll.left + "px",
                    top: "0",
                    position: "fixed"
                }).appendTo("body");
            };

            var absoluteTopState = function(offset, position, scroll) {
                this.css({
                    left: position.left - offset.left + scroll.left + "px",
                    top: "auto",
                    position: "absolute"
                });
                tableBase.before(this);
            };

            var fixedAllState = function() {
                this.css({
                    left: "0",
                    top: "0",
                    position: "fixed"
                }).appendTo("body");
            };

            var leftColStateMatrix = [
                [absoluteAllState,  fixedLeftState,     absoluteAllState,   fixedLeftState],
                [absoluteAllState,  (absoluteAllState), absoluteAllState,   (absoluteAllState)],
                [absoluteTopState,  absoluteTopState,   (absoluteAllState), (absoluteAllState)],
                [absoluteTopState,  (absoluteAllState), (absoluteAllState), fixedLeftState]
            ];

            var topRowStateMatrix = [
                [absoluteAllState,  absoluteAllState,   absoluteLeftState,  absoluteLeftState],
                [fixedTopState,     (absoluteAllState), absoluteLeftState,  (absoluteAllState)],
                [absoluteAllState,  absoluteAllState,   (absoluteAllState), (absoluteAllState)],
                [fixedTopState,     (absoluteAllState), (absoluteAllState), fixedTopState]
            ];

            var leftTopStateMatrix = [
                [absoluteAllState,  fixedLeftState,     absoluteLeftState,  fixedAllState],
                [fixedTopState,     (absoluteAllState), absoluteLeftState,  (absoluteAllState)],
                [absoluteTopState,  absoluteTopState,   (absoluteAllState), (absoluteAllState)],
                [fixedAllState,     (absoluteAllState), (absoluteAllState), fixedAllState]
            ];

            function checkScroll(deep) {

                var offset = tableBase.offset();
                var position = tableBase.position();

                var scroll = {
                    left: windowPage.scrollLeft(),
                    top: windowPage.scrollTop()
                };

                var isScrollLeft = false;
                var isScrollTop = false;
                if (windowPage.data("scrollLeft") !== scroll.left) {
                    isScrollLeft = true;
                    windowPage.data("scrollLeft", scroll.left);
                }
                if (windowPage.data("scrollTop") !== scroll.top) {
                    isScrollTop = true;
                    windowPage.data("scrollTop", scroll.top);
                }

                if (!isScrollLeft && !isScrollTop && !deep) {
                    return;
                }

                if (scroll.left > offset.left) {
                    leftCol.addClass("shadow-v");
                    leftTop.addClass("fade-v");
                } else {
                    leftCol.removeClass("shadow-v");
                    leftTop.removeClass("fade-v");
                }
                if (scroll.top > offset.top) {
                    topRow.addClass("shadow-h");
                } else {
                    topRow.removeClass("shadow-h");
                }

                var leftStateIndex = (deep || isScrollLeft ?
                    (scroll.left > offset.left ? 1 : 0)
                    + (scroll.top > offset.top ? 2 : 0) : 0
                );
                var topStateIndex = (deep || isScrollTop ?
                    (scroll.top > offset.top ? 1 : 0)
                    + (scroll.left > offset.left ? 2 : 0) : 0
                );

                var state = leftStateIndex + 16*topStateIndex;

                if (deep || state !== tableBase.data("state")) {
                    tableBase.data("state", state);
                    leftColStateMatrix[topStateIndex][leftStateIndex].call(leftCol, offset, position, scroll);
                    topRowStateMatrix[topStateIndex][leftStateIndex].call(topRow, offset, position, scroll);
                    leftTopStateMatrix[topStateIndex][leftStateIndex].call(leftTop, offset, position, scroll);
                }
            }

            checkScroll();

            <%-- Подсказка по параметрам --%>
            jsHelper.buildParamTooltip({
                selector: "table.comparison-table td.first-col.param-title .param-description",
                paramIdPrefix: "param-tooltip-p",
                categoryName: "<c:out value="${catalogItem.name}" />",
                categoryTermUrl: "<spring:url value='/category/{id}/term'><spring:param name='id' value='${catalogItem.id}' /></spring:url>",
                right: true
            });

            <%-- Фильтр по параметрам --%>
            <%-- TODO Переработать фильтрацию. Некрасиво при redirect с других страниц (hash, url, ...). --%>
            var filterSelectedOption = $("#filter-option-selected");

            // Обработка выделения параметров
            $("table.comparison-table tbody tr").each(function(){
                var row = $(this);
                if (row.hasClass("group-name")) {
                    var groupIdClass = row.attr("class").split(" ")[0];
                    var groupSet = $("table.comparison-table tbody tr." + groupIdClass);
                    row.data("groupSet", groupSet);
                } else
                if (row.hasClass("param-values")) {
                    var paramIdClass = row.attr("class").split(" ")[0];
                    var rowSet = $("table.comparison-table tbody tr." + paramIdClass);
                    row.data("paramIdClass", paramIdClass).data("rowSet", rowSet).removeClass("no-js").hover(
                        function(){
                            $(this).data("rowSet").addClass("hover");
                        },
                        function(){
                            $(this).data("rowSet").removeClass("hover");
                        }
                    ).click(function(e){
                        var target = $(e.target);
                        if (target.is("abbr, a.param-description, span.close")) {
                            return;
                        }

                        var row = $(this);

                        row.data("rowSet").toggleClass("selected").toggleClass("select");

                        row.prevAll(".group-name.filterable").each(function(){
                            var group = $(this);
                            var selectCount = 0;
                            group.nextAll(".filterable").each(function(){
                                var filterable = $(this);
                                if (filterable.hasClass("group-name")) {
                                    return false; // Закончен обход параметров группы
                                }
                                if (filterable.hasClass("selected")) {
                                    selectCount++;
                                }
                            });
                            var groupSet = group.data("groupSet");
                            selectCount > 0 ? groupSet.addClass("selected") : groupSet.removeClass("selected");
                            return false; // Только группа параметра
                        });

                        selectedToHash();
                        checkFilterSelectedOption();
                    });
                }
            });

            function checkFilterSelectedOption() {
                if (tableBase.find("tbody tr.param-values.selected").length > 0) {
                    filterSelectedOption.parent().removeClass("disabled");
                } else {
                    filterSelectedOption.parent().addClass("disabled");
                }
            }

            // Обработка фильтрации параметров
            $("#comparison-filter .filter-option").addClass("pseudo").removeAttr("href").click(function(){
                var option = $(this);
                var parent = option.parent();
                if (!parent.is(".current:not(:has(#filter-option-selected)), .disabled")) {
                    $("#comparison-filter .current").removeClass("current");
                    parent.addClass("current");
                    var filter = option.attr("id").substr("filter-option-".length);
                    $("table.comparison-table .filterable").each(function(){
                        var filterable = $(this);
                        if (filterable.hasClass(filter)) {
                            filterable.removeClass("hide");
                        } else {
                            filterable.addClass("hide");
                        }
                    });
                    checkScroll(true);
                }
            });

            function selectedToHash() {
                var hash = "selected=";
                tableBase.find("tbody tr.param-values.selected").each(function(index){
                    var row = $(this);
                    var paramId = row.data("paramIdClass").substr("param-".length);
                    hash += (index > 0 ? "," : "") + paramId;
                });
                window.location.hash = hash;
            }

            function selectFromHash() {
                var paramIdClassArray = [];
                var hash = window.location.hash;
                if (hash && hash.indexOf("#selected=") === 0) {
                    var selected = hash.substr("#selected=".length);
                    if (selected) {
                        var paramIds = selected.split(",");
                        for (var i = 0; i < paramIds.length; i++) {
                            var paramId = parseInt(paramIds[i]);
                            if (!isNaN(paramId)) {
                                paramIdClassArray.push("param-" + paramId);
                            }
                        }
                    }
                }
                if (paramIdClassArray.length) {
                    tableBase.find("tbody tr.param-values").each(function(){
                        var row = $(this);
                        if ($.inArray(row.data("paramIdClass"), paramIdClassArray) >= 0) {
                            row.data("rowSet").addClass("selected").addClass("select");
                            row.prevAll(".group-name.filterable").each(function(){
                                var groupSet = $(this).data("groupSet");
                                groupSet.addClass("selected");
                                return false; // Только группа параметра
                            });
                        }
                    });
                    checkFilterSelectedOption();
                }
            }

            selectFromHash();

            <%-- Удаление из списка сравнения --%>
            <%-- TODO более красивое решение, без перезагрузки. Некрасиво при фильтрации. + notInComparison --%>
            jsHelper.buildPseudoLink({
                pseudoLinkSelector: "a.comparison-item-delete.inComparison",
                pseudoLinkClass: "pseudo",
                requestData: {inline: true},
                success: jsHelper.buildPseudoLinkSuccessCallback({
                    noError: function(html){
                        var href = html.attr("href");
                        if (href) {
                            window.location = href;
                        }
                    }
                })
            });
        });
        // ]]>
    </script>
    </c:if>
</div><!-- #content-->
