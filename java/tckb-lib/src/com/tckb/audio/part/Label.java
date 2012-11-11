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
    private int pixel = 0;

    public boolean isIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
    private boolean isVisible = false;

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

    public int getPixel() {
        return pixel;
    }

    public void setPixel(int pixel) {
        this.pixel = pixel;
    }
}