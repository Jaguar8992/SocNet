package main.api.response.account;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorListResponse {
    private String error;
    private long timestamp;
    private List data;

    public ErrorListResponse(List data) {
        this.error = null;
        this.timestamp = System.currentTimeMillis() / 1000;
        this.data = data;
    }
}
