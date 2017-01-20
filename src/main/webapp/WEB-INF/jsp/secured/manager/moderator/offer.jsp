<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="pfn" uri="http://www.nullpointer.ru/pfn/tags" %>

<div class="col1">
    <c:if test="${empty category}">
        <p>
            Выберите категорию для модерации.
        </p>
        <c:if test="${not empty pendingPathList}">
            <ul>
                <c:forEach var="path" items="${pendingPathList}">
                    <li>
                        <c:forEach var="item" items="${path}" varStatus="st">
                            <span>
                                <c:if test="${item.type == 'SECTION'}">
                                    <c:out value="${item.name}" />
                                </c:if>
                                <c:if test="${item.type == 'CATEGORY'}">
                                    <a href="<spring:url value="/secured/manager/moderator/offer/{id}">
                                           <spring:param name="id" value="${item.id}" />
                                       </spring:url>"><c:out value="${item.name}" /></a>&nbsp;(${pendingCountMap[item.id]})
                                </c:if>
                            </span>
                            <c:if test="${not st.last}">&nbsp;/&nbsp;</c:if>
                        </c:forEach>
                    </li>
                </c:forEach>
            </ul>
        </c:if>
    </c:if>
    <c:if test="${not empty category}">
        <c:if test="${fn:length(offerList) == 0}">
            <p>
                В категории нет предложений для модерации.
                <a href="<spring:url value="/secured/manager/moderator/offer/{id}">
                       <spring:param name="id" value="${category.id}" />
                   </spring:url>">Обновить</a> 
                или 
                <a href="<spring:url value="/secured/manager/moderator/offer/{id}">
                       <spring:param name="id" value="${category.id}" />
                       <spring:param name="all" value="${true}" />
                   </spring:url>">посмотреть все записи</a>?

            </p>
        </c:if>
        <c:if test="${fn:length(offerList) > 0}">
            <c:set var="redirectUrlValue">
                <spring:url value="secured/manager/moderator/offer/{id}" >
                    <spring:param name="id" value="${category.id}" />
                    <c:if test="${not empty param.all}"><spring:param name="all" value="${param.all}" /></c:if>
                    <c:if test="${not empty param.startDate}"><spring:param name="startDate" value="${param.startDate}" /></c:if>
                    <c:if test="${not empty param.startId}"><spring:param name="startId" value="${param.startId}" /></c:if>
                    <c:if test="${not empty param.forward}"><spring:param name="forward" value="${param.forward}" /></c:if>
                </spring:url>
            </c:set>
            <c:set var="redirectUrlValue" value="/${redirectUrlValue}" />

            <div class="sort-box">
                Показать:
                <ul>
                    <c:choose>
                        <c:when test="${param.all}">
                            <li>
                                <a href="<spring:url value="/secured/manager/moderator/offer/{id}">
                                       <spring:param name="id" value="${category.id}" />
                                   </spring:url>">новые записи</a>
                            </li>
                            <li class="current">
                                все записи
                            </li>
                        </c:when>
                        <c:otherwise>
                            <li class="current">новые записи</li>
                            <li>
                                <a href="<spring:url value="/secured/manager/moderator/offer/{id}">
                                       <spring:param name="id" value="${category.id}" />
                                       <spring:param name="all" value="${true}" />
                                   </spring:url>">все записи</a>
                            </li>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
            <div class="moderator-offer-list">
                <c:forEach items="${offerList}" var="offer" varStatus="st">
                    <c:if test="${st.first}">
                        &larr;&nbsp;<a class="backward" href="<spring:url value="/secured/manager/moderator/offer/{id}" htmlEscape="true">
                                           <spring:param name="id" value="${category.id}" />
                                           <c:if test="${param.all}"><spring:param name="all" value="${true}" /></c:if>
                                           <spring:param name="startDate" value="${offer.moderationStartDate.time}" />
                                           <spring:param name="startId" value="${offer.id}" />
                                           <spring:param name="forward" value="${false}" />
                                       </spring:url>">Назад</a>
                    </c:if>
                    <div class="moderator-offer-item">
                        <c:if test="${not empty offer.image}">
                            <div class="offer-image">
                                <img alt="" src="<cdn:url container="offer_mini" key="${offer.image}" />" />
                            </div>
                        </c:if>
                        <div class="offer-info">
                            <h2><c:out value="${offer.name}" /></h2>
                            <p class="time-box">
                                <fmt:formatDate pattern="dd.MM.yyyy HH:mm" value="${offer.moderationStartDate}" />
                            </p>
                            <c:if test="${not empty offer.description}">
                                <p class="description"><c:out value="${offer.description}" /></p>
                            </c:if>
                            <p class="price">
                                <spring:message code="currency.${offer.currency}.format" arguments="${pfn:formatPrice(offer.price, false)}" argumentSeparator="|" />
                                за 
                                <%-- проверка на неравенство единице(!= 1) не работает, приходится изголяться --%>
                                <c:set var="showRatio" value="${offer.ratio > 1 || offer.ratio < 1}" />
                                <c:if test="${showRatio}"><fmt:formatNumber value="${offer.ratio}" maxFractionDigits="2" />&nbsp;</c:if>
                                <abbr title="<c:out value="${unit.name}" />">${fn2:htmlformula(unit.abbreviation)}</abbr>
                                у <c:out value="${companyMap[offer.companyId].name}" />
                                &nbsp;<a title="Просмотреть поставщика в каталоге" href="<spring:url value="/company/{id}">
                                             <spring:param name="id" value="${offer.companyId}" />
                                         </spring:url>">&#8663</a>
                            </p>
                            <p class="status">
                                <c:choose>
                                    <c:when test="${offer.status == 'APPROVED'}">
                                        <span class="status-approved">Проверено</span>
                                        <c:if test="${not empty offer.moderationEndDate}">
                                            <fmt:formatDate pattern="dd.MM.yyyy" value="${offer.moderationEndDate}" />
                                        </c:if>
                                    </c:when>
                                    <c:when test="${offer.status == 'PENDING'}">
                                        <span class="status-pending">Ожидает проверки</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="status-rejected">Отклонено</span>
                                        <c:if test="${not empty offer.moderationEndDate}">
                                            <fmt:formatDate pattern="dd.MM.yyyy " value="${offer.moderationEndDate}" />
                                        </c:if>
                                        <c:forEach var="reason" items="${offer.rejectionList}" varStatus="reasonSt">
                                            <spring:message code="ui.offer.rejection.${reason}" /><c:if test="${not reasonSt.last}">, </c:if>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </p>
                            <ul class="toolbox">
                                <c:if test="${offer.status != 'APPROVED'}">
                                    <li><a class="approve" href="<spring:url value="/secured/manager/moderator/offer/decision/{id}" htmlEscape="true">
                                               <spring:param name="id" value="${offer.id}" />
                                               <spring:param name="action" value="approve" />
                                               <spring:param name="redirect" value="${redirectUrlValue}" />
                                           </spring:url>">Одобрить</a>
                                    </li>
                                </c:if>
                                <c:if test="${offer.status == 'APPROVED'}">
                                    <li class="disabled">Одобрить</li>
                                </c:if>
                                <c:if test="${offer.status != 'REJECTED'}">
                                    <li><a class="reject" href="<spring:url value="/secured/manager/moderator/offer/decision/{id}" htmlEscape="true">
                                               <spring:param name="id" value="${offer.id}" />
                                               <spring:param name="action" value="reject" />
                                               <spring:param name="redirect" value="${redirectUrlValue}" />
                                           </spring:url>">Отклонить</a>
                                    </li>
                                </c:if>
                                <c:if test="${offer.status == 'REJECTED'}">
                                    <li class="disabled">Отклонить</li>
                                </c:if>
                            </ul>
                        </div>
                    </div>
                    <c:if test="${st.last}">
                        <a class="forward" href="<spring:url value="/secured/manager/moderator/offer/{id}" htmlEscape="true">
                               <spring:param name="id" value="${category.id}" />
                               <c:if test="${param.all}"><spring:param name="all" value="${true}" /></c:if>
                               <spring:param name="startDate" value="${offer.moderationStartDate.time}" />
                               <spring:param name="startId" value="${offer.id}" />
                               <spring:param name="forward" value="${true}" />
                           </spring:url>">Вперед</a>&nbsp;&rarr;
                    </c:if>
                </c:forEach>
            </div>
        </c:if>
    </c:if>
</div>
<div class="col2">
    <tiles:insertDefinition name="common-tree">
        <tiles:putAttribute name="nodeList" value="${catalogTree}" />
        <tiles:putAttribute name="treeClass" value="manager-catalog-tree" />
        <tiles:putAttribute name="infoClass" value="pending-offer-count" />
    </tiles:insertDefinition>
</div>

<script type="text/javascript">
    // <![CDATA[
    $(document).ready(function() {
        /*
        $("a.approve").click(function() {
            $.post(getUrl($(this)), {action: "approve"}, function(d) {
                console.log(d);
            });
            return false;
        });
         */
       
        // TODO: сделать всплывающий список для отклонения по-месту
        /*
        $("a.reject").click(function() {
            return false;
        });
         */
        function getUrl(e) {
            var url = e.attr("href");
            return url.substr(0, url.indexOf("?"));
        }
    });
    // ]]>
</script>
