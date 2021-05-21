package com.mycompany.app.recipe.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.app.recipe.exception.BadRequestException;
import com.mycompany.app.recipe.exception.ConflictException;
import com.mycompany.app.recipe.exception.NotFoundException;
import com.mycompany.app.recipe.repository.RecipeRepository;
import com.mycompany.app.recipe.web.api.RecipeApiDelegate;
import com.mycompany.app.recipe.web.api.model.Instruction;
import com.mycompany.app.recipe.web.api.model.Recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

@SpringBootTest
public class RecipeApiDelegateTest {
    private static final Integer INSTRUCTION_ID_1 = 1;
    private static final String DIRECTION_1 = "Switch on the stove";
    private static final Integer INSTRUCTION_ID_2 = 2;
    private static final String DIRECTION_2 = "Put the Pan on the stove";
    private static final String CREATION_DATE = "dd‐MM‐yyyy HH:mm";
    private static final String RECEIPE_NAME_1 = "egg burger";
    private static final String RECEIPE_NAME_2 = "omlette";
    private static final String RECEIPE_ID_1 = "egg-01";
    private static final String RECEIPE_HREF_1 = "http://localhost:8080/demo-api/recipeManagement/v1/";
    private static final String RECEIPE_ID_2 = "egg-02";
    private String recipeId = null;

    @Autowired
    private RecipeApiDelegate recipeApiDelegate;

    @Autowired
    private RecipeRepository recipeRepository;

    @BeforeEach
    public void init() {
        recipeRepository.deleteAll();
        
        Instruction instruction1 = new Instruction().stepNo(INSTRUCTION_ID_1).stepDescription(DIRECTION_1);
        Instruction instruction2 = new Instruction().stepNo(INSTRUCTION_ID_2).stepDescription(DIRECTION_2);

        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(instruction1);
        instructionList.add(instruction2);

        List<String> ingredientList = new ArrayList<>();
        ingredientList.add("egg");
        ingredientList.add("oil");

        Recipe recipe = new Recipe()
            .name(RECEIPE_NAME_1)
            .creationDateTime(CREATION_DATE)
            .isVegetarian(false)
            .noOfPeopleCanEat(4)
            .cookingInstructions(instructionList)
            .ingredients(ingredientList);

        recipeId = recipeApiDelegate.createRecipe(recipe).getBody().getId();
    }

