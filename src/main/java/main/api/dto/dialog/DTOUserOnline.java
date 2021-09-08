package main.api.dto.dialog;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import main.api.dto.DTO;

@Data
@AllArgsConstructor
public class DTOUserOnline implements DTO {

    private boolean online;

    @JsonProperty("last_activity")
    private long lastActivity;

}
