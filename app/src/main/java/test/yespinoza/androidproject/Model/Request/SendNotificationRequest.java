package test.yespinoza.androidproject.Model.Request;

import test.yespinoza.androidproject.Model.Entity.User;

public class SendNotificationRequest {

    private User userSender;
    private User userReceiver;
    private String notification;

    public User getUserSender() {
        return userSender;
    }

    public void setUserSender(User userSender) {
        this.userSender = userSender;
    }

    public User getUserReceiver() {
        return userReceiver;
    }

    public void setUserReceiver(User userReceiver) {
        this.userReceiver = userReceiver;
    }

    public String getNotification() {
        return notification;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }
}
