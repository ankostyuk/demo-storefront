<%@ page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:set var="itemList" value="${userSession.cartItemMap[cart.id]}" />
<c:set var="multiCart" value="${fn:length(userSession.cartList) > 1}"/>
<c:set var="redirect">/cart/${cart.id}</c:set>

<div id="container">
    <div id="content-right-sidebar">
        <h1><c:out value="${cart.name}" /></h1>
        <c:if test="${not empty cart.description}">
            <p><c:out value="${cart.description}" /></p>
        </c:if>
        <c:if test="${not empty cart.id}">
            <div class="cart-actions">
                <ul class="h-list">
                    <li><a class="act" rel="nofollow" href="<spring:url value="/cart/edit/{cartId}">
                               <spring:param name="cartId" value="${cart.id}" />
                           </spring:url>">Редактировать</a>
                    </li>
                    <li><a rel="nofollow" class="dce cart-delete" href="<spring:url value="/cart/delete/{cartId}">
                               <spring:param name="cartId" value="${cart.id}" />
                               <c:if test="${not empty redirect}">
                                   <spring:param name="redirect" value="${redirect}" />
                               </c:if>
                           </spring:url>">Удалить</a>
                    </li>
                </ul>
            </div>
        </c:if>
        <c:if test="${empty itemList}">
            <p class="info">Список пуст</p>
        </c:if>
        <c:if test="${not empty itemList}">
            <div class="match-list">
                <c:forEach var="cartItem" items="${itemList}">
                    <c:set var="match" value="${cartMatchMap[cartItem.match]}" />
                    <c:if test="${not empty match}">
                        <c:if test="${match.type == 'OFFER'}">
                            <tiles:insertDefinition name="match-item-offer">
                                <tiles:putAttribute name="match" value="${match}" />
                                <tiles:putAttribute name="categoryPath" value="${categoryPathMap[match.offer.categoryId]}" />
                                <tiles:putAttribute name="cartItem" value="${cartItem}" />
                                <tiles:putAttribute name="redirect" value="${redirect}"/>
                            </tiles:insertDefinition>
                        </c:if>
                        <c:if test="${match.type == 'MODEL'}">
                            <tiles:insertDefinition name="match-item-model">
                                <tiles:putAttribute name="match" value="${match}" />
                                <tiles:putAttribute name="categoryPath" value="${categoryPathMap[match.model.categoryId]}" />
                                <tiles:putAttribute name="cartItem" value="${cartItem}" />
                                <tiles:putAttribute name="redirect" value="${redirect}"/>
                            </tiles:insertDefinition>
                        </c:if>
                    </c:if>
                    <c:if test="${empty match}">
                        <div style="display: none;">Элемент не существует ${cartItem.match}</div>
                    </c:if>
                </c:forEach>
            </div>
            <tiles:insertDefinition name="match-tools">
                <tiles:putAttribute name="_redirect" value="${redirect}" />
            </tiles:insertDefinition>
        </c:if>
    </div><!-- #content-right-sidebar-->
</div><!-- #container-->

<div class="sidebar" id="sideRight">
    <div class="cart-list">
        <c:if test="${fn:length(userSession.cartList) > 0}">
            <ul class="v-list inside items">
                <c:forEach var="otherCart" items="${userSession.cartList}">
                    <c:if test="${cart.id == otherCart.id}">
                        <li><span class="current"><c:out value="${otherCart.name}" /></span></li>
                    </c:if>
                    <c:if test="${cart.id != otherCart.id}">
                        <li><span><a id="cart-${otherCart.id}" rel="nofollow" href="<spring:url value="/cart/{cartId}">
                                   <spring:param name="cartId" value="${otherCart.id}" />
                               </spring:url>"><c:out value="${otherCart.name}" /></a></span>
                        </li>
                    </c:if>
                </c:forEach>
            </ul>
        </c:if>
        <div class="actions">
            <ul class="h-list">
                <li><a class="composite new" rel="nofollow" href="<spring:url value="/cart/add">
                           <spring:param name="redirect" value="${redirect}" />
                       </spring:url>"><span class="link">Добавить новый список</span></a>
                </li>
            </ul>
        </div>
    </div>
</div><!-- .sidebar#sideRight -->

<c:if test="${multiCart}">
    <ul id="dropdown-cart-item-list-move" class="v-list inside">
        <c:forEach var="otherCart" items="${userSession.cartList}">
            <c:if test="${cart.id != otherCart.id}">
                <li><a id="move-to-cart-${otherCart.id}" class="act pseudo move-to-cart" rel="nofollow" href="<spring:url value="/cart/{cartId}/item/move" htmlEscape="true">
                           <spring:param name="cartId" value="${cart.id}" />
                           <spring:param name="toCartId" value="${otherCart.id}" />
                           <spring:param name="redirect" value="${redirect}" />
                       </spring:url>"><c:out value="${otherCart.name}" /></a>
                </li>
            </c:if>
        </c:forEach>
    </ul>
</c:if>

<script type="text/javascript">
    // <![CDATA[
    jsHelper.document.ready(function(){

        jsHelper.buildPseudoLink({
            pseudoLinkSelector: "a.cart-delete",
            pseudoLinkClass: "pseudo",
            requestData: {inline: true},
            withConfirm: true,
            confirmMessage: "Вы уверены, что хотите удалить список покупок?",
            success: jsHelper.buildPseudoLinkSuccessCallback({
                noError: function(){
                    window.location = "<c:url value='/cart' />";
                }
            })
        });

    <c:if test="${multiCart}">

        var pseudoLink = jsHelper.buildPseudoLink({
            pseudoLinkSelector: "a.cart-item-move",
            pseudoLinkClass: "pseudo",
            dropdownContentSelector: "#dropdown-cart-item-list-move"
        });

        $("#dropdown-cart-item-list-move a.move-to-cart").click(function(e){
            e.preventDefault();

            pseudoLink.hideDropdown();

            var cartLink = $(this);
            var link = pseudoLink.getLink();

            link.parents(".match-item").effect("transfer", {to: "#cart-" + cartLink.attr("id").substr("move-to-cart-".length), className: "cart-item-move-effect-transfer"}, jsHelper.transferEffectSpeed);

            $.ajax({
                context: link[0],
                url: cartLink.attr("href"),
                data: {
                    inline: true,
                    id: link.attr("id").substr("cart-item-".length)
                },
                type: "POST",
                success: jsHelper.buildPseudoLinkSuccessCallback({
                    noError: function(){
                        jsHelper.removeContent(this.parents(".match-item"));
                    }
                })
            });
        });

    </c:if>

        jsHelper.buildPseudoLink({
            pseudoLinkSelector: "a.cart-item-delete",
            pseudoLinkClass: "pseudo",
            requestData: {inline: true},
            success: jsHelper.buildPseudoLinkSuccessCallback({
                noError: function(){
                    jsHelper.removeContent(this.parents(".match-item"));
                }
            })
        });
    });
    // ]]>
</script>
