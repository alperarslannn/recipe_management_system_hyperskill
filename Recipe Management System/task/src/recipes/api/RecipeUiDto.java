package recipes.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeUiDto {
    @NotEmpty
    @NotBlank
    private String name;
    @NotEmpty
    @NotBlank
    private String description;
    @NotEmpty
    private List<String> ingredients;
    @NotEmpty
    private List<String> directions;
}
