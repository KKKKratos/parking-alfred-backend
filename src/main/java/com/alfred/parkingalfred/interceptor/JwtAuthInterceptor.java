package com.alfred.parkingalfred.interceptor;

import com.alfred.parkingalfred.annotation.Authorize;
import com.alfred.parkingalfred.enums.RoleEnum;
import com.alfred.parkingalfred.utils.EnumUtil;
import com.alfred.parkingalfred.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Stream;

public class JwtAuthInterceptor implements HandlerInterceptor {

    private String[] permits = {"/**"};

    private static final AntPathMatcher matcher = new AntPathMatcher();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if (!(handler instanceof HandlerMethod))
            return true;

        String path = request.getRequestURI().substring(request.getContextPath().length());
        if (isExcludePath(path))
            return true;

        String token = request.getHeader("Authorization");
        if (token == null) {
            buildUnauthorizedResponse(response);
            return false;
        }

        token = token.replace("Bearer ", "");
        Claims claims = JwtUtil.getTokenBody(token);
        if (claims.isEmpty()) {
            buildUnauthorizedResponse(response);
            return false;
        }

        Authorize authorize = getAuthorize(handler);
        if (authorize == null)
            return true;
        if (checkAuthorize(authorize, claims))
            return true;

        buildUnauthorizedResponse(response);
        return false;
    }

    private boolean checkAuthorize(Authorize authorize, Claims claims) {
        Object roleClaim = claims.get("role");
        if (roleClaim == null) return false;

        RoleEnum role = EnumUtil.getByCode(Integer.parseInt(roleClaim.toString()), RoleEnum.class);
        for (RoleEnum r : authorize.value()) {
            if (r == role)
                return true;
        }
        return false;
    }

    private Authorize getAuthorize(Object handler) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Authorize authorize = handlerMethod.getMethod().getAnnotation(Authorize.class);
        if (authorize == null)
            authorize = handlerMethod.getMethod().getDeclaringClass().getAnnotation(Authorize.class);
        return authorize;
    }

    private void buildUnauthorizedResponse(HttpServletResponse response) throws IOException {
        response.setHeader("Content-type", "text/html;charset=UTF-8");
        response.setCharacterEncoding("utf-8");
        response.setStatus(401);
        response.getWriter().write("401 Unauthorized");
    }

    private boolean isExcludePath(String uri) {
        return Stream.of(permits).anyMatch(x -> matcher.match(x, uri));
    }
}
