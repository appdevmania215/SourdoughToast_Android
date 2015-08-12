package com.gmc.sourdoughtoast.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.internal.ImageDownloader;
import com.facebook.internal.ImageRequest;
import com.facebook.internal.ImageRequest.Builder;
import com.facebook.internal.ImageResponse;
import com.gmc.sourdoughtoast.FragmentVote;
import com.gmc.sourdoughtoast.MainActivity;
import com.gmc.sourdoughtoast.R;
import com.gmc.sourdoughtoast.StaticMainClass;
import com.gmc.sourdoughtoast.utils.RequestFromServer;
import com.gmc.sourdoughtoast.utils.RequestUri;
import com.gmc.sourdoughtoast.utils.Transport;


public class VoterListArrayAdapter extends ArrayAdapter<ArrayList<String>>{
 
	protected ViewHolder viewHolder = null;
	protected LayoutInflater inflater = null;
	
	ArrayList<Bitmap> listPictures1 = null;
	ArrayList<Bitmap> listPictures2 = null;
	ImageDownloader downloader = new ImageDownloader();;
	
	int type = 0;
	
	protected int PERSON1 = 0;
	protected int PERSON2 = 1;
	protected int FEUDNUMBER = 2;
	MainActivity mParent = null;
	
	public VoterListArrayAdapter(Context c, int textViewResourceId, 
			ArrayList<ArrayList<String>> arrays, int type, FragmentActivity fragmentActivity) {
		super(c, textViewResourceId, arrays);
		this.inflater = LayoutInflater.from(c);
		this.type = type;
		this.mParent = (MainActivity)fragmentActivity;
//		listPictures1 = new ArrayList<Bitmap>();
//		listPictures2 = new ArrayList<Bitmap>();
//		for ( int i = 0; i < arrays.size(); i++ ) {
//			listPictures1.add(null);
//			listPictures2.add(null);
//		}
	}
	
	@Override
	public int getCount() {
		return super.getCount();
	}

	public String getItem(int position, int type) {
		return super.getItem(position).get(type);
	}

	@Override
	public long getItemId(int position) {
		return super.getItemId(position);
	}

	@Override
	public View getView(final int position, View convertview, ViewGroup parent) {
		View v = convertview;
		
		if ( inflater == null )
			return v;
		if ( type == MainActivity.SETTINGS && position == 0 ) {
			if ( getCount() > 1 )
				v = inflater.inflate(R.layout.list_row_settings, null);
			else
				v = inflater.inflate(R.layout.list_row_settings_empty, null);
			TextView tvUsername = (TextView)v.findViewById(R.id.tvUserName);
			tvUsername.setText(StaticMainClass.username);
			return v;
		}
		
		v = inflater.inflate(R.layout.list_row, null);
		viewHolder = new ViewHolder();
		viewHolder.ivPerson1 = (RelativeLayout)v.findViewById(R.id.layoutPerson1);
		viewHolder.ivPerson2 = (RelativeLayout)v.findViewById(R.id.layoutPerson2);
		viewHolder.tvFeudNumber = (TextView)v.findViewById(R.id.tvFeudNumber);
		viewHolder.btnViewTrial = (Button)v.findViewById(R.id.btnViewTrial);
		
		if ( type == MainActivity.SETTINGS ) {
			viewHolder.btnViewTrial.setBackgroundResource(R.drawable.custom_btn_red);
			viewHolder.btnViewTrial.setText("Delete");
		}

		v.setTag(viewHolder);
		String tag1 = position + "-" + 1;
		viewHolder.ivPerson1.setTag(tag1);
		
		String tag2 = position + "-" + 2;
		viewHolder.ivPerson2.setTag(tag2);
		
		try {
			final RelativeLayout person1 = viewHolder.ivPerson1;
			Builder builder1;
			builder1 = new Builder(mParent, ImageRequest.getProfilePictureUrl(getItem(position, PERSON1), 120, 120));
			builder1.setCallerTag(tag1);
			builder1.setCallback(new ImageRequest.Callback() {
				
				@Override
				public void onCompleted(ImageResponse response) {
					person1.setBackgroundDrawable(new BitmapDrawable(response.getBitmap()));
				}
			});
			ImageDownloader.downloadAsync(builder1.build());
			
			final RelativeLayout person2 = viewHolder.ivPerson2;
			Builder builder2;
			builder2 = new Builder(mParent, ImageRequest.getProfilePictureUrl(getItem(position, PERSON2), 120, 120));
			builder2.setCallerTag(tag2);
			builder2.setCallback(new ImageRequest.Callback() {
				
				@Override
				public void onCompleted(ImageResponse response) {
					person2.setBackgroundDrawable(new BitmapDrawable(response.getBitmap()));
				}
			});
			ImageRequest request2 = builder2.build();
			System.out.println(request2.getImageUri());
			ImageDownloader.downloadAsync(request2);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		if ( listPictures1.get(position) == null && getItem(position, PERSON1) != null ) {
//			String imgurl1 = "http://graph.facebook.com/" + getItem(position, PERSON1) + "/picture?type=large";
//			new TheTask(viewHolder.ivPerson1, imgurl1, position, listPictures1).execute();
//		}
//		else
//			viewHolder.ivPerson1.setBackgroundDrawable(new BitmapDrawable(listPictures1.get(position)));
		
//		if ( listPictures2.get(position) == null && getItem(position, PERSON2) != null)
//		{
//			String imgurl2 = "https://graph.facebook.com/" + getItem(position, PERSON2) + "/picture?type=large";
//			new TheTask(viewHolder.ivPerson2, imgurl2, position, listPictures2).execute();
//		}
//		else
//			viewHolder.ivPerson2.setBackgroundDrawable(new BitmapDrawable(listPictures2.get(position)));
		
		viewHolder.tvFeudNumber.setText(getItem(position, FEUDNUMBER));
		viewHolder.btnViewTrial.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onViewTrial(position);
			}
		});
		return v;
	}
	
	protected void onViewTrial(final int pos) {
		if ( type == MainActivity.SETTINGS ) {
			mParent.pdialog.show();
			String url = RequestUri.url + "?action=delete_own_feud" + "&feud_id=" + getItem(pos, FEUDNUMBER);
	  		url = url.replace(" ", "%20");
	  	   	System.out.println(url);
	  	    
	  	   	RequestFromServer r = (RequestFromServer) new RequestFromServer(url){
	  			// Optional callback override.
	  		    @Override
	  		    protected void onSuccess(Transport transport) {
	  		    	JSONObject response = transport.getResponseJson();
	  		    	if ( response == null )
	  		    		mParent.error("Failure request your video.");
	  		    	else if ( response.optString("success").equals("1") ) {
	  		    		mParent.success("Delete", "Successfully Deleted!");
	  		    		remove(getItem(pos));
	  		    	}
	  		    	else
	  		    	{
	  		    		mParent.failure(response.optString("detail"));		    		
	  		    	}
	  		    }

	  		    // Optional callback override.
	  		    @Override
	  		    protected void onError(IOException ex) {
	  		        //Toast.makeText(mParent.getApplicationContext(), "Error occured: " + ex.getMessage(), Toast.LENGTH_SHORT);
	  		    	mParent.error("Server Error");
	  		    }


	  		    // Optional callback override.
	  		    @Override
	  		    protected void onFailure(Transport transport) {
	  		        //Toast.makeText(mParent.getApplicationContext(), "Something went wrong. code: " + transport.getStatus(),Toast.LENGTH_SHORT);
	  		    	mParent.failure();
	  		    }
	  		}.execute("POST");
	  		r.accept(RequestFromServer.CTYPE_JSON); 
	  		r.setContentType(RequestFromServer.CTYPE_JSON);
		}
		else {
			FragmentVote.type = StaticMainClass.mainclass;
			FragmentVote.voteNumber = pos;
			
			StaticMainClass.mainclass = MainActivity.VOTE;
			
			MainActivity main = (MainActivity)inflater.getContext();
			main.onResumeFragments();
		}
	}
	
	/*
	 * ViewHolder
	 */
	class ViewHolder{
		public RelativeLayout ivPerson1  = null;
		public RelativeLayout ivPerson2 = null;
		public TextView tvFeudNumber = null;
		public Button btnViewTrial = null;
	}

