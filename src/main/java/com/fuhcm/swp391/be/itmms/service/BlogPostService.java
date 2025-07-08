package com.fuhcm.swp391.be.itmms.service;

import com.fuhcm.swp391.be.itmms.constant.BlogStatus;
import com.fuhcm.swp391.be.itmms.dto.request.BlogPostRequest;
import com.fuhcm.swp391.be.itmms.dto.response.BlogPostResponse;
import com.fuhcm.swp391.be.itmms.entity.Account;
import com.fuhcm.swp391.be.itmms.entity.BlogPost;
import com.fuhcm.swp391.be.itmms.repository.AccountRepository;
import com.fuhcm.swp391.be.itmms.repository.BlogPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Autowired
    private AccountRepository accountRepository;

    public List<BlogPostResponse> getBlogPosts() {
        List<BlogPostResponse>  blogPostResponses = new ArrayList<>();
        List<BlogPost> blogPosts = blogPostRepository.findAll();
        for (BlogPost blogPost : blogPosts) {
            blogPostResponses.add(new BlogPostResponse(blogPost));
        }
        return blogPostResponses;
    }

    public BlogPostResponse createBlog(BlogPostRequest request, Authentication authentication) {
        Account createdBy = accountRepository.findByEmail(authentication.getName());
        if(createdBy == null){
            throw new UsernameNotFoundException("Username not found");
        }
        BlogPost blogPost = new BlogPost();
        blogPost.setTitle(request.getTitle());
        blogPost.setContent(request.getContent());
        blogPost.setCreatedAt(LocalDate.now());
        blogPost.setStatus(BlogStatus.PENDING);
        blogPost.setHandler(null);
        blogPost.setCreatedBy(createdBy);
        blogPostRepository.save(blogPost);
        return new BlogPostResponse(blogPost);
    }

    public BlogPostResponse updateBlog(Long id, String status, Authentication authentication) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Blog not found"));
        Account approvedBy = accountRepository.findByEmail(authentication.getName());
        if(approvedBy == null){
            throw new UsernameNotFoundException("Username not found");
        }
        if(status.equalsIgnoreCase(BlogStatus.APPROVED.toString())){
            blogPost.setStatus(BlogStatus.APPROVED);
        } else if(status.equalsIgnoreCase(BlogStatus.REJECTED.toString())){
            blogPost.setStatus(BlogStatus.REJECTED);
        }
        blogPost.setHandler(approvedBy);
        blogPost.setHandleAt(LocalDate.now());
        blogPostRepository.save(blogPost);
        return new BlogPostResponse(blogPost);
    }

    public BlogPostResponse deletePost(Long id, Authentication authentication) {
        BlogPost blogPost = blogPostRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Blog not found"));
        Account deletedBy = accountRepository.findByEmail(authentication.getName());
        if(deletedBy == null){
            throw new UsernameNotFoundException("Username not found");
        }
        blogPost.setStatus(BlogStatus.DELETED);
        blogPost.setDeletedAt(LocalDate.now());
        blogPost.setDeletedBy(deletedBy);
        blogPostRepository.save(blogPost);
        return new BlogPostResponse(blogPost);
    }

    public List<BlogPostResponse> getMinePosts(Authentication authentication) {
        Account createdBy = accountRepository.findByEmail(authentication.getName());
        if(createdBy == null){
            throw new UsernameNotFoundException("Username not found");
        }
        List<BlogPostResponse> blogPostResponses = new ArrayList<>();
        List<BlogPost> blogPosts = blogPostRepository.findByCreatedBy(createdBy);
        for (BlogPost blogPost : blogPosts) {
            blogPostResponses.add(new BlogPostResponse(blogPost));
        }
        return blogPostResponses;
    }
}
