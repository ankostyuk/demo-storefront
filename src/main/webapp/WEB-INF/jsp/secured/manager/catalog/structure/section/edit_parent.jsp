<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Перемещение раздела каталога в другой раздел</h2>
    <h3><c:out value="${sectionProperties.item.name}" /></h3>
    <form:form cssClass="basic" modelAttribute="sectionProperties">
        <div class="field">
            <form:label path="parentItemId">Раздел каталога</form:label>
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
            <p class="hint">
                Укажите доступный раздел или корень каталога, в который будет перемещен данный раздел
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Переместить раздел" />
        </div>
    </form:form>
</div>
<div class="col2">

</div>