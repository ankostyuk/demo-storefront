<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="usf" uri="http://www.nullpointer.ru/usf/tags" %>

<tiles:useAttribute name="_id" ignore="true" />
<tiles:useAttribute name="_categoryId" ignore="true" />
<tiles:useAttribute name="_isInComparison" ignore="true" />
<tiles:useAttribute name="_userSession" ignore="true" />
<tiles:useAttribute name="_redirect" ignore="true" />
<tiles:useAttribute name="_actionInfo" ignore="true" />

<c:if test="${empty _actionInfo}">
    <c:if test="${not _isInComparison}">
        <a class="act comparison-item-add" rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison/add" htmlEscape="true">
               <spring:param name="categoryId" value="${_categoryId}" />
               <spring:param name="id" value="${_id}" />
               <c:if test="${not empty _redirect}">
                   <spring:param name="redirect" value="${_redirect}" />
               </c:if>
           </spring:url>">К сравнению</a>
    </c:if>
    <c:if test="${_isInComparison}">
        <a class="comparison-added" rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison">
               <spring:param name="categoryId" value="${_categoryId}" />
               <spring:param name="ids" value="${usf:getComparisonUrlParam(_userSession, _categoryId)}" />
           </spring:url>">Уже в списке сравнения</a>
    </c:if>
</c:if>
<c:if test="${not empty _actionInfo}">
    <c:if test="${_actionInfo.type == 'MATCH_ADD_TO_COMPARISON'}">
        <c:if test="${_actionInfo.success}">
            <a class="comparison-added" rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison">
                   <spring:param name="categoryId" value="${_categoryId}" />
                   <spring:param name="ids" value="${usf:getComparisonUrlParam(_userSession, _categoryId)}" />
               </spring:url>">Уже в списке сравнения</a>
        </c:if>
        <c:if test="${not _actionInfo.success}">
            <div class="action-error-info">
                <p>
                    Невозможно добавить в <a rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison">
                           <spring:param name="categoryId" value="${_categoryId}" />
                           <spring:param name="ids" value="${usf:getComparisonUrlParam(_userSession, _categoryId)}" />
                       </spring:url>">список сравнения</a>, список сравнения переполнен.
                </p>
            </div>
        </c:if>
    </c:if>
    <c:if test="${_actionInfo.type == 'MATCH_DELETE_FROM_COMPARISON'}">
        <c:if test="${_actionInfo.success}">
            <a <c:if test="${not empty _actionInfo.redirect}">href="<spring:url value="${_actionInfo.redirect}" htmlEscape="true" />"</c:if> >&nbsp;</a>
        </c:if>
        <c:if test="${not _actionInfo.success}">
            <div class="action-error-info">
                <p>
                    Невозможно удалить из <a rel="nofollow" href="<spring:url value="/category/{categoryId}/comparison">
                           <spring:param name="categoryId" value="${_categoryId}" />
                           <spring:param name="ids" value="${usf:getComparisonUrlParam(_userSession, _categoryId)}" />
                       </spring:url>">списка сравнения</a>. Пожалуйста, попробуйте позже.
                </p>
            </div>
        </c:if>
    </c:if>
</c:if>
