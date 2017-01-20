<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Изменение порядка группы параметров категории «<c:out value="${categoryProperties.item.name}" />»</h2>
    <h3><c:out value="${paramGroupProperties.paramGroup.name}" /></h3>
    <form:form cssClass="basic" modelAttribute="paramGroupProperties">
        <div class="field">
            <form:label path="paramGroup.ordinal">Порядок</form:label>
            <form:select cssClass="long" path="paramGroup.ordinal">
                <c:forEach var="orderParamGroupProperties" items="${orderList}">
                    <c:set var="disabled" value="false" />
                    <c:if test="${not orderParamGroupProperties.canChoose}">
                        <!-- TODO fucking IE -->
                        <c:set var="disabled" value="true" />
                    </c:if>
                    <form:option value="${orderParamGroupProperties.paramGroup.ordinal}" label="${orderParamGroupProperties.paramGroup.name}" disabled="${disabled}" />
                </c:forEach>
            </form:select>
            <p class="hint">
                Укажите доступную группу параметров категории, порядковое место которой займет данная группа параметров
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