<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Изменение порядка параметра категории «<c:out value="${categoryProperties.item.name}" />» группы «<c:out value="${paramGroupProperties.paramGroup.name}" />»</h2>
    <h3><c:out value="${paramProperties.param.name}" /></h3>
    <form:form cssClass="basic" modelAttribute="paramProperties">
        <div class="field">
            <form:label path="param.ordinal">Порядок</form:label>
            <form:select cssClass="long" path="param.ordinal">
                <c:forEach var="orderParamProperties" items="${orderList}">
                    <c:set var="disabled" value="false" />
                    <c:if test="${not orderParamProperties.canChoose}">
                        <!-- TODO fucking IE -->
                        <c:set var="disabled" value="true" />
                    </c:if>
                    <form:option value="${orderParamProperties.param.ordinal}" label="${orderParamProperties.param.name}" disabled="${disabled}" />
                </c:forEach>
            </form:select>
            <p class="hint">
                Укажите доступный параметр категории, порядковое место которого займет данный параметр
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