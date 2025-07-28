package com.fuhcm.swp391.be.itmms.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class ServiceStageRequest {

    @NotBlank(message = "Tên giai đoạn dịch vụ không được để trống")
    private String name;
    @Positive(message = "Thời gian điều trị phải lớn hơn 0")
    private int duration;

}
