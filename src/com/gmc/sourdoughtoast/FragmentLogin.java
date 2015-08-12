package com.gmc.sourdoughtoast;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class FragmentLogin extends Fragment {
	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.frag_login, null);		

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Button btnLoginFacebook = (Button) getActivity().findViewById(R.id.btnLoginFacebook);
		btnLoginFacebook.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StaticMainClass.mainclass = MainActivity.FACEBOOK_LOGIN;
				MainActivity main = (MainActivity)getActivity();
				main.onResumeFragments();
			}
		});
		
		/*Button btnLoginTwitter = (Button) getActivity().findViewById(R.id.btnLoginTwitter);
		btnLoginTwitter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StaticMainClass.mainclass = MainActivity.TWITTER_LOGIN;
				MainActivity main = (MainActivity)getActivity();
				main.onResumeFragments();
			}
		});*/
		
		Button btnNoThanks = (Button) getActivity().findViewById(R.id.btnNoThanks);
		btnNoThanks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				StaticMainClass.mainclass = MainActivity.OPEN;
				MainActivity main = (MainActivity)getActivity();
				main.onResumeFragments();
			}
		});
	}
}
