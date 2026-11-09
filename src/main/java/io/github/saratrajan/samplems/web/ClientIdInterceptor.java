package io.github.saratrajan.samplems.web;

import io.github.saratrajan.samplems.error.MissingClientIdException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;
import org.springframework.web.servlet.HandlerInterceptor;

/** Rejects order calls whose X-Client-Id header is missing or malformed. */
public class ClientIdInterceptor implements HandlerInterceptor {

    static final Pattern CLIENT_ID = Pattern.compile("^[a-z][a-z0-9-]{2,31}$");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientId = request.getHeader("X-Client-Id");
        if (clientId == null || !CLIENT_ID.matcher(clientId).matches()) {
            throw new MissingClientIdException("X-Client-Id header is missing or invalid");
        }
        return true;
    }
}
