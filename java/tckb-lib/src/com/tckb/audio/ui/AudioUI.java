/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.audio.ui;

import com.tckb.audio.AudProcessor;
import com.tckb.audio.NonTrivialAudio;
import com.tckb.audio.part.Block;
import com.tckb.util.Utility;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;

/**
 *
 * @author tckb
 */
public class AudioUI extends Observable {
    private static final Logger mylogger = Logger.getLogger("com.lia.ui");

    private SourceObserver defaultObserver = null;
    private boolean manualSeek = false;

    public AudioUI() {
        defaultObserver = new SourceObserver();
        addObserver(defaultObserver);
    }

    public void setAudioFile(File src) {

        setChanged();
        this.notifyObservers(src);

    }

    public void setAutoPlay(boolean b) {
        defaultObserver.setAutoPlay(b);
    }

    public void setUIPause(JButton aud_pause_but) {

        defaultObserver.addUIPause(aud_pause_but);
    }

    public void setUIPlay(JButton aud_play_but) {

        defaultObserver.addUIPlay(aud_play_but);
    }

    public void setUISeeker(JSlider aud_seeker_slid) {

        defaultObserver.addSeeker(aud_seeker_slid);
    }

    public void setUISeeker(JProgressBar aud_seeker_slid) {
        defaultObserver.addSeeker(aud_seeker_slid);
    }

    public void setUIWvContainer(JScrollPane aContainer) {
        defaultObserver.addContainer(aContainer);
    }

    public boolean getStatusOK() {
        return this.defaultObserver.getStatus();
    }

    public void zoomIn(int zlevel) {
        defaultObserver.zoomWave(true, zlevel);
    }

    public void zoomOut(int zlevel) {
        defaultObserver.zoomWave(false, zlevel);
    }

    // TODO: temporary
    /**
     * Attach the transcript file generated from the aligner
     *
     * @param transcriptFile
     */
    public void attachTranscriptFile(File transcriptFile) {
        defaultObserver.attachTrList(Utility.getTrList(transcriptFile));

    }

    public void attachTranscriptContainer(JTextComponent trContainer) {
        defaultObserver.attachTrContainer(trContainer);
    }

    public void attachTranscriptHighLightContainer(JTextComponent trContainer) {
        defaultObserver.attachHighTrContainer(trContainer);
    }

    public void zoomIn() {
        defaultObserver.zoomWave(true, ++defaultObserver.zLevel);
    }

    public void zoomOut() {
        defaultObserver.zoomWave(false, defaultObserver.zLevel--);
    }

    public void attachTranscriptPlay(JButton trans_align_but) {
        defaultObserver.attachTrPlayButton(trans_align_but);

    }

    private class SourceObserver implements Observer {

        private JSlider seeker;
        private boolean autoPlay = false;
        private boolean statusOK;
        private JProgressBar seeker2;
        private NonTrivialAudio audio = null;
        private Block[] waveData = null;
        private JScrollPane waveformContainer = null;
        private double hres = 0.05;
        private JTextComponent trContainer = null;
        private boolean isTranscriptAvailable = false;
        private HashMap<Double, String> trList = null;
        private JTextComponent trHighContainer = null;
        private int zLevel = 1;
        private int displayWidth;
        private int displayHeight;
        private AudWvPanel audioPanel = null;
        private ArrayList<Double> timeStamps = null;
        private boolean audioPlaying = false;

