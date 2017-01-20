package ru.nullpointer.storefront.service;

import ru.nullpointer.storefront.service.support.ImageCommand;
import java.io.File;
import java.io.IOException;
import javax.annotation.Resource;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;
import ru.nullpointer.storefront.config.RepositoryConfig;
import ru.nullpointer.storefront.domain.Brand;
import ru.nullpointer.storefront.domain.Company;
import ru.nullpointer.storefront.domain.Model;
import ru.nullpointer.storefront.domain.Offer;
import ru.nullpointer.storefront.util.RandomUtils;

/**
 *
 * @author Alexander Yastrebov
 */
@Service
public class ImageService implements InitializingBean {

    private static final String IMAGE_FOLDER_NAME = "i";
    private static final String MINI_FOLDER_NAME = "mini";
    private static final String LARGE_FOLDER_NAME = "large";
    //
    private static final int FILE_NAME_LENGTH = 10;
    //
    private static final String TEMP_PREFIX = "upload";
    private static final String TEMP_SUFFIX = ".bin";
    //
    private Logger logger = LoggerFactory.getLogger(ImageService.class);
    //
    @Value("${image.tool.timeout}")
    private long timeout;
    //
    @Value("${image.format}")
    private String imageFormat;
    //
    @Value("${image.offer.mini.command}")
    private String offerMiniCommand;
    //
    @Value("${image.offer.command}")
    private String offerCommand;
    //
    @Value("${image.offer.large.command}")
    private String offerLargeCommand;
    //
    @Value("${image.model.mini.command}")
    private String modelMiniCommand;
    //
    @Value("${image.model.command}")
    private String modelCommand;
    //
    @Value("${image.model.large.command}")
    private String modelLargeCommand;
    //
    @Value("${image.brand.command}")
    private String brandCommand;
    //
    @Value("${image.company.command}")
    private String companyCommand;
    //
    @Resource
    private RepositoryConfig repositoryConfig;

    public boolean isValidImage(MultipartFile multipartFile) {
        return (multipartFile != null && multipartFile.getSize() > 0);
    }

    /**
     * TODO: Рефакторинг: перенести работу с изображениями в OfferService
     *
     * Устанавливает изображение товарного предложения.
     * Создает основное изображение и его миниатюру.
     * Удаляет старое изображение (если есть).
     * @param offer
     * @param multipartFile загруженный файл изображения.
     * Может быть <code>null</code> для удаления старого изображения предложения
     */
    public void setOfferImage(Offer offer, MultipartFile multipartFile) {
        Assert.notNull(offer);

        ImageCommand commands[] = new ImageCommand[]{
            new ImageCommand(offerMiniCommand, timeout, getMiniFolder("offer")),
            new ImageCommand(offerCommand, timeout, getImageFolder("offer")),
            new ImageCommand(offerLargeCommand, timeout, getLargeFolder("offer"))
        };

        String key = processImages(multipartFile, offer.getImage(), commands);

        offer.setImage(key);
    }

    /**
     * Создает копию изображения товарного предложения (если есть) и устанавливает ее.
     * Старое изображение НЕ удаляется (если есть).
     * @param offer
     */
    // TODO: Рефакторинг: перенести работу с изображениями в OfferService
    public void copyOfferImage(Offer offer) {
        Assert.notNull(offer);
        if (offer.getImage() == null) {
            return;
        }

        String newKey = copyImages(offer.getImage(),
                getMiniFolder("offer"),
                getImageFolder("offer"),
                getLargeFolder("offer"));

        offer.setImage(newKey);
    }

    /**
     * TODO: Рефакторинг: перенести работу с изображениями в ModelService
     *
     * Устанавливает изображение модели.
     * Создает основное изображение и его миниатюру.
     * Удаляет старое изображение (если есть).
     * @param offer
     * @param multipartFile загруженный файл изображения.
     * Может быть <code>null</code> для удаления старого изображения предложения
     */
    public void setModelImage(Model model, MultipartFile multipartFile) {
        Assert.notNull(model);

        ImageCommand commands[] = new ImageCommand[]{
            new ImageCommand(modelMiniCommand, timeout, getMiniFolder("model")),
            new ImageCommand(modelCommand, timeout, getImageFolder("model")),
            new ImageCommand(modelLargeCommand, timeout, getLargeFolder("model"))
        };

        String key = processImages(multipartFile, model.getImage(), commands);

        model.setImage(key);
    }

    /**
     * Создает копию изображения модели (если есть) и устанавливает ее.
     * Старое изображение НЕ удаляется (если есть).
     * @param model
     */
    // TODO: Рефакторинг: перенести работу с изображениями в ModelService
    public void copyModelImage(Model model) {
        Assert.notNull(model);
        if (model.getImage() == null) {
            return;
        }

        String newKey = copyImages(model.getImage(),
                getMiniFolder("model"),
                getImageFolder("model"),
                getLargeFolder("model"));

        model.setImage(newKey);
    }

