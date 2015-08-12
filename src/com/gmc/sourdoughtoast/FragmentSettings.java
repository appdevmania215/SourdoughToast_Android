package com.gmc.sourdoughtoast;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmc.sourdoughtoast.adapter.VoterListArrayAdapter;
import com.gmc.sourdoughtoast.utils.RequestFromServer;
import com.gmc.sourdoughtoast.utils.RequestUri;
import com.gmc.sourdoughtoast.utils.Transport;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class FragmentSettings extends Fragment {
	 
	ArrayList<ArrayList<String>> voteritems;
	MainActivity parent = null;
	ProgressDialog pdialog = null;
	
	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.frag_settings, null);		
		parent = (MainActivity)getActivity();
		pdialog = parent.pdialog;
		
		Button logout = (Button) view.findViewById(R.id.btnLogout);
	   	logout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				StaticMainClass.mainclass = MainActivity.LOGOUT;
				MainActivity main = (MainActivity)inflater.getContext();
				main.onResumeFragments();
			}
		});
	   	
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		pdialog.show();
		String url = RequestUri.url + "?action=get_own_feud_list" + "&oauthUid=" + StaticMainClass.oauthuid;
  		url = url.replace(" ", "%20");
  	   	System.out.println(url);
  	    voteritems = new ArrayList<ArrayList<String>>();
  	    
  	   	RequestFromServer r = (RequestFromServer) new RequestFromServer(url){
  			// Optional callback override.
  		    @Override
  		    protected void onSuccess(Transport transport) {
  		    	JSONObject response = transport.getResponseJson();
  		    	if ( response == null )
  		    		parent.error("Failure request your video.");
  		    	else if ( response.optString("success").equals("1") ) {
  		    		JSONArray details = null;
  		    		try {
  			    		if ( response != null && response.length() > 0 )
  			    			details = response.getJSONArray("openlist");
  			    		ArrayList<String> emptyitem = new ArrayList<String>();
  			    		voteritems.add(emptyitem);
  			    		if ( details != null ) {
	  			    		for ( int i = 0; i < details.length(); i++ ) {
	  	  		    			JSONObject feud = details.getJSONObject(i);
	  	  		    			
		  	  		    		ArrayList<String> item = new ArrayList<String>();
		  	  					item.add(feud.optString("plaintiff_oauthUid"));
		  	  					item.add(feud.optString("defendant_oauthUid"));
		  	  					item.add(feud.optString("feud_id"));
		  	  					
		  	  					voteritems.add(item);
	  	  		    		}
	  			    		System.out.println("HELLO : " + details.toString());
	  			    		ImageView ivEmptySettings = (ImageView)getActivity().findViewById(R.id.ivEmptySettings);
	  			    		if ( details.length() == 0 )
	  			    			ivEmptySettings.setVisibility(View.VISIBLE);
	  			    		else
	  			    			ivEmptySettings.setVisibility(View.INVISIBLE);
	  			    		
	  			    		ListView lvMenuList = (ListView)getActivity().findViewById(R.id.lvSettingsList);
	  			  		
		  			  		VoterListArrayAdapter voteradapter = new VoterListArrayAdapter(getActivity().getBaseContext(), R.layout.list_row, voteritems, MainActivity.SETTINGS, getActivity());
		  			  		lvMenuList.setAdapter(voteradapter);
  			    		}
  			    		pdialog.hide();
  					} catch (JSONException e1) {
  						pdialog.hide();
  						e1.printStackTrace();
  					} 
  		    	}
  		    	else
  		    	{
  		    		parent.failure(response.optString("detail"));		    		
  		    	}
  		    }

  		    // Optional callback override.
  		    @Override
  		    protected void onError(IOException ex) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Error occured: " + ex.getMessage(), Toast.LENGTH_SHORT);
  		    	parent.error("Server Error");
  		    }


  		    // Optional callback override.
  		    @Override
  		    protected void onFailure(Transport transport) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Something went wrong. code: " + transport.getStatus(),Toast.LENGTH_SHORT);
  		    	parent.failure();
  		    }
  		}.execute("POST");
  		r.accept(RequestFromServer.CTYPE_JSON); 
  		r.setContentType(RequestFromServer.CTYPE_JSON);
	}
}
