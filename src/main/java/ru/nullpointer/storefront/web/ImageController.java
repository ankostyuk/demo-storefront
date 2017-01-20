package ru.nullpointer.storefront.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.ServletContextAware;
import ru.nullpointer.storefront.config.RepositoryConfig;

/**
 * Контроллер для отдачи изображений.
 * !!! Использовать только для отладки, в продакшне использовать фронтэнд !!!
 * TODO: удалить этот файл
 * @author Alexander Yastrebov
 */
@Controller
public class ImageController implements ServletContextAware {

    private static final int DEFAULT_BUFFER_SIZE = 10240; // 10KB.
    //
    private Logger logger = LoggerFactory.getLogger(ImageController.class);
    //
    private ServletContext servletContext;
    @Resource
    private RepositoryConfig repositoryConfig;

    @RequestMapping(value = "/i/**/*", method = RequestMethod.GET)
    public void handleGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String fileName = request.getServletPath();

        File file = new File(repositoryConfig.getRepositoryRoot(), fileName);

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND); // 404
            return;
        }

        String contentType = servletContext.getMimeType(file.getName());
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        response.reset();
        response.setBufferSize(DEFAULT_BUFFER_SIZE);
        response.setHeader("Content-Type", contentType);
        response.setHeader("Content-Length", String.valueOf(file.length()));

        InputStream is = new FileInputStream(file);
        OutputStream os = response.getOutputStream();

        FileCopyUtils.copy(is, os);

        response.flushBuffer();

        is.close();
        os.close();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
