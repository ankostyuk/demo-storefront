package ru.nullpointer.storefront.web.secured.company.settings;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.config.CurrencyConfig;
import ru.nullpointer.storefront.domain.ExtraCurrency;
import ru.nullpointer.storefront.service.CurrencyService;
import ru.nullpointer.storefront.web.ui.DecimalEditor;
import ru.nullpointer.storefront.web.ui.EnumEditor;
import ru.nullpointer.storefront.web.ui.ObjectMap;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/company/settings/currency")
public class CompanyCurrencySettingsController {

    private Logger logger = LoggerFactory.getLogger(CompanyCurrencySettingsController.class);
    //
    private static final BigDecimal MAX_RATE_PERCENT_DIFFERENCE = BigDecimal.valueOf(30, 0);
    //
    @Resource
    private CurrencyConfig currencyConfig;
    @Resource
    private CurrencyService currencyService;

    @ModelAttribute("rateMap")
    public Map<String, BigDecimal> getRateMap() {
        Map<String, BigDecimal> rateMap = new HashMap<String, BigDecimal>();
        for (String cur : currencyConfig.getExtraCurrencyList()) {
            BigDecimal rateValue = currencyService.getDefaultCurrencyMultiplier(cur);
            rateMap.put(cur, rateValue);
        }
        return rateMap;
    }

    @ModelAttribute("options")
    public ObjectMap<ExtraCurrency> getOptions() {
        ObjectMap<ExtraCurrency> options = new ObjectMap<ExtraCurrency>();

        Map<String, ExtraCurrency> extraCurrencyMap = currencyService.getExtraCurrencyMap();
        for (String cur : currencyConfig.getExtraCurrencyList()) {
            ExtraCurrency extraCurrency = extraCurrencyMap.get(cur);
            if (extraCurrency != null) {
                options.put(cur, extraCurrency);
            } else {
                extraCurrency = new ExtraCurrency();
                extraCurrency.setCurrency(cur);
                options.put(cur, extraCurrency);
            }
        }
        return options;
    }

    @InitBinder("options")
    public void initBinder(WebDataBinder binder) {
        List<String> allowedList = new ArrayList<String>();
        for (String cur : currencyConfig.getExtraCurrencyList()) {
            String typePath = ObjectMap.buildPropertyPath(cur, "type");
            String percentPath = ObjectMap.buildPropertyPath(cur, "percent");
            String fixedRatePath = ObjectMap.buildPropertyPath(cur, "fixedRate");

            allowedList.add(typePath);
            allowedList.add(percentPath);
            allowedList.add(fixedRatePath);

            binder.registerCustomEditor(ExtraCurrency.Type.class, typePath, new EnumEditor(ExtraCurrency.Type.class));
            binder.registerCustomEditor(BigDecimal.class, percentPath, new DecimalEditor(BigDecimal.class, true, 1));
            binder.registerCustomEditor(BigDecimal.class, fixedRatePath, new DecimalEditor(BigDecimal.class, true, 2));
        }
        binder.setAllowedFields(allowedList.toArray(new String[0]));
    }

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet(@ModelAttribute("options") ObjectMap<ExtraCurrency> options, ModelMap model) {
        logger.debug("get options: {}", options);
    }

    @RequestMapping(method = RequestMethod.POST)
    public String handlePost(
            @ModelAttribute("options") ObjectMap<ExtraCurrency> options,
            BindingResult result,
            @ModelAttribute("rateMap") Map<String, BigDecimal> rateMap) {

        logger.debug("options: {}", options);

        if (!validate(options, result, rateMap)) {
            // некорректный запрос
            logger.warn("некорректный запрос");
            return "redirect:/secured/company/settings/currency";
        }
        if (result.hasErrors()) {
            logger.debug("errors: {}", result);
            return "secured/company/settings/currency";
        }

        // все ОК
        for (Object key : options.getKeys()) {
            ExtraCurrency extraCurrency = options.get(key);
            if (extraCurrency.getId() == null) {
                currencyService.storeExtraCurrency(extraCurrency);
            } else {
                currencyService.updateExtraCurrencyInfo(extraCurrency);
            }
        }

        return "redirect:/secured/company/settings/currency?updated";
    }

    private boolean validate(ObjectMap<ExtraCurrency> options, Errors errors, Map<String, BigDecimal> rateMap) {
        for (String cur : currencyConfig.getExtraCurrencyList()) {
            ExtraCurrency extraCurrency = options.get(cur);
            if (extraCurrency == null) {
                // нет нужной валюты
                return false;
            }
            if (extraCurrency.getType() == null) {
                // курс по-умолчанию
                continue;
            }
            switch (extraCurrency.getType()) {
                case PERCENT:
                    if (!hasFieldErrors(errors, cur, "percent")) {
                        if (extraCurrency.getPercent() == null) {
                            // процент не указан или указан некорректно
                            rejectValue(errors, cur, "percent", "notnull", null);
                        } else if (extraCurrency.getPercent().abs().compareTo(MAX_RATE_PERCENT_DIFFERENCE) > 0) {
                            // процент по модулю больше MAX_RATE_PERCENT_DIFFERENCE
                            rejectValue(errors, cur, "percent", "size", MAX_RATE_PERCENT_DIFFERENCE);
                        }
                    }
                    break;
                case FIXED_RATE:
                    if (!hasFieldErrors(errors, cur, "fixedRate")) {
                        if (extraCurrency.getFixedRate() == null) {
                            // фиксированный курс не указан или указан некорректно
                            rejectValue(errors, cur, "fixedRate", "notnull", null);
                        } else {
                            BigDecimal rate = rateMap.get(cur);
                            // 100 * |(rate - fixedRate)|/rate
                            BigDecimal diff = rate.subtract(extraCurrency.getFixedRate()).divide(rate, RoundingMode.HALF_UP);
                            diff = diff.multiply(new BigDecimal(100));
                            diff = diff.abs();

                            if (diff.compareTo(MAX_RATE_PERCENT_DIFFERENCE) > 0) {
                                // фиксированный курс отличается от курса ЦБ более чем на MAX_RATE_PERCENT_DIFFERENCE процентов
                                rejectValue(errors, cur, "fixedRate", "size", MAX_RATE_PERCENT_DIFFERENCE);
                            }
                        }
                    }
                    break;
            }
        }
        return true;
    }

    private boolean hasFieldErrors(Errors errors, String currency, String property) {
        String path = ObjectMap.buildPropertyPath(currency, property);
        return errors.hasFieldErrors(path);
    }

    private void rejectValue(Errors errors, String currency, String property, String code, Object arg) {
        String path = ObjectMap.buildPropertyPath(currency, property);
        String errorCode = new StringBuilder("validation.ExtraCurrency.")//
                .append(property)//
                .append(".")//
                .append(code)//
                .toString();
        if (arg != null) {
            errors.rejectValue(path, errorCode, new Object[]{arg}, "");
        } else {
            errors.rejectValue(path, errorCode);
        }
    }
}
