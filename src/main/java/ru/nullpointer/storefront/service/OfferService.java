package ru.nullpointer.storefront.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.dao.CatalogItemDAO;
import ru.nullpointer.storefront.dao.CategoryDAO;
import ru.nullpointer.storefront.dao.ModelDAO;
import ru.nullpointer.storefront.dao.OfferDAO;
import ru.nullpointer.storefront.dao.ParamSetDAO;
import ru.nullpointer.storefront.domain.CatalogItem;
import ru.nullpointer.storefront.domain.Category;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.domain.support.CompanyOfferShowing;
import ru.nullpointer.storefront.domain.support.CompanyOfferSorting;
import ru.nullpointer.storefront.domain.support.DateWindowConfig;
import ru.nullpointer.storefront.domain.support.NumberInterval;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.domain.support.StringCount;
import ru.nullpointer.storefront.service.search.SearchIndexing;
import ru.nullpointer.storefront.service.support.FieldUtils;

/**
 * 
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Service
public class OfferService {

    private Logger logger = LoggerFactory.getLogger(OfferService.class);
    //
    @Resource
    private SecurityService securityService;
    @Resource
    private TimeService timeService;
    @Resource
    private OfferDAO offerDAO;
    @Resource
    private CategoryDAO categoryDAO;
    @Resource
    private ParamModelHelper paramModelHelper;
    @Resource
    private ParamSetDAO paramSetDAO;
    @Resource
    private ModelDAO modelDAO;
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private OfferModerationHelper offerModerationHelper;
    @Resource
    private CatalogItemDAO catalogItemDAO;

    /**
     * Возвращает список товарных предложений поставщика
     * 
     * @param categoryIdSet
     *            набор идентификационных номеров категории или
     *            <code>null</code> для получения списка всех товарных
     *            предложений компании
     * @param pageConfig
     *            настройки постраничного вывода
     * @param showing
     *            настройки показа предложений
     * @param sorting
     *            параметр сортировки
     * @return
     * @throws IllegalArgumentException
     *             если любой из аргументов (кроме categoryIdSet) равен
     *             <code>null<code>
     */
    @Secured("ROLE_COMPANY")
    @Transactional(readOnly = true)
    public PaginatedQueryResult<Offer> getCompanyOffers(Set<Integer> categoryIdSet, PageConfig pageConfig, CompanyOfferShowing showing, CompanyOfferSorting sorting) {
        Integer companyId = securityService.getAuthenticatedCompanyId();
        List<Offer> list = offerDAO.getCompanyOfferList(companyId, categoryIdSet, pageConfig, showing, sorting);

        int total = offerDAO.getCompanyOfferCount(companyId, categoryIdSet, showing);

        return new PaginatedQueryResult<Offer>(pageConfig, list, total);
    }

    @Secured("ROLE_COMPANY")
    @Transactional(readOnly = true)
    public int getCompanyOfferCount(Set<Integer> categoryIdSet, CompanyOfferShowing showing) {
        Integer companyId = securityService.getAuthenticatedCompanyId();
        return offerDAO.getCompanyOfferCount(companyId, categoryIdSet, showing);
    }

    /**
     * Возвращает карту количества товарных предложений поставщика Ключем карты
     * является идентификационный номер категории, значением - количество
     * товарных предложений поставщика в этой категории
     * @return
     */
    @Secured("ROLE_COMPANY")
    @Transactional(readOnly = true)
    public Map<Integer, Integer> getCompanyOfferCountMap() {
        Integer companyId = securityService.getAuthenticatedCompanyId();
        return offerDAO.getCompanyOfferCountMap(companyId);
    }

    /**
     * Возвращает товарное предложение поставщика по <code>id</code>
     * @param id идентификационный номер товарного предложения
     * @return предложение или <code>null</code> если предложения с таким
     *      <code>id</code> не существует или предложение не принадлежит
     *      залогиненному поставщику
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Secured("ROLE_COMPANY")
    @Transactional(readOnly = true)
    public Offer getCompanyOfferById(Integer id) {
        return getCompanyOfferByIdInternal(id);
    }

    /**
     * Сохраняет товарное предложение в каталоге. Устанавливает владельцем
     * предложения аутентифицированного поставщика.
     * 
     * @param offer
     * @param paramValueMap
     *            - карта значений дополнительных параметров предложения. Ключем
     *            карты является <code>id</code> дополнительного параметра.
     *            Класс объекта-значения зависит от типа параметра. Может
     *            равняться <code>null</code> если предложение связано с моделью 
     *            или не имеет дополнительных параметров.
     */
    // TODO: перенести работу с изображениями в OfferService
    @Secured("ROLE_COMPANY")
    @Transactional
    @SearchIndexing
    public void storeOffer(Offer offer, Map<Integer, Object> paramValueMap) {
        checkOffer(offer);

        Category category = categoryDAO.getCategoryById(offer.getCategoryId());
        Assert.notNull(category);

        offer.setParamDescription(null);

        if (category.isParametrized()) {
            if (offer.isModelLinked()) {
                Model model = modelDAO.getModelById(offer.getModelId());
                Assert.isTrue(category.getId().equals(model.getCategoryId()));

                offer.setBrandId(model.getBrandId());
                offer.setBrandName(null);
            } else if (containsParamValues(paramValueMap)) {
                Integer psdId = category.getParameterSetDescriptorId();

                Integer paramSetId = paramSetDAO.insert(psdId, paramValueMap);
                offer.setParamSetId(paramSetId);

                offer.setParamDescription(paramModelHelper.buildParamDescription(psdId, paramValueMap));
            }
        } else {
            offer.setParamSetId(null);
            offer.setModelId(null);
        }

        offer.setCompanyId(securityService.getAuthenticatedCompanyId());

        // TODO: квоты
        Date now = timeService.now();
        offer.setCreateDate(now);
        offer.setEditDate(now);

        // Модерация
        offerModerationHelper.initModeration(offer, now);

        boolean defaultCurrency = currencyConfig.getDefaultCurrency().equals(offer.getCurrency());
        if (defaultCurrency) {
            calculateDefaultUnitPrice(offer);
        } else {
            offer.setUnitPrice(BigDecimal.ZERO);
        }
        offerDAO.insert(offer);

        if (!defaultCurrency) {
            offerDAO.updateOffersUnitPrice(currencyConfig.getDefaultCurrency(), offer.getCurrency(), null, offer.getId());
        }

        // Активировать категорию
        if (offer.getStatus() == Offer.Status.APPROVED) {
            activateApprovedOfferCategoryPath(offer);
        }
    }

    /**
     * Обновляет информацию о товарном предложении. Не меняет
     * поставщика-владельца предложения и категорию предложения.
     * 
     * @param offer
     * @param paramValueMap
     *            - карта значений дополнительных параметров предложения. Ключем
     *            карты является <code>id</code> дополнительного параметра.
     *            Класс объекта-значения зависит от типа параметра. Может
     *            равняться <code>null</code> если предложение связано с моделью
     *            или не имеет дополнительных параметров или необходимо удалить
     *            ранее установленные параметры.
     * @return
     */
    // TODO: перенести работу с изображениями в OfferService
    @Secured("ROLE_COMPANY")
    @Transactional
    @SearchIndexing
    public void updateOfferInfo(Offer offer, Map<Integer, Object> paramValueMap) {
        checkOffer(offer);

        Offer existingOffer = getCompanyOfferByIdInternal(offer.getId());
        Assert.notNull(existingOffer, "Попытка изменить несуществующее или чужое предложение");

        Category category = categoryDAO.getCategoryById(offer.getCategoryId());
        Assert.notNull(category);

        offer.setParamDescription(null);

        if (category.isParametrized()) {
            Integer psdId = category.getParameterSetDescriptorId();

            if (offer.isModelLinked()) {
                Model model = modelDAO.getModelById(offer.getModelId());
                Assert.isTrue(category.getId().equals(model.getCategoryId()));

                offer.setBrandId(model.getBrandId());
                offer.setBrandName(null);
                if (offer.isParametrized()) {
                    // удалить дополнительные параметры
                    paramSetDAO.delete(psdId, offer.getParamSetId());
                    offer.setParamSetId(null);
                }
            } else if (containsParamValues(paramValueMap)) {
                if (offer.isParametrized()) {
                    // обновить
                    paramSetDAO.update(psdId, offer.getParamSetId(), paramValueMap);
                } else {
                    // установить
                    Integer paramSetId = paramSetDAO.insert(psdId, paramValueMap);
                    offer.setParamSetId(paramSetId);
                }
                offer.setParamDescription(paramModelHelper.buildParamDescription(psdId, paramValueMap));
            } else {
                if (offer.isParametrized()) {
                    // удалить дополнительные параметры
                    paramSetDAO.delete(psdId, offer.getParamSetId());
                    offer.setParamSetId(null);
                }
            }
        } else {
            offer.setParamSetId(null);
            offer.setModelId(null);
        }

        Date now = timeService.now();
        offer.setEditDate(now);

        // Модерация
        offerModerationHelper.updateModeration(offer, now, existingOffer);

        boolean defaultCurrency = currencyConfig.getDefaultCurrency().equals(offer.getCurrency());
        if (defaultCurrency) {
            calculateDefaultUnitPrice(offer);
        } else {
            offer.setUnitPrice(BigDecimal.ZERO);
        }

        offerDAO.updateInfo(offer);

        if (!defaultCurrency) {
            offerDAO.updateOffersUnitPrice(currencyConfig.getDefaultCurrency(), offer.getCurrency(), null, offer.getId());
        }

        // Активировать категорию
        if (offer.getStatus() == Offer.Status.APPROVED) {
            activateApprovedOfferCategoryPath(offer);
        }
    }

    /**
     * Удаляет товарное предложение
     * 
     * @param offer
     */
    @Secured("ROLE_COMPANY")
    @Transactional
    @SearchIndexing
    // TODO: перенести работу с изображениями в OfferService
    public void deleteOffer(Offer offer) {
        Assert.notNull(offer);
        Assert.notNull(offer.getId());
        Assert.notNull(getCompanyOfferByIdInternal(offer.getId()), "Попытка удалить несуществующее или чужое предложение");

        if (offer.isParametrized()) {
            Category category = categoryDAO.getCategoryById(offer.getCategoryId());
            Assert.notNull(category);

            paramSetDAO.delete(category.getParameterSetDescriptorId(), offer.getParamSetId());
        }
        offerDAO.delete(offer);
    }

    /**
     * Возвращает количество товарных предложений в категории
     * @param categoryId идентификационный номер категории
     * @return количество товарных предложений в категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public int getCategoryOfferCount(Integer categoryId) {
        return offerDAO.getCategoryOfferCount(categoryId);
    }

    /**
     * Возвращает количество доступных товарных предложений в категории
     * Доступно если:
     * активно,
     * прошло модерацию,
     * ...
     * @param categoryId идентификационный номер категории
     * @return количество товарных предложений в категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public int getCategoryAccessibleOfferCount(Integer categoryId) {
        return offerDAO.getCategoryAccessibleOfferCount(categoryId);
    }

    /**
     * Возвращает количество доступных товарных предложений связанных с моделью в категории
     * Доступно если:
     * активно,
     * прошло модерацию,
     * ...
     * @param categoryId идентификационный номер категории
     * @return количество товарных предложений связанных с моделью в категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public int getCategoryAccessibleModelOfferCount(Integer categoryId) {
        return offerDAO.getCategoryAccessibleModelOfferCount(categoryId);
    }

    /**
     * Постранично возвращает список товарных предложений категории
     *
     * @param categoryId
     *        идентификационный номер категории
     * @param pageConfig
     *        настройки постраничного вывода
     * @return список товарных предложений категории или пустой список, если
     *         категория не имеет товарных предложений
     * @throws IllegalArgumentException
     *             если <code>categoryId</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public PaginatedQueryResult<Offer> getCategoryOfferPaginatedList(Integer categoryId, PageConfig pageConfig) {
        List<Offer> list = offerDAO.getCategoryOfferPaginatedList(categoryId, pageConfig);

        int total = offerDAO.getCategoryOfferCount(categoryId);

        return new PaginatedQueryResult<Offer>(pageConfig, list, total);
    }

    /**
     * Возвращает диапазон цен за единицу измерения на активные предложения во всем каталоге
     * @return
     */
    @Transactional(readOnly = true)
    public NumberInterval getCatalogOfferPriceInterval() {
        return offerDAO.getCatalogOfferPriceInterval();
    }

    /**
     * Возвращает диапазон цен за единицу измерения на активные предложения в категории
     * @param categoryId идентификатор категории
     * @throws IllegalArgumentException если <code>categoryId</code> равен <code>null</code>
     * @return
     */
    @Transactional(readOnly = true)
    public NumberInterval getCategoryOfferPriceInterval(Integer categoryId) {
        return offerDAO.getCategoryOfferPriceInterval(categoryId);
    }

    /**
     * Возвращает диапазон цен за единицу измерения на активные предложения модели.
     * @param modelId идентификатор модели
     * @throws IllegalArgumentException если <code>modelId</code> равен <code>null</code>
     * @return
     */
    @Transactional(readOnly = true)
    public NumberInterval getModelOfferPriceInterval(Integer modelId) {
        return offerDAO.getModelOfferPriceInterval(modelId);
    }

    /**
     * Возвращает карту количества товарных предложений ожидающих модерации.
     * Ключем карты является идентификационный номер категории, значением - количество
     * товарных предложений в этой категории
     * @return
     */
    @Secured("ROLE_MANAGER_MODERATOR_OFFER")
    @Transactional(readOnly = true)
    public Map<Integer, Integer> getModeratorPendingOfferCountMap() {
        Integer moderatorId = securityService.getAuthenticatedManagerId();
        return offerDAO.getModeratorPendingOfferCountMap(moderatorId);
    }

    /**
     * Возвращает список товарных предложений назначенных модератору
     * @param categoryId ИД категории
     * @param windowConfig настройки оконного вывода
     * @param pendingOnly если <code>true</code>, то вернуть только предложения ожидающие модерации
     * @return
     */
    @Secured("ROLE_MANAGER_MODERATOR_OFFER")
    @Transactional(readOnly = true)
    public List<Offer> getModeratorOfferList(Integer categoryId, DateWindowConfig windowConfig, boolean pendingOnly) {
        Integer moderatorId = securityService.getAuthenticatedManagerId();
        return offerDAO.getModeratorOfferList(moderatorId, categoryId, windowConfig, pendingOnly);
    }

    /**
     * Возвращает товарное предложение, назначенное модератору, по <code>id</code>
     * @param id идентификационный номер товарного предложения
     * @return предложение или <code>null</code> если предложения с таким
     *      <code>id</code> не существует или предложение не назначено
     *      залогиненному модератору
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Secured("ROLE_MANAGER_MODERATOR_OFFER")
    @Transactional(readOnly = true)
    public Offer getModeratorOfferById(Integer id) {
        return getModeratorOfferByIdInternal(id);
    }

    /**
     * Одобряет товарное предложение поставщика
     * @param offerId идентификатор товарного предложения
     */
    @Secured("ROLE_MANAGER_MODERATOR_OFFER")
    @Transactional
    @SearchIndexing
    public void approveOffer(Integer offerId) {
        Assert.notNull(offerId);
        Offer offer = getModeratorOfferByIdInternal(offerId);
        Assert.notNull(offer);

        offer.setStatus(Offer.Status.APPROVED);
        offer.setModerationEndDate(timeService.now());
        offer.setRejectionMask(null);

        offerDAO.updateInfo(offer);

        activateApprovedOfferCategoryPath(offer);
    }

    /**
     * Отклоняет товарное предложение поставщика
     * @param offerId идентификатор товарного предложения
     * @param rejectionList список причин отклонения
     * @throws IllegalArgumentException если <code>rejectionList</code>
     *      равен <code>null</code> или пуст
     */
    @Secured("ROLE_MANAGER_MODERATOR_OFFER")
    @Transactional
    @SearchIndexing
    public void rejectOffer(Integer offerId, List<Offer.Rejection> rejectionList) {
        Assert.notNull(rejectionList);
        Assert.isTrue(!rejectionList.isEmpty());

        Offer offer = getModeratorOfferByIdInternal(offerId);
        Assert.notNull(offer);

        offer.setStatus(Offer.Status.REJECTED);
        offer.setRejectionList(rejectionList);
        offer.setModerationEndDate(timeService.now());

        offerDAO.updateInfo(offer);
    }

    /**
     * Возвращает товарное предложение по <code>id</code>.
     * @param id идентификационный номер товарного предложения
     * @return предложение или <code>null</code> если предложения с таким <code>id</code> не доступно
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Offer getOfferById(Integer id) {
        return offerDAO.getOfferById(id);
    }

    /**
     * Возвращает доступное товарное предложение по <code>id</code>.
     * Доступно если:
     * активно,
     * прошло модерацию,
     * ...
     * категория предложения активна,
     * ...
     * @param id идентификационный номер товарного предложения
     * @return предложение или <code>null</code> если предложения с таким <code>id</code> не существует или не доступно
     * @throws IllegalArgumentException если <code>id</code> равен <code>null</code>
     */
    @Transactional(readOnly = true)
    public Offer getAccessibleOfferById(Integer id) {
        Assert.notNull(id);

        Offer offer = offerDAO.getOfferById(id);

        if (offer == null || !isOfferAccessible(offer)) {
            return null;
        }

        CatalogItem categoryItem = catalogItemDAO.getCategoryItemById(offer.getCategoryId());
        Assert.notNull(categoryItem);
        if (!categoryItem.getActive()) {
            return null;
        }
        return offer;
    }

    /**
     * Проверяет доступность товарного предложения
     * Доступно если:
     * активно,
     * прошло модерацию,
     * ...
     * @param offer
     * @throws IllegalArgumentException если <code>offer</code> равен <code>null</code>
     */
    public static boolean isOfferAccessible(Offer offer) {
        Assert.notNull(offer);
        return offer.getActive() && offer.getStatus().equals(Offer.Status.APPROVED);
    }

    @Transactional
    // TODO ? @SearchIndexing
    public void deactivateOffers() {
        Date now = timeService.now();
        int count = offerDAO.deactivateOffers(now);

        logger.debug("Деактивировано предложений: {}, дата/время: {}", new Object[]{count, now});
    }

    @Transactional(readOnly = true)
    public Map<Integer, Offer> getOfferMap(Set<Integer> offerIdSet) {
        return offerDAO.getOfferMap(offerIdSet);
    }

    @Secured("ROLE_MANAGER_BRAND")
    @Transactional(readOnly = true)
    public List<StringCount> getUnlinkedBrandNameList() {
        return offerDAO.getUnlinkedBrandNameList();
    }

    @Secured("ROLE_MANAGER_BRAND")
    @Transactional
    public void setBrandByName(String name, Integer brandId) {
        if (name == null || name.trim().isEmpty()) {
            return;
        }

        int count = offerDAO.setBrandByName(name, brandId);

        logger.debug("Кол-во предложений с обновленными ccылками на бренд: {}", count);
    }

    private void checkOffer(Offer offer) {
        Assert.notNull(offer);
        Assert.notNull(offer.getCategoryId());
        Assert.notNull(offer.getName());
        Assert.notNull(offer.getPrice());
        Assert.notNull(offer.getCurrency());
        Assert.notNull(offer.getRatio());

        FieldUtils.nullifyIfEmpty(offer, "description");
        FieldUtils.nullifyIfEmpty(offer, "originCountry");
        FieldUtils.nullifyIfEmpty(offer, "brandName");
        FieldUtils.nullifyIfEmpty(offer, "modelName");
    }

    private Offer getCompanyOfferByIdInternal(Integer id) {
        Offer offer = offerDAO.getOfferById(id);
        Integer companyId = securityService.getAuthenticatedCompanyId();
        if (offer == null || !offer.getCompanyId().equals(companyId)) {
            return null;
        }
        return offer;
    }

    private Offer getModeratorOfferByIdInternal(Integer id) {
        Offer offer = offerDAO.getOfferById(id);
        Integer moderatorId = securityService.getAuthenticatedManagerId();
        if (offer == null || !moderatorId.equals(offer.getModeratorId())) {
            return null;
        }
        return offer;
    }

    private boolean containsParamValues(Map<Integer, Object> paramValueMap) {
        if (paramValueMap != null) {
            for (Object value : paramValueMap.values()) {
                if (value != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private void calculateDefaultUnitPrice(Offer offer) {
        BigDecimal price = offer.getPrice().setScale(4, RoundingMode.HALF_UP);
        BigDecimal ratio = offer.getRatio();
        BigDecimal unitPrice = price.divide(ratio, RoundingMode.HALF_UP);

        offer.setUnitPrice(unitPrice);
    }

    private void activateApprovedOfferCategoryPath(Offer offer) {
        List<CatalogItem> path = catalogItemDAO.getPath(offer.getCategoryId());
        for (int i = 0; i < path.size(); i++) {
            CatalogItem item = path.get(i);
            if (!item.getActive()) {
                item.setActive(Boolean.TRUE);
                catalogItemDAO.updateItemActive(item);
            }
        }
    }
}
