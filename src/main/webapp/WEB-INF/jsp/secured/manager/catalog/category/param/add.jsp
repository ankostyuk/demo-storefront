<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="col1">
    <h2>Добавление параметра категории «<c:out value="${catalogItem.name}" />» в группу «<c:out value="${paramGroup.name}" />»</h2>

    <div class="action-set no-border">
        <p>
            <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/boolean/add/{id}'><spring:param name="id" value='${paramGroup.id}' /></spring:url>">Добавить логический параметр</a>
        </p>
        <p>
            <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/number/add/{id}'><spring:param name="id" value='${paramGroup.id}' /></spring:url>">Добавить числовой параметр</a>
        </p>
        <p>
            <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/select/add/{id}'><spring:param name="id" value='${paramGroup.id}' /></spring:url>">Добавить выборочный параметр</a>
        </p>
    </div>

</div>
<div class="col2">

</div>