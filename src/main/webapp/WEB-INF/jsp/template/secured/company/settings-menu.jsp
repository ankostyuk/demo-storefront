<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:useAttribute id="applicatonBuildMode" name="ui.application.build.mode" />
<tiles:useAttribute name="pageKey" />

<div class="settings-menu">
    <ul class="h-list items">
        <li>
            <span <c:if test="${pageKey == 'settings'}">class="current"</c:if>>
                <c:if test="${pageKey != 'settings'}"><a href="<spring:url value="/secured/company/settings" />"></c:if>Общие настройки<c:if test="${pageKey != 'settings'}"></a></c:if>
            </span>
        </li>
        <li>
            <span <c:if test="${fn:startsWith(pageKey, 'settings/contact')}">class="current"</c:if>>
                <c:if test="${pageKey != 'settings/contact'}"><a href="<spring:url value="/secured/company/settings/contact" />"></c:if>Контакты<c:if test="${pageKey != 'settings/contact'}"></a></c:if>
            </span>
        </li>
        <li>
            <span <c:if test="${fn:startsWith(pageKey, 'settings/delivery')}">class="current"</c:if>>
                <c:if test="${pageKey != 'settings/delivery'}"><a href="<spring:url value="/secured/company/settings/delivery" />"></c:if>Условия доставки<c:if test="${pageKey != 'settings/delivery'}"></a></c:if>
            </span>
        </li>
        <li>
            <span <c:if test="${pageKey == 'settings/currency'}">class="current"</c:if>>
                <c:if test="${pageKey != 'settings/currency'}"><a href="<spring:url value="/secured/company/settings/currency" />"></c:if>Курсы валют<c:if test="${pageKey != 'settings/currency'}"></a></c:if>
            </span>
        </li>
        <c:if test="${applicatonBuildMode != 'DEMO'}">
            <li>
                <span <c:if test="${pageKey == 'settings/password'}">class="current"</c:if>>
                    <c:if test="${pageKey != 'settings/password'}"><a href="<spring:url value="/secured/company/settings/password" />"></c:if>Изменение пароля<c:if test="${pageKey != 'settings/password'}"></a></c:if>
                </span>
            </li>
        </c:if>
    </ul>
</div>
