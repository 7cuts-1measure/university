package common.event;

public class UserConnectedEvent implements Event {

    private final String userName;

    public UserConnectedEvent(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }
    
}
