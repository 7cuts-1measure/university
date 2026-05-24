package common.response;

import java.util.List;


public class ListUsersResponse implements Response {
    public final List<String> listUsers;

    public ListUsersResponse(List<String> listUsers) {
        this.listUsers = listUsers;
    }

}
