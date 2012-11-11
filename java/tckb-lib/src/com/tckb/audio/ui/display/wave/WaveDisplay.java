package com.tckb.audio.ui.display.wave;

import com.tckb.audio.part.Block;
import com.tckb.audio.part.Block.Reduction;
import com.tckb.audio.part.Label;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.borrowed.jfreechart.ChartColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejml.ops.CommonOps;
import org.ejml.simple.SimpleMatrix;

/**
 *
 * @author tckb
 */
public class WaveDisplay extends AudioDisplay {

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
        this.MAX_ZOOM = level;

    }

    @Override
    public double getMAX_ZOOM() {
        return MAX_ZOOM;
    }

    @Override
    public double getMIN_ZOOM() {
        return MIN_ZOOM;
    }

    @Override
    public final void setMIN_ZOOM(double MIN_ZOOM) {
        this.MIN_ZOOM = MIN_ZOOM;
    }

    @Override
    public void zoomIn() {
        int step = getZoomStep(); // sec
        // check if zoomIn available
        if (getZoomLevel() >= getMAX_ZOOM()) {
//            System.out.println("zooming In");

            if ((windowSize_sec - step) >= getMAX_ZOOM()) {
                setZoomLevel(windowSize_sec - step);
            }
//            repaint();
//        }
        }
    }

    @Override
    public void zoomOut() {

        int step = getZoomStep();
        if (getZoomLevel() <= getMIN_ZOOM()) {
//            System.out.println("zooming out");

//            if ((windowSize_sec - step) >= getMIN_ZOOM()) {
            setZoomLevel(windowSize_sec + step);
//            }
//            repaint();

        }


    }

    @Override
    public final void setZoomStep(int level) {
        this.zoomStep = level;

    }

    @Override
    public int getZoomStep() {
        return zoomStep;
    }

    /*
     *  In sec's
     */
    @Override
    public final void setZoomLevel(double seconds) {
//        if (windowSize_sec <= getMAX_ZOOM()) {

        this.windowSize_sec = seconds;
        this.windowSize_sample = windowSize_sec * params.SRATE;
        winStart_sample = 0;
        winEnd_sample = (params.DUR_SEC > windowSize_sec) ? windowSize_sample : params.DUR_SEC * params.SRATE;
//        }
//     
        repaint();
    }

    @Override
    public double getZoomLevel() {
        return windowSize_sec;
    }

    /**
     *
     * @param datab
     * @param wvParams
     */
    public WaveDisplay(Block[] datab, WvConstant wvParams) {
        params = wvParams;

        setDoubleBuffered(true);
        setBackground(ChartColor.LIGHT_GRAY);
        setZoomStep(1);
        setMIN_ZOOM(params.DUR_SEC);
        setMAX_ZOOM(1);
//        System.out.println("dur: "+params.DUR_MS);
        if (params.DUR_SEC == 0) {
            windowSize_sec = params.DUR_MS;
        } else {
            windowSize_sec = params.DUR_SEC;
        }
        crosshairLen = 15;

// set default level to minimum zoom level
//        setZoomLevel(params.DUR_SEC);


    }

    @Override
    public int getCrosshairLen() {
        return crosshairLen;
    }

    @Override
    public void setCrosshairLen(int crosshairLen) {
        this.crosshairLen = crosshairLen;
    }

    public void paintComponent(final Graphics g) {
        super.paintComponent(g);


        // TODO: Autoresize doesn't work correctly if the zoom step is changed
        // Auto resize of the wave at current zoom step

//        if (zlevel == 1) {
//            currentWidth = getWidth();
//        } else {
//            currentWidth = zoomWidth;
//        }

//        mylogger.log(Level.INFO, "Curr Zoomlevel:{0} Width:{1} ", new Object[]{zlevel, currentWidth});
        adjWvParams(getWidth());
//        
        int currPxl = 0;
//        double adj = 0.0;
        int maxPixel = params.PIXEL_COUNT;
//        
        if (params.SAMPLE_PER_PIXEL < WvConstant.RED_SAMPLE_256) {
            params.SAMPLE_PER_PIXEL = WvConstant.RED_SAMPLE_256;
        }
        params.RED_PER_PIXEL = round(params.RED_COUNT, params.PIXEL_COUNT);
//
//// Adjust the default settings based on the duration
//        if (params.DUR_SEC >= (5 * 60)) {
//            adj = adj_factor_1;
//            showMinMarks = true;
//            show30sMarks = true;
//            show50msMarks = false;
//
//            //displayMinMarks((Graphics2D) g);
//            //displaySecMarks((Graphics2D) g);
//
//            
//        } else if (params.DUR_SEC < (5 * 60) && params.DUR_SEC > (60)) {
//            
//            
//            adj = adj_factor_2;
//            showMinMarks = true;
//            show30sMarks = true;
//            show50msMarks = false;
//            
//        } else {
//            adj = adj_factor_3;
//            showMinMarks = false;
//            show30sMarks = false;
//            show50msMarks = true;
//        }
//        
//        mylogger.log(Level.FINE, "SPP={0} RPP={1}", new Object[]{params.SAMPLE_PER_PIXEL, params.RED_PER_PIXEL});
//        
        displayPixels((Graphics2D) g, currPxl, maxPixel);
//        displayMarks((Graphics2D) g, showMinMarks, show30sMarks, show50msMarks);



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
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);



        // Stroke definitions
        BasicStroke waveStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
        BasicStroke currPointerStroke = new BasicStroke(3f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);




        g.drawString(Double.toString(adjustDoubleDecimal(winStart_sample / params.SRATE)) + " sec", 1, 10);
        g.setStroke(waveStroke);
        g.draw(new Line2D.Double(1, 14, 1, 18));




        g.drawString(Double.toString(adjustDoubleDecimal(winEnd_sample / params.SRATE)) + " sec", getWidth() - 50, 10);
        g.draw(new Line2D.Double(getWidth() - 2, 14, getWidth() - 2, 18));

        g.drawString("Duration: " + Double.toString(adjustDoubleDecimal(params.DUR_SEC)) + " sec", getWidth() / 2, getHeight() - 10);




        int winStart_Red = redNumber(winStart_sample);
        cachRed = new ArrayList< Reduction>();
        int currPlay_Red = 0;
        int mid = interpolate(0, -1, 1, 5, getHeight() - 5);


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
                tmax = max(tmax, currWinRedList.get(i).getMax());
                tmin = min(tmin, currWinRedList.get(i).getMin());
