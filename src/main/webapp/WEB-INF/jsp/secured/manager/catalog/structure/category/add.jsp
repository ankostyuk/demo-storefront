<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Добавление категории каталога</h2>
    <c:if test="${canAdd}">
        <form:form cssClass="basic" modelAttribute="categoryProperties">
            <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
            <div class="field">
                <form:label path="item.name">Наименование<span class="mandatory">*</span></form:label>
                <form:input cssClass="text long" path="item.name" />
                <p class="hint">
                    <form:errors cssClass="error" path="item.name" />
                </p>
            </div>
            <div class="field">
                <form:label path="parentItemId">Родительский раздел<span class="mandatory">*</span></form:label>
                <form:select cssClass="long" path="parentItemId">
                    <spring:message var="selectSectionMessage" code="ui.catalog.structure.catalog.section.select" />
                    <form:option value="${null}" label="${selectSectionMessage}" />
                    <c:forEach var="parentItemOption" items="${parentItemSelectList}">
                        <!-- TODO fucking IE disabled -->
                        <option
                            value="<c:out value="${parentItemOption.value.item.id}" />"
                            style="padding-left: ${parentItemOption.value.level - 1}em;"
                            <c:if test="${categoryProperties.parentItemId == parentItemOption.value.item.id}">selected="selected"</c:if>
                            <c:if test="${not parentItemOption.value.canChoose}">disabled="disabled"</c:if>
                            ><c:out value="${parentItemOption.name}" /></option>
                    </c:forEach>
                </form:select>
                <p class="hint">
                    <spring:bind path="parentItemId">
                        <c:if test="${!status.error}">Укажите доступный раздел, внутри которого будет находится добавляемая категория</c:if>
                    </spring:bind>
                    <form:errors cssClass="error" path="parentItemId" />
                </p>
            </div>
            <div class="field">
                <form:label path="category.unitId">Единица измерения категории<span class="mandatory">*</span></form:label>
                <form:select cssClass="long" path="category.unitId">
                    <c:forEach var="unitOption" items="${unitSelectList}">
                        <c:if test="${unitOption.value == null}">
                            <c:set var="unitId" value="${unitOption.value}" />
                            <spring:message var="unitName" code="ui.catalog.category.unit.select" />
                        </c:if>
                        <c:if test="${unitOption.value != null}">
                            <c:set var="unitId" value="${unitOption.value}" />
                            <c:set var="unitName" value="${unitOption.name}" />
                        </c:if>
                        <form:option value="${unitId}" label="${unitName}" />
                    </c:forEach>
                </form:select>
                <p class="hint">
                    <form:errors cssClass="error" path="category.unitId" />
                </p>
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
                    Только активные категории публикуются в каталоге. Вы сможете изменить активность категории после добавления.
                </p>
            </div>
            --%>
            <div class="field">
                <input type="submit" value="Добавить категорию" />
            </div>
        </form:form>
    </c:if>
    <c:if test="${not canAdd}">
        <p>
            Категория каталога не может быть добавлена!
        </p>
        <p>
            Возможные причины:
        </p>
        <ul>
            <li>в каталоге товаров отсутствуют разделы — категория не может быть добавлена в корень каталога.</li>
        </ul>
    </c:if>
</div>
<div class="col2">

</div>