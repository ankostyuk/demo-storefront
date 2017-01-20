<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<div id="container">
    <div id="content-left-sidebar">

        <c:if test="${path != null}">
            <div class="offers-top-box">
                <div class="catalog-item-path">
                    <tiles:insertDefinition name="secured-company-catalog-item-path">
                        <tiles:putAttribute name="_path" value="${path}" />
                        <tiles:putAttribute name="_catalog" value="${catalog}" />
                        <tiles:putAttribute name="_sorting" value="${sorting}" />
                        <tiles:putAttribute name="_showing" value="${showing}" />
                    </tiles:insertDefinition>
                </div>
            </div>
        </c:if>

        <c:choose>
            <c:when test="${noSubCategories}">
                <p>
                    К сожалению, в этом разделе пока еще нет категорий.
                </p>
                <p>
                    Пожалуйста, <a href="http://bildika.reformal.ru/">напишите</a> нам нам какие категории вы бы хотели видеть в этом разделе, и мы обязательно добавим.
                </p>
            </c:when>
            <c:when test="${currentItem == null}">
                <spring:url var="showAllUrl" value="/secured/company/offers" htmlEscape="true">
                    <spring:param name="cat" value="${catalog.alias}" />
                    <spring:param name="show" value="all" />
                    <spring:param name="sort" value="${sorting.alias}" />
                </spring:url>
                <p>
                    <c:choose>
                        <c:when test="${showing == 'ALL'}">
                            У вас еще нет предложений.
                            Вы можете <a href="<spring:url value="/secured/company/offer/category" htmlEscape="true">
                                             <spring:param name="cat" value="${catalog.alias}" />
                                         </spring:url>">добавить их прямо сейчас</a>.
                        </c:when>
                        <c:when test="${showing == 'REJECTED'}">
                            У вас нет отклоненных предложений. Вы можете <a href="${showAllUrl}">посмотреть все предложения</a>.
                        </c:when>
                        <c:when test="${showing == 'INACTIVE'}">
                            У вас нет неактуальных предложений. Вы можете <a href="${showAllUrl}">посмотреть все предложения</a>.
                        </c:when>
                    </c:choose>
                </p>
            </c:when>
            <c:otherwise>
                <spring:url var="showAllUrl" value="/secured/company/offers/{id}" htmlEscape="true">
                    <spring:param name="id" value="${currentItem.id}" />
                    <spring:param name="cat" value="${catalog.alias}" />
                    <spring:param name="show" value="all" />
                    <spring:param name="sort" value="${sorting.alias}" />
                </spring:url>
                <c:choose>
                    <c:when test="${currentItem.type == 'SECTION'}">
                        <c:if test="${!currentItem.active}">
                            <p>Этот раздел временно неактивен. Предложения будут опубликованы после активации раздела менеджером.</p>
                        </c:if>
                        <p>
                            <c:choose>
                                <c:when test="${showing == 'ALL'}">
                                    У вас еще нет предложений в этом разделе.
                                    Вы можете <a href="<spring:url value="/secured/company/offer/category" htmlEscape="true">
                                                     <spring:param name="id" value="${currentItem.id}" />
                                                     <spring:param name="cat" value="${catalog.alias}" />
                                                 </spring:url>">добавить их прямо сейчас</a>.
                                </c:when>
                                <c:when test="${showing == 'REJECTED'}">
                                    У вас нет отклоненных предложений в этом разделе. Вы можете <a href="${showAllUrl}">посмотреть все предложения</a> раздела.
                                </c:when>
                                <c:when test="${showing == 'INACTIVE'}">
                                    У вас нет неактуальных предложений в этом разделе. Вы можете <a href="${showAllUrl}">посмотреть все предложения</a> раздела.
                                </c:when>
                            </c:choose>
                        </p>
                    </c:when>
                    <c:when test="${currentItem.type == 'CATEGORY'}">
                        <c:if test="${!currentItem.active}">
                            <p>Эта категория временно неактивна. Предложения будут опубликованы после активации категории менеджером.</p>
                        </c:if>
                        <p>
                            <c:choose>
                                <c:when test="${showing == 'ALL'}">
                                    У вас еще нет предложений в этой категории.
                                    Вы можете <a href="<spring:url value="/secured/company/offer/add" htmlEscape="true">
                                                     <spring:param name="categoryId" value="${currentItem.id}" />
                                                     <spring:param name="redirect" value="/secured/company/offers/${currentItem.id}?cat=${catalog.alias}&sort=${sorting.alias}" />
                                                 </spring:url>">добавить их прямо сейчас</a>.
                                </c:when>
                                <c:when test="${showing == 'REJECTED'}">
                                    У вас нет отклоненных предложений в этой категории. Вы можете <a href="${showAllUrl}">посмотреть все предложения</a> категории.
                                </c:when>
                                <c:when test="${showing == 'INACTIVE'}">
                                    У вас нет неактуальных предложений в этой категории. Вы можете <a href="${showAllUrl}">посмотреть все предложения</a> категории.
                                </c:when>
                            </c:choose>
                        </p>
                    </c:when>
                </c:choose>
            </c:otherwise>
        </c:choose>

    </div><!-- #content-->
</div><!-- #container-->
<div class="sidebar" id="sideLeft">

    <p class="info">
        Не нашли нужной категории?
        <br />
        <a href="http://bildika.reformal.ru/">Напишите</a> нам, и мы обязательно добавим.
    </p>
    
    <%-- Показать фильтр только если есть предложения --%>
    <c:if test="${totalOfferCount > 0}">
        <c:if test="${currentItem == null}">
            <c:set var="baseUrlValue" value="/secured/company/offers" />
        </c:if>
        <c:if test="${currentItem != null}">
            <c:set var="baseUrlValue" value="/secured/company/offers/${currentItem.id}" />
        </c:if>
        <tiles:insertDefinition name="secured-company-catalog-tree-filter">
            <tiles:putAttribute name="_baseUrlValue" value="${baseUrlValue}" />
            <tiles:putAttribute name="_catalog" value="${catalog}" />
            <tiles:putAttribute name="_showing" value="${showing}" />
            <tiles:putAttribute name="_sorting" value="${sorting}" />
        </tiles:insertDefinition>
    </c:if>

    <tiles:insertDefinition name="secured-company-catalog-tree">
        <tiles:putAttribute name="_catalogTree" value="${catalogTree}" />
        <tiles:putAttribute name="_currentPath" value="${path}" />
    </tiles:insertDefinition>
</div><!-- .sidebar#sideLeft -->