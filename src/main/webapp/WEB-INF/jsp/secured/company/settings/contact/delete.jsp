<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar" class="company-settings-contact">
    <tiles:insertDefinition name="secured-company-settings-menu" />

    <h1>Удаление контакта</h1>
    <p>
        Вы уверены что хотите удалить контакт
        «<strong><c:out value="${contact.value}" /></strong><c:if test="${not empty contact.label}"> (<c:out value="${contact.label}" />)</c:if>»?
    </p>

    <form:form modelAttribute="contact" cssClass="plain">
        <div class="submit">
            <input type="submit" value="Удалить контакт" />
            или <a href="<spring:url value="/secured/company/settings/contact" htmlEscape="true" />">вернуться назад</a>
        </div>
    </form:form>

</div><!-- #content-->