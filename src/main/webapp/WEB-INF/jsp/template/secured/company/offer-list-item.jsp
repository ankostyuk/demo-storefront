<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="pfn" uri="http://www.nullpointer.ru/pfn/tags" %>

<tiles:useAttribute name="_offer" />
<tiles:useAttribute name="_categoryPath" ignore="true" />
<tiles:useAttribute name="_defaultCurrency" />
<tiles:useAttribute name="_unit" />
<tiles:useAttribute name="_catalog" ignore="true" />
<tiles:useAttribute name="_sorting" ignore="true" />
<tiles:useAttribute name="_showing" ignore="true" />
<tiles:useAttribute name="_redirect" />

<div class="match-item">
    <c:if test="${not empty _offer.image}">
        <div class="match-image">
            <a title="Редактировать" href="<spring:url value="/secured/company/offer/edit/{id}"><spring:param name="id" value="${_offer.id}" /><spring:param name="redirect" value="${_redirect}" /></spring:url>"><img alt="" src="<cdn:url container="offer_mini" key="${_offer.image}" />" /></a>
        </div>
    </c:if>
    <div class="offer-edit-info">
        <c:choose>
            <c:when test="${_sorting == 'DATE_CREATED_ASCENDING' || _sorting == 'DATE_CREATED_DESCENDING'}">
                Добавлено <fmt:formatDate pattern="dd.MM.yyyy" value="${_offer.createDate}" />
            </c:when>
            <c:otherwise>
                Изменено <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${_offer.editDate}" />
            </c:otherwise>
        </c:choose>
        <ul class="h-list actions">
            <c:if test="${_offer.status != 'REJECTED'}">
                <li><a class="act" href="<spring:url value="/secured/company/offer/copy/{id}"><spring:param name="id" value="${_offer.id}" /><spring:param name="redirect" value="${_redirect}" /></spring:url>">Копировать</a></li>
            </c:if>
            <li><a class="dce" href="<spring:url value="/secured/company/offer/delete/{id}"><spring:param name="id" value="${_offer.id}" /><spring:param name="redirect" value="${_redirect}" /></spring:url>">Удалить</a></li>
        </ul>
    </div>
    <div class="match-info">
        <c:if test="${not empty _categoryPath}">
            <div class="match-path">
                <tiles:insertDefinition name="secured-company-catalog-item-path">
                    <tiles:putAttribute name="_path" value="${_categoryPath}" />
                    <tiles:putAttribute name="_catalog" value="${_catalog}" />
                    <tiles:putAttribute name="_sorting" value="${_sorting}" />
                    <tiles:putAttribute name="_showing" value="${_showing}" />
                    <tiles:putAttribute name="_lastLink" value="true" />
                </tiles:insertDefinition>
            </div>
        </c:if>
        <a class="name" title="Редактировать" href="<spring:url value="/secured/company/offer/edit/{id}"><spring:param name="id" value="${_offer.id}" /><spring:param name="redirect" value="${_redirect}" /></spring:url>"><c:out value="${_offer.name}" /></a>

        <c:if test="${_offer.active}">
            <p class="actual-info actual">Актуально до <fmt:formatDate pattern="dd.MM.yyyy" value="${_offer.actualDate}" /></p>
        </c:if>
        <c:if test="${not _offer.active}">
            <p class="actual-info no-actual">Неактуально</p>
        </c:if>

        <p class="price-info">
            <%-- Сначала цена поставщика, потом приведенная --%>
            <%-- argumentSeparator для того чтобы spring:message не считал запятую в значении разделителем --%>
            <span class="price">
                <spring:message code="currency.${_offer.currency}.format" arguments="${pfn:formatPrice(_offer.price, false)}" argumentSeparator="|" />
            </span>
            за
            <%-- проверка на неравенство единице(!= 1) не работает, приходится изголяться --%>
            <c:set var="showRatio" value="${_offer.ratio > 1 || _offer.ratio < 1}" />
            <c:if test="${showRatio}"><fmt:formatNumber value="${_offer.ratio}" maxFractionDigits="2" />&nbsp;</c:if>
            <abbr title="<c:out value="${_unit.name}" />">${fn2:htmlformula(_unit.abbreviation)}</abbr>
            <c:if test="${_offer.currency != _defaultCurrency || showRatio}">
                <span class="unit-price">
                    (<spring:message code="currency.default.format" arguments="${pfn:formatPrice(_offer.unitPrice, true)}" argumentSeparator="|" />
                    за ${fn2:htmlformula(_unit.abbreviation)})
                </span>
            </c:if>
            <c:if test="${_offer.available}">есть в наличии, </c:if>
            <c:if test="${not _offer.available}">нет в наличии, </c:if>
            <c:if test="${_offer.delivery}">c доставкой</c:if>
            <c:if test="${not _offer.delivery}">без доставки</c:if>
        </p>
        <c:if test="${not empty _offer.description}">
            <p class="description"><c:out value="${_offer.description}" /></p>
        </c:if>
        <c:if test="${not empty _offer.paramDescription && fn:length(_offer.description) < 100}">
            <p class="param-description">Характеристики: ${fn2:htmlformula(_offer.paramDescription)}</p>
        </c:if>
        <div class="moderation-info">
            <c:choose>
                <c:when test="${_offer.status == 'APPROVED'}">
                    <span class="approved">Проверено
                        <c:if test="${not empty _offer.moderationEndDate}">
                            <fmt:formatDate pattern="dd.MM.yyyy" value="${_offer.moderationEndDate}" />
                        </c:if>
                    </span>
                </c:when>
                <c:when test="${_offer.status == 'PENDING'}">
                    <span class="pending">Ожидает проверки</span>
                </c:when>
                <c:otherwise>
                    <div class="rejected">Отклонено
                        <c:if test="${not empty _offer.moderationEndDate}"> 
                            <fmt:formatDate pattern="dd.MM.yyyy" value="${_offer.moderationEndDate}" />
                        </c:if>
                        <ul class="rejection-list">
                            <c:forEach var="reason" items="${_offer.rejectionList}" varStatus="st">
                                <li>
                                    <c:choose>
                                        <c:when test="${reason == 'CATEGORY'}">
                                            <a title="Переместить предложение в другую категорию" href="<spring:url value="/secured/company/offer/move/{id}" htmlEscape="true">
                                                   <spring:param name="id" value="${_offer.id}" />
                                                   <spring:param name="cat" value="${_catalog.alias}" />
                                                   <spring:param name="redirect" value="${_redirect}" />
                                               </spring:url>"><spring:message code="ui.offer.rejection.${reason}" /></a>
                                        </c:when>
                                        <c:when test="${reason == 'OTHER'}">
                                            <a title="Ознакомиться с правилами размещения информационных материалов" href="<spring:url value="/agreement/rules" htmlEscape="true" />">
                                                <spring:message code="ui.offer.rejection.${reason}" />
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message code="ui.offer.rejection.${reason}" />
                                        </c:otherwise>
                                    </c:choose>
                                </li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>
