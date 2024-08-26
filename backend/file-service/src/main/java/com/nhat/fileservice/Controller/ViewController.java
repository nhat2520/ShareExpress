package com.nhat.fileservice.Controller;

import com.nhat.fileservice.Model.File;
import com.nhat.fileservice.Repository.FileRepository;
import com.nhat.fileservice.Response.FilesResponse;
import com.nhat.fileservice.Service.ViewService;
import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.validator.constraints.CodePointLength;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/home")
public class ViewController {
    private static final Logger log = LoggerFactory.getLogger(ViewController.class);
    @Autowired
    ViewService viewService;
    @Autowired
    private FileRepository fileRepository;

//    @GetMapping
//    public FilesResponse files(HttpServletRequest request) {
//        String username = request.getAttribute("username").toString();
//        Long userID = (Long) request.getAttribute("userID");
//
//        return viewService.getListInFolder(1L, userID);
//    }

    @GetMapping("/**")
    public ResponseEntity<FilesResponse> getFolderContent(HttpServletRequest request) {
        try {
            // Lấy đường dẫn từ URI yêu cầu
            String path = removeTrailingSlash(request.getRequestURI());

            // Lấy userID từ attribute (cần kiểm tra xem attribute có tồn tại không)
            Long userID = (Long) request.getAttribute("userID");

            if (userID == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            //Tao home neu chua co
            viewService.createHome(userID);

            // Lấy danh sách nội dung trong thư mục
            FilesResponse response = viewService.getListInFolder(path, userID);

            if (response == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Xử lý các lỗi không lường trước
            log.error("Error occurred while fetching folder content", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

//    @GetMapping("/folder={folderID}")
//    public String folder(@PathVariable Long folderID) {
//
//        return viewService.getListInFolder(folderID);
//    }


    public String removeTrailingSlash(String input) {
        if (input != null && input.endsWith("/")) {
            return input.substring(0, input.length() - 1);
        }
        return input;
    }

}
