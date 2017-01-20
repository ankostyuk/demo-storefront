<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="pfn" uri="http://www.nullpointer.ru/pfn/tags" %>

<tiles:useAttribute name="offer" />
<tiles:useAttribute name="company" />
<tiles:useAttribute name="companyRegion" />
<tiles:useAttribute name="settings" />
<tiles:useAttribute name="_unit" ignore="true" />

<%-- argumentSeparator для того чтобы spring:message не считал запятую в значении разделителем --%>
<span class="price"><spring:message code="currency.default.format" arguments="${pfn:formatPrice(offer.unitPrice, true)}" argumentSeparator="|" /></span>
<c:if test="${settings.priceType == 'EXTRA_CURRENCY'}">
    (<spring:message code="currency.${settings.extraCurrency}.format" arguments="${pfn:formatPrice(offer.unitPrice * settings.extraCurrencyMultiplier, true)}" argumentSeparator="|" />)
</c:if>
<%-- TODO unit ? --%>
<c:if test="${not empty _unit}">
    за <abbr title="<c:out value="${_unit.name}" />">${fn2:htmlformula(_unit.abbreviation)}</abbr>
</c:if>
<c:if test="${offer.available}">
    в наличии
</c:if>
<c:if test="${not offer.available}">
    на заказ
</c:if>
у
<a href="<spring:url value='/company/{id}'><spring:param name='id' value='${offer.companyId}' /></spring:url>"><c:out value="${company.name}" /></a>,
<c:out value="${companyRegion.name}" />
