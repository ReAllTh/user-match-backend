package link.reallth.usermatchbackend.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * cross-origin filter
 *
 * @author ReAllTh
 */
@Component
public class CrosFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET POST");
        response.setHeader("Access-Control-Max-Age", "3600");
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        filterChain.doFilter(request, response);
    }
}
