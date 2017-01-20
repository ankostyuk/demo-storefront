<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<c:if test="${currentItem == null}">
    <c:set var="baseUrlValue" value="/secured/company/offers" />
</c:if>
<c:if test="${currentItem != null}">
    <c:set var="baseUrlValue" value="/secured/company/offers/${currentItem.id}" />
</c:if>
<c:set var="redirectUrlValue" value="${baseUrlValue}?cat=${catalog.alias}&show=${showing.alias}&sort=${sorting.alias}&page=${queryResult.pageNumber}" />

<div id="container">
    <div id="content-left-sidebar">

        <div class="offers-top-box">
            <ul class="v-list">
                <c:if test="${path != null}">
                    <li class="catalog-item-path">
                        <tiles:insertDefinition name="secured-company-catalog-item-path">
                            <tiles:putAttribute name="_path" value="${path}" />
                            <tiles:putAttribute name="_catalog" value="${catalog}" />
                            <tiles:putAttribute name="_sorting" value="${sorting}" />
                            <tiles:putAttribute name="_showing" value="${showing}" />
                            <tiles:putAttribute name="_lastLink" value="${queryResult.pageNumber != 1}" />
                        </tiles:insertDefinition>
                    </li>
                </c:if>
                <li class="tools">
                    <c:choose>
                        <c:when test="${currentItem != null && currentItem.type == 'CATEGORY'}">
                            <a class="composite new" href="<spring:url value="/secured/company/offer/add" htmlEscape="true">
                                   <spring:param name="categoryId" value="${currentItem.id}" />
                                   <spring:param name="redirect" value="${redirectUrlValue}" />
                               </spring:url>"><span class="link">Добавить предложение</span></a>
                        </c:when>
                        <c:otherwise>
                            <a class="composite new" href="<spring:url value="/secured/company/offer/category" htmlEscape="true">
                                   <c:if test="${currentItem != null}"><spring:param name="id" value="${currentItem.id}" /></c:if>
                                   <spring:param name="cat" value="${catalog.alias}" />
                               </spring:url>"><span class="link">Добавить предложение</span></a>
                        </c:otherwise>
                    </c:choose>
                </li>
            </ul>
        </div>
        <c:if test="${currentItem != null && !currentItem.active}">
            <c:if test="${currentItem.type == 'CATEGORY'}">
                <p>Эта категория временно неактивна. Предложения будут опубликованы после активации категории менеджером.</p>
            </c:if>
            <c:if test="${currentItem.type == 'SECTION'}">
                <p>Этот раздел временно неактивен. Предложения будут опубликованы после активации раздела менеджером.</p>
            </c:if>
        </c:if>

        <div class="offers-show-box">
            <span class="h-list-title">Показать</span>
            <ul class="h-list items compact">
                <c:choose>
                    <c:when test="${showing == 'ALL'}">
                        <li><span class="current">все&nbsp;<span class="offer-count-inline">${queryResult.total}</span></span></li>
                    </c:when>
                    <c:otherwise>
                        <li><span><a class="list-act" title="Показать все предложения" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                         <spring:param name="cat" value="${catalog.alias}" />
                                         <spring:param name="show" value="all" />
                                         <spring:param name="sort" value="${sorting.alias}" />
                                     </spring:url>">все</a>&nbsp;<span class="offer-count-inline">${showingCountMap['ALL']}</span></span>
                        </li>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${showing == 'REJECTED'}">
                        <li><span class="current">отклоненные&nbsp;<span class="offer-count-inline">${queryResult.total}</span></span></li>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${showingCountMap['REJECTED'] > 0}">
                            <li><span><a class="list-act" title="Показать отклоненные модератором предложения" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                             <spring:param name="cat" value="${catalog.alias}" />
                                             <spring:param name="show" value="rejected" />
                                             <spring:param name="sort" value="${sorting.alias}" />
                                         </spring:url>">отклоненные</a>&nbsp;<span class="offer-count-inline">${showingCountMap['REJECTED']}</span></span>
                            </li>
                        </c:if>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${showing == 'INACTIVE'}">
                        <li><span class="current">неактуальные&nbsp;<span class="offer-count-inline">${queryResult.total}</span></span></li>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${showingCountMap['INACTIVE'] > 0}">
                            <li><span><a class="list-act" title="Показать неактуальные предложения" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                             <spring:param name="cat" value="${catalog.alias}" />
                                             <spring:param name="show" value="inactive" />
                                             <spring:param name="sort" value="${sorting.alias}" />
                                         </spring:url>">неактуальные</a>&nbsp;<span class="offer-count-inline">${showingCountMap['INACTIVE']}</span></span>
                            </li>
                        </c:if>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
        <div class="sort-box">
            <span class="title">Сортировать по</span>
            <ul class="h-list compact">
                <c:choose>
                    <c:when test="${sorting == 'DATE_EDITED_ASCENDING'}">
                        <li class="current">
                            <a class="composite sort-asc" title="Показать последние измененные" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                   <spring:param name="cat" value="${catalog.alias}" />
                                   <spring:param name="show" value="${showing.alias}" />
                                   <spring:param name="sort" value="date-edited-desc" />
                               </spring:url>"><span class="link">дате изменения</span></a>
                        </li>
                    </c:when>
                    <c:when test="${sorting == 'DATE_EDITED_DESCENDING'}">
                        <li class="current">
                            <a class="composite sort-desc" title="Показать измененные ранее" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                   <spring:param name="cat" value="${catalog.alias}" />
                                   <spring:param name="show" value="${showing.alias}" />
                                   <spring:param name="sort" value="date-edited-asc" />
                               </spring:url>"><span class="link">дате изменения</span></a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a title="Показать последние измененные" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                   <spring:param name="cat" value="${catalog.alias}" />
                                   <spring:param name="show" value="${showing.alias}" />
                                   <spring:param name="sort" value="date-edited-desc" />
                               </spring:url>">дате изменения</a>
                        </li>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${sorting == 'DATE_CREATED_ASCENDING'}">
                        <li class="current">
                            <a class="composite sort-asc" title="Показать более поздние" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                   <spring:param name="cat" value="${catalog.alias}" />
                                   <spring:param name="show" value="${showing.alias}" />
                                   <spring:param name="sort" value="date-created-desc" />
                               </spring:url>"><span class="link">дате добавления</span></a>
                        </li>
                    </c:when>
                    <c:when test="${sorting == 'DATE_CREATED_DESCENDING'}">
                        <li class="current">
                            <a class="composite sort-desc" title="Показать более ранние" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                   <spring:param name="cat" value="${catalog.alias}" />
                                   <spring:param name="show" value="${showing.alias}" />
                                   <spring:param name="sort" value="date-created-asc" />
                               </spring:url>"><span class="link">дате добавления</span></a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li><a title="Показать более поздние" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                   <spring:param name="cat" value="${catalog.alias}" />
                                   <spring:param name="show" value="${showing.alias}" />
                                   <spring:param name="sort" value="date-created-desc" />
                               </spring:url>">дате добавления</a>
                        </li>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${sorting == 'NAME'}">
                        <li class="current">наименованию</li>
                    </c:when>
                    <c:otherwise>
                        <li><a title="Отсортировать в алфавитном порядке" href="<spring:url value="${baseUrlValue}" htmlEscape="true">
                                   <spring:param name="cat" value="${catalog.alias}" />
                                   <spring:param name="show" value="${showing.alias}" />
                                   <spring:param name="sort" value="name" />
                               </spring:url>">наименованию</a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
        <%-- TODO: Возможно и не стоит отображать эту подсказку --%>
        <p class="info">
            <c:choose>
                <c:when test="${showing == 'ALL'}">
                    Всего <c:out value="${queryResult.total} ${fn2:ruplural(queryResult.total, 'предложение/предложения/предложений')}" />
                </c:when>
                <c:when test="${showing == 'REJECTED'}">
                    Всего <c:out value="${queryResult.total} ${fn2:ruplural(queryResult.total, 'отклоненное предложение/отклоненных предложения/отклоненных предложений')}" />
                </c:when>
                <c:when test="${showing == 'INACTIVE'}">
                    Всего <c:out value="${queryResult.total} ${fn2:ruplural(queryResult.total, 'неактуальное предложение/неактуальных предложения/неактуальных предложений')}" />
                </c:when>
            </c:choose>
            <c:if test="${queryResult.total < queryResult.pageSize}">, показаны все.</c:if>
            <c:if test="${queryResult.total > queryResult.pageSize}">, показаны — с ${queryResult.firstNumber} по ${queryResult.lastNumber}.</c:if>
        </p>

        <div class="offer-list">
            <c:forEach var="offer" items="${queryResult.list}">
                <tiles:insertDefinition name="secured-company-offer-list-item">
                    <tiles:putAttribute name="_offer" value="${offer}" />
                    <c:if test="${pathMap != null}">
                        <tiles:putAttribute name="_categoryPath" value="${pathMap[offer.categoryId]}" />
                    </c:if>
                    <tiles:putAttribute name="_defaultCurrency" value="${defaultCurrency}" />
                    <tiles:putAttribute name="_unit" value="${unitMap[offer.categoryId]}" />
                    <tiles:putAttribute name="_catalog" value="${catalog}" />
                    <tiles:putAttribute name="_sorting" value="${sorting}" />
                    <tiles:putAttribute name="_showing" value="${showing}" />
                    <tiles:putAttribute name="_redirect" value="${redirectUrlValue}" />
                </tiles:insertDefinition>
            </c:forEach>
        </div>

        <c:if test="${queryResult.total > queryResult.pageSize}">
            <tiles:insertTemplate template="/WEB-INF/jsp/template/common-pager.jsp">
                <tiles:putAttribute name="queryResult" value="${queryResult}" />
                <tiles:putAttribute name="pagerUrl">
                    <spring:url value="${baseUrlValue}" htmlEscape="true">
                        <spring:param name="cat" value="${catalog.alias}" />
                        <spring:param name="show" value="${showing.alias}" />
                        <spring:param name="sort" value="${sorting.alias}" />
                    </spring:url>
                </tiles:putAttribute>
                <tiles:putAttribute name="pagerUrlHasParams" value="true" />
            </tiles:insertTemplate>
        </c:if>

    </div><!-- #content-->
</div><!-- #container-->
<div class="sidebar" id="sideLeft">

    <p class="info">
        Не нашли нужной категории? 
        <br />
        <a href="http://bildika.reformal.ru/">Напишите</a> нам, и мы обязательно добавим.
    </p>

    <tiles:insertDefinition name="secured-company-catalog-tree-filter">
        <tiles:putAttribute name="_baseUrlValue" value="${baseUrlValue}" />
        <tiles:putAttribute name="_catalog" value="${catalog}" />
        <tiles:putAttribute name="_showing" value="${showing}" />
        <tiles:putAttribute name="_sorting" value="${sorting}" />
    </tiles:insertDefinition>

    <tiles:insertDefinition name="secured-company-catalog-tree">
        <tiles:putAttribute name="_catalogTree" value="${catalogTree}" />
        <tiles:putAttribute name="_currentPath" value="${path}" />
    </tiles:insertDefinition>
</div><!-- .sidebar#sideLeft -->
