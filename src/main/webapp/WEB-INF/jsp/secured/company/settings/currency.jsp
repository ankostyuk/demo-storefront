<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">
    <tiles:insertDefinition name="secured-company-settings-menu" />

    <h1>Курсы валют</h1>
    <p>
        Основная валюта&nbsp;&mdash; <strong><spring:message code="currency.default.name" /></strong> (<spring:message code="currency.default.abbr" />)
    </p>
    <p>
        Выберите, по какому курсу будет осуществляться пересчет цен на ваши предложения, если они указаны в валюте отличной от основной валюты.
    </p>

    <c:url var="action" value="/secured/company/settings/currency" />
    <form:form class="plain currency" modelAttribute="options" action="${action}">
        <c:if test="${not empty paramValues['updated']}">
            <p class="message"><span class="form-updated">Курсы валют успешно сохранены</span></p>
        </c:if>
        <spring:bind path="options.*" >
            <c:if test="${status.error}">
                <p class="message"><span class="form-error">Некорректно заполнены некоторые поля ниже</span></p>
            </c:if>
        </spring:bind>
        <c:forEach var="currency" items="${options.keys}" >
            <h3 class="field-group-name">Пересчет в <spring:message code="currency.${currency}.name" />&nbsp;<span class="font-normal">(<spring:message code="currency.${currency}.abbr" />)</span></h3>
            <ul class="field-group">
                <li <c:if test="${empty options.value[currency].type}">class="current"</c:if>>
                    <label>
                        <form:radiobutton id="" path="value[${currency}].type" cssClass="radio"  value="${null}" />
                        по курсу ЦБ РФ 
                        <fmt:formatNumber value="${rateMap[currency]}" pattern="#.00" var="rate" />
                        <span class="rate">(<spring:message code="currency.${currency}.format" arguments="1" /> = ${rate}<spring:message code="currency.default.format" arguments=" " />)</span>
                    </label>
                </li>
                <li <c:if test="${options.value[currency].type == 'PERCENT'}">class="current"</c:if>>
                    <div class="input-box">
                        <label>
                            <form:radiobutton id="" path="value[${currency}].type" cssClass="radio" value="PERCENT" />
                            по курсу ЦБ РФ +
                        </label>
                        <label>
                            <form:input id="" cssClass="text inline-number" path="value[${currency}].percent" />&nbsp;<span>%</span><!-- <span> обязателен - для IE7 -->
                        </label>
                    </div>
                    <form:errors cssClass="error" path="value[${currency}].percent" />
                </li>
                <li <c:if test="${options.value[currency].type == 'FIXED_RATE'}">class="current"</c:if>>
                    <div class="input-box">
                        <label>
                            <form:radiobutton id="" path="value[${currency}].type" cssClass="radio"  value="FIXED_RATE" />
                            по фиксированному курсу
                        </label>
                        <label>
                            <form:input cssClass="text inline-number" id="" path="value[${currency}].fixedRate" />&nbsp;<span><spring:message code="currency.default.format" arguments=" " /> за <spring:message code="currency.${currency}.format" arguments="1" /></span>
                        </label>
                    </div>
                    <form:errors cssClass="error" path="value[${currency}].fixedRate" />
                </li>
            </ul>
        </c:forEach>
        <div class="submit">
            <input type="submit" value="Сохранить"/>
        </div>
    </form:form>

</div><!-- #content-->