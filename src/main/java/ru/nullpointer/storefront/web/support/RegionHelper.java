package ru.nullpointer.storefront.web.support;

import java.util.Collections;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.util.Assert;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.Settings;
import ru.nullpointer.storefront.service.RegionService;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Component
public class RegionHelper {

    private static final int FIND_REGION_MIN_CHARS = 2;

    @Resource
    private RegionService regionService;

    /**
     * Инициализирует регион.<br/><br/>
     *
     * Извлекает из <code>request</code> параметры URL:<br/>
     * <code>"_initRegionId"</code> <code>Integer</code>,<br/>
     * <code>"_initRegionText"</code> <code>String</code>.<br/><br/>
     *
     * Если <code>"_initRegionText"</code> пустая строка,
     * то возвращает регион с <code>"_initRegionId"</code>,
     * если регион с <code>"_initRegionId"</code> существует.<br/><br/>
     *
     * Если <code>"_initRegionText"</code> непустая строка,
     * то ищет регионы с <code>"_initRegionText"</code> и:<br/>
     * - если нет найденных регионов с <code>"_initRegionText"</code>,
     * то возвращает регион с <code>"_initRegionId"</code>,
     * если регион с <code>"_initRegionId"</code> существует;<br/>
     * - если найден 1 регион с <code>"_initRegionText"</code>,
     * то возвращает найденный регион;<br/>
     * - если найдено более 1 региона с <code>"_initRegionText"</code>
     * и среди найденных есть регион с <code>"_initRegionId"</code>,
     * то возвращает регион с <code>"_initRegionId"</code>,
     * иначе помещает <code>java.util.List</code> список найденных регионов
     * в <code>model</code> как атрибут с именем <code>"initRegionListByText"</code>
     * и возвращает <code>null</code>.<br/><br/>
     *
     * В остальных случаях возвращает <code>null</code>.<br/><br/>
     *
     * Помещает в <code>model</code> атрибут с именем <code>"initRegionText"</code>.
     * Значение атрибута равно наименованию региона
     * если возвращаемый регион не равен <code>null</code>,
     * иначе - <code>"_initRegionText"</code>.<br/><br/>
     *
     * Если возвращаемый регион не равен <code>null</code> -
     * помещает в <code>model</code> атрибут с именем <code>"initRegion"</code>.
     * Значение атрибута равно возвращаемому региону.<br/><br/>
     *
     * Помещает в <code>model</code> атрибут с именем <code>"initRegionError"</code>
     * и значением <code>true</code>,
     * если возвращаемый регион равен <code>null</code> и <code>"_initRegionText"</code> непустая строка.<br/><br/>
     *
     * Поиск регионов по <code>"_initRegionText"</code> осуществляется с учетом {@link FIND_REGION_MIN_CHARS}.<br/><br/>
     *
     * Для возвращаемого региона будет определен путь.<br/>
     *
     * @param request
     * @param model
     * @return
     */
    public Region initRegion(HttpServletRequest request, ModelMap model) {
        Assert.notNull(request);
        Assert.notNull(model);

        Integer regionId = null;
        try {
            regionId = Integer.valueOf(request.getParameter("_initRegionId"));
        } catch (NumberFormatException e) {
        }

        String regionText = request.getParameter("_initRegionText");

        Region region = null;

        Region regionById = (regionId == null ? null : regionService.getRegionById(regionId));
        List<Region> regionListByText = Collections.emptyList();

        boolean search = StringUtils.isNotBlank(regionText);

        if (search) {
            regionText = regionService.normalizeRegionNameText(regionText);
            if (regionText.length() >= FIND_REGION_MIN_CHARS) {
                regionListByText = regionService.suggestRegionsByNameText(regionText);
            }

            if (regionListByText.isEmpty()) {
                region = regionById;
            } else if (regionListByText.size() == 1) {
                region = regionListByText.get(0);
            } else {
                if (regionById != null) {
                    for (Region r : regionListByText) {
                        if (regionById.getId().equals(r.getId())) {
                            region = regionById;
                            break;
                        }
                    }
                }
                if (region == null) {
                    model.addAttribute("initRegionListByText", regionListByText);
                }
            }
        } else {
            region = regionById;
        }

        model.addAttribute("initRegionText", region == null ? regionText : region.getName());
        if (region != null) {
            region.setPath(regionService.getRegionPath(region));
            model.addAttribute("initRegion", region);
        } else if (search) {
            model.addAttribute("initRegionError", true);
        }

        return region;
    }

    public Region getRegionWitnPath(Integer regionId) {
        if (regionId != null) {
            Region region = regionService.getRegionById(regionId);
            if (region != null) {
                region.setPath(regionService.getRegionPath(region));
                return region;
            }
        }
        return null;
    }

    /**
     * Принудительно выставить regionAware, если регион не установлен
     */
    public static void forceRegionAware(Settings settings) {
        Assert.notNull(settings);
        if (settings.getRegionId() == null) {
            settings.setRegionAware(true);
        }
    }

    public static Region getUserRegion(Settings settings) {
        Assert.notNull(settings);
        return settings.isRegionAware() ? settings.getRegion() : null;
    }
}
