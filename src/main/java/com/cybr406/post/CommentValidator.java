package com.cybr406.post;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class CommentValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.equals(Comment.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "commentAuthor",
                "field.required",
                "Author is a required field.");

        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "commentContent",
                "field.required",
                "Content is a required field.");
        ValidationUtils.rejectIfEmptyOrWhitespace(
                errors,
                "postId",
                "field.required",
                "PostId is a required field"
        );
    }
}
