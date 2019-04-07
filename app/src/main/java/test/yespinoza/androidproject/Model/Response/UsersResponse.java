package test.yespinoza.androidproject.Model.Response;

import java.util.ArrayList;

import test.yespinoza.androidproject.Model.Entity.Place;
import test.yespinoza.androidproject.Model.Entity.User;

public class UsersResponse extends BaseResponse {

    private ArrayList<User> Data;

    public ArrayList<User> getData() {
        return Data;
    }

    public void setData(ArrayList<User> data) {
        Data = data;
    }
}
