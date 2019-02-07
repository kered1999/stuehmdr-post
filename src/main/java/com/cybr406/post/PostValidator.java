package com.cybr406.post;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PostValidator implements Validator {

  @Override
  public boolean supports(Class<?> clazz) {
    return clazz.equals(Post.class);
  }

  @Override
  public void validate(Object target, Errors errors) {
      ValidationUtils.rejectIfEmptyOrWhitespace(
          errors,
          "author",
          "field.required",
          "Author is a required field.");

    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors,
        "content",
        "field.required",
        "Author is a required field.");
  }
  
}
