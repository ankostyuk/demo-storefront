<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn2" uri="http://www.nullpointer.ru/fn2/tags" %>
<div class="col1">
    <h1>Удаление модели</h1>
    <c:if test="${empty modelInfo}">
        <p>
            С моделью не связано ни одного опубликованного предложения.
        </p>
    </c:if>
    <c:if test="${not empty modelInfo}">
        <p>
            Внимание! После удаления <strong>${modelInfo.offerCount}</strong>
            ${fn2:ruplural(modelInfo.offerCount, 'предложение перестанет быть связанным/предложения перестанут быть связанными/предложений перестанут быть связанными')}
            с этой моделью.
        </p>
    </c:if>
    <p>
        Вы уверены что хотите удалить модель «<strong><c:out value="${model.name}" /></strong>»?
    </p>
    <form:form modelAttribute="model">
        <div class="field">
            <input type="submit" value="Удалить модель" />
        </div>
    </form:form>
</div>
<div class="col2">

</div>