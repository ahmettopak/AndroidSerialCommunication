package com.ahmet.androidserialcommunication;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.ahmet.androidserialcommunication.serial.SerialConfig;
import com.ahmet.androidserialcommunication.serial.SerialDevice;
import com.ahmet.androidserialcommunication.serial.SerialException;
import com.ahmet.androidserialcommunication.serial.SerialListener;
import com.ahmet.androidserialcommunication.serial.SerialManager;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SerialListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SerialManager serialManager = new SerialManager(this, this);

        List<UsbSerialDriver> devices = serialManager.findAllDevices();
        for (UsbSerialDriver d : devices) {
            Toast.makeText(this, "Found device: " + d.getDevice().getProductId(), Toast.LENGTH_SHORT).show();
        }

        if (!devices.isEmpty()) {
            try {
                serialManager.connectToDeviceWithRetry(devices.get(0), 5, 2000); // Try to connect 5 times with a 2-second interval between retries
                serialManager.sendData("?J 1");

            } catch (SerialException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                ((TextView)findViewById(R.id.textView)).setText(data + "Data");
            }
        });


    }
}