package com.example.pillhelper.utils;

import android.content.Context;

import com.example.pillhelper.dataBaseSupervisor.DataBaseBoundUserHelper;
import com.example.pillhelper.dataBaseUser.DataBaseAlarmsHelper;
import com.example.pillhelper.dataBaseUser.DataBaseBoundSupervisorHelper;
import com.example.pillhelper.dataBaseUser.DataBaseBoxHelper;
import com.example.pillhelper.dataBaseUser.DataBaseClinicalDataHelper;
import com.example.pillhelper.dataBaseBulla.DataBaseBullaHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import static com.example.pillhelper.utils.Constants.ALARM_TYPE;
import static com.example.pillhelper.utils.Constants.ATIVO;
import static com.example.pillhelper.utils.Constants.BOX_POSITION;
import static com.example.pillhelper.utils.Constants.DESCRIPTION_BULLA;
import static com.example.pillhelper.utils.Constants.DOMINGO;
import static com.example.pillhelper.utils.Constants.DOSAGEM;
import static com.example.pillhelper.utils.Constants.HORA;
import static com.example.pillhelper.utils.Constants.ID_ALARME;
import static com.example.pillhelper.utils.Constants.ID_CAIXA;
import static com.example.pillhelper.utils.Constants.ID_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.INFORMATION_BULLA;
import static com.example.pillhelper.utils.Constants.LUMINOSO;
import static com.example.pillhelper.utils.Constants.MEDICINE_TYPE;
import static com.example.pillhelper.utils.Constants.MINUTO;
import static com.example.pillhelper.utils.Constants.NAME_BULLA;
import static com.example.pillhelper.utils.Constants.NOME_CAIXA;
import static com.example.pillhelper.utils.Constants.NOME_REMEDIO;
import static com.example.pillhelper.utils.Constants.NOME_SUPERVISOR;
import static com.example.pillhelper.utils.Constants.NOME_USER;
import static com.example.pillhelper.utils.Constants.NOTIFICATION_ID;
import static com.example.pillhelper.utils.Constants.PERIODO_HORA;
import static com.example.pillhelper.utils.Constants.PERIODO_MIN;
import static com.example.pillhelper.utils.Constants.QUANTIDADE;
import static com.example.pillhelper.utils.Constants.QUANTIDADE_BOX;
import static com.example.pillhelper.utils.Constants.QUARTA;
import static com.example.pillhelper.utils.Constants.QUINTA;
import static com.example.pillhelper.utils.Constants.REGISTRADO_POR;
import static com.example.pillhelper.utils.Constants.SABADO;
import static com.example.pillhelper.utils.Constants.SEGUNDA;
import static com.example.pillhelper.utils.Constants.SEXTA;
import static com.example.pillhelper.utils.Constants.SONORO;
import static com.example.pillhelper.utils.Constants.TERCA;
import static com.example.pillhelper.utils.Constants.TITLE_BULLA;
import static com.example.pillhelper.utils.Constants.VEZES_DIA;
import static com.example.pillhelper.utils.Constants.VINCULO;

public class LoadDataBase {

