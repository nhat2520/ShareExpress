package com.nhat.userservice.Controller;

import com.nhat.userservice.Jwt.JwtTokenProvider;
import com.nhat.userservice.Model.CustomUserDetails;
import com.nhat.userservice.Model.FileRequest;
import com.nhat.userservice.Request.LoginRequest;
import com.nhat.userservice.Model.User;
import com.nhat.userservice.Request.RegisterRequest;
import com.nhat.userservice.Response.LoginResponse;
import com.nhat.userservice.Response.RegisterResponse;
import com.nhat.userservice.Service.AuthService;
import com.nhat.userservice.Service.FileServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@RestController
@Slf4j
public class AuthController {
    private static final String FILE_SERVICE_URL = "http://file-service/resource";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    AuthService authService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FileServiceClient fileServiceClient;

//    private final com.nhat.userservice.Service.AuthService.FileServiceClient fileServiceClient;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

//    public AuthController(com.nhat.userservice.Service.AuthService.FileServiceClient fileServiceClient) {
//        this.fileServiceClient = fileServiceClient;
//    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("Admin Roles");
    }

    @PostMapping("/api/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        try {
            // Kiểm tra tính hợp lệ của thông tin đăng ký
            if (registerRequest.getUsername() == null || registerRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body(new RegisterResponse("Invalid input data", false));
            }

            // Sử dụng ModelMapper để map request sang User
            ModelMapper modelMapper = new ModelMapper();
            User user = modelMapper.map(registerRequest, User.class);

            // Đăng ký người dùng
            authService.registerUser(user);

            // Trả về phản hồi thành công
            return ResponseEntity.ok(new RegisterResponse("Register Successfully", true));

        } catch (AuthService.UserAlreadyExistsException e) {
            // Xử lý trường hợp người dùng đã tồn tại
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new RegisterResponse("User already exists", false));

        } catch (Exception e) {
            // Xử lý các ngoại lệ khác
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RegisterResponse("An error occurred during registration", false));
        }
    }

//    @PostMapping("/home")
//    public ResponseEntity<LoginResponse> testRestTemplate(@RequestBody FileRequest fileRequest) {
//        String url = FILE_SERVICE_URL + "/home";
//
//        // Gửi POST request và nhận phản hồi
//        ResponseEntity<LoginResponse> response = restTemplate.postForEntity(url, fileRequest, LoginResponse.class);
//
//        // Kiểm tra phản hồi từ file-service
//        if (response.getStatusCode() == HttpStatus.OK) {
//            // Trả về phản hồi thành công với token
//            return
//        } else {
//            // Trường hợp file-service trả về lỗi
//
//        }
//    }

    @PostMapping("/api/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.info("Attempting to login user {}", loginRequest.getUsername());
            // Xác thực người dùng với Username và Password
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lấy thông tin chi tiết của người dùng sau khi xác thực thành công
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Long userId = (((CustomUserDetails) userDetails).getId());

            // Tạo JWT token
            String token = jwtTokenProvider.createToken(authentication.getName(), userId);

            log.info("Send request to file-service");
            // Tạo home cho người dùng

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new LoginResponse(token, "Login Successfully", true));
        } catch (BadCredentialsException e) {
            // Trường hợp thông tin đăng nhập không chính xác
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse("", "Invalid username or password", false));

        } catch (ResourceAccessException e) {
            e.printStackTrace();
            // Trường hợp lỗi kết nối đến file-service
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new LoginResponse("", "File service unavailable", false));

        } catch (Exception e) {
            // Trường hợp các lỗi khác không xác định
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LoginResponse("", "An error occurred during login", false));
        }
    }


    public class FolderAlreadyExistsException extends RuntimeException {
        public FolderAlreadyExistsException(String message) {
            super(message);
        }
    }

}
