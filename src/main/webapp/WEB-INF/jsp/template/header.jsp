<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<tiles:useAttribute id="applicatonBuildMode" name="ui.application.build.mode" />
<c:if test="${applicatonBuildMode == 'DEMO'}">
    <tiles:insertTemplate template="/WEB-INF/jsp/template/common-demo-info.jsp" />
</c:if>

<tiles:useAttribute name="pageKey" ignore="true" />

<c:if test="${pageKey == 'index' && applicatonBuildMode != 'DEMO'}">
    <p id="site-description">Поиск и выбор товаров для строительства и ремонта</p>
</c:if>

<div id="logo">
    <a <c:if test="${pageKey != 'index'}">href="<c:url value='/' />"</c:if>><img alt="Бильдика" src="<cdn:url container="css" key="${'/i/logo.png'}" />" /></a>
</div>
<div id="navbar">
    <ul class="h-list">
        <li>
            <c:if test="${pageKey != 'term'}">
                <a href="<c:url value='/term' />">Словарь терминов</a>
            </c:if>
            <c:if test="${pageKey == 'term'}">
                <span class="current">Словарь терминов</span>
            </c:if>
        </li>
        <li>
            <c:if test="${pageKey != 'contacts'}">
                <a href="<c:url value='/contacts' />">Контакты</a>
            </c:if>
            <c:if test="${pageKey == 'contacts'}">
                <span class="current">Контакты</span>
            </c:if>
        </li>
        <%-- TODO <li><a href="#">Блог</a></li>--%>
    </ul>
</div>
<div id="regbox">
    <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
        <c:if test="${not fn:startsWith(pageKey, 'registration/company')}">
            <a href="<c:url value="/registration/company/partner" />">Для компаний</a>
        </c:if>
        <c:if test="${fn:startsWith(pageKey, 'registration/company')}">
            <span class="current">Для компаний</span>
        </c:if>
    </sec:authorize>
    <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
        <a href="<c:url value='/secured/gateway' />">Личный кабинет</a>
    </sec:authorize>
</div>
<div id="searchbar">
    <form id="search-form" action="<c:url value='/search' />" method="get">
        <div>
            <div class="submit-box"><input class="search-submit" type="submit" value="Найти" /></div>
            <div class="input-box"><input id="search-suggest" class="text query" type="text" name="text" value="<c:out value="${not empty searchSettings && not empty searchSettings.text ? searchSettings.text : ''}" />"/></div>
        </div>
        <div class="options">
            <c:if test="${not empty searchSettings}">
                <c:if test="${not empty searchSettings.catalogItemId}">
                    <label>
                        <input class="checkbox" type="checkbox" name="item" value="<c:out value="${searchSettings.catalogItemId}" />" /><span class="text">искать только в данной категории</span>
                    </label>
                </c:if>
                <c:if test="${not empty searchSettings.example}">
                    <label class="example no-js">
                        <span class="text">Например, <a class="pseudo" href="#"><c:out value="${searchSettings.example}" /></a></span>
                    </label>
                </c:if>
            </c:if>
        </div>
    </form>
</div>
<div id="userbar">
    <ul class="h-list">
        <tiles:useAttribute id="regionName" name="ui.region.name" ignore="true" />
        <tiles:useAttribute id="regionAware" name="ui.region.aware" ignore="true" />
        <li><a id="region-set-link" class="composite act" rel="nofollow" href="<c:url value='/settings/region'/>"><span class="link">Регион</span><span class="extra">:</span></a>
            <c:if test="${not empty regionName}">
                <c:if test="${regionAware}">
                    <span class="region-name"><c:out value="${regionName}" /></span>
                </c:if>
                <c:if test="${not regionAware}">
                    <span class="region-name">все регионы</span>
                </c:if>
            </c:if>
            <c:if test="${empty regionName}">
                <span class="region-name empty">все регионы</span>
            </c:if>
        </li>
        <li <c:if test="${fn:startsWith(pageKey, 'cart')}">class="current"</c:if>>
            <c:if test="${pageKey != 'cart'}"><a class="act" rel="nofollow" href="<c:url value='/cart' />"></c:if>Список покупок<c:if test="${pageKey != 'cart'}"></a></c:if>
        </li>
        <li <c:if test="${fn:startsWith(pageKey, 'settings')}">class="current"</c:if>>
            <c:if test="${pageKey != 'settings'}"><a class="act" rel="nofollow" href="<c:url value='/settings' />"></c:if>Настройки<c:if test="${pageKey != 'settings'}"></a></c:if>
        </li>
        <sec:authorize ifAnyGranted="ROLE_ANONYMOUS">
            <li <c:if test="${fn:startsWith(pageKey, 'login')}">class="current"</c:if>>
                <c:if test="${pageKey != 'login'}"><a class="act" href="<c:url value='/login' />"></c:if>Вход<c:if test="${pageKey != 'login'}"></a></c:if>
            </li>
        </sec:authorize>
        <sec:authorize ifNotGranted="ROLE_ANONYMOUS">
            <li><span class="login"><sec:authentication property="principal.email" /></span></li>
            <li><a class="dce" href="<c:url value='/logout' />">Выйти</a></li>
        </sec:authorize>
    </ul>
</div>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        <%-- Блок поиска --%>
        $("#searchbar .options .example.no-js").removeClass("no-js");
        $("#searchbar .options .example a").click(function(e){
            e.preventDefault();
            $("#search-suggest").val($(this).text());
        });
        jsHelper.buildSearchSuggest({
            inputSelector: "#search-suggest",
            searchUrl: "<c:url value='/search/suggest' />",
            formSelector: "#search-form",
            contextUrl: "<c:url value='/' />"
        });
        <%-- Установить фокус на поле поиска --%>
        if (window.location.hash === "") {
            $("#search-suggest").focus();
        }

        <%-- Редактирование региона "по месту" --%>
        jsHelper.buildRegionInlineSet({
            pseudoLinkSingleSelector: "#region-set-link",
            linkText: "Регион",
            ignoreEscapeEventSelector: "#region-info-set-link",
            pseudoLinkClass: "pseudo",
            regionSuggestURL: "<c:url value='/region/suggest' />",
            postURL: "<c:url value='/settings/region/inline' />",
            regionName: "<c:out value='${regionName}' />",
            regionAware: <c:if test="${empty regionName || regionAware}">${'true'}</c:if><c:if test="${not empty regionName && not regionAware}">${'false'}</c:if>
        });
        $("#region-info-set-link").addClass("pseudo").click(function(e){
            e.preventDefault();
            $("#region-set-link").click();
        });
    });
    // ]]>
</script>
