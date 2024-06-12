package com.ahmet.androidserialcommunication.serial;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 6/12/2024
 */

import android.app.Activity;
import android.hardware.usb.UsbManager;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.util.ArrayList;
import java.util.List;

public class SerialManager {
    private final UsbManager usbManager;
    private final SerialListener serialListener;
    private final SerialConfig serialConfig;
    private SerialDevice currentDevice;

    public SerialManager(Activity activity, SerialListener serialListener) {
        this.usbManager = (UsbManager) activity.getSystemService(Activity.USB_SERVICE);
        this.serialListener = serialListener;
        serialConfig = new SerialConfig.Builder()
                .targetProductID(8963)
                .baudRate(115200)
                .dataBits(8)
                .stopBits(UsbSerialPort.STOPBITS_1)
                .parity(UsbSerialPort.PARITY_NONE)
                .build();

    }

    public List<UsbSerialDriver> findAllDevices() {
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        List<UsbSerialDriver> deviceList = new ArrayList<>();
        for (UsbSerialDriver driver : availableDrivers) {
            deviceList.add(driver);
        }
        return deviceList;
    }

    public void connectToDevice(UsbSerialDriver driver) throws SerialException {
        currentDevice = new SerialDevice(usbManager, serialListener, serialConfig);
        currentDevice.connect(driver);
    }

    public void connectToDeviceWithRetry(UsbSerialDriver driver, int retryCount, int retryIntervalMs) throws SerialException {
        currentDevice = new SerialDevice(usbManager, serialListener, serialConfig);
        currentDevice.connectWithRetry(driver, retryCount, retryIntervalMs);
    }

    public void sendData(String data) throws SerialException {
        if (currentDevice != null) {
            currentDevice.sendData(data);
        } else {
            throw new SerialException("No device is currently connected.");
        }
    }

    public void closeCurrentDevice() throws SerialException {
        if (currentDevice != null) {
            currentDevice.close();
            currentDevice = null;
        }
    }
}
