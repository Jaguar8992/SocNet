package main.api.response.friendsget;

import lombok.Getter;
import lombok.Setter;
import main.api.response.user.UserResponse;
import main.api.response.user.UserResponseList;

import java.util.List;

@Getter
@Setter
public class FriendsResponseList extends UserResponseList {

    public FriendsResponseList(String error, long timestamp, int total, int offset,
                               int perPage, List<UserResponse> data) {
        super(error, timestamp, total, offset, perPage, data);
    }

}
