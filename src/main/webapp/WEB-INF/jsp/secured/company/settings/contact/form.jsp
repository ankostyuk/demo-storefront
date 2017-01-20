<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar" class="company-settings-contact">
    <tiles:insertDefinition name="secured-company-settings-menu" />

    <c:set var="newContact" value="${empty contact.id}" />
    <c:if test="${newContact}">
        <h1>Добавление контакта</h1>
    </c:if>
    <c:if test="${not newContact}">
        <h1>Редактирование контакта</h1>
    </c:if>

    <form:form modelAttribute="contact">
        <ul class="field-group">
            <li>
                <form:label path="type" cssClass="text">Тип</form:label>
                <form:select path="type" cssClass="text">
                    <c:forEach var="contactType" items="${contactTypeValues}">
                        <c:set var="contactTypeLabel"><spring:message code="ui.contact.type.${contactType}" /></c:set>
                        <form:option label="${contactTypeLabel}" value="${contactType}" />
                    </c:forEach>
                </form:select>
                <form:errors cssClass="error" path="type" />
            </li>
            <li>
                <form:label path="value" cssClass="text">Значение</form:label>
                <form:input cssClass="text" path="value" />
                <form:errors cssClass="error" path="value" />
                <p class="hint">Например: +7 495 123-45-67 доб. 123</p>
            </li>
            <li>
                <form:label path="label" cssClass="text">Описание</form:label>
                <form:input cssClass="text" path="label" />
                <form:errors cssClass="error" path="label" />
                <p class="hint">Например: менеджер Алексей, отдел продаж</p>
            </li>
        </ul>
        <div class="submit">
            <c:if test="${newContact}">
                <input type="submit" value="Добавить контакт" />
            </c:if>
            <c:if test="${not newContact}">
                <input type="submit" value="Сохранить контакт" />
            </c:if>
        </div>
    </form:form>

</div><!-- #content-->
