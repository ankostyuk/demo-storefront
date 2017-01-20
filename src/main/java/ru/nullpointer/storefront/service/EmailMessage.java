package ru.nullpointer.storefront.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.util.Assert;

/**
 *
 * @author Alexander
 */
public class EmailMessage {

    private String from;
    private String to;
    private String subject;
    private String templateName;
    private Map<String, Object> model;
    private boolean html;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getSubject() {
        return subject;
    }

    public String getTemplateName() {
        return templateName;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public boolean isHtml() {
        return html;
    }

    private EmailMessage(String from, String to, String subject, String templateName, Map<String, Object> model, boolean html) {
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.templateName = templateName;
        this.model = model;
        this.html = html;
    }

    public static class Builder {

        private String from;
        private String to;
        private String subject;
        private String templateName;
        private Map<String, Object> model = new HashMap<String, Object>();
        private boolean html = false;

        public Builder() {
        }

        public EmailMessage build() {
            Assert.hasText(from);
            Assert.hasText(to);
            Assert.hasText(subject);
            Assert.hasText(templateName);

            model = Collections.unmodifiableMap(model);
            
            return new EmailMessage(from, to, subject, templateName, model, html);
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public void setHtml(boolean html) {
            this.html = html;
        }

        public void addModelAttribute(String name, Object value) {
            model.put(name, value);
        }
    }
}
