<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="usf" uri="http://www.nullpointer.ru/usf/tags" %>
<%@ taglib prefix="pfn" uri="http://www.nullpointer.ru/pfn/tags" %>

<c:set var="redirect" value="/offer/${offer.id}"/>

<div id="container">
    <div id="content-right-sidebar" class="offer-page">
        <c:if test="${not offer.modelLinked}">
            <tiles:insertDefinition name="catalog-item-path">
                <tiles:putAttribute name="catalogItemPath" value="${categoryPath}" />
                <tiles:putAttribute name="_noLastLink" value="${false}" />
            </tiles:insertDefinition>
        </c:if>
        <c:if test="${offer.modelLinked}">
            <tiles:insertDefinition name="offer-model-catalog-item-path">
                <tiles:putAttribute name="_categoryPath" value="${categoryPath}" />
                <tiles:putAttribute name="_offerModel" value="${offerModel}" />
            </tiles:insertDefinition>
        </c:if>
        <h1><c:out value="${offer.name}" /></h1>
        <c:if test="${not offerAccessible}">
            <p class="not-accessible-info">
                Товарное предложение неактуально
            </p>
        </c:if>
        <p class="price-info<c:if test="${not offerAccessible}"> not-accessible</c:if>">
            <tiles:insertDefinition name="offer-price">
                <tiles:putAttribute name="offer" value="${offer}" />
                <tiles:putAttribute name="company" value="${company}" />
                <tiles:putAttribute name="companyRegion" value="${companyRegion}" />
                <tiles:putAttribute name="settings" value="${userSession.settings}" />
                <tiles:putAttribute name="_unit" value="${unit}" />
            </tiles:insertDefinition>
        </p>
        <c:if test="${offerAccessible}">
            <div class="actions">
                <ul class="h-list">
                    <li>
                        <tiles:insertDefinition name="match-cart-info">
                            <tiles:putAttribute name="_id" value="o${offer.id}" />
                            <tiles:putAttribute name="_cartId" value="${usf:getOfferCartId(userSession, offer.id)}" />
                            <tiles:putAttribute name="_redirect" value="${redirect}" />
                        </tiles:insertDefinition>
                    </li>
                    <c:if test="${offer.parametrized || offer.modelLinked}">
                        <li>
                            <tiles:insertDefinition name="match-comparison-info">
                                <tiles:putAttribute name="_id" value="o${offer.id}" />
                                <tiles:putAttribute name="_categoryId" value="${offer.categoryId}" />
                                <tiles:putAttribute name="_isInComparison" value="${usf:isOfferInComparison(userSession, offer.id)}" />
                                <tiles:putAttribute name="_userSession" value="${userSession}" />
                                <tiles:putAttribute name="_redirect" value="${redirect}" />
                            </tiles:insertDefinition>
                        </li>
                    </c:if>
                </ul>
                <tiles:insertDefinition name="match-tools">
                    <tiles:putAttribute name="_redirect" value="${redirect}" />
                </tiles:insertDefinition>
            </div>
        </c:if>
        <c:if test="${offer.parametrized || offer.modelLinked}">
            <tiles:insertDefinition name="match-param">
                <tiles:putAttribute name="_catalogItem" value="${catalogItem}" />
                <tiles:putAttribute name="paramModel" value="${paramModel}" />
                <tiles:putAttribute name="paramInput" value="${paramInput}" />
                <tiles:putAttribute name="paramCountGroupMap" value="${paramCountGroupMap}" />
            </tiles:insertDefinition>
        </c:if>
        <%-- проверка на неравенство единице(!= 1) не работает, приходится изголяться --%>
        <c:set var="showRatio" value="${offer.ratio > 1 || offer.ratio < 1}" />
        <c:if test="${offer.currency != defaultCurrency || showRatio}">
            <p>
                Цена указанная поставщиком:
                <spring:message code="currency.${offer.currency}.format" arguments="${pfn:formatPrice(offer.price, false)}" argumentSeparator="|" />
                за <c:if test="${showRatio}"><fmt:formatNumber value="${offer.ratio}" maxFractionDigits="2" />&nbsp;</c:if>${fn2:htmlformula(unit.abbreviation)}
            </p>
        </c:if>
        <h3>Информация</h3>
        <c:if test="${not empty offer.description}">
            <p>
                <c:out value="${offer.description}" />
            </p>
        </c:if>
        <c:if test="${not empty brand}">
            <p>
                Бренд:
                <a href="<spring:url value="/brand/{id}"><spring:param name="id" value="${brand.id}" /></spring:url>">
                    <c:out value="${brand.name}" />
                </a>
            </p>
        </c:if>
        <c:if test="${empty brand and not empty offer.brandName}">
            <p>
                Бренд: <a href="<spring:url value="/search">
                              <spring:param name="text" value="${offer.brandName}" />
                          </spring:url>" title="Искать похожие предложения"><c:out value="${offer.brandName}" /></a>
            </p>
        </c:if>
        <c:if test="${not empty offerModel}">
            <p>
                Модель: <a href="<spring:url value="/model/{id}">
                               <spring:param name="id" value="${offerModel.id}" />
                           </spring:url>" title="Посмотреть информацию о модели"><c:out value="${offerModel.name}" /></a>
            </p>
        </c:if>
        <c:if test="${empty offerModel and not empty offer.modelName}">
            <p>
                Модель: <a href="<spring:url value="/search">
                               <spring:param name="text" value="${offer.modelName}" />
                           </spring:url>" title="Искать похожие предложения"><c:out value="${offer.modelName}" /></a>
            </p>
        </c:if>
        <p>
            <span class="title">Страна производства:</span>
            <c:if test="${not empty originCountry}">
                <c:out value="${originCountry.name}" />
            </c:if>
            <c:if test="${empty originCountry}">
                не указана
            </c:if>
        </p>
        <h3>Доставка</h3>
        <c:if test="${not offer.delivery}">
            <p>Не осуществляется</p>
        </c:if>
        <c:if test="${offer.delivery}">
            <c:if test="${userSession.settings.regionAware && not userRegionDelivery}">
                <p>Не доставляется в ваш регион</p>
            </c:if>
            <p>
                <c:if test="${not empty company.deliveryConditions}">
                    <c:out value="${company.deliveryConditions}" />
                </c:if>
                <c:if test="${empty company.deliveryConditions}">
                    Уточняйте условия у поставщика
                </c:if>
            </p>
            <c:if test="${not empty deliveryRegionList}">
                <h4>Регионы доставки</h4>
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
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <c:if test="${not empty offer.image}">
        <tiles:insertDefinition name="match-image">
            <tiles:putAttribute name="_matchTitle" value="${offer.name}" />
            <tiles:putAttribute name="_imageThumbUrl"><cdn:url container="offer" key="${offer.image}" /></tiles:putAttribute>
            <tiles:putAttribute name="_imageUrl"><cdn:url container="offer_large" key="${offer.image}" /></tiles:putAttribute>
        </tiles:insertDefinition>
    </c:if>
</div><!-- .sidebar#sideRight -->
