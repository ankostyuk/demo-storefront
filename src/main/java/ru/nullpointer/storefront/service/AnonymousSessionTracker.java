package ru.nullpointer.storefront.service;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.WebUtils;
import ru.nullpointer.storefront.util.RandomUtils;

/**
 *
 * @author Alexander Yastrebov
 */
@Component
class AnonymousSessionTracker implements Filter {

    private static final int SESSION_ID_LENGTH = 32;
    //
    @Resource
    private SessionService sessionService;
    @Resource
    private TimeService timeService;
    //
    private String cookieName = "ansid";

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if (cookie == null) {
            generateCookie(request, response);
        } else {
            refresh(cookie, request, response);
        }

        chain.doFilter(req, resp);
    }

    String getSessionIdFromRequest() {
        RequestAttributes attrs = RequestContextHolder.currentRequestAttributes();
        if (attrs != null) {
            Object request = attrs.resolveReference(RequestAttributes.REFERENCE_REQUEST);
            Cookie cookie = WebUtils.getCookie((HttpServletRequest) request, cookieName);
            if (cookie != null) {
                return extractSessionId(cookie);
            }
        }
        return null;
    }

    private String extractSessionId(Cookie cookie) {
        String value = cookie.getValue();
        if (value.length() > SESSION_ID_LENGTH) {
            return value.substring(0, SESSION_ID_LENGTH);
        }
        return null;
    }

    private void generateCookie(HttpServletRequest request, HttpServletResponse response) {
        // value = <RANDOM STRING><TIME in seconds>
        String value = new StringBuilder()//
                .append(
                RandomUtils.generateRandomString(SESSION_ID_LENGTH,
                RandomUtils.DIGITS,
                RandomUtils.ASCII_LOWER,
                RandomUtils.ASCII_UPPER))//
                .append(timeService.now().getTime() / 1000)//
                .toString();
        setSessionCookie(value, request, response);
    }

    private void refresh(Cookie cookie, HttpServletRequest request, HttpServletResponse response) {
        boolean invalidCookie = false;
        String sid = extractSessionId(cookie);
        if (sid != null) {
            String timeValue = cookie.getValue().substring(SESSION_ID_LENGTH);
            try {
                long time = Long.parseLong(timeValue);
                long now = timeService.now().getTime() / 1000;
                if (now - time > sessionService.getSessionRefreshRate()) {
                    String value = new StringBuilder()//
                            .append(sid)//
                            .append(now)//
                            .toString();
                    setSessionCookie(value, request, response);
                    sessionService.touchSession(sid);
                }
            } catch (NumberFormatException ex) {
                invalidCookie = true;
            }
        } else {
            invalidCookie = true;
        }

        if (invalidCookie) {
            // некорректная кука - удалить
            setSessionCookie(null, request, response);
        }
    }

    private void setSessionCookie(String value, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie(cookieName, value);

        String path = request.getContextPath();
        cookie.setPath(path.equals("") ? "/" : path);

        // если значение null - удалить
        cookie.setMaxAge(value == null ? 0 : sessionService.getSessionMaxAge());

        response.addCookie(cookie);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }
}
