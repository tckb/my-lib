/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio;

import com.tckb.audio.NonTrivialAudio.ChannelNotFoundException;
import com.tckb.audio.part.Block;
import com.tckb.audio.part.Block.Reduction;
import com.tckb.audio.ui.display.AudioDisplay;
import com.tckb.audio.ui.display.wave.WaveDisplay;
import com.tckb.audio.ui.display.wave.WvConstant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CommonOps;

/**
 *
 * @author tckb
 */
public class AudProcessor {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    private int srate;
    private int lastBlockSampleCount;
    private int lastBlockRedCount;
    private int fullBlocks;
    private Block[] cachedData;
    private NonTrivialAudio audio;
    private WvConstant wvParams;
    private int channel;
    private double globalMax;

    private AudProcessor(NonTrivialAudio audio, int ch) throws ChannelNotFoundException {
        this.audio = audio;
        wvParams = new WvConstant();
        channel = ch;
        cachedData = processAudio();

    }

    public static AudProcessor createProcessor(NonTrivialAudio audio, int ch) throws ChannelNotFoundException {

        return new AudProcessor(audio, ch);

    }

    // TODO: Clean this mess up!
    // this assumes that the size of each sample is 16 bits
    public final Block[] processAudio() throws ChannelNotFoundException {//, int displayWidth) {

        // Independent Variables: Constants
        int[] origDataSamples = audio.getAudioData_fast(channel); // get the first channel

        DenseMatrix64F audioData = new DenseMatrix64F(1, origDataSamples.length);
        for (int j = 0; j < origDataSamples.length; j++) {
            audioData.set(0, j, (double) origDataSamples[j]);
        }

        globalMax = CommonOps.elementMax(audioData);



//        CommonOps.divide(CommonOps.elementMax(audioData), audioData);


        wvParams.SAMPLE_COUNT = origDataSamples.length;
        calWvParams();

        Block bList[] = new Block[wvParams.BLOCK_COUNT]; // 0 -> blockcount-1
//        Block emptyBlock;
        int bCnt = 0;
        Reduction cRed;

        for (int s = 0; bCnt < wvParams.BLOCK_COUNT; s += WvConstant.BLOCK_16K_SAMPLE) {
            mylogger.log(Level.FINE, "Filling block:{0}", bCnt);

            int sampleCount = (bCnt < wvParams.BLOCK_COUNT - 1) ? WvConstant.BLOCK_16K_SAMPLE : lastBlockRedCount * WvConstant.RED_SAMPLE_256;
            mylogger.log(Level.FINE, "Sample count: {0}", sampleCount);

//            emptyBlock = new Block(sampleCount);

            for (int k = 0; k < sampleCount; k += WvConstant.RED_SAMPLE_256) {
                //     mylogger.info(sampleCount%RED_SAMPLE_256);

//                cRed = computeReduction(normAudData, s + k, WvConstant.RED_SAMPLE_256);
                cRed = computeReduction(audioData, s + k, WvConstant.RED_SAMPLE_256);
                wvParams.wavData.add(cRed);
//
//                if (!emptyBlock.put(cRed)) {
//                    mylogger.warning("Cann't add anymore!");
//                    break;
//                }
            }
//           bList[bCnt++] = emptyBlock;
            bCnt++;

        }
        origDataSamples = null;
        // Now, request for garbage collection
        System.gc();

        return bList;

    }
// TODO: Deprecated method delete

    private Reduction computeReduction(double[] origDataSamples, int pos, int rSize) {
        ArrayList<Double> data = new ArrayList<Double>();

        for (int j = 0; j < rSize; j++) {

            double value = (double) origDataSamples[pos + j];

            data.add(value);
        }

        double max = Collections.max(data);
        double min = Collections.min(data);

        return new Reduction(min, max);

    }

    private Reduction computeReduction(DenseMatrix64F rowData, int colS, int size) {

        DenseMatrix64F extract = CommonOps.extract(rowData, 0, 1, colS, colS + size);
        Double max = CommonOps.elementMax(extract) / globalMax;
        Double min = CommonOps.elementMin(extract) / globalMax;

        return new Reduction(min, max);

    }

    private void calWvParams() {

        srate = audio.getSampleRate();
        wvParams.SRATE = srate;

        lastBlockSampleCount = wvParams.SAMPLE_COUNT % WvConstant.BLOCK_16K_SAMPLE;
        lastBlockRedCount = lastBlockSampleCount / WvConstant.RED_SAMPLE_256; // discard the remaining samples!
        fullBlocks = wvParams.SAMPLE_COUNT / WvConstant.BLOCK_16K_SAMPLE;
        wvParams.BLOCK_COUNT = (lastBlockSampleCount != 0) ? (fullBlocks + 1) : fullBlocks;
        wvParams.ADJ_SAMPLE_COUNT = (fullBlocks * WvConstant.BLOCK_16K_SAMPLE) + lastBlockRedCount * WvConstant.RED_SAMPLE_256; // adjusting the sample count
        wvParams.DUR_MS = audio.getDurationInMS();
        wvParams.DUR_SEC = audio.getDurationInSeconds();
        wvParams.RED_COUNT = fullBlocks * (WvConstant.BLOCK_16K_SAMPLE / WvConstant.RED_SAMPLE_256) + lastBlockRedCount;




        mylogger.log(Level.FINE, "Sample count={0}", wvParams.SAMPLE_COUNT);
        mylogger.log(Level.FINE, "Last block sample count={0}", lastBlockSampleCount);
        mylogger.log(Level.FINE, "Number of full blocks={0}", fullBlocks);
        mylogger.log(Level.FINE, "Last block red count={0}", lastBlockRedCount);
        mylogger.log(Level.FINE, "No of blocks : {0}", wvParams.BLOCK_COUNT);
    }

    //TODO: Fix this!
    public AudioDisplay getWavePanel() {
        WaveDisplay wavePanel = new WaveDisplay(cachedData, wvParams);
        wavePanel.setZoomLevel(wavePanel.getMIN_ZOOM());
        return wavePanel;
    }
}
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
//        Vector2d normRed = new Vector2d(min, max);
//        normRed.normalize();
//        // System.out.println(normRed.x+":"+normRed.y);
//        return new Reduction(normRed.x, normRed.y);
//        return new Reduction(minI.value(0), maxI.value(0));
