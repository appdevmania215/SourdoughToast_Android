package com.gmc.sourdoughtoast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.gmc.sourdoughtoast.sqlite.Vote;
import com.gmc.sourdoughtoast.sqlite.VoteSQLiteHelper;
import com.gmc.sourdoughtoast.utils.AlertUtil;
import com.gmc.sourdoughtoast.utils.RequestFromServer;
import com.gmc.sourdoughtoast.utils.RequestUri;
import com.gmc.sourdoughtoast.utils.Transport;
 
@SuppressLint("NewApi")
public class FragmentVote extends Fragment {
	public static final int MAX_VOTE_NUM = 11;

	private static final String TAG = null;
	
	public static final String FIRST_TYPE = "plaintiff";
	public static final String SECOND_TYPE = "defendant";
	
	public static int voteNumber = -1;
	public static int type = -1; 
	
	int feudid = -1;
	int votetype = -1;
	int report = 0;
	
	String firstVideoUrl = "";
	String secondVideoUrl = "";
	String firstVideoDescription = "";
	String secondVideoDescription = "";
	String firstVideoOpenedDate = "";
	String secondVideoServedDate = "";
	VoteSQLiteHelper db = null;
	String commentType = "";
	
	int firstVoteCount = 0;
	int secondeVoteCount = 0;
	
	TextView tvFirstPersonName;
	TextView tvSecondPersonName;
	
	TextView tvFirstVotedNumber;
	TextView tvSecondVotedNumber;
	
	TextView tvFirstProgress;
	TextView tvSecondProgress;
	
	Button btnFirstVote;
	Button btnSecondVote;
	
	Button btnFirstComment;
	Button btnSecondComment;
	
	Button btnReport;
	Button btnFave;
	
	Button btnFirstWatchMe;
	Button btnSecondWatchMe;
	
	VideoView videoView;
	MediaController mediaController;
	
	ProgressDialog pDialog;
	ImageView ivLoading;
	
	String currentVideoPath = "";
	
	private Timer updateTimer;
	
	ArrayList<ArrayList<String>> firstCommentsList = null;
	ArrayList<ArrayList<String>> secondCommentsList = null;
	
	LinearLayout layoutVote;
	int firstLastComment = 0;
	
	RelativeLayout layoutVideoPlayer;
	LinearLayout.LayoutParams layoutparmas;
	RelativeLayout.LayoutParams layoutvideoparmas;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.frag_vote, null);		

		return view;
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		JSONArray votearray = null;
		
		db = new VoteSQLiteHelper(getActivity());
		
