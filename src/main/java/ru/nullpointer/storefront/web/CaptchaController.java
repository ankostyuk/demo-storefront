package ru.nullpointer.storefront.web;

import com.octo.captcha.service.CaptchaServiceException;
import com.octo.captcha.service.image.ImageCaptchaService;
import java.awt.image.BufferedImage;
import javax.servlet.ServletOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.imageio.ImageIO;


/**
 *
 * @author Alexander Yastrebov
 */
@Controller
public class CaptchaController {

    private static final String CAPTCHA_FORMAT = "jpeg";
    private static final String CAPTCHA_MIME = "image/jpeg";
    //
    @Autowired
    private ImageCaptchaService captchaService;

    @RequestMapping("/captcha")
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BufferedImage challenge = null;
        try {
            String sessionId = request.getSession().getId();
            challenge = captchaService.getImageChallengeForID(sessionId, request.getLocale());
        } catch (IllegalArgumentException ex) {
            response.sendError(response.SC_NOT_FOUND);
            return;
        } catch (CaptchaServiceException ex) {
            response.sendError(response.SC_INTERNAL_SERVER_ERROR);
            return;
        }
        
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType(CAPTCHA_MIME);
        
        ServletOutputStream output = response.getOutputStream();
        
        ImageIO.write(challenge, CAPTCHA_FORMAT, output);
        
        output.flush();
        output.close();
    }
}
