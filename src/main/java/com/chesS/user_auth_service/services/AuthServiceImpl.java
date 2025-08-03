package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.dto.request.LoginRequest;
import com.chesS.user_auth_service.dto.request.RegisterRequest;
import com.chesS.user_auth_service.dto.response.AuthResponse;
import com.chesS.user_auth_service.entities.Role;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.repositories.RoleRepository;
import com.chesS.user_auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final  UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JWTService jwtService;

    @Override
    public User Register(RegisterRequest registerRequest){

        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new RuntimeException("Email is already in use ! ");
        }

        Role defaultRole = roleRepository.findByName("PLAYER").orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .email(registerRequest.getEmail())
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .isVerified(false)
                .status(User.Status.ACTIVE)
                .provider(User.Provider.LOCAL)
                .role(defaultRole)
                .build() ;

        return userRepository.save(user);
    }


    @Override
    @PreAuthorize("permitAll")
    public AuthResponse Login(LoginRequest loginRequest){
        try{
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(
//                            loginRequest.getEmail(),
//                            loginRequest.getPassword()
//                    )
//            );
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new RuntimeException("Invalid credentials");
            }
            String accessToken = jwtService.generateToken(user) ;
            String refreshToken = jwtService.generateRefreshToken(user) ;

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().getName())
                    .build();
        } catch (AuthenticationException ex) {
            throw new IllegalArgumentException("Invalid credentials", ex);
        }
    }


}

