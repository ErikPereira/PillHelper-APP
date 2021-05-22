package com.example.pillhelper;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

import static android.app.AlarmManager.RTC_WAKEUP;
import static com.example.pillhelper.App.CHANNEL_ID;

public class AlarmeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        Intent fullScreenIntent = new Intent(context, ActivityAlarmeAtivo.class);
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        fullScreenIntent.putExtra("NOTIFICATION_ID", notificationId);

        int alarmType = intent.getIntExtra("ALARM_TYPE", 0);
        if (alarmType != 0) {
            if (alarmType == 1) {
                int horas = intent.getIntExtra("ALARM_HOUR", 0);
                int minutos = intent.getIntExtra("ALARM_MINUTES", 0);
                int[] dias = intent.getIntArrayExtra("ALARM_DAYS");

                Calendar calendar = Calendar.getInstance();

                int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                int daysIndex = weekDay - 1;

                boolean isDiasEmpty = true;

                for (int i = 0; i < dias.length; i++) {
                    if (dias[i] == 1) isDiasEmpty = false;
                }

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);


                Calendar nextNotifTime = Calendar.getInstance();
                nextNotifTime.add(Calendar.MONTH, 1);
                nextNotifTime.set(Calendar.DATE, 1);
                nextNotifTime.add(Calendar.DATE, -1);

                if (day == nextNotifTime.get(Calendar.DAY_OF_MONTH)) {
                    if (month == 11) {
                        year = year + 1;
                        month = 0;
                    } else {
                        day = 1;
                        month = month + 1;
                    }
                } else {
                    day = day + 1;
                }

                calendar.set(year, month, day, horas, minutos, 0);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent newIntent = new Intent(context, AlarmeReceiver.class);
                newIntent.putExtra("NOTIFICATION_ID", notificationId);
                newIntent.putExtra("ALARM_TYPE", alarmType);
                newIntent.putExtra("ALARM_HOUR", horas);
                newIntent.putExtra("ALARM_MINUTES", minutos);
                newIntent.putExtra("ALARM_DAYS", dias);

                if (dias[daysIndex] == 1 || isDiasEmpty) {
                    createNotification(context, fullScreenIntent, notificationManagerCompat, notificationId);
                }

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            } else if (alarmType == 2) {

                Calendar calendar = Calendar.getInstance();

                int hora_inicio = intent.getIntExtra("ALARM_HOUR", 0);
                int minuto_inicio = intent.getIntExtra("ALARM_MINUTES", 0);
                int vezes_dia = intent.getIntExtra("ALARM_TIMES_DAY", 0);
                int vezes_dia_faltante = intent.getIntExtra("ALARM_TIMES_DAY_MISSING", 0);
                int periodo_hora = intent.getIntExtra("ALARM_PERIOD_HOUR", 0);
                int periodo_minuto = intent.getIntExtra("ALARM_PERIOD_MINUTE", 0);
                int hora_prox = intent.getIntExtra("ALARM_PERIOD_HOUR_NEXT", 0);
                int minuto_prox = intent.getIntExtra("ALARM_PERIOD_MINUTE_NEXT", 0);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                if (vezes_dia_faltante == 0) {
                    Calendar nextNotifTime = Calendar.getInstance();
                    nextNotifTime.add(Calendar.MONTH, 1);
                    nextNotifTime.set(Calendar.DATE, 1);
                    nextNotifTime.add(Calendar.DATE, -1);

                    if (day == nextNotifTime.get(Calendar.DAY_OF_MONTH)) {
                        if (month == 11) {
                            year = year + 1;
                            month = 0;
                        } else {
                            day = 1;
                            month = month + 1;
                        }
                    } else {
                        day = day + 1;
                    }

                    calendar.set(year, month, day, hora_inicio, minuto_inicio, 0);
                    vezes_dia_faltante = vezes_dia;

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent newIntent = new Intent(context, AlarmeReceiver.class);
                    newIntent.putExtra("NOTIFICATION_ID", notificationId);
                    newIntent.putExtra("ALARM_TYPE", alarmType);
                    newIntent.putExtra("ALARM_HOUR", hora_inicio);
                    newIntent.putExtra("ALARM_MINUTES", minuto_inicio);
                    newIntent.putExtra("ALARM_TIMES_DAY", vezes_dia);
                    newIntent.putExtra("ALARM_TIMES_DAY_MISSING", vezes_dia_faltante);
                    newIntent.putExtra("ALARM_PERIOD_HOUR", periodo_hora);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE", periodo_minuto);
                    newIntent.putExtra("ALARM_PERIOD_HOUR_NEXT", periodo_hora);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE_NEXT", periodo_minuto);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else if (vezes_dia_faltante == vezes_dia) {
                    if (minuto_inicio + periodo_minuto >= 60) {
                        minuto_prox = (minuto_inicio + periodo_minuto) - 60;

                        if (hora_inicio + periodo_hora + 1 >= 24) {

                            hora_prox = (hora_inicio + periodo_hora + 1) - 24;
                            day++;

                            if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                                if (month == 11) {
                                    year = year + 1;
                                    month = 0;
                                } else {
                                    day = 1;
                                    month = month + 1;
                                }
                            } else {
                                day = day + 1;
                            }

                        } else {
                            hora_prox = hora_inicio + periodo_hora + 1;
                        }
                    } else {
                        minuto_prox = minuto_inicio + periodo_minuto;

                        if (hora_inicio + periodo_hora >= 24) {

                            hora_prox = (hora_inicio + periodo_hora) - 24;
                            day++;

                            if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                                if (month == 11) {
                                    year = year + 1;
                                    month = 0;
                                } else {
                                    day = 1;
                                    month = month + 1;
                                }
                            } else {
                                day = day + 1;
                            }
                        } else {
                            hora_prox = hora_inicio + periodo_hora;
                        }
                    }

                    calendar.set(year, month, day, hora_prox, minuto_prox, 0);
                    vezes_dia_faltante--;

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent newIntent = new Intent(context, AlarmeReceiver.class);
                    newIntent.putExtra("NOTIFICATION_ID", notificationId);
                    newIntent.putExtra("ALARM_TYPE", alarmType);
                    newIntent.putExtra("ALARM_HOUR", hora_inicio);
                    newIntent.putExtra("ALARM_MINUTES", minuto_inicio);
                    newIntent.putExtra("ALARM_TIMES_DAY", vezes_dia);
                    newIntent.putExtra("ALARM_TIMES_DAY_MISSING", vezes_dia_faltante);
                    newIntent.putExtra("ALARM_PERIOD_HOUR", periodo_hora);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE", periodo_minuto);
                    newIntent.putExtra("ALARM_PERIOD_HOUR_NEXT", hora_prox);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE_NEXT", minuto_prox);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    int next_minute;
                    int next_hour;

                    if (minuto_prox + periodo_minuto >= 60) {
                        next_minute = (minuto_prox + periodo_minuto) - 60;

                        if (hora_prox + periodo_hora + 1 >= 24) {

                            next_hour = (hora_prox + periodo_hora + 1) - 24;
                            day++;

                            if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                                if (month == 11) {
                                    year = year + 1;
                                    month = 0;
                                } else {
                                    day = 1;
                                    month = month + 1;
                                }
                            } else {
                                day = day + 1;
                            }

                        } else {
                            next_hour = hora_prox + periodo_hora + 1;
                        }
                    } else {
                        next_minute = minuto_prox + periodo_minuto;

                        if (hora_prox + periodo_hora >= 24) {

                            next_hour = (hora_prox + periodo_hora) - 24;
                            day++;

                            if (day == Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                                if (month == 11) {
                                    year = year + 1;
                                    month = 0;
                                } else {
                                    day = 1;
                                    month = month + 1;
                                }
                            } else {
                                day = day + 1;
                            }
                        } else {
                            next_hour = hora_prox + periodo_hora;
                        }
                    }

                    calendar.set(year, month, day, next_hour, next_minute, 0);
                    vezes_dia_faltante--;

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent newIntent = new Intent(context, AlarmeReceiver.class);
                    newIntent.putExtra("NOTIFICATION_ID", notificationId);
                    newIntent.putExtra("ALARM_TYPE", alarmType);
                    newIntent.putExtra("ALARM_HOUR", hora_inicio);
                    newIntent.putExtra("ALARM_MINUTES", minuto_inicio);
                    newIntent.putExtra("ALARM_TIMES_DAY", vezes_dia);
                    newIntent.putExtra("ALARM_TIMES_DAY_MISSING", vezes_dia_faltante);
                    newIntent.putExtra("ALARM_PERIOD_HOUR", periodo_hora);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE", periodo_minuto);
                    newIntent.putExtra("ALARM_PERIOD_HOUR_NEXT", next_hour);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE_NEXT", next_minute);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

                createNotification(context, fullScreenIntent, notificationManagerCompat, notificationId);
            }
        }
    }

    private void createNotification(Context context, Intent fullScreenIntent, NotificationManagerCompat notificationManagerCompat, int notificationId) {
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .build();

        notificationManagerCompat.notify(notificationId, notification);
    }
}
