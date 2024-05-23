package recipes.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import recipes.domain.Recipe;
import recipes.domain.RecipeRepository;
import recipes.domain.RegisteredUser;
import recipes.domain.RegisteredUserRepository;
import recipes.security.CustomUserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
@Validated
public class RecipeUiController {

    private final RecipeRepository recipeRepository;
    private final RegisteredUserRepository registeredUserRepository;

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeUiDto> getRecipe(@PathVariable Long recipeId){
        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            return ResponseEntity.ok(RecipeUiDto.builder()
                    .name(recipe.getName())
                    .description(recipe.getDescription())
                    .category(recipe.getCategory())
                    .date(recipe.getDate())
                    .ingredients(recipe.getIngredients())
                    .directions(recipe.getDirections())
                    .build());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping({"/search", "/search/"})
    public ResponseEntity<List<RecipeUiDto>> searchRecipe(@RequestParam(defaultValue = "") String category, @RequestParam(defaultValue = "") String name){
        if((!name.isEmpty() && !category.isEmpty()) || (name.isEmpty() && category.isEmpty())) {return ResponseEntity.badRequest().build();}

        List<Recipe> recipes = new ArrayList<>();
        if(!name.isEmpty()){
            recipes = recipeRepository.findAllByNameContainingIgnoreCaseOrderByDateDesc(name);
        }else{
            recipes = recipeRepository.findAllByCategoryEqualsIgnoreCaseOrderByDateDesc(category);
        }
        List<RecipeUiDto> filteredRecipes = recipes.stream().map(recipe -> RecipeUiDto.builder()
                .name(recipe.getName())
                .category(recipe.getCategory())
                .description(recipe.getDescription())
                .date(recipe.getDate())
                .ingredients(recipe.getIngredients())
                .directions(recipe.getDirections()).build()
        ).toList();
        return ResponseEntity.ok(filteredRecipes);
    }

    @PostMapping("/new")
    public ResponseEntity<AddRecipeUiDto> addRecipe(@Valid @RequestBody RecipeUiDto recipeUiDto, @AuthenticationPrincipal UserDetails userDetails){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Optional<RegisteredUser> usernameOptional = registeredUserRepository.findByUsername(customUserDetails.getUsername());
        if(usernameOptional.isEmpty()) { throw new UsernameNotFoundException(customUserDetails.getUsername()); }

        Recipe recipe = Recipe.builder()
                .name(recipeUiDto.getName())
                .description(recipeUiDto.getDescription())
                .date(LocalDateTime.now())
                .category(recipeUiDto.getCategory())
                .ingredients(recipeUiDto.getIngredients())
                .directions(recipeUiDto.getDirections())
                .registeredUser(usernameOptional.get())
                .build();
        recipeRepository.save(recipe);

        AddRecipeUiDto addRecipeUiDto = AddRecipeUiDto.builder().id(recipe.getId()).build();
        return ResponseEntity.ok(addRecipeUiDto);
    }

    @DeleteMapping("/{recipeId}")
    public ResponseEntity<AddRecipeUiDto> deleteRecipe(@PathVariable Long recipeId, @AuthenticationPrincipal UserDetails userDetails){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Optional<RegisteredUser> usernameOptional = registeredUserRepository.findByUsername(customUserDetails.getUsername());
        if(usernameOptional.isEmpty()) { throw new UsernameNotFoundException(customUserDetails.getUsername()); }

        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(recipeOptional.isPresent()) {
            if(!recipeOptional.get().getRegisteredUser().getUsername().equals(customUserDetails.getUsername())) {
                return ResponseEntity.status(403).build();
            }
            Recipe recipe = recipeOptional.get();
            recipeRepository.delete(recipe);
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{recipeId}")
    public ResponseEntity<AddRecipeUiDto> updateRecipe(@PathVariable Long recipeId, @Valid @RequestBody RecipeUiDto recipeUiDto, @AuthenticationPrincipal UserDetails userDetails){
        CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
        Optional<RegisteredUser> usernameOptional = registeredUserRepository.findByUsername(customUserDetails.getUsername());
        if(usernameOptional.isEmpty()) { throw new UsernameNotFoundException(customUserDetails.getUsername()); }

        Optional<Recipe> recipeOptional = recipeRepository.findById(recipeId);
        if(recipeOptional.isPresent()) {
            if(!recipeOptional.get().getRegisteredUser().getUsername().equals(customUserDetails.getUsername())) {
                return ResponseEntity.status(403).build();
            }

            Recipe recipe = recipeOptional.get();
            recipe.setName(recipeUiDto.getName());
            recipe.setDescription(recipeUiDto.getDescription());
            recipe.setCategory(recipeUiDto.getCategory());
            recipe.setDate(LocalDateTime.now());
            recipe.setIngredients(recipeUiDto.getIngredients());
            recipe.setDirections(recipeUiDto.getDirections());
            recipeRepository.save(recipe);
            return ResponseEntity.status(204).build();
        }
        return ResponseEntity.notFound().build();
    }
}
