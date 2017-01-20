<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>

<tiles:useAttribute name="_catalogTree" />
<tiles:useAttribute name="_parentLevel" ignore="true"/>

<c:if test="${fn:length(catalogTree) > 0}">
    <div class="catalog-tree-box">
        <ul class="v-list items">
            <c:set var="oldLevel" value="${1 + _parentLevel}" />
            <c:forEach var="node" items="${catalogTree}" varStatus="st">
                <c:choose>
                    <c:when test="${node.level > oldLevel}">
                        <ul>
                            <c:set var="oldLevel" value="${node.level}" />
                    </c:when>
                    <c:when test="${node.level < oldLevel}">
                        </li>
                        <c:forEach begin="${node.level + 1}" end="${oldLevel}">
                            </ul>
                            </li>
                        </c:forEach>
                        <c:set var="oldLevel" value="${node.level}" />
                    </c:when>
                    <c:when test="${node.level == oldLevel && not st.first}">
                        </li>
                    </c:when>
                </c:choose>

                <li id="<c:out value="ci${node.data['id']}" />">

                    <c:if test="${node.data['type'] == 'SECTION'}">
                        <span class="section-name<c:if test="${node.disabled}"> disabled</c:if>">
                            <span class="ci-name"><c:out value="${node.name}" /></span>
                        </span><c:if test="${not empty node.data['info']}">
                            <span class="offer-count-inline"><c:out value="${node.data['info']}" /></span>
                        </c:if>
                    </c:if>
                    <c:if test="${node.data['type'] == 'CATEGORY'}">
                        <span>
                            <label <c:if test="${node.disabled}">class="disabled" title="Категория временно неактивна"</c:if>>
                                <input class="radio" name="categoryId" value="${node.data['id']}" type="radio" <c:if test="${node.current}">checked="checked"</c:if> />
                                <span class="ci-name"><c:out value="${node.name}" /></span>
                            </label>
                        </span><c:if test="${not empty node.data['info']}">
                            <span class="offer-count-inline"><c:out value="${node.data['info']}" /></span>
                        </c:if>
                    </c:if>

                <c:if test="${st.last}">
                    </li>
                    <c:forEach begin="${1 + _parentLevel}" end="${node.level - 1}">
                        </ul>
                    </li>
                    </c:forEach>
                </c:if>
            </c:forEach>
        </ul>
    </div>
</c:if>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){
        jsHelper.buildCollapsableTree({
            treeSelector: "div.catalog-tree-box > ul",
            treeClass: "tree collapsable",
            collapseByNameClickSelector: "li.has-collapse > span.section-name",
            initiallyOpenIds: [],
            stateToCookie: true,
            stateFromCookie: true,
            cookieName: "company-category-select-catalog-tree-expanded",
            cookieOptions: {
                path: "<c:url value="/secured/company"/>"
            }
        });
        
        <c:if test="${fn:length(catalogTree) > 1}">
            <%-- Подсказчик категорий --%>
            jsHelper.buildСategorySuggest({
                afterSelector: "div.catalog-tree-box > ul",
                categoryNodeSelector: "div.catalog-tree-box ul li:has(> span > label)",
                catalogItemNameSelector: "span.ci-name:first",
                parentNodeSelector: "div.catalog-tree-box li",
                cache: true,
                onSelected: function(nodeProperties) {
                    nodeProperties.node.find(":radio").attr("checked", "checked");
                    nodeProperties.node.parents("form").submit();
                }
            });
        </c:if>
    });
    // ]]>
</script>

