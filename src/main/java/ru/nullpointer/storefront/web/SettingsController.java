package ru.nullpointer.storefront.web;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.Settings;
import ru.nullpointer.storefront.service.SettingsService;
import ru.nullpointer.storefront.web.support.RegionHelper;
import ru.nullpointer.storefront.web.ui.EnumEditor;
import ru.nullpointer.storefront.web.ui.SelectOption;

/**
 * @author Alexander Yastrebov
 * @author ankostyuk
 */
@Controller
@RequestMapping("/settings")
public class SettingsController {

    private Logger logger = LoggerFactory.getLogger(SettingsController.class);
    //
    @Resource
    private SettingsService settingsService;
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private RegionHelper regionHelper;

    @ModelAttribute("settings")
    public Settings getSettings(HttpServletRequest request) {
        return settingsService.getSettings();
    }

    @InitBinder("settings")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id");
        binder.registerCustomEditor(Settings.PRICE_TYPE.class, new EnumEditor(Settings.PRICE_TYPE.class, false));
    }

    @ModelAttribute("extraCurrencyList")
    public List<SelectOption<String>> getExtraCurrencyList() {
        List<SelectOption<String>> list = new ArrayList<SelectOption<String>>();
        for (String s : currencyConfig.getExtraCurrencyList()) {
            list.add(new SelectOption<String>(s, s));
        }
        return list;
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet(@ModelAttribute("settings") Settings settings, ModelMap model) {
        RegionHelper.forceRegionAware(settings);
        model.addAttribute("userRegion", regionHelper.getRegionWitnPath(settings.getRegionId()));
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("settings") Settings settings,
            ModelMap model,
            HttpServletRequest request) {

        Region region = regionHelper.initRegion(request, model);
        if (region != null) {
            settings.setRegionId(region.getId());
        }

        logger.debug("Settings (post): {}", settings);

        settingsService.updateSettings(settings);

        if (model.containsAttribute("initRegionError")) {
            RegionHelper.forceRegionAware(settings);
            return "settings";
        }

        model.clear();
        return "redirect:/settings?updated";
    }
}
