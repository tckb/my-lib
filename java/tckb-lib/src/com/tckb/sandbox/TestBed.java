/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.borrowed.elan.WAVHeader;
import com.tckb.util.Utility;
import java.io.File;
import java.io.FileInputStream;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tckb
 */
public class TestBed {

    public static void main(String[] args) {
        try {
            //        try {
            //            NonTrivialAudio audio = new NonTrivialAudio(new File("/Users/tckb/Msc-MI/Thesis/audio@thesis/orig/audiotest_cut.wav"));
            //
            //            System.out.println("Audio Header information:"+audio.getHeader());
            //            
            //            
            //            
            //            
            //            audio.start();
            //            audio.pause();
            //            audio.stop();
            //            


            File f = Utility.getFileFromUI(null);
            final FileChannel channel = new FileInputStream(f).getChannel();
            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            System.out.println("Buffer size: " + buffer.capacity());
            System.out.println("header size: " + new WAVHeader(f.getAbsolutePath()));




            // when finished
            channel.close();





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





            //        } catch (IOException ex) {
            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //        } catch (UnsupportedAudioFileException ex) {
            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //        } catch (LineUnavailableException ex) {
            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //        }
            //        
        } catch (Exception ex) {
            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
        }





    }
}
