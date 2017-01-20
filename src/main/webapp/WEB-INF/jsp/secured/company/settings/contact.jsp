<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div id="content-no-sidebar" class="company-settings-contact">
    <tiles:insertDefinition name="secured-company-settings-menu" />

    <h1>Контакты</h1>
    <c:if test="${empty contactList}">
        <p>
            Вы еще не указали контактные данные.
            Вы можете <a href="<spring:url value="/secured/company/settings/contact/add" />">сделать это прямо сейчас</a>.
        </p>
    </c:if>

    <c:if test="${not empty contactList}">
        <div class="contacts">
            <c:forEach var="contact" items="${contactList}">
                <c:if test="${previousType != contact.type}">
                    <c:if test="${not empty previousType}"></ul></c:if>
                    <c:set var="previousType" value="${contact.type}" />
                    <h3 class="contact-type"><spring:message code="ui.contact.type.${contact.type}" /></h3>
                    <ul class="contact ${fn:toLowerCase(contact.type)}">
                    </c:if>
                    <li>
                        <a class="" title="Редактировать" href="<spring:url value="/secured/company/settings/contact/edit/{id}">
                               <spring:param name="id" value="${contact.id}" />
                           </spring:url>">
                            <c:out value="${contact.value}" />
                        </a>
                        <c:if test="${not empty contact.label}">
                            - <span class="label"><c:out value="${contact.label}" /></span>
                        </c:if>

                        <span class="delete">(<a class="dce" href="<spring:url value='/secured/company/settings/contact/delete/{id}'>
                                                     <spring:param name='id' value='${contact.id}' />
                           </spring:url>">удалить</a>)</span>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <p class="contact-actions">
            <a class="composite new" href="<spring:url value="/secured/company/settings/contact/add" />"><span class="link">Добавить контакт</span></a>
        </p>
    </c:if>

</div><!-- #content-->