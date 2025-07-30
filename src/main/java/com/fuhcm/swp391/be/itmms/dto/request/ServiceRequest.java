package com.fuhcm.swp391.be.itmms.dto.request;

import com.fuhcm.swp391.be.itmms.constant.ServiceStatus;
import com.fuhcm.swp391.be.itmms.dto.response.AccountBasic;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceRequest {

    @NotBlank(message = "Tên dịch vụ không được để trống")
    @Size(max = 100, message = "Tên dịch vụ tối đa 100 ký tự")
    private String serviceName;

    @NotBlank(message = "Tiêu đề phụ không được để trống")
    @Size(max = 255, message = "Tiêu đề phụ tối đa 255 ký tự")
    private String subTitle;

    @PositiveOrZero(message = "Giá phải lớn hơn hoặc bằng 0")
    private double price;

    @NotBlank(message = "Mô tả không được để trống")
    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String summary;

    @NotNull(message = "Trạng thái dịch vụ không được để trống")
    private ServiceStatus status;

    @NotBlank(message = "Đường dẫn hình ảnh không được để trống")
    private String imgUrl;

    private AccountBasic managerAccount;

}
