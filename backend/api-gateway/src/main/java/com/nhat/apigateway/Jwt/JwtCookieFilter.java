package com.nhat.apigateway.Jwt;

import ch.qos.logback.classic.Logger;
import jakarta.servlet.http.Cookie;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.net.URI;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtCookieFilter implements WebFilter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JwtCookieFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        log.info("path is " + exchange.getRequest().getURI().getPath());

        String path = exchange.getRequest().getURI().getPath();

        if (path.equals("/user/api/register") || path.equals("/user/api/login")) {
            log.info("check path...");
            return chain.filter(exchange);
        }

        if (!IsTokenExist(exchange)) {
            return this.redirectTo(exchange, "/user/api/login");
        }

        return chain.filter(exchange);
    }

    private Mono<Void> redirectTo(ServerWebExchange exchange, String url) {
        if (!exchange.getRequest().getURI().getPath().equals(url)) {
            exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
            exchange.getResponse().getHeaders().setLocation(URI.create(url));
            return exchange.getResponse().setComplete();
        }
        return Mono.empty();
    }

    private boolean IsTokenExist(ServerWebExchange exchange) {
        HttpCookie tokenCookie = exchange.getRequest().getCookies().getFirst("token");

        // Khởi tạo biến token
        String token;

        // Kiểm tra xem cookie "token" có tồn tại hay không
        if (tokenCookie != null) {
            // Nếu tồn tại, lấy giá trị của cookie
            token = tokenCookie.getValue();
        } else {
            // Nếu không tồn tại, gán token là null
            token = null;
        }

        if (token == null || token.isEmpty()) {
            return false;
        }
        return true;
    }
}