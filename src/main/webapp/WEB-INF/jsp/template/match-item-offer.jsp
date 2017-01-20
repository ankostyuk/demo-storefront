<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="usf" uri="http://www.nullpointer.ru/usf/tags" %>

<tiles:useAttribute name="match" />
<tiles:useAttribute name="categoryPath" ignore="true" />
<tiles:useAttribute name="cartItem" ignore="true" />
<tiles:useAttribute name="redirect" ignore="true" />
<tiles:useAttribute name="_unit" ignore="true" />

<div class="match-item">
    <c:if test="${not empty match.offer.image}">
        <div class="match-image">
            <a class="hover-src-o${match.offer.id}" href="<spring:url value='/offer/{id}'><spring:param name='id' value='${match.offer.id}' /></spring:url>"><img alt="" src="<cdn:url container="offer_mini" key="${match.offer.image}" />" /></a>
        </div>
    </c:if>
    <c:if test="${not empty cartItem}">
        <tiles:insertDefinition name="cart-item-actions">
            <tiles:putAttribute name="cartItem" value="${cartItem}" />
            <tiles:putAttribute name="_redirect" value="${redirect}" />
        </tiles:insertDefinition>
    </c:if>
    <div class="match-info">
        <c:if test="${not empty categoryPath}">
            <tiles:insertDefinition name="match-path">
                <tiles:putAttribute name="_matchPath" value="${categoryPath}" />
            </tiles:insertDefinition>
        </c:if>
        <a class="name hover-dst-o${match.offer.id}" href="<spring:url value='/offer/{id}'><spring:param name='id' value='${match.offer.id}' /></spring:url>"><c:out value="${match.offer.name}" /></a>
        <c:if test="${not empty match.offer.description}">
            <p class="description"><c:out value="${match.offer.description}" /></p>
        </c:if>
        <c:if test="${not empty match.offer.paramDescription && fn:length(match.offer.description) < 100}">
            <p class="param-description">Характеристики: ${fn2:htmlformula(match.offer.paramDescription)}</p>
        </c:if>
        <p class="price-info">
            <tiles:insertDefinition name="offer-price">
                <tiles:putAttribute name="offer" value="${match.offer}" />
                <tiles:putAttribute name="company" value="${match.company}" />
                <tiles:putAttribute name="companyRegion" value="${match.companyRegion}" />
                <tiles:putAttribute name="settings" value="${userSession.settings}" />
                <tiles:putAttribute name="_unit" value="${_unit}" />
            </tiles:insertDefinition>
        </p>
        <c:if test="${match.offer.delivery}">
            <p class="delivery">
                <c:if test="${not empty match.company.deliveryConditions}">
                    <span class="delivery-conditions-act">Доставка</span>:
                    <span class="delivery-conditions-short">есть</span>
                    <span class="delivery-conditions-all"><c:out value="${match.company.deliveryConditions}" /></span>
                </c:if>
                <c:if test="${empty match.company.deliveryConditions}">
                    Доставка: есть, уточняйте условия у поставщика
                </c:if>
            </p>
        </c:if>
        <c:if test="${empty cartItem || match.offer.parametrized || match.offer.modelLinked}">
            <div class="actions">
                <ul class="h-list">
                    <c:if test="${empty cartItem}">
                        <li>
                            <tiles:insertDefinition name="match-cart-info">
                                <tiles:putAttribute name="_id" value="o${match.offer.id}" />
                                <tiles:putAttribute name="_cartId" value="${usf:getOfferCartId(userSession, match.offer.id)}" />
                                <tiles:putAttribute name="_redirect" value="${redirect}" />
                            </tiles:insertDefinition>
                        </li>
                    </c:if>
                    <c:if test="${match.offer.parametrized || match.offer.modelLinked}">
                        <li>
                            <tiles:insertDefinition name="match-comparison-info">
                                <tiles:putAttribute name="_id" value="o${match.offer.id}" />
                                <tiles:putAttribute name="_categoryId" value="${match.offer.categoryId}" />
                                <tiles:putAttribute name="_isInComparison" value="${usf:isOfferInComparison(userSession, match.offer.id)}" />
                                <tiles:putAttribute name="_userSession" value="${userSession}" />
                                <tiles:putAttribute name="_redirect" value="${redirect}" />
                            </tiles:insertDefinition>
                        </li>
                    </c:if>
                </ul>
            </div>
        </c:if>
    </div>
</div>