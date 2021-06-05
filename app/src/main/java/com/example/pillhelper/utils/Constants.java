package com.example.pillhelper.utils;

public class Constants {

    public static final String BASE_URL = "http://192.168.1.11:3000/"; //URL BASE
    public static final String TOKEN_ACCESS = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiZGV2Iiwic3lzdGVtIjoiQVBJIn0.6lrvVMtHdb6P5f-Au4c36SK1OT8kb0_gg5BuEok_TpU";
    public static final String CREATE_USER = "insertOneUser";//O POST PARA CRIAR O USUARIO
    public static final String LOGIN_USER = "checkLoginUser";//POST PARA EFETUAR O LOGIN
    public static final String USER_DATA = "getOneUser";//POST PARA RECEBER OS DADOS DO USUARIO
    public static final String CREATE_ALARM = "createAlarmUser";//POST PARA CRIAR ALARME
    public static final String MODIFY_ALARM = "updateAlarmUser";//POST PARA ALTERAR UM ALARME
    public static final String DELETE_ALARM = "deleteAlarmUser";//POST PARA ALTERAR UM ALARME
    public static final String CREATE_BOX = "registerBox";//POST PARA CRIAR E ALTERAR UMA CAIXA
    public static final String UPDATE_BOX = "updateBoxUser";//POST PARA CRIAR E ALTERAR UMA CAIXA
    public static final String DELETE_BOX = "deleteBoxInUser";//POST PARA DELETAR UMA CAIXA
    public static final String UPDATE_SUPERVISOR_IN_USER = "updateSupervisorInUser"; // POST para atualizar os dados do supervisor dentro de um usuario
    public static final String DELETE_SUPERVISOR_IN_USER = "deleteSupervisorInUser"; // POST para deletar os dados do supervisor dentro de um usuario
    public static final String REGISTER_SUPERVISOR = "registerSupervisor"; // POST para registar um supervisor dentro de um usuario
    public static final String ADD_CLINICAL_DATA = "addClinicalData"; // POST para registar um supervisor dentro de um usuario
    public static final String DELETE_CLINICAL_DATA = "deleteClinicalData"; // POST para registar um supervisor dentro de um usuario
    public static final String UPDATE_CLINICAL_DATA = "updateClinicalData"; // POST para registar um supervisor dentro de um usuario

    public static final String ALARM_TYPE = "alarm_type";//1 == fixo 2 == intervalo
    public static final String MEDICINE_TYPE = "medicine_type";//1 == pilula 2 == liquid
    public static final String ATIVO = "active";// 1 == ativo 0 == inativo
    public static final String NOME_REMEDIO = "medical_name";
    public static final String DOSAGEM = "dosage";
    public static final String QUANTIDADE = "quantity";
    public static final String QUANTIDADE_BOX = "quantity_box";
    public static final String HORA = "hour";
    public static final String MINUTO = "minute";
    public static final String DOMINGO = "sunday";
    public static final String SEGUNDA = "monday";
    public static final String TERCA = "tuesday";
    public static final String QUARTA = "wednesday";
    public static final String QUINTA = "thursday";
    public static final String SEXTA = "friday";
    public static final String SABADO = "saturday";
    public static final String VEZES_DIA = "times_day";
    public static final String PERIODO_HORA = "period_hour";
    public static final String PERIODO_MIN = "period_min";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String LUMINOSO = "luminous";
    public static final String SONORO = "sound";
    public static final String BOX_POSITION = "posBox";

    public static final String ID_USUARIO = "uuid";
    public static final String ID_ALARME = "uuidAlarm";
    public static final String ID_CAIXA = "uuidBox";
    public static final String NOME_CAIXA = "nameBox";
    public static final String MUDAR_USUARIO = "mudarUsuario";

    public static final String NAME_CLINICAL_DATA = "name";
    public static final String VALUE_CLINICAL_DATA = "value";

    public static final String OPEN_BOX_FRAG = "open_box_frag";

    public static final String ID_SUPERVISOR = "uuidSupervisor";
    public static final String REGISTRADO_POR = "registeredBy";
    public static final String VINCULO = "bond";
    public static final String NOME_SUPERVISOR = "name";
    public static final String EMAIL_SUPERVISOR = "email";
    public static final String CELL_SUPERVISOR = "cell";
}