//	class TheTask extends AsyncTask<Void,Void,Void>
//	{
//		Bitmap image = null;
//		RelativeLayout iv = null;
//		int pos = -1;
//		String url = "";
//		ArrayList<Bitmap> listPicture = null;
//		public TheTask(RelativeLayout iv, String url, int pos, ArrayList<Bitmap> listpicture) {
//			this.iv = iv;
//			this.url = url;
//			this.pos = pos;
//			this.listPicture = listpicture;
//	    }
//	    @Override
//	    protected void onPreExecute() {
//	        // TODO Auto-generated method stub
//	        super.onPreExecute();
//	    }
//
//
//	    @Override
//	    protected Void doInBackground(Void... params) {
//	        // TODO Auto-generated method stub
//	    	if ( inflater == null )
//	    		return null;
//	        try
//	        {
//	        	image = downloadBitmap(url);
//	        }
//	        catch(Exception e)
//	        {
//	            e.printStackTrace();
//	        }
//	        return null;
//	    }
//
//	    @Override
//	    protected void onPostExecute(Void result) {
//	        // TODO Auto-generated method stub
//	        super.onPostExecute(result);
//	        if( image != null && listPicture != null )
//	        {
//	        	listPicture.add(pos, image);
//	        	iv.setBackgroundDrawable(new BitmapDrawable(image));
//	        }
//	    }   
//	}
//	protected Bitmap downloadBitmap(String url) {
//		if ( inflater == null )
//			return null;
//		
//		 Bitmap image = null;
//	     // initilize the default HTTP client object
//	     final DefaultHttpClient client = new DefaultHttpClient();
//
//	     //forming a HttoGet request 
//	     final HttpGet getRequest = new HttpGet(url);
//	     try {
//
//	         HttpResponse response = client.execute(getRequest);
//
//	         //check 200 OK for success
//	         final int statusCode = response.getStatusLine().getStatusCode();
//
//	         if (statusCode != HttpStatus.SC_OK) {
//	             Log.w("ImageDownloader", "Error " + statusCode + 
//	                     " while retrieving bitmap from " + url);
//	             return null;
//
//	         }
//
//	         final HttpEntity entity = response.getEntity();
//	         if (entity != null) {
//	             InputStream inputStream = null;
//	             try {
//	                 // getting contents from the stream 
//	                 inputStream = entity.getContent();
//
//	                 // decoding stream data back into image Bitmap that android understands
//	                 image = BitmapFactory.decodeStream(inputStream);
//
//
//	             } finally {
//	                 if (inputStream != null) {
//	                     inputStream.close();
//	                 }
//	                 entity.consumeContent();
//	             }
//	         }
//	     } catch (Exception e) {
//	         // You Could provide a more explicit error message for IOException
//	         getRequest.abort();
//	         Log.e("ImageDownloader", "Something went wrong while" +
//	                 " retrieving bitmap from " + url + e.toString());
//	     } 
//
//	     return image;
//	 }
	
	@Override
	protected void finalize() throws Throwable {
		free();
		super.finalize();
	}
	
	protected void free(){
//		listPictures1.clear();
//		listPictures1 = null;
//		listPictures2.clear();
//		listPictures2 = null;
		inflater = null;
		viewHolder = null;
	}
}
