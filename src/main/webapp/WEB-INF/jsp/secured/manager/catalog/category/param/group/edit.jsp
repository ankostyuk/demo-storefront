<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Редактирование группы параметров категории «<c:out value="${categoryProperties.item.name}" />»</h2>
    <h3><c:out value="${paramGroupProperties.paramGroup.name}" /></h3>
    <form:form cssClass="basic" modelAttribute="paramGroupProperties">
        <div class="field">
            <form:label path="paramGroup.name">Наименование</form:label>
            <form:input cssClass="text long" path="paramGroup.name" />
            <p class="hint">
                <form:errors cssClass="error" path="paramGroup.name" />
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Сохранить группу параметров" />
        </div>
        <div class="action-set">
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/group/edit/order/{id}'><spring:param name='id' value='${paramGroupProperties.paramGroup.id}' /></spring:url>">Изменить порядок группы параметров</a>
            </p>
        </div>
        <div class="action-set">
            <p>
                <a class="action <c:if test="${paramGroupProperties.canDelete}">enabled</c:if><c:if test="${not paramGroupProperties.canDelete}">disabled</c:if>" href="<spring:url value="/secured/manager/catalog/category/param/group/delete/{id}"><spring:param name='id' value='${paramGroupProperties.paramGroup.id}' /></spring:url>">Удалить группу параметров</a>
            </p>
        </div>
    </form:form>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>