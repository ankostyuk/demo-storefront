<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>

<div id="container">
    <div id="content-right-sidebar" class="company-info-page">
        <h1><c:out value="${company.name}" /></h1>
        <c:if test="${not empty company.site}">
            <a href="<c:out value='${company.site}' />"><c:out value="${fn:substringAfter(company.site, '://')}" /></a>
        </c:if>
        <h3>Контактная информация</h3>
        <p>
            Регион:
            <tiles:insertDefinition name="region-path">
                <tiles:putAttribute name="_region" value="${companyRegion}" />
                <tiles:putAttribute name="_regionPath" value="${companyRegionPath}" />
            </tiles:insertDefinition>
        </p>
        <c:if test="${not empty company.address}">
            <p>Адрес: <c:out value="${company.address}" /></p>
        </c:if>
        <c:if test="${not empty company.contactPhone}">
            <p>Телефон: <c:out value="${fn2:formatPhone(company.contactPhone)}" /></p>
        </c:if>
        <c:if test="${not empty company.contactPerson}">
            <p>Контактное лицо: <c:out value="${company.contactPerson}" /></p>
        </c:if>
        <c:if test="${not empty contactList}">
            <div class="contacts">
                <c:set var="previousType" value="${null}" />
                <c:forEach var="contact" items="${contactList}">
                    <c:if test="${previousType != contact.type}">
                        <c:if test="${not empty previousType}"></ul></c:if>
                        <c:set var="previousType" value="${contact.type}" />
                        <h4 class="contact-type"><spring:message code="ui.contact.type.${contact.type}" /></h4>
                        <ul class="contact ${fn:toLowerCase(contact.type)}">
                        </c:if>
                        <li>
                            <c:out value="${contact.type == 'PHONE' ? fn2:formatPhone(contact.value) : contact.value}" /><c:if test="${not empty contact.label}">&nbsp;- <span class="label"><c:out value="${contact.label}" /></span></c:if>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>
        <c:if test="${not empty company.schedule}">
            <h3>Режим работы</h3>
            <p><c:out value="${company.schedule}" /></p>
        </c:if>
        <c:if test="${not empty company.paymentConditions && ((not empty company.paymentConditions.cash && company.paymentConditions.cash) || (not empty company.paymentConditions.cashless && company.paymentConditions.cashless) || not empty company.paymentConditions.text)}">
            <h3>Оплата</h3>
            <c:if test="${company.paymentConditions.cash}">
                <p>Наличными</p>
            </c:if>
            <c:if test="${company.paymentConditions.cashless}">
                <p>Безналичный расчет</p>
            </c:if>
            <p><c:out value="${company.paymentConditions.text}" /></p>
        </c:if>
        <c:if test="${not empty company.deliveryConditions || not empty deliveryRegionList}">
            <h3>Доставка</h3>
            <c:if test="${userSession.settings.regionAware && not userRegionDelivery}">
                <p>Нет доставки в ваш регион</p>
            </c:if>
            <c:if test="${not empty company.deliveryConditions}">
                <p>
                    <c:out value="${company.deliveryConditions}" />
                </p>
            </c:if>
            <c:if test="${not empty deliveryRegionList}">
                <h4 class="delivery-region">Регионы доставки</h4>
                <ul>
                    <c:forEach var="region" items="${deliveryRegionList}">
                        <li>
                            <tiles:insertDefinition name="region-path">
                                <tiles:putAttribute name="_region" value="${region}" />
                                <tiles:putAttribute name="_regionPath" value="${region.path}" />
                            </tiles:insertDefinition>
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
        </c:if>
        <c:if test="${not empty rootSectionList}">
            <tiles:insertDefinition name="catalog-category-item-list">
                <tiles:putAttribute name="_sectionList" value="${rootSectionList}" />
                <tiles:putAttribute name="_sectionCategoryMap" value="${rootSectionCategoryMap}" />
                <tiles:putAttribute name="_maxColumnCount" value="${2}" />
            </tiles:insertDefinition>
        </c:if>
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <div class="company-info-box">
        <c:if test="${not empty company.logo}">
            <div class="company-logo">
                <a <c:if test="${not empty company.site}">href="<c:out value='${company.site}' />"</c:if>><img alt="<c:out value="${company.name}" />" src="<cdn:url container="company" key="${company.logo}" />" /></a>
            </div>
        </c:if>
        <c:if test="${not empty company.scope}">
            <p><c:out value="${company.scope}" /></p>
        </c:if>
        <p class="info">
            Информация публикуется в виде, предоставленном поставщиком
        </p>
    </div>
</div><!-- .sidebar#sideRight -->
