package com.chesS.user_auth_service.oauth;

import com.chesS.user_auth_service.entities.Role;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.entities.User.Provider;
import com.chesS.user_auth_service.entities.User.Status;
import com.chesS.user_auth_service.services.JWTService;
import com.chesS.user_auth_service.services.RefreshTokenService;
import com.chesS.user_auth_service.repositories.RoleRepository;
import com.chesS.user_auth_service.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest req,
                                        HttpServletResponse res,
                                        Authentication auth) throws IOException {

        User user;

        Object principal = auth.getPrincipal();
        if (principal instanceof SimpleOAuth2User simple) {
            // Case when your CustomOAuth2UserService returned our wrapper
            user = simple.getUser();
        } else if (principal instanceof OidcUser oidc) {
            // Google OIDC path (openid scope) -> DefaultOidcUser
            Map<String, Object> attrs = oidc.getAttributes();
            user = upsertFromAttributes("google", attrs);
        } else if (principal instanceof OAuth2User o2) {
            // Generic OAuth2 (non-OIDC) providers
            Map<String, Object> attrs = o2.getAttributes();
            user = upsertFromAttributes("google", attrs); // registrationId isn't directly here; for now it's Google
        } else {
            throw new IllegalStateException("Unsupported principal type: " + principal.getClass());
        }

        // Mint tokens using YOUR services
        String accessToken  = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        res.setContentType("application/json");
        res.getWriter().write("""
        {
          "tokenType":"Bearer",
          "accessToken":"%s",
          "refreshToken":"%s"
        }
        """.formatted(accessToken, refreshToken));
        res.getWriter().flush();
    }

    private User upsertFromAttributes(String provider, Map<String, Object> attrs) {
        String email = (String) attrs.get("email");
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Provider did not return an email.");
        }
        String name = (String) attrs.getOrDefault("name", attrs.get("given_name"));

        return userRepository.findByEmail(email).orElseGet(() -> {
            Role userRole = roleRepository.findByName("PLAYER")
                    .orElseThrow(() -> new IllegalStateException("Default role PLAYER not found"));
            User u = User.builder()
                    .email(email)
                    .username(name != null ? name : email)
                    .password(null)
                    .isVerified(true)
                    .status(Status.ACTIVE)
                    .provider(Provider.GOOGLE) // we’re handling Google now
                    .role(userRole)
                    .build();
            return userRepository.save(u);
        });
    }
}
