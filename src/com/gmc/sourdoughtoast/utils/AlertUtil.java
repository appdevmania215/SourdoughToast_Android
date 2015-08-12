package com.gmc.sourdoughtoast.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.gmc.sourdoughtoast.R;
 
public class AlertUtil {
 
    private static final int MESSAGE_ALERT = 1;
    private static final int CONFIRM_ALERT = 2;
    private static final int DECISION_ALERT = 3;
 
    public static void messageAlert(Context ctx, String title, String message) {
        showAlertDialog(MESSAGE_ALERT, ctx, title, message, null, "OK");
    }
 
    public static void confirmationAlert(Context ctx, String title, String message, DialogInterface.OnClickListener callBack) {
        showAlertDialog(CONFIRM_ALERT, ctx, title, message, callBack, "OK", "Cancel");
    }
 
    public static void decisionAlert(Context ctx, String title, String message, DialogInterface.OnClickListener posCallback, String... buttonNames) {
        showAlertDialog(DECISION_ALERT, ctx, title, message, posCallback, buttonNames);
    }
 
    public static void showAlertDialog(int alertType, Context ctx, String title, String message, DialogInterface.OnClickListener posCallback, String... buttonNames) {
        if ( title == null ) title = ctx.getResources().getString(R.string.app_name);
        if ( message == null ) message = "default message";
 
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle(title)
                .setMessage(message)
 
                // false = pressing back button won't dismiss this alert
                .setCancelable(false)
 
                // icon on the left of title
                .setIcon(android.R.drawable.ic_dialog_alert);
 
        switch (alertType) {
            case MESSAGE_ALERT:
                break;
 
            case CONFIRM_ALERT:
                builder.setPositiveButton(buttonNames[0], posCallback);
                break;
 
            case DECISION_ALERT:
                break;
        }
 
        builder.setNegativeButton(buttonNames [buttonNames.length - 1], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }
}