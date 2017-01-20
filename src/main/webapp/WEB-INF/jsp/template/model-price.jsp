<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="pfn" uri="http://www.nullpointer.ru/pfn/tags" %>

<tiles:useAttribute name="modelInfo" />
<tiles:useAttribute name="settings" />
<tiles:useAttribute name="_unit" ignore="true" />

<c:if test="${modelInfo.offerCount == 1}">
    <span class="price"><spring:message code="currency.default.format" arguments="${pfn:formatPrice(modelInfo.minPrice, true)}" argumentSeparator="|" /></span>
    <c:if test="${settings.priceType == 'EXTRA_CURRENCY'}">
        (<spring:message code="currency.${settings.extraCurrency}.format" arguments="${pfn:formatPrice(modelInfo.minPrice * settings.extraCurrencyMultiplier, true)}" argumentSeparator="|" />)
    </c:if>
</c:if>
<c:if test="${modelInfo.offerCount > 1}">
    <span class="price">${pfn:formatPrice(modelInfo.minPrice, true)}</span>&nbsp;&mdash;
    <span class="price"><spring:message code="currency.default.format" arguments="${pfn:formatPrice(modelInfo.maxPrice, true)}" argumentSeparator="|" /></span>
    <c:if test="${settings.priceType == 'EXTRA_CURRENCY'}">
        (${pfn:formatPrice(modelInfo.minPrice * settings.extraCurrencyMultiplier, true)}
        &mdash;
        ${pfn:formatPrice(modelInfo.maxPrice * settings.extraCurrencyMultiplier, true)}&nbsp;<spring:message code="currency.${settings.extraCurrency}.abbr" />)
    </c:if>
</c:if>
<c:if test="${not empty _unit}">
    за <abbr title="<c:out value="${_unit.name}" />">${fn2:htmlformula(_unit.abbreviation)}</abbr>
</c:if>
