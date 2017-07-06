package com.pradumnkmahanta.nearbyplaces.utilities;

import android.content.Context;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * WsEditProfile class created.
 */
public class WsEditProfile {
    private Context mContext;
    private int code;
    private String message;

    public WsEditProfile(final Context mContext) {
        this.mContext = mContext;
//        message = mContext.getString(R.string.something_went_wrong_msg);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void executeService(Bundle bundle) {
//        final String url = WSConstants.BASE_URL + WSConstants.WS_UPDATE_PROFILE;
//        final String response;
//        response = new WSUtil().callServiceHttpPost(url, generateRequest(bundle));
//        parseResponse(response);
//        AppLog.showLogD("Result", "" + response);


    }

//    private RequestBody generateRequest(final Bundle bundle) {
//        final PreferenceUtils preferenceUtils = new PreferenceUtils(mContext);
//        final MultipartBuilder multipartBuilder = new MultipartBuilder();
//        multipartBuilder.addFormDataPart(WSConstants.WS_KEY_MOBILE_NUMBER, preferenceUtils.getString(preferenceUtils.KEY_USER_MOBILE));
//        multipartBuilder.type(MultipartBuilder.FORM);
//        multipartBuilder.addFormDataPart(WSConstants.WS_KEY_FIRSTNAME, bundle.getString(WSConstants.WS_KEY_FIRSTNAME));
//        multipartBuilder.addFormDataPart(WSConstants.WS_KEY_LASTNAME, bundle.getString(WSConstants.WS_KEY_LASTNAME));
//        multipartBuilder.addFormDataPart(WSConstants.WS_KEY_EMAIL, bundle.getString(WSConstants.WS_KEY_EMAIL));
//        multipartBuilder.addFormDataPart(WSConstants.WS_KEY_GENDER, bundle.getString(WSConstants.WS_KEY_GENDER));
//        multipartBuilder.addFormDataPart(WSConstants.WS_KEY_ABOUT_ME, bundle.getString(WSConstants.WS_KEY_ABOUT_ME));
//        multipartBuilder.addFormDataPart(WSConstants.WS_KEY_DATE_OF_BIRTH, bundle.getString(WSConstants.WS_KEY_DATE_OF_BIRTH));
//
//        if (!TextUtils.isEmpty(bundle.getString(WSConstants.WS_KEY_COVER_IMAGE))) {
//            final File fileBackgroundImage = new File((bundle.getString(WSConstants.WS_KEY_COVER_IMAGE)));
//            multipartBuilder.addFormDataPart(WSConstants.WS_KEY_COVER_IMAGE, fileBackgroundImage.getName(),
//                    RequestBody.create(MediaType.parse("image/png"), fileBackgroundImage));
//
//        }
//        if (!TextUtils.isEmpty(bundle.getString(WSConstants.WS_KEY_PROFILE_IMAGE))) {
//            final File fileProfileImage = new File(bundle.getString(WSConstants.WS_KEY_PROFILE_IMAGE));
//            multipartBuilder.addFormDataPart(WSConstants.WS_KEY_PROFILE_IMAGE, fileProfileImage.getName(),
//                    RequestBody.create(MediaType.parse("image/png"), fileProfileImage));
//
//        }
//        return multipartBuilder.build();
//
//    }


    private void parseResponse(final String response) {
        if (response != null && response.toString().trim().length() > 0) {
            final JSONObject mainObject;
            try {
                mainObject = new JSONObject(response);
                JSONObject settingObject = mainObject.getJSONObject("settings");
                code = settingObject.optInt("success");
                message = settingObject.optString("message");
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }
}
