package com.pradumnkmahanta.nearbyplaces.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.pradumnkmahanta.nearbyplaces.R;

public class AlertDialogManager {
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);alertDialog.setIcon((status) ? R.drawable.icon_success : R.drawable.icon_faliure);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
