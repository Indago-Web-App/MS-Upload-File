package it.linearsystem.indago.config;

import jakarta.validation.*;
import jakarta.validation.executable.ExecutableValidator;
import jakarta.validation.metadata.BeanDescriptor;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author by Andrea Zaccanti
 * @Created 15 Maggio 2020
 */
@Component
public class RequestValidator implements Validator {

    @Override
    public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        Set<ConstraintViolation<T>> violations = validator.validate(object);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }

        return violations;

    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName, Class<?>... groups) {
        return null;
    }

    @Override
    public <T> Set<ConstraintViolation<T>> validateValue(Class<T> beanType, String propertyName, Object value, Class<?>... groups) {
        return null;
    }

    @Override
    public BeanDescriptor getConstraintsForClass(Class<?> clazz) {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> type) {
        return null;
    }

    @Override
    public ExecutableValidator forExecutables() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        factory.getValidator().forExecutables();
        /* ExecutableValidator executableValidator = */
        factory.getValidator().forExecutables();
//		return executableValidator;	
        return null;
    }

}
