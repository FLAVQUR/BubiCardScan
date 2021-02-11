package com.plugin.bubicardscan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.intsig.idcardscan.sdk.CommonUtil;
import com.intsig.idcardscan.sdk.IDCardScanSDK;
import com.intsig.idcardscan.sdk.ISCardScanActivity;
import com.intsig.idcardscan.sdk.ResultData;
import com.intsig.nativelib.IDCardScan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.intsig.idcardscan.sdk.ISCardScanActivity.EXTRA_KEY_RESULT_DATA;
import static com.intsig.idcardscan.sdk.ISCardScanActivity.EXTRA_KEY_RESULT_ERROR_CODE;

public class BubiCardScanActivity extends Activity {
    private boolean firstTime = true;	
	private static final String TAG = "BubiCardScan";
    private static final String APP_KEY = "1C5CF8F7430740F2QY9UY907";
    private static final int REQ_CODE_CAPTURE = 100;
    public static final String DIR_IMG_RESULT = Environment.getExternalStorageDirectory() + "/idcardscan/";
    int crossChecked = 0;
    int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 2002;
	
	@Override
    public void onStart() {
        super.onStart();

        // Write your code inside this condition
        // Here should start the process that expects the onActivityResult
		checkCameraPersimion();
		
        if (firstTime == true) {
			Intent intent = new Intent(this, ISCardScanActivity.class);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_CROSS_CHECK, crossChecked);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_BOOL_ALL_SCREEN, false);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_ORIENTATION, ISCardScanActivity.ORIENTATION_HORIZONTAL);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_TIPS_FONT_SIZE, 18);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_TIPS_FONT_COLOR, Color.WHITE);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_SHOW_CLOSE, true);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_IMAGE_FOLDER, DIR_IMG_RESULT);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_MATCH, 0xff2A7DF3);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_COLOR_NORMAL, 0xff01d2ff);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_APP_KEY, APP_KEY);
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_TIPS, "Please put your ID card in the box for identification");
			intent.putExtra(ISCardScanActivity.EXTRA_KEY_COMPLETECARD_IMAGE, IDCardScanSDK.OPEN_COMOLETE_CHECK);

			startActivityForResult(intent, REQ_CODE_CAPTURE);
        }
		
		IDCardScan.SetGrayAndBlurJuge(0.5f, 0.f);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQ_CODE_CAPTURE) {
            ResultData result = (ResultData) data.getSerializableExtra(EXTRA_KEY_RESULT_DATA);

			Intent intent = new Intent();
			JSONObject captureJson;
			try {
				captureJson = buildCaptureJsonObject(result);
			} catch (JSONException e) {
				Log.d(TAG, "userCompleted: failed to build json result");
				return;
			}
			
			Log.d(TAG, "result: " + captureJson.toString());
			intent.putExtra("data", captureJson.toString());
			setResult(Activity.RESULT_OK, intent);
			finish();

        } else if (resultCode == RESULT_CANCELED && requestCode == REQ_CODE_CAPTURE) {
			Intent intent = new Intent();
            Log.d(TAG, "Recognition failed or cancelled");
            if (data != null) {
                int error_code = data.getIntExtra(EXTRA_KEY_RESULT_ERROR_CODE, 0);
                Toast.makeText(this, "Error:" + error_code + "\nMsg:" + CommonUtil.getPkgSigKeyLog(BubiCardScanActivity.this, APP_KEY), Toast.LENGTH_LONG).show();
            }
			setResult(Activity.RESULT_CANCELED, intent);
            finish();
        }
    }
	
	protected JSONObject buildCaptureJsonObject(ResultData resultData) throws JSONException {
        JSONObject captureJson = new JSONObject();

        JSONObject docJson = new JSONObject();

        docJson.put("Id", resultData.getId());
        docJson.put("Name", resultData.getName());
        docJson.put("Sex", resultData.getSex());
        docJson.put("National", resultData.getNational());
        docJson.put("Birthday", resultData.getBirthday());
        docJson.put("Address", resultData.getIssueauthority());
        docJson.put("Validity", resultData.getValidity());
        docJson.put("IsFront", resultData.isFront());
        docJson.put("IdShotsPath", resultData.getIdShotsPath());
        docJson.put("OriImagePath", resultData.getOriImagePath());
        docJson.put("TrimImagePath", resultData.getTrimImagePath());
        docJson.put("AvatarPath", resultData.getAvatarPath());
        docJson.put("IsColorImage", resultData.isColorImage());
        docJson.put("IsBlurImage", resultData.isBlurImage());
        docJson.put("Angel", resultData.getAngel());
        docJson.put("Regtime", resultData.getRegtime());

        captureJson.put("document", docJson);

        return captureJson;
    }

	public void checkCameraPersimion() {
        List<String> grantedlist = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            grantedlist.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            grantedlist.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (grantedlist.size() != 0) {
            String[] grantArray = grantedlist.toArray(new String[grantedlist.size()]);
            ActivityCompat.requestPermissions(this, grantArray, MY_PERMISSIONS_REQUEST_CALL_PHONE2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(BubiCardScanActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}