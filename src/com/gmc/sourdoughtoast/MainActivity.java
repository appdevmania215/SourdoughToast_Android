package com.gmc.sourdoughtoast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.LoggingBehavior;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.OpenRequest;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.facebook.model.GraphObject;
import com.gmc.sourdoughtoast.utils.AlertUtil;
import com.gmc.sourdoughtoast.utils.RequestFromServer;
import com.gmc.sourdoughtoast.utils.RequestUri;
import com.gmc.sourdoughtoast.utils.Transport;

public class MainActivity extends FragmentActivity {
	
	public static final int LOGIN = 0;
	public static final int FACEBOOK_LOGIN = 1;
	public static final int TWITTER_LOGIN = 2;
	public static final int NO_THANKS = 3;
	public static final int OPEN = 4;
	public static final int CLOSED = 5;
	public static final int SETTINGS = 6;
	public static final int VOTE = 7;
	public static final int LOGOUT = 8;
	
	public static ProgressDialog pdialog = null;
	
	RelativeLayout layoutContent = null;
	FragmentManager fragmentManager;  
    FragmentTransaction fragmentTransaction;
    
 // Facebook
    //private static final String URL_PREFIX_FRIENDS = "https://graph.facebook.com/me/friends?access_token=";
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    
    static final String appId = "215639185279182";
    static final String PENDING_REQUEST_BUNDLE_KEY = "com.gmc.sourdoughtoast:PendingRequest";
    boolean pendingRequest;
    Session session;
    
    int action = SETTINGS;
    
    static boolean isLogined = false;
    boolean isFacebookLogin = false;
    
    Button btnOpen = null;
    Button btnClosed = null;
    Button btnSettings = null;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		pdialog=new ProgressDialog(this);
	   	pdialog.setCancelable(false);
	   	pdialog.setMessage("Loading ....");
	   	
	   	Settings.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
	   	
        session = Session.getActiveSession();
        if (session == null) {
            if (savedInstanceState != null) {
                session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
            }
            if (session == null) {
                session = new Session(MainActivity.this);
            }
            Session.setActiveSession(session);
            if (session.getState().equals(SessionState.CREATED_TOKEN_LOADED) && isLogined ) {
                session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
            }
        }
        
		layoutContent = (RelativeLayout) findViewById(R.id.layoutContent);

		fragmentManager = getSupportFragmentManager();  
		
		btnOpen = (Button)findViewById(R.id.btnOpen);
		btnClosed = (Button) findViewById(R.id.btnClosed);
		btnSettings = (Button) findViewById(R.id.btnSettings);

