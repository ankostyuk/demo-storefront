package ru.nullpointer.storefront.web.tags;

import java.util.ArrayList;
import java.util.List;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Match;
import ru.nullpointer.storefront.domain.CartItem;
import ru.nullpointer.storefront.web.support.UserSession;
import ru.nullpointer.storefront.web.support.MatchParser;

/**
 *
 * @author Alexander Yastrebov
 */
public class UserSessionFunctions {

    private UserSessionFunctions() {
    }

    public static boolean isMatchInComparison(UserSession userSession, Match match) {
        Assert.notNull(userSession);
        Assert.notNull(match);
        for (List<Match> matchList : userSession.getComparisonMap().values()) {
            if (matchList.contains(match)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOfferInComparison(UserSession userSession, Integer id) {
        return isMatchInComparison(userSession, new Match(Match.Type.OFFER, id));
    }

    public static boolean isModelInComparison(UserSession userSession, Integer id) {
        return isMatchInComparison(userSession, new Match(Match.Type.MODEL, id));
    }

    public static String getComparisonUrlParam(UserSession userSession, Integer categoryId) {
        Assert.notNull(userSession);
        Assert.notNull(categoryId);

        List<Match> matchList = userSession.getComparisonMap().get(categoryId);
        return getComparisonUrlParamEx(matchList, null);
    }

    public static String getComparisonUrlParamEx(List<Match> matchList, Match exclude) {
        if (matchList == null || matchList.isEmpty()) {
            return "";
        }

        if (exclude != null) {
            matchList = new ArrayList<Match>(matchList);
            matchList.remove(exclude);
        }
        return new MatchParser().serialize(matchList);
    }

    public static int getComparisonCount(UserSession userSession, Integer categoryId) {
        Assert.notNull(userSession);
        Assert.notNull(categoryId);

        List<Match> matchList = userSession.getComparisonMap().get(categoryId);
        return matchList != null ? matchList.size() : 0;
    }

    public static Integer getOfferCartId(UserSession userSession, Integer id) {
        return getCartId(userSession, new Match(Match.Type.OFFER, id));
    }

    public static Integer getModelCartId(UserSession userSession, Integer id) {
        return getCartId(userSession, new Match(Match.Type.MODEL, id));
    }

    public static Integer getCartId(UserSession userSession, Match match) {
        Assert.notNull(userSession);
        Assert.notNull(match);
        for (List<CartItem> itemList : userSession.getCartItemMap().values()) {
            for (CartItem item : itemList) {
                if (item.getMatch().equals(match)) {
                    return item.getCartId();
                }
            }
        }
        return null;
    }
}
