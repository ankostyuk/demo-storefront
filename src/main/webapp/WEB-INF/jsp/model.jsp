<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="usf" uri="http://www.nullpointer.ru/usf/tags" %>

<c:set var="redirect" value="/model/${model.id}" />

<div id="container">
    <div id="content-right-sidebar" class="model-page">
        <div class="model-info">
            <tiles:insertDefinition name="catalog-item-path">
                <tiles:putAttribute name="catalogItemPath" value="${modelCategoryPath}" />
                <tiles:putAttribute name="_noLastLink" value="${false}" />
            </tiles:insertDefinition>
            <h1><c:out value="${model.name}" /></h1>
            <c:if test="${empty modelInfo}">
                <c:if test="${not regionAware}">
                    <p class="info">
                        Нет в продаже.
                    </p>
                </c:if>
                <c:if test="${regionAware}">
                    <p class="info">
                        Нет в продаже в вашем регионе.
                    </p>
                    <p class="info">
                        Попробуйте включить отображение предложений <a id="region-info-set-link" class="act" rel="nofollow" href="<c:url value='/settings/region'/>">других регионов</a>.
                    </p>
                </c:if>
            </c:if>
            <c:if test="${not empty modelInfo}">
                <p class="price-info">
                    <tiles:insertDefinition name="model-price">
                        <tiles:putAttribute name="modelInfo" value="${modelInfo}" />
                        <tiles:putAttribute name="settings" value="${userSession.settings}" />
                        <tiles:putAttribute name="_unit" value="${unit}" />
                    </tiles:insertDefinition>
                    <span>
                        <a class="offer-count" href="<spring:url value='/model/{id}/offers'><spring:param name='id' value='${model.id}' /></spring:url>">
                            <c:out value="${modelInfo.offerCount} ${fn2:ruplural(modelInfo.offerCount, 'предложение/предложения/предложений')}" />
                        </a>
                    </span>
                </p>
            </c:if>
            <div class="actions">
                <ul class="h-list">
                    <c:if test="${not empty modelInfo}">
                        <li>
                            <tiles:insertDefinition name="match-cart-info">
                                <tiles:putAttribute name="_id" value="m${model.id}" />
                                <tiles:putAttribute name="_cartId" value="${usf:getModelCartId(userSession, model.id)}" />
                                <tiles:putAttribute name="_redirect" value="${redirect}" />
                            </tiles:insertDefinition>
                        </li>
                    </c:if>
                    <li>
                        <tiles:insertDefinition name="match-comparison-info">
                            <tiles:putAttribute name="_id" value="m${model.id}" />
                            <tiles:putAttribute name="_categoryId" value="${model.categoryId}" />
                            <tiles:putAttribute name="_isInComparison" value="${usf:isModelInComparison(userSession, model.id)}" />
                            <tiles:putAttribute name="_userSession" value="${userSession}" />
                            <tiles:putAttribute name="_redirect" value="${redirect}" />
                        </tiles:insertDefinition>
                    </li>
                </ul>
                <tiles:insertDefinition name="match-tools">
                    <tiles:putAttribute name="_redirect" value="${redirect}" />
                </tiles:insertDefinition>
            </div>
            <tiles:insertDefinition name="match-param">
                <tiles:putAttribute name="_catalogItem" value="${catalogItem}" />
                <tiles:putAttribute name="paramModel" value="${paramModel}" />
                <tiles:putAttribute name="paramInput" value="${paramInput}" />
                <tiles:putAttribute name="paramCountGroupMap" value="${paramCountGroupMap}" />
            </tiles:insertDefinition>
                
            <h3>Информация</h3>
            <c:if test="${not empty model.description}">
                <p><c:out value="${model.description}" /></p>
            </c:if>
            <p>
                Бренд: <a href="<spring:url value='/brand/{id}'><spring:param name='id' value='${brand.id}' /></spring:url>"><c:out value="${brand.name}" /></a>
            </p>
            <c:if test="${not empty model.vendorCode}">
                <p>
                    Код производителя: <c:out value="${model.vendorCode}" />
                </p>
            </c:if>
            <c:if test="${not empty model.site}">
                <p>
                    Модель на сайте производителя: <a href="<c:out value="${model.site}" />"><c:out value="${fn:substringAfter(model.site, '://')}" /></a>
                </p>
            </c:if>
        </div>
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <c:if test="${not empty model.image}">
        <tiles:insertDefinition name="match-image">
            <tiles:putAttribute name="_matchTitle" value="${model.name}" />
            <tiles:putAttribute name="_imageThumbUrl"><cdn:url container="model" key="${model.image}" /></tiles:putAttribute>
            <tiles:putAttribute name="_imageUrl"><cdn:url container="model_large" key="${model.image}" /></tiles:putAttribute>
        </tiles:insertDefinition>
    </c:if>
</div><!-- .sidebar#sideRight -->
