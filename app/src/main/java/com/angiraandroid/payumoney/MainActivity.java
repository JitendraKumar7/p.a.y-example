package com.angiraandroid.payumoney;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private synchronized void init() {

        params.put(PayUMoney_Constants.KEY, "merchant_key>");
        params.put(PayUMoney_Constants.TXN_ID, "transaction_it");
        params.put(PayUMoney_Constants.AMOUNT, "amount");
        params.put(PayUMoney_Constants.PRODUCT_INFO, "product_info");
        params.put(PayUMoney_Constants.FIRST_NAME, "first_name");
        params.put(PayUMoney_Constants.EMAIL, "email");
        params.put(PayUMoney_Constants.PHONE, "phone_number");
        params.put(PayUMoney_Constants.SURL, "success_url");
        params.put(PayUMoney_Constants.FURL, "failure_url");
        params.put(PayUMoney_Constants.UDF1, "");
        params.put(PayUMoney_Constants.UDF2, "");
        params.put(PayUMoney_Constants.UDF3, "");
        params.put(PayUMoney_Constants.UDF4, "");
        params.put(PayUMoney_Constants.UDF5, "");

        String hash = Utils.generateHash(params, "salt");

        params.put(PayUMoney_Constants.HASH, hash);
        params.put(PayUMoney_Constants.SERVICE_PROVIDER, "payu_paisa");

    }

    private HashMap<String, String> params = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        findViewById(R.id.paymentSDK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PayUmoneySdkInitializer.PaymentParam.Builder builder = new
                        PayUmoneySdkInitializer.PaymentParam.Builder();
                builder.setAmount("")                          // Payment amount
                        .setTxnId("")                                             // Transaction ID
                        .setPhone("")                                           // User Phone number
                        .setProductName("")                   // Product Name or description
                        .setFirstName("")                              // User First name
                        .setEmail("")                                            // User Email ID
                        .setsUrl("")                    // Success URL (surl)
                        .setfUrl("")                     //Failure URL (furl)
                        .setUdf1("")
                        .setUdf2("")
                        .setUdf3("")
                        .setUdf4("")
                        .setUdf5("")
                        .setUdf6("")
                        .setUdf7("")
                        .setUdf8("")
                        .setUdf9("")
                        .setUdf10("")
                        .setIsDebug(true)                              // Integration environment - true (Debug)/ false(Production)
                        .setKey("enter merchant key")                        // Merchant key
                        .setMerchantId("enter merchant ID");             // Merchant ID

                try {
                    init();
                    PayUmoneySdkInitializer.PaymentParam paymentParam = builder.build();
                    String hash = Utils.generateHash(params, "salt");
                    paymentParam.setMerchantHash(hash);


                    PayUmoneyFlowManager.startPayUMoneyFlow(paymentParam,
                            MainActivity.this, R.style.AppTheme_default, true);

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        findViewById(R.id.paymentSDK).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                init();

                Intent intent = new Intent(MainActivity.this, MakePaymentActivity.class);
                intent.putExtra(PayUMoney_Constants.ENVIRONMENT, PayUMoney_Constants.ENV_DEV);
                intent.putExtra(PayUMoney_Constants.PARAMS, params);

                startActivityForResult(intent, PayUMoney_Constants.PAYMENT_REQUEST);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUMoney_Constants.PAYMENT_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Payment Success,", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Payment Failed | Cancelled.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);

            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {

                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    //Success Transaction
                } else {
                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

            } else {
                Log.d("MainActivity", "Both objects are null!");
            }
        }
    }
}