        @Override
        public void update(Observable o, Object obj) {

            mylogger.info("Sound source changed, Adjusting the controls");


            if (obj instanceof File) {
                try {
                    // stop any playing audio 
                    this.stopCurrentPlay();


                    //create new instance 

                    File src = (File) obj;
                    displayWidth = (int) waveformContainer.getSize().getWidth();
                    displayHeight = (int) waveformContainer.getSize().getHeight();
                    audio = new NonTrivialAudio(src);
                    double durationMS = audio.getDurationInMS();


                    isTranscriptAvailable = false;

                    //System.out.println("displaying wave form");

                    //TODO: Develop AudioProcessor.getZoomLevels(audio) and use that here
                    // intially display the complete wave


                    waveData = AudProcessor.processAudio(audio);
                    audioPanel = (AudWvPanel) AudProcessor.getWavePanel(waveData, displayWidth, displayHeight);
                    waveformContainer.setViewportView(audioPanel);


                    //  zoomWave(true, 1);
                    // Reset the seeker if, defined
                    resetSeekersMS(durationMS);

                    mylogger.log(Level.INFO, "Sound Clip duration: {0}", durationMS);


                    if (autoPlay) {
                        mylogger.fine("Playing audio");

                        startPlaying();
                    }
                    this.statusOK = true;

                } catch (IOException ex) {
                    mylogger.log(Level.SEVERE, null, ex);
                } catch (UnsupportedAudioFileException ex) {
                    mylogger.log(Level.SEVERE, null, ex);
                } catch (LineUnavailableException ex) {
                    mylogger.log(Level.SEVERE, null, ex);
                }

            } else {
                mylogger.warning("Source not an instance of file");
            }

        }

