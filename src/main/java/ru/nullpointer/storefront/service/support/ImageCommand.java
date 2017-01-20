package ru.nullpointer.storefront.service.support;

import java.io.File;
import java.io.IOException;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteWatchdog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

public class ImageCommand {

    private static final String SOURCE_PLACEHOLDER = "<source>";
    private static final String DESTINATION_PLACEHOLDER = "<destination>";
    //
    private static Logger logger = LoggerFactory.getLogger(ImageCommand.class);
    //
    private final String command;
    private final long timeout;
    private final File destinationPath;

    public ImageCommand(String command, long timeout, File destinationPath) {
        Assert.notNull(command);
        this.command = command;
        this.timeout = timeout;
        this.destinationPath = destinationPath;
    }

    public File getDestinationPath() {
        return destinationPath;
    }

    public boolean execute(String sourcePath, String destFileName) {
        String cmd = command;
        cmd = cmd.replace(SOURCE_PLACEHOLDER, sourcePath);
        cmd = cmd.replace(DESTINATION_PLACEHOLDER, new File(destinationPath, destFileName).getAbsolutePath());

        logger.debug("Выполняем команду «{}»", cmd);

        CommandLine commandLine = CommandLine.parse(cmd);
        DefaultExecutor executor = new DefaultExecutor();
        ExecuteWatchdog watchdog = new ExecuteWatchdog(timeout);
        executor.setWatchdog(watchdog);
        try {
            int exitValue = executor.execute(commandLine);
            return (exitValue == 0);
        } catch (ExecuteException ex) {
            logger.info("Ошибка при выполнении команды «{}» {}", new Object[]{cmd, ex});
        } catch (IOException ex) {
            logger.info("Ошибка при выполнении команды «{}» {}", new Object[]{cmd, ex});
        }
        return false;
    }
}
