/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.audio.NonTrivialAudio.InvalidChannnelException;
import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 *
 * @author tckb
 */
public class TestBed {

    public static void main(String[] args) throws InvalidChannnelException {
//        try {
            //        try {
            //            //        try {
            //            //            NonTrivialAudio audio = new NonTrivialAudio(new File("/Users/tckb/Msc-MI/Thesis/audio@thesis/orig/audiotest_cut.wav"));
            //            //
            //            //            System.out.println("Audio Header information:"+audio.getHeader());
            //            //                public void actionPerformed(ActionEvent ae) {
            //            //                    System.out.println("I'm called");
            //            //                }
            //            //            });
            //            //            t.setDelay(5000);
            //            ////            t.setRepeats(false);
            //            //        
            //            //        
            //
            //
            //            //        try {
            //            //            while(true){
            //            //                
            //            //            }
            //            //            
            //            //        try {
            //            //            NonTrivialAudio audio = new NonTrivialAudio(new File("/Users/tckb/Msc-MI/Thesis/audio@thesis/orig/audiotest_cut.wav"));
            //            //
            //            //            System.out.println("Audio Header information:"+audio.getHeader());
            //            //            
            //            //            
            //            //            
            //            //            
            //            //            audio.start();
            //            //            audio.pause();
            //            //            audio.stop();
            //            //            
            //
            //
            //            //            File f = Utility.getFileFromUI(null);
            //            //            final FileChannel channel = new FileInputStream(f).getChannel();
            //            //            MappedByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
            //            //            System.out.println("Buffer size: " + buffer.capacity());
            //            //            System.out.println("header size: " + new WAVHeader(f.getAbsolutePath()));
            //            //
            //            //
            //            //
            //            //
            //            //            // when finished
            //            //            channel.close();
            //            //
            //
            //
            //
            //
            //            //        File someFile = Utility.getFileFromUI(null);
            //            //        File dupFile = Utility.makeDuplicate(someFile);
            //            //        System.out.println("File Contents: " + Utility.readFileAsString(dupFile));
            //            //        System.out.println("File Contents: " + Utility.readFileAsString(dupFile));
            //
            //            //
            //            //        XYSeries series = new XYSeries("Average Size");
            //            //        series.add(20.0, 10.0);
            //            //        series.add(40.0, 20.0);
            //            //        series.add(70.0, 50.0);
            //            //        XYDataset xyDataset = new XYSeriesCollection(series);
            //            //
            //            //        JFreeChart chart = ChartFactory.createXYAreaChart(null, null, null, xyDataset, PlotOrientation.HORIZONTAL, true, true, true);
            //            //                
            //
            //
            //
            //
            //
            //            //        } catch (IOException ex) {
            //            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //        } catch (UnsupportedAudioFileException ex) {
            //            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //        } catch (LineUnavailableException ex) {
            //            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //        }
            //            //            //        
            //            //        } catch (Exception ex) {
            //            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //            //        }
            //            //            //
            //            //
            ////            File someFile = Utility.getFileFromUI(null);
            //            //
            //            //
            //
            //            Logger.getLogger("com.tckb.audio").setLevel(Level.INFO);
            //
            //            final NonTrivialAudio a = new NonTrivialAudio(Utility.getFileFromUI(null));
            //
            //
            //
            ////            final NonTrivialAudio a = new NonTrivialAudio(new File("/Users/tckb/Projects/AvaTech/TestData/Ata_01_CE_1_13.wav"));
            //
            ////
            ////            System.out.println("Reading fast2");
            ////            long start1 = Utility.tic();
            ////            ArrayList<Double[]> data1 = a.getAudioData_fast2(1);
            ////            double time1 = Utility.toc(start1);
            ////            System.out.println("Time taken: " + time1 + " sec");
            ////            System.out.println(data1.size());
            //
            //
            //
            //
            //
            //
            //            System.out.println("Reading fast3");
            //            long start2 = Utility.tic();
            //            SortedMap<Integer, Double[]> data2 = a.getAudioData_fast3(1);
            //            double time = Utility.toc(start2) * 1000;
            //            System.out.println("Time taken: " + Utility.toFormatedTimeString((int) time) + " sec");
            //            System.out.println(data2.keySet());
            //
            ////            System.out.println(1+14>>1);
            //
            //
            //
            //
            //
            //            ////        
            //            //            new Thread() {
            //            //                @Override
            //            //                public void run() {
            //            //                    try {
            //            //                        System.out.println("Reading fast");
            //            //                        long start = System.nanoTime();
            //            //                        ArrayList<Double[]> data = a.getAudioData_fast2(1);
            //            //                        long end = System.nanoTime();
            //            //                        System.out.println("Time taken: " + (end - start) / Math.pow(10, 9) + " sec");
            //            //                    } catch (InvalidChannnelException ex) {
            //            //                        Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //                    }
            //            //                }
            //            //            }.start();
            //            //
            //            //
            //            //        } catch (IOException ex) {
            //            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //        } catch (UnsupportedAudioFileException ex) {
            //            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //        } catch (LineUnavailableException ex) {
            //            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //            //        }
            //
            //
            //
            //
            //            //        System.out.println(Utility.toFormatedTimeString(1000*60));
            //        } catch (IOException ex) {
            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //        } catch (UnsupportedAudioFileException ex) {
            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //        } catch (LineUnavailableException ex) {
            //            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
            //
            //



            //        BigDecimal bigNumber1 = new BigDecimal(3.333336, MathContext.DECIMAL64);
            //        BigDecimal bigNumber2 = new BigDecimal(3.333333, MathContext.DECIMAL64);
            //
            //
            //
            //        System.out.println(bigNumber1.setScale(2, BigDecimal.ROUND_CEILING));
            //        System.out.println(bigNumber2.setScale(2, BigDecimal.ROUND_CEILING));
//            final int FRAME_RATE = 11;
//            final int SECONDS_TO_RUN_FOR = 2;
//            final Robot robot = new Robot();
//            final Toolkit toolkit = Toolkit.getDefaultToolkit();
//            final Rectangle screenBounds = new Rectangle(toolkit.getScreenSize());
//
//            // First, let's make a IMediaWriter to write the file.
//            final IMediaWriter writer = ToolFactory.makeWriter("output.mp4");
//
//            // We tell it we're going to add one video stream, with id 0,
//            // at position 0, and that it will have a fixed frame rate of
//            // FRAME_RATE.
//            writer.addAudioStream(0, 0, screenBounds.width, screenBounds.height);
//            // Now, we're going to loop
//            long startTime = System.nanoTime();
//            for (int index = 0; index < SECONDS_TO_RUN_FOR * FRAME_RATE; index++) {
//                try {
//                    // take the screen shot
//                    BufferedImage screen = robot.createScreenCapture(screenBounds);
//
//                    // convert to the right image type
//                    BufferedImage bgrScreen = convertToType(screen,
//                            BufferedImage.TYPE_3BYTE_BGR);
//
//                    // encode the image to stream #0
//                    writer.encodeVideo(0, bgrScreen,
//                            System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
//                    System.out.println("encoded image: " + index);
//
//                    // sleep for framerate milliseconds
//                    Thread.sleep((long) (1000 / FRAME_RATE));
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//            // Finally we tell the writer to close and write the trailer if
//            // needed
//            writer.close();
//        } catch (AWTException ex) {
//            Logger.getLogger(TestBed.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
        DoubleFFT_1D fft= new DoubleFFT_1D(10);
        double[] data = {1,2,3,4,5,6,7,8,9,10};
        fft.realForward(data);
        
        
         
        }
    
    
//    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
//
//        BufferedImage image;
//
//        // if the source image is already the target type, return the source image
//        if (sourceImage.getType() == targetType) {
//            image = sourceImage;
//        }
//        // otherwise create a new image of the target type and draw the new image
//        else {
//            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
//            image.getGraphics().drawImage(sourceImage, 0, 0, null);
//        }
//
//        return image;
//
//    }
    
}
