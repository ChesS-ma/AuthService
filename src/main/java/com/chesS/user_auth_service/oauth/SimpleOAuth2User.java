package com.chesS.user_auth_service.oauth;

import com.chesS.user_auth_service.entities.User;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Data
public class SimpleOAuth2User implements OAuth2User {
    private final User user;
    private final Map<String, Object> attributes;

    public SimpleOAuth2User(User user, Map<String, Object> attributes) {
        this.user = user;
        this.attributes = attributes != null ? attributes : Collections.emptyMap();
    }

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        // If Role is entity with name field, use getRole().getName(); else adapt
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getName()));
    }

    @Override public String getName() {
        return user.getId().toString();
    }
}
