package de.winkler.springboot.user;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ObjectError;

class UserEntityValidatorTest {

    @Test
    void userValidator() {
        User frosch = new User();
        frosch.setName("Winkler");
        frosch.setFirstname("Andre");
        frosch.setAge(40);
        frosch.setNickname("Frosch");
        frosch.setPassword("password");

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

    public static class User {
        private String password;
        private String name;
        private String firstname;
        private String nickname;
        private int age;

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFirstname() {
            return firstname;
        }

        public void setFirstname(String firstname) {
            this.firstname = firstname;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

}
