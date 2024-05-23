package recipes.api;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import org.hibernate.validator.constraints.Length;
import recipes.api.validator.StrictEmail;

@Getter
public class RegistrationUiDto {
    @StrictEmail
    @NotEmpty
    @NotBlank
    @JsonProperty("email")
    private String username;
    @NotEmpty
    @NotBlank
    @Length(min = 8)
    private String password;
}
