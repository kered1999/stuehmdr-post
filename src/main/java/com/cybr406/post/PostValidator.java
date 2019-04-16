package com.cybr406.post;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.util.Map;

public class PostValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(Post.class) || Map.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    if (target instanceof Map) {
      return;
    }

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "content",
        "field.required",
        "Content is a required field.");
  }
  
}
