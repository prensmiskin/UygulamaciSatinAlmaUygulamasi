package com.example.uygulamacisatnalma;

import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;

import java.util.List;

public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    private Button btnbenzinSatınalOne,btnbenzinSatınalTwo,btnAboneSatınal,btnSatinAldiklerim;
    private TextView TextOne,TextTwo;
    private BillingClient mBillingClient ;
    private int  benzinMiktari = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnbenzinSatınalOne = findViewById(R.id.btnbenzinSatınalOne);
        btnbenzinSatınalTwo = findViewById(R.id.btnbenzinSatınalTwo);
        btnAboneSatınal = findViewById(R.id.btnAboneSatınal);
        btnSatinAldiklerim = findViewById(R.id.btnSatinAldiklerim);
        TextTwo = findViewById(R.id.TextTwo);
        TextOne = findViewById(R.id.TextOne);
        mBillingClient = BillingClient.newBuilder(this).setListener(this).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int responseCode) {
                if (responseCode == BillingClient.BillingResponse.OK){
                    ButtonlarinDurumuDegistir(true);

                }else{
                    ButtonlarinDurumuDegistir(false);
                    Toast.makeText(getApplicationContext(),"Ödeme Sistemi İçin Google Hesabınızı Kontrol Ediniz",Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                ButtonlarinDurumuDegistir(true);
                Toast.makeText(getApplicationContext(),"Ödeme Sistemi Şuanda Geçerli DEĞİL",Toast.LENGTH_SHORT).show();



            }
        });
                    btnbenzinSatınalOne.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                    .setSku("benzinalone")
                                    .setType(BillingClient.SkuType.INAPP)
                                    .build();
                            mBillingClient.launchBillingFlow(MainActivity.this,flowParams);


                        }
                    });
                    btnbenzinSatınalTwo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                    .setSku("benzinaltwo")
                                    .setType(BillingClient.SkuType.INAPP)
                                    .build();
                            mBillingClient.launchBillingFlow(MainActivity.this,flowParams);

                        }
                    });
                    btnSatinAldiklerim.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.INAPP, new PurchaseHistoryResponseListener() {
                                @Override
                                public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {
                                    if (responseCode == BillingClient.BillingResponse.OK && purchasesList != null ){
                                        StringBuilder sb  = new StringBuilder();
                                        for (final Purchase purchase : purchasesList){
                                            sb.append(purchase.getSku()+"\n");

                                        }
                                        TextTwo.setText(sb.toString());


                                    }

                                }
                            });

                        }
                    });
                    btnAboneSatınal.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                    .setSku("abone6")
                                    .setType(BillingClient.SkuType.SUBS)
                                    .build();
                            mBillingClient.launchBillingFlow(MainActivity.this,flowParams);

                        }
                    });


    }

    @Override
    public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> purchases) {
        if (responseCode == BillingClient.BillingResponse.OK && purchases != null ){
            for (final Purchase purchase : purchases){
                mBillingClient.consumeAsync(purchase.getPurchaseToken(), new ConsumeResponseListener() {
                    @Override
                    public void onConsumeResponse(int responseCode, String purchaseToken) {
                        if (responseCode == BillingClient.BillingResponse.OK){
                           if (purchase.getSku().equals("benzin10")){
                               benzinMiktari += 10;
                           }
                            if (purchase.getSku().equals("benzin20")){
                                benzinMiktari += 20;
                            }
                            Toast.makeText(getApplicationContext()
                                    ,
                                    purchase.getSku()+"Ürün Tüketildi",Toast.LENGTH_SHORT).show();


                        }

                    }
                });
            }


        }
        if (responseCode == BillingClient.BillingResponse.USER_CANCELED){
            Toast.makeText(getApplicationContext()
                    ,"Ödeme İşlemi İptal Edildi",Toast.LENGTH_SHORT).show();

        }

    }
    public  void ButtonlarinDurumuDegistir(boolean durum){
        btnbenzinSatınalOne.setEnabled(durum);
        btnbenzinSatınalTwo.setEnabled(durum);
        btnAboneSatınal.setEnabled(durum);
        btnSatinAldiklerim.setEnabled(durum);

    }
}
