package utils;

import aquality.selenium.core.logging.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileUtils {
    public static void createFolder(String nameFolder) {
        Path path = Paths.get(nameFolder);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                Logger.getInstance().error("Directory creation error: " + e);
            }
        }
    }

    public static void createNewFile(Path pathReport) {
        try {
            if (Files.exists(pathReport)) {
                Files.delete(pathReport);
            }
            Files.createFile(pathReport);
        } catch (IOException e) {
            Logger.getInstance().error("File creation error: " + e);
        }
    }

    public static void writeInFile(Path pathReport, String str) {
        try {
            Files.write(pathReport, str.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            Logger.getInstance().error("Error writing to file: " + e);
        }
    }
}