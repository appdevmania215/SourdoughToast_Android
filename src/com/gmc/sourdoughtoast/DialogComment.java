package com.gmc.sourdoughtoast;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


@SuppressLint({ "ValidFragment", "NewApi" })
public class DialogComment extends DialogFragment {
	private EditText mMessage;
	FragmentVote mParent = null;
    public DialogComment(FragmentVote parent) {
    	this.mParent = parent;
    }  

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_comment, container);
        
        mMessage = (EditText) view.findViewById(R.id.etDescription);
        //getDialog().setTitle("Post Message");
        
        Button btnOk = (Button) view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mParent.sendComment(mMessage.getText().toString());
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
