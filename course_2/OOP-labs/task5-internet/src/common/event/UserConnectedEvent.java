package common.event;

public class UserConnectedEvent implements UserEvent {

    private final String userName;

    public UserConnectedEvent(String userName) {
        this.userName = userName;
    }

    @Override
    public String getUserName() {
        return userName;
    }
    
}
