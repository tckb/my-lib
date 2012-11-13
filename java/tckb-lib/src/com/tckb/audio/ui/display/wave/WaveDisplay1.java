package com.tckb.audio.ui.display.wave;

import com.tckb.audio.part.Block;
import com.tckb.audio.part.Block.Reduction;
import com.tckb.audio.part.Label;
import com.tckb.audio.ui.display.AudioDisplay1;
import com.tckb.borrowed.jfreechart.ChartColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author tckb
 */
public class WaveDisplay1 extends JPanel implements AudioDisplay1 {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio.ui");
    private final int h = 0;
    private WvConstant params;
    private double winStart_sample = 0;
    private double winEnd_sample;
    private double currPlay_sample;
    private ArrayList< Reduction> cachRed;
    private double windowSize_sample;
    private double windowSize_sec;
    private int zoomStep;
    private double MAX_ZOOM;
    private double MIN_ZOOM;
    private ArrayList<Label> labels = new ArrayList<Label>();
    private int crosshairLen;

    private void setMAX_ZOOM(double level) {
        mylogger.log(Level.INFO, "Settings max zoom: {0}", level);
        this.MAX_ZOOM = level;

    }

    public double getMAX_ZOOM() {
        return MAX_ZOOM;
    }

    public double getMIN_ZOOM() {
        return MIN_ZOOM;
    }

    public final void setMIN_ZOOM(double level) {
        mylogger.log(Level.INFO, "Settings min zoom: {0}", level);

        this.MIN_ZOOM = level;
    }

    public void zoomIn() {
        int step = getZoomStep(); // sec
        mylogger.log(Level.INFO, "Zooming-in {0} level ", step);

        // check if zoomIn available
        if (getZoomLevel() >= getMAX_ZOOM()) {
//            System.out.println("zooming In");

            if ((windowSize_sec - step) >= getMAX_ZOOM()) {
                setZoomLevel(windowSize_sec - step);
            }
//            repaint();
//        }
        } else {
            mylogger.log(Level.WARNING, "Zooming-in level:{0} not available! ", step);

        }
    }

    public void zoomOut() {

        int step = getZoomStep();
        mylogger.log(Level.INFO, "Zooming-out {0} level ", step);

        if (getZoomLevel() <= getMIN_ZOOM()) {
//            System.out.println("zooming out");

//            if ((windowSize_sec - step) >= getMIN_ZOOM()) {
            setZoomLevel(windowSize_sec + step);
//            }
//            repaint();

        } else {
            mylogger.log(Level.WARNING, "Zooming-out {0} level  not avaiable", step);

        }



    }

    public final void setZoomStep(int level) {
        mylogger.log(Level.INFO, "Setting zoom step level:{0} ", level);

        this.zoomStep = level;

    }

    public int getZoomStep() {
        return zoomStep;
    }

    /*
     *  In sec's
     */
    public final void setZoomLevel(double seconds) {
//        if (windowSize_sec <= getMAX_ZOOM()) {
        mylogger.log(Level.INFO, "Setting zoom level :{0} seconds ", seconds);

        this.windowSize_sec = seconds;
        this.windowSize_sample = windowSize_sec * params.SRATE;
        winStart_sample = 0;
        winEnd_sample = (params.DUR_SEC > windowSize_sec) ? windowSize_sample : params.DUR_SEC * params.SRATE;
//        }
//     
        repaint();
    }

    public double getZoomLevel() {
        return windowSize_sec;
    }

    /**
     *
     * @param datab
     * @param wvParams
     */
    public WaveDisplay1(Block[] datab, WvConstant wvParams) {
        mylogger.info("Initializing wave display");

        params = wvParams;

        setDoubleBuffered(true);
        setBackground(ChartColor.LIGHT_GRAY);
        setZoomStep(1);
        setMIN_ZOOM(params.DUR_SEC);
        setMAX_ZOOM(1);
        if (params.DUR_SEC == 0) {
            windowSize_sec = params.DUR_MS;
        } else {
            windowSize_sec = params.DUR_SEC;
        }
        crosshairLen = 15;


    }

    public int getCrosshairLen() {
        return crosshairLen;
    }

    public void setCrosshairLen(int crosshairLen) {
        mylogger.log(Level.INFO, "Setting crosshair length to :{0} pixels ", crosshairLen);


        this.crosshairLen = crosshairLen;
    }

