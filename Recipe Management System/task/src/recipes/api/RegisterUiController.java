package recipes.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recipes.domain.RegisteredUser;
import recipes.domain.RegisteredUserRepository;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
@Validated
public class RegisterUiController {

    private final RegisteredUserRepository registeredUserRepository;
    private final BCryptPasswordEncoder encoder;

    @PostMapping("")
    public ResponseEntity<Void> addUser(@Valid @RequestBody RegistrationUiDto registrationUiDto){
        if(registeredUserRepository.findByUsername(registrationUiDto.getUsername()).isEmpty()){
            String salt = BCrypt.gensalt();
            String hashedPassword = hashPassword(registrationUiDto.getPassword(), salt);

            RegisteredUser registeredUser = RegisteredUser.builder()
                    .username(registrationUiDto.getUsername())
                    .password(hashedPassword)
                    .salt(salt)
                    .build();
            registeredUserRepository.save(registeredUser);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    private String hashPassword(String password, String salt) {
        return encoder.encode(password + salt);
    }
}
