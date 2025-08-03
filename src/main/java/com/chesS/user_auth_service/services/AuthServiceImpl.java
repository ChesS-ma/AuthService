package com.chesS.user_auth_service.services;

import com.chesS.user_auth_service.dto.request.LoginRequest;
import com.chesS.user_auth_service.dto.request.RegisterRequest;
import com.chesS.user_auth_service.entities.Role;
import com.chesS.user_auth_service.entities.User;
import com.chesS.user_auth_service.repositories.RoleRepository;
import com.chesS.user_auth_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PasswordEncoder passwordEncoder;
    private final  UserRepository userRepository;
    private final RoleRepository roleRepository;

    public User Register(RegisterRequest registerRequest){

        if(userRepository.existsByEmail(registerRequest.getEmail())){
            throw new RuntimeException("Email is already in use ! ");
        }

        Role defaultRole = roleRepository.findByName("PLAYER").orElseThrow(() -> new RuntimeException("Default role not found"));

        User user = User.builder()
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .isVerified(false)
                .status(User.Status.ACTIVE)
                .role(defaultRole)
                .build() ;

        return userRepository.save(user);
    }


    public User Login(LoginRequest loginRequest){
        User user = userRepository.findByEmail(loginRequest.getEmail()).orElseThrow( () -> new RuntimeException(" User not Found!") );

        if(!passwordEncoder.matches(loginRequest.getPassword() , user.getPassword())){
            throw new RuntimeException("Invalid Credentials! ");
        }
        //TODO: generate Tokens with jwtService ...



        return user ;
    }


}

