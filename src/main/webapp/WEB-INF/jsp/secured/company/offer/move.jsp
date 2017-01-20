<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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

    <h1>Перемещение предложения</h1>
    <p>
        Выберите категорию для перемещения предложения «<strong><c:out value="${offer.name}" /></strong>»
    </p>
    <p class="info">
        Не нашли нужной категории? <a href="http://bildika.reformal.ru/">Напишите</a> нам, и мы обязательно добавим.
    </p>
    <c:if test="${offer.parametrized || offer.modelLinked}">
        <p>
            Внимание! Дополнительные параметры предложения будут потеряны.
        </p>
    </c:if>
    <form class="plain category-select" action="<spring:url value="/secured/company/offer/move/{id}">
              <spring:param name="id" value="${offer.id}" />
          </spring:url>" method="post">
        <c:if test="${categoryNotSelected}">
            <p class="message"><span class="form-error">Выберите категорию из списка</span></p>
        </c:if>
        <c:if test="${categoryIsSame}">
            <p class="message"><span class="form-error">Выберите другую категорию. Предложение сейчас находится в категории «<c:out value="${currentItem.name}" />»</span></p>
        </c:if>

        <tiles:insertDefinition name="secured-company-catalog-tree-filter">
            <tiles:putAttribute name="_baseUrlValue" value="/secured/company/offer/move/${offer.id}" />
            <tiles:putAttribute name="_catalog" value="${catalog}" />
        </tiles:insertDefinition>

        <tiles:insertDefinition name="secured-company-category-select">
            <tiles:putAttribute name="_catalogTree" value="catalogTree" />
            <tiles:putAttribute name="_parentLevel" value="0" />
        </tiles:insertDefinition>

        <div class="submit">
            <input type="submit" value="Переместить предложение" />
            <input type="hidden" name="cat" value="${catalog.alias}" />
            <c:if test="${not empty param.redirect}">
                <input type="hidden" name="redirect" value="<c:out value="${param.redirect}" />" />
                или <a href="<spring:url value="${param.redirect}" htmlEscape="true" />">вернуться назад</a>
            </c:if>
        </div>
    </form>

</div><!-- #content-->
