package common.response;

import java.util.List;


public class ListUsersResponse implements Response {
    private final List<String> listUsers;

    public ListUsersResponse(List<String> listUsers) {
        this.listUsers = listUsers;
    }

    public List<String> getListUsers() {
        return listUsers;
    }

}
