<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<div id="header" class="manager">
    <div id="logo">
        <a href="<c:url value="/"/>">Бильдика</a>
    </div>
    <div id="user-box">
        <ul>
            <li><sec:authentication property="principal.email" /></li>
            <li><a href="<c:url value="/logout"/>">Выйти</a></li>
        </ul>
    </div>

    <div id="nav">
        <tiles:useAttribute name="key" />
        <ul>
            <sec:authorize ifAnyGranted="ROLE_MANAGER_CATALOG">
                <li <c:if test="${key == 'catalog'}">class="active"</c:if>><a href="<c:url value="/secured/manager/catalog"/>">Каталог</a></li>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_MANAGER_MODEL">
                <li <c:if test="${key == 'model'}">class="active"</c:if>><a href="<c:url value="/secured/manager/model"/>">Модели</a></li>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_MANAGER_BRAND">
                <li <c:if test="${key == 'brand'}">class="active"</c:if>><a href="<c:url value="/secured/manager/brand"/>">Бренды</a></li>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_MANAGER_MODERATOR_OFFER">
                <li <c:if test="${key == 'moderator_offer'}">class="active"</c:if>><a href="<c:url value="/secured/manager/moderator/offer"/>">Модерация предложений</a></li>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_MANAGER_DICTIONARY">
                <li <c:if test="${key == 'term'}">class="active"</c:if>><a href="<c:url value="/secured/manager/term"/>">Словарь терминов</a></li>
            </sec:authorize>
            <sec:authorize ifAnyGranted="ROLE_MANAGER_COMPANY_LOGIN">
                <li <c:if test="${key == 'company'}">class="active"</c:if>><a href="<c:url value="/secured/manager/company"/>">Поставщики</a></li>
            </sec:authorize>
        </ul>
    </div>

</div>
