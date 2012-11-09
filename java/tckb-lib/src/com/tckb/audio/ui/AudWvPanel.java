/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.ui;

import com.tckb.audio.part.Block;
import com.tckb.borrowed.jfreechart.ChartColor;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 *
 * @author tckb
 */
public class AudWvPanel extends JPanel {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio.ui");
    private final Block[] blocks;
    private double zlevel;
    private final double adj_factor_1 = 0.03;
    private final double adj_factor_2 = 0.002;
    private final double adj_factor_3 = 0.002;
    private boolean showMinMarks = false;
    private boolean show30sMarks = false;
    private boolean show50msMarks = false;
    private int zoomWidth;
    private final int h;
    private int originalWidth;
    private boolean zoomIn = false;
    private int currentWidth;
    private WvConstant params;

    public AudWvPanel(Block[] datab, WvConstant wvParams, int width, int height) {
        blocks = datab;
        params = wvParams;
        zlevel = 1;
        originalWidth = width;
        zoomWidth = originalWidth;
        h = height;
        setBackground(Color.LIGHT_GRAY);


    }

    @Override
    public void paintComponent(final Graphics g) {
        adjustWidth();
        super.paintComponent(g);


        // TODO: Autoresize doesn't work correctly if the zoom level is changed
        // Auto resize of the wave at current zoom level

        if (zlevel == 1) {
            currentWidth = getWidth();
        } else {
            currentWidth = zoomWidth;
        }

        mylogger.log(Level.INFO, "Curr Zoomlevel:{0} Width:{1} ", new Object[]{zlevel, currentWidth});
        adjWvParams(currentWidth);

        int currPxl = 0;
        double adj = 0.0;
        int maxPixel = params.PIXEL_COUNT;

        if (params.SAMPLE_PER_PIXEL < WvConstant.RED_SAMPLE_256) {
            params.SAMPLE_PER_PIXEL = WvConstant.RED_SAMPLE_256;
        }
        params.RED_PER_PIXEL = round(params.RED_COUNT, params.PIXEL_COUNT);

// Adjust the default settings based on the duration
        if (params.DUR_SEC >= (5 * 60)) {
            adj = adj_factor_1;
            showMinMarks = true;
            show30sMarks = true;
            show50msMarks = false;

            //displayMinMarks((Graphics2D) g);
            //displaySecMarks((Graphics2D) g);


        } else if (params.DUR_SEC < (5 * 60) && params.DUR_SEC > (60)) {


            adj = adj_factor_2;
            showMinMarks = true;
            show30sMarks = true;
            show50msMarks = false;

        } else {
            adj = adj_factor_3;
            showMinMarks = false;
            show30sMarks = false;
            show50msMarks = true;
        }

        mylogger.log(Level.FINE, "SPP={0} RPP={1}", new Object[]{params.SAMPLE_PER_PIXEL, params.RED_PER_PIXEL});

        displayPixels((Graphics2D) g, currPxl, maxPixel, adj);
        displayMarks((Graphics2D) g, showMinMarks, show30sMarks, show50msMarks);



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

    private void displayPixels(Graphics2D g, int currPxl, int maxPixel, double yAdj) {
        // Since we are slicing the file into chucks of 
        // Reductions, SPP = RED_SAMPLE_256 =  256, in this case

        int currBlock = -1;
        int currRed = -1;
        int globalRed = -1;
        int redMax = -1;
        double tmin = Integer.MAX_VALUE;
        double tmax = Integer.MIN_VALUE;
        // adjusting factor


         g.setColor(ChartColor.LIGHT_BLUE);
//        g.setColor(Color.BLUE);

        while (currPxl < maxPixel) {

            if (currRed == redMax) {

                // time to go to next block
                if (++currBlock >= params.BLOCK_COUNT) {
                    mylogger.fine("-End of Blocks-");
                    break;
                }
                currRed = 0;
                redMax = blocks[currBlock].redCount();
                mylogger.log(Level.FINEST, "Current block={0} Current pixel={1} Current max red count={2}", new Object[]{currBlock, currPxl, redMax});

            }

            if (blocks[currBlock].get(currRed) == null) {
                mylogger.log(Level.SEVERE, " Something is wrong!  block red is null, block#{0} currentRed= {1} red max: {2}", new Object[]{currBlock, currRed, redMax});
                break;
            }

            mylogger.log(Level.FINE, "Current block:{0}", currBlock);
            tmin = Math.min(tmin, blocks[currBlock].get(currRed).getMin());
            tmax = Math.max(tmax, blocks[currBlock].get(currRed).getMax());
            currRed++;
            globalRed++;
//            mylogger.log(Level.FINEST, "CR:{0};RPP{1};{2}", new Object[]{globalRed, params.RED_PER_PIXEL, globalRed % params.RED_PER_PIXEL});


            if ((globalRed % (params.RED_PER_PIXEL)) == 0) {

                mylogger.log(Level.FINEST, "Plotting...Current pxl: {0} Current red: {1}", new Object[]{currPxl, currRed});

// draw a horizontal line between the min, max pair
//              (-h)  - 
//                    -         X(tmin) 
//              (h/2) - --------+---------------
//                    -          (currPxl)
//                    -         X(tmax)
//              (+h)  -        

                int tmin_adj = interpolate(tmin, -1, 1, 0, getHeight() );
                int tmax_adj = interpolate(tmax, -1, 1, 0, getHeight() );



                // System.out.println(tmin+":"+tmax);
//                 g.draw(new Line2D.Double(currPxl, ((tmin * yAdj) + h / 2), currPxl, ((tmax * yAdj) + h / 2)));
//                   g.draw(new Line2D.Double(currPxl, tmin, currPxl, tmax));
                g.draw(new Line2D.Double(currPxl, tmin_adj, currPxl, tmax_adj));


                tmin = Integer.MAX_VALUE;
                tmax = Integer.MIN_VALUE;
                currPxl++;

            }

        }

    }
// Marks 'min' on the wavepanel

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

//                    g.setStroke(defaultStroke);
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

//                    g.setStroke(defaultStroke);
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
        // BasicStroke defaultStroke = new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

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
                //   System.out.println("time" + time * 1000);
                g.setPaint(Color.BLACK);
                // System.out.println("change to black");
                g.draw(new Line2D.Double(cp, h - 1, cp, 0));



            }
            // System.out.println("cu t: " + time * 1000);
            cp++;

        }

        g.setStroke(new BasicStroke());

    }

    private boolean inRange(double a, double b, double threshold) {






        return false;
    }

    public void zoom(boolean zoomIn, int zoomLevel) {
        mylogger.log(Level.INFO, "ZoomIn: {0} Zoom level:{1}", new Object[]{zoomIn, zoomLevel});
        this.zlevel = zoomLevel;
        this.zoomIn = zoomIn;
        adjustWidth();
        repaint();
        //int h = (int) waveformContainer.getSize().getHeight();
        //System.out.println("zwidth:" + originalWidth + " zoom:" + zoom);
        //displayWavePanel(waveData, originalWidth, h);
    }

    private void adjustWidth() {
        zoomWidth = (int) (zoomIn ? (getWidth() * zlevel) : (currentWidth / zlevel));
    }

    public void adjWvParams(int displayWidth) {
        mylogger.info("Calculating constants...");

        params.PIXEL_COUNT = displayWidth;
        params.TIME_PER_PIXEL = params.DUR_SEC / params.PIXEL_COUNT;
        params.TIME_PER_RED = params.DUR_SEC / WvConstant.RED_SAMPLE_256;  // time per say, 256 samples
        params.TIME_PER_SAMPLE = params.DUR_SEC / params.ADJ_SAMPLE_COUNT; // use the adjusted value instead of original sample count
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
//        System.out.println("oldvalue: " + oldValue + " new value: " + newValue);

        return newValue;
    }
}
