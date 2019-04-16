package com.cybr406.post;

import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long commentId;

    Long postId;

    String commentAuthor;

    @Lob
    @Type(type = "org.hibernate.type.TextType")
    String commentContent;

    public Long getPostId() {
        return postId;
    }

    public void setPostID(Long postId) {
        this.postId = postId;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long id) {
        this.commentId = id;
    }

    public String getCommentAuthor() { return commentAuthor; }

    public void setCommentAuthor(String author) {
        this.commentAuthor = author;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String content) {
        this.commentContent = content;
    }
}
