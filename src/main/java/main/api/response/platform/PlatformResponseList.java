package main.api.response.platform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlatformResponseList {

    private String error;
    private long timestamp;
    private long total;
    private int offset;
    private int perPage;
    List<PlatformResponse> data;
}
