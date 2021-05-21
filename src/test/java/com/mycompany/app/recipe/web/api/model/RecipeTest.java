package com.mycompany.app.recipe.web.api.model;

import java.util.List;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mycompany.app.recipe.web.api.model.Recipe;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class RecipeTest {
    private static final Integer INSTRUCTION_ID_1 = 1;
    private static final String DIRECTION_1 = "Switch on the stove";
    private static final Integer INSTRUCTION_ID_2 = 2;
    private static final String DIRECTION_2 = "Put the Pan on the stove";
    private static final String CREATION_DATE = "dd‐MM‐yyyy HH:mm";
    private static final String RECEIPE_NAME_1 = "egg burger";
    private static final String RECEIPE_NAME_2 = "omlette";
    private static final String RECEIPE_ID_1 = "egg-01";
    private static final String RECEIPE_ID_2 = "egg-02";
    
    @Test
    void equalsVerifier() throws Exception {
        Instruction instruction1 = new Instruction().stepNo(INSTRUCTION_ID_1).stepDescription(DIRECTION_1);
        Instruction instruction2 = new Instruction().stepNo(INSTRUCTION_ID_2).stepDescription(DIRECTION_2);

        List<Instruction> instructionList = new ArrayList<>();
        instructionList.add(instruction1);
        instructionList.add(instruction2);

        List<String> ingredientList = new ArrayList<>();
        ingredientList.add("egg");
        ingredientList.add("oil");

        Recipe recipe1 = new Recipe()
            .id(RECEIPE_ID_1)
            .name(RECEIPE_NAME_1)
            .creationDateTime(CREATION_DATE)
            .isVegetarian(false)
            .noOfPeopleCanEat(4)
            .cookingInstructions(instructionList)
            .ingredients(ingredientList);

        Recipe recipe2 = new Recipe()
            .id(RECEIPE_ID_1)
            .name(RECEIPE_NAME_1)
            .creationDateTime(CREATION_DATE)
            .isVegetarian(false)
            .noOfPeopleCanEat(4)
            .cookingInstructions(instructionList)
            .ingredients(ingredientList);

        assertThat(recipe1).isEqualTo(recipe2);

        recipe1.setId(RECEIPE_ID_2);
        assertThat(recipe1).isNotEqualTo(recipe2);
        
    }
}
