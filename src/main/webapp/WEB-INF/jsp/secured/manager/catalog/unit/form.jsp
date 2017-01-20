<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <c:set var="newUnit" value="${empty unit.id}" />

    <c:if test="${newUnit}">
        <h1>Добавление единицы измерения</h1>
    </c:if>
    <c:if test="${not newUnit}">
        <h1>Редактирование единицы измерения «<c:out value="${unit.name}" />»</h1>
    </c:if>

    <form:form cssClass="basic" modelAttribute="unit">
        <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
        <div class="field">
            <form:label path="name">Наименование<span class="mandatory">*</span></form:label>
            <form:input cssClass="text medium" path="name" />
            <p class="hint">
                <form:errors cssClass="error" path="name" />
            </p>
        </div>
        <div class="field">
            <form:label path="abbreviation">Сокращенное наименование</form:label>
            <form:input cssClass="text medium" path="abbreviation" />
            <p class="hint">
                <form:errors cssClass="error" path="abbreviation" />
            </p>
        </div>
        <div class="field">
            <c:if test="${newUnit}">
                <input type="submit" value="Добавить единицу измерения" />
            </c:if>
            <c:if test="${not newUnit}">
                <input type="submit" value="Сохранить единицу измерения" />
            </c:if>
        </div>
        <c:if test="${not newUnit}">
            <div class="action-set">
                <p>
                    <a class="action <c:if test="${canDelete}">enabled</c:if><c:if test="${not canDelete}">disabled</c:if>" href="<spring:url value="/secured/manager/catalog/unit/delete/{id}"><spring:param name='id' value='${unit.id}' /></spring:url>">Удалить единицу измерения</a>
                </p>
            </div>
        </c:if>
    </form:form>
</div>
<div class="col2">
    <p>
        В сокращенном наименовании поддерживаются управляющие символы
    </p>
    <ul>
        <li>возведение в степень: a<strong>^{2}</strong> = a<sup>2</sup></li>
        <li>нижний индекс: Z<strong>_{min}</strong> = Z<sub>min</sub></li>
        <li>знак умножения: a<strong>*</strong>b = a&times;b</li>
    </ul>
    <p>
        Список часто используемых символов
    </p>
    <ul>
        <li>знак градуса: &deg;</li>
        <li>знак умножения: &times;</li>
    </ul>
</div>