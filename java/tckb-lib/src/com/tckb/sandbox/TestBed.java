/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.audio.NonTrivialAudio;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author tckb
 */
public class TestBed {

    public static void main(String[] args) {
        try {
            NonTrivialAudio audio = new NonTrivialAudio(new File("/Users/tckb/Msc-MI/Thesis/audio@thesis/orig/audiotest_cut.wav"));

            System.out.println(audio);


            audio.start();









            //        File someFile = Utility.getFileFromUI(null);
            //        File dupFile = Utility.makeDuplicate(someFile);
            //        System.out.println("File Contents: " + Utility.readFileAsString(dupFile));
            //        System.out.println("File Contents: " + Utility.readFileAsString(dupFile));

            //
            //        XYSeries series = new XYSeries("Average Size");
            //        series.add(20.0, 10.0);
            //        series.add(40.0, 20.0);
            //        series.add(70.0, 50.0);
            //        XYDataset xyDataset = new XYSeriesCollection(series);
            //
            //        JFreeChart chart = ChartFactory.createXYAreaChart(null, null, null, xyDataset, PlotOrientation.HORIZONTAL, true, true, true);
            //                





        } catch (IOException ex) {
            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
        } catch (LineUnavailableException ex) {
            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
        }






    }
}
