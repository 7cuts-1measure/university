package common.message;

public class LoginCommand implements Command{
    
    private final String userName;

    private final String clientName;
    

    public String getUserName() {
        return userName;
    }

    public String getClientName() {
        return clientName;
    }

    public LoginCommand(String userName, String clientName) {
        this.userName = userName;
        this.clientName = clientName;
    }

    @Override
    public String getSessionId() {
        return null;
    }

}
