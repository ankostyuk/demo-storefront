<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h1>Удаление термина</h1>
    <h3><c:out value="${term.name}" /></h3>
    <form class="basic" method="POST">
        <div class="field">
            <label>
                Удалить термин?
            </label>
            <input type="submit" value="Удалить" />
        </div>
    </form>
</div>
<div class="col2">
</div>