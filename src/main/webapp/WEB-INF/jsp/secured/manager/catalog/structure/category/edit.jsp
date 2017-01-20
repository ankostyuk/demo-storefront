<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Редактирование категории каталога</h2>
    <h3><c:if test="${categoryProperties.paramCategory}"><span class="info"><span>&#167;&nbsp;</span></span></c:if><c:out value="${categoryProperties.item.name}" /><span>&nbsp;(<c:out value="${categoryProperties.offerCount}" />)</span><span class="tool"><a class="enabled" title="Просмотреть категорию в каталоге" href="<spring:url value='/category/{id}'><spring:param name='id' value='${categoryProperties.item.id}' /></spring:url>">&nbsp;&#8663;&nbsp;</a></span></h3>
    <form:form cssClass="basic" modelAttribute="categoryProperties">
        <div class="field">
            <form:label path="item.name">Наименование</form:label>
            <form:input cssClass="text long" path="item.name" />
            <p class="hint">
                <form:errors cssClass="error" path="item.name" />
            </p>
        </div>
        <div class="field">
            <form:label path="category.unitId">Единица измерения категории</form:label>
            <form:select cssClass="long" path="category.unitId">
                <c:forEach var="unitOption" items="${unitSelectList}">
                    <%-- TODO: @Deprecated не может быть null при редактировании --%>
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
            <c:if test="${not categoryProperties.canActive}">
                <p class="hint">
                    Активность категории не может быть изменена. Сделайте активным родительский раздел и попробуйте снова.
                </p>
            </c:if>
            <label>
                <form:checkbox path="item.active" disabled="${!categoryProperties.canActive}" />
                Активность
            </label>
            <p class="hint">
                Только активные категории публикуются в каталоге
            </p>
        </div>
        <div class="field">
            <form:label path="item.theme">Визуальная тема</form:label>
            <form:input cssClass="text long"  path="item.theme" />
            <p class="hint">
                <form:errors cssClass="error" path="item.theme" />
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Сохранить категорию" />
        </div>
        <div class="action-set">
            <c:if test="${categoryProperties.paramCategory}">
                <p>
                    <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/{id}'><spring:param name="id" value='${categoryProperties.item.id}' /></spring:url>">Параметры категории</a>
                </p>
            </c:if>
            <c:if test="${not categoryProperties.paramCategory}">
                <p>
                    <a class="action enabled" href="<spring:url value='/secured/manager/catalog/category/param/group/add/{id}'><spring:param name="id" value='${categoryProperties.item.id}' /></spring:url>">Сформировать параметры категории</a>
                </p>
            </c:if>
        </div>
        <div class="action-set">
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure/item/edit/order/{id}'><spring:param name='id' value='${categoryProperties.item.id}' /></spring:url>">Изменить порядок категории</a>
            </p>
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure/category/edit/section/{id}'><spring:param name='id' value='${categoryProperties.item.id}' /></spring:url>">Переместить категорию каталога в другой раздел</a>
            </p>
        </div>
        <div class="action-set">
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure/category/copy/{id}'><spring:param name='id' value='${categoryProperties.item.id}' /></spring:url>">Копировать категорию</a>
            </p>
        </div>
        <div class="action-set">
            <p>
                <a class="action <c:if test="${categoryProperties.canDelete}">enabled</c:if><c:if test="${not categoryProperties.canDelete}">disabled</c:if>" href="<spring:url value="/secured/manager/catalog/structure/category/delete/{id}"><spring:param name='id' value='${categoryProperties.item.id}' /></spring:url>">Удалить категорию</a>
            </p>
        </div>
    </form:form>
</div>
<div class="col2">

</div>