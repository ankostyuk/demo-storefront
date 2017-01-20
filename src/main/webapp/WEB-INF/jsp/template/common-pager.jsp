<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="cdn" uri="http://www.nullpointer.ru/cdn/tags" %>

<tiles:importAttribute name="queryResult" />
<tiles:importAttribute name="pagerUrl" />
<tiles:importAttribute name="pagerUrlHasParams" ignore="true" />
<tiles:importAttribute name="displayedPageSideCount" ignore="true" />

<c:set var="pagerBaseUrl" value="${pagerUrl}${pagerUrlHasParams ? '&amp;page=' : '?page='}" />
<%-- по сколько страниц показывать с каждой стороны от текущей страницы --%>
<c:set var="pageListHalfSize" value="${displayedPageSideCount != null ? displayedPageSideCount : 4}" />

<div class="pager">
    <div class="page-box">
        <span class="title">Страницы</span>
        <ul class="pages">
            <%-- не показывать точки --%>
            <c:if test="${queryResult.pageCount <= 2*pageListHalfSize}">
                <c:forEach begin="1" end="${queryResult.pageCount}" varStatus="st">
                    <c:if test="${queryResult.pageNumber == st.index}">
                        <li class="current"><span>${st.index}</span></li>
                    </c:if>
                    <c:if test="${queryResult.pageNumber != st.index}">
                        <li><span><a href="${pagerBaseUrl}${st.index}">${st.index}</a></span></li>
                    </c:if>
                </c:forEach>
            </c:if>
            <%-- показывать точки --%>
            <c:if test="${queryResult.pageCount > 2*pageListHalfSize}">
                <c:set var="startPage" value="${queryResult.pageNumber < 2*pageListHalfSize ? 1 : queryResult.pageNumber - pageListHalfSize}" />
                <c:set var="endPage" value="${queryResult.pageNumber > queryResult.pageCount - 2*pageListHalfSize ? queryResult.pageCount : queryResult.pageNumber + pageListHalfSize}" />
                <%-- точки в начале --%>
                <c:if test="${startPage != 1}">
                    <li><span><a href="${pagerBaseUrl}${startPage - 1}">...</a></span></li>
                </c:if>
                <c:forEach begin="${startPage}" end="${endPage}" varStatus="st">
                    <c:if test="${queryResult.pageNumber == st.index}">
                        <li class="current"><span>${st.index}</span></li>
                    </c:if>
                    <c:if test="${queryResult.pageNumber != st.index}">
                        <li><span><a href="${pagerBaseUrl}${st.index}">${st.index}</a></span></li>
                    </c:if>
                </c:forEach>
                <%-- точки в конце --%>
                <c:if test="${endPage != queryResult.pageCount}">
                    <li><span><a href="${pagerBaseUrl}${endPage + 1}">...</a></span></li>
                </c:if>
            </c:if>
        </ul>
    </div>
    <ul class="nav">
        <c:if test="${queryResult.pageNumber <= 1}">
            <li class="prev"><span></span></li>
        </c:if>
        <c:if test="${queryResult.pageNumber > 1}">
            <li class="prev"><a href="${pagerBaseUrl}${queryResult.pageNumber - 1}"></a></li>
        </c:if>
        <c:if test="${queryResult.pageNumber < queryResult.pageCount}">
            <li class="next"><a href="${pagerBaseUrl}${queryResult.pageNumber + 1}"></a></li>
        </c:if>
        <c:if test="${queryResult.pageNumber >= queryResult.pageCount}">
            <li class="next"><span></span></li>
        </c:if>
    </ul>
</div>
