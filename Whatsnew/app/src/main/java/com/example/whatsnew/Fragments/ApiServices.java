package com.example.whatsnew.Fragments;

import com.example.whatsnew.Notification.MyResponse;
import com.example.whatsnew.Notification.Sender;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiServices {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAvHNPXNU:APA91bFv8I2NmN64qlAEGn6jpvPwT05rHJYP51Bk9k5a0eSpYwvuuoOFHnV1vhVonLZtPz2cmarJs5Z5l4OqtZBRLCOZTeBwKxu6jORHNFUm1k8i1tg8WcoBSscArpcwSGfLUQXcKJpm"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
