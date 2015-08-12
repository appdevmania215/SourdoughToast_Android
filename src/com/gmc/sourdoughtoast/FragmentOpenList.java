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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class FragmentOpenList extends Fragment {
	 
	public static JSONArray votearray;
	MainActivity parent = null;
	ProgressDialog pdialog = null;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.frag_voter_list, null);		
		parent = (MainActivity)getActivity();
		pdialog = parent.pdialog;
	   	
		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		pdialog.show();
		String url = RequestUri.url + "?action=get_open_feud_list";
  		url = url.replace(" ", "%20");
  	   	System.out.println(url);
  	    final ArrayList<ArrayList<String>> voteitems = new ArrayList<ArrayList<String>>();
  	    votearray = null;
  	   	RequestFromServer r = (RequestFromServer) new RequestFromServer(url){
  			// Optional callback override.
  		    @Override
  		    protected void onSuccess(Transport transport) {
  		    	JSONObject response = transport.getResponseJson();
  		    	if ( response == null )
  		    		parent.error("Failure request open feud");
  		    	else if ( response.optString("success").equals("1") ) {
  		    		JSONArray details = null;
  		    		try {
  			    		if ( response != null && response.length() > 0 )
  			    			details = response.getJSONArray("openlist");
  			    		if ( details != null ) {
	  			    		for ( int i = 0; i < details.length(); i++ ) {
	  	  		    			JSONObject feud = details.getJSONObject(i);
	  	  		    			//System.out.println(feud.toString());
		  	  		    		ArrayList<String> item = new ArrayList<String>();
		  	  					item.add(feud.optString("plaintiff_oauthUid"));
		  	  					item.add(feud.optString("defendant_oauthUid"));
		  	  					item.add(feud.optString("feud_id"));
		  	  					
		  	  					voteitems.add(item);
	  	  		    		}
	  			    		ListView lvMenuList = (ListView)getActivity().findViewById(R.id.lvVoterList);
	  			  		
		  			  		VoterListArrayAdapter voteradapter = new VoterListArrayAdapter(getActivity().getBaseContext(), R.layout.list_row, voteitems, MainActivity.OPEN, getActivity());
		  			  		lvMenuList.setAdapter(voteradapter);
		  			  		votearray = details;
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
