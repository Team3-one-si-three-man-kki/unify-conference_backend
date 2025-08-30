package com.example.unicon.token.service;

import jakarta.servlet.http.HttpServletRequest;

public interface TokenRefreshService {
    String reissueAccessToken(HttpServletRequest request);
}