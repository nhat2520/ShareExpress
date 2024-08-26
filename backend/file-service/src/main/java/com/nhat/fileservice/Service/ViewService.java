package com.nhat.fileservice.Service;

import com.nhat.fileservice.Model.File;
import com.nhat.fileservice.Model.Folder;
import com.nhat.fileservice.Repository.FileRepository;
import com.nhat.fileservice.Repository.FolderRepository;
import com.nhat.fileservice.Response.FilesResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ViewService {
    private static final Logger log = LoggerFactory.getLogger(ViewService.class);
    @Autowired
    FileRepository fileRepository;

    @Autowired
    FolderRepository folderRepository;

    public FilesResponse getListInFolder(String path, Long userID) {
        List<File> files = fileRepository.findByPath(path, userID);
        List<Folder> folders = folderRepository.findByPath(path, userID);
        return new FilesResponse(folders, files);
    }

    public void createHome(Long userID) {
        if (!folderRepository.existsByPathAndOwnerId("/resource/home", userID)) {
            log.info("Creating home folder");

            Folder folder = new Folder();
            String path = "/resource/home";

            folder.setPath(path);
            folder.setName("home");
            folder.setOwnerId(userID);
            folder.setCreatedAt(LocalDateTime.now());
            folder.setUpdatedAt(LocalDateTime.now());

            folderRepository.save(folder);
        }
    }
}