    @Override
    public void paintComponent(final Graphics g) {
        super.paintComponent(g);
        mylogger.fine("Inside paiting component ");
        adjWvParams(getWidth());

        int currPxl = 0;
        int maxPixel = params.PIXEL_COUNT;
        mylogger.log(Level.FINE, "Maximum pixels available: {0}", maxPixel);

        // TODO: Deperecated code!
        if (params.SAMPLE_PER_PIXEL < WvConstant.RED_SAMPLE_256) {
            params.SAMPLE_PER_PIXEL = WvConstant.RED_SAMPLE_256;
        }
        params.RED_PER_PIXEL = round(params.RED_COUNT, params.PIXEL_COUNT);


        displayPixels((Graphics2D) g, currPxl, maxPixel);
    }

    public int round(int x, int y) {

        int d = x / y;
        int r = x % y;
        //  System.out.println(x + ":" + y + ";" + d + ";" + (r>y/2));
        if (r > (y / 2)) {
            return ++d;
        } else {
            return d;
        }
    }

    private void displayPixels(Graphics2D g, int currPxl, int maxPixel) {
//        g.clearRect(0, 0, getWidth(), getHeight());
        mylogger.log(Level.INFO, "Painting {0} hor-pixels {1} vert-pixels   ", new Object[]{maxPixel, getHeight()});

        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);



        // Stroke definitions
        BasicStroke waveStroke = new BasicStroke(0.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        BasicStroke currPointerStroke = new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);



        // Paint window start 
        g.drawString(Double.toString(adjustDoubleDecimal(winStart_sample / params.SRATE)) + " sec", 1, 10);
        g.setStroke(waveStroke);
        g.draw(new Line2D.Double(1, 14, 1, 18));



        // Paint window End 

        g.drawString(Double.toString(adjustDoubleDecimal(winEnd_sample / params.SRATE)) + " sec", getWidth() - 50, 10);
        g.draw(new Line2D.Double(getWidth() - 2, 14, getWidth() - 2, 18));

        // Paint duration
        g.drawString("Duration: " + Double.toString(adjustDoubleDecimal(params.DUR_SEC)) + " sec", getWidth() / 2, getHeight() - 10);




        int winStart_Red = redNumber(winStart_sample);
        cachRed = new ArrayList< Reduction>();
        int currPlay_Red = 0;
        int mid = interpolate(0, -1, 1, 0, getHeight());


        List<Reduction> currWinRedList = params.wavData.subList(redNumber(winStart_sample), redNumber(winEnd_sample));


        while (currPxl < maxPixel) {





//            g.setColor(ChartColor.LIGHT_BLUE);
            g.setColor(new Color(0x40, 0x45, 0xFF));
            g.setStroke(waveStroke);

//            
            int currWinPxl_redstart = (int) Math.floor((winEnd_sample - winStart_sample) * currPxl / (WvConstant.RED_SAMPLE_256 * maxPixel));
            int currWinPxl_redEnd = (int) Math.floor(((winEnd_sample - winStart_sample) * (currPxl + 1) / (WvConstant.RED_SAMPLE_256 * maxPixel)));


            double tmin = Integer.MAX_VALUE;
            double tmax = Integer.MIN_VALUE;

            for (int i = currWinPxl_redstart; i < currWinPxl_redEnd; i++) {
                tmax = Math.max(tmax, currWinRedList.get(i).getMax());// max(tmax, currWinRedList.get(i).getMax());
                tmin = Math.min(tmin, currWinRedList.get(i).getMin());// min(tmin, currWinRedList.get(i).getMin());
            }


            int currPlay_redix = (redNumber(currPlay_sample) - winStart_Red - 1);


            // interpolate tmin & tmax to the current width and height
            int tmin_adj = interpolate(tmin, -1, 1, 0, getHeight());
            int tmax_adj = interpolate(tmax, -1, 1, 0, getHeight());

            mylogger.log(Level.FINEST, "Interpolating wavepixels old/new: {0}/{1}, {2}/{3}  ", new Object[]{tmin, tmin_adj, tmax, tmax_adj});

            if ((tmax_adj) <= getHeight() / 2) {

                mylogger.log(Level.FINEST, "tmax_adj is safe to render: tmax: {0} <= height/2: {1}   ", new Object[]{tmax_adj, getHeight() / 2});



                // Save the adjested reductions
                cachRed.add(new Reduction(tmax_adj, tmin_adj));




                mylogger.finest("Draw 'the' pixel min-max pair");
                g.setColor(ChartColor.BLUE);
                g.draw(new Line2D.Double(currPxl, tmin_adj, currPxl, tmax_adj));



                // save  current sec for painting crosshair
                if (currPlay_redix >= currWinPxl_redstart && currPlay_redix <= currWinPxl_redEnd) {

                    currPlay_Red = currPxl;

                }

                mylogger.finest("Painting available labels");

                for (Label l : labels) {
                    Double sample = l.getSample();
                    int sample_redix = (redNumber(sample) - winStart_Red - 1);
                    if (sample_redix >= currWinPxl_redstart && sample_redix <= currWinPxl_redEnd) {
//                    if (!l.isVisible()) {
                        l.setCurrRedix(currPxl);
                        g.setColor(ChartColor.VERY_LIGHT_GREEN);
                        g.draw(new Line2D.Double(l.getCurrRedix(), 50, l.getCurrRedix(), getHeight() - 50));
                        g.setColor(ChartColor.VERY_LIGHT_YELLOW);

                        g.drawString(l.getText(), l.getCurrRedix() + 5, getHeight() - 50);
                        l.setVisible(true);
//                    }


                    }
                }


                currPxl++;

            }

        }

