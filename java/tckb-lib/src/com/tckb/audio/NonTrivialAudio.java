/*
 * To change this template, choose Tools | Templates
 * and open the template rawDataStream the editor.
 */
package com.tckb.audio;

import com.tckb.borrowed.elan.WAVHeader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Based on Hack from
 * http://codeidol.com/java/swing/Audio/Play-Non-Trivial-Audio Implementation of
 * javax.sound.sampled.Clip can't handle large audio file
 *
 * @author tckb
 */
public class NonTrivialAudio implements Runnable {

    private static final Logger mylogger = Logger.getLogger("com.tckb.audio");
    private File audSrc;
    private AudioInputStream rawDataStream;
    private SourceDataLine audioLine;
    private WAVHeader header;
    private int frameSize;
    private byte[] empty34kBuffer = new byte[32 * 1024]; // 32k is arbitrary
    private byte[] empty64kBuffer = new byte[64 * 1024]; // 64k is arbitrary
    private byte[] empty128kBuffer = new byte[128 * 1024]; // 128k is arbitrary
    private Thread playThread;
    private boolean playing;
    private boolean notYetEOF;
    private int totalFrames = -1;
    private boolean playSafe = false; // for maintaining synchronisation
    private long bytesToSkip = -1;
    private int totalBytesread = -1; // no of bytes read at while running 
    private double noOfRuns;
    private long bytesLeft;

    public NonTrivialAudio(File f)
            throws IOException,
            UnsupportedAudioFileException,
            LineUnavailableException {
        audSrc = f;
        playing = false;
        header = new WAVHeader(f.getAbsolutePath());
        resetStream();

    }

