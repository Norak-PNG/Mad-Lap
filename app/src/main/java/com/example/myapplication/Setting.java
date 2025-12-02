package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import java.util.Locale;

public class Setting extends Fragment {


    private static final String ARG_SETTING_NAME = "settingName";
    private String settingName;



    public static Setting newInstance(String name) {
        Setting fragment = new Setting();
        Bundle args = new Bundle();
        args.putString(ARG_SETTING_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            settingName = getArguments().getString(ARG_SETTING_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        AutoCompleteTextView drop_down_menu = view.findViewById(R.id.autoCompleteTextView);
        String[] Languages = {"English", "Khmer"};
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(requireContext(),R.layout.drop_down_item, Languages);
        drop_down_menu.setAdapter(arrayAdapter);
        drop_down_menu.setText(Languages[0], false);

//        Locale test = Locale.getDefault();
//        String language = test.getLanguage();
//        Toast.makeText(getContext() , "test " + language, Toast.LENGTH_SHORT).show();
//        if (language.equals("km")) {
//            drop_down_menu.setText(Languages[1], false);
//        } else {
//            drop_down_menu.setText(Languages[0], false);
//        }


        drop_down_menu.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedLanguage = (String) parent.getItemAtPosition(position);
            Toast.makeText(getContext(), "Selected: " + selectedLanguage, Toast.LENGTH_SHORT).show();
        });
    }

}