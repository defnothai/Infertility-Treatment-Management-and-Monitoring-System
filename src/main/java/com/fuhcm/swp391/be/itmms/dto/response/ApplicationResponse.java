package com.fuhcm.swp391.be.itmms.dto.response;

import com.fuhcm.swp391.be.itmms.entity.Application;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ApplicationResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate dateSend;
    private LocalDate dateHandled;
    private AccountResponse sendby;
    private AccountResponse handledby;
    private String status;

    public ApplicationResponse(Application application){
        this.id = application.getId();
        this.title = application.getTitle();
        this.description = application.getDescription();
        this.dateSend = application.getDateSend();
        this.dateHandled = application.getDateHandled();
        this.status = application.getStatus().toString();
        if(application.getAccount() != null){
            this.sendby = new AccountResponse(application.getAccount());
        } else {
            this.sendby = null;
        }

        if(application.getDoctor() != null){
            this.handledby = new AccountResponse(application.getDoctor());
        } else{
            this.handledby = null;
        }

    }
}
