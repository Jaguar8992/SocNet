package main.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class DTOSuccessfully {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String error;

    private long timestamp;

    private DTO data;
}