    public void loadDataBaseUser(JsonArray alarmsArray, JsonArray boxArray, JsonArray supervisorArray, JsonObject clinicalDataObject, JsonArray bullasArray, DataBaseAlarmsHelper mDataBaseAlarmsHelper, DataBaseBoxHelper mDataBaseBoxHelper, DataBaseClinicalDataHelper mDataBaseClinicalDataHelper, DataBaseBoundSupervisorHelper mDataBaseBoundSupervisorHelper, DataBaseBullaHelper mDataBaseBullaHelper){
        if (alarmsArray != null) {

            for (int i = 0; i < alarmsArray.size(); i++) {
                JsonElement jsonElement = alarmsArray.get(i);
                JsonObject jsonAlarm = jsonElement.getAsJsonObject();

                int[] days = new int[7];
                days[0] = jsonAlarm.get(DOMINGO).getAsInt();
                days[1] = jsonAlarm.get(SEGUNDA).getAsInt();
                days[2] = jsonAlarm.get(TERCA).getAsInt();
                days[3] = jsonAlarm.get(QUARTA).getAsInt();
                days[4] = jsonAlarm.get(QUINTA).getAsInt();
                days[5] = jsonAlarm.get(SEXTA).getAsInt();
                days[6] = jsonAlarm.get(SABADO).getAsInt();

                mDataBaseAlarmsHelper.addData(
                        jsonAlarm.get(ID_ALARME).getAsString(),
                        jsonAlarm.get(ALARM_TYPE).getAsInt(),
                        jsonAlarm.get(MEDICINE_TYPE).getAsInt(),
                        jsonAlarm.get(ATIVO).getAsInt(),
                        jsonAlarm.get(NOME_REMEDIO).getAsString(),
                        jsonAlarm.get(DOSAGEM).getAsInt(),
                        jsonAlarm.get(QUANTIDADE).getAsInt(),
                        jsonAlarm.get(QUANTIDADE_BOX).getAsInt(),
                        jsonAlarm.get(HORA).getAsInt(),
                        jsonAlarm.get(MINUTO).getAsInt(),
                        days,
                        jsonAlarm.get(VEZES_DIA).getAsInt(),
                        jsonAlarm.get(PERIODO_HORA).getAsInt(),
                        jsonAlarm.get(PERIODO_MIN).getAsInt(),
                        jsonAlarm.get(NOTIFICATION_ID).getAsInt(),
                        jsonAlarm.get(LUMINOSO).getAsInt(),
                        jsonAlarm.get(SONORO).getAsInt(),
                        jsonAlarm.get(BOX_POSITION).getAsInt());
            }
        }

        if (boxArray != null) {
            for (int i = 0; i < boxArray.size(); i++) {
                JsonElement jsonElement = boxArray.get(i);
                JsonObject jsonBox = jsonElement.getAsJsonObject();

                mDataBaseBoxHelper.addData(jsonBox.get(ID_CAIXA).getAsString(),
                        jsonBox.get(NOME_CAIXA).getAsString());
            }
        }

        if (supervisorArray != null) {
             for (int i = 0; i < supervisorArray.size(); i++) {
                JsonElement jsonElement = supervisorArray.get(i);
                JsonObject jsonSupervisor = jsonElement.getAsJsonObject();

                mDataBaseBoundSupervisorHelper.addData(
                        jsonSupervisor.get(ID_SUPERVISOR).getAsString(),
                        jsonSupervisor.get(REGISTRADO_POR).getAsString(),
                        jsonSupervisor.get(VINCULO).getAsString(),
                        jsonSupervisor.get(NOME_SUPERVISOR).getAsString());
            }
        }

        if (clinicalDataObject != null) {
            JsonArray clinicalDataNamesArray = clinicalDataObject.getAsJsonArray("clinicalDataNames");

            for (int i = 0; i < clinicalDataNamesArray.size(); i++) {
                String name = clinicalDataNamesArray.get(i).getAsString();

                mDataBaseClinicalDataHelper.addData(
                        name,
                        clinicalDataObject.get(name).getAsString());

            }
        }
        
        if (bullasArray != null) {
            for (int i = 0; i < bullasArray.size(); i++) {
                JsonElement jsonElement = bullasArray.get(i);
                JsonObject jsonBulla = jsonElement.getAsJsonObject();
                JsonArray arrayBullaInformation = jsonBulla.getAsJsonArray("information");
                String nameBulla = jsonBulla.get(NAME_BULLA).getAsString();
                nameBulla = nameBulla.substring(0, 1).toUpperCase() + nameBulla.substring(1).toLowerCase();

                for (int j = 0; j  < arrayBullaInformation.size(); j++) {
                    JsonElement jsonElementBulla = arrayBullaInformation.get(j);
                    JsonObject bullaInformation = jsonElementBulla.getAsJsonObject();

                    mDataBaseBullaHelper.addData(
                            nameBulla,
                            bullaInformation.get(TITLE_BULLA).getAsString(),
                            bullaInformation.get(DESCRIPTION_BULLA).getAsString(),
                            bullaInformation.get(INFORMATION_BULLA).getAsString());
                }
            }
        }
    }

    public void loadDataBaseSupervisor(JsonArray usersArray, JsonArray bullasArray, DataBaseBoundUserHelper mDataBaseBoundUserHelper,DataBaseBullaHelper mDataBaseBullaHelper){
        if (usersArray != null) {
            for (int i = 0; i < usersArray.size(); i++) {
                JsonElement jsonElement = usersArray.get(i);
                JsonObject jsonUser = jsonElement.getAsJsonObject();

                mDataBaseBoundUserHelper.addData(
                        jsonUser.get("uuidUser").getAsString(),
                        jsonUser.get(REGISTRADO_POR).getAsString(),
                        jsonUser.get(VINCULO).getAsString(),
                        jsonUser.get(NOME_USER).getAsString());
            }
        }

        if (bullasArray != null) {
            for (int i = 0; i < bullasArray.size(); i++) {
                JsonElement jsonElement = bullasArray.get(i);
                JsonObject jsonBulla = jsonElement.getAsJsonObject();
                JsonArray arrayBullaInformation = jsonBulla.getAsJsonArray("information");
                String nameBulla = jsonBulla.get(NAME_BULLA).getAsString();
                nameBulla = nameBulla.substring(0, 1).toUpperCase() + nameBulla.substring(1).toLowerCase();

                for (int j = 0; j  < arrayBullaInformation.size(); j++) {
                    JsonElement jsonElementBulla = arrayBullaInformation.get(j);
                    JsonObject bullaInformation = jsonElementBulla.getAsJsonObject();
                    mDataBaseBullaHelper.addData(
                            nameBulla,
                            bullaInformation.get(TITLE_BULLA).getAsString(),
                            bullaInformation.get(DESCRIPTION_BULLA).getAsString(),
                            bullaInformation.get(INFORMATION_BULLA).getAsString());
                }
            }
        }

    }
}
