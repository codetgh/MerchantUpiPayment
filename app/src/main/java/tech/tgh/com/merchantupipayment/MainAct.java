package tech.tgh.com.merchantupipayment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import tech.tgh.com.merchantupipayment.events.ListEvent;
import tech.tgh.com.merchantupipayment.adapters.CustomChooserAdapter;
import tech.tgh.com.merchantupipayment.models.EvalInstalledAppInfo;
import tech.tgh.com.merchantupipayment.utils.ActionSheet;
import tech.tgh.com.merchantupipayment.utils.ApkInfoUtil;
import tech.tgh.com.merchantupipayment.utils.CheckNetworkConnection;
import tech.tgh.com.merchantupipayment.utils.CommonUtil;
import tech.tgh.com.merchantupipayment.utils.Constants;

public class MainAct extends AppCompatActivity implements ActionSheet.MenuItemClickListener{

    private static final String TAG = "MainAct";
    private Context context = MainAct.this;

    private Context mContext = context;
    private static final int PAY_REQ_CODE = 938;

    private EditText mEtMerchantVpa;
    private EditText mEtMerchantName;
    private EditText mEtOrderAmt;
    private EditText mEtRemark;

    private String appName;
    private ActionSheet actionSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEtMerchantName = findViewById(R.id.merchantName);
        mEtMerchantVpa = findViewById(R.id.merchantVpa);
        mEtOrderAmt = findViewById(R.id.orderAmt);
        mEtRemark = findViewById(R.id.remark);
        Button mBtUpiPay = findViewById(R.id.upiPay);

