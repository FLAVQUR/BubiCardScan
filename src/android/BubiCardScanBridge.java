package com.plugin.bubicardscan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONException;


public class BubiCardScanBridge extends CordovaPlugin {
	private static final String TAG = "BubiCardScan";
	private static final int REQ_CODE_CAPTURE = 100;
	private CallbackContext currentCallbackContext = null;

	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);

		Log.d(TAG, "Initializing BubiCardScan");
	}

    public boolean execute(String action, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
			this.currentCallbackContext = callbackContext;
			
			Intent intent = new Intent("com.plugin.bubicardscan.BubiCardScanActivity");
			cordova.startActivityForResult(this, intent, REQ_CODE_CAPTURE);
            return true;
        }
        return false;
    }

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == cordova.getActivity().RESULT_OK){
			Bundle extras = data.getExtras();// Get data sent by the Intent
			String information = extras.getString("data"); // data parameter will be send from the other activity.
			PluginResult plugResult = new PluginResult(PluginResult.Status.OK, information);
			plugResult.setKeepCallback(true);
			currentCallbackContext.sendPluginResult(plugResult);
			return;
		}else if(resultCode == cordova.getActivity().RESULT_CANCELED){
			PluginResult plugResult = new PluginResult(PluginResult.Status.OK, "canceled action, process this in javascript");
			plugResult.setKeepCallback(true);
			currentCallbackContext.sendPluginResult(plugResult);
			return;
		}
		// Handle other results if exists.
		super.onActivityResult(requestCode, resultCode, data);
    }
}
