/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

        WAVEFORM, SPECTOGRAM
    };

    abstract public int clearAllLabels();

    abstract public ArrayList<Label> getAllLabels();

    abstract public String deleteLabelAt(double pos_sec);

    abstract public int getCrosshairLen();

    abstract public double getMAX_ZOOM();

    abstract public double getMIN_ZOOM();

    abstract public double getZoomLevel();

    abstract public int getZoomStep();

    abstract public void refreshDisplay();

    abstract public void resetZoom();

    abstract public void setCrosshairLen(int crosshairLen);
    
    abstract public void setCrosshairPos(double pos_sec);

    abstract public void setLabelAt(String text, double pos_sec);

    abstract public void setMIN_ZOOM(double MIN_ZOOM);

    /*
     *  In sec's
     */
    abstract public void setZoomLevel(double seconds);

    abstract public void setZoomStep(int level);

    abstract public void updateCrosshairPosition(double pos_sec);

    abstract public void zoomIn();

    abstract public void zoomOut();
    
    abstract public boolean toggleDisplay();
    
    abstract public void setDisplayInfo(String info);
}
