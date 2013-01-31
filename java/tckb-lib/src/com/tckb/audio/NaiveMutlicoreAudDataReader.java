/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio;

import java.nio.ByteBuffer;
import java.util.SortedMap;
import java.util.concurrent.RecursiveTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tckb
 */
public final class NaiveMutlicoreAudDataReader extends RecursiveTask<SortedMap<Integer, Double[]>> {

    private int CHUNK_THRESHOLD; // global constants 
    private ByteBuffer buffer; // audio buffer
    private int sampleSize;
    private int frameSize;
    private int chunk_size;
    private int chToRead;
    private int noOfCh;
    private int availableChunks;
    private int totalFramesInBuffer;
    private int start_frame, end_frame, center_frame;
    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    /**
     * <b>CAUTION:<br/> This map MUST be EXTERNALLY synchronized!</b> <br/> <i>
     * <br/> USE: SortedMap m = Collections.synchronizedSortedMap(new
     * TreeMap(...));</i>
     *
     *
     */
    private SortedMap<Integer, Double[]> chunkData;

    public NaiveMutlicoreAudDataReader(ByteBuffer buffer, int frameSize, int sampleSize, int chunk_size, int noOfChannels, SortedMap<Integer, Double[]> output) {
        this.buffer = buffer;
        this.sampleSize = sampleSize;
        this.frameSize = frameSize;
        this.noOfCh = noOfChannels;
        this.chunk_size = chunk_size;
        this.chunkData = output;
        this.chToRead = 1; // by default read the first channel
        this.start_frame = getFrameNr(buffer.position());
        this.end_frame = getFrameNr(buffer.capacity());
        this.center_frame = (start_frame + end_frame) >> 1; // equal to Math.floor
        this.totalFramesInBuffer = getNoOfFramesInBuffer(buffer);
        this.availableChunks = getNoOfChunksInBuffer(buffer);
        setCHUNK_THRESHOLD(50);



//       System.out.println("No of chunks:"+availableChunks);

        mylogger.log(Level.INFO, "{3} Data: C: {4} [  {0} - {1} -  {2} ] ", new Object[]{start_frame, center_frame, end_frame, Thread.currentThread().getName(), getNoOfChunksInBuffer(buffer)});

        mylogger.log(Level.FINE, "{0} Total number of chunks: {1}", new Object[]{Thread.currentThread().getName(), getNoOfChunksInBuffer(buffer)});
        mylogger.log(Level.FINE, "{0} totalFramesInBuffer: {1}", new Object[]{Thread.currentThread().getName(), totalFramesInBuffer});

    }

    public void setReaderToAudioChannel(int channel) {
        this.chToRead = channel;
    }

    public int getCHUNK_THRESHOLD() {
        return CHUNK_THRESHOLD;
    }

    public void setCHUNK_THRESHOLD(int CHUNK_THRESHOLD) {
        mylogger.log(Level.FINE, "{0} Setting Threshold: {1}", new Object[]{Thread.currentThread().getName(), CHUNK_THRESHOLD});
        this.CHUNK_THRESHOLD = CHUNK_THRESHOLD;
    }

    /**
     * Reads a chunk of frames of size 'chunk_size'
     *
     * @return data
     */
    private Double[] readAudioChunk(int framesToRead) {

        int framesAvailable = getNoOfFramesInBuffer(buffer);
        int actualFramesToRead = (framesToRead <= framesAvailable) ? framesToRead : framesAvailable;
//        System.out.println(Thread.currentThread().getName() + " Total frames avilable: " + framesAvailable + " Frames requested: " + framesToRead + " frames read: " + actualFramesToRead);
        int start = 0 + (chToRead - 1) * sampleSize;
        int skip = ((noOfCh - chToRead) * sampleSize);
//        System.out.println("Start byte: " + start + " Skipping bytes: " + skip);
        Double[] data = new Double[actualFramesToRead];
        int i = 0;
// works for only 2-bytes-sample
        for (int t = start;
                t < buffer.capacity();
                t += skip) {

            int low = buffer.get(t);
            int high = buffer.get(t + 1);
            int sample16bit = getSixteenBitSample(high, low);

            double sample = sample16bit / Math.pow(2, 15);

            data[i++] = sample; // check if works?

            if (i >= actualFramesToRead) {
                break;
            }


        }
        return data;
    }

