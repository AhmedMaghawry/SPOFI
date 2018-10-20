package com.ezzat.spofi.View;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ezzat.spofi.Control.Constants;
import com.ezzat.spofi.Model.Gender;
import com.ezzat.spofi.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThirdRegFragment extends Fragment implements RegisterInterface{

    private RadioGroup radioGroup;
    private RadioButton selectedGender, male_radio, female_radio;
    private ImageView male, female;
    private View view;

    public ThirdRegFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_third_reg, container, false);
        radioGroup = view.findViewById(R.id.gender);
        male = view.findViewById(R.id.male_bg);
        female = view.findViewById(R.id.fe_bg);
        male_radio = view.findViewById(R.id.male);
        female_radio = view.findViewById(R.id.female);
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male_radio.setChecked(true);
                female_radio.setChecked(false);
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female_radio.setChecked(true);
                male_radio.setChecked(false);
            }
        });
        Log.i(Constants.TAG + "Ezz", "Third Frag");
        return view;
    }

    public boolean attempReg() {
        boolean cancel = false;
        View focusView = null;

        if (radioGroup.getCheckedRadioButtonId() == -1){
            focusView = radioGroup;
            cancel = true;
        } else {
            selectedGender = view.findViewById(radioGroup.getCheckedRadioButtonId());
        }

        return !cancel;
    }

    public Gender getGender() {
        return Gender.valueOf(selectedGender.getText().toString());
    }
}
