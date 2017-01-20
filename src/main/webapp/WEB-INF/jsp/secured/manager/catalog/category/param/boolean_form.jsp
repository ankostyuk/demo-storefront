<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<div class="col1">
    <c:if test="${newParam}">
        <h2>Добавление логического параметра категории «<c:out value="${catalogItem.name}" />» в группу «<c:out value="${paramGroup.name}" />»</h2>
    </c:if>
    <c:if test="${not newParam}">
        <h2>Редактирование логического параметра категории «<c:out value="${catalogItem.name}" />» в группе «<c:out value="${paramGroup.name}" />»</h2>
    </c:if>

    <form:form cssClass="basic" modelAttribute="param">

        <tiles:insertDefinition name="param-common-form" />

        <div class="field-separator"></div>
        <div>
            <div class="field">
                <form:label path="trueName">Наименование значения «истина»<span class="mandatory">*</span></form:label>
                <form:input cssClass="text medium" path="trueName" />
                <p class="hint">
                    <form:errors cssClass="error" path="trueName" />
                </p>
            </div>
            <div class="field">
                <form:label path="falseName">Наименование значения «ложь»<span class="mandatory">*</span></form:label>
                <form:input cssClass="text medium" path="falseName" />
                <p class="hint">
                    <form:errors cssClass="error" path="falseName" />
                </p>
            </div>
        </div>

        <div class="field">
            <c:if test="${newParam}">
                <input type="submit" value="Добавить логический параметр" />
            </c:if>
            <c:if test="${not newParam}">
                <input type="submit" value="Сохранить логический параметр" />
            </c:if>
        </div>
    </form:form>

    <c:if test="${not newParam}">
        <tiles:insertDefinition name="param-common-actions" />
    </c:if>
</div>
<div class="col2">

</div>