<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Добавление группы параметров категории «<c:out value="${categoryProperties.item.name}" />»</h2>
    <form:form cssClass="basic" modelAttribute="paramGroupProperties">
        <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
        <div class="field">
            <form:label path="paramGroup.name">Наименование<span class="mandatory">*</span></form:label>
            <form:input cssClass="text long" path="paramGroup.name" />
            <p class="hint">
                <form:errors cssClass="error" path="paramGroup.name" />
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Добавить группу параметров" />
        </div>
    </form:form>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>