package com.dingbei.diagnose.bean;

import java.io.Serializable;

/**
 * @author Dayo
 * @time 2020/2/6 11:10
 * @desc 体征数据
 */
public class SignBean implements Serializable {

    private String height; //身高
    private String weight; //体重
    private String temperature; //体温
    private String blood_oxygen; //血氧
    private String blood_pressure; //血压，注意格式是"高压/低压"
    private String blood_sugar; //血糖
    private String pulse_rate; //脉率

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getBlood_oxygen() {
        return blood_oxygen;
    }

    public void setBlood_oxygen(String blood_oxygen) {
        this.blood_oxygen = blood_oxygen;
    }

    public String getBlood_pressure() {
        return blood_pressure;
    }

    public void setBlood_pressure(String blood_pressure) {
        this.blood_pressure = blood_pressure;
    }

    public String getBlood_sugar() {
        return blood_sugar;
    }

    public void setBlood_sugar(String blood_sugar) {
        this.blood_sugar = blood_sugar;
    }

    public String getPulse_rate() {
        return pulse_rate;
    }

    public void setPulse_rate(String pulse_rate) {
        this.pulse_rate = pulse_rate;
    }
}
