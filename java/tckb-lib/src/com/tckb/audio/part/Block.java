/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.part;

import com.tckb.audio.ui.WvConstant;

/**
 *
 * @author ctungathur mport static com.lia.core.wave.WvConstant.RED_SAMPLE_256;
 *
 * /**
 */
public class Block {

    private int redCnt;
    private Reduction[] redList;

    public Block(int size) {


        redList = new Reduction[getNext(size, WvConstant.RED_SAMPLE_256)];
        redCnt = 0;


    }

    public Reduction get(int i) {
        if (i >= 0 && i < redList.length) {
            return redList[i];
        } else {
            return null;
        }


    }

    public boolean put(Reduction r) {
        //System.out.println("id="+this.hashCode()+" redCnt="+redCnt+" MaxRed="+redList.length);
        if (redCnt >= redList.length) {
            return false;
        } else {
            //System.out.println("Red cnt : ");
            redList[redCnt++] = r;
            return true;
        }

    }

    public int redCount() {
        if (redCnt > redList.length) {
            return redList.length;
        } else {
            return redCnt;
        }


    }

    public int getSize() {
        return redCnt * WvConstant.RED_SAMPLE_256;
    }

    private int getNext(int a, int b) {
        if (a % b != 0) {
            return (a / b) + 1;
        } else {
            return (a / b);
        }

    }

    @Override
    public String toString() {
        return "[id=" + hashCode() + " RedCount =" + redCnt + " RedSize=" + WvConstant.RED_SAMPLE_256 + "]";
    }

    public static class Reduction {

        private double max;
        private double min;
        private double rms;
        private int id;

        public Reduction(double max, double min) {
            this.max = max;
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public void setRMS(double rms) {
            this.rms = rms;
        }

        public double getRMS() {
            return rms;
        }

        @Override
        public String toString() {
            return "[max=" + max + " :min=" + min + ":rms=" + rms + "]";
        }
    }
}
