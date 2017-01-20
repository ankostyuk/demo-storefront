<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>

<tiles:useAttribute name="_catalogTree" />
<tiles:useAttribute name="_currentPath" ignore="true" />

<div class="catalog-tree-box">
    <%--<div class="category-suggest-box"><input class="category-suggest" name="categorySuggest" type="text" /></div>--%>
    <tiles:insertDefinition name="common-tree">
        <tiles:putAttribute name="nodeList" value="${_catalogTree}" />
        <tiles:putAttribute name="treeClass" value="v-list items" />
        <tiles:putAttribute name="nameClass" value="ci-name" />
        <tiles:putAttribute name="infoClass" value="offer-count-inline" />
        <tiles:putAttribute name="idPrefix" value="ci" />
    </tiles:insertDefinition>
</div>

<c:if test="${_currentPath != null}">
    <c:set var="_initiallyOpenIds">
        <c:forEach var="item" items="${_currentPath}" varStatus="st">"<c:out value='ci${item.id}' />"<c:if test="${not st.last}">,</c:if></c:forEach>
    </c:set>
</c:if>
        
<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        jsHelper.buildCollapsableTree({
            treeSelector: "div.catalog-tree-box > ul",
            treeClass: "tree collapsable",
            initiallyOpenIds: [${_initiallyOpenIds}],
            stateToCookie: true,
            stateFromCookie: true,
            cookieName: "company-catalog-tree-expanded",
            cookieOptions: {
                path: "<c:url value="/secured/company"/>"
            }
        });

        <%-- Подсказчик категорий --%>
        jsHelper.buildСategorySuggest({
            afterSelector: "div.catalog-tree-box > ul",
            categoryNodeSelector: "div.catalog-tree-box ul ul li:not(:has(ul))",
            catalogItemNameSelector: "span.ci-name:first",
            parentNodeSelector: "div.catalog-tree-box li",
            cache: true,
            onSelected: function(nodeProperties) {
                var el = nodeProperties.node.find("> span");
                if (el.hasClass("current")) {
                    $(this).effect("transfer", {to: "div.offers-top-box .catalog-item-path span.last", className: "category-current-effect-transfer"}, jsHelper.transferEffectSpeed);
                } else {
                    window.location = el.find("a").attr("href");
                }
            }
        });
    });
    // ]]>
</script>
