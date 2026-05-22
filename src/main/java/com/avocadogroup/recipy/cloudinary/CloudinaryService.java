package com.avocadogroup.recipy.cloudinary;

import com.avocadogroup.recipy.common.exceptions.InternalServerErrorException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Service for managing file uploads and deletions via Cloudinary.
 *
 * <p>This service wraps the Cloudinary SDK and provides reusable methods
 * for uploading and deleting files. It returns secure URLs suitable for
 * storing in the database instead of raw file data.</p>
 *
 * <p>Usage example:</p>
 * <pre>
 *   String url = cloudinaryService.uploadFile(multipartFile, "avatars");
 *   cloudinaryService.deleteFile("avatars/abc123");
 * </pre>
 *
 * @see com.avocadogroup.recipy.common.configs.CloudinaryConfig
 */
@Slf4j // Enable Lombok SLF4J logging for this service
@Service // Marks this class as a Spring service bean
@AllArgsConstructor // Generates a constructor with all fields for dependency injection
public class CloudinaryService {
    private final Cloudinary cloudinary; // The Cloudinary SDK client from CloudinaryConfig

    /**
     * Uploads a file to Cloudinary and returns its secure HTTPS URL
     *
     * <p>The file is uploaded to the specified folder with automatic
     * resource type detection (image, video, raw, etc.).</p>
     *
     * @param file   the {@link MultipartFile} to upload (must not be {@code null} or empty)
     * @param folder the Cloudinary folder to organize the upload (e.g., "avatars", "documents")
     * @return the secure URL of the uploaded file
     * @throws InternalServerErrorException if the upload fails due to an I/O error
     */
    public String uploadFile(MultipartFile file, String folder) {
        try {
            // Upload the file bytes to Cloudinary
            var result = cloudinary.uploader().upload(
                    file.getBytes(), // Convert the MultipartFile to a byte array for upload
                    ObjectUtils.asMap( // Build the upload options map
                            "folder", folder, // Organize the upload into the specified folder
                            "resource_type", "auto" // Let Cloudinary auto-detect the file type
                    ));

            // Extract and return the secure HTTPS URL
            return (String) result.get("secure_url");
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to upload file: " + e.getMessage());
        }
    }

    /**
     * Deletes a file from Cloudinary using its Public ID
     *
     * <p>The public ID can be extracted from the Cloudinary URL. For example,
     * a file at {@code https://res.cloudinary.com/.../avatars/abc123.jpg}
     * has the public ID {@code avatars/abc123}.</p>
     *
     * @param publicId the Cloudinary public ID of the file to delete
     * @throws InternalServerErrorException if the deletion fails due to an I/O error
     */
    public void deleteFile(String publicId) {
        try {
            // Delete the file by its public ID
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            throw new InternalServerErrorException("Failed to delete file: " + e.getMessage());
        }
    }

//    /**
//     * Deletes a file from Cloudinary using its secure URL.
//     *
//     * <p>Extracts the public ID from the Cloudinary URL and deletes the file.
//     * If deletion fails, the error is logged and swallowed so it does not
//     * block the calling operation.</p>
//     *
//     * @param imageUrl the full Cloudinary secure URL of the file to delete
//     */
//    public void deleteFileByUrl(String imageUrl) {
//        try {
//            // Safety validation
//            if (imageUrl == null || imageUrl.isBlank()) {
//                // If null, nothing to delete
//                return;
//            }
//
//            // Parse the public ID from the full URL
//            var publicId = extractPublicIdFromUrl(imageUrl);
//
//            // If parsing failed
//            if (publicId == null) {
//                // Log the warning
//                log.warn("Could not extract public ID from Cloudinary URL: {}", imageUrl);
//
//                // Exit without throwing since we can't proceed
//                return;
//            }
//
//            // Delegate to the public-ID-based delete method
//            deleteFile(publicId);
//        } catch (Exception e) { // Catch any unexpected errors gracefully
//            log.warn("Failed to delete old image from Cloudinary: {}", e.getMessage()); // Log and swallow
//        }
//    }
//
//    /**
//     * Extracts the Cloudinary public ID from a full secure URL.
//     *
//     * <p>Cloudinary URL format:</p>
//     * {@code https://res.cloudinary.com/<cloud>/<type>/upload/v<version>/<folder>/<file>.<ext>}
//     *
//     * <p>Extracted public ID: {@code <folder>/<file>}</p>
//     *
//     * @param imageUrl the full Cloudinary secure URL
//     * @return the public ID, or {@code null} if it could not be extracted
//     */
//    private String extractPublicIdFromUrl(String imageUrl) {
//        try {
//            // Parse the string as a URL object
//            var url = new URL(imageUrl);
//
//            // Get the path component (e.g., /demo/image/upload/v1234/recipes/abc.jpg)
//            var path = url.getPath();
//
//            // Find the "/upload/" segment in the path
//            var uploadIndex = path.indexOf("/upload/");
//
//            // Check if the URL is not a Cloudinary URL (-1)
//            if (uploadIndex == -1) {
//                // If cannot extract public ID then return null
//                return null;
//            }
//
//            // Get everything after "/upload/"
//            var afterUpload = path.substring(uploadIndex + "/upload/".length()); // substring(startIndex)
//
//            if (afterUpload.startsWith("v")) { // Check for the version prefix (e.g., "v1234/")
//                var nextSlash = afterUpload.indexOf('/'); // Find the slash after the version
//                if (nextSlash != -1) { // Ensure there is a slash present
//                    afterUpload = afterUpload.substring(nextSlash + 1); // Remove the version prefix
//                }
//            }
//
//            var extIndex = afterUpload.lastIndexOf('.'); // Find the last dot for the file extension
//            if (extIndex != -1) { // Check if an extension exists
//                afterUpload = afterUpload.substring(0, extIndex); // Remove the file extension
//            }
//
//            return afterUpload; // Return the extracted public ID (e.g., "recipes/abc")
//        } catch (Exception e) { // Handle any malformed URL or parsing errors
//            log.warn("Failed to parse Cloudinary URL: {}", e.getMessage()); // Log the warning
//            return null; // Return null to indicate parsing failure
//        }
//    }
}
