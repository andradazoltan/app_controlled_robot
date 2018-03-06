package com.turtletest.turtle;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/** Group info - names
 */
public class AboutUs extends Fragment{

    /* Display info layout as view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        getContext().getTheme().applyStyle(R.style.colorControlHighlight_blue, true);
        return inflater.inflate(R.layout.aboutus, container, false);
    }

}
