package com.example.pillhelper;

import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.example.pillhelper.Constants.ID_USUARIO;

public interface JsonPlaceHolderApi {

    @POST(Constants.CREATE_USER)
    Call<JsonObject> postCreateUser(@Body JsonObject body);

    @FormUrlEncoded
    @POST(Constants.LOGIN_USER)
    Call<JsonObject> postLogin(@Header("authorization") String token,
                               @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST(Constants.USER_DATA)
    Call<JsonObject> postUserData(@Field(ID_USUARIO) String id);

    @POST(Constants.CREATE_ALARM)
    Call<JsonObject> postCreateAlarm(@Body JsonObject body);

    @POST(Constants.MODIFY_ALARM)
    Call<JsonObject> postModifyAlarm(@Body JsonObject body);

    @POST(Constants.DELETE_ALARM)
    Call<JsonObject> postDeleteAlarm(@Body JsonObject body);

    @POST(Constants.CREATE_UPDATE_BOX)
    Call<JsonObject> postCreateUpdateBox(@Body JsonObject body);

    @POST(Constants.DELETE_BOX)
    Call<JsonObject> postDeleteBox(@Body JsonObject body);
}
