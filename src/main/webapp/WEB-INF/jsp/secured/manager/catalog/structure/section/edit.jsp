<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Редактирование раздела каталога</h2>
    <h3><c:out value="${sectionProperties.item.name}" /><span>&nbsp;(<c:out value="${sectionProperties.offerCount}" />)</span><span class="tool"><a class="enabled" title="Просмотреть раздел в каталоге" href="<spring:url value='/section/{id}'><spring:param name='id' value='${sectionProperties.item.id}' /></spring:url>">&nbsp;&#8663;&nbsp;</a></span></h3>
    <form:form cssClass="basic" modelAttribute="sectionProperties">
        <div class="field">
            <form:label path="item.name">Наименование</form:label>
            <form:input cssClass="text long" path="item.name" />
            <p class="hint">
                <form:errors cssClass="error" path="item.name" />
            </p>
        </div>
        <div class="field">
            <c:if test="${not sectionProperties.canActive}">
                <p class="hint">
                    Активность раздела не может быть изменена. Сделайте активным родительский раздел и попробуйте снова.
                </p>
            </c:if>
            <label>
                <form:checkbox path="item.active" disabled="${!sectionProperties.canActive}" />
                Активность
            </label>
            <p class="hint">
                Только активные разделы публикуются в каталоге
            </p>
            <c:if test="${sectionProperties.canActive}">
                <p class="hint">
                    Все подразделы и подкатегории раздела унаследуют состояние активности данного раздела
                </p>
            </c:if>
        </div>
        <div class="field">
            <form:label path="item.theme">Визуальная тема</form:label>
            <form:input cssClass="text long"  path="item.theme" />
            <p class="hint">
                <form:errors cssClass="error" path="item.theme" />
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Сохранить раздел" />
        </div>

        <div class="action-set">
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure/section/add'><spring:param name="parentId" value='${sectionProperties.item.id}' /></spring:url>">Добавить раздел</a>
            </p>
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure/category/add'><spring:param name="parentId" value="${sectionProperties.item.id}" /></spring:url>">Добавить категорию</a>
            </p>
        </div>
        <div class="action-set">
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure/item/edit/order/{id}'><spring:param name='id' value='${sectionProperties.item.id}' /></spring:url>">Изменить порядок раздела</a>
            </p>
            <p>
                <a class="action enabled" href="<spring:url value='/secured/manager/catalog/structure/section/edit/section/{id}'><spring:param name='id' value='${sectionProperties.item.id}' /></spring:url>">Переместить раздел каталога в другой раздел</a>
            </p>
        </div>
        <div class="action-set">
            <p>
                <a class="action <c:if test="${sectionProperties.canDelete}">enabled</c:if><c:if test="${not sectionProperties.canDelete}">disabled</c:if>" href="<spring:url value="/secured/manager/catalog/structure/section/delete/{id}"><spring:param name='id' value='${sectionProperties.item.id}' /></spring:url>">Удалить раздел</a>
            </p>
        </div>
    </form:form>
</div>
<div class="col2">

</div>