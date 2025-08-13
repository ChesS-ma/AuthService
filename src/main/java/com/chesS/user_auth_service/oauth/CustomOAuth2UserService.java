package com.chesS.user_auth_service.oauth;

import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.entities.User.Provider;
import com.chesS.user_auth_service.entities.User.Status;
import com.chesS.user_auth_service.entities.Role;
import com.chesS.user_auth_service.repositories.RoleRepository;
import com.chesS.user_auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // must have findByName("USER")

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User delegate = super.loadUser(req);
        var attrs = delegate.getAttributes();

        String email = (String) attrs.get("email");
        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException("Google account has no email.");
        }
        String displayName = (String) attrs.getOrDefault("name", attrs.get("given_name"));

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            Role userRole = roleRepository.findByName("PLAYER")
                    .orElseThrow(() -> new IllegalStateException("Default role USER not found"));

            return userRepository.save(User.builder()
                    .email(email)
                    .username(displayName != null ? displayName : email)
                    .password(null)            // no password for social users
                    .isVerified(true)
                    .status(Status.ACTIVE)
                    .provider(Provider.GOOGLE)
                    .role(userRole)
                    .build());
        });

        // If account exists but provider not set, you can link it
        if (user.getProvider() == null) {
            user.setProvider(Provider.GOOGLE);
            userRepository.save(user);
        }
        if (user.getStatus() == null) {
            user.setStatus(Status.ACTIVE);
            userRepository.save(user);
        }

        return new SimpleOAuth2User(user, attrs);
    }
}
