package com.fuhcm.swp391.be.itmms.dto.response;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fuhcm.swp391.be.itmms.constant.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = UserProfileResponse.class, name = "user"),
        @JsonSubTypes.Type(value = DoctorProfileResponse.class, name = "doctor"),
        @JsonSubTypes.Type(value = StaffProfileResponse.class, name = "staff")
})
@Data
@NoArgsConstructor
public abstract class ProfileResponse {
    private String userName;
    private String email;
    private String phoneNumber;

    public ProfileResponse(String userName, String email, String phoneNumber) {
        this.userName = userName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }
}
