package com.cybr406.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.validation.Valid;
import java.util.Map;

@RestController
public class CommentController {

    private CommentRepository commentRepository;

    @Autowired
    public CommentController(CommentRepository commentRepository){this.commentRepository = commentRepository;}

    @InitBinder
    void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new CommentValidator());
    }

    @GetMapping("/posts/comments")
    public Page<Comment> getComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @GetMapping("/posts/{postId}/comments")
    public Page<Comment> getPostComment(Pageable pageable, @PathVariable Long postId){
        return commentRepository.findByPostId(pageable, postId);
    }

    @GetMapping("/posts/comments/{id}")
    public ResponseEntity<Comment> getComment(@PathVariable Long id) {
        return commentRepository.findById(id)
                .map(comment -> new ResponseEntity<>(comment, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


    @PostMapping("/posts/{postid}/comments")
    public ResponseEntity<Comment> putComment(@PathVariable Long postid, @Valid @RequestBody Comment comment) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String user = (String) auth.getPrincipal();

        comment.setCommentAuthor(user);
        comment.setPostId(postid);

        return new ResponseEntity<>(commentRepository.save(comment), HttpStatus.CREATED);    }


    @PatchMapping("/posts/comments/{id}")
    public ResponseEntity<Comment> patchComment(@PathVariable Long id, @RequestBody Map<String, Object> patch) {
        return commentRepository.findById(id)
                .map(comment -> new ResponseEntity<>(applyPatch(patch, comment), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
    private Comment applyPatch(Map<String, Object> patch, Comment comment) {
        patch.forEach((key, value) -> {
            try {
                ReflectionUtils.setField(comment.getClass().getField(key), comment, value);
            } catch (Exception e) {
                throw new HttpClientErrorException(
                        HttpStatus.BAD_REQUEST,
                        String.format("Failed patch field %s", key));
            }
        });
        return commentRepository.save(comment);
    }
}
