package com.nhat.fileservice.Service;

import com.nhat.fileservice.Model.File;
import com.nhat.fileservice.Model.Folder;
import com.nhat.fileservice.Model.Share;
import com.nhat.fileservice.Repository.FileRepository;
import com.nhat.fileservice.Repository.FolderRepository;
import com.nhat.fileservice.Repository.ShareRepository;
import com.nhat.fileservice.Request.ShareRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ShareService {
    private static final Logger log = LoggerFactory.getLogger(ShareService.class);
    @Autowired
    private ShareRepository shareRepository;
    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private FolderRepository folderRepository;

    public String createShareLink(ShareRequest shareRequest) {
        String token = UUID.randomUUID().toString();

        Share share = new Share();
        Long resourceID = shareRequest.getResourceID();
        Long userID = shareRequest.getUserID();
        share.setUserId(userID);
        if (shareRequest.getType().equals("file")) {
            log.info("type: file");
            share.setFileId(resourceID);
        } else {
            log.info("type: folder");
            share.setFolderId(resourceID);
        }
        share.setSharedWithUserId(shareRequest.getReceiverID());
        share.setToken(token);
        share.setSharedAt(LocalDateTime.now());

        shareRepository.save(share);
        return token;
    }


    public Object getShareItem(String token) throws Exception{
            Share share = shareRepository.findByToken(token);
            if (share == null) {
                throw new Exception("Share not found");
            }
           if (share.getFileId() != null) {
               File file = fileRepository.findById(share.getFileId()).orElse(null);
               return file;
           } else {
               Folder folder = folderRepository.findById(share.getFolderId()).orElse(null);
               return folder;
           }
    }
}
