<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<div class="col1">
    <h2>Удаление варианта выбора</h2>
    <c:if test="${canDelete}">
        <p>Удалить вариант выбора «<c:out value="${paramSelectOption.name}" />»?</p>
        <form class="basic" action="<spring:url value="/secured/manager/catalog/category/param/select/option/delete/{id}">
                  <spring:param name="id" value="${paramSelectOption.id}" />
              </spring:url>" method="post">
            <div class="field">
                <input type="submit" value="Удалить" />
            </div>
        </form>
    </c:if>
    <c:if test="${not canDelete}">
        <p>
            Вариант выбора не может быть удален, т.к. является единственным вариантом выбора параметра.
        </p>
    </c:if>
</div>
<div class="col2">

</div>