        // display crosshair
        mylogger.finest("Painting crosshair");

        int p = currPlay_Red - crosshairLen;
        int i = 1;


        while (i <= (2 * crosshairLen) + 1) {
//            System.out.println("p: "+p);
            if ((p + i) > 0 && (p + i) < maxPixel && (p + i) < cachRed.size()) {

                double max = cachRed.get((p + i)).getMax();
                double min = cachRed.get((p + i)).getMin();

                // crosshair-length 
                g.setStroke(waveStroke);
                g.setColor(ChartColor.VERY_DARK_BLUE);
                g.draw(new Line2D.Double(p + i, min, p + i, max));

                // crosshair-top
                g.setColor(ChartColor.VERY_LIGHT_BLUE);
                g.draw(new Line2D.Double(p + i, max - 0.2, p + i, max + 0.2));
                g.draw(new Line2D.Double(p + i, min - 0.2, p + i, min + 0.2));

                // crosshair time
                if ((p + i) == currPlay_Red) {
                    g.setStroke(currPointerStroke);

                    g.setColor(ChartColor.LIGHT_RED);
                    g.draw(new Line2D.Double((p + i), mid + 0.5, (p + i), mid - 0.5));


                    g.drawString(Double.toString(adjustDoubleDecimal(currPlay_sample / params.SRATE)) + " sec", (p + i) - 5, 15);
                    g.setStroke(waveStroke);

                    g.draw(new Line2D.Double((p + i), 18, (p + i), 20));



                }



            }
            i++;
        }


    }

    public void adjWvParams(int displayWidth) {
        mylogger.info("Adjusting wave constants");

        params.PIXEL_COUNT = displayWidth;
        params.TIME_PER_PIXEL = params.DUR_SEC / params.PIXEL_COUNT;
        params.TIME_PER_RED = params.DUR_SEC / WvConstant.RED_SAMPLE_256;  // pos_sample per say, 256 samples
        params.TIME_PER_SAMPLE = params.DUR_SEC / params.ADJ_SAMPLE_COUNT; // use the adjusted value instead of original pos_sample count
        params.SAMPLE_PER_PIXEL = params.ADJ_SAMPLE_COUNT / params.PIXEL_COUNT;



        mylogger.log(Level.FINE, "Adjusted Sample count={0}", params.ADJ_SAMPLE_COUNT);
        mylogger.log(Level.FINER, "tpp:{0}", params.DUR_SEC / params.PIXEL_COUNT);


    }

    public int interpolate(double oldValue, double oldRangeMin, double oldRangeMax, double newRangeMin, double newRangeMax) {
        mylogger.log(Level.FINEST, "Interpolating {0} [ {1}, {2} ] -> [ {3}, {4}] ", new Object[]{oldValue, oldValue, oldRangeMax, newRangeMin, newRangeMax});

        int scale = (int) Math.round((newRangeMax - newRangeMin) / (oldRangeMax - oldRangeMin));
        int newValue = (int) Math.round(newRangeMin + (oldValue - oldRangeMin) * scale);
        return newValue;
    }

    public double max(double... listValues) {
        SimpleMatrix list = new SimpleMatrix(1, listValues.length, true, listValues);
        return CommonOps.elementMax(list.getMatrix());
    }

    private double min(double... listValues) {
        SimpleMatrix list = new SimpleMatrix(1, listValues.length, true, listValues);
        return CommonOps.elementMin(list.getMatrix());
    }

    @Override
    public void updateCrosshairPosition(double pos_sec) {
        mylogger.log(Level.FINER, "Updating crosshair position: {0} sec", pos_sec);
        double pos_sample = pos_sec * params.SRATE;

        if (pos_sample >= winStart_sample && pos_sample < winEnd_sample) {
            mylogger.fine("crosshair position inside current window");


            currPlay_sample = pos_sample;
            revalidate();

            repaint();

        } else {
            mylogger.info("crosshair position outside current window");

            double samples = 0;
            if ((windowSize_sample) > (params.SAMPLE_COUNT - winEnd_sample)) {
                if (params.SAMPLE_COUNT - winEnd_sample > 0) {
                    windowSize_sample = params.SAMPLE_COUNT - winEnd_sample;
                } else {
                    mylogger.info("crosshair position Last window reached");
                    samples = 0;
                }
//                System.out.println("windowSize_sample: " + windowSize_sample);

            } else {
                samples = windowSize_sample;
            }

            winStart_sample += samples;
            winEnd_sample += samples;
            currPlay_sample = pos_sample;

//            System.out.println("p:" + pos_sec + ":s:" + winStart_sample + ":" + winEnd_sample);
            revalidate();
            repaint();
        }



    }

    private int redNumber(double sample) {

//        double s = interpolate(pos_sample, 0, params.DUR_SEC * params.SRATE, winStart_sample, winEnd_sample);


        // double pos_sample = (s > (10 * params.SRATE)) ? (s - (10 * params.SRATE)) : s;
        return ((int) Math.floor(sample / 256));
    }

    public void resetZoom() {
        mylogger.info("Resetting zoom ");
        this.setZoomLevel(getMIN_ZOOM());

    }

    public void setLabelAt(String text, double pos_sec) {
        mylogger.log(Level.INFO, "Setting label: {0} at sec: {1}", new Object[]{text, pos_sec});

        double pos_sample = pos_sec * params.SRATE;

        labels.add(new Label(text, pos_sample));


    }

    public String deleteLabelAt(double pos_sec) {

        double pos_sample = pos_sec * params.SRATE;

        String label = "";
        ArrayList<Label> copy = new ArrayList<Label>(labels);
        for (Label l : copy) {
            if (l.getSample() == pos_sample) {
                label = l.getText();
                labels.remove(l);
            }
        }
        copy = null;
        mylogger.log(Level.INFO, "Deleting label: {0} at sec: {1}", new Object[]{label, pos_sec});

        return label;

    }

    public int clearAllLabels() {
        mylogger.info("Cleaing all labels");
        int nlabels = labels.size();
        labels.clear();
        return nlabels;

    }

    public void refreshDisplay() {
        mylogger.info("Refreshing display");

        repaint();
    }

    public Double getWindowSize_sample() {
        return this.windowSize_sample;
    }

    public double adjustDoubleDecimal(double value) {
        mylogger.log(Level.FINE, "Adjusting valu {0}", new Object[]{value});

        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        //
        DecimalFormat newFormat = new DecimalFormat("#.##", otherSymbols);
        return Double.valueOf(newFormat.format(value));

    }

    @Override
    public ArrayList<Label> getAllLabels() {
        return labels;
    }

    @Override
    public void setCrosshairPos(double pos_sec) {
        mylogger.log(Level.INFO, "Setting crosshair position at : {0} sec", new Object[]{pos_sec});

        double pos_sample = pos_sec * params.SRATE;




        winStart_sample = Math.floor(pos_sample / 2);
        winEnd_sample += windowSize_sample;
    }
}
