package main.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DTOTag implements DTO {
    private int id;
    private String tag;
}
