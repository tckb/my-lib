/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.ui;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tckb
 */
public final class WvConstant {
    // Constants

    public static int BLOCK_16K_SAMPLE = 16 * 1024; // 16k samples[2 bytes]-per-block -> 32KB-per-block
    public static int BLOCK_32K_SAMPLE = 32 * 1024; // 32k samples[2 bytes]-per-block -> 64KB-per-block
    public static int RED_SAMPLE_256 = 256;         // 256 samples[2 bytes]-per-reduction -> 512B-per-reduction
    public static int SAMPLE_SIZE = 16;             // 2 bytes-per-sample
   
    //  Calculated by calling AudioProcessor.calConstants()
    public static int PIXEL_COUNT = -1;
    public static int SAMPLE_COUNT = -1;
    public static int ADJ_SAMPLE_COUNT = -1;
    public static int BLOCK_COUNT = -1;
    public static int RED_PER_PIXEL = -1;
    public static int SAMPLE_PER_PIXEL = -1;
    public static int RED_COUNT = -1;
    public static double DUR_SEC = -1.0;
    public static double TIME_PER_PIXEL = -1.0;
    public static double TIME_PER_RED = -1.0;
    public static double TIME_PER_SAMPLE = -1.0;

    @Override
    public String toString() {
        try {
            
            
            System.out.println(this.getClass().getName()+": ");
            
            
            
            for(Field f : getClass().getDeclaredFields()) {
                System.out.println(f.getName()+": "+f.get(f.getType()));
            }
            
            
            

        } catch (IllegalArgumentException ex) {
            Logger.getLogger(WvConstant.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(WvConstant.class.getName()).log(Level.SEVERE, null, ex);
        }



        return "Pixel count:" + PIXEL_COUNT + " Sample count: " + SAMPLE_COUNT + " Block count: " + BLOCK_COUNT;
    }
}
