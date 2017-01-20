package ru.nullpointer.storefront.web.secured.admin;

import java.util.Calendar;
import java.util.Date;
import javax.annotation.Resource;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.nullpointer.storefront.service.RegistrationService;
import ru.nullpointer.storefront.service.TimeService;

/**
 *
 * @author Alexander Yastrebov
 */
@Controller
@RequestMapping("/secured/admin/statistics")
public class AdminStatisticsController {

    private Logger logger = LoggerFactory.getLogger(AdminStatisticsController.class);
    //
    @Resource
    private RegistrationService regService;
    @Resource
    private TimeService timeService;

    @RequestMapping(method = RequestMethod.GET)
    public void handleGet(ModelMap model) {
        Date now = timeService.now();

        Date thisDay = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
        Date thisWeek = getThisWeek(now);
        Date thisMonth = DateUtils.truncate(now, Calendar.MONTH);

        logger.debug("thisDay: {}, thisWeek: {}, thisMonth: {}", new Object[]{thisDay, thisWeek, thisMonth});

        model.addAttribute("companiesTotal", regService.getRegisteredCompanyCount(null, now));
        model.addAttribute("companiesThisDay", regService.getRegisteredCompanyCount(thisDay, now));
        model.addAttribute("companiesThisWeek", regService.getRegisteredCompanyCount(thisWeek, now));
        model.addAttribute("companiesThisMonth", regService.getRegisteredCompanyCount(thisMonth, now));
    }

    private Date getThisWeek(Date now) {
        // DateUtils не поддерживает DateUtils.truncate(now, Calendar.WEEK_OF_MONTH);
        Calendar c = Calendar.getInstance();
        c.setTime(DateUtils.truncate(now, Calendar.DAY_OF_MONTH));
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }
}
