/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio;

import com.tckb.audio.part.Block;
import com.tckb.audio.part.Block.Reduction;
import com.tckb.audio.ui.AudWvPanel;
import com.tckb.audio.ui.WvConstant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
/**
 *
 * @author tckb
 */
public class AudProcessor {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    private static int srate;
    private static int lastBlockSampleCount;
    private static int lastBlockRedCount;
    private static int fullBlocks;

    // this assumes that the size of each sample is 16 bits
    public static Block[] processAudio(NonTrivialAudio audio) {//, int displayWidth) {

        // Independent Variables: Constants
        int[] origDataSamples = audio.getAudioData(0); // get the first channel
        WvConstant.SAMPLE_COUNT = origDataSamples.length;


        srate = audio.getSampleRate();
        //       lastBlockSampleCount = WvConstant.SAMPLE_COUNT % WvConstant.BLOCK_16K_SAMPLE;

        lastBlockSampleCount = WvConstant.SAMPLE_COUNT % WvConstant.BLOCK_16K_SAMPLE;
        lastBlockRedCount = lastBlockSampleCount / WvConstant.RED_SAMPLE_256; // discard the remaining samples!
        fullBlocks = WvConstant.SAMPLE_COUNT / WvConstant.BLOCK_16K_SAMPLE;
        WvConstant.BLOCK_COUNT = (lastBlockSampleCount != 0) ? (fullBlocks + 1) : fullBlocks;

        mylogger.log(Level.FINE, "Sample count={0}", WvConstant.SAMPLE_COUNT);
        mylogger.log(Level.FINE, "Last block sample count={0}", lastBlockSampleCount);
        mylogger.log(Level.FINE, "Number of full blocks={0}", fullBlocks);
        mylogger.log(Level.FINE, "Last block red count={0}", lastBlockRedCount);

// Split the audio into blocks 

        mylogger.log(Level.FINE, "No of blocks : {0}", WvConstant.BLOCK_COUNT);
        Block bList[] = new Block[WvConstant.BLOCK_COUNT]; // 0 -> blockcount-1
        Block emptyBlock;
        int bCnt = 0;
        Reduction cRed = null;

        for (int s = 0; bCnt < WvConstant.BLOCK_COUNT; s += WvConstant.BLOCK_16K_SAMPLE) {
            // mylogger.info("Filling block:" + bCnt );

            int sampleCount = (bCnt < WvConstant.BLOCK_COUNT - 1) ? WvConstant.BLOCK_16K_SAMPLE : lastBlockRedCount * WvConstant.RED_SAMPLE_256;
            //  mylogger.info("Sample count: " + sampleCount);

            emptyBlock = new Block(sampleCount);

            for (int k = 0; k < sampleCount; k += WvConstant.RED_SAMPLE_256) {
                //     mylogger.info(sampleCount%RED_SAMPLE_256);

                cRed = computeReduction(origDataSamples, s + k, WvConstant.RED_SAMPLE_256);
                if (!emptyBlock.put(cRed)) {
                    mylogger.warning("Cann't add anymore!");
                    break;
                }
            }
            bList[bCnt++] = emptyBlock;
            

        }

        // Now, request for garbage collection
        System.gc();
        
        return bList;

    }

    private static Reduction computeReduction(int[] origDataSamples, int pos, int rSize) {
//        Dataset data2 = new DefaultDataset();
//        for (int j = 0; j < rSize; j++) {
//
//            double[] value = new double[]{(double) origDataSamples[pos + j]};
//            Instance instance1 = new DenseInstance(value);
//            data2.add(instance1);
//            // mylogger.info(data2);
//        }

//               Normalize the samples: Performance overhead?
//            NormalizeMidrange nmr = new NormalizeMidrange(0,4);
//            nmr.filter(data2);
//
//        Instance maxI = DatasetTools.maxAttributes(data2);
//        Instance minI = DatasetTools.minAttributes(data2);



        ArrayList<Double> data = new ArrayList<Double>();

        for (int j = 0; j < rSize; j++) {

            double value = (double) origDataSamples[pos + j];

            data.add(value);
            // mylogger.info(data2);
        }



        double max = Collections.max(data);
        double min = Collections.min(data);



//        Vector2d normRed = new Vector2d(min, max);
//        normRed.normalize();
//        // System.out.println(normRed.x+":"+normRed.y);
//        return new Reduction(normRed.x, normRed.y);


        return new Reduction(min, max);
//        return new Reduction(minI.value(0), maxI.value(0));

    }

    //TODO: Fix this!
    public static JPanel getWavePanel(NonTrivialAudio audio, int w, int h) {
        Block bList[] = processAudio(audio);
        // calConstants(w);
        return new AudWvPanel(bList, w, h);
    }

    public static JPanel getWavePanel(Block[] bList, int w, int h) {
        //calConstants(w);
        return new AudWvPanel(bList, w, h);
    }

    public static void calConstants(int displayWidth) {
        mylogger.info("Calculating constants...");
        WvConstant.ADJ_SAMPLE_COUNT = (fullBlocks * WvConstant.BLOCK_16K_SAMPLE) + lastBlockRedCount * WvConstant.RED_SAMPLE_256; // adjusting the sample count
        WvConstant.PIXEL_COUNT = displayWidth;
        WvConstant.DUR_SEC = WvConstant.SAMPLE_COUNT / srate;
        WvConstant.TIME_PER_PIXEL = WvConstant.DUR_SEC / WvConstant.PIXEL_COUNT;
        WvConstant.TIME_PER_RED = WvConstant.DUR_SEC / WvConstant.RED_SAMPLE_256;  // time per say, 256 samples
        WvConstant.TIME_PER_SAMPLE = WvConstant.DUR_SEC / WvConstant.ADJ_SAMPLE_COUNT; // use the adjusted value instead of original sample count
        WvConstant.SAMPLE_PER_PIXEL = WvConstant.ADJ_SAMPLE_COUNT / WvConstant.PIXEL_COUNT;
        WvConstant.RED_COUNT = fullBlocks * (WvConstant.BLOCK_16K_SAMPLE / WvConstant.RED_SAMPLE_256) + lastBlockRedCount;



        mylogger.log(Level.FINE, "Adjusted Sample count={0}", WvConstant.ADJ_SAMPLE_COUNT);
        mylogger.log(Level.FINER, "tpp:{0}", WvConstant.DUR_SEC / WvConstant.PIXEL_COUNT);


    }

    private AudProcessor() {
    }
}

