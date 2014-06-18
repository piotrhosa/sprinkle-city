package com.pfhosa.sprinklecity.fragments;

import com.pfhosa.sprinklecity.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CreateCharacterHumanFragment extends Fragment {
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
 
    	final View view = inflater.inflate(R.layout.fragment_create_character_bottom_left, container, false);
    	return view;
    }
}