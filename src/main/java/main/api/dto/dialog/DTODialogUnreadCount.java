package main.api.dto.dialog;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DTODialogUnreadCount implements DTODialog{
    private long count;
}
