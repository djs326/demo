package com.example.system.auth.controller;

import com.example.common.model.Result;
import com.example.system.auth.dto.LoginReq;
import com.example.system.auth.service.AuthService;
import com.example.system.auth.utils.JwtUtil;
import com.example.system.auth.vo.LoginResp;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证管理", description = "认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<LoginResp> login(@Valid @RequestBody LoginReq req, HttpServletRequest request, HttpServletResponse response) {
        LoginResp loginResp = authService.login(req, request, response);
        return Result.success(loginResp);
    }

    @Operation(summary = "刷新Token")
    @PostMapping("/refresh")
    public Result<Void> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = getTokenFromCookie(request, "refresh_token");
        authService.refreshToken(refreshToken, response);
        return Result.success();
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = getTokenFromCookie(request, "access_token");
        if (accessToken != null && jwtUtil.validateToken(accessToken)) {
            Long userId = jwtUtil.getUserIdFromToken(accessToken);
            authService.logout(userId, request, response);
        } else {
            authService.logout(null, request, response);
        }
        return Result.success();
    }

    private String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
