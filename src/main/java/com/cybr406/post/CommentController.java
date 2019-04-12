package com.cybr406.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class CommentController {

    private CommentRepository commentRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository){this.commentRepository = commentRepository;}

    @InitBinder
    void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new CommentValidator());
    }

    @GetMapping("/posts/{id}/comments")
    public ResponseEntity<Comment> getComments(Pageable pageable, @PathVariable Long id) {
        //return commentRepository.findAll(pageable);
               return commentRepository.findById(id)
                .map(comment -> new ResponseEntity<>(comment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/posts/{id}/comments")
    public ResponseEntity<Comment> putComment(@PathVariable Long id, @Valid @RequestBody Comment comment) {
        if (!commentRepository.findById(id).isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.OK);
    }
}
