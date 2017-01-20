package ru.nullpointer.storefront.service;

import javax.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class EmailService {

    @Autowired
    @Qualifier("emailTemplateConfiguration")
    private Configuration conf;
    //
    private JavaMailSender mailSender;

    public void sendEmail(final EmailMessage emailMessage) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage);

                message.setTo(emailMessage.getTo());
                message.setFrom(emailMessage.getFrom());
                message.setSubject(emailMessage.getSubject());

                Template template = conf.getTemplate(emailMessage.getTemplateName() + ".ftl");
                String emailText = FreeMarkerTemplateUtils.processTemplateIntoString(template, emailMessage.getModel());

                message.setText(emailText, emailMessage.isHtml());
            }
        };
        mailSender.send(preparator);
    }

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
}
