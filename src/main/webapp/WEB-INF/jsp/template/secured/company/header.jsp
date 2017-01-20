<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:useAttribute id="applicatonBuildMode" name="ui.application.build.mode" />
<c:if test="${applicatonBuildMode == 'DEMO'}">
    <tiles:insertTemplate template="/WEB-INF/jsp/template/common-demo-info.jsp" />
</c:if>

<div id="logo">
    <a href="<c:url value="/secured/company" />"><img alt="Бильдика" src="<cdn:url container="css" key="/i/company/logo.png" />" /></a>
</div>
<div id="outbox">
    <a href="<c:url value="/" />">Каталог</a>
</div>
<div id="searchbar">
    <span class="title">Поиск по моим предложениям</span>
    <form id="search-form" action="<c:url value="/secured/company/search" />" method="get">
        <div>
            <div class="submit-box"><input class="search-submit" type="submit" value="Найти" /></div>
            <div class="input-box"><input id="search-suggest" class="text query" type="text" name="text" /></div>
        </div>
    </form>
</div>
<div id="company-navbar">
    <ul class="menu">
        <tiles:useAttribute name="pageKey" ignore="true" />
        <li <c:if test="${fn:startsWith(pageKey, 'offers')}">class="current"</c:if>>
            <c:if test="${pageKey != 'offers'}"><a class="act" href="<c:url value="/secured/company/offers" />"></c:if>Мои предложения<c:if test="${pageKey != 'offers'}"></a></c:if>
        </li>

        <li <c:if test="${fn:startsWith(pageKey, 'settings')}">class="current"</c:if>>
            <c:if test="${pageKey != 'settings'}"><a class="act" href="<c:url value="/secured/company/settings" />"></c:if>Настройки<c:if test="${pageKey != 'settings'}"></a></c:if>
        </li>
    </ul>

    <ul class="loginbar">
        <li><span class="login"><sec:authentication property="principal.email" /></span></li>
        <li><a class="dce" href="<c:url value="/logout"/>">Выйти</a></li>
    </ul>
</div>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        jsHelper.buildSearchSuggest({
            inputSelector: "#search-suggest",
            searchUrl: "<c:url value='/secured/company/search/suggest' />",
            formSelector: "#search-form",
            contextUrl: "<c:url value='/' />"
        });
    });
    // ]]>
</script>
