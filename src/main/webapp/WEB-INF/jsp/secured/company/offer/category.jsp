<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar">

    <c:if test="${path != null}">
        <div class="catalog-item-path">
            <tiles:insertDefinition name="secured-company-catalog-item-path">
                <tiles:putAttribute name="_path" value="${path}" />
                <tiles:putAttribute name="_lastLink" value="${true}" />
            </tiles:insertDefinition>
        </div>
    </c:if>

    <h1>Добавление предложения</h1>
    <p>
        Выберите категорию для добавления предложения
    </p>

    <p class="info">
        Не нашли нужной категории? <a href="http://bildika.reformal.ru/">Напишите</a> нам, и мы обязательно добавим.
    </p>

    <form class="plain category-select" action="<spring:url value="/secured/company/offer/category" />" method="post">
        <c:if test="${categoryNotSelected}">
            <p class="message"><span class="form-error">Выберите категорию из списка</span></p>
        </c:if>

        <c:if test="${showCatalogTreeFilter}">
            <c:set var="baseUrlValue" value="/secured/company/offer/category" />
            <c:if test="${parentItem  != null}">
                <c:set var="baseUrlValue" value="${baseUrlValue}?id=${parentItem.id}" />
            </c:if>
            <tiles:insertDefinition name="secured-company-catalog-tree-filter">
                <tiles:putAttribute name="_baseUrlValue" value="${baseUrlValue}" />
                <tiles:putAttribute name="_catalog" value="${catalog}" />
            </tiles:insertDefinition>
        </c:if>

        <tiles:insertDefinition name="secured-company-category-select">
            <tiles:putAttribute name="_catalogTree" value="${catalogTree}" />
            <tiles:putAttribute name="_parentLevel" value="${fn:length(path)}" />
        </tiles:insertDefinition>

        <div class="submit">
            <c:if test="${parentItem  != null}"><input type="hidden" name="id" value="${parentItem.id}" /></c:if>
            <input type="hidden" name="cat" value="${catalog.alias}" />
            <input type="submit" value="Добавить предложение" />
        </div>
    </form>

</div><!-- #content-->
