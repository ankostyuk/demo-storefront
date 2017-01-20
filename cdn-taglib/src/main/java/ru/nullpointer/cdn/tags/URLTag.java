package ru.nullpointer.cdn.tags;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import org.apache.commons.lang.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Alexander Yastrebov
 */
public class URLTag extends TagSupport {

    private static final long serialVersionUID = 1L;
    //
    private Logger logger = LoggerFactory.getLogger(URLTag.class);
    //
    private static final String CONFIG_LOCATION_ATTR = "ru.nullpointer.cdn.tags.configLocation";
    private static final String CONFIG_ATTR = "ru.nullpointer.cdn.tags.config";
    //
    private String var;
    private String container;
    private String key;

    public URLTag() {
        init();
    }

    private void init() {
        var = null;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public void setContainer(String container) {
        Validate.notNull(container);
        this.container = container;
    }

    public void setKey(String key) {
        Validate.notNull(key);
        this.key = key;
    }

    @Override
    public int doStartTag() throws JspException {
        loadConfig();
        return super.doStartTag();
    }

    @Override
    public int doEndTag() throws JspException {
        Config config = getConfig();

        Container c = config.getContainerMap().get(container);
        if (c == null) {
            String msg = "No container with name \"" + container + "\" configured";
            logger.error(msg);
            throw new IllegalArgumentException(msg);
        }

        String result = c.buildUrl(container, key, (HttpServletRequest) pageContext.getRequest());

        if (var != null) {
            pageContext.setAttribute(var, result);
        } else {
            writeResult(result);
        }
        return EVAL_PAGE;
    }

    private void writeResult(String msg) throws JspException {
        try {
            pageContext.getOut().write(String.valueOf(msg));
        } catch (IOException ex) {
            throw new JspException(ex);
        }
    }

    private void loadConfig() throws JspException {
        Object obj = pageContext.getAttribute(CONFIG_ATTR, PageContext.APPLICATION_SCOPE);
        if (obj == null) {
            String configLocation = pageContext.getServletContext().getInitParameter(CONFIG_LOCATION_ATTR);

            Config config = null;
            try {
                config = ConfigParser.parseConfig(configLocation, pageContext.getServletContext());
            } catch (Exception ex) {
                throw new JspException("Error loading cdn configuration", ex);
            }
            pageContext.setAttribute(CONFIG_ATTR, config, PageContext.APPLICATION_SCOPE);
        }
    }

    private Config getConfig() {
        return (Config) pageContext.getAttribute(CONFIG_ATTR, PageContext.APPLICATION_SCOPE);
    }
}
