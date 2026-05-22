//package com.avocadogroup.recipy.cloudinary;
//
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
///**
// * Service for managing file uploads and deletions via Cloudinary.
// *
// * <p>This service wraps the Cloudinary SDK and provides reusable methods
// * for uploading and deleting files. It returns secure URLs suitable for
// * storing in the database instead of raw file data.</p>
// *
// * <p>Usage example:</p>
// * <pre>
// *   String url = cloudinaryService.uploadFile(multipartFile, "avatars");
// *   cloudinaryService.deleteFile("avatars/abc123");
// * </pre>
// *
// * @see com.avocadogroup.recipy.common.configs.CloudinaryConfig
// */
//@Service
//@AllArgsConstructor
//public class CloudinaryService {
//    private final Cloudinary cloudinary;
//
//    /**
//     * Uploads a file to Cloudinary and returns its secure HTTPS URL
//     *
//     * <p>The file is uploaded to the specified folder with automatic
//     * resource type detection (image, video, raw, etc.).</p>
//     *
//     * @param file   the {@link MultipartFile} to upload (must not be {@code null} or empty)
//     * @param folder the Cloudinary folder to organize the upload (e.g., "avatars", "documents")
//     * @return the secure URL of the uploaded file
//     * @throws InternalServerErrorException if the upload fails due to an I/O error
//     */
//    public String uploadFile(MultipartFile file, String folder) {
//        try {
//            // Upload the file to Cloudinary
//            Map result = cloudinary.uploader().upload(
//                    file.getBytes(), ObjectUtils.asMap( // Configuration Options
//                            "folder", folder, // Folder to organize the upload
//                            "resource_type", "auto" // Automatic resource type detection
//                    ));
//
//            // Generate the url and return it
//            return (String) result.get("secure_url");
//        } catch (IOException e) {
//            throw new InternalServerErrorException("Failed to upload file: " + e.getMessage());
//        }
//    }
//
//    /**
//     * Deletes a file from Cloudinary using its Public ID
//     *
//     * <p>The public ID can be extracted from the Cloudinary URL. For example,
//     * a file at {@code https://res.cloudinary.com/.../avatars/abc123.jpg}
//     * has the public ID {@code avatars/abc123}.</p>
//     *
//     * @param publicId the Cloudinary public ID of the file to delete
//     * @throws InternalServerErrorException if the deletion fails due to an I/O error
//     */
//    public void deleteFile(String publicId) {
//        try {
//            // Delete the file from Cloudinary
//            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
//        } catch (IOException e) {
//            throw new InternalServerErrorException("Failed to delete file: " + e.getMessage());
//        }
//    }
//}
