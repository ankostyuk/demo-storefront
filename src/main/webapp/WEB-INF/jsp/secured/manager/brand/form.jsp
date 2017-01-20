<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col1">
    <c:set var="newBrand" value="${empty brand.id}" />

    <c:if test="${newBrand}">
        <h1>Добавление бренда</h1>
    </c:if>
    <c:if test="${not newBrand}">
        <h1>Редактирование бренда</h1>
    </c:if>

    <p>
        Внимание! Удалить бренд невозможно.
    </p>
    <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
    <form:form cssClass="basic" modelAttribute="brand" enctype="multipart/form-data">
        <div class="field">
            <form:label path="name">Наименование<span class="mandatory">*</span></form:label>
            <form:input cssClass="text long" path="name" />
            <p class="hint">
                <form:errors cssClass="error" path="name" />
            </p>
        </div>
        <div class="field">
            <form:label path="keywords">Ключевые слова</form:label>
            <form:input cssClass="text long" path="keywords" />
            <p class="hint">
                <spring:bind path="keywords">
                    <c:if test="${!status.error}">Укажите ключевые слова через пробел</c:if>
                </spring:bind>
                <form:errors cssClass="error" path="keywords" />
            </p>
        </div>
        <div class="field">
            <form:label path="site">Веб-сайт</form:label>
            <form:input cssClass="text long" path="site" />
            <p class="hint">
                <spring:bind path="site">
                    <c:if test="${!status.error}">Укажите адрес веб-сайта, например <em>http://www.example.com</em></c:if>
                </spring:bind>
                <form:errors cssClass="error" path="site" />
            </p>
        </div>
        <fieldset>
            <legend>Логотип</legend>
            <tiles:insertDefinition name="secured-common-image-select">
                <tiles:putAttribute name="paramName" value="logo" />
                <tiles:putAttribute name="reselect" value="${reselectLogo}" />
                <tiles:putAttribute name="key" value="${brand.logo}" />
                <tiles:putAttribute name="container" value="brand" />
                <tiles:putAttribute name="imageWidth" value="200" />
            </tiles:insertDefinition>
        </fieldset>
        <div class="field">
            <c:if test="${newBrand}">
                <input type="submit" value="Добавить бренд" />
            </c:if>
            <c:if test="${not newBrand}">
                <input type="submit" value="Сохранить бренд" />
            </c:if>
        </div>
    </form:form>
</div>
<div class="col2">
    <c:if test="${not newBrand and canDelete}">
        <ul>
            <li>
                <a href="<spring:url value='/secured/manager/brand/delete/{id}'><spring:param name='id' value='${brand.id}' />
                   </spring:url>">Удалить бренд</a>
            </li>
        </ul>
    </c:if>
</div>