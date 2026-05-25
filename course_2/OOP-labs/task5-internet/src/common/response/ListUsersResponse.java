package common.response;

import java.util.List;


public class ListUsersResponse implements Response {
    public final List<String> usersList;

    public ListUsersResponse(List<String> listUsers) {
        this.usersList = listUsers;
    }

}
