package com.ahmet.androidserialcommunication;

import android.hardware.usb.UsbDevice;

import com.hoho.android.usbserial.driver.UsbSerialDriver;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 2/20/2024
 */

public interface SerialListener {

    void onDeviceFind(UsbSerialDriver usbSerialDriver);
    void onDataReceive(String data);

}
