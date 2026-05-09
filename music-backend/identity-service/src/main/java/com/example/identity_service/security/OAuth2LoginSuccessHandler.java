package com.example.identity_service.security;

import com.example.identity_service.dto.request.UserCreationRequest;
import com.example.identity_service.entity.User;
import com.example.identity_service.exception.AppException;
import com.example.identity_service.exception.ErrorCode;
import com.example.identity_service.repository.UserRepository;
import com.example.identity_service.service.AuthenticateService;
import com.example.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final AuthenticateService authService;
    private final UserService userService;
    private final UserRepository userRepository;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = authToken.getPrincipal();

        String sub = user.getAttribute("sub"); // OpenID
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String firstName = user.getAttribute("given_name");
        String lastName = user.getAttribute("family_name");
        String picture = user.getAttribute("picture");
        if (!userRepository.existsByUsername("google_"+sub)) {
            UserCreationRequest request1 = UserCreationRequest.builder()
                    .username("google_" + sub)
                    .email(email)
                    .firstName(firstName)
                    .lastName(lastName)
                    .displayName(firstName + " " + lastName)
                    .roles(Set.of("USER"))
                    .displayName(name)
                    .avatar(picture)
                    .provider("GOOGLE")
                    .build();
            userService.createUser(request1);
        }
        User userEntity = userRepository.findByUsername("google_" + sub).orElseThrow(() -> new AppException(ErrorCode.UNCATEGORIZED));
        String jwt = authService.generateToken(userEntity);
        response.sendRedirect("http://localhost:4200/auth/success?token="+jwt);
    }
}