    public void setBrandLogo(Brand brand, MultipartFile multipartFile) {
        Assert.notNull(brand);

        ImageCommand commands[] = new ImageCommand[]{
            new ImageCommand(brandCommand, timeout, getImageFolder("brand"))
        };

        String key = processImages(multipartFile, brand.getLogo(), commands);

        brand.setLogo(key);
    }

    public void setCompanyLogo(Company company, MultipartFile multipartFile) {
        Assert.notNull(company);

        ImageCommand commands[] = new ImageCommand[]{
            new ImageCommand(companyCommand, timeout, getImageFolder("company"))
        };

        String key = processImages(multipartFile, company.getLogo(), commands);

        company.setLogo(key);
    }

    private String processImages(MultipartFile multipartFile, String oldKey, ImageCommand[] commands) {

        String newKey = oldKey;
        boolean success = true;

        if (isValidImage(multipartFile)) {
            String key = generateImageKey();
            String fileName = getFileName(key);

            File temp = getFile(multipartFile);
            String path = temp.getAbsolutePath();

            for (int i = 0; i < commands.length; i++) {
                success = commands[i].execute(path, fileName);
                if (!success) {
                    // ошибка: удалить уже созданные
                    for (int j = 0; j < i; j++) {
                        deleteFile(commands[j].getDestinationPath(), fileName);
                    }
                    break;
                }
            }
            temp.delete();

            if (success) {
                newKey = key;
            }
        } else {
            newKey = null;
        }

        if (success && oldKey != null) {
            String fileName = getFileName(oldKey);
            for (ImageCommand c : commands) {
                deleteFile(c.getDestinationPath(), fileName);
            }
        }

        return newKey;
    }

    /**
     * Создает копии изображений с ключем <code>key</code> в каталогах <code>paths</code>
     * Возвращает ключ для доступа к копиям или <code>null</code> если копирование неудалось.
     * @param key
     * @param paths
     * @return
     */
    private String copyImages(String key, File... paths) {
        String oldFileName = getFileName(key);

        String newKey = generateImageKey();
        String newFileName = getFileName(newKey);

        // TODO: проверять результат копирования
        for (File path : paths) {
            copyFile(path, oldFileName, newFileName);
        }
        return newKey;
    }

    private String generateImageKey() {
        return RandomUtils.generateRandomString(FILE_NAME_LENGTH,
                RandomUtils.DIGITS,
                RandomUtils.ASCII_LOWER);
    }

    private String getFileName(String key) {
        return key + "." + imageFormat;
    }

    private boolean deleteFile(File path, String fileName) {
        File f = new File(path, fileName);
        return f.delete();
    }

    private boolean copyFile(File path, String oldFileName, String newFileName) {
        File src = new File(path, oldFileName);
        File dst = new File(path, newFileName);
        try {
            FileUtils.copyFile(src, dst);
        } catch (IOException ex) {
            return false;
        }
        return true;
    }

    private File getImageFolder(String target) {
        return getPathFolder(IMAGE_FOLDER_NAME, target);
    }

    private File getMiniFolder(String target) {
        return getPathFolder(IMAGE_FOLDER_NAME, target, MINI_FOLDER_NAME);
    }

    private File getLargeFolder(String target) {
        return getPathFolder(IMAGE_FOLDER_NAME, target, LARGE_FOLDER_NAME);
    }

    private File getPathFolder(String... pathElements) {
        File repoRoot = repositoryConfig.getRepositoryRoot();
        File container = new File(repoRoot, StringUtils.join(pathElements, "/"));
        if (!container.exists()) {
            if (!container.mkdirs()) {
                throw new RuntimeException("Не удалось создать каталог '" + container.getAbsolutePath() + "'");
            }
        }
        return container;
    }

    private File getFile(MultipartFile file) {
        try {
            File temp = File.createTempFile(TEMP_PREFIX, TEMP_SUFFIX, repositoryConfig.getRepositoryRoot());
            file.transferTo(temp);
            return temp;
        } catch (IOException ex) {
            throw new RuntimeException("Не удалось создать файл", ex);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.isTrue(timeout > 0);

        Assert.hasText(imageFormat);
        Assert.hasText(offerMiniCommand);
        Assert.hasText(offerCommand);
        Assert.hasText(offerLargeCommand);

        Assert.hasText(modelMiniCommand);
        Assert.hasText(modelCommand);
        Assert.hasText(modelLargeCommand);

        Assert.hasText(brandCommand);
        Assert.hasText(companyCommand);
    }
}
