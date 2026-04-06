package com.example.system.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.common.exception.BusinessException;
import com.example.common.exception.ErrorCode;
import com.example.system.auth.dto.LoginReq;
import com.example.system.auth.entity.SysUser;
import com.example.system.auth.mapper.SysUserMapper;
import com.example.system.auth.utils.JwtUtil;
import com.example.system.auth.vo.LoginResp;
import com.example.system.audit.entity.SysLoginLog;
import com.example.system.audit.service.SysOperLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenVersionService tokenVersionService;

    @Autowired
    private SysOperLogService sysOperLogService;

    @Value("${jwt.cookie.domain:localhost}")
    private String cookieDomain;

    @Value("${jwt.cookie.path:/}")
    private String cookiePath;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.http-only:true}")
    private boolean cookieHttpOnly;

    public LoginResp login(LoginReq req, HttpServletRequest request, HttpServletResponse response) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setUsername(req.getUsername());
        loginLog.setIpaddr(request.getRemoteAddr());

        try {
            LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysUser::getUsername, req.getUsername());
            SysUser user = sysUserMapper.selectOne(wrapper);

            if (user == null) {
                loginLog.setStatus(1);
                loginLog.setMsg("用户不存在");
                sysOperLogService.saveLoginLog(loginLog);
                throw new BusinessException(ErrorCode.USER_NOT_FOUND);
            }

            if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
                loginLog.setStatus(1);
                loginLog.setMsg("密码错误");
                sysOperLogService.saveLoginLog(loginLog);
                throw new BusinessException(ErrorCode.PASSWORD_ERROR);
            }

            if (user.getStatus() == 0) {
                loginLog.setStatus(1);
                loginLog.setMsg("用户已禁用");
                sysOperLogService.saveLoginLog(loginLog);
                throw new BusinessException(ErrorCode.USER_DISABLED);
            }

            user.setLastLoginTime(LocalDateTime.now());
            sysUserMapper.updateById(user);

            Long version = tokenVersionService.getCurrentVersion(user.getId());
            String accessToken = jwtUtil.generateAccessToken(user.getId(), version);
            String refreshToken = jwtUtil.generateRefreshToken(user.getId(), version);

            setCookies(response, accessToken, refreshToken);

            loginLog.setStatus(0);
            loginLog.setMsg("登录成功");
            sysOperLogService.saveLoginLog(loginLog);

            return new LoginResp(user.getId(), user.getUsername(), user.getName());
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            loginLog.setStatus(1);
            loginLog.setMsg(e.getMessage());
            sysOperLogService.saveLoginLog(loginLog);
            throw e;
        }
    }

    public void refreshToken(String refreshToken, HttpServletResponse response) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        Long userId = jwtUtil.getUserIdFromToken(refreshToken);
        Long tokenVersion = jwtUtil.getVersionFromToken(refreshToken);

        if (!tokenVersionService.validateTokenVersion(userId, tokenVersion)) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID);
        }

        Long newVersion = tokenVersionService.getCurrentVersion(userId);
        String newAccessToken = jwtUtil.generateAccessToken(userId, newVersion);
        String newRefreshToken = jwtUtil.generateRefreshToken(userId, newVersion);

        setCookies(response, newAccessToken, newRefreshToken);
    }

    public void logout(Long userId, HttpServletRequest request, HttpServletResponse response) {
        SysLoginLog loginLog = new SysLoginLog();
        loginLog.setIpaddr(request.getRemoteAddr());

        try {
            if (userId != null) {
                tokenVersionService.incrementVersion(userId);
            }
            clearCookies(response);

            loginLog.setStatus(0);
            loginLog.setMsg("登出成功");
            sysOperLogService.saveLoginLog(loginLog);
        } catch (Exception e) {
            loginLog.setStatus(1);
            loginLog.setMsg(e.getMessage());
            sysOperLogService.saveLoginLog(loginLog);
            throw e;
        }
    }

    private void setCookies(HttpServletResponse response, String accessToken, String refreshToken) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                .domain(cookieDomain)
                .path(cookiePath)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(1800)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshToken)
                .domain(cookieDomain)
                .path(cookiePath)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(604800)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }

    private void clearCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "")
                .domain(cookieDomain)
                .path(cookiePath)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(0)
                .sameSite("Strict")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "")
                .domain(cookieDomain)
                .path(cookiePath)
                .httpOnly(cookieHttpOnly)
                .secure(cookieSecure)
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
    }
}
