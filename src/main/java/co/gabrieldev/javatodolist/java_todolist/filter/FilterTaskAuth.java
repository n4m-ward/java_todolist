package co.gabrieldev.javatodolist.java_todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import co.gabrieldev.javatodolist.java_todolist.user.IUserRepository;
import co.gabrieldev.javatodolist.java_todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        try {
            if (request.getServletPath().equals("/users/")) {
                filterChain.doFilter(request, response);
                return;
            }

            String[] credentials = this.getCredentials(request);
            String username = credentials[0];
            UserModel user = this.userRepository.findByUsername(username);

            if (user == null) {
                throw new Exception("Unauthorized");
            }

            String password = credentials[1];
            if (!user.passwordMatch(password)) {
                throw new Exception("Unauthorized");
            }
            request.setAttribute("userId", user.getId());
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private String[] getCredentials(HttpServletRequest request) throws Exception {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) {
            throw new Exception("Unauthorized");
        }
        var authEncoded = authorization.substring("Basic ".length()).trim();
        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
        var authString = new String(authDecoded);
        return authString.split(":");
    }
}
