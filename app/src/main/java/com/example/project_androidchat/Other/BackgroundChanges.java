package com.example.project_androidchat.Other;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.project_androidchat.R;

import java.util.Calendar;

public class BackgroundChanges {
    // Текущая дата / id макета для установки фона
    public int currentDate;
    public LinearLayout idLayout;

    public BackgroundChanges() {};

    public BackgroundChanges(int currentDate, LinearLayout idLayout) {
        this.currentDate = currentDate;
        this.idLayout = idLayout;
        // Метод проверки времени и выбора фона
        CheckTime(this.currentDate, this.idLayout);
    }
    public void CheckTime(int currentDate, LinearLayout idLayout) {
        if(currentDate >= 12 && currentDate < 17){
            // День
            idLayout.setBackgroundResource(R.drawable.day);
        }
        else if(currentDate >= 17 && currentDate < 21){
            // Вечер
            idLayout.setBackgroundResource(R.drawable.evening);
        }
        else if((currentDate >= 0 && currentDate < 4) || (currentDate >= 21 && currentDate < 24)){
            // Ночь
            idLayout.setBackgroundResource(R.drawable.night);
        }
        else if(currentDate >= 4 && currentDate < 12){
            // Утро
            idLayout.setBackgroundResource(R.drawable.morning);
        }
    }
}
