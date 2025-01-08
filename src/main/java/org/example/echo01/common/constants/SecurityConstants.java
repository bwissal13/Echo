package org.example.echo01.common.constants;

public final class SecurityConstants {
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/v1/auth/register";
    public static final String LOGIN_URL = "/api/v1/auth/login";
    public static final String REFRESH_TOKEN_URL = "/api/v1/auth/refresh-token";
    public static final String LOGOUT_URL = "/api/v1/auth/logout";
    
    public static final String[] PUBLIC_URLS = {
        "/api/v1/auth/**",
        "/v2/api-docs",
        "/v3/api-docs",
        "/v3/api-docs/**",
        "/swagger-resources",
        "/swagger-resources/**",
        "/configuration/ui",
        "/configuration/security",
        "/swagger-ui/**",
        "/webjars/**",
        "/swagger-ui.html"
    };

    private SecurityConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
} 