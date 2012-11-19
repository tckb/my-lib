
package com.tckb.audio.ui.display;

import com.tckb.audio.part.Label;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author tckb
 */
public abstract class AudioDisplay extends JPanel {

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

    /**
     *
     */
    abstract public void refreshDisplay();

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
    abstract public void setCrosshairPos(double pos_sec);

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

    /**
     *
     * @return
     */
    abstract public boolean toggleDisplay();

    /**
     *
     * @param info
     */
    abstract public void setDisplayInfo(String info);

    /**
     *
     * @return
     */
    abstract public boolean toggleLabels();

    /**
     *
     * @return
     */
    abstract public boolean toggleInfo();

    /**
     *
     * @return
     */
    abstract public boolean toggleCrosshair();

    /**
     *
     * @return
     */
    abstract public boolean toggleWindowInfo();

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
     * @param x
     * @param y
     */
    abstract public void setLabelAtXY(Label l, int x, int y);

    /**
     *
     * @param l
     */
    abstract public void highLightLabel(Label l);

    /**
     *
     * @param x
     * @param y
     */
    abstract public void showCursorAt(int x, int y);

    /**
     *
     * @param b
     */
    abstract public void showCursor(boolean b);
    
    /**
     *
     * @param b
     */
    abstract public void editLabels(boolean b);
}
