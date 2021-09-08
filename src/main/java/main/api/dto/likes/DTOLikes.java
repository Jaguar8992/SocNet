package main.api.dto.likes;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import main.api.dto.DTO;

import java.util.List;

@Data
public class DTOLikes implements DTO {
    private int likes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Integer> users;
}
