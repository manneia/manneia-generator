package com.manneia.cli.pattern;

/**
 * @author lkx
 */
public class Device {

    private final String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOff() {
        System.out.println(name + "关灯");
    }

    public void turnOn() {
        System.out.println(name + "开灯");
    }
}
