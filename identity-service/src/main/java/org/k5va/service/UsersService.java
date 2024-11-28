package org.k5va.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.CreateUserDto;
import org.k5va.entity.Role;
import org.k5va.entity.User;
import org.k5va.mappers.UserMapper;
import org.k5va.repository.RoleRepository;
import org.k5va.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public CvUserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format("User '%s' not found", userName))
                );

        List<SimpleGrantedAuthority> userAuthorities = user.getRoles().stream()
                .map(Role::getRole)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new CvUserDetails(
                user.getName(),
                user.getEmail(),
                user.getPassword(),
                userAuthorities
        );
    }

    public void create(CreateUserDto createUserDto) {
        User user = userMapper.toEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(roleRepository.findByRoleIn(createUserDto.roles()));

        userRepository.save(user);
    }
}