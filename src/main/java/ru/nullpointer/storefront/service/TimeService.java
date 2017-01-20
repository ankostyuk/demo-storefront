package ru.nullpointer.storefront.service;

import java.util.Date;
import org.springframework.stereotype.Service;


/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class TimeService {

    public Date now() {
        return new Date();
    }
}