//                System.out.println(i);
            }


            int currPlay_redix = (redNumber(currPlay_sample) - winStart_Red - 1);


            // interpolate tmin & tmax to the current width and height
            int tmin_adj = interpolate(tmin, -1, 1, 5, getHeight() - 5);
            int tmax_adj = interpolate(tmax, -1, 1, 5, getHeight() - 5);






            // Save the adjested reductions
            cachRed.add(new Reduction(tmax_adj, tmin_adj));
//            g.setColor(ChartColor.VERY_DARK_GREEN);
//
//            g.draw(new Line2D.Double(currPxl, tmin_adj, currPxl, tmin_adj));
//            g.draw(new Line2D.Double(currPxl, tmax_adj, currPxl, tmax_adj));




            // draw current pixel
            g.setColor(ChartColor.BLUE);
            g.draw(new Line2D.Double(currPxl, tmin_adj, currPxl, tmax_adj));


            for (Label l : labels) {
                Double sample = l.getSample();
                int sample_redix = (redNumber(sample) - winStart_Red - 1);
                if (sample_redix >= currWinPxl_redstart && sample_redix <= currWinPxl_redEnd) {
//                    if (!l.isIsVisible()) {
                    l.setPixel(currPxl);
                    g.setColor(ChartColor.VERY_LIGHT_GREEN);
                    g.draw(new Line2D.Double(l.getPixel(), 50, l.getPixel(), getHeight() - 50));
                    g.setColor(ChartColor.VERY_LIGHT_YELLOW);

                    g.drawString(l.getText(), l.getPixel() + 5, getHeight() - 50);
                    l.setIsVisible(true);
//                    }


                }
            }

            // draw crosshair pointer for current sec
            if (currPlay_redix >= currWinPxl_redstart && currPlay_redix <= currWinPxl_redEnd) {

                currPlay_Red = currPxl;


//                g.setStroke(currPointerStroke);
//
//                // draw crosshair
////                g.setColor(ChartColor.DARK_BLUE);
////                g.draw(new Line2D.Double(currPxl, tmin_adj, currPxl, tmax_adj));
////                // draw center point
//                g.setColor(ChartColor.WHITE);
//                g.draw(new Line2D.Double(currPxl, mid + 1, currPxl, mid - 1));
//                // draw top & bottom of crosshair
////                g.setColor(ChartColor.LIGHT_YELLOW);
////                g.draw(new Line2D.Double(currPxl, tmin_adj + 0.5, currPxl, tmin_adj - 0.5));
////                g.draw(new Line2D.Double(currPxl, tmax_adj + 0.5, currPxl, tmax_adj - 0.5));
//
//
//
            }


            currPxl++;



        }

        // display crosshair

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

