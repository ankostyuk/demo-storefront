<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Удаление единицы измерения категорий</h2>
    <c:if test="${canDelete}">
        <h3><c:out value="${unit.name}" /></h3>
        <form class="basic" method="POST">
            <div class="field">
                <label>
                    Удалить единицу измерения?
                </label>
                <input type="submit" value="Удалить" />
            </div>
        </form>
    </c:if>
    <c:if test="${not canDelete}">
        <p>
            Единица измерения «<c:out value="${unit.name}" />» не может быть удалена.
            Имеются категории в которых используется эта единица.
        </p>
    </c:if>
</div>
<div class="col2">

</div>