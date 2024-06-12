package com.ahmet.androidserialcommunication.serial;

import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.util.concurrent.Executors;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 6/12/2024
 */


public class SerialIOManager {
    private final SerialInputOutputManager ioManager;

    public SerialIOManager(UsbSerialPort port, SerialListener serialListener) {
        ioManager = new SerialInputOutputManager(port, new SerialInputOutputManager.Listener() {
            @Override
            public void onNewData(byte[] data) {
                serialListener.onDataReceive(data.length + " bytes received\n" + new String(data));
            }

            @Override
            public void onRunError(Exception e) {
                // Handle runtime errors
            }
        });
    }

    public void start() {
        Executors.newSingleThreadExecutor().submit(ioManager);
    }

    public void stop() {
        ioManager.stop();
    }
}