<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<div id="header" class="admin">
    <div id="logo">
        <a href="<c:url value='/'/>">Бильдика</a>
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
            <li <c:if test="${key == 'account/company'}">class="active"</c:if>><a href="<c:url value="/secured/admin/account/company"/>">Поставщики</a></li>
            <li <c:if test="${key == 'account/manager'}">class="active"</c:if>><a href="<c:url value="/secured/admin/account/manager"/>">Менеджеры</a></li>
            <li <c:if test="${key == 'account/admin'}">class="active"</c:if>><a href="<c:url value="/secured/admin/account/admin"/>">Администраторы</a></li>
            <li <c:if test="${key == 'statistics'}">class="active"</c:if>><a href="<c:url value="/secured/admin/statistics"/>">Статистика</a></li>
            <li <c:if test="${key == 'searchindexing'}">class="active"</c:if>><a href="<c:url value="/secured/admin/searchindexing"/>">Поисковая индексация</a></li>
        </ul>
    </div>
</div>