    private int getSixteenBitSample(int high, int low) {
        return (high << 8) + (low & 0x00ff);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    protected SortedMap<Integer, Double[]> compute() {


        // Check if the available chunks exceeds threshold
        if (availableChunks <= CHUNK_THRESHOLD) {  // less-than case should not happen in theory

            mylogger.log(Level.INFO, "{0}  availableChunks <= CHUNK_THRESHOLD ; COMPUTING...", Thread.currentThread().getName());


            for (int f = start_frame; f < (start_frame + totalFramesInBuffer); f += chunk_size) {
//                System.out.println("Reading chunks");
//                System.out.println(Thread.currentThread().getName() + "Frame nr position: " + buffer.position());
                int pos = f * frameSize;
                buffer.position(pos);
                // read in chunks
                Double[] data = readAudioChunk(chunk_size);
                Integer chunkNr = getChunkNr_buf(pos);
                chunkData.put(chunkNr, data);

                
                
                System.out.println("processing...");
                
                
            }






        } else {

//            System.out.println(Thread.currentThread().getName() + " dividing");

            NaiveMutlicoreAudDataReader leftAudDataReader, rightAudDataReader;
            // Applying Divide - and - conquer rule
            // 'fork' half of the chunks to left 
            // clone the existing buffer
            ByteBuffer copyLeftOfBuffer = buffer.duplicate();
            ByteBuffer copyRightOfBuffer = buffer.duplicate();

            int leftStart_frame, leftEnd_frame;
            int rightStart_frame, rightEnd_frame;

            leftStart_frame = start_frame;
            leftEnd_frame = center_frame;

            rightStart_frame = center_frame + 1;
            rightEnd_frame = end_frame;




            // setup the position
            copyLeftOfBuffer.position(buffer.position());
            copyLeftOfBuffer.limit(getBufPos(leftEnd_frame)); // in-theory, endChunkLtFrameNr should be less than its size

            copyLeftOfBuffer = copyLeftOfBuffer.slice();
            // create a 'fork'

            leftAudDataReader = new NaiveMutlicoreAudDataReader(copyLeftOfBuffer, frameSize, sampleSize, chunk_size, noOfCh, chunkData);
//            System.out.println(Thread.currentThread().getName() + " left has:" + leftAudDataReader.availableChunks);



            leftAudDataReader.setReaderToAudioChannel(chToRead);

            leftAudDataReader.fork();



            copyRightOfBuffer.position(getBufPos(rightStart_frame));
            copyRightOfBuffer.limit(buffer.capacity());

            rightAudDataReader = new NaiveMutlicoreAudDataReader(copyRightOfBuffer, frameSize, sampleSize, chunk_size, noOfCh, chunkData);
            rightAudDataReader.setReaderToAudioChannel(chToRead);

            mylogger.log(Level.INFO, "{2} Splitting left:  C: {3} [  {0} - {1} ] ", new Object[]{leftStart_frame, leftEnd_frame, Thread.currentThread().getName(), getNoOfChunksInBuffer(copyLeftOfBuffer)});
            mylogger.log(Level.INFO, "{2} Splitting right: C: {3}  [  {0} - {1} ] ", new Object[]{rightStart_frame, rightEnd_frame, Thread.currentThread().getName(), getNoOfChunksInBuffer(copyRightOfBuffer)});





            SortedMap<Integer, Double[]> map1 = rightAudDataReader.compute();
            SortedMap<Integer, Double[]> map2 = leftAudDataReader.join();
//rightAudDataReader.fork();

            chunkData.putAll(map1);

            chunkData.putAll(map2);


            // join the result
            // return the result


            // discard the exisiting buffer
            buffer.clear();






//            invokeAll(leftAudDataReader, rightAudDataReader);
//            System.out.println(Thread.currentThread().getName() + " finished");





        }
        System.out.println(Thread.currentThread().getName() + " :" + chunkData.keySet() + ":" + chunkData.size());

        return chunkData;
    }

    public int getFrameNr(int buf_pos) {
        return (buf_pos / frameSize) + 1;
    }

    public int getFrameNr_chunk(int chunkNr) {
        return (chunkNr - 1) * chunk_size;
    }

    public int getChunkNr(int frameNr) {
        return (frameNr / chunk_size) + 1;
    }

    public int getChunkNr_buf(int buf_pos) {
        return (getFrameNr(buf_pos) / chunk_size) + 1;
    }

    public int getBufPos(int frameNr) {
        return (frameNr - 1) * frameSize;
    }

    public int getBufPos_chunk(int chunkNr) {
        return (getFrameNr(chunkNr) - 1) * frameSize;
    }

    public int getEffectiveBufferCapacity(ByteBuffer buffer) {
        return (buffer.capacity() - buffer.position());
    }

    public int getNoOfFramesInBuffer(ByteBuffer buffer) {
        return (int) Math.ceil((double) getEffectiveBufferCapacity(buffer) / frameSize);

    }

    public int getNoOfChunksInBuffer(ByteBuffer buffer) {
        return (int) Math.ceil((double) getNoOfFramesInBuffer(buffer) / chunk_size);
    }
}
