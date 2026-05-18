package common;

import java.util.Date;

public class UserMessage {
    private final String text;
    private Date dateArrived = null;

    public UserMessage(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setDateArrived(Date date) {
        dateArrived = date;
    }

    /**
     * @return date when message was arrived or {@code null} if nobody set the date
     */
    public Date getDateArrived() {
        return dateArrived;
    }
}
