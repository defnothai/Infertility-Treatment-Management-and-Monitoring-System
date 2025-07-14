package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.constant.BlogStatus;
import com.fuhcm.swp391.be.itmms.entity.BlogPost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogPostResponse {
    private Long id;
    private String title;
    private String content;
    private LocalDate createdAt;
    private BlogStatus status;
    private String note;
    private LocalDate handleAt;
    private LocalDate deletedAt;
    private AccountBasic deletedBy;
    private AccountBasic approvedBy;
    private DoctorResponse createdBy;



    public BlogPostResponse(BlogPost blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.createdAt = blog.getCreatedAt();
        this.status = blog.getStatus();
        this.note = blog.getNote();
        this.handleAt = blog.getHandleAt();
        this.deletedAt = blog.getDeletedAt();
        if(blog.getDeletedBy() != null){
            this.deletedBy = new AccountBasic(blog.getDeletedBy());
        } else {
            this.deletedBy = null;
        }
        if(blog.getHandler() != null){
            this.approvedBy = new AccountBasic(blog.getHandler());
        } else {
            this.approvedBy = null;
        }
        this.createdBy = new DoctorResponse(blog.getCreatedBy());
    }
}
