package com.fuhcm.swp391.be.itmms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuggestionResponse {

    private Long replaceId;
    private List<AccountResponse> suggestions;
}
