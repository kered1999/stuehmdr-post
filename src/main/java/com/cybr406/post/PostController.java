package com.cybr406.post;

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

  public PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @InitBinder
  void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.addValidators(new PostValidator());
  }

  @PostMapping("/posts")
  public ResponseEntity<Post> createPost(@Valid @RequestBody Post post) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String user = (String) auth.getPrincipal();

    post.setAuthor(user);

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

  @PatchMapping("/posts/{id}")
  public ResponseEntity<Post> patchPost(@PathVariable Long id, @RequestBody Map<String, Object> patch) {
    Optional<Post> result = postRepository.findById(id);

    if (!result.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (cannotModifyPost(result.get())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    return new ResponseEntity<>(applyPatch(patch, result.get()), HttpStatus.OK);
  }

  @DeleteMapping("/posts/{id}")
  public ResponseEntity deletePost(@PathVariable Long id) {
    Optional<Post> result = postRepository.findById(id);

    if (!result.isPresent()) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    if (cannotModifyPost(result.get())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    postRepository.delete(result.get());

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  private boolean cannotModifyPost(Post post) {
    String author = post.getAuthor();
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String user = (String) auth.getPrincipal();
    Collection authorities = auth.getAuthorities();
    return !user.equals(author) && !authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
  }

  private Post applyPatch(Map<String, Object> patch, Post post) {
    patch.forEach((key, value) -> {
      try {
        Field field = ReflectionUtils.findField(Post.class, key);
        ReflectionUtils.makeAccessible(Objects.requireNonNull(field));
        ReflectionUtils.setField(field, post, value);
      } catch (Exception e) {
        throw new HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                String.format("Failed patch field %s", key));
      }
    });
    return postRepository.save(post);
  }

}