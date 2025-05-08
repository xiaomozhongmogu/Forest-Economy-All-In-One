package edu.scau.forestproject.filter;

import edu.scau.forestproject.utils.JwtUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
@WebFilter(urlPatterns = "/*")
public class TokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取请求路径
        String requestURI = request.getRequestURI();

        //判断是否为登陆请求，如果是，则放行
        if (requestURI.contains("/login")) {
            log.info("登陆请求，放行");
            filterChain.doFilter(request, response);
            return;
        }

        //获取请求头中的token
        String token = request.getHeader("token");

        //判断token是否存在，如果不存在，则进入未登陆的主页
        if (token == null || token.isEmpty()) {
            log.info("token不存在");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }

        //如果token存在，则校验，若失败，则进入未登陆的主页
        try {
            JwtUtils.parseToken(token);
        } catch (Exception e) {
            log.info("token非法");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//401
            return;
        }

        //如果校验通过，进入登陆后的主页
        log.info("令牌合法，放行");
        filterChain.doFilter(request, response);

    }
}
