/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.part;

import java.awt.Rectangle;

/**
 *
 * @author tckb
 */
public class Label {

    private String text = "";
    private double sample = 0;
    private Integer currRedix = 0;
    private int x = -1;
    private int horizLen_inPixels = 0;
    private int vertLen_inPixels = 0;
    private int y = -1;

    public Rectangle getBoundingBox() {
        return boundingBox;
    }

    public boolean isOverride() {
        return clicked;
    }

    public void sertOverride(boolean clicked) {
        this.clicked = clicked;
    }
    private Rectangle boundingBox;
    private boolean clicked = false;

    public Label() {
        boundingBox = new Rectangle(0, 0, 0, 0);
    }

    public int getHorizLen_inPixels() {
        return horizLen_inPixels;

    }

    public int getVertLen_inPixels() {
        return vertLen_inPixels;
    }

    public void setVertLen_inPixels(int vertLen_inPixels) {
        this.vertLen_inPixels = vertLen_inPixels;
    }

    public void setHorizLen_inPixels(int horizLen_inPixels) {
        this.horizLen_inPixels = horizLen_inPixels;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getHorzPixel() {
        return x;
    }

    public void setHorzPixel(int pixel) {
        this.x = pixel;
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

    public void createBoudingBox() {
        boundingBox = new Rectangle(x, y, vertLen_inPixels, horizLen_inPixels);
    }

    public void deleteLabelBox() {
        boundingBox = new Rectangle(0, 0, 0, 0);
    }

    public boolean isInsideLabelBox(int x, int y) {

        return boundingBox.contains(x, y);
    }

    public void setVertPixel(int i) {
        this.y = i;

    }

    public int getVertPixel() {
        return y;
    }
}