    @Test()
    void createRecipeException() {
        assertThrows(RuntimeException.class, () -> {
            recipeApiDelegate.createRecipe(null);
        });

        recipeRepository.deleteAll();
        
        Instruction instruction1 = new Instruction().stepNo(INSTRUCTION_ID_1).stepDescription(DIRECTION_1);
        Instruction instruction2 = new Instruction().stepNo(INSTRUCTION_ID_2).stepDescription(DIRECTION_2);

        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(instruction1);
        instructionList.add(instruction2);

        List<String> ingredientList = new ArrayList<>();
        ingredientList.add("egg");
        ingredientList.add("oil");

        Recipe recipe = new Recipe()
            .id(RECEIPE_ID_1)
            .creationDateTime(CREATION_DATE)
            .isVegetarian(false)
            .noOfPeopleCanEat(4)
            .cookingInstructions(instructionList)
            .ingredients(ingredientList);

        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.createRecipe(recipe);
        });

        recipe.setId(null);
        recipe.setHref(RECEIPE_HREF_1);
        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.createRecipe(recipe);
        });

        recipe.setId(null);
        recipe.setHref(null);
        recipe.setName(null);
        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.createRecipe(recipe);
        });
    }

    @Test()
    void createRecipeConflictException() {
        Recipe recipe2 = new Recipe();
        recipe2.setName(RECEIPE_NAME_1);

        assertThrows(ConflictException.class, () -> {
            recipeApiDelegate.createRecipe(recipe2);
        });
    }

    @Test()
    void createRecipe() {
        Instruction instruction1 = new Instruction().stepNo(INSTRUCTION_ID_1).stepDescription(DIRECTION_1);
        Instruction instruction2 = new Instruction().stepNo(INSTRUCTION_ID_2).stepDescription(DIRECTION_2);

        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(instruction1);
        instructionList.add(instruction2);

        List<String> ingredientList = new ArrayList<>();
        ingredientList.add("egg");
        ingredientList.add("oil");

        Recipe recipe = new Recipe()
            .name(RECEIPE_NAME_2)
            .creationDateTime(CREATION_DATE)
            .isVegetarian(false)
            .noOfPeopleCanEat(4)
            .cookingInstructions(instructionList)
            .ingredients(ingredientList);

        ResponseEntity<Recipe> recipeEntity = recipeApiDelegate.createRecipe(recipe);

        assertNotNull(recipeEntity.getBody().getId());
        assertNotNull(recipeEntity.getBody().getName());        
    }

    @Test
    void retrieveRecipeException() { //String id, String fields
        String fields = "id,href,name";

        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.retrieveRecipe(null, fields);
        });  
        
        assertThrows(NotFoundException.class, () -> {
            recipeApiDelegate.retrieveRecipe("xyz", fields);
        });
    }

    @Test
    void retrieveRecipe() { //String id, String fields
        String fields = "name";

        ResponseEntity<Recipe> recipeEntity = recipeApiDelegate.retrieveRecipe(this.recipeId, fields);
        
        assertNotNull(recipeEntity.getBody().getId());
        assertNotNull(recipeEntity.getBody().getName());
        assertNull(recipeEntity.getBody().getHref());
        assertNull(recipeEntity.getBody().getCookingInstructions());
        assertNull(recipeEntity.getBody().getIngredients());
        assertNull(recipeEntity.getBody().getCreationDateTime());
        assertNull(recipeEntity.getBody().getIsVegetarian());
    }

    @Test
    void listRecipeException() {
        
        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.listRecipe(null, null, null, null, 101);
        });

        assertThrows(NotFoundException.class, () -> {
            recipeApiDelegate.listRecipe("name=Test", null, null, null, 100);
        });
    }

    @Test
    void listRecipe() {
        
        ResponseEntity<List<Recipe>> recipeEntityList = null;
        recipeEntityList = recipeApiDelegate.listRecipe(null, null, null, null, null);

        //assertEquals(recipeEntityList.getBody().size(), 1);

        Recipe recipe1 = new Recipe();
        recipe1.setName("name1");
        recipeApiDelegate.createRecipe(recipe1);
        
        Recipe recipe2 = new Recipe();
        recipe2.setName("name2");
        recipeApiDelegate.createRecipe(recipe2);
        
        Recipe recipe3 = new Recipe();
        recipe3.setName("name3");
        recipeApiDelegate.createRecipe(recipe3);
        
        Recipe recipe4 = new Recipe();
        recipe4.setName("name4");
        recipeApiDelegate.createRecipe(recipe4);
        
        Recipe recipe5 = new Recipe();
        recipe5.setName("name5");
        recipeApiDelegate.createRecipe(recipe5);

        List<String> fields = new ArrayList<>();
        fields.add("id");
        fields.add("href");
        fields.add("name");

        String filters = "name="+RECEIPE_NAME_1+","+"name5";
        String sort = "ASC=name";

        recipeEntityList = recipeApiDelegate.listRecipe(filters, fields, sort, null, null);

        assertEquals(recipeEntityList.getBody().size(), 2);

        Recipe recipeEntity = recipeEntityList.getBody().get(0);
        assertEquals(recipeEntity.getId(), this.recipeId);
        assertEquals(recipeEntity.getName(), RECEIPE_NAME_1);
    }

    @Test
    void patchRecipeException() {
        ResponseEntity<Recipe> recipeEntity = null;

        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.patchRecipe(null, null);
        });

        Map<String, String> data = new HashMap<>();
        
        assertThrows(NotFoundException.class, () -> {
            recipeApiDelegate.patchRecipe("sds", data);
        });

        data.put("id", this.recipeId);
        assertThrows(NotFoundException.class, () -> {
            recipeApiDelegate.patchRecipe("sds", data);
        });
    }

    @Test
    void patchRecipe() {
        List<String> ingredientList = Arrays.asList("egg", "salt", "tomato");
        
        Map<String, Object> data = new HashMap<>();
        data.put("ingredients", ingredientList);

        ResponseEntity<Recipe> recipeEntity = recipeApiDelegate.patchRecipe(this.recipeId, data);

        assertIterableEquals(ingredientList, recipeEntity.getBody().getIngredients());
    }

    @Test
    void updateRecipeException() {
        Recipe recipe = new Recipe();

        recipe.setId(null);
        recipe.setHref("http://localhost:8080/");
        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.updateRecipe("sds", recipe);
        });
        
        recipe.setHref(null);
        recipe.setName("name");
        assertThrows(BadRequestException.class, () -> {
            recipeApiDelegate.updateRecipe("sds", recipe);
        });

        recipe.setHref(null);
        recipe.setName(null);
        assertThrows(NotFoundException.class, () -> {
            recipeApiDelegate.updateRecipe("sds", recipe);
        });
    }

    @Test
    void updateRecipe() {
        Recipe recipe = new Recipe();
        List<String> ingredientList = Arrays.asList("egg", "salt", "tomato");

        recipe.setIngredients(ingredientList);
        ResponseEntity<Recipe> recipeEntity = recipeApiDelegate.updateRecipe(this.recipeId, recipe);
        
        assertNotNull(recipeEntity.getBody().getId());
        assertNotNull(recipeEntity.getBody().getName());
        assertNotNull(recipeEntity.getBody().getHref());
        assertIterableEquals(ingredientList, recipeEntity.getBody().getIngredients());    
        assertNull(recipeEntity.getBody().getCookingInstructions());
        assertNull(recipeEntity.getBody().getCreationDateTime());
        assertNull(recipeEntity.getBody().getIsVegetarian());
    }

    @Test
    void deleteRecipeException() {
        assertThrows(NotFoundException.class, () -> {
            recipeApiDelegate.deleteRecipe("sds");
        });
    }
    
    @Test
    void deleteRecipe() {
        ResponseEntity<Void> recipeEntity = recipeApiDelegate.deleteRecipe(this.recipeId);
        assertEquals(204, recipeEntity.getStatusCodeValue());
    }
}
