/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.ui;

/**
 *
 * @author tckb
 */
public final class WvConstant {
    // Constants

    public static final int BLOCK_16K_SAMPLE = 16 * 1024; // 16k samples[2 bytes]-per-block -> 32KB-per-block
    public static final int BLOCK_32K_SAMPLE = 32 * 1024; // 32k samples[2 bytes]-per-block -> 64KB-per-block
    public static final int RED_SAMPLE_256 = 256;         // 256 samples[2 bytes]-per-reduction -> 512B-per-reduction
    public static final int SAMPLE_SIZE = 16;             // 2 bytes-per-sample
    //  Calculated by calling AudioProcessor.calConstants()
    public int PIXEL_COUNT = -1;
    public int SAMPLE_COUNT = -1;
    public int ADJ_SAMPLE_COUNT = -1;
    public int BLOCK_COUNT = -1;
    public int RED_PER_PIXEL = -1;
    public int SAMPLE_PER_PIXEL = -1;
    public int RED_COUNT = -1;
    public double DUR_SEC = -1.0;
    public double TIME_PER_PIXEL = -1.0;
    public double TIME_PER_RED = -1.0;
    public double TIME_PER_SAMPLE = -1.0;
}
