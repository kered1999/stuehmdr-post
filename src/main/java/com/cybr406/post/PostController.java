package com.cybr406.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;


import javax.validation.Valid;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
public class PostController {

  private PostRepository postRepository;

  @Autowired
  public PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @InitBinder
  void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new PostValidator());
  }
  
  @PostMapping("/posts")
  public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
    return new ResponseEntity<>(postRepository.save(post), HttpStatus.CREATED);
  }
  
  @GetMapping("/posts")
  public Page<Post> getPosts(Pageable pageable) {
    return postRepository.findAll(pageable);
  }
  
  @GetMapping("/posts/{id}")
  public ResponseEntity<Post> getPost(@PathVariable Long id) {
    return postRepository.findById(id)
        .map(post -> new ResponseEntity<>(post, HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @PutMapping("/posts/{id}")
  public ResponseEntity<Post> putPost(@PathVariable Long id, @Valid @RequestBody Post post) {
    if (!postRepository.findById(id).isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    post.setId(id);
    return new ResponseEntity<>(postRepository.save(post), HttpStatus.OK);
  }
  
  @PatchMapping("/posts/{id}")
  public ResponseEntity<Post> patchPost(@PathVariable Long id, @RequestBody Map<String, Object> patch) {
    return postRepository.findById(id)
        .map(post -> new ResponseEntity<>(applyPatch(patch, post), HttpStatus.OK))
        .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
  
  private Post applyPatch(Map<String, Object> patch, Post post) {
    patch.forEach((key, value) -> {
      try {
        ReflectionUtils.setField(post.getClass().getField(key), post, value);
      } catch (Exception e) {
        throw new HttpClientErrorException(
            HttpStatus.BAD_REQUEST,
            String.format("Failed patch field %s", key));
      }
    });
    return postRepository.save(post);
  }
  
}