    @Override
    public void run() {
        totalBytesread = 0;

        synchronized (this) {
            mylogger.fine("Play locked!");
            int readPoint = 0;
            int bytesRead = 0;

            try {
                mylogger.info("--Start of Stream--");
                mylogger.log(Level.INFO, "Bytes available: {0}", rawDataStream.available());
                while (notYetEOF) {
                    if (playing) {




                        bytesRead = rawDataStream.read(empty34kBuffer, readPoint, empty34kBuffer.length - readPoint);
                        if (bytesRead == -1) {
                            notYetEOF = false;
                            break;
                        }
                        // how many frames did we get,
                        // and how many are left over?
                        int frames = bytesRead / frameSize;
                        int leftover = bytesRead % frameSize;


                        totalBytesread += bytesRead;


                        mylogger.log(Level.FINER, "Readpoint: {2} frames read: {0} leftover frames: {1} current read: {3} totalbytes read: {4}", new Object[]{frames, leftover, readPoint, bytesRead, totalBytesread});



//                        if (cnt == noOfRuns) {
//                            mylogger.info(" Seek point reached!");
//                            skipbytes = bytesLeft;
//                        }


                        // send to audioLine
                        audioLine.write(empty34kBuffer, readPoint, bytesRead - leftover); // this one here causes sound to produce

                        // save the leftover bytes
                        System.arraycopy(empty34kBuffer, bytesRead, empty34kBuffer, 0, leftover);

                        readPoint = leftover;



                    } //if playing
                    else {
                        // if not playing                   
                        // Thread.yield(); 
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            mylogger.log(Level.SEVERE, "Play interrupted:{0}", ie);

                        }
                    }

                }// while notYetEOF

                mylogger.info("--End of Stream--");
                audioLine.drain();
                audioLine.stop();
                try {
                    int bytesVailable = rawDataStream.available();

                    if (bytesVailable != 0) {
                        // what, there are still bytes left ? - manually stopped?!
                        mylogger.log(Level.INFO, "Manual overrride! audio bytes still available: {0}", bytesVailable);

                        // release the lock!
                        playSafe = true;
                        rawDataStream.close();

                        notifyAll();
                        mylogger.fine("Lock released!");

                    }
                } catch (Exception ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    } // run

    public void start() {

        if (playThread != null) {

            if (!playing) {  // this means that the audio has beeen just paused
                mylogger.fine("Not Playing, paused!");

                playing = true;         // so, turn play to true
                audioLine.start();

            } else {                      // this means that the audio has finsihed playing and user would like to play it again

                mylogger.fine("Other case   ");
                resetStream();                      // so, start a new  instance
                startThread();
            }
        } else {
            // this means that its a fresh start
            // stream is already initialised!  

            startThread();      // so, start a new instance
            mylogger.fine("Thread new");

        }
    }

    public void pause() {
        playing = false;
        audioLine.stop();


    }

    public void stop() {

        // manually set the eof to end playing
        this.notYetEOF = false;

    }

    public SourceDataLine getLine() {

        return audioLine;
    }

    public File getFile() {
        return audSrc;
    }

    public double getCurrentSecond() {
        double seconds = 0.0;
        seconds = audioLine.getFramePosition() / audioLine.getFormat().getFrameRate();
        return seconds;
    }

    public int getCurrentFrame() {
        return audioLine.getFramePosition();
    }

    public double getCurrentMS() {

        return getCurrentSecond() * 1000;
    }

    public double getDurationInSeconds() {
        try {


            return getDurationInFrames() / audioLine.getFormat().getFrameRate();

        } catch (Exception ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        }
        return 0;
    }

    public double getDurationInMS() {

        return getDurationInSeconds() * 1000;

    }

    public boolean isRunning() {
        return notYetEOF;
    }

    public int getDurationInFrames() {
        //Check if its already available
        if (totalFrames == -1) {
            AudioInputStream audioData = null;
            int noOfFrames = 0;
            boolean notYetEOF = true;
            try {

                audioData = AudioSystem.getAudioInputStream(audSrc);

                try {

                    int readPoint = 0;
                    int bytesRead = 0;


                    while (notYetEOF) {

                        bytesRead = audioData.read(empty64kBuffer,
                                readPoint,
                                empty64kBuffer.length - readPoint);
                        if (bytesRead == -1) {
                            notYetEOF = false;
                            break;
                        }

                        // how many frames did we get,
                        // and how many are left over?
                        int frames = bytesRead / frameSize;
                        noOfFrames += frames;

                    }


                    mylogger.log(Level.INFO, "No of frames:{0}", noOfFrames);
                    //reset

                } catch (Exception ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (UnsupportedAudioFileException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                try {
                    audioData.close();




                } catch (IOException ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }
            }



            this.totalFrames = noOfFrames;
        }

        return totalFrames;

    }

    public boolean seekSecond(double second) {

        double length = this.getDurationInSeconds();
        mylogger.log(Level.INFO, "Seeking to second: {0} / {1}", new Object[]{second, length});

        if (second > length) {
            return false;
        } else {
            double framesToBeRead = second * audioLine.getFormat().getFrameRate();
            return seek((int) framesToBeRead);
        }


    }

    public boolean seekMS(double ms) {

        double length = this.getDurationInMS();
        mylogger.log(Level.INFO, "Seeking to ms: {0} / {1}", new Object[]{ms, length});

        if (ms > length) {
            return false;
        } else {
            double framesToBeRead = ms * audioLine.getFormat().getFrameRate();
            return seek((int) (framesToBeRead / 1000));
        }


    }

    public boolean seek(int frames) {

        mylogger.log(Level.INFO, "Seeking to frame: {0} / {1} ", new Object[]{frames, this.getDurationInFrames()});
        mylogger.log(Level.FINE, "Frame difference {0}", (frames - this.getCurrentFrame()));





// Stop the current play if playing
        if (isRunning()) {
            // resets the stream when manually stopped
            mylogger.fine("Stopping current play...");
            this.stop();
        }

// Reset the stream

        // try acquiring the lock!
        synchronized (this) {

            while (!playSafe) {
                try {
                    // hold your horses! :P
                    wait();
                } catch (InterruptedException ex) {
                    mylogger.log(Level.SEVERE, null, ex);
                    return false;
                }
            }


        }
//            Thread curTh = Thread.currentThread();
//            mylogger.info("Current Thread: " + curTh.getName());

        //  frames * framesize = bytesread  
        //  second * framerate  = frames 
        bytesToSkip = (long) (frames * frameSize); // set the bytes to skip to reach that frame


        // reading 32k bytes

        noOfRuns = Math.floor(bytesToSkip / (32 * 1024));
        bytesLeft = (bytesToSkip % (32 * 1024));

        mylogger.log(Level.INFO, "Bytes to skip: {2} no.of times to read: {0} bytes remaining:{1} ", new Object[]{noOfRuns, bytesLeft, bytesToSkip});


        // Now start playing!
        this.start();                              // start skips if there are any bytes available
        return true;


    }

    private void resetStream() {
        mylogger.fine("Resetting audio stream");
        try {
            rawDataStream = AudioSystem.getAudioInputStream(audSrc);
            boolean bytesSkip = false;


            // check if there are any bytes to skip, if yes then skip!

            while (bytesToSkip > 0) {
                try {
                    mylogger.log(Level.INFO, "Bytes to skip: {0}/{1}", new Object[]{bytesToSkip, rawDataStream.available()});

                    long bytesRead = rawDataStream.skip(bytesToSkip);
                    bytesToSkip -= bytesRead;

                } catch (IOException ex) {
                    Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
                }

                bytesSkip = true; // lazy !

            }



            AudioFormat format = rawDataStream.getFormat();
            AudioFormat.Encoding formatEncoding = format.getEncoding();
            if (!(formatEncoding.equals(AudioFormat.Encoding.PCM_SIGNED)
                    || formatEncoding.equals(AudioFormat.Encoding.PCM_UNSIGNED))) {
                throw new UnsupportedAudioFileException(
                        audSrc.getName() + " is not PCM audio");
            }
            mylogger.fine("Audio format:PCM");

            frameSize = format.getFrameSize();
            notYetEOF = true;




            if (!bytesSkip) {
                DataLine.Info info =
                        new DataLine.Info(SourceDataLine.class, format);



                audioLine = (SourceDataLine) AudioSystem.getLine(info);
                mylogger.fine("Opening audio line");
                audioLine.open();

                mylogger.fine("Audio line opened");
            } else {
                audioLine.drain();
                audioLine.stop();
            }


            mylogger.log(Level.INFO, "Samples:  {0} Samples / sec @ {1} bits per sample; Frame size: {2}; Big endian : {3} ", new Object[]{audioLine.getFormat().getSampleRate(), audioLine.getFormat().getSampleSizeInBits(), audioLine.getFormat().getFrameSize(), audioLine.getFormat().isBigEndian()});

        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void startThread() {
        this.playing = true;
        playThread = new Thread(this);
        playThread.setName("audio_" + audSrc.getName() + "_play");
        audioLine.start();
        playThread.start();
    }

    public boolean isAlive() {
        if (rawDataStream == null) {
            return false;
        } else {
            try {
                if (rawDataStream.available() <= 0) {
                    return false;
                } else {
                    return true;


                }
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);


                return false;
            }
        }
    }

    /**
     * Channel numbering starts with 0 ... getNoChannels()-1
     *
     * @param CurrChannel
     * @return
     */
    public int[] getAudioData(int CurrChannel) throws ChannelNotFoundException {
        int[] chData = null;
        AudioInputStream myStream = null;
        try {
            myStream = AudioSystem.getAudioInputStream(audSrc);
            try {
                int sampleIndex = 0;
                int numChannels = myStream.getFormat().getChannels();


                if (CurrChannel >= numChannels) {
                    throw new ChannelNotFoundException();
                }

                int frameLength = (int) myStream.getFrameLength();  // length of stream in-terms of frames

                int frmSze = (int) myStream.getFormat().getFrameSize(); // 2 , 4 ... bytes per frame


                int[][] toReturn = new int[numChannels][frameLength];
                byte[] bytes = new byte[frameLength * frmSze];

                myStream.read(bytes);


                for (int t = 0; t < bytes.length;) {
                    for (int channel = 0; channel < numChannels; channel++) {
                        int low = (int) bytes[t];
                        t++;
                        int high = (int) bytes[t];
                        t++;
                        int sample = getSixteenBitSample(high, low);
                        toReturn[channel][sampleIndex] = sample;
                    }
                    sampleIndex++;
                }

                return toReturn[CurrChannel];
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (UnsupportedAudioFileException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                myStream.close();
            } catch (IOException ex) {
                Logger.getLogger(NonTrivialAudio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private int getSixteenBitSample(int high, int low) {
//        if (rawDataStream.getFormat().isBigEndian()) {
//            return (high << 8) + (low & 0x00ff);
//        } else {
//            return low + high;
//        }
        return (high << 8) + (low & 0x00ff);
    }

    public int getNoChannels() {
        return audioLine.getFormat().getChannels();
    }

    public int getSampleRate() {
        return (int) rawDataStream.getFormat().getSampleRate();
    }

    public WAVHeader getHeader() {
        return header;
    }

    @Override
    public String toString() {
        return header.toString();

    }

    public class ChannelNotFoundException extends Exception {

        @Override
        public String getMessage() {
            return "Invalid Channel specified!";
        }
    }
}
// -- DEAD CODE --
// 
// empty34kBuffer.length - bytes = readpoint
//                        if (skipbytes > 0) {
//                            readPoint = (int) (empty34kBuffer.length - skipbytes);
//                            skipbytes = 0;
//                        }
// -------------------------------------------
