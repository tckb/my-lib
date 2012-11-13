/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.part;

/**
 *
 * @author tckb
 */
public class Label {

    private String text = "";
    private double sample = 0;
    private Integer currRedix = 0;

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    private boolean visible = false;

    public Label(String text, double sample) {
        this.text = text;
        this.sample = sample;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getSample() {
        return sample;
    }

    public void setSample(double sample) {
        this.sample = sample;
    }

    public Integer getCurrRedix() {
        return currRedix;
    }

    public void setCurrRedix(Integer currRedix) {
        this.currRedix = currRedix;
    }
}