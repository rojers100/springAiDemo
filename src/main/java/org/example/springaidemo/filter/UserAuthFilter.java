package org.example.springaidemo.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.springaidemo.context.UserContext;
import org.example.springaidemo.moudle.UserModel;
import org.example.springaidemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
public class UserAuthFilter implements Filter {

    @Autowired
    private UserService userService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            // 获取token
            String token = httpRequest.getHeader("token");
            if (!StringUtils.hasText(token)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Missing token");
                return;
            }

            // 获取用户信息
            UserModel user = userService.getUserByToken(token);
            if (user == null) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.getWriter().write("Invalid token");
                return;
            }

            // 设置用户上下文
            UserContext.setCurrentUser(user);

            // 继续处理请求
            chain.doFilter(request, response);
        } finally {
            // 清理用户上下文
            UserContext.clear();
        }
    }
} 