		onClickNavButton();
	}
	
	@Override
	public void onResumeFragments () {
		super.onResumeFragments();
		AppEventsLogger.activateApp(this);
		if ( action == StaticMainClass.mainclass )
			return;
		action = StaticMainClass.mainclass;
		switch ( action ) {
		case LOGIN:
			loginFragment();
			break;
		case FACEBOOK_LOGIN:
			onFacebookLogin();
			break;
		case TWITTER_LOGIN:
			onTwitterLogin();
			break;
		case OPEN:
			open();
			break;
		case CLOSED:
			closed();
			break;
		case SETTINGS:
			settings();
			break;
		case VOTE:
			vote();
			break;
		case LOGOUT:
			logout();
			break;
		}
	}
    
	public void onFacebookLogin() {
		session = Session.getActiveSession();
		if (session.isOpened()) {
            getProfileInformation();
        } else {
            loginToFacebook();
        }
	}
	
	@Override
    public void onStart() {
        super.onStart();
        Session.getActiveSession().addCallback(statusCallback);
    }

    @Override
    public void onStop() {
        super.onStop();
        Session.getActiveSession().removeCallback(statusCallback);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
        
        session = Session.getActiveSession();
        if (data != null && isLogined && session.onActivityResult(this, requestCode, resultCode, data) &&
                pendingRequest &&
                session.getState().isOpened() ) {
        	getProfileInformation();
        }
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        pendingRequest = savedInstanceState.getBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        session = Session.getActiveSession();
        Session.saveSession(session, outState);
        
        outState.putBoolean(PENDING_REQUEST_BUNDLE_KEY, pendingRequest);
    }

    private void loginToFacebook() {
    	isFacebookLogin = true;
        session = Session.getActiveSession();
        if ( session != null && !session.isOpened() && !session.isClosed()) {
        	// Ask for username and password
            OpenRequest op = new Session.OpenRequest(this);

            //op.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
            op.setCallback(statusCallback);
            List<String> permissions = new ArrayList<String>();
            permissions.add("email");
            op.setPermissions(permissions);

            Session.setActiveSession(session);
            session.openForRead(op);
        } else {
            Session.openActiveSession(this, true, statusCallback);
        }
        /*if (this.session.isOpened()) {
            getProfileInformation();
        } else {
            StatusCallback callback = new StatusCallback() {
                public void call(Session session, SessionState state, Exception exception) {
                    if (exception != null) {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(R.string.login_failed_dialog_title)
                                .setMessage(exception.getMessage())
                                .setPositiveButton(R.string.ok_button, null)
                                .show();
                        MainActivity.this.session = createSession();
                    }
                }
            };
            pendingRequest = true;
            this.session.openForRead(new Session.OpenRequest(this).setCallback(callback));
        }*/
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	if ( isFacebookLogin == true )
        		getProfileInformation();
        }
    }
	
	public void getProfileInformation() {
		session = Session.getActiveSession();
		if ( session.isOpened() ) {
			pdialog.show();
			Bundle params = new Bundle();
			params.putString("fields", "id,name,first_name,last_name,username,email,verified");
			//params.putString("fields", "id,name,installed,picture");
	        Request rq = new Request(session, "me", params, HttpMethod.GET, new Request.Callback() {
	            public void onCompleted(Response response) {
	                GraphObject graphObject = response.getGraphObject();
	                FacebookRequestError error = response.getError();
	                if (graphObject != null) {
	                	System.out.println(graphObject.toString());
	                    if (graphObject.getProperty("id") != null) {
	                    	System.out.println(graphObject.toString());
	                        String id = graphObject.getProperty("id").toString();
	    	                // getting name of the user
	    	                final String fname;
	    	                if ( graphObject.getProperty("first_name") != null )
	    	                	fname = graphObject.getProperty("first_name").toString();
	    	                else
	    	                	fname = "";
	    	                final String lname;
	    	                if ( graphObject.getProperty("last_name") != null )
	    	                	lname = graphObject.getProperty("last_name").toString();
	    	                else
	    	                	lname = "";
	    	                // getting email of the user
	    	                String email = "";
	    	                if ( graphObject.getProperty("email") != null )
	    	                	email = graphObject.getProperty("email").toString();
	    	                
	    	                String oauthProvider = "facebook";
	    	                int isVerified = 0;
	    	                if ( graphObject.getProperty("verified") != null && graphObject.getProperty("verified").toString() == "true" )
	    	                	isVerified = 1;
	    	                
	    	                String url = RequestUri.url + "?action=login&oauthUid=" + id + "&firstname=" + fname + "&lastname=" + lname + "&email=" + email + "&oauthProvider=" + oauthProvider + "&isVerified=" + isVerified + "&phone=";
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
	    		       		    		StaticMainClass.userid = response.optInt("userId");
	    		       		    		StaticMainClass.oauthuid = response.optString("oauthUid");
	    		       		    		StaticMainClass.username = fname + " " + lname;
	    		       		    		//success("Login", "Successfully logined with Facebook");
	    		       		    		isLogined = true;
	    		       		    		System.out.println(StaticMainClass.userid + ":" + StaticMainClass.oauthuid + ":" + StaticMainClass.username);
	    		       		    		StaticMainClass.mainclass = 4;
	    		       		    		onResumeFragments();
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
	                    } else {
	                        failure();
	                    }
	                } else if (error != null) {
	                    error(error.getErrorMessage());
	                }
	            }
	        });
	        pendingRequest = false;
	        Request.executeBatchAsync(rq);     
		}
	}
	
	private void onFacebookLogout() {
		isFacebookLogin = false;
		session = Session.getActiveSession();
        if ( !session.isClosed() ) {
            session.closeAndClearTokenInformation();
            //session.close();
        }
        session = null;
	}
	
	public void onTwitterLogin() {
		
	}
	
	@Override
	public void onBackPressed(){
	  if (getSupportFragmentManager().getBackStackEntryCount() == 1){
	    finish();
	  }
	  else {
	    super.onBackPressed();
	  }
	}
	
	private void replaceFragment (Fragment fragment){
	  String backStateName = fragment.getClass().getName();

	  FragmentManager manager = getSupportFragmentManager();
	  boolean fragmentPopped = manager.popBackStackImmediate (backStateName, 0);

	  if (!fragmentPopped){ //fragment not in back stack, create it.
	    FragmentTransaction ft = manager.beginTransaction();
	    ft.replace(R.id.layoutContent, fragment);
	    ft.addToBackStack(backStateName);
	    ft.commit();
	  }
	}
	
	public void loginFragment() {
//		try {
//    		layoutContent.removeAllViews();
//		} catch (Exception e) {
//		}
		FragmentLogin framLogin = new FragmentLogin();
        replaceFragment(framLogin);
//        fragmentTransaction = fragmentManager.beginTransaction();  
//        fragmentTransaction.add(layoutContent.getId(), framLogin);  
//           
//        fragmentTransaction.commit(); 
	}
	
	public void open() {
//		try {
//    		layoutContent.removeAllViews();
//		} catch (Exception e) {
//		}
		onClickNavButton();
		FragmentOpenList framVoterList = new FragmentOpenList();
		replaceFragment(framVoterList);
//        fragmentTransaction = fragmentManager.beginTransaction();  
//        fragmentTransaction.add(layoutContent.getId(), framVoterList);  
//           
//        fragmentTransaction.commit();
	}
	public void closed() {
//		try {
//    		layoutContent.removeAllViews();
//		} catch (Exception e) {
//		}
		onClickNavButton();
		FragmentClosedList framVoterList = new FragmentClosedList();
		replaceFragment(framVoterList);
//        fragmentTransaction = fragmentManager.beginTransaction();  
//        fragmentTransaction.add(layoutContent.getId(), framVoterList);  
//           
//        fragmentTransaction.commit(); 
	}
	
	public void vote() {
//		try {
//    		layoutContent.removeAllViews();
//		} catch (Exception e) {
//		}
		FragmentVote framVote = new FragmentVote();
		replaceFragment(framVote);
//        fragmentTransaction = fragmentManager.beginTransaction();  
//        fragmentTransaction.add(layoutContent.getId(), framVote);  
//           
//        fragmentTransaction.commit(); 
	}
	
	public void settings() {
		onClickNavButton();
		if ( isLogined ) {
//			try {
//	    		layoutContent.removeAllViews();
//			} catch (Exception e) {
//			}
			FragmentSettings framSettings = new FragmentSettings();
			replaceFragment(framSettings);
//	        fragmentTransaction = fragmentManager.beginTransaction();  
//	        fragmentTransaction.add(layoutContent.getId(), framSettings);  
//	           
//	        fragmentTransaction.commit();
		}
		else
		{
			action = LOGIN;
			loginFragment();
		}
	}
	
	private void logout() {
		if ( isFacebookLogin == true )
			onFacebookLogout();
		
		isLogined = false;
		StaticMainClass.userid = -1;
		StaticMainClass.username = "";
		StaticMainClass.mainclass = LOGIN;
		this.onResumeFragments();
	}
	
	public void onOpen(View v) {
		StaticMainClass.mainclass = OPEN;
		this.onResumeFragments();
	}
	
	public void onClosed(View v) {
		StaticMainClass.mainclass = CLOSED;
		this.onResumeFragments();
	}
	
	public void onSettings(View v) {
		StaticMainClass.mainclass = SETTINGS;
		this.onResumeFragments();
	}
	
	private void onClickNavButton() {
		if ( action == OPEN ) {
			btnOpen.setBackgroundColor(Color.parseColor("#232323"));
			btnOpen.setTextColor(Color.WHITE);
		}
		else {
			btnOpen.setBackgroundColor(Color.parseColor("#444444"));
			btnOpen.setTextColor(Color.LTGRAY);
		}
		
		if ( action == CLOSED ) {
			btnClosed.setTextColor(Color.WHITE);
			btnClosed.setBackgroundColor(Color.parseColor("#232323"));
		}
		else {
			btnClosed.setTextColor(Color.LTGRAY);
			btnClosed.setBackgroundColor(Color.parseColor("#444444"));
		}
		
		if ( action == SETTINGS ) {
			btnSettings.setTextColor(Color.WHITE);
			btnSettings.setBackgroundColor(Color.parseColor("#232323"));
		}
		else {
			btnSettings.setTextColor(Color.LTGRAY);
			btnSettings.setBackgroundColor(Color.parseColor("#444444"));
		}
	}
	
	public void success(String title, String msg) {
		pdialog.hide();
		AlertUtil.messageAlert(this, title, msg);
		Toast.makeText(this, "Successful Operation!",Toast.LENGTH_SHORT); 
	}
	
	public void failure(String msg) {
		pdialog.hide();
		AlertUtil.messageAlert(this, "Request", msg);
		Toast.makeText(this, "Something went wrong. code",Toast.LENGTH_SHORT); 
	}
	
	public void failure() {
		pdialog.hide();
		AlertUtil.messageAlert(this, "Request", "Failure");
		Toast.makeText(this, "Something went wrong. code",Toast.LENGTH_SHORT); 
	}
	
	public void error(String msg)
	{
		pdialog.hide();
		AlertUtil.messageAlert(this, "Request", msg);
		Toast.makeText(this, "Error occured", Toast.LENGTH_SHORT);
	}
}
