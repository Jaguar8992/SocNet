package main.api.dto.dialog;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.api.dto.DTO;

@Data
@AllArgsConstructor
public class DTODialogUnreadCount implements DTO {
    private long count;
}
