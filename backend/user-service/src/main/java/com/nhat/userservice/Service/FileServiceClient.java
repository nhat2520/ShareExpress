package com.nhat.userservice.Service;

import com.nhat.userservice.Model.FileRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FileServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    private static final String FILE_SERVICE_URL = "http://file-service/resource";

    public ResponseEntity<String> createHome(FileRequest fileRequest) {
        String url = FILE_SERVICE_URL + "/home";
        try {
            return restTemplate.postForEntity(url, fileRequest, String.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
