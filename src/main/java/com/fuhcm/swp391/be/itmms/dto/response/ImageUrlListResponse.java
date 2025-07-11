package com.fuhcm.swp391.be.itmms.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ImageUrlListResponse {
    private List<String> imageUrls;
}
