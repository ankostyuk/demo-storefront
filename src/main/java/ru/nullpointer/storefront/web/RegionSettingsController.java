package ru.nullpointer.storefront.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.nullpointer.storefront.domain.Region;
import ru.nullpointer.storefront.domain.Settings;
import ru.nullpointer.storefront.service.RegionService;
import ru.nullpointer.storefront.service.SettingsService;
import ru.nullpointer.storefront.web.support.RegionHelper;

/**
 * @author ankostyuk
 */
@Controller
public class RegionSettingsController {

    @Resource
    private SettingsService settingsService;
    @Resource
    private RegionService regionService;
    @Resource
    private RegionHelper regionHelper;

    @ModelAttribute("settings")
    public Settings getSettings() {
        return settingsService.getSettings();
    }

    @InitBinder("settings")
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("id");
    }

    @RequestMapping(value = "/settings/region", method = RequestMethod.GET)
    public void handleGet(
            @ModelAttribute("settings") Settings settings,
            ModelMap model) {
        RegionHelper.forceRegionAware(settings);
        model.addAttribute("userRegion", regionHelper.getRegionWitnPath(settings.getRegionId()));
    }

    @RequestMapping(value = "/settings/region", method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("settings") Settings settings,
            @RequestParam(value = "redirect", required = false) String redirect,
            ModelMap model,
            HttpServletRequest request) {

        Region region = regionHelper.initRegion(request, model);
        if (region != null) {
            settings.setRegionId(region.getId());
        }
        
        settingsService.updateSettings(settings);

        if (model.containsAttribute("initRegionError")) {
            RegionHelper.forceRegionAware(settings);
            return "settings/region";
        }

        model.clear();
        
        if (!StringUtils.isBlank(redirect)) {
            return "redirect:" + redirect;
        }
        
        return "redirect:/settings/region?updated";
    }

    @ResponseBody
    @RequestMapping(value = "/settings/region/inline", method = RequestMethod.POST)
    public boolean handleInlinePost(
            @RequestParam(value = "inline-region-id", required = false) Integer regionId,
            @RequestParam(value = "inline-region-aware", required = false) String regionAware) {

        Settings settings = getSettings();

        if (regionId != null) {
            Region region = regionService.getRegionById(regionId);
            if (region != null) {
                settings.setRegionId(region.getId());
            }
        }
        
        settings.setRegionAware(regionAware != null);

        settingsService.updateSettings(settings);

        return true;
    }
}
