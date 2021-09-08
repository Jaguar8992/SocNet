package main.api.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@NoArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponseList<T> {

    private String error;
    private long timestamp;
    private T data;

    public CommonResponseList(String error, T data) {
        this.error = error;
        this.timestamp = Instant.now().getEpochSecond();
        this.data = data;
    }
}
