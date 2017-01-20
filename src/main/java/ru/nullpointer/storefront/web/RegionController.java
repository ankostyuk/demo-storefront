package ru.nullpointer.storefront.web;

import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.support.PageConfig;
import ru.nullpointer.storefront.domain.support.PaginatedQueryResult;
import ru.nullpointer.storefront.service.RegionService;

/**
 * @author ankostyuk
 */
// TODO куча действий Spring-a на обработку запроса - можно уменьшить?
@Controller
public class RegionController {

    private Logger logger = LoggerFactory.getLogger(RegionController.class);
    //
    private static final int REGIONS_PAGE_SIZE_DEFAULT = 5;
    //
    @Resource
    private RegionService regionService;

    @RequestMapping(value = "/region/suggest", method = RequestMethod.GET)
    public
    @ResponseBody
    PaginatedQueryResult<Region> handleGet(
            @RequestParam(value = "text", required = false) String text,
            @RequestParam(value = "page", required = false) Integer page) {
        return regionService.suggestRegionsPaginatedByNameText(text, new PageConfig(page, REGIONS_PAGE_SIZE_DEFAULT));
    }
}
