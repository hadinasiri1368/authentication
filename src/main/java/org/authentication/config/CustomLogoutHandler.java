package org.authentication.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.authentication.common.CommonUtils;
import org.authentication.common.JwtTokenUtil;
import org.authentication.model.User;
import org.authentication.service.TokenManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
public class CustomLogoutHandler implements LogoutHandler {
    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        String token = CommonUtils.getToken(request);
        if (CommonUtils.isNull(token))
            throw new RuntimeException("1004");
        User user = JwtTokenUtil.getUserFromToken(token);
        if (!CommonUtils.isNull(user)) {
            TokenManager.getInstance().removeTokenById(user.getId());
        }
    }
}
