package com.nestigo.systemdesign.nestigo.security;

import com.nestigo.systemdesign.nestigo.entities.UserEntity;
import com.nestigo.systemdesign.nestigo.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;



    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {



/* if Db lookup needs to be avoided
//for minimal db lookups principal is a String email, not UserEntity
        try {
            final String requestTokenHeader = request.getHeader("Authorization");

            // if we won't get the token
            if (requestTokenHeader == null || !requestTokenHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            System.out.println("URI=" + request.getRequestURI());
            System.out.println("Authorization header=" + request.getHeader("Authorization"));

            // if we get the token
            String token = requestTokenHeader.substring(7).trim();



            Long userId = jwtService.getUserId(token);     // from sub
            String email = jwtService.getEmail(token);     //from claim
            var authorities = jwtService.getRoles(token).stream()
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .toList();

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                //Minimal: use email as principal (no DB object)
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(email, null, authorities);

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // store userId in a request attribute instead:
                request.setAttribute("userId", userId);

                System.out.println("=== JWT DEBUG ===");
                System.out.println("Request URI: " + request.getRequestURI());
                System.out.println("User ID (JWT sub): " + userId);
                System.out.println("Email (JWT): " + email);
                System.out.println("Roles (JWT): " + jwtService.getRoles(token));
                System.out.println("Authorities (Spring): " + authorities);
                System.out.println("================");

                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}


 */

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorizationHeader.substring(7).trim();

        try {
            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                Long userId = jwtService.getUserId(token);

                // DB lookup
                UserEntity user = userService.getUserById(userId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
            return;
        }

        filterChain.doFilter(request, response);
     }
    }