// display label (ifany)
//        for (Label l : labels) {
//
//            if (l.getPixel() != 0) {
//                g.setColor(ChartColor.VERY_LIGHT_GREEN);
//                g.draw(new Line2D.Double(l.getPixel(), 20, l.getPixel(), getHeight() - 50));
//                g.drawString(l.getText(), l.getPixel() + 2, getHeight() - 50);
//            }
//
//
//        }
    }

    private void displayMinMarks(Graphics2D g) {


        //                                      paintstrokesize                                                       {dashsize,spacesize}
        BasicStroke dashedStroke = new BasicStroke(0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{5f, 5f}, 0.0f);
        BasicStroke defaultStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

        int cp = 0;
        int time = 0;
        mylogger.log(Level.FINE, "Time per pixel:{0}", params.TIME_PER_PIXEL);
        // tpp * pixelcount = total duration



        while (cp < params.PIXEL_COUNT) {
            time = (int) (cp * params.TIME_PER_PIXEL);

            if (time > 0 && time % 60.f == 0) {

//                    g.setStroke(waveStroke);
//                    g.setColor(Color.BLACK);
//                    g.drawString("min", cp, h - 20);

                // g.setStroke();

                g.setColor(Color.RED);
                g.setStroke(dashedStroke);
                g.draw(new Line2D.Double(cp, h - 1, cp, 0));

            }
            cp++;

        }

        g.setStroke(defaultStroke);

    }

    // Marks 'min' on the wavepanel
    private void displaySecMarks(Graphics2D g) {


        //                                      paintstrokesize                                                       {dashsize,spacesize}
        BasicStroke dashedStroke = new BasicStroke(0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{5f, 5f}, 0.0f);
        BasicStroke defaultStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

        int cp = 0;
        int time = 0;
        mylogger.log(Level.FINE, "Time per pixel:{0}", params.TIME_PER_PIXEL);
        // tpp * pixelcount = total duration



        while (cp < params.PIXEL_COUNT) {
            time = (int) (cp * params.TIME_PER_PIXEL);

            if (time > 0 && time % (30.f) == 0) {

//                    g.setStroke(waveStroke);
//                    g.setColor(Color.BLACK);
//                    g.drawString("min", cp, h - 20);

                // g.setStroke();

                g.setColor(Color.GREEN);
                g.setStroke(dashedStroke);
                g.draw(new Line2D.Double(cp, h - 1, cp, 0));

            }
            cp++;

        }

        g.setStroke(defaultStroke);

    }

    // Marks 'min' on the wavepanel
    private void displayMarks(Graphics2D g, boolean show60sMark, boolean show30sMark, boolean show50msMark) {


        //                                      paintstrokesize                                                       {dashsize,spacesize}
        BasicStroke dashedStroke = new BasicStroke(0.1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{5f, 5f}, 0.0f);
        BasicStroke defaultStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

        int cp = 0;
        double time = 0;
        mylogger.log(Level.INFO, "Time per pixel:{0}", params.TIME_PER_PIXEL);
        // tpp * pixelcount = total duration

        g.setStroke(dashedStroke);

        while (cp < params.PIXEL_COUNT) {
            time = (cp * params.TIME_PER_PIXEL);

            if ((Math.round(time) > 0 && Math.round(time) % (30.f) == 0) && show30sMark) {

                //  g.setPaint(ChartColor.VERY_LIGHT_RED);
                g.setPaint(Color.RED);

                // System.out.println("chaneg to green");
                g.draw(new Line2D.Double(cp, h + 10, cp, 0));





            }
            if ((Math.round(time) > 0 && Math.round(time) % (60.f) == 0) && show60sMark) {


                //g.setPaint(ChartColor.LIGHT_RED);
                g.setPaint(Color.BLACK);

                // System.out.println("change to red");
                g.draw(new Line2D.Double(cp, h - 1, cp, 0));


            }

            if ((Math.round(time * 1000) > 0 && (Math.round(time * 1000) % 500.f) == 0) && show50msMark) {
                //   System.out.println("pos_sample" + pos_sample * 1000);
                g.setPaint(Color.BLACK);
                // System.out.println("change to black");
                g.draw(new Line2D.Double(cp, h - 1, cp, 0));



            }
            // System.out.println("cu t: " + pos_sample * 1000);
            cp++;

        }

        g.setStroke(new BasicStroke());

    }

    public void adjWvParams(int displayWidth) {
        mylogger.info("Calculating constants...");

        params.PIXEL_COUNT = displayWidth;
        params.TIME_PER_PIXEL = params.DUR_SEC / params.PIXEL_COUNT;
        params.TIME_PER_RED = params.DUR_SEC / WvConstant.RED_SAMPLE_256;  // pos_sample per say, 256 samples
        params.TIME_PER_SAMPLE = params.DUR_SEC / params.ADJ_SAMPLE_COUNT; // use the adjusted value instead of original pos_sample count
        params.SAMPLE_PER_PIXEL = params.ADJ_SAMPLE_COUNT / params.PIXEL_COUNT;



        mylogger.log(Level.FINE, "Adjusted Sample count={0}", params.ADJ_SAMPLE_COUNT);
        mylogger.log(Level.FINER, "tpp:{0}", params.DUR_SEC / params.PIXEL_COUNT);


    }

    public int interpolate(double oldValue, double oldRangeMin, double oldRangeMax, double newRangeMin, double newRangeMax) {
//        System.out.println("old range: " + oldRangeMin + ":" + oldRangeMax);
//        System.out.println("new range: " + newRangeMin + ":" + newRangeMax);



        int scale = (int) Math.round((newRangeMax - newRangeMin) / (oldRangeMax - oldRangeMin));

//        System.out.println("scale: " + scale);

        int newValue = (int) Math.round(newRangeMin + (oldValue - oldRangeMin) * scale);
//         System.out.println("oldvalue: " + oldValue + " new value: " + newValue);

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

        double pos_sample = pos_sec * params.SRATE;

        if (pos_sample >= winStart_sample && pos_sample < winEnd_sample) {

            currPlay_sample = pos_sample;
            repaint();

        } else {
            double samples = 0;
            if ((windowSize_sample) > (params.SAMPLE_COUNT - winEnd_sample)) {
                if (params.SAMPLE_COUNT - winEnd_sample > 0) {
                    windowSize_sample = params.SAMPLE_COUNT - winEnd_sample;
                } else {
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
            repaint();
        }



    }

    private int redNumber(double sample) {

//        double s = interpolate(pos_sample, 0, params.DUR_SEC * params.SRATE, winStart_sample, winEnd_sample);


        // double pos_sample = (s > (10 * params.SRATE)) ? (s - (10 * params.SRATE)) : s;
        return ((int) Math.floor(sample / 256));
    }

    @Override
    public void resetZoom() {

        this.setZoomLevel(getMIN_ZOOM());

    }

    @Override
    public void setLabelAt(String text, double pos_sec) {
        double pos_sample = pos_sec * params.SRATE;

        labels.add(new Label(text, pos_sample));


    }

    @Override
    public String deleteLabelAt(double pos_sec) {
        return null;

    }

    @Override
    public int clearAllLabels() {
        return 0;
    }

    @Override
    public void refreshDisplay() {
        repaint();
    }

    public Double getWindowSize_sample() {
        return this.windowSize_sample;
    }

    public double adjustDoubleDecimal(double value) {
        DecimalFormat newFormat = new DecimalFormat("#.##");
        return Double.valueOf(newFormat.format(value));

    }
}
