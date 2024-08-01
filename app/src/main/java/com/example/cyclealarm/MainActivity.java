package com.example.cyclealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText sleepTimeInput;
    private Button calculateButton;
    private TextView wakeTimesOutput;
    private Button setAlarmButton;
    private int selectedHour;
    private int selectedMinute;
    private static final int PERMISSION_REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sleepTimeInput = findViewById(R.id.sleepTimeInput);
        calculateButton = findViewById(R.id.calculateButton);
        wakeTimesOutput = findViewById(R.id.wakeTimesOutput);
        setAlarmButton = findViewById(R.id.setAlarmButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sleepTime = sleepTimeInput.getText().toString();
                if (!sleepTime.isEmpty()) {
                    String wakeTimes = calculateWakeTimes(sleepTime);
                    wakeTimesOutput.setText(wakeTimes);
                    setAlarmButton.setVisibility(View.VISIBLE);
                }
            }
        });

        setAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog();
            }
        });
    }

    private String calculateWakeTimes(String sleepTime) {
        // Lógica para calcular horários de acordo com ciclos de sono
        // Substitua isso com a lógica correta para calcular os horários
        return "Horários calculados aqui";
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            if (canScheduleExactAlarms()) {
                                setAlarm(selectedHour, selectedMinute);
                            } else {
                                requestExactAlarmPermission();
                            }
                        } else {
                            setAlarm(selectedHour, selectedMinute);
                        }
                    }
                }, hour, minute, true);
        timePickerDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private boolean canScheduleExactAlarms() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        return alarmManager.canScheduleExactAlarms();
    }


    private void setAlarm(int hour, int minute) {
        try {
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Configura o alarme para a hora selecionada
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                    showToast("Alarme criado");
                } else {
                    requestExactAlarmPermission();
                }
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
                showToast("Alarme criado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showToast("Erro ao criar alarme");
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void requestExactAlarmPermission() {
        // Solicita permissão para alarmes exatos, se necessário (API 31+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            startActivity(intent);
        }
    }
}
/*

 private String calculateWakeTimes(String sleepTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        List<String> reverseWakeTimes = new ArrayList<>();
        try {
            Date sleepDate = sdf.parse(sleepTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(sleepDate);

            // Adiciona os horários para acordar considerando ciclos de 90 minutos
            for (int i = 1; i <= 6; i++) { // Considera 6 ciclos de sono
                calendar.add(Calendar.MINUTE, 90); // Adiciona 90 minutos para cada ciclo
                reverseWakeTimes.add(sdf.format(calendar.getTime()) + " - " + i + " ciclo(s)");
            }
            Collections.reverse(reverseWakeTimes);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Ordena os horários do mais próximo ao mais distante
        Collections.reverse(reverseWakeTimes);

        StringBuilder sb = new StringBuilder("Horários sugeridos para acordar:\n");
        for (String time : reverseWakeTimes) {
            sb.append(time).append("\n");
        }
        return sb.toString();
    }

    */