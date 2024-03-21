package com.ahmet.androidserialcommunication;

import android.app.Activity;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;

import androidx.annotation.Nullable;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;
import com.hoho.android.usbserial.util.SerialInputOutputManager;

import java.io.IOException;
import java.util.List;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 2/20/2024
 */


public class SerialDevice {
    private final Activity activity;
    private final SerialListener serialListener;
    private final UsbManager usbManager;
    private UsbSerialPort port;
    private int targetProductID;
    private int baudRate;
    private int dataBits;
    private int stopBits;
    private int parity;

    public static class Builder {
        private final Activity activity;
        private final SerialListener serialListener;
        private int targetProductID = 67;
        private int baudRate = 115200;
        private int dataBits = 8;
        private int stopBits = UsbSerialPort.STOPBITS_1;
        private int parity = UsbSerialPort.PARITY_NONE;

        public Builder(Activity activity, SerialListener serialListener) {
            this.activity = activity;
            this.serialListener = serialListener;
        }

        public Builder targetProductID(int targetProductID) {
            this.targetProductID = targetProductID;
            return this;
        }

        public Builder baudRate(int baudRate) {
            this.baudRate = baudRate;
            return this;
        }

        public Builder dataBits(int dataBits) {
            this.dataBits = dataBits;
            return this;
        }

        public Builder stopBits(int stopBits) {
            this.stopBits = stopBits;
            return this;
        }

        public Builder parity(int parity) {
            this.parity = parity;
            return this;
        }

        public SerialDevice build() {
            return new SerialDevice(this);
        }
    }

    private SerialDevice(Builder builder) {
        this.activity = builder.activity;
        this.serialListener = builder.serialListener;
        this.usbManager = (UsbManager) activity.getSystemService(activity.USB_SERVICE);
        this.targetProductID = builder.targetProductID;
        this.baudRate = builder.baudRate;
        this.dataBits = builder.dataBits;
        this.stopBits = builder.stopBits;
        this.parity = builder.parity;
    }

    public void connect() throws SerialException{
        UsbSerialDriver driver = findDevice();
        if (driver != null) {
            UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
            if (connection != null) {
                List<UsbSerialPort> ports = driver.getPorts();
                if (!ports.isEmpty()) {
                    port = ports.get(0);
                    try {
                        port.open(connection);
                        port.setParameters(baudRate, dataBits, stopBits, parity);
                        startIOManager();
                    } catch (IOException e) {
                        close();
                        throw new SerialException("Error setting up serial port.", e);
                    }
                } else {
                    throw new SerialException("No serial ports found on the device.");
                }
            } else {
                throw new SerialException("Failed to open USB device connection.");
            }
        } else {
            throw new SerialException("Target USB serial driver not found.");
        }
    }

    @Nullable
    private UsbSerialDriver findDevice() {
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(usbManager);
        for (UsbSerialDriver usbSerialDriver : availableDrivers) {
            if (usbSerialDriver.getDevice().getProductId() == targetProductID) {
                serialListener.onDeviceFind(usbSerialDriver);
                return usbSerialDriver;
            }
        }
        return null;
    }

    private void startIOManager() {
        if (port != null) {
            SerialInputOutputManager serialInputOutputManager = new SerialInputOutputManager(port, new SerialInputOutputManager.Listener() {
                @Override
                public void onNewData(byte[] data) {
                    serialListener.onDataReceive(new String(data));
                }

                @Override
                public void onRunError(Exception e) {
                    // Handle runtime errors
                }
            });
            serialInputOutputManager.start();
        }
    }

    public void sendData(String data) throws SerialException{
        if (port != null) {
            try {
                port.write(data.getBytes(), 100);
            } catch (IOException e) {
                throw new SerialException("Error sending data.", e);
            }
        } else {
            throw new SerialException("Serial port is not initialized.");
        }
    }

    public void close() throws SerialException{
        if (port != null) {
            try {
                port.close();
            } catch (IOException e) {
                throw new SerialException("Port Not Closed.");
            } finally {
                port = null;
            }
        }
    }
}