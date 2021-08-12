package main.controller;

import main.api.request.UserLoginRequest;
import main.service.auth.LoginLogoutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/v1/auth")
public class ApiAuthController {
    private final LoginLogoutService loginLogoutService;

    public ApiAuthController(LoginLogoutService loginLogoutService) {
        this.loginLogoutService = loginLogoutService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> getUserLoginResponse(@RequestBody UserLoginRequest userLoginRequest) {
        return loginLogoutService.loginUser(userLoginRequest.getEmail(), userLoginRequest.getPassword());
    }

    @PostMapping("/logout")
    public ResponseEntity<?> getUserLogoutResponse(HttpServletRequest request, HttpServletResponse response) {
        return loginLogoutService.logoutUser(request, response);
    }
}
