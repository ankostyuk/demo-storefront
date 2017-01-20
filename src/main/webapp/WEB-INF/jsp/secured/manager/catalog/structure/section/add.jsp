<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Добавление раздела каталога</h2>
    <form:form cssClass="basic" modelAttribute="sectionProperties">
        <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
        <div class="field">
            <form:label path="item.name">Наименование<span class="mandatory">*</span></form:label>
            <form:input cssClass="text long" path="item.name" />
            <p class="hint">
                <form:errors cssClass="error" path="item.name" />
            </p>
        </div>
        <div class="field">
            <form:label path="parentItemId">Родительский раздел</form:label>
            <form:select cssClass="long" path="parentItemId">
                <c:forEach var="parentItemProperties" items="${parentItemList}">
                    <!-- TODO fucking IE disabled -->
                    <option
                        value="<c:out value="${parentItemProperties.item.id}" />"
                        style="padding-left: ${parentItemProperties.level - 1}em;"
                        <c:if test="${sectionProperties.parentItemId == parentItemProperties.item.id}">selected="selected"</c:if>
                        <c:if test="${not parentItemProperties.canChoose}">disabled="disabled"</c:if>
                        ><c:out value="${parentItemProperties.item.name}" /></option>
                </c:forEach>
            </form:select>
        </div>
        <div class="field">
            <form:label path="item.theme">Визуальная тема</form:label>
            <form:input cssClass="text long"  path="item.theme" />
            <p class="hint">
                <form:errors cssClass="error" path="item.theme" />
            </p>
        </div>
        <%--
        <div class="field">
            <label>
                <form:checkbox path="item.active" />
                Активность
            </label>
            <p class="hint">
                Только активные разделы публикуются в каталоге. Вы сможете изменить активность раздела после добавления.
            </p>
        </div>
        --%>
        <div class="field">
            <input type="submit" value="Добавить раздел" />
        </div>
    </form:form>
</div>
<div class="col2">

</div>