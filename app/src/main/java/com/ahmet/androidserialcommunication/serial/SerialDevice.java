package com.ahmet.androidserialcommunication.serial;

import android.annotation.SuppressLint;
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
    private final UsbManager usbManager;
    private final SerialListener serialListener;
    private final SerialConfig serialConfig;
    private UsbSerialPort port;
    private SerialIOManager serialIOManager;

    public SerialDevice(UsbManager usbManager, SerialListener serialListener, SerialConfig serialConfig) {
        this.usbManager = usbManager;
        this.serialListener = serialListener;
        this.serialConfig = serialConfig;
    }

    public void connect(UsbSerialDriver driver) throws SerialException {
        UsbDeviceConnection connection = usbManager.openDevice(driver.getDevice());
        if (connection != null) {
            List<UsbSerialPort> ports = driver.getPorts();
            if (!ports.isEmpty()) {
                port = ports.get(0);
                try {
                    port.open(connection);
                    port.setParameters(serialConfig.getBaudRate(), serialConfig.getDataBits(), serialConfig.getStopBits(), serialConfig.getParity());
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
    }

    public void connectWithRetry(UsbSerialDriver driver, int retryCount, int retryIntervalMs) throws SerialException {
        int attempt = 0;
        while (attempt < retryCount) {
            try {
                connect(driver);
                return; // Connection successful
            } catch (SerialException e) {
                attempt++;
                if (attempt >= retryCount) {
                    throw e; // Rethrow exception if max attempts reached
                }
                try {
                    Thread.sleep(retryIntervalMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new SerialException("Thread interrupted during retry sleep.", ie);
                }
            }
        }
    }

    private void startIOManager() {
        if (port != null) {
            serialIOManager = new SerialIOManager(port, serialListener);
            serialIOManager.start();
        }
    }

    public void sendData(String data) throws SerialException {
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

    public void close() throws SerialException {
        if (port != null) {
            try {
                port.close();
            } catch (IOException e) {
                throw new SerialException("Port not closed.", e);
            } finally {
                port = null;
                if (serialIOManager != null) {
                    serialIOManager.stop();
                }
            }
        }
    }
}