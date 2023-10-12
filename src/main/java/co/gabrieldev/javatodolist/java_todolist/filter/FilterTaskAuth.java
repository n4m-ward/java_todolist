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
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String[] credentials = this.getCredentials(request);
        if(credentials == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        String username = credentials[0];
        UserModel user = this.userRepository.findByUsername(username);
        if(user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String password = credentials[1];
        if(!user.passwordMatch(password)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        System.out.println(username);
        System.out.println(password);
        filterChain.doFilter(request, response);
    }

    private String[] getCredentials(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if(authorization == null) {
            return null;
        }
        var authEncoded = authorization.substring("Basic ".length()).trim();
        byte[] authDecoded = Base64.getDecoder().decode(authEncoded);
        var authString = new String(authDecoded);
        return authString.split(":");
    }
}

