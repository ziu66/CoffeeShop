/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coffeeshop;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import javax.swing.ImageIcon;

public class ImageHandler {
    // Directory where images will be stored (relative to project root)
    private static final String IMAGE_DIR = "images/";
    private static final String RESOURCE_DIR = "src/main/resources/images/";
    
    public static String saveUploadedImage(File sourceFile) throws IOException {
        // Create directories if they don't exist
        new File(IMAGE_DIR).mkdirs();
        new File(RESOURCE_DIR).mkdirs();
        
        // Generate unique filename with timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String originalName = sourceFile.getName();
        String fileExtension = originalName.substring(originalName.lastIndexOf("."));
        String newFileName = "product_" + timeStamp + "_" + UUID.randomUUID().toString().substring(0, 8) + fileExtension;
        
        // Save to both locations
        Path target1 = Paths.get(IMAGE_DIR + newFileName);
        Path target2 = Paths.get(RESOURCE_DIR + newFileName);
        
        Files.copy(sourceFile.toPath(), target1, StandardCopyOption.REPLACE_EXISTING);
        Files.copy(sourceFile.toPath(), target2, StandardCopyOption.REPLACE_EXISTING);
        
        return newFileName; // Return just the filename
    }
    
    public static void loadItemImage(MenuItem item) {
        if (item.getImageUrl() == null || item.getImageUrl().isEmpty()) {
            return;
        }
        
        try {
            // Try to load from resource path first
            String resourcePath = "/images/" + item.getImageUrl();
            java.net.URL imageUrl = ImageHandler.class.getResource(resourcePath);
            
            if (imageUrl != null) {
                ImageIcon originalIcon = new ImageIcon(imageUrl);
                Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                item.setImageIcon(new ImageIcon(scaledImage));
                return;
            }
            
            // Fall back to file system if not found in resources
            File imageFile = new File(IMAGE_DIR + item.getImageUrl());
            if (imageFile.exists()) {
                ImageIcon originalIcon = new ImageIcon(imageFile.getAbsolutePath());
                Image scaledImage = originalIcon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                item.setImageIcon(new ImageIcon(scaledImage));
            }
        } catch (Exception e) {
            System.err.println("Error loading image for " + item.getName() + ": " + e.getMessage());
        }
    }
}