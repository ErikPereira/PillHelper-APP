package com.example.pillhelper;

public class Constants {

    public static final String BASE_URL = "http://192.168.56.1:3000/"; //URL BASE
    public static final String TOKEN_ACCESS = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjoiZGV2Iiwic3lzdGVtIjoiQVBJIn0.6lrvVMtHdb6P5f-Au4c36SK1OT8kb0_gg5BuEok_TpU";
    public static final String CREATE_USER = "insertOneUser";//O POST PARA CRIAR O USUARIO
    public static final String LOGIN_USER = "checkLoginUser";//POST PARA EFETUAR O LOGIN
    public static final String USER_DATA = "getOneUser";//POST PARA RECEBER OS DADOS DO USUARIO
    public static final String CREATE_ALARM = "createAlarmUser";//POST PARA CRIAR ALARME
    public static final String MODIFY_ALARM = "updateAlarmUser";//POST PARA ALTERAR UM ALARME
    public static final String DELETE_ALARM = "deleteAlarmUser";//POST PARA ALTERAR UM ALARME
    public static final String CREATE_UPDATE_BOX = "registerBox";//POST PARA CRIAR E ALTERAR UMA CAIXA
    public static final String DELETE_BOX = "Caixa/excluiCaixa";//POST PARA DELETAR UMA CAIXA

    public static final String ALARM_TYPE = "alarm_type";//1 == fixo 2 == intervalo
    public static final String MEDICINE_TYPE = "medicine_type";//1 == pilula 2 == liquid
    public static final String ATIVO = "ativo";// 1 == ativo 0 == inativo
    public static final String NOME_REMEDIO = "nome_remedio";
    public static final String DOSAGEM = "dosagem";
    public static final String QUANTIDADE = "quantidade";
    public static final String QUANTIDADE_BOX = "quantidade_box";
    public static final String HORA = "hora";
    public static final String MINUTO = "minuto";
    public static final String DOMINGO = "domingo";
    public static final String SEGUNDA = "segunda";
    public static final String TERCA = "terca";
    public static final String QUARTA = "quarta";
    public static final String QUINTA = "quinta";
    public static final String SEXTA = "sexta";
    public static final String SABADO = "sabado";
    public static final String VEZES_DIA = "vezes_dia";
    public static final String PERIODO_HORA = "periodo_hora";
    public static final String PERIODO_MIN = "periodo_min";
    public static final String NOTIFICATION_ID = "notification_id";
    public static final String LUMINOSO = "luminoso";
    public static final String SONORO = "sonoro";
    public static final String BOX_POSITION = "posCaixa";

    public static final String ID_USUARIO = "id";
    public static final String ID_CAIXA = "idCaixa";
    public static final String NOME_CAIXA = "nomeCaixa";
    public static final String MUDAR_USUARIO = "mudarUsuario";

    public static final String OPEN_BOX_FRAG = "open_box_frag";
}
