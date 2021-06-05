package com.example.pillhelper.services;

import com.example.pillhelper.utils.Constants;
import com.google.gson.JsonObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

import static com.example.pillhelper.utils.Constants.ID_USUARIO;

public interface JsonPlaceHolderApi {

    @POST(Constants.CREATE_USER)
    Call<JsonObject> postCreateUser(@Header("authorization") String token,
                                    @Body JsonObject body);

    @FormUrlEncoded
    @POST(Constants.LOGIN_USER)
    Call<JsonObject> postLogin(@Header("authorization") String token,
                               @FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST(Constants.USER_DATA)
    Call<JsonObject> postUserData(@Header("authorization") String token,
                                  @Field(ID_USUARIO) String id);

    @POST(Constants.CREATE_ALARM)
    Call<JsonObject> postCreateAlarm(@Header("authorization") String token,
                                     @Body JsonObject body);

    @POST(Constants.MODIFY_ALARM)
    Call<JsonObject> postModifyAlarm(@Header("authorization") String token,
                                     @Body JsonObject body);

    @POST(Constants.DELETE_ALARM)
    Call<JsonObject> postDeleteAlarm(@Header("authorization") String token,
                                     @Body JsonObject body);

    @POST(Constants.CREATE_BOX)
    Call<JsonObject> postCreateBox(@Header("authorization") String token,
                                         @Body JsonObject body);

    @POST(Constants.UPDATE_BOX)
    Call<JsonObject> postUpdateBox(@Header("authorization") String token,
                                         @Body JsonObject body);

    @POST(Constants.DELETE_BOX)
    Call<JsonObject> postDeleteBox(@Header("authorization") String token,
                                   @Body JsonObject body);

    @POST(Constants.UPDATE_SUPERVISOR_IN_USER)
    Call<JsonObject> postUpdateSupervisorInUser(@Header("authorization") String token,
                                   @Body JsonObject body);

    @POST(Constants.DELETE_SUPERVISOR_IN_USER)
    Call<JsonObject> postDeleteSupervisorInUser(@Header("authorization") String token,
                                                @Body JsonObject body);

    @POST(Constants.REGISTER_SUPERVISOR)
    Call<JsonObject> postRegisterSupervisor(@Header("authorization") String token,
                                                @Body JsonObject body);

    @POST(Constants.UPDATE_CLINICAL_DATA)
    Call<JsonObject> postUpdateClinicalData(@Header("authorization") String token,
                                            @Body JsonObject body);

    @POST(Constants.DELETE_CLINICAL_DATA)
    Call<JsonObject> postDeleteClinicalData(@Header("authorization") String token,
                                            @Body JsonObject body);

    @POST(Constants.ADD_CLINICAL_DATA)
    Call<JsonObject> postAddClinicalData(@Header("authorization") String token,
                                            @Body JsonObject body);
}
