package it.linearsystem.indago.config;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
//import org.springframework.validation.Validator;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
//import javax.xml.bind.Validator;
import javax.validation.executable.ExecutableValidator;
import javax.validation.metadata.BeanDescriptor;

import org.springframework.stereotype.Component;
 
/**
 * 
 * @author by Andrea Zaccanti
 * @Created 15 Maggio 2020
 * 
 */
@Component
public class RequestValidator implements Validator {
    
	@Override
	public <T> Set<ConstraintViolation<T>> validate(T object, Class<?>... groups) {
		
	    javax.validation.ValidatorFactory factory = javax.validation.Validation.buildDefaultValidatorFactory();
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
		/* ExecutableValidator executableValidator = */ factory.getValidator().forExecutables();
//		return executableValidator;	
		return null;
	}
	
}
