package org.authentication.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.authentication.common.CommonUtils;
import org.authentication.common.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = CommonUtils.getToken(request);
        if (CommonUtils.isNull(token)) {
            filterChain.doFilter(request, response);
            return;
        }
        if (CommonUtils.isNull(SecurityContextHolder.getContext().getAuthentication())) {
            UserDetails userDetails = JwtTokenUtil.getUserDetails(request);
            if (JwtTokenUtil.validateToken(token)) {
                CommonUtils.checkValidationToken(token, request.getRequestURI());
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                filterChain.doFilter(request, response);
            } else{
                log.info("RequestURL:" + request.getRequestURL() + "  UUID=" + request.getHeader("X-UUID") +"   message=token.is.not.valid");
                throw new RuntimeException("token.is.not.valid");
            }

        }
    }
}
