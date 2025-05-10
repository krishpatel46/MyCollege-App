package com.example.mycollege.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import com.example.mycollege.databinding.ActivityGenerateQrcodeBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class GenerateQRCodeActivity extends AppCompatActivity {

    private ActivityGenerateQrcodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGenerateQrcodeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.submitBtn.setOnClickListener(view -> {
            String qrText = binding.qrText.getText().toString().trim();

            MultiFormatWriter formatWriter = new MultiFormatWriter();
            try {
                BitMatrix matrix = formatWriter.encode(qrText, BarcodeFormat.QR_CODE, 700, 700);

                //create a bitmap to generate QR code image
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap qrBitmap = barcodeEncoder.createBitmap(matrix);

                binding.qrCodeImage.setImageBitmap(qrBitmap);

                //method to auto hide software keyboard
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(binding.qrText.getApplicationWindowToken(), 0);

            } catch (WriterException e) {
                Log.d("writer exception", e.getMessage());
            }

        });


        binding.cancelBtn.setOnClickListener(view -> {
            startActivity(new Intent(GenerateQRCodeActivity.this, AdminDashboardActivity.class));
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(GenerateQRCodeActivity.this, AdminDashboardActivity.class));
        finish();
    }
}