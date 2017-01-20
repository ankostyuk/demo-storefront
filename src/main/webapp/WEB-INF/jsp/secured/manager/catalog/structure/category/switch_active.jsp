<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Изменение активности категории каталога</h2>
    <h3><c:out value="${categoryProperties.item.name}" /></h3>
    <c:if test="${categoryProperties.canActive}">
        <p>
            Только активные категории публикуются в каталоге
        </p>
        <form:form cssClass="basic" modelAttribute="sectionProperties">
            <div class="field">
                <label>
                    Изменить активность категории с <c:if test="${categoryProperties.item.active}">активной на неактивную</c:if><c:if test="${not categoryProperties.item.active}">неактивной на активную</c:if>?
                </label>
                <input type="submit" value="Изменить" />
            </div>
        </form:form>
    </c:if>
    <c:if test="${not categoryProperties.canActive}">
        <p>
            Активность категории не может быть изменена. Сделайте активным родительский раздел и попробуйте снова.
        </p>
    </c:if>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>