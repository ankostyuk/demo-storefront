<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col1">
    <c:set var="newTerm" value="${empty term.id}" />

    <c:if test="${newTerm}">
        <h1>Добавление термина</h1>
    </c:if>
    <c:if test="${not newTerm}">
        <h1>Редактирование термина</h1>
    </c:if>

    <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
    <form:form cssClass="basic" modelAttribute="term">
        <div class="field">
            <form:label path="name">Наименование<span class="mandatory">*</span></form:label>
            <form:input cssClass="text long" path="name" />
            <p class="hint">
                <form:errors cssClass="error" path="name" />
            </p>
        </div>
        <div class="field">
            <form:label path="description">Описание<span class="mandatory">*</span></form:label>
            <form:textarea rows="10" cols="20" cssClass="long" path="description" />
            <p class="hint">
                <spring:bind path="description">
                    <c:if test="${!status.error}">Укажите описание термина</c:if>
                </spring:bind>
                <form:errors cssClass="error" path="description" />
            </p>
        </div>
        <div class="field">
            <form:label path="source">Источник</form:label>
            <form:input cssClass="text long" path="source" />
            <p class="hint">
                <spring:bind path="source">
                    <c:if test="${!status.error}">Укажите источник информации, например <em>source.com</em></c:if>
                </spring:bind>
                <form:errors cssClass="error" path="source" />
            </p>
        </div>
        <div class="field">
            <c:if test="${newTerm}">
                <input type="submit" value="Добавить термин" />
            </c:if>
            <c:if test="${not newTerm}">
                <input type="submit" value="Сохранить термин" />
            </c:if>
        </div>
    </form:form>
</div>
<div class="col2">
    <c:if test="${not newTerm}">
        <ul>
            <li>
                <a href="<spring:url value='/secured/manager/term/delete/{id}'><spring:param name='id' value='${term.id}' /></spring:url>">Удалить термин</a>
            </li>
        </ul>
    </c:if>
</div>