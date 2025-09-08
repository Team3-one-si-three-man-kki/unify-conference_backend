package com.example.unicon.filter;

import com.example.unicon.infrastructure.redis.token.TokenBlacklistRepository;
import com.example.unicon.util.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        if (StringUtils.hasText(token)) {
            try {
                // 블랙리스트에 있는지 먼저 확인
                if (Boolean.TRUE.equals(tokenBlacklistRepository.isBlacklisted(token))) {
                    // 블랙리스트에 있다면 그냥 통과시켜서 뒤에서 403 에러가 나도록 함
                    filterChain.doFilter(request, response);
                    return;
                }

                // 토큰 유효성 검증 (만료 시 ExpiredJwtException 발생)
                if (jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    // request에 tenantId와 email 속성 추가
                    String tenantId = jwtTokenProvider.getTenantIdFromToken(token);
                    request.setAttribute("tenantId", tenantId);
                    request.setAttribute("email", email);

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (ExpiredJwtException e) {
                // [핵심!] 토큰이 '만료'된 경우, 401 Unauthorized 상태 코드를 응답
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\": \"토큰이 만료되었습니다.\"}");
                return; // 필터 체인을 여기서 중단
            } catch (Exception e) {
                // 그 외 다른 모든 예외의 경우
                SecurityContextHolder.clearContext();
                // 에러 로깅을 추가하면 좋습니다.
                // log.error("JWT Filter Error", e);
            }
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