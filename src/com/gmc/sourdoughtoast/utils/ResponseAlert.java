package com.gmc.sourdoughtoast.utils;

import android.content.Context;

public class ResponseAlert {
	
	public static final void success(Context context) {
		AlertUtil.messageAlert(context, "Manage Programs", "Success");
		//Toast.makeText(mParent.getApplicationContext(), "Successful Operation!",Toast.LENGTH_SHORT); 
	}
	
	public static final void failure(Context context) {
		AlertUtil.messageAlert(context, "Manage Programs", "Failure");
		//Toast.makeText(mParent.getApplicationContext(), "Something went wrong. code",Toast.LENGTH_SHORT); 
	}
	
	public static final void error(Context context)
	{
		AlertUtil.messageAlert(context, "Manage Programs", "Server Error");
		//Toast.makeText(mParent.getApplicationContext(), "Error occured", Toast.LENGTH_SHORT);
	}
}
