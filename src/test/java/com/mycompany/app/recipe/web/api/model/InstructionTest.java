package com.mycompany.app.recipe.web.api.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mycompany.app.recipe.web.api.model.Instruction;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class InstructionTest {
    private static final Integer ID_1 = 1;
    private static final String DIRECTION_1 = "Switch on the stove";
    private static final Integer ID_2 = 2;
    private static final String DIRECTION_2 = "Put Pan on the stove";
    
    @Test
    void equalsVerifier() throws Exception {
        Instruction instruction1 = new Instruction().stepNo(ID_1).stepDescription(DIRECTION_1);
        Instruction instruction2 = new Instruction().stepNo(ID_1).stepDescription(DIRECTION_1);
       
        assertThat(instruction1).isEqualTo(instruction2);

        instruction1.setStepDescription(DIRECTION_2);
        assertThat(instruction1).isNotEqualTo(instruction2);
        
        instruction1.setStepNo(null);
        assertThat(instruction1).isNotEqualTo(instruction2);
    }
}
