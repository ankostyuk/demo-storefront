<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Удаление параметра категории «<c:out value="${catalogItem.name}" />» группы «<c:out value="${paramGroup.name}" />»</h2>
    <c:if test="${canDelete}">
        <p>
            Удалить параметр «<c:out value="${p.name}" />»?
        </p>
        <form class="basic" action="<spring:url value="/secured/manager/catalog/category/param/delete/{id}">
                  <spring:param name="id" value="${p.id}" />
              </spring:url>" method="post">
            <div class="field">
                <input type="submit" value="Удалить" />
            </div>
        </form>
    </c:if>
    <c:if test="${not canDelete}">
        <p>
            Параметр категории не может быть удален.
        </p>
    </c:if>
</div>
<div class="col2">

</div>