package com.tckb.audio.ui.display;

import com.tckb.audio.part.Label;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author tckb
 */
public abstract class AudioDisplay extends JPanel {

    protected int timePrecs = 2; // 2 digits after decimal point
    protected boolean displayPixels;
    protected boolean displayLabels;
    protected boolean displayInfo;
    protected boolean displayCrosshair;
    protected boolean displayWinInfo;
    protected boolean showCursor = false;
    protected boolean editingLabel;
    protected int cursor_x, cursor_y;

    /**
     * Type of display
     */
    public static enum TYPE {

        /**
         * Waveform display
         */
        WAVEFORM,
        /**
         * Spectrogram display
         */
        SPECTROGRAM
    };

    /**
     *
     * @return
     */
    abstract public int clearAllLabels();

    /**
     *
     * @return
     */
    abstract public ArrayList<Label> getAllLabels();

    /**
     *
     * @param pos_sec
     * @return
     */
    abstract public String deleteLabelAt(double pos_sec);

    /**
     *
     * @return
     */
    abstract public int getCrosshairLen();

    /**
     *
     * @return
     */
    abstract public double getMAX_ZOOM();

    /**
     *
     * @return
     */
    abstract public double getMIN_ZOOM();

    /**
     *
     * @return
     */
    abstract public double getZoomLevel();

    /**
     *
     * @return
     */
    abstract public int getZoomStep();

    public void refreshDisplay() {
        repaint();
    }

    /**
     *
     */
    abstract public void resetZoom();

    /**
     *
     * @param crosshairLen
     */
    abstract public void setCrosshairLen(int crosshairLen);

    /**
     *
     * @param pos_sec
     */
    abstract public void setCursorPos(double pos_sec);

    /**
     *
     * @param text
     * @param pos_sec
     */
    abstract public void setLabelAt(String text, double pos_sec);

    /**
     *
     * @param MIN_ZOOM
     */
    abstract public void setMIN_ZOOM(double MIN_ZOOM);

    /*
     *  In sec's
     */
    /**
     *
     * @param seconds
     */
    abstract public void setZoomLevel(double seconds);

    /**
     *
     * @param level
     */
    abstract public void setZoomStep(int level);

    /**
     *
     * @param pos_sec
     */
    abstract public void updateCrosshairPosition(double pos_sec);

    /**
     *
     */
    abstract public void zoomIn();

    /**
     *
     */
    abstract public void zoomOut();

    public boolean toggleDisplay() {
        displayPixels = !displayPixels;
        repaint();
        return displayPixels;
    }

    /**
     *
     * @param info
     */
    abstract public void setDisplayInfo(String info);

    public boolean toggleLabels() {
        displayLabels = !displayLabels;
        repaint();
        return displayLabels;
    }

    public boolean toggleInfo() {
        displayInfo = !displayInfo;
        repaint();
        return displayInfo;
    }

    public boolean toggleCrosshair() {
        displayCrosshair = !displayCrosshair;
        repaint();
        return displayCrosshair;
    }

    public boolean toggleWindowInfo() {
        displayWinInfo = !displayWinInfo;
        repaint();
        return displayWinInfo;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    abstract public Label getLabelAtXY(int x, int y);

    /**
     *
     * @param l
     */
    abstract public void highLightLabel(Label l);

    public void setLabelAtXY(Label l, int x, int y) {
        if (editingLabel) {
            l.setOverride(true); // just a flag
            l.setHorzPixel(x);
            l.setVertPixel(y);
            repaint();

        }


    }

    public void showCursorAt(int x, int y) {
        this.cursor_x = x;
        this.cursor_y = y;
        repaint();

    }

    public void showCursor(boolean show) {
        this.showCursor = show;
        repaint();
    }

    /**
     *
     * @param b
     */
    public void editLabels(boolean b) {
        this.editingLabel = b;
        repaint();
    }

    /**
     *
     * @param sampleOfLabel
     */
    abstract public double getLabelTimeStamp(double sampleOfLabel);

    public int getTimePrecision() {
        return timePrecs;
    }

    public void setTimePrecision(int digits) {
        this.timePrecs = digits;

    }

    /**
     *
     * @return currentPanel image
     * @throws AWTException
     */
    public BufferedImage getCurrentFrameImage() throws AWTException {

        BufferedImage thisPanel = new Robot().createScreenCapture(
                new Rectangle(this.getLocationOnScreen().x, this.getLocationOnScreen().y,
                this.getWidth(), this.getHeight()));
        return thisPanel;

    }
}
