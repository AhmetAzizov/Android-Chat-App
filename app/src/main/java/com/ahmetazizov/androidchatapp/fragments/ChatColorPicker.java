package com.ahmetazizov.androidchatapp.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.ahmetazizov.androidchatapp.R;
import com.google.android.material.card.MaterialCardView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ChatColorPicker extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_color_picker, container, false);
    }








    private static final String FILE_NAME = "chatColor.txt";

    CheckBox defaultColor, red, green, blue, purple, orange, yellow, brown, pink;
    CardView cardDefault, cardRed, cardGreen, cardBlue, cardPurple, cardOrange, cardYellow, cardBrown, cardPink;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        defaultColor = view.findViewById(R.id.checkBoxDefault);
        red = view.findViewById(R.id.checkBoxRed);
        green = view.findViewById(R.id.checkBoxGreen);
        blue = view.findViewById(R.id.checkBoxBlue);
        purple = view.findViewById(R.id.checkBoxPurple);
        orange = view.findViewById(R.id.checkBoxOrange);
        yellow = view.findViewById(R.id.checkBoxYellow);
        brown = view.findViewById(R.id.checkBoxBrown);
        pink = view.findViewById(R.id.checkBoxPink);

        cardDefault = view.findViewById(R.id.cardDefault);
        cardRed = view.findViewById(R.id.cardRed);
        cardGreen = view.findViewById(R.id.cardGreen);
        cardBlue = view.findViewById(R.id.cardBlue);
        cardPurple = view.findViewById(R.id.cardPurple);
        cardOrange = view.findViewById(R.id.cardOrange);
        cardYellow = view.findViewById(R.id.cardYellow);
        cardBrown = view.findViewById(R.id.cardBrown);
        cardPink = view.findViewById(R.id.cardPink);

        CheckBox[] checkBoxes = new CheckBox[] {defaultColor, red, green, blue, purple, orange, yellow, brown, pink};
        CardView[] cards = new CardView[] {cardDefault, cardRed, cardGreen, cardBlue, cardPurple, cardOrange, cardYellow, cardBrown, cardPink};


        String currentColor = loadFile().trim();

        switch (currentColor) {
            case "default": defaultColor.setChecked(true); break;
            case "red": red.setChecked(true); break;
            case "green": green.setChecked(true); break;
            case "blue": blue.setChecked(true); break;
            case "purple": purple.setChecked(true); break;
            case "orange": orange.setChecked(true); break;
            case "yellow": yellow.setChecked(true); break;
            case "brown": brown.setChecked(true); break;
            case "pink": pink.setChecked(true); break;
            default: defaultColor.setChecked(true);
        }




//        for (CheckBox checkBox : checkBoxes) {
//
//            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//                if (isChecked) {
//
//                    for (CheckBox checkBox2 : checkBoxes) {
//
//                        if (checkBox2.equals(checkBox)) continue;
//
//                        checkBox2.setChecked(false);
//                    }
//
//                }
//            });
//
//        }


        for (CardView card : cards) {

            card.setOnClickListener(v -> {

                for (CheckBox checkBox2 : checkBoxes) {
                    checkBox2.setChecked(false);
                }

                switch (v.getId()) {
                    case R.id.cardDefault: defaultColor.setChecked(true); saveFile("default"); break;
                    case R.id.cardRed: red.setChecked(true); saveFile("red"); break;
                    case R.id.cardGreen: green.setChecked(true); saveFile("green"); break;
                    case R.id.cardBlue: blue.setChecked(true); saveFile("blue"); break;
                    case R.id.cardPurple: purple.setChecked(true); saveFile("purple"); break;
                    case R.id.cardOrange: orange.setChecked(true); saveFile("orange"); break;
                    case R.id.cardYellow: yellow.setChecked(true); saveFile("yellow"); break;
                    case R.id.cardBrown: brown.setChecked(true); saveFile("brown"); break;
                    case R.id.cardPink: brown.setChecked(true); saveFile("pink"); break;
                }

            });

        }
    }


    private void saveFile(String color) {
        FileOutputStream fos = null;

        try {
            fos = requireContext().openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(color.getBytes());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String loadFile() {
        FileInputStream fis = null;

        try {
            fis = requireContext().openFileInput(FILE_NAME);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;

            while ((text = br.readLine()) != null) {
                sb.append(text).append("\n");
            }

            return sb.toString();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return "default";
    }
}