//		List<Vote> list = db.getAllVotes();
//		for ( int i = 0; i < list.size(); i++ )
//			System.out.println(list.get(i).toString());
		
		if ( type == MainActivity.OPEN ) 
			votearray = FragmentOpenList.votearray;
		else if ( type == MainActivity.CLOSED )
			votearray = FragmentClosedList.votearray;
		
		if ( voteNumber != -1 && votearray != null && votearray.length() > voteNumber) {
			tvFirstPersonName = (TextView) getActivity().findViewById(R.id.tvVoteFirstPersonName);
			btnFirstVote = (Button) getActivity().findViewById(R.id.btnFirstVote);
			btnFirstWatchMe = (Button) getActivity().findViewById(R.id.btnFirstWatch);
			
			tvSecondPersonName = (TextView) getActivity().findViewById(R.id.tvVoteSecondPersonName);
			btnSecondVote = (Button) getActivity().findViewById(R.id.btnSecondVote);
			btnSecondWatchMe = (Button) getActivity().findViewById(R.id.btnSecondWatch);
			
			TextView tvFeudNumber = (TextView) getActivity().findViewById(R.id.tvFeudNumber);
			
			tvFirstVotedNumber = (TextView) getActivity().findViewById(R.id.tvFirstVotedNumber);
			tvFirstProgress = (TextView) getActivity().findViewById(R.id.tvFirstProgress);
			tvSecondVotedNumber = (TextView) getActivity().findViewById(R.id.tvSecondVotedNumber);
			tvSecondProgress = (TextView) getActivity().findViewById(R.id.tvSecondProgress);
			
			btnReport = (Button) getActivity().findViewById(R.id.btnReport);
			btnFave = (Button) getActivity().findViewById(R.id.btnFave);
			
			btnFirstComment = (Button) getActivity().findViewById(R.id.btnFirstComment);
			btnSecondComment = (Button) getActivity().findViewById(R.id.btnSecondComment);
			
			layoutVideoPlayer = (RelativeLayout) getActivity().findViewById(R.id.layoutVideoPlayer);
			layoutparmas = (LinearLayout.LayoutParams) layoutVideoPlayer.getLayoutParams();
			
			if ( StaticMainClass.userid == -1 ) {
				disableCommentButton(Vote.FIRST, false);
				disableCommentButton(Vote.SECOND, false);
				disableFaveButton(false);
			}
			
			btnFirstVote.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onClickVote(Vote.FIRST);
				}
			});
			
			btnSecondVote.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onClickVote(Vote.SECOND);
				}
			});
			
			btnFirstComment.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onClickComment(FIRST_TYPE);
				}
			});
			
			btnSecondComment.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onClickComment(SECOND_TYPE);
				}
			});
			
			btnReport.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onClickReport();
				}
			});

			btnFave.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onClickFave();
				}
			});
			
			// Create a progressbar
	        pDialog = new ProgressDialog(getActivity());
	        // Set progressbar title
	        //pDialog.setTitle("Android Video Streaming");
	        // Set progressbar message
	        pDialog.setMessage("Loading...");
	        pDialog.setIndeterminate(false);
	        pDialog.setCancelable(false);
	        
	        ivLoading = (ImageView) getActivity().findViewById(R.id.ivLoading);
	        ivLoading.setVisibility(View.INVISIBLE);
	        
			videoView = (VideoView) getActivity().findViewById(R.id.vvPlayer);
			layoutvideoparmas = (RelativeLayout.LayoutParams) videoView.getLayoutParams();
			Display display = getActivity().getWindowManager().getDefaultDisplay();
			int width = display.getWidth() - dpToPx(40);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
			layoutParams.width = width;
			layoutParams.height = width * 3 / 4;
			layoutVideoPlayer.setLayoutParams(layoutParams);
			
		    mediaController = new MediaController(getActivity());
		    mediaController.setAnchorView(videoView);
		    videoView.setMediaController(mediaController);

		    videoView.setOnPreparedListener(new 
					MediaPlayer.OnPreparedListener()  {
	            @Override
	            public void onPrepared(MediaPlayer mp) {                         
	            	//pDialog.dismiss();
	            	updateTimer = new Timer("progress Updater");
	        		updateTimer.scheduleAtFixedRate(new TimerTask() {
	        			@Override
	        			public void run() {
	        				getActivity().runOnUiThread(new Runnable() {
	        					public void run() {
	        						//Log.i(TAG, "Buffering = " + videoView.getBufferPercentage() + ":" + videoView.getDuration());
	        						if ( videoView.getBufferPercentage() > 0 || videoView.getDuration() > 0) {
	        							if (updateTimer != null) {
	        								updateTimer.cancel();
	        							}
	        							//pDialog.dismiss();
	        							ivLoading.setVisibility(View.INVISIBLE);
	        							
//	        							Display display = getActivity().getWindowManager().getDefaultDisplay();
//	        							int width = display.getWidth();
//	        							LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//	        							
//	        							layoutParams.height = width * videoView.getHeight() / videoView.getWidth();
//	        							layoutVideoPlayer.setLayoutParams(layoutParams);
	        						}
	        					}
	        				});
	        			}
	        		}, 0, 1000);
	            }
	        });	
		    videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					//pDialog.dismiss();
					ivLoading.setVisibility(View.INVISIBLE);
					//error("Can't play the video!");
					return false;
				}
			});
		    
		    btnFirstWatchMe.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDescription(firstVideoDescription, firstVideoUrl, firstVideoOpenedDate);
				}
			});
		    
		    btnSecondWatchMe.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDescription(secondVideoDescription, secondVideoUrl, secondVideoServedDate);
				}
			});
			
			try {
				final JSONObject info = (JSONObject) votearray.get(voteNumber);
				feudid = info.optInt("feud_id");
				tvFeudNumber.setText(info.optString("feud_id"));
				tvFirstPersonName.setText(info.optString("plaintiff_name"));
				tvSecondPersonName.setText(info.optString("defendant_name"));
				
				firstVoteCount = info.optInt("plaintiff_votes");
				secondeVoteCount = info.optInt("defendant_votes"); 
				setProgress(true);
				setProgress(false);
				
				firstVideoUrl = info.optString("plaintiff_video");
				secondVideoUrl = info.optString("defendant_video");
				firstVideoDescription = info.optString("plaintiff_video_desc");
				secondVideoDescription = info.optString("defendant_video_desc");
				firstVideoOpenedDate = "Opened On: " + info.optString("feud_opened");
				//secondVideoServedDate = firstVideoOpenedDate;
				secondVideoServedDate = "Served On: " + info.optString("feud_served");
				//System.out.println(info.toString());
				
				if ( type == MainActivity.OPEN ) {
					Vote vote = db.getVote(feudid);
					if ( vote != null ) {
						if ( vote.getType() != -1 ) {
							votetype = vote.getType();
							disableVoteButton(votetype);
						}
						if ( vote.getReport() == 1 ) {
							report = 1;
							disableReportButton();
						}
					}
				}
				else if ( type == MainActivity.CLOSED ) {
					showWinner(info.optString("votes_winner"));
				}
				
				layoutVote = (LinearLayout)getActivity().findViewById(R.id.layoutVote);
								
				boolean commented;
				commented = addComments(info.getJSONArray("plantiff_comments"), Vote.FIRST);
				if ( StaticMainClass.userid != -1 && commented == true )
					disableCommentButton(Vote.FIRST, commented);
				firstLastComment = layoutVote.getChildCount();
				
				commented = addComments(info.getJSONArray("defendant_comments"), Vote.SECOND);
				if ( StaticMainClass.userid != -1 && commented == true )
					disableCommentButton(Vote.SECOND, commented);
				
//				firstCommentsList = getCommentArrayList(info.getJSONArray("plantiff_comments"));
//				ListView lvFistComment = (ListView)getActivity().findViewById(R.id.lvFirstComment);
//		  		CommentListArrayAdapter firstCommentAdapter = new CommentListArrayAdapter(getActivity().getBaseContext(), R.layout.list_row_first_comment, firstCommentsList, Vote.FIRST);
//		  		lvFistComment.setAdapter(firstCommentAdapter);
//				
//				secondCommentsList = getCommentArrayList(info.getJSONArray("defendant_comments"));
//				ListView lvSecondComment = (ListView)getActivity().findViewById(R.id.lvSecondComment);
//		  		CommentListArrayAdapter secondCommentAdapter = new CommentListArrayAdapter(getActivity().getBaseContext(), R.layout.list_row_second_comment, secondCommentsList, Vote.SECOND);
//		  		lvSecondComment.setAdapter(secondCommentAdapter);
//		  		
//		  		RelativeLayout layoutFirstComment = (RelativeLayout)getActivity().findViewById(R.id.layoutFirstComment);
//		  		RelativeLayout.LayoutParams firstparams = (RelativeLayout.LayoutParams)layoutFirstComment.getLayoutParams();
//		  		firstparams.height = lvFistComment.getHeight();
//		  		layoutFirstComment.setLayoutParams(firstparams);
		  		
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean addComments(JSONArray comments, int type) {
		boolean commented = false;
		String oauthuid = StaticMainClass.oauthuid;
		
		for ( int i = 0; i < comments.length(); i++ ) {
			JSONObject comment;
			try {
				comment = comments.getJSONObject(i);
				View v;
				if ( type == Vote.FIRST ) {
					v = LayoutInflater.from(getActivity()).inflate(R.layout.list_row_first_comment, null);
				}
				else
					v = LayoutInflater.from(getActivity()).inflate(R.layout.list_row_second_comment, null);
				
				if ( oauthuid.equals(comment.optString("oauth_uid")) )
					commented = true;
				System.out.println(oauthuid + ":" + comment.optString("oauth_uid"));
				TextView tvName = (TextView)v.findViewById(R.id.tvName);
				TextView tvDate = (TextView)v.findViewById(R.id.tvDate);
				TextView tvComment = (TextView)v.findViewById(R.id.tvComment);
				
				tvName.setText(comment.optString("full_name"));
				tvDate.setText(comment.optString("post_date"));
				tvComment.setText(comment.optString("comment"));
				layoutVote.addView(v);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return commented;
	}
	private void addComment(int type, String name, String date, String comment) {
		disableCommentButton(type, true);
		View v;
		if ( type == Vote.FIRST ) {
			v = LayoutInflater.from(getActivity()).inflate(R.layout.list_row_first_comment, null);
		}
		else {
			v = LayoutInflater.from(getActivity()).inflate(R.layout.list_row_second_comment, null);
		}
		TextView tvName = (TextView)v.findViewById(R.id.tvName);
		TextView tvDate = (TextView)v.findViewById(R.id.tvDate);
		TextView tvComment = (TextView)v.findViewById(R.id.tvComment);
				
		tvName.setText(name);
		tvDate.setText(date);
		tvComment.setText(comment);
		if ( type == Vote.FIRST )
			layoutVote.addView(v, firstLastComment++);
		else
			layoutVote.addView(v);
	}
	private void disableCommentButton(int type, boolean commented) {
		if ( type == Vote.FIRST ) {
			btnFirstComment.setBackgroundResource(R.drawable.custom_btn_yellow_disable);
			btnFirstComment.setTextColor(Color.parseColor("#888888"));
			if ( commented ) {
				btnFirstComment.setText("Commented!");
				btnFirstComment.setEnabled(false);
			}
		}
		else {
			btnSecondComment.setBackgroundResource(R.drawable.custom_btn_yellow_disable);
			btnSecondComment.setTextColor(Color.parseColor("#888888"));
			if ( commented ) { 
				btnSecondComment.setText("Commented!");
				btnSecondComment.setEnabled(false);
			}
		}
	}
	private void disableFaveButton(boolean faved) {
		btnFave.setBackgroundResource(R.drawable.custom_btn_blue_disable);
		btnFave.setTextColor(Color.parseColor("#888888"));
		if ( faved )
			btnFave.setText("Faved!");
	}
	
	private void showDescription(String description, String path, String date) {
		//DialogDescription 
		FragmentManager fm = getActivity().getFragmentManager();
		DialogDescription dlgDescription = new DialogDescription(this, description, path, date);
		dlgDescription.setStyle(DialogDescription.STYLE_NO_TITLE, 0);
		dlgDescription.show(fm, "fragment_edit_description");
	}
	public void playVideo(String path) {
        try {
            Log.v(TAG, "path: " + path);
            if (path == null || path.length() == 0) {
                Toast.makeText(getActivity(), "File URL/path is empty",
                        Toast.LENGTH_LONG).show();
 
            } else {
                // If the path has not changed, just start the media player
                if (path.equals(currentVideoPath) && videoView != null) {
                    videoView.start();
                    videoView.requestFocus();
                    return;
                }
                //pDialog.show();
                ivLoading.setVisibility(View.VISIBLE);
//                Bitmap bm = null;
//                TransitionDrawable td = new TransitionDrawable(new Drawable[]{
//                        new ColorDrawable(android.R.color.transparent),
//                        new BitmapDrawable(getActivity().getBaseContext().getResources(), bm)
//                });
//
//                ivLoading.setImageDrawable(td);
//                td.startTransition(250);
//            	videoView.setLayoutParams(layoutvideoparmas);
//            	layoutVideoPlayer.setLayoutParams(layoutparmas);
                currentVideoPath = path;
                Log.d("TEST", path);
                videoView.setVideoPath(path);
                videoView.start();
                videoView.requestFocus();
 
            }
        } catch (Exception e) {
            Log.e(TAG, "error: " + e.getMessage(), e);
            if (videoView != null) {
                videoView.stopPlayback();
            }
        }
    }
	 
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getActivity().getBaseContext().getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	private boolean confirmLogin() {
		if ( StaticMainClass.userid == -1 ) {
			AlertUtil.confirmationAlert(getActivity(), "Vote", "Please login!", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					StaticMainClass.mainclass = MainActivity.LOGIN;
					MainActivity main = (MainActivity)getActivity();
					main.onResumeFragments();
				}
			});
			return false;
		}
		return true;
	}
	@SuppressLint("NewApi")
	private void onClickComment(String commenttype) {
		if ( confirmLogin() )
		{
			this.commentType = commenttype;
			FragmentManager fm = getActivity().getFragmentManager();
	        DialogComment dlgComment = new DialogComment(this);
	        dlgComment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
	        dlgComment.show(fm, "fragment_edit_comment");
		}
	}
	private void onClickFave() {
		if ( confirmLogin() )
		{
			
		}
	}
	
	private void disableVoteButton(int type) {
		btnFirstVote.setEnabled(false);
		btnFirstVote.setBackgroundResource(R.drawable.custom_btn_orange_disable);
		btnFirstVote.setTextColor(Color.parseColor("#888888"));
		
		btnSecondVote.setEnabled(false);
		btnSecondVote.setBackgroundResource(R.drawable.custom_btn_orange_disable);
		btnSecondVote.setTextColor(Color.parseColor("#888888"));
		if ( type == Vote.FIRST ) {
			btnFirstVote.setText("Voted!");
			btnSecondVote.setText("Vote");
		}
		else if ( type == Vote.SECOND ) {
			btnFirstVote.setText("Vote");
			btnSecondVote.setText("Voted!");
		}
		else {
			btnFirstVote.setText("Vote");
			btnSecondVote.setText("Vote");
		}
	}
	
	private void showWinner(String type) {
		if ( type.equals("Plaintiff") ) {
			btnSecondVote.setVisibility(View.INVISIBLE);
			btnFirstVote.setEnabled(false);
			btnFirstVote.setBackgroundResource(R.drawable.winner);
			btnFirstVote.setText("");
		}
		else {
			btnFirstVote.setVisibility(View.INVISIBLE);
			btnSecondVote.setEnabled(false);
			btnSecondVote.setBackgroundResource(R.drawable.winner);
			btnSecondVote.setText("");
		}
	}
	
	private void setProgress(boolean first) {
		int width = dpToPx(60);
		if ( first ) {
			RelativeLayout.LayoutParams param1 = (RelativeLayout.LayoutParams)tvFirstProgress.getLayoutParams();
			param1.width = (int)(width * firstVoteCount / MAX_VOTE_NUM);
			tvFirstProgress.setLayoutParams(param1);
			tvFirstVotedNumber.setText(Integer.toString(firstVoteCount));
		}
		else {
			RelativeLayout.LayoutParams param2 = (RelativeLayout.LayoutParams)tvSecondProgress.getLayoutParams();
			param2.width = (int)(width * secondeVoteCount / MAX_VOTE_NUM);
			tvSecondProgress.setLayoutParams(param2);
			tvSecondVotedNumber.setText(Integer.toString(secondeVoteCount));
		}
	}
	
	private void onClickVote(final int votetype) {
		pDialog.show();
		String type;
		if ( votetype == Vote.FIRST )
			type = FIRST_TYPE;
		else
			type = SECOND_TYPE;
		String url = RequestUri.url + "?action=vote_feud&feud_id=" + feudid + "&type=" + type;
  		 url = url.replace(" ", "%20");
  	   	 System.out.println(url);
  	   	 //success("TEST", "OK");
  	   	 //this.
  	   	 RequestFromServer r = (RequestFromServer) new RequestFromServer(url){
  			// Optional callback override.
  		    @Override
  		    protected void onSuccess(Transport transport) {
  		    	JSONObject response = transport.getResponseJson();
  		    	if ( response == null )
  		    		error("Server Error.");
  		    	else if ( response.optString("success").equals("1") ) {
  		    		success("Vote", "Successfully voted!");
  		    		FragmentVote.this.votetype = votetype;
	  		  		if ( votetype == Vote.FIRST ) {
	  		  			firstVoteCount++;
	  		  			setProgress(true);
	  		  		}
	  		  		else if ( votetype == Vote.SECOND ) {
	  		  			secondeVoteCount++;
	  		  			setProgress(false);
	  		  		}
	  		  		db.addVote(new Vote(StaticMainClass.userid, feudid, votetype, report));
	  		  		disableVoteButton(votetype);
  		    	}
  		    	else
  		    	{
  		    		failure(response.optString("detail"));		    		
  		    	}
  		    }

  		    // Optional callback override.
  		    @Override
  		    protected void onError(IOException ex) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Error occured: " + ex.getMessage(), Toast.LENGTH_SHORT);
  		    	error("Server Error");
  		    }


  		    // Optional callback override.
  		    @Override
  		    protected void onFailure(Transport transport) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Something went wrong. code: " + transport.getStatus(),Toast.LENGTH_SHORT);
  		    	failure();
  		    }
  		}.execute("POST");
  		r.accept(RequestFromServer.CTYPE_JSON); 
  		r.setContentType(RequestFromServer.CTYPE_JSON);
	}
	
	private void disableReportButton() {
		btnReport.setEnabled(false);
		btnReport.setBackgroundResource(R.drawable.custom_btn_red_disable);
		btnReport.setTextColor(Color.parseColor("#888888"));
		btnReport.setText("Reported!");
	}
	
	private void onClickReport() {	
		pDialog.show();
		String url = RequestUri.url + "?action=report_feud&feud_id=" + feudid;
  		 url = url.replace(" ", "%20");
  	   	 System.out.println(url);
  	   	 //success("TEST", "OK");
  	   	 //this.
  	   	 RequestFromServer r = (RequestFromServer) new RequestFromServer(url){
  			// Optional callback override.
  		    @Override
  		    protected void onSuccess(Transport transport) {
  		    	JSONObject response = transport.getResponseJson();
  		    	if ( response == null )
  		    		error("Server Error.");
  		    	else if ( response.optString("success").equals("1") ) {
  		    		success("Vote", "Successfully Reported!");
  		    		report = 1;
	  		  		db.addVote(new Vote(StaticMainClass.userid, feudid, votetype, report));
	  		  		disableReportButton();
	  		  		disableVoteButton(-1);
  		    	}
  		    	else
  		    	{
  		    		failure(response.optString("detail"));		    		
  		    	}
  		    }

  		    // Optional callback override.
  		    @Override
  		    protected void onError(IOException ex) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Error occured: " + ex.getMessage(), Toast.LENGTH_SHORT);
  		    	error("Server Error");
  		    }


  		    // Optional callback override.
  		    @Override
  		    protected void onFailure(Transport transport) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Something went wrong. code: " + transport.getStatus(),Toast.LENGTH_SHORT);
  		    	failure();
  		    }
  		}.execute("POST");
  		r.accept(RequestFromServer.CTYPE_JSON); 
  		r.setContentType(RequestFromServer.CTYPE_JSON);
	}
	
	public void sendComment(final String comment) {
		pDialog.show();
		String url = "?action=send_comment&feud_id=" + feudid + "&user_id=" + StaticMainClass.userid + "&type=" + commentType + "&comment=" + comment;
  		url = url.replace(" ", "%20");
  		url = url.replace("\n", "%20");
  		url = RequestUri.url + url;
  	   	 System.out.println(url);
  	   	 //success("TEST", "OK");
  	   	 //this.
  	   	 RequestFromServer r = (RequestFromServer) new RequestFromServer(url){
  			// Optional callback override.
  		    @Override
  		    protected void onSuccess(Transport transport) {
  		    	JSONObject response = transport.getResponseJson();
  		    	if ( response == null )
  		    		error("Server Error.");
  		    	else if ( response.optString("success").equals("1") ) {
  		    		success("Vote", "Successfully commented!");
  		    		String date = response.optString("date");
  		    		int type;
  		    		if ( commentType.equals(FIRST_TYPE) )
  		    			type = Vote.FIRST;
  		    		else
  		    			type = Vote.SECOND;
  		    		addComment(type, StaticMainClass.username, date, comment);
  		    	}
  		    	else
  		    	{
  		    		failure(response.optString("detail"));		
  		    	}
  		    }

  		    // Optional callback override.
  		    @Override
  		    protected void onError(IOException ex) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Error occured: " + ex.getMessage(), Toast.LENGTH_SHORT);
  		    	error("Server Error");
  		    }


  		    // Optional callback override.
  		    @Override
  		    protected void onFailure(Transport transport) {
  		        //Toast.makeText(mParent.getApplicationContext(), "Something went wrong. code: " + transport.getStatus(),Toast.LENGTH_SHORT);
  		    	failure();
  		    }
  		}.execute("POST");
  		r.accept(RequestFromServer.CTYPE_JSON); 
  		r.setContentType(RequestFromServer.CTYPE_JSON);
	}
	
	@Override
	protected void finalize() throws Throwable {
		voteNumber = -1;
		if (updateTimer != null) {
			updateTimer.cancel();
		}
		super.finalize();
	}
	
	public void success(String title, String msg) {
		pDialog.hide();
		AlertUtil.messageAlert(getActivity(), title, msg);
		Toast.makeText(getActivity(), "Successful Operation!",Toast.LENGTH_SHORT); 
	}
	
	public void failure(String msg) {
		pDialog.hide();
		AlertUtil.messageAlert(getActivity(), "Request", msg);
		Toast.makeText(getActivity(), "Something went wrong. code",Toast.LENGTH_SHORT); 
	}
	
	public void failure() {
		pDialog.hide();
		AlertUtil.messageAlert(getActivity(), "Request", "Failure");
		Toast.makeText(getActivity(), "Something went wrong. code",Toast.LENGTH_SHORT); 
	}
	
	public void error(String msg)
	{
		pDialog.hide();
		AlertUtil.messageAlert(getActivity(), "Request", msg);
		Toast.makeText(getActivity(), "Error occured", Toast.LENGTH_SHORT);
	}
}
