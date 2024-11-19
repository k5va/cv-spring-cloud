package org.k5va.contoller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.k5va.dto.AuthRequestDto;
import org.k5va.dto.CreateUserDto;
import org.k5va.service.AuthService;
import org.k5va.service.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UsersService usersService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody CreateUserDto userDto) {
        log.info("Register user {}", userDto.username());
        usersService.create(userDto);
        return ResponseEntity.ok("User created");
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody AuthRequestDto request) {
        log.info("Get token for user {}", request.username());
        return ResponseEntity.ok(authService.getToken(request).getToken());
    }
}
