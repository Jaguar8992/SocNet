package main.api.response.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class UserResponseList
{
    private String error;
    private long timestamp;
    private long total;
    private int offset;
    private int perPage;
    private List<UserResponse> data;

    public UserResponseList(String error, long timestamp, long total, int offset,
                               int perPage, List<UserResponse> data)
    {
        this.error = error;
        this.timestamp = timestamp;
        this.total = total;
        this.offset = offset;
        this.perPage = perPage;
        this.data = data;
    }
}
