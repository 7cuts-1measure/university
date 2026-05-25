package common.event;

public class UserDisconnectedEvent implements Event{
    private final String userName;

    private final String reason;
    
    @Override
    public String getUserName() {
        return userName;
    }



    public UserDisconnectedEvent(String userName, String reason) {
        this.userName = userName;
        this.reason = reason;
    }



    public String getReason() {
        return reason;
    }


    
}
