package com.ahmetazizov.androidchatapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.ahmetazizov.androidchatapp.R;

public class RatingDialog extends Dialog {

    ImageView[] starArray = new ImageView[5];

    public RatingDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.layout_dialog);

        // Set the window background to transparent
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // To prevent the dialog from closing when clicked outside
//        setCanceledOnTouchOutside(false);

        // To prevent the dialog from closing when back button is pressed
//        setCancelable(false);

        for (int i = 0; i < starArray.length; i++) {
            String id = "star" + (i + 1);

            int resID = context.getResources().getIdentifier(id, "id", context.getPackageName());

            starArray[i] = findViewById(resID);
        }


        for (ImageView star : starArray) {

            star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < 5; i++) {
                        starArray[i].setColorFilter(context.getResources().getColor(R.color.DarkGray));
                    }

                    int count = 0;

                    switch (v.getId()) {
                        case R.id.star1: count = 1; break;
                        case R.id.star2: count = 2; break;
                        case R.id.star3: count = 3; break;
                        case R.id.star4: count = 4; break;
                        case R.id.star5: count = 5; break;
                    }


                    for (int i = 0; i < count; i++) {
                        starArray[i].setColorFilter(context.getResources().getColor(R.color.Gold));
                    }
                }
            });

        }
    }

}
