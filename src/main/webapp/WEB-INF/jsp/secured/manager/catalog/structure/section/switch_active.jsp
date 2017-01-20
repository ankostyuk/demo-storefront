<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div class="col1">
    <h2>Изменение активности раздела каталога</h2>
    <h3><c:out value="${sectionProperties.item.name}" /></h3>
    <c:if test="${sectionProperties.canActive}">
        <p>
            Только активные разделы публикуются в каталоге
        </p>
        <p>
            Все подразделы и подкатегории раздела станут <c:if test="${sectionProperties.item.active}">неактивными</c:if><c:if test="${not sectionProperties.item.active}">активными</c:if>
        </p>
        <form:form cssClass="basic" modelAttribute="sectionProperties">
            <div class="field">
                <label>
                    Изменить активность раздела с <c:if test="${sectionProperties.item.active}">активного на неактивный</c:if><c:if test="${not sectionProperties.item.active}"> неактивного на активный</c:if>?
                </label>
                <input type="submit" value="Изменить" />
            </div>
        </form:form>
    </c:if>
    <c:if test="${not sectionProperties.canActive}">
        <p>
            Активность раздела не может быть изменена. Сделайте активным родительский раздел и попробуйте снова.
        </p>
    </c:if>
</div>
<div class="col2">
    <!-- Column 2 start -->
    <p>column2</p>
    <!-- Column 2 end -->
</div>