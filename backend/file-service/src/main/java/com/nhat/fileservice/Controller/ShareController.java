package com.nhat.fileservice.Controller;

import com.nhat.fileservice.Request.ShareRequest;
import com.nhat.fileservice.Service.ShareService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ShareController {
    private static final Logger log = LoggerFactory.getLogger(ShareController.class);
    @Autowired
    ShareService shareService;

    @PostMapping("/share")
    public ResponseEntity<String> shareItem(@RequestBody ShareRequest shareRequest) {
        try {
            String token = shareService.createShareLink(shareRequest);
            String respone = "http://localhost:8080/resource/share/" + token;
            return ResponseEntity.ok(respone);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/share/{token}")
    public ResponseEntity<String> getShareLink(@PathVariable String token, HttpServletRequest request) {
        try {
//            Long userID = (Long) request.getAttribute("userID");
            log.info("token: " + token);
            Object O = shareService.getShareItem(token);
            log.info("O: " + O);

            return ResponseEntity.ok("oke");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
