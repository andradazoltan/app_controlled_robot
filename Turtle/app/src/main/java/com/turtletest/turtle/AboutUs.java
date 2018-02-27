package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Grace on 2018-02-19.
 */

public class AboutUs extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getContext().getTheme().applyStyle(R.style.colorControlHighlight_blue, true);
        return inflater.inflate(R.layout.aboutus, container, false);
    }

}
