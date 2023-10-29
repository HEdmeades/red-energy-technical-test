package com.example.redenergytechnicaltest.domain;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

public abstract class DomainObject {

    public void validate() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<Object>> errors = validator.validate(this);
        if(!errors.isEmpty()){
            throw new Exception(errors.stream().map(cv -> String.format("%s: %s %s", cv.getRootBean().getClass().getSimpleName(), cv.getPropertyPath(), cv.getMessage())).reduce("", String::concat));
        }
    }
}
