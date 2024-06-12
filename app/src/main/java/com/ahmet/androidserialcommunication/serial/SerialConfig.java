package com.ahmet.androidserialcommunication.serial;

import com.hoho.android.usbserial.driver.UsbSerialPort;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 6/12/2024
 */


public class SerialConfig {
    private final int targetProductID;
    private final int baudRate;
    private final int dataBits;
    private final int stopBits;
    private final int parity;

    public SerialConfig(int targetProductID, int baudRate, int dataBits, int stopBits, int parity) {
        this.targetProductID = targetProductID;
        this.baudRate = baudRate;
        this.dataBits = dataBits;
        this.stopBits = stopBits;
        this.parity = parity;
    }

    public int getTargetProductID() {
        return targetProductID;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getDataBits() {
        return dataBits;
    }

    public int getStopBits() {
        return stopBits;
    }

    public int getParity() {
        return parity;
    }

    public static class Builder {
        private int targetProductID = 67;
        private int baudRate = 115200;
        private int dataBits = 8;
        private int stopBits = UsbSerialPort.STOPBITS_1;
        private int parity = UsbSerialPort.PARITY_NONE;

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

        public SerialConfig build() {
            return new SerialConfig(targetProductID, baudRate, dataBits, stopBits, parity);
        }
    }
}