package com.example.project_androidchat.Adapter;

import android.icu.text.CaseMap;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    // ArrayList содержащий объекты типа Fragment(экраны) и String(заголовки)
    private ArrayList <Fragment> fragments;
    private ArrayList <String> tittles;

    public ViewPagerAdapter(FragmentManager fm) {
        // Вызов класса FragmentManager для взаимодейсвтия между фрагментами
        super(fm);
        this.fragments = new ArrayList<>();
        this.tittles = new ArrayList<>();
    }
    // Позиция фрагмента
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    // Количество фрагментов
    @Override
    public int getCount() {
        return fragments.size();
    }

    // Создание отдельного фрагмента с заголовком
    public void AddFragment(Fragment fragment, String tittle) {
        // С помощью метода add добавляем объект в
        fragments.add(fragment);
        tittles.add(tittle);
    }
}
