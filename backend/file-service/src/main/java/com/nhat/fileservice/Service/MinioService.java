package com.nhat.fileservice.Service;

import com.nhat.fileservice.Model.File;
import com.nhat.fileservice.Model.Folder;
import com.nhat.fileservice.Repository.FileRepository;
import com.nhat.fileservice.Repository.FolderRepository;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

@Service
public class MinioService {
    private static final Logger log = LoggerFactory.getLogger(MinioService.class);
    @Autowired
    MinioClient minioClient;

    @Autowired
    FileRepository fileRepository;

    @Autowired
    FolderRepository folderRepository;

    @Value("${minio.bucket-name}")
    private String bucketName;
    @Autowired
    private ViewService viewService;

    public void uploadFile(MultipartFile file, long userID, long folderID) throws Exception {
        if (file == null || file.isEmpty()) {
            throw new InvalidFolderException("File must not be empty");
        }

        Folder parentFolder = folderRepository.findFolderByID(folderID, userID);
        if (parentFolder == null) {
            throw new InvalidFolderException("Parent folder does not exist or you do not have permission");
        }

        String path = parentFolder.getPath();
        String objectName = userID + path + "/" + file.getOriginalFilename();
        log.info("Uploading file {}", objectName);

        // Kiểm tra nếu file đã tồn tại
        if (fileRepository.existsByPathAndOwnerId(path, userID)) {
            throw new FileAlreadyExistsException("File already exists at path: " + objectName);
        }

        // Lưu metadata file vào database trước
        File fileDB = new File();
        fileDB.setPath(path);
        fileDB.setName(file.getOriginalFilename());
        fileDB.setSize(file.getSize());
        fileDB.setFolder(parentFolder);
        fileDB.setOwnerId(userID);
        fileDB.setCreatedAt(LocalDateTime.now());
        fileDB.setUpdatedAt(LocalDateTime.now());
        fileRepository.save(fileDB);

        // Tải file lên MinIO
        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file input stream", e);
        }
    }


    public void createFolder(String foldername, Long userID, Long parentFolderID) throws FolderAlreadyExistsException, InvalidFolderException {
        // Kiểm tra tính hợp lệ của foldername và parentFolderID
        if (foldername == null || foldername.trim().isEmpty()) {
            throw new InvalidFolderException("Folder name must not be empty");
        }

        log.info("parentFolderID: " + parentFolderID);
        log.info("userID: " + userID);
        Folder parentFolder = folderRepository.findFolderByID(parentFolderID, userID);

        if (parentFolder == null) {
            throw new InvalidFolderException("Parent folder does not exist or you do not have permission");
        }

        // Kiểm tra nếu thư mục đã tồn tại
        String path = parentFolder.getPath() + "/" + foldername;
        if (folderRepository.existsByPathAndOwnerId(path, userID)) {
            throw new FolderAlreadyExistsException("Folder already exists at path: " + path);
        }

        // Tạo và lưu thư mục mới
        Folder folder = new Folder();
        folder.setPath(path);
        folder.setName(foldername);
        folder.setParentFolder(parentFolder);
        folder.setOwnerId(userID);
        folder.setCreatedAt(LocalDateTime.now());
        folder.setUpdatedAt(LocalDateTime.now());

        folderRepository.save(folder);
    }

    public class FolderAlreadyExistsException extends RuntimeException {
        public FolderAlreadyExistsException(String message) {
            super(message);
        }
    }

    public class InvalidFolderException extends RuntimeException {
        public InvalidFolderException(String message) {
            super(message);
        }
    }


    public InputStream downloadFile(Long fileID, HttpServletRequest request) throws Exception {
        Long userID = (Long) request.getAttribute("userID");
        log.info("UserID: " + userID);
        File file = fileRepository.findById(fileID, userID);
        String objectName = userID + file.getPath() + "/" + file.getName();

        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void deleteFile(Long fileID, HttpServletRequest request) throws Exception {
        Long userID = (Long) request.getAttribute("userID");
        log.info("UserID: " + userID);
        File file = fileRepository.findById(fileID, userID);
        String objectName = userID + file.getPath() + "/" + file.getName();
        log.info("Deleting file " + objectName);

        //xoa trong database
        fileRepository.delete(file);

        //xoa tren minio
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    public void createHome(Long userID) {
        Folder folder = new Folder();
        String path = "/resource/home";

        if (folderRepository.existsByPathAndOwnerId(path, userID)) {
            throw new FolderAlreadyExistsException("Folder already exists at path: " + path);
        }

        folder.setPath(path);
        folder.setName("home");
        folder.setOwnerId(userID);
        folder.setCreatedAt(LocalDateTime.now());
        folder.setUpdatedAt(LocalDateTime.now());

        folderRepository.save(folder);
    }
    public class FileAlreadyExistsException extends RuntimeException {
        public FileAlreadyExistsException(String message) {
            super(message);
        }
    }

}
