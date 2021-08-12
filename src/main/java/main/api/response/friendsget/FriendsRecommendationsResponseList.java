package main.api.response.friendsget;

import main.api.response.user.UserResponse;
import main.api.response.user.UserResponseList;

import java.util.List;


public class FriendsRecommendationsResponseList extends UserResponseList {

    public FriendsRecommendationsResponseList(String error, long timestamp, int total, int offset,
                                              int perPage, List<UserResponse> data) {
        super(error, timestamp, total, offset, perPage, data);
    }

}

