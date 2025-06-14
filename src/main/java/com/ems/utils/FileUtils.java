package com.ems.utils;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUtils {
    private static final String UPLOAD_DIRECTORY = "event-images";

    public static String saveEventImage(Part filePart, String contextPath) throws IOException {
        // Create upload directory if it doesn't exist
        String applicationPath = contextPath;
        String uploadPath = applicationPath + File.separator + UPLOAD_DIRECTORY;

        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Generate unique filename
        String fileName = UUID.randomUUID().toString() + getFileExtension(filePart);

        // Save the file
        filePart.write(uploadPath + File.separator + fileName);

        // Return the relative path to be stored in database
        return UPLOAD_DIRECTORY + "/" + fileName;
    }

    private static String getFileExtension(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");

        for (String item : items) {
            if (item.trim().startsWith("filename")) {
                String fileName = item.substring(item.indexOf("=") + 2, item.length() - 1);
                int dotIndex = fileName.lastIndexOf(".");
                return dotIndex > 0 ? fileName.substring(dotIndex) : "";
            }
        }
        return "";
    }

    public static boolean deleteEventImage(String imageUrl, String contextPath) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return false;
        }

        try {
            Path imagePath = Paths.get(contextPath + File.separator + imageUrl);
            return Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            return false;
        }
    }
}