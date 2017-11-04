package io.github.keibai.signin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import io.github.keibai.R;



/**
 * A simple {@link Fragment} subclass.
 */
public class Signin extends Fragment {


    public Signin() {
        // Required empty public constructor
    }

    public static Signin newInstance() {
        Signin fragment = new Signin();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.sign_in, container, false);
    }

}
