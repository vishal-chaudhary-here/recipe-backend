package com.mycompany.app.recipe.web.api.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import com.mycompany.app.recipe.web.api.model.Error;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ErrorTest {
    private static final String ERROR_CODE = "test";
    private static final String ERROR_MESSAGE_1 = "Error Message 1";
    private static final String ERROR_MESSAGE_2 = "Error Message 2";

    @Test
    void equalsVerifier() throws Exception {
        Error error1 = new Error();
        error1.setCode(ERROR_CODE);
        error1.setMessage(ERROR_MESSAGE_1);
        Error error2 = new Error();
        error2.setCode(ERROR_CODE);
        error2.setMessage(ERROR_MESSAGE_1);
       
        assertThat(error1).isEqualTo(error2);
        error1.setMessage(ERROR_MESSAGE_2);
        assertThat(error1).isNotEqualTo(error2);
        error1.setCode(null);
        assertThat(error1).isNotEqualTo(error2);
    }
}
