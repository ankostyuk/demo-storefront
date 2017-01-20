<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Перемещение параметра категории «<c:out value="${categoryProperties.item.name}" />» из группы «<c:out value="${paramGroupProperties.paramGroup.name}" />» в другую группу</h2>
    <h3><c:out value="${paramProperties.param.name}" /></h3>
    <form:form cssClass="basic" modelAttribute="paramProperties">
        <div class="field">
            <form:label path="param.paramGroupId">Группа параметров</form:label>
            <form:select cssClass="long" path="param.paramGroupId">
                <c:forEach var="paramGroupProperties" items="${paramGroupList}">
                    <c:set var="disabled" value="false" />
                    <c:if test="${not paramGroupProperties.canChoose}">
                        <!-- TODO fucking IE -->
                        <c:set var="disabled" value="true" />
                    </c:if>
                    <form:option value="${paramGroupProperties.paramGroup.id}" label="${paramGroupProperties.paramGroup.name}" disabled="${disabled}" />
                </c:forEach>
            </form:select>
            <p class="hint">
                Укажите доступную группу параметров категории, в которую будет перемещен данный параметр
            </p>
        </div>
        <div class="field">
            <input type="submit" value="Переместить параметр" />
        </div>
    </form:form>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>