        private void addUIPlay(JButton aud_play_but) {
            aud_play_but.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    startPlaying();
                }
            });


        }

        private void addUIPause(JButton aud_pause_but) {

            aud_pause_but.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    pauseCurrentPlay();
                }
            });


        }

        private void addSeeker(JSlider aud_seeker_slid) {
            aud_seeker_slid.addMouseListener(new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    // when mouse pres you know his intensions is to seekSecond so, enable manual seekSecond!
                    manualSeek = true; // this will pause the seekSecond updater!
                    // pauseCurrentPlay();

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    int currPos = ((JSlider) e.getSource()).getValue();
                    mylogger.log(Level.FINE, "Mouse released: Current Position: {0}", currPos);
                    seekPlay(currPos);
                    manualSeek = false;

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            this.seeker = aud_seeker_slid;
        }

        private void addSeeker(JProgressBar aud_seeker_slid) {
            this.seeker2 = aud_seeker_slid;

        }

        private void addContainer(JScrollPane aContainer) {
            this.waveformContainer = aContainer;
        }

        private void pauseCurrentPlay() {

            if (audio != null) {
                audio.pause();
            }


        }

        private void stopCurrentPlay() {

            if (audio != null) {
                audio.stop();
                audioPlaying = false;
            }


        }

        private void seekPlay(double currPos) {
            if (audio != null) {
                audio.seekMS(currPos); // audio seek automatically adjusts the play
            }

        }

        private void startPlaying() {
            audioPlaying = true;
            // play the audio
            audio.start();


            // Update seekers and transcript ( if available ) 
            new Thread("seeker-updater") {

                int tsCnt = -1;
                //TODO: This assumes that the timestamps start from 0.0!
                double currTS = 0.0;

                @Override
                public void run() {
                    // Empty the container first
                    trContainer.setText("");



                    while (audio != null && audioPlaying) {
                        try {
                            // update only when there is no manaul override
                            if (!manualSeek) {
                                // System.out.println("update thread: ..good to go!");
                                updateSeekerMS((int) audio.getCurrentMS());

                                if (isTranscriptAvailable) {
                                    highlightTranscript(audio.getCurrentMS());
                                }


                            } else {
                                // System.out.println("update thread: I hate manual interactions! pausing...");
                            }
                            //TODO: CPU usuage!!
                            Thread.sleep(1);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(AudioUI.class.getName()).log(Level.SEVERE, null, ex);
                        }



                    }
                }

                private void highlightTranscript(double sec) {
                    //System.out.println("Current Audio : " + audio.getCurrentMS());

                    // if we exhausted the transcript then return
                    if (tsCnt < timeStamps.size() - 1) {
                        //  System.out.println("CurrMs:" + sec);

                        // TODO: Use threshold
                        double trThresh = 50;

                        if (trList != null) {
                            try {
                                String trWord = getPossibleHits(sec, trThresh);


                                if (trWord != null) {


                                    trContainer.setText(trContainer.getText() + " " + trWord);

                                    int trStartIx = Utility.searchInTxComp(trContainer, trWord);

                                    if (trStartIx >= 0) {
                                        //System.out.println(trWord);
                                        int trEndIx = trWord.length();
                                        // System.out.println("found at " + trStartIx);
                                        //  mylogger.log(Level.INFO, " Hit: {0},{1}", new Object[]{sec, trWord});
                                        //  trContainer.getHighlighter().removeAllHighlights();
                                        trHighContainer.getHighlighter().addHighlight(trStartIx - 1, trStartIx + trEndIx, DefaultHighlighter.DefaultPainter);
                                    }

                                }

                            } catch (BadLocationException ex) {
                                Logger.getLogger(AudioUI.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }


                    } else {
                    }


                }

                private String getPossibleHits(double currMs, final double trThresh) {

                    // System.out.println("Diff: " + (Math.abs(currMs - currTS)));

                    String word = null;

                    // apply threshold to find possible hits

                    if (Math.abs(currMs - currTS) <= trThresh || Math.abs(currMs - currTS) == 0) {

                        currTS = getNextTimeStamp();
                        //  System.out.println("currTS: " + currTS);
                        word = getNextWord();
                        // System.out.println("word: " + word);

                    }



                    return word;

                }

                private String getNextWord() {
                    return trList.get(timeStamps.get(tsCnt));
                }

                private double getNextTimeStamp() {

                    return timeStamps.get(++tsCnt);

                }
            }.start();

        }

        private void setAutoPlay(boolean b) {
            this.autoPlay = b;
        }

        private boolean getStatus() {
            return this.statusOK;
        }

        private void updateSeekerMS(int val) {
            // System.out.println("am updating");
            if (seeker != null) {
                seeker.setValue(val);
            }
            if (seeker2 != null) {
                seeker2.setValue(val);
                seeker2.setString(new Integer(seeker2.getValue()).toString() + " ms");
                seeker2.setToolTipText(null);
                seeker2.setBorderPainted(true);
            }

        }

        private void displayWavePanel(Block[] waveData, int w, int h) {
            mylogger.log(Level.INFO, "Wave size w:{0} h:{1}", new Object[]{w, h});
            JPanel wPanel = AudProcessor.getWavePanel(waveData, w, h);
            wPanel.setPreferredSize(new Dimension(w, h));
            wPanel.setVisible(true);
            waveformContainer.setViewportView(wPanel);


        }

        private void attachTrContainer(JTextComponent trContainer) {
            this.trContainer = trContainer;
        }

        private void attachTrList(HashMap<Double, String> trList) {
            this.isTranscriptAvailable = true;
            this.trList = trList;
            timeStamps = new ArrayList<Double>(trList.keySet());
            Collections.sort(timeStamps);


        }

        private void attachHighTrContainer(JTextComponent trContainer) {
            this.trHighContainer = trContainer;
        }

        private void zoomWave(boolean zoomIn, int zoom) {
            audioPanel.zoom(zoomIn, zLevel);


        }

        private void resetSeekersMS(double duration) {
            // Adjust the seeker
            if (seeker != null) {
                seeker.setValue(0);
                seeker.setMinimum(0);
                seeker.setMinorTickSpacing(10 * 1000);  // 10 secs
                seeker.setMajorTickSpacing(60 * 1000); // 60 secs
                seeker.setPaintTicks(true);
                seeker.setMaximum((int) duration);
            }


            if (seeker2 != null) {
                seeker2.setValue(0);
                seeker.setPaintTrack(true);
                seeker.updateUI();
                seeker2.setMinimum(0);
                seeker2.setMaximum((int) duration);
            }
        }

        private void attachTrPlayButton(JButton trans_align_but) {
            trans_align_but.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    startPlaying();
                }
            });


        }
    }
}