        mBtUpiPay.setBackgroundDrawable(CommonUtil.setRoundedCorner(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.colorPrimary),15, GradientDrawable.RECTANGLE));
        mBtUpiPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Encode data
                String merchantName = Uri.encode(mEtMerchantName.getText().toString());

                String merchantVpa = mEtMerchantVpa.getText().toString();
                DecimalFormat form = new DecimalFormat("0.00");
                String orderAmt = form.format(Double.parseDouble(mEtOrderAmt.getText().toString()));
                String remark = Uri.encode(mEtRemark.getText().toString());

                /**
                 * here we create upi url string which listen by our available UPI app. All these data auto fill the
                 * UPI app respective editText and user can't change them.
                 * cu = currency which is default INR
                 * */

                String upiStrWithData = "upi://pay?pa=" + merchantVpa + "&pn=" + merchantName + "&am=" + orderAmt + "&cu=INR&tn=" + remark;

                payWithUpi(upiStrWithData);

            }
        });
    }

    private void payWithUpi(String upiStr) {
        Intent intent = new Intent();
        intent.setData(Uri.parse(upiStr));
        String[] desireApp = new String[]{"net.one97.paytm", "com.freecharge.android",
                "com.google.android.apps.nbu.paisa.user"};

        //Intent chooser = Intent.createChooser(intent,"Pay with..");
        //Intent custumChooser = generateCustomChooserIntent(intent,new String[]{"com.myairtelapp","com.phonepe.app",
        // "in.org.npci.upiapp","com.whatsapp"});
        /*Intent customChooser = generateCustomChooserIntentWithFixApp(intent, new String[]{"net.one97.paytm", "com.freecharge.android",
                "com.google.android.apps.nbu.paisa.user"});
        startActivityForResult(customChooser, PAY_REQ_CODE);*/

        //with normal layout
        /*bottomDialogAsChooserCustom(intent, new String[]{"net.one97.paytm", "com.freecharge.android",
                "com.google.android.apps.nbu.paisa.user"});*/

        //custom chooser in android material layout
        setTheme(R.style.ActionSheetStyle);
        showActionSheet(intent,desireApp);

    }

    public void showActionSheet(Intent intent,String[] desireApp ) {
        actionSheet = new ActionSheet(this);
        actionSheet.setCancelButtonTitle("Cancel");

        //want to set list call this method
        //actionSheet.addItems("Cricket","Football","Hockey","BasketBall");

        //want to set gridLayout call this method or you can add any type of custom layout
        //get view with populated data and pass to
        View view = bottomDialogAsChooserCustom(intent,desireApp);
        actionSheet.setBottomDialogAsCustomView(view);

        actionSheet.setItemClickListener(this);
        actionSheet.setCancelableOnTouchMenuOutside(true);
        actionSheet.showMenu();
    }


    private View bottomDialogAsChooserCustom(final Intent prototype, String[] desiredApp) {
        String noAppMsg = "";
        List<ResolveInfo> resInfo = mContext.getPackageManager().queryIntentActivities(prototype, 0);
        EvalInstalledAppInfo evalInstalledAppInfo;
        final ArrayList<EvalInstalledAppInfo> evalInstalledAppInfoArrayList = new ArrayList<>();

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (Arrays.asList(desiredApp).contains(resolveInfo.activityInfo.packageName)) {

                    ApkInfoUtil apkInfoUtil = new ApkInfoUtil(mContext);
                    Drawable appIcon = apkInfoUtil.getAppIconByPackageName(resolveInfo.activityInfo.packageName);

                    if(String.valueOf(resolveInfo.activityInfo.loadLabel(mContext.getPackageManager())).toLowerCase().equals("UPI In-app payments".toLowerCase()))
                    {
                        evalInstalledAppInfo = new EvalInstalledAppInfo(appIcon,
                                resolveInfo.activityInfo.name, resolveInfo.activityInfo.packageName,
                                "Google Pay");
                    }else
                    {
                        evalInstalledAppInfo = new EvalInstalledAppInfo(appIcon,
                                resolveInfo.activityInfo.name, resolveInfo.activityInfo.packageName,
                                String.valueOf(resolveInfo.activityInfo.loadLabel(mContext.getPackageManager())));
                    }
                    evalInstalledAppInfoArrayList.add(evalInstalledAppInfo);
                }
            }

            if (!evalInstalledAppInfoArrayList.isEmpty()) {
                // sorting for nice readability
                Collections.sort(evalInstalledAppInfoArrayList, new Comparator<EvalInstalledAppInfo>() {
                    @Override
                    public int compare(EvalInstalledAppInfo map, EvalInstalledAppInfo map2) {
                        return map.getSimpleName().compareTo(map2.getSimpleName());
                    }
                });

            }else
            {
                noAppMsg = "Desired app is not available in your android phone.Please try with GPay,Paytm and Freecharge.";
            }
        }else
        {
            noAppMsg = "There is no UPI app in your mobile.";
            Toast.makeText(mContext, "There is no UPI app in your mobile.", Toast.LENGTH_SHORT).show();
        }

        //View view = getLayoutInflater().inflate(R.layout.custom_chooser_main_layout, null);
        View mainLayoutInGridView = getLayoutInflater().inflate(R.layout.custom_chooser_main_layout, null);

        /*final BottomSheetDialog dialog = new BottomSheetDialog(mContext); //for this you have to use design lib

        //dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box_bg);

        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();*/

        GridView customChooserMainGv = (GridView) mainLayoutInGridView.findViewById(R.id.chooserGv);
        TextView noData = (TextView) mainLayoutInGridView.findViewById(R.id.noDataTextView);
        Button cancelDialog = (Button) mainLayoutInGridView.findViewById(R.id.cancel);

        cancelDialog.setVisibility(View.GONE);

        mainLayoutInGridView.setBackgroundDrawable(CommonUtil.setRoundedCorner(Color.WHITE,Color.WHITE,10,
                GradientDrawable.RECTANGLE));

        if(!evalInstalledAppInfoArrayList.isEmpty())
        {
            noData.setVisibility(View.GONE);
            customChooserMainGv.setVisibility(View.VISIBLE);

            CustomChooserAdapter customChooserAdapter = new CustomChooserAdapter(mContext);
            customChooserAdapter.setDataAdapter(evalInstalledAppInfoArrayList, new ListEvent() {
                @Override
                public void onLongClick(int index) {

                }

                @Override
                public void onClick(final int index) {

                    if(evalInstalledAppInfoArrayList.get(index).getSimpleName().toLowerCase().equals("UPI In-app payments".toLowerCase()))
                    {
                        appName = "Google Pay";
                    }else
                    {
                        appName = evalInstalledAppInfoArrayList.get(index).getSimpleName();
                    }

                    //paytm doesn't open the app perfectly if it is not open already in background. so i have used this way to work
                    //some time.
                    if(evalInstalledAppInfoArrayList.get(index).getSimpleName().toLowerCase().equals("Paytm".toLowerCase()))
                    {
                        ((MainAct) mContext).startActivity(mContext.getPackageManager().getLaunchIntentForPackage(evalInstalledAppInfoArrayList.get(index).getPackageName()));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: "+"in 5 second");
                                Intent targetedShareIntent = (Intent) prototype.clone();
                                targetedShareIntent.setPackage(evalInstalledAppInfoArrayList.get(index).getPackageName());
                                targetedShareIntent.setClassName(evalInstalledAppInfoArrayList.get(index).getPackageName(),
                                        evalInstalledAppInfoArrayList.get(index).getClassName());
                                ((Activity)mContext).startActivityForResult(targetedShareIntent, Constants.PAY_REQ_CODE);
                                //FgtPaymentNdOffers.this.startActivityForResult(targetedShareIntent, ActivityCode.PAY_REQ_CODE);
                            }
                        },1800);
                    }else {
                        Intent targetedShareIntent = (Intent) prototype.clone();
                        targetedShareIntent.setPackage(evalInstalledAppInfoArrayList.get(index).getPackageName());
                        targetedShareIntent.setClassName(evalInstalledAppInfoArrayList.get(index).getPackageName(),
                                evalInstalledAppInfoArrayList.get(index).getClassName());
                        ((Activity) mContext).startActivityForResult(targetedShareIntent, Constants.PAY_REQ_CODE);

                        //When you want to use this in fragment.
                        //FgtPaymentNdOffers.this.startActivityForResult(targetedShareIntent, ActivityCode.PAY_REQ_CODE);
                    }
                    //dialog.dismiss();
                    actionSheet.dismissMenu ();
                }
            });
            customChooserMainGv.setAdapter(customChooserAdapter);

        }else
        {
            //noAppMsg = "Desired app is not available in your android phone.Please try with GPay, Paytm and Freecharge.";
            noData.setText(noAppMsg);
            noData.setPadding(Constants.NO_APP_DIALOG_MSG_PADDING,Constants.NO_APP_DIALOG_MSG_PADDING,
                    Constants.NO_APP_DIALOG_MSG_PADDING,Constants.NO_APP_DIALOG_MSG_PADDING);
            noData.setVisibility(View.VISIBLE);
            customChooserMainGv.setVisibility(View.GONE);
        }

        return mainLayoutInGridView;

    }

    @Override
    public void onItemClick(int itemPosition) {
        Toast.makeText(MainAct.this,"Item "+itemPosition+" Clicked",Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PAY_REQ_CODE:
                if (RESULT_OK == resultCode) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d(TAG, "onActivityResult: " + trxt);
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d(TAG, "onActivityResult: " + "Return data is null");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }

                } else if (resultCode == 11) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        Log.d(TAG, "onActivityResult: " + trxt);

                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        Log.d(TAG, "onActivityResult: " + "Return data is null"); //when user simply back without payment
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    Log.d(TAG, "onActivityResult: " + "Return data is null"); //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (CheckNetworkConnection.isConnectionAvailable(context)) {
            String str = data.get(0);
            String paymentCancel = "";
            if(str == null)
            {
                str = "discard";
            }
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");

                if(equalStr.length >= 2){
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                }else{
                    paymentCancel = "Payment cancel by user.";
                }
            }

            if (status.equals("success")) {
                //To what you want after successful transaction
                Toast.makeText(context, "Transaction successful.", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "responseStr: "+approvalRefNo);
            } else if("Payment cancel by user.".equals(paymentCancel)){
                Toast.makeText(context, "Payment cancel by user.", Toast.LENGTH_SHORT).show();

            }else{
                Toast.makeText(context, "Transaction failed.Please try again", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Internet connection is not available. Please check and try again", Toast.LENGTH_SHORT).show();
        }
    }

    //no us.just for understanding

    //default intent chooser
    private Intent generateCustomChooserIntentWithFixApp(Intent prototype, String[] desiredApp) {
        List<Intent> targetedShareIntents = new ArrayList<>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<>();
        Intent chooserIntent;

        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(prototype, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (Arrays.asList(desiredApp).contains(resolveInfo.activityInfo.packageName)) {

                    HashMap<String, String> info = new HashMap<>();
                    info.put("packageName", resolveInfo.activityInfo.packageName);
                    info.put("className", resolveInfo.activityInfo.name);
                    info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
                    intentMetaInfo.add(info);
                }
            }

            if (!intentMetaInfo.isEmpty()) {
                // sorting for upi app
                Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                        return map.get("simpleName").compareTo(map2.get("simpleName"));
                    }
                });

                // create the custom intent list
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }

                chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), "Pay with ..");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            } else {
                Toast.makeText(context, "There is no UPI App in your phone.", Toast.LENGTH_SHORT).show();
            }
        }

        return Intent.createChooser(prototype, "Pay with..");
    }

    //custom intent chooser
    public void bottomDialogAsChooserCustom1(final Intent prototype, String[] desiredApp) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(prototype, 0);
        EvalInstalledAppInfo evalInstalledAppInfo;
        final ArrayList<EvalInstalledAppInfo> evalInstalledAppInfoArrayList = new ArrayList<>();
        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (Arrays.asList(desiredApp).contains(resolveInfo.activityInfo.packageName)) {

                    ApkInfoUtil apkInfoUtil = new ApkInfoUtil(context);
                    Drawable appIcon = apkInfoUtil.getAppIconByPackageName(resolveInfo.activityInfo.packageName);

                    if(String.valueOf(resolveInfo.activityInfo.loadLabel(context.getPackageManager())).toLowerCase().equals("UPI In-app payments".toLowerCase()))
                    {
                        evalInstalledAppInfo = new EvalInstalledAppInfo(appIcon,
                                resolveInfo.activityInfo.name, resolveInfo.activityInfo.packageName,
                                "Google Pay");
                    }else
                    {
                        evalInstalledAppInfo = new EvalInstalledAppInfo(appIcon,
                                resolveInfo.activityInfo.name, resolveInfo.activityInfo.packageName,
                                String.valueOf(resolveInfo.activityInfo.loadLabel(context.getPackageManager())));
                    }
                    evalInstalledAppInfoArrayList.add(evalInstalledAppInfo);
                }
            }

            if (!evalInstalledAppInfoArrayList.isEmpty()) {
                // sorting for nice readability
                Collections.sort(evalInstalledAppInfoArrayList, new Comparator<EvalInstalledAppInfo>() {
                    @Override
                    public int compare(EvalInstalledAppInfo map, EvalInstalledAppInfo map2) {
                        return map.getSimpleName().compareTo(map2.getSimpleName());
                    }
                });

            }else
            {
                return;
            }
        }else
        {
            Toast.makeText(context, "There is no UPI app in your mobile.", Toast.LENGTH_SHORT).show();
        }

        View view = getLayoutInflater().inflate(R.layout.custom_chooser_main_layout, null);

        final BottomSheetDialog dialog = new BottomSheetDialog(context);

        //dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box_bg);

        dialog.setContentView(view);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        GridView customChooserMainGv = (GridView) view.findViewById(R.id.chooserGv);
        TextView noData = (TextView) view.findViewById(R.id.noDataTextView);
        Button cancelDialog = (Button) view.findViewById(R.id.cancel);

        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if(!evalInstalledAppInfoArrayList.isEmpty())
        {
            noData.setVisibility(View.GONE);
            customChooserMainGv.setVisibility(View.VISIBLE);

            CustomChooserAdapter customChooserAdapter = new CustomChooserAdapter(context);
            customChooserAdapter.setDataAdapter(evalInstalledAppInfoArrayList, new ListEvent() {
                @Override
                public void onLongClick(int index) {

                }

                @Override
                public void onClick(final int index) {

                    if(evalInstalledAppInfoArrayList.get(index).getSimpleName().toLowerCase().equals("UPI In-app payments".toLowerCase()))
                    {
                        appName = "Google Pay";
                    }else
                    {
                        appName = evalInstalledAppInfoArrayList.get(index).getSimpleName();
                    }

                    if(evalInstalledAppInfoArrayList.get(index).getSimpleName().toLowerCase().equals("Paytm".toLowerCase()))
                    {
                        startActivity(context.getPackageManager().getLaunchIntentForPackage(evalInstalledAppInfoArrayList.get(index).getPackageName()));

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.d(TAG, "run: "+"in 5 second");
                                Intent targetedShareIntent = (Intent) prototype.clone();
                                targetedShareIntent.setPackage(evalInstalledAppInfoArrayList.get(index).getPackageName());
                                targetedShareIntent.setClassName(evalInstalledAppInfoArrayList.get(index).getPackageName(),
                                        evalInstalledAppInfoArrayList.get(index).getClassName());
                                ((Activity)context).startActivityForResult(targetedShareIntent, Constants.PAY_REQ_CODE);
                                //FgtPaymentNdOffers.this.startActivityForResult(targetedShareIntent, ActivityCode.PAY_REQ_CODE);
                            }
                        },1800);
                    }else {
                        Intent targetedShareIntent = (Intent) prototype.clone();
                        targetedShareIntent.setPackage(evalInstalledAppInfoArrayList.get(index).getPackageName());
                        targetedShareIntent.setClassName(evalInstalledAppInfoArrayList.get(index).getPackageName(),
                                evalInstalledAppInfoArrayList.get(index).getClassName());
                        ((Activity) context).startActivityForResult(targetedShareIntent, Constants.PAY_REQ_CODE);
                        //FgtPaymentNdOffers.this.startActivityForResult(targetedShareIntent, ActivityCode.PAY_REQ_CODE);
                    }
                    dialog.dismiss();
                }
            });
            customChooserMainGv.setAdapter(customChooserAdapter);

        }else
        {
            noData.setVisibility(View.VISIBLE);
            customChooserMainGv.setVisibility(View.GONE);
        }

    }


}
