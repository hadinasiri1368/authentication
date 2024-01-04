package org.authentication.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.authentication.common.CommonUtils;
import org.authentication.common.JwtTokenUtil;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class CheckPermission extends OncePerRequestFilter implements Filter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = CommonUtils.getToken(request);
        String message = CommonUtils.getTokenValidationMessage(token);
        boolean flag = false;
        if (CommonUtils.isNull(message)) {
            message = "you dont have permission";
            if (CommonUtils.hasPermission(JwtTokenUtil.getUserFromToken(token), request.getRequestURI())) {
                flag = true;
            }
        }
        if (flag) {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                response.getWriter().write(e.getMessage());
            }
        } else {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().write(message);
        }
    }
}
