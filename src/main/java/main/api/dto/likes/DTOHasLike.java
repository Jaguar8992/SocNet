package main.api.dto.likes;

import lombok.Data;
import main.api.dto.DTO;

@Data
public class DTOHasLike implements DTO {
    private boolean likes;
}
