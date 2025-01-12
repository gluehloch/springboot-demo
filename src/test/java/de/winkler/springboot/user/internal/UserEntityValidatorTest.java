package de.winkler.springboot.user.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

import de.winkler.springboot.user.Nickname;
import de.winkler.springboot.user.UserValidator;

class UserEntityValidatorTest {

    @Test
    void userValidator() {
        UserEntity frosch = UserEntity.UserBuilder
                .of(Nickname.of("Frosch"), "PasswordFrosch")
                .firstname("Andre")
                .name("Winkler")
                .age(40)
                .build();

        BeanPropertyBindingResult beanPropertyBindingResult = new BeanPropertyBindingResult(frosch, "frosch");
        UserValidator userValidator = new UserValidator();
        assertThat(beanPropertyBindingResult.getErrorCount()).isEqualTo(0);

        frosch.setAge(-1);
        userValidator.validate(frosch, beanPropertyBindingResult);
        assertThat(beanPropertyBindingResult.getErrorCount()).isEqualTo(1);
        assertThat(beanPropertyBindingResult.getTarget()).isEqualTo(frosch);


        assertThat(beanPropertyBindingResult.getGlobalErrors()).hasSize(1);
        ObjectError objectError = beanPropertyBindingResult.getGlobalErrors().get(0);
        assertThat(objectError.getCode()).isEqualTo("age");
        assertThat(objectError.getDefaultMessage()).isEqualTo("negativevalue");

        assertThat(beanPropertyBindingResult.getFieldErrors()).isEmpty();
    }

}
