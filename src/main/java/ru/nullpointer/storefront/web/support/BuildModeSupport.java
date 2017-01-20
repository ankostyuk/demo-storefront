package ru.nullpointer.storefront.web.support;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.nullpointer.storefront.web.exception.NotFoundException;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
public class BuildModeSupport {

    @Value("${application.build.mode}")
    private String applicationBuildMode;

    public void throwInDemo(RuntimeException ex) {
        if ("DEMO".equals(applicationBuildMode)) {
            throw ex;
        }
    }

    public void notFoundInDemo() {
        throwInDemo(new NotFoundException("Страница не найдена"));
    }
}
