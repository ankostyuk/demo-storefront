<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="usf" uri="http://www.nullpointer.ru/usf/tags" %>

<tiles:useAttribute name="_categoryId" ignore="true" />
<tiles:useAttribute name="_comparisonList" ignore="true" />
<tiles:useAttribute name="_userSession" ignore="true" />
<tiles:useAttribute name="_redirect" ignore="true" />

<div class="comparison-list">
    <h3><span>В списке сравнения</span></h3>
    <div class="content-box">
        <ul class="v-list inside">
            <c:forEach var="match" items="${_comparisonList}">
                <li>
                    <c:if test="${match.type == 'OFFER'}">
                        <a href="<spring:url value='/offer/{id}'>
                               <spring:param name='id' value='${match.offer.id}' />
                           </spring:url>"><c:out value="${match.offer.name}" /></a>
                    </c:if>
                    <c:if test="${match.type == 'MODEL'}">
                        <a href="<spring:url value='/model/{id}'>
                               <spring:param name='id' value='${match.model.id}' />
                           </spring:url>"><c:out value="${match.model.name}" /></a>
                    </c:if>
                </li>
            </c:forEach>
        </ul>
        <div class="actions">
            <ul class="h-list">
                <c:if test="${fn:length(_comparisonList) > 1}">
                    <li>
                        <a class="act" rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison" htmlEscape="true">
                           <spring:param name="categoryId" value="${_categoryId}" />
                           <spring:param name="ids" value="${usf:getComparisonUrlParam(_userSession, _categoryId)}" />
                       </spring:url>">Сравнить</a>
                    </li>
                </c:if>
                <li>
                    <a class="dce comparison-clear" rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison/clear" htmlEscape="true">
                           <spring:param name="categoryId" value="${_categoryId}" />
                            <c:if test="${not empty _redirect}">
                               <spring:param name="redirect" value="${_redirect}" />
                            </c:if>
                       </spring:url>">Очистить</a>
                </li>
            </ul>
        </div>
    </div>
</div>
