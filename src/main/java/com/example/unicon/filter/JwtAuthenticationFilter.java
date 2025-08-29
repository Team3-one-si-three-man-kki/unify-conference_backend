package com.example.unicon.filter;

import com.example.unicon.infrastructure.redis.token.TokenBlacklistRepository;
import com.example.unicon.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // ... (필드 및 shouldNotFilter 메소드는 동일)
    private static final String BEARER_PREFIX = "Bearer ";
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final UserDetailsService userDetailsService;
    private final List<String> permitAllPatterns = List.of(
            "/api/guest/**", "/health", "/actuator/**",
            "/css/**", "/js/**", "/images/**"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        for (String pattern : permitAllPatterns) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return "OPTIONS".equalsIgnoreCase(request.getMethod());
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        if (StringUtils.hasText(token)) {
            if (Boolean.TRUE.equals(tokenBlacklistRepository.isBlacklisted(token))) {
                request.setAttribute("jwtAuthenticated", false);
                filterChain.doFilter(request, response);
                return;
            }

            boolean valid = jwtTokenProvider.validateToken(token);

            if (valid) {
                try {
                    // 3) 클레임 추출 (userId -> email)
                    String email    = jwtTokenProvider.getEmailFromToken(token); // getUserIdFromToken -> getEmailFromToken
                    String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
                    String role     = jwtTokenProvider.getRoleFromToken(token);
                    Boolean isActive= jwtTokenProvider.getIsActiveFromToken(token);

                    // 4) SecurityContext 설정 (UserDetailsService에 email 전달)
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email); // userId -> email
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    request.setAttribute("jwtAuthenticated", true);
                    request.setAttribute("email", email); // 속성 이름도 변경하면 좋음
                    request.setAttribute("tenantId", tenantId);
                    request.setAttribute("userRole", role);
                    request.setAttribute("isActive", isActive);

                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    request.setAttribute("jwtAuthenticated", false);
                }
            } else {
                request.setAttribute("jwtAuthenticated", false);
            }
        } else {
            request.setAttribute("jwtAuthenticated", false);
        }

        filterChain.doFilter(request, response);
    }

    // ... (resolveToken 메소드는 동일)
    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(BEARER_PREFIX)) {
            return bearer.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}