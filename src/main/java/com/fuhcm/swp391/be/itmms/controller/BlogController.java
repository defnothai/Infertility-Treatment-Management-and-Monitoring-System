package com.fuhcm.swp391.be.itmms.controller;


import com.fuhcm.swp391.be.itmms.dto.request.BlogPostRequest;
import com.fuhcm.swp391.be.itmms.dto.response.BlogPostResponse;
import com.fuhcm.swp391.be.itmms.dto.response.ResponseFormat;
import com.fuhcm.swp391.be.itmms.service.BlogPostService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/blogs")
public class BlogController {

    @Autowired
    private BlogPostService blogPostService;

    @GetMapping
    public ResponseEntity getAllPosts() {
        List<BlogPostResponse> blogPostResponses = blogPostService.getBlogPosts();
        if(blogPostResponses.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseFormat<>(HttpStatus.NOT_FOUND.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy blog posts thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy blog posts thành công",
                blogPostResponses));
    }

    @GetMapping("/mine")
    public ResponseEntity getMinePosts(Authentication authentication) {
        List<BlogPostResponse> blogPostResponses = blogPostService.getMinePosts(authentication);
        if(blogPostResponses.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseFormat<>(HttpStatus.NOT_FOUND.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy blog posts thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy blog posts thành công",
                blogPostResponses));
    }

    @GetMapping("/for-user")
    public ResponseEntity getPostsForUser(){
        List<BlogPostResponse> blogPostResponses = blogPostService.getBlogPostsForUsers();
        if(blogPostResponses.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseFormat<>(HttpStatus.NOT_FOUND.value(),
                            "FETCH_DATA_FAIL",
                            "Lấy blog posts thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Lấy blog posts thành công",
                blogPostResponses));
    }

    @PostMapping
    public ResponseEntity createPost(
            @Valid @RequestBody BlogPostRequest blogPostRequest,
            Authentication authentication) {
        BlogPostResponse blogPost = blogPostService.createBlog(blogPostRequest, authentication);
        if(blogPost == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "FETCH_DATA_FAIL",
                            "Tạo blog thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Tạo blog thành công",
                blogPost));
    }

    @PutMapping
    public ResponseEntity updatePost(
            @RequestParam("id") @Min(1) Long id,
            @RequestParam("status") @NotBlank String status,
            @RequestParam("note") String note,
            Authentication authentication) {
        BlogPostResponse blogPost = blogPostService.updateBlog(id, status, authentication, note);
        if(blogPost == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "FETCH_DATA_FAIL",
                            "Approve blog thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Approve blog thành công",
                null));
    }

    @DeleteMapping
    public ResponseEntity deletePost(
            @RequestParam("id") @Min(1) Long id,
            Authentication authentication) {
        BlogPostResponse blogPost = blogPostService.deletePost(id, authentication);
        if(blogPost == null){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseFormat<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "FETCH_DATA_FAIL",
                            "Xóa blog thất bại",
                            null));
        }
        return ResponseEntity.ok(new ResponseFormat<>(HttpStatus.OK.value(),
                "FETCH_DATA_SUCCESS",
                "Xóa blog thành công",
                null));
    }
}
