<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<c:if test="${not empty _actionInfo}">
    <c:if test="${_actionInfo.type == 'CART_DELETE'}">
        <c:if test="${not _actionInfo.success}">
            <div class="action-error-info">
                <p>
                    Невозможно удалить список покупок. Пожалуйста, попробуйте позже.
                </p>
            </div>
        </c:if>
    </c:if>
</c:if>
