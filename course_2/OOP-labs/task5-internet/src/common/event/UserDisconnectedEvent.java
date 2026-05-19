package common.event;

public class UserDisconnectedEvent implements UserEvent{
    private final String userName;

    @Override
    public String getUserName() {
        return userName;
    }

    public UserDisconnectedEvent(String userName) {
        this.userName = userName;
    }
    
}
