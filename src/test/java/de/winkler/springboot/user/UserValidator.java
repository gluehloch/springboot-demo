package de.winkler.springboot.user;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import de.winkler.springboot.user.UserEntityValidatorTest.User;

public class UserValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return false;
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "name", "name.empty");
        ValidationUtils.rejectIfEmpty(errors, "firstname", "firstname.empty");
        ValidationUtils.rejectIfEmpty(errors, "password", "password.empty");
        ValidationUtils.rejectIfEmpty(errors, "nickname", "nickname.empty");

        User user = (User) target;
        if (user.getAge() < 0) {
            errors.reject("age", "negativevalue");
        } else if (user.getAge() > 120) {
            errors.reject("age", "too.darn.old");
        }
    }

}
