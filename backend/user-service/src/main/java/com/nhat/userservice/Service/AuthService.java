package com.nhat.userservice.Service;

import com.nhat.userservice.Jwt.JwtTokenProvider;
import com.nhat.userservice.Model.FileRequest;
import com.nhat.userservice.Model.User;
import com.nhat.userservice.Repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    JwtTokenProvider jwtTokenProvider;



//    @Autowired
//    CustomUserDetailsService customUserDetailsService;
//
//    public boolean validateToken(String jwt) {
//        try {
//            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
//                String username = jwtTokenProvider.getUsername(jwt);
//                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//                        userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//                log.info("token pass");
//            } else {
//                return false;
//             }
//        } catch (Exception ex) {
//            log.error("Could not set user authentication in security context", ex);
//            return false;
//        }
//        return true;
//    }
//
public class  UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}

public User registerUser(User user) throws Exception {
    // Kiểm tra xem người dùng đã tồn tại hay chưa
    if (userRepository.existsByUsername(user.getUsername())) {
        throw new UserAlreadyExistsException("User already exists");
    }

    // Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu
    user.setPassword(encoder.encode(user.getPassword()));

    // Lưu người dùng vào cơ sở dữ liệu
    return userRepository.save(user);
}

//
//    private String getJwtFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }

    @Service
    public static class FileServiceClient {
        @Autowired
        private RestTemplate restTemplate;

        private static final String FILE_URL = "http://file-service/resource";

        public void createHome(FileRequest fileRequest) {
            String url = FILE_URL + "/home";
            restTemplate.put(url, fileRequest);
        }
    }
}
