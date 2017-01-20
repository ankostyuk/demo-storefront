<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<div class="col1">
    <c:if test="${newParam}">
        <h2>Добавление числового параметра категории «<c:out value="${catalogItem.name}" />» в группу «<c:out value="${paramGroup.name}" />»</h2>
    </c:if>
    <c:if test="${not newParam}">
        <h2>Редактирование числового параметра категории «<c:out value="${catalogItem.name}" />» в группе «<c:out value="${paramGroup.name}" />»</h2>
    </c:if>
    <form:form cssClass="basic" modelAttribute="param">

        <tiles:insertDefinition name="param-common-form" />

        <div class="field-separator"></div>
        <div>
            <div class="field">
                <form:label path="minValue">Минимальное значение<span class="mandatory">*</span></form:label>
                <form:input cssClass="text medium" path="minValue" />
                <p class="hint">
                    <form:errors cssClass="error" path="minValue" />
                </p>
            </div>
            <div class="field">
                <form:label path="maxValue">Максимальное значение<span class="mandatory">*</span></form:label>
                <form:input cssClass="text medium" path="maxValue" />
                <p class="hint">
                    <form:errors cssClass="error" path="maxValue" />
                </p>
            </div>
            <div class="field">
                <label for="unitName">Единица измерения<span class="mandatory">*</span></label>
                <input id="unitName" name="unitName" type="text" class="text medium" value="${unit.name}" />
                <form:hidden path="unitId" />
                <p class="hint">
                    <form:errors cssClass="error" path="unitId" />
                </p>
            </div>
            <div class="field">
                <form:label path="precision">Формат дробной части<span class="mandatory">*</span></form:label>
                <form:select cssClass="medium"  path="precision">
                    <c:forEach var="precision" begin="0" end="4">
                        <form:option value="${precision}"><fmt:formatNumber value="0" minFractionDigits="${precision}" maxFractionDigits="${precision}"/></form:option>
                    </c:forEach>
                </form:select>
                <p class="hint">
                    <form:errors cssClass="error" path="precision" />
                    <spring:bind path="precision">
                        <c:if test="${!status.error}">Формат дробной части используется при отображении значений параметра</c:if>
                    </spring:bind>
                </p>
            </div>
        </div>

        <spring:bind path="param" ignoreNestedPath="true">
            <c:if test="${status.error}">
                <div class="field">
                    <p class="hint">
                        <span class="error"><c:out value="${status.errorMessage}" /></span>
                    </p>
                </div>
            </c:if>
        </spring:bind>

        <div class="field">
            <c:if test="${newParam}">
                <input type="submit" value="Добавить числовой параметр" />
            </c:if>
            <c:if test="${not newParam}">
                <input type="submit" value="Сохранить числовой параметр" />
            </c:if>
        </div>
    </form:form>

    <c:if test="${not newParam}">
        <tiles:insertDefinition name="param-common-actions" />
    </c:if>
</div>
<div class="col2">

</div>

<script type="text/javascript">
    // <![CDATA[
    $(document).ready(function(){
        $("input[type='text'][name='unitName']").suggest({
            request: {
                url: "<c:url value="/secured/manager/catalog/unit/suggest" />"
            },
            data: {
                searchField: "name"
            },
            onSelected: function(itemData) {
                $("input[name='unitId']").val(itemData.id);
            },
            onBlurWithChanged: function() {
                $("input[name='unitId']").val("");
            }
        });
    });
    // ]]>
</script>
