<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Удаление группы параметров категории «<c:out value="${categoryProperties.item.name}" />»</h2>
    <h3><c:out value="${paramGroupProperties.paramGroup.name}" /></h3>
    <c:if test="${paramGroupProperties.canDelete}">
        <form class="basic" method="POST">
            <div class="field">
                <label>
                    Удалить группу параметров категории?
                </label>
                <input type="submit" value="Удалить" />
            </div>
        </form>
    </c:if>
    <c:if test="${not paramGroupProperties.canDelete}">
        <p>
               Группа параметров категории не может быть удалена!
        </p>
        <p>
               Возможные причины:
        </p>
        <ul>
            <li>В группе параметров категории имеются параметры.</li>
        </ul>
    </c:if>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>