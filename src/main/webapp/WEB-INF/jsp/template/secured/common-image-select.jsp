<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<%-- Наименование поля выбора изображения (input name)--%>
<tiles:importAttribute name="paramName" />
<%-- Заголовок поля выбора изображения --%>
<tiles:importAttribute name="label" ignore="true" />
<%-- Флаг необходимости выбрать изображение заново --%>
<tiles:importAttribute name="reselect" ignore="true" />
<%-- Ключ изображения --%>
<tiles:importAttribute name="key" ignore="true" />
<%-- Контейнер изображения --%>
<tiles:importAttribute name="container" ignore="true" />
<%-- Наименование поля удаления изображения --%>
<tiles:importAttribute name="deleteParamName" ignore="true" />
<%-- Флаг отображения изображения за границами контрола --%>
<tiles:importAttribute name="overflow" ignore="true" />

<c:if test="${empty deleteParamName}">
    <c:set var="deleteParamName" value="delete${fn:toUpperCase(fn:substring(paramName, 0, 1))}${fn:substring(paramName, 1, fn:length(paramName))}" />
</c:if>

<div class="image-field">
    <div class="field-box">
        <c:if test="${not empty label}">
            <label class="text"><c:out value="${label}" /></label>
        </c:if>
        <c:if test="${empty label}">
            <label class="text">Изображение</label>
        </c:if>
        <input name="${paramName}" class="file" type="file" />
        <p class="hint">
            <c:if test="${reselect}"><span class="reselect">Выберите изображение заново. Максимальный размер&nbsp;&mdash; 3&nbsp;МБ</span></c:if>
            <c:if test="${not reselect}">Выберите изображение размером не более 3&nbsp;МБ</c:if>
        </p>
        <c:if test="${not empty key}">
            <label class="checkbox delete">
                <input class="checkbox" type="checkbox" name="${deleteParamName}" />
                Удалить изображение
            </label>
        </c:if>
    </div>
    <c:if test="${not empty key}">
        <div class="image-box <c:if test="${overflow}">overflow</c:if>">
            <img src="<cdn:url container="${container}" key="${key}" />" alt="" />
        </div>
    </c:if>
</div>

