package com.example.redenergytechnicaltest.domain;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.validation.groups.Default;
import java.util.Set;

public class DomainObject {

    public void validate() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Object>> errors = validator.validate(this);
        if(!errors.isEmpty()){
            throw new Exception(errors.stream().map(cv -> String.format("%s: %s %s", cv.getRootBean().getClass().getSimpleName(), cv.getPropertyPath(), cv.getMessage())).reduce("", String::concat));
        }
    }
}
