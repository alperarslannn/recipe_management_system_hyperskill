package recipes.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import recipes.domain.Recipe;
import recipes.domain.RecipeRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
@Validated
public class RecipeUiController {

    private final RecipeRepository recipeRepository;

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeUiDto> getRecipe(@PathVariable Long recipeId){
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            return ResponseEntity.ok(RecipeUiDto.builder().name(recipe.getName()).description(recipe.getDescription()).ingredients(recipe.getIngredients()).directions(recipe.getDirections()).build());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/new")
    public ResponseEntity<AddRecipeUiDto> addRecipe(@Valid @RequestBody RecipeUiDto recipeUiDto){
        Recipe recipe = Recipe.builder().name(recipeUiDto.getName()).description(recipeUiDto.getDescription()).ingredients(recipeUiDto.getIngredients()).directions(recipeUiDto.getDirections()).build();
        recipeRepository.save(recipe);

        AddRecipeUiDto addRecipeUiDto = AddRecipeUiDto.builder().id(recipe.getId()).build();
        return ResponseEntity.ok(addRecipeUiDto);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<AddRecipeUiDto> deleteRecipe(@PathVariable Long recipeId){
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            recipeRepository.delete(recipe);
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.notFound().build();
    }
}
