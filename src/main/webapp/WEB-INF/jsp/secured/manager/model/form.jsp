<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<div class="col1">
    <c:set var="newModel" value="${empty model.id}" />

    <c:if test="${newModel}">
        <h1>Добавление модели в категорию «<c:out value="${catalogItem.name}" />»</h1>
    </c:if>
    <c:if test="${not newModel}">
        <h1>Редактирование модели</h1>
    </c:if>

    <p>Символом <span class="mandatory">*</span> отмечены поля, обязательные для заполнения.</p>
    <form:form cssClass="basic" modelAttribute="model" enctype="multipart/form-data">
        <div class="field">
            <label for="brandName">Бренд<span class="mandatory">*</span></label>
            <input id="brandName" name="brandName" type="text" class="text long" value="${brand.name}" />
            <form:hidden path="brandId" />
            <p class="hint">
                <form:errors cssClass="error" path="brandId" />
            </p>
        </div>
        <div class="field">
            <form:label path="name">Наименование<span class="mandatory">*</span></form:label>
            <form:input cssClass="text long" path="name" />
            <p class="hint">
                <form:errors cssClass="error" path="name" />
            </p>
        </div>
        <div class="field">
            <form:label path="vendorCode">Код производителя<span class="mandatory">*</span></form:label>
            <form:input cssClass="text long" path="vendorCode" />
            <p class="hint">
                <form:errors cssClass="error" path="vendorCode" />
            </p>
        </div>
        <div class="field">
            <form:label path="description">Описание</form:label>
            <form:textarea rows="4" cols="20" cssClass="long" path="description" />
            <p class="hint">
                <form:errors cssClass="error" path="description" />
            </p>
        </div>
        <div class="field">
            <form:label path="keywords">Ключевые слова</form:label>
            <form:input cssClass="text long" path="keywords" />
            <p class="hint">
                <spring:bind path="keywords">
                    <c:if test="${!status.error}">Укажите ключевые слова через пробел</c:if>
                </spring:bind>
                <form:errors cssClass="error" path="keywords" />
            </p>
        </div>
        <div class="field">
            <form:label path="site">Модель на сайте производителя</form:label>
            <form:input cssClass="text long" path="site" />
            <p class="hint">
                <spring:bind path="site">
                    <c:if test="${!status.error}">Укажите адрес веб-сайта, например <em>http://www.example.com</em></c:if>
                </spring:bind>
                <form:errors cssClass="error" path="site" />
            </p>
        </div>
        <fieldset>
            <legend>Изображение</legend>
            <tiles:insertDefinition name="secured-common-image-select">
                <tiles:putAttribute name="paramName" value="image" />
                <tiles:putAttribute name="reselect" value="${reselectImage}" />
                <tiles:putAttribute name="key" value="${model.image}" />
                <tiles:putAttribute name="container" value="model_mini" />
                <tiles:putAttribute name="imageWidth" value="150" />
            </tiles:insertDefinition>
        </fieldset>
        <fieldset>
            <legend>Параметры модели</legend>
            <spring:bind path="paramInput" ignoreNestedPath="true">
                <c:if test="${status.error}">
                    <p class="hint">
                        <span class="error"><c:out value="${status.errorMessage}" /></span>
                    </p>
                </c:if>
            </spring:bind>
            <tiles:insertDefinition name="secured-common-param-input">
                <tiles:putAttribute name="_catalogItem" value="${catalogItem}" />
            </tiles:insertDefinition>
        </fieldset>
        <div class="field">
            <c:if test="${newModel}">
                <input type="submit" value="Добавить модель" />
            </c:if>
            <c:if test="${not newModel}">
                <input type="submit" value="Сохранить модель" />
            </c:if>
        </div>
    </form:form>
</div>
<div class="col2">
    <c:if test="${not newModel and canDelete}">
        <ul>
            <li>
                <a href="<spring:url value='/secured/manager/model/delete/{id}'><spring:param name='id' value='${model.id}' />
                   </spring:url>">Удалить модель</a>
            </li>
        </ul>
    </c:if>
</div>

<script type="text/javascript">
    // <![CDATA[
    $(document).ready(function(){
        $("input[type='text'][name='brandName']").suggest({
            request: {
                url: "<c:url value="/brand/suggest" />"
            },
            data: {
                searchField: "name"
            },
            onSelected: function(itemData) {
                $("input[name='brandId']").val(itemData.id);
            },
            onBlurWithChanged: function() {
                $("input[name='brandId']").val("");
            }
        });
    });
    // ]]>
</script>