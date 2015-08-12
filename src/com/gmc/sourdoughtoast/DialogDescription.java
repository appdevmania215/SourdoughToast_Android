package com.gmc.sourdoughtoast;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

@SuppressLint({ "ValidFragment", "NewApi" })
public class DialogDescription extends DialogFragment {

	FragmentVote mParent = null;
	String comment;
	String videourl;
	String date;
    public DialogDescription(FragmentVote parent, String comment, String videourl, String date) {
    	this.mParent = parent;
    	this.comment = comment;
    	this.videourl = videourl;
    	this.date = date;
    }
  
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_description, container);
        
        TextView tvDate = (TextView) view.findViewById(R.id.tvDate);
        tvDate.setText(date);
        
        TextView tvDescription = (TextView) view.findViewById(R.id.tvDescription);
        tvDescription.setText(comment);
        
        Button btnContinue = (Button) view.findViewById(R.id.btnContinue);
        btnContinue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mParent.playVideo(videourl);
				dismiss();
			}
		});
        
        Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});

        return view;
    }
}
