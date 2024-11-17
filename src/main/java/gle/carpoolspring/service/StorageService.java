package gle.carpoolspring.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class StorageService {

    private final Path rootLocation = Paths.get("uploads"); // Folder where photos will be stored

    // Constructor to create the upload folder if it doesn't exist
    public StorageService() {
        try {
            Files.createDirectories(rootLocation);  // Ensure the directory exists
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    // Method to store the file and return its name
    public String store(MultipartFile file) {
        try {
            // Get the original filename
            String filename = file.getOriginalFilename();

            // Define the path where the file will be stored
            Path destinationPath = rootLocation.resolve(filename);

            // Save the file
            file.transferTo(destinationPath);

            return filename;  // Return the filename or file path (can be stored in the DB)
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}
