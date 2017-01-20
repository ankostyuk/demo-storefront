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
<tiles:useAttribute name="regionAware" ignore="true" />

<div class="match-item">
    <c:if test="${not empty match.model.image}">
        <div class="match-image">
            <a class="hover-src-m${match.model.id}" href="<spring:url value='/model/{id}'><spring:param name='id' value='${match.model.id}' /></spring:url>"><img alt="" src="<cdn:url container="model_mini" key="${match.model.image}" />" /></a>
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
        <a class="name hover-dst-m${match.model.id}" href="<spring:url value='/model/{id}'><spring:param name='id' value='${match.model.id}' /></spring:url>"><c:out value="${match.model.name}" /></a>
        <c:if test="${not empty match.model.description}">
            <p class="description"><c:out value="${match.model.description}" /></p>
        </c:if>
        <c:if test="${not empty match.model.paramDescription && fn:length(match.model.description) < 100}">
            <p class="param-description">Характеристики: ${fn2:htmlformula(match.model.paramDescription)}</p>
        </c:if>
        <p class="price-info">
            <c:if test="${empty match.modelInfo}">
                <span class="not-available">
                    Нет в продаже
                    <c:if test="${regionAware}">в вашем регионе</c:if>
                </span>
            </c:if>
            <c:if test="${not empty match.modelInfo}">
                <tiles:insertDefinition name="model-price">
                    <tiles:putAttribute name="modelInfo" value="${match.modelInfo}" />
                    <tiles:putAttribute name="settings" value="${userSession.settings}" />
                    <tiles:putAttribute name="_unit" value="${_unit}" />
                </tiles:insertDefinition>
                <span class="offers">
                    <a class="offer-count" href="<spring:url value='/model/{id}/offers'><spring:param name='id' value='${match.model.id}' /></spring:url>">
                        <c:out value="${match.modelInfo.offerCount} ${fn2:ruplural(match.modelInfo.offerCount, 'предложение/предложения/предложений')}" />
                    </a>
                </span>
            </c:if>
        </p>
        <c:if test="${not empty match.modelInfo}">
            <div class="actions">
                <ul class="h-list">
                    <c:if test="${empty cartItem}">
                        <li>
                            <tiles:insertDefinition name="match-cart-info">
                                <tiles:putAttribute name="_id" value="m${match.model.id}" />
                                <tiles:putAttribute name="_cartId" value="${usf:getModelCartId(userSession, match.model.id)}" />
                                <tiles:putAttribute name="_redirect" value="${redirect}" />
                            </tiles:insertDefinition>
                        </li>
                    </c:if>
                    <li>
                        <tiles:insertDefinition name="match-comparison-info">
                            <tiles:putAttribute name="_id" value="m${match.model.id}" />
                            <tiles:putAttribute name="_categoryId" value="${match.model.categoryId}" />
                            <tiles:putAttribute name="_isInComparison" value="${usf:isModelInComparison(userSession, match.model.id)}" />
                            <tiles:putAttribute name="_userSession" value="${userSession}" />
                            <tiles:putAttribute name="_redirect" value="${redirect}" />
                        </tiles:insertDefinition>
                    </li>
                </ul>
            </div>
        </c:if>
    </div>
</div>