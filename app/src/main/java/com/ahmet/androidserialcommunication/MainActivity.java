package com.ahmet.androidserialcommunication;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

public class MainActivity extends AppCompatActivity implements SerialListener{

    private SerialDevice serialDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        serialDevice = new SerialDevice.Builder(this, this)
                .targetProductID(67)
                .baudRate(115200)
                .dataBits(8)
                .stopBits(UsbSerialPort.STOPBITS_1)
                .parity(UsbSerialPort.PARITY_NONE)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            serialDevice.connect();
        }
        catch (SerialException e){
            Toast.makeText(this, "Serial Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDeviceFind(UsbSerialDriver usbSerialDriver) {
        UsbDevice usbDevice = usbSerialDriver.getDevice();
        Toast.makeText(this, "Device Found: " + usbDevice.getProductName() + "   " + usbDevice.getProductId(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataReceive(String data) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "Data Received: " + data, Toast.LENGTH_SHORT).show();
            }
        });


    }
}