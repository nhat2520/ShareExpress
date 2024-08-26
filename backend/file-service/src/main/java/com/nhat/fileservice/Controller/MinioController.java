package com.nhat.fileservice.Controller;

import com.nhat.fileservice.Repository.FileRepository;
import com.nhat.fileservice.Service.MinioService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
public class MinioController {
    private static final Logger log = LoggerFactory.getLogger(MinioController.class);
    @Autowired
    private MinioService minioService;

    @PostMapping("/folder")
    public ResponseEntity<String> createFolder(
            @RequestParam("name") String name,
            @RequestParam("parentFolderID") Long parentFolderID,
            HttpServletRequest request) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Folder name must not be empty");
        }

        try {
            Long userID = getUserID(request);
            if (userID == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            // Tạo thư mục mới
            minioService.createFolder(name, userID, parentFolderID);

            // Trả về phản hồi thành công
            return ResponseEntity.ok("Folder created successfully");

        } catch (MinioService.FolderAlreadyExistsException e) {
            // Xử lý trường hợp thư mục đã tồn tại
            log.warn("Folder creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Folder already exists");

        } catch (MinioService.InvalidFolderException e) {
            // Xử lý trường hợp thông tin thư mục không hợp lệ
            log.warn("Folder creation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid folder data");

        } catch (Exception e) {
            // Xử lý các lỗi không lường trước
            log.error("Folder creation failed due to an unexpected error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Folder creation failed due to an unexpected error.");
        }
    }


    @PostMapping("/file")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("folderID") Long folderID,
            HttpServletRequest request) {
        try {
            Long userID = getUserID(request);
            if (userID == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
            }

            minioService.uploadFile(file, userID, folderID);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (MinioService.InvalidFolderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid folder: " + e.getMessage());
        } catch (MinioService.FileAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("File already exists: " + e.getMessage());
        } catch (Exception e) {
            log.error("File upload failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed due to an unexpected error.");
        }
    }

//    @PostMapping("createFolder")
//    public String createFolder(@RequestParam("name") String name, @RequestParam("userID") Long userID, @RequestParam("parentFolderID" ) Long parentFolderID ) {
//        try {
//            minioService.createFolder(name, userID, parentFolderID);
//            return "Folder created successfully.";
//        } catch (Exception e) {
//            return "Folder creation failed.";
//        }
//    }

    @GetMapping("/download")
    public ResponseEntity<Object> downloadFile(@RequestParam("fileID") Long fileID, HttpServletRequest request) {
        try {
            // Tải file từ dịch vụ
            InputStream file = minioService.downloadFile(fileID, request);

            // Tạo InputStreamResource từ InputStream
            InputStreamResource resource = new InputStreamResource(file);

            // Trả về ResponseEntity với tiêu đề Content-Disposition để tải file xuống
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileID + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (Exception e) {
            // Ghi log lỗi và trả về lỗi 500
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error occurred while downloading file: " + e.getMessage());
        }
    }

    @DeleteMapping("/file")
    public ResponseEntity<Object> deleteFile(@RequestParam("fileID") Long fileID, HttpServletRequest request) {
        try {
            // Gọi phương thức xóa file trong service
            minioService.deleteFile(fileID, request);

            // Trả về phản hồi thành công với mã 204 No Content
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            // Xử lý lỗi chung và không xác định
            log.error("An error occurred while deleting the file. FileID: {}, UserID: {}", fileID, request.getAttribute("userID"), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the file");
        }
    }

    private Long getUserID(HttpServletRequest request) {
        return (Long) request.getAttribute("userID");
    }
}