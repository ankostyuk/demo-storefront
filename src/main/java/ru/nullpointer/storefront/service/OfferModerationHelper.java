package ru.nullpointer.storefront.service;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.dao.AccountDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.Role;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
class OfferModerationHelper {

    @Resource
    private AccountDAO accountDAO;
    @Resource
    private OfferDAO offerDAO;

    void initModeration(Offer offer, Date now) {
        Integer moderatorId = selectModeratorId();
        if (moderatorId != null) {
            // Есть модератор - отправить на модерацию
            setPendingStatus(offer, now, moderatorId);
        } else {
            // Нет модератора - опубликовать сразу
            setApprovedStatus(offer, now);
        }
    }

    void updateModeration(Offer offer, Date now, Offer existingOffer) {
        Integer moderatorId = offer.getModeratorId();
        if (moderatorId == null) {
            // Модератор не установлен - выбрать модератора
            moderatorId = selectModeratorId();
        }

        if (moderatorId != null) {
            // Есть модератор
            // Изменились модерируемые поля или не было модератора - отправить на модерацию
            if (moderatedFieldsChanged(offer, existingOffer) || offer.getModeratorId() == null) {
                setPendingStatus(offer, now, moderatorId);
            }
        } else {
            // Нет модератора - опубликовать сразу
            setApprovedStatus(offer, now);
        }
    }

    void onModeratorAdd(Integer accountId) {
        Set<Integer> moderatorIdSet = new HashSet<Integer>(1);
        moderatorIdSet.add(accountId);

        offerDAO.setModeratorRefs(null, moderatorIdSet);
    }

    void onModeratorRemove(Integer accountId) {
        List<Integer> moderatorIdList = accountDAO.getAccountIdListFromRole(Role.ROLE_MANAGER_MODERATOR_OFFER);
        Set<Integer> moderatorIdSet = new HashSet<Integer>(moderatorIdList);
        moderatorIdSet.remove(accountId);

        offerDAO.setModeratorRefs(accountId, moderatorIdSet);
    }

    private Integer selectModeratorId() {
        List<Integer> moderatorIdList = accountDAO.getAccountIdListFromRole(Role.ROLE_MANAGER_MODERATOR_OFFER);
        if (!moderatorIdList.isEmpty()) {
            Random random = new Random();
            int luckyModerator = random.nextInt(moderatorIdList.size());
            return moderatorIdList.get(luckyModerator);
        }
        return null;
    }

    private boolean moderatedFieldsChanged(Offer offer, Offer existingOffer) {
        boolean result = false;
        result |= different(offer.getName(), existingOffer.getName());
        result |= different(offer.getDescription(), existingOffer.getDescription());

        // Только если установили или обновили изображение
        if (offer.getImage() != null) {
            result |= different(offer.getImage(), existingOffer.getImage());
        }
        return result;
    }

    private boolean different(Object o1, Object o2) {
        if (o1 != null) {
            return !o1.equals(o2);
        } else {
            return o2 != null;
        }
    }

    private void setApprovedStatus(Offer offer, Date now) {
        offer.setStatus(Offer.Status.APPROVED);
        offer.setModeratorId(null);
        offer.setModerationStartDate(now);
        offer.setModerationEndDate(now);
        offer.setRejectionMask(null);
    }

    private void setPendingStatus(Offer offer, Date now, Integer moderatorId) {
        offer.setStatus(Offer.Status.PENDING);
        offer.setModeratorId(moderatorId);
        offer.setModerationStartDate(now);
        offer.setModerationEndDate(null);
        offer.setRejectionMask(null);
    }
}
