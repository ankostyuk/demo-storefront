<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<div class="col1">
    <c:if test="${newParam}">
        <h2>Добавление выборочного параметра категории «<c:out value="${catalogItem.name}" />» в группу «<c:out value="${paramGroup.name}" />»</h2>
    </c:if>
    <c:if test="${not newParam}">
        <h2>Редактирование выборочного параметра категории «<c:out value="${catalogItem.name}" />» в группе «<c:out value="${paramGroup.name}" />»</h2>
    </c:if>
    <form:form cssClass="basic" modelAttribute="param">

        <tiles:insertDefinition name="param-common-form" />

        <div class="field-separator"></div>

        <c:forEach var="optionKey" items="${selectOptions.keys}" varStatus="st">
            <div class="field">
                <c:if test="${st.first}">
                    <c:if test="${(fn:length(selectOptions.keys) > 1)}">
                        <label>Варианты выбора<span class="mandatory">*</span></label>
                    </c:if>
                    <c:if test="${(fn:length(selectOptions.keys) <= 1)}">
                        <label>Вариант выбора<span class="mandatory">*</span></label>
                    </c:if>
                </c:if>

                <spring:bind path="selectOptions.value[${optionKey}].name" ignoreNestedPath="true">
                    <input id="selectOptions.value${optionKey}.name" name="${status.expression}" class="text medium" value="${status.value}" />

                    <c:if test="${not newParam}">
                        <%-- Tool box --%>
                        <c:set var="option" value="${selectOptions.value[optionKey]}" />
                        <c:choose>
                            <c:when test="${st.index > 0 and st.index < fn:length(selectOptions.value) - 1}">
                                <span class="tool">
                                    <a class="enabled" title="Сдвинуть вариант выбора вверх" href="<spring:url value='/secured/manager/catalog/category/param/select/option/shift/up/{id}'>
                                           <spring:param name='id' value='${option.id}' />
                                       </spring:url>">&nbsp;&#8593;&nbsp;</a>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;&#8593;&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when test="${st.index < fn:length(selectOptions.value) - 2}">
                                <span class="tool">
                                    <a class="enabled" title="Сдвинуть вариант выбора вниз" href="<spring:url value='/secured/manager/catalog/category/param/select/option/shift/down/{id}'>
                                           <spring:param name='id' value='${option.id}' />
                                       </spring:url>">&nbsp;&#8595;&nbsp;</a>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;&#8595;&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>

                        <c:choose>
                            <c:when  test="${not st.last and fn:length(selectOptions.value) > 2}">
                                <span class="tool"><a class="enabled" title="Удалить вариант выбора" href="<spring:url value='/secured/manager/catalog/category/param/select/option/delete/{id}'>
                                                          <spring:param name='id' value='${option.id}' />
                                                      </spring:url>">&nbsp;&#8855;&nbsp;</a>
                                </span>
                            </c:when>
                            <c:otherwise>
                                <span class="tool"><span class="disabled">&nbsp;&#8855;&nbsp;</span></span>
                            </c:otherwise>
                        </c:choose>
                    </c:if>

                    <c:if test="${status.error}">
                        <p class="hint">
                            <span class="error"><c:out value="${status.errorMessage}" /></span>
                        </p>
                    </c:if>
                </spring:bind>
            </div>
        </c:forEach>

        <div class="field">
            <spring:bind path="selectOptions" ignoreNestedPath="true">
                <c:if test="${status.error}">
                    <p class="hint">
                        <span class="error"><c:out value="${status.errorMessage}" /></span>
                    </p>
                </c:if>
            </spring:bind>
        </div>

        <div class="field">
            <c:if test="${newParam}">
                <input type="submit" value="Добавить" />
                <input type="submit" value="Добавить и продолжить" name="continue" />
            </c:if>
            <c:if test="${not newParam}">
                <input type="submit" value="Сохранить" />
                <input type="submit" value="Сохранить и продолжить" name="continue" />
            </c:if>
        </div>
    </form:form>

    <c:if test="${not newParam}">
        <tiles:insertDefinition name="param-common-actions" />
    </c:if>
</div>
<div class="col2">

</div>