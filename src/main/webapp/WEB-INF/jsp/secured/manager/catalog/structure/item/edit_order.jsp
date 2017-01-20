<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Изменение порядка элемента каталога</h2>
    <h3><c:out value="${itemProperties.item.name}" /></h3>
    <form:form cssClass="basic" modelAttribute="itemProperties">
        <div class="field">
            <form:label path="afterItemId">Порядок</form:label>
            <form:select cssClass="long" path="afterItemId">
                <c:forEach var="afterItemProperties" items="${afterItemList}">
                    <c:set var="disabled" value="false" />
                    <c:if test="${not afterItemProperties.canChoose}">
                        <!-- TODO fucking IE -->
                        <c:set var="disabled" value="true" />
                    </c:if>
                    <form:option value="${afterItemProperties.item.id}" label="${afterItemProperties.item.name}" disabled="${disabled}" />
                </c:forEach>
            </form:select>
            <p class="hint">
                Укажите доступный элемент каталога, перед которым будет находится данный элемент, или установите последним в списке
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Изменить порядок" />
        </div>
    </form:form>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>