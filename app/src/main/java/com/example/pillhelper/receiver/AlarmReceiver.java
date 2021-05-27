package com.example.pillhelper.receiver;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.pillhelper.R;
import com.example.pillhelper.activity.AlarmActiveActivity;

import java.util.Calendar;

import static android.app.AlarmManager.RTC_WAKEUP;
import static com.example.pillhelper.App.CHANNEL_ID;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, Intent intent) {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

        Intent fullScreenIntent = new Intent(context, AlarmActiveActivity.class);
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        fullScreenIntent.putExtra("NOTIFICATION_ID", notificationId);

        int alarmType = intent.getIntExtra("ALARM_TYPE", 0);
        if (alarmType != 0) {
            if (alarmType == 1) {
                int hour = intent.getIntExtra("ALARM_HOUR", 0);
                int min = intent.getIntExtra("ALARM_MINUTES", 0);
                int[] days = intent.getIntArrayExtra("ALARM_DAYS");

                Calendar calendar = Calendar.getInstance();

                int weekDay = calendar.get(Calendar.DAY_OF_WEEK);
                int daysIndex = weekDay - 1;

                boolean isDaysEmpty = true;

                for (int i = 0; i < days.length; i++) {
                    if (days[i] == 1)
                        isDaysEmpty = false;
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

                calendar.set(year, month, day, hour, min, 0);

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent newIntent = new Intent(context, AlarmReceiver.class);
                newIntent.putExtra("NOTIFICATION_ID", notificationId);
                newIntent.putExtra("ALARM_TYPE", alarmType);
                newIntent.putExtra("ALARM_HOUR", hour);
                newIntent.putExtra("ALARM_MINUTES", min);
                newIntent.putExtra("ALARM_DAYS", days);

                if (days[daysIndex] == 1 || isDaysEmpty) {
                    createNotification(context, fullScreenIntent, notificationManagerCompat, notificationId);
                }

                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            } else if (alarmType == 2) {

                Calendar calendar = Calendar.getInstance();

                int hour_start = intent.getIntExtra("ALARM_HOUR", 0);
                int min_start = intent.getIntExtra("ALARM_MINUTES", 0);
                int times_day = intent.getIntExtra("ALARM_TIMES_DAY", 0);
                int missing_day_times = intent.getIntExtra("ALARM_TIMES_DAY_MISSING", 0);
                int period_hour = intent.getIntExtra("ALARM_PERIOD_HOUR", 0);
                int period_min = intent.getIntExtra("ALARM_PERIOD_MINUTE", 0);
                int hour_prox = intent.getIntExtra("ALARM_PERIOD_HOUR_NEXT", 0);
                int min_prox = intent.getIntExtra("ALARM_PERIOD_MINUTE_NEXT", 0);

                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                if (missing_day_times == 0) {
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

                    calendar.set(year, month, day, hour_start, min_start, 0);
                    missing_day_times = times_day;

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent newIntent = new Intent(context, AlarmReceiver.class);
                    newIntent.putExtra("NOTIFICATION_ID", notificationId);
                    newIntent.putExtra("ALARM_TYPE", alarmType);
                    newIntent.putExtra("ALARM_HOUR", hour_start);
                    newIntent.putExtra("ALARM_MINUTES", min_start);
                    newIntent.putExtra("ALARM_TIMES_DAY", times_day);
                    newIntent.putExtra("ALARM_TIMES_DAY_MISSING", missing_day_times);
                    newIntent.putExtra("ALARM_PERIOD_HOUR", period_hour);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE", period_min);
                    newIntent.putExtra("ALARM_PERIOD_HOUR_NEXT", period_hour);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE_NEXT", period_min);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else if (missing_day_times == times_day) {
                    if (min_start + period_min >= 60) {
                        min_prox = (min_start + period_min) - 60;

                        if (hour_start + period_hour + 1 >= 24) {

                            hour_prox = (hour_start + period_hour + 1) - 24;
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
                            hour_prox = hour_start + period_hour + 1;
                        }
                    } else {
                        min_prox = min_start + period_min;

                        if (hour_start + period_hour >= 24) {

                            hour_prox = (hour_start + period_hour) - 24;
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
                            hour_prox = hour_start + period_hour;
                        }
                    }

                    calendar.set(year, month, day, hour_prox, min_prox, 0);
                    missing_day_times--;

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent newIntent = new Intent(context, AlarmReceiver.class);
                    newIntent.putExtra("NOTIFICATION_ID", notificationId);
                    newIntent.putExtra("ALARM_TYPE", alarmType);
                    newIntent.putExtra("ALARM_HOUR", hour_start);
                    newIntent.putExtra("ALARM_MINUTES", min_start);
                    newIntent.putExtra("ALARM_TIMES_DAY", times_day);
                    newIntent.putExtra("ALARM_TIMES_DAY_MISSING", missing_day_times);
                    newIntent.putExtra("ALARM_PERIOD_HOUR", period_hour);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE", period_min);
                    newIntent.putExtra("ALARM_PERIOD_HOUR_NEXT", hour_prox);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE_NEXT", min_prox);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                } else {
                    int next_minute;
                    int next_hour;

                    if (min_prox + period_min >= 60) {
                        next_minute = (min_prox + period_min) - 60;

                        if (hour_prox + period_hour + 1 >= 24) {

                            next_hour = (hour_prox + period_hour + 1) - 24;
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
                            next_hour = hour_prox + period_hour + 1;
                        }
                    } else {
                        next_minute = min_prox + period_min;

                        if (hour_prox + period_hour >= 24) {

                            next_hour = (hour_prox + period_hour) - 24;
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
                            next_hour = hour_prox + period_hour;
                        }
                    }

                    calendar.set(year, month, day, next_hour, next_minute, 0);
                    missing_day_times--;

                    AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                    Intent newIntent = new Intent(context, AlarmReceiver.class);
                    newIntent.putExtra("NOTIFICATION_ID", notificationId);
                    newIntent.putExtra("ALARM_TYPE", alarmType);
                    newIntent.putExtra("ALARM_HOUR", hour_start);
                    newIntent.putExtra("ALARM_MINUTES", min_start);
                    newIntent.putExtra("ALARM_TIMES_DAY", times_day);
                    newIntent.putExtra("ALARM_TIMES_DAY_MISSING", missing_day_times);
                    newIntent.putExtra("ALARM_PERIOD_HOUR", period_hour);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE", period_min);
                    newIntent.putExtra("ALARM_PERIOD_HOUR_NEXT", next_hour);
                    newIntent.putExtra("ALARM_PERIOD_MINUTE_NEXT", next_minute);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    alarmManager.setExactAndAllowWhileIdle(RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                }

                createNotification(context, fullScreenIntent, notificationManagerCompat, notificationId);
            }
        }
    }

    private void createNotification(Context context, Intent fullScreenIntent,
            NotificationManagerCompat notificationManagerCompat, int notificationId) {
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher).setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_text))
                .setPriority(NotificationCompat.PRIORITY_MAX).setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setFullScreenIntent(fullScreenPendingIntent, true).build();

        notificationManagerCompat.notify(notificationId, notification);
    }
}
