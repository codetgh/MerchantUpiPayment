package tech.tgh.com.merchantupipayment;

import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.camera.CameraSettings;

import tech.tgh.com.merchantupipayment.utils.WindowHeighAndWidth;

public class ScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageView switchFlashlightButton;

    boolean onOrNot;
    CameraSettings settings;

    String companyName,branchName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        if(getSupportActionBar() != null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);

        Size s = new Size(WindowHeighAndWidth.getScreenWidth(ScanActivity.this)-50,500);
        barcodeScannerView.getBarcodeView().setFramingRectSize(s);
        barcodeScannerView.setTorchListener(this);

        // Camera settings
        settings = new CameraSettings();
        //settings.setFocusMode(CameraSettings.FocusMode.CONTINUOUS);
        settings.setFocusMode(CameraSettings.FocusMode.AUTO);
        barcodeScannerView.getBarcodeView().setCameraSettings(settings);
        switchFlashlightButton = (ImageView)findViewById(R.id.switch_flashlight);

        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();
    }
    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (!onOrNot) {
            barcodeScannerView.setTorchOn();
            onOrNot=true;
        }else{
            barcodeScannerView.setTorchOff();
            onOrNot=false;
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTorchOn() {

        switchFlashlightButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on));
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off));
    }


}
