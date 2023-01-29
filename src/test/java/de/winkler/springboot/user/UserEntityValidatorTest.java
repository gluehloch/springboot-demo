package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;

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
    }

}
