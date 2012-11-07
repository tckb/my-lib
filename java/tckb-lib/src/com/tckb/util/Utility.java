/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.util;

import java.io.*;
import java.nio.channels.FileChannel;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

/**
 *
 * @author tckb
 */
public class Utility {

    // Public constants
    public final static String WORD_BREAK = " ";
    public final static String LINE_BREAK = System.getProperty("line.separator");
    public final static String FILE_SEPERATOR = System.getProperty("path.separator");
    public final static String OS_NAME = System.getProperty("os.name");
    public final static String OS_ARC = System.getProperty("os.arch");
    public final static String J_CLSPTH = System.getProperty("java.class.path");
    public final static String USER_HME = System.getProperty("user.home");
    public final static String USER_DIR = System.getProperty("user.dir");
    private static final Logger mylogger = Logger.getLogger("com.lia.core");

    private static void copy12(File file1, File file2) {
        try {
            mylogger.log(Level.INFO, "Copy12:[{0}]->[{1}]{ ...", new Object[]{file1.getAbsolutePath(), file2.getAbsolutePath()});

            FileChannel in = (new FileInputStream(file1)).getChannel();
            FileChannel out = (new FileOutputStream(file2)).getChannel();
            in.transferTo(0, file1.length(), out);
            in.close();
            out.close();
            mylogger.info("... done}");
        } catch (Exception ex) {
            mylogger.log(Level.SEVERE, "Error while copying: ", ex);

        }

    }

    /**
     *
     * @param fromFile
     * @param toFile
     * @return
     */
    public static boolean copyToFolderAs(File fromFile, File folder, String asName) {
        mylogger.log(Level.INFO, "Copying:[{0}] file to folder [{1}]", new Object[]{fromFile.getAbsolutePath(), folder.getAbsolutePath()});

        if (!folder.isDirectory()) {
            mylogger.log(Level.SEVERE, "Folder {0} is not really a folder! COPY FAILED", new Object[]{folder.getAbsolutePath()});

            return false;
        }
        if (!fromFile.exists()) {
            mylogger.log(Level.SEVERE, "File {0} is doesn't exists! COPY FAILED", new Object[]{fromFile.getAbsolutePath()});

            return false;
        } else {
            try {
                File newFile = new File(folder.getAbsolutePath() + FILE_SEPERATOR + asName);
                if (!newFile.createNewFile()) {
                    mylogger.log(Level.SEVERE, "File {0} creation failed!", new Object[]{newFile.getAbsolutePath()});

                    return false;
                } else {
                    copy12(fromFile, newFile);
                    mylogger.info("Copy completed");
                    return true;
                }



            } catch (IOException ex) {
            mylogger.log(Level.SEVERE, "Something went wrong; error while copying: ", ex);
            }
        }
        return false;


    }

    /**
     *
     * @param fileToCopy
     * @param folder
     * @return
     */
    public static boolean copyToFolder(File fileToCopy, File folder) {

        return copyToFolderAs(fileToCopy, folder, fileToCopy.getName());
    }

    public static File makeDuplicate(File thisFile) {
        copyToFolder(thisFile, thisFile.getParentFile());
        return null;
        
    }

    public static String getSafePath(File file) {
        return "\"" + file.getPath() + "\"";
    }

    public static String getSafeName(File file) {
        return file.getName().trim().replace(".", "_");
    }

    // Apparently, fast load text file ? "readin" function in original text
    // http://users.cs.cf.ac.uk/O.F.Rana/jdc/swing-nov7-01.txt
    synchronized public static void loadFileToPane(String fname, JTextComponent pane) {
        FileReader fr = null;
        try {
            fr = new FileReader(fname);
            pane.read(fr, null);
            fr.close();
        } catch (IOException ex) {
            mylogger.log(Level.SEVERE, "Error while loading: ", ex);
        } finally {
            try {
                fr.close();
            } catch (IOException ex) {
                mylogger.log(Level.SEVERE, "Error while loading: ", ex);
            }
        }

    }

    synchronized public static void loadTextToFile(String text, File fname) {
        try {
            mylogger.log(Level.INFO, "Loading: [{0}] ->file: [{1}]", new Object[]{text, fname.getAbsolutePath()});

            FileWriter fr = new FileWriter(fname);
            fr.write(text);

            fr.close();
        } catch (IOException ex) {
            mylogger.log(Level.SEVERE, "Error while loading: ", ex);
        }

    }

    synchronized public static void loadTextToPane(String text, JTextComponent pane, boolean append) {

        if (append) {
            ((JTextArea) pane).append(text);
        } else {
            ((JTextArea) pane).setText(text);
        }
    }

    public static String stripExtension(File file) {
        mylogger.log(Level.INFO, "Stripping extension: {0}", file.getName());
        int i = file.getName().indexOf('.');
        String s = file.getName().substring(0, i);
        mylogger.log(Level.INFO, ". location{0}", i);
        mylogger.log(Level.INFO, "name {0}", s);
        return s;
    }

    /**
     * Returns individual words in 'f' as a Array of Strings
     *
     * @param f - file to be read
     * @return Array of Strings
     */
    public static String[] getWordsInFile(File f) {

        return readFileAsString(f).split(WORD_BREAK);
    }

    /**
     * Returns file contents as long string NOTE: line breaks are NOT preserved!
     *
     * @param f - file name
     * @return File contents as string
     */
    public static String readFileAsString(File f) {

        StringBuilder content = new StringBuilder();
        String line = null;
        BufferedReader br = null;
        try {

            mylogger.log(Level.INFO, "Reading: [{0}] File: [{1}]", new Object[]{f.getName(), f.getAbsolutePath()});
            br = new BufferedReader(new FileReader(f));
            while ((line = br.readLine()) != null) {
                content.append(line).append(" ");
            }

        } catch (FileNotFoundException ex) {
            mylogger.log(Level.SEVERE, "Error:", ex);
        } finally {

            try {

                br.close();
            } catch (IOException ex) {
                mylogger.log(Level.SEVERE, "Error:", ex);
            }
            return content.toString();
        }
    }

    public static String readFileAsString(String fname) {


        return readFileAsString(new File(fname));
    }

    /**
     * Returns offset of the given word in src the component
     *
     * @param src source component
     * @param word desired word
     * @return position offset
     */
    public static int searchInTxComp(JTextComponent src, String word) {
        int firstOffset = -1;


        if (word == null || word.isEmpty()) {
            return -1;
        }

        // Look for the word we are given - insensitive searchInTxComp
        String content = null;
        try {
            Document d = src.getDocument();

            content = d.getText(0, d.getLength()).toLowerCase();
        } catch (BadLocationException e) {
            // Cannot happen
            return -1;
        }

        word = word.toLowerCase();
        int lastIndex = 0;
        int wordSize = word.length();




        while ((lastIndex = content.indexOf(word, lastIndex)) != -1) {
            int endIndex = lastIndex + wordSize;

            if (firstOffset == -1) {
                firstOffset = lastIndex;
            }
            lastIndex = endIndex;
        }

        return firstOffset;
    }

    /**
     *
     * @param transcriptFile
     * @return Hashmap of (timestamp, transcriptword) pair
     */
    public static HashMap<Double, String> getTrList(File transcriptFile) {
        HashMap<Double, String> trList = new HashMap<Double, String>();

        String line = null;
        BufferedReader br = null;
        try {

            mylogger.log(Level.INFO, "Parsing transcript file:{0}", new Object[]{transcriptFile.getName()});
            br = new BufferedReader(new FileReader(transcriptFile));
            while ((line = br.readLine()) != null) {

                String parts[] = line.split(" ");

                Double ts = Double.parseDouble(parts[0]);

                String word = parts[1];
                trList.put(ts * 1000, word); // store the timestamps as ms

                //  System.out.println("Contains key:" + trList.containsKey(ts) + ":" + trList.get(ts));
            }
            // System.out.println(trList);
            mylogger.log(Level.INFO, "Parsing done; trlist size:{0}", trList.size());

        } catch (FileNotFoundException ex) {
            mylogger.log(Level.SEVERE, "Error:", ex);
        } finally {

            try {

                br.close();
            } catch (IOException ex) {
                mylogger.log(Level.SEVERE, "Error:", ex);
            }


            return trList;
        }


    }

    public static File openFileUI() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static File getFileFromUI(JComponent parent) {
        JFileChooser jfc = new JFileChooser();
        jfc.showOpenDialog(parent);

        return jfc.getSelectedFile();

    }

    public static File createTmpFile(String pxfx, String sfx) {
        try {
            return File.createTempFile(pxfx, sfx);
        } catch (IOException ex) {
            mylogger.log(Level.SEVERE, "Error: Can not create temp file: " + pxfx + sfx, ex);
            return null;
        }
    }

    private Utility() {
    }

    // Shamefully borrowed from Stackoverflow!
    // http://stackoverflow.com/questions/109383/how-to-sort-a-mapkey-value-on-the-values-in-java
    public static class MapUtils {

        public static int countUniqueValues(Map<String, Integer> map) {
            int cnt = 0;
            ArrayList<Integer> valueList = new ArrayList<Integer>(map.values());
            for (Integer val : valueList) {
                if (val != -Integer.MAX_VALUE) {
                    Collections.replaceAll(valueList, val, -Integer.MAX_VALUE);
                    cnt++;
                }
            }

            return cnt;
        }

        /**
         * Sort a map by it's keys in ascending order.
         *
         * @return new instance of {@link LinkedHashMap} contained sorted
         * entries of supplied map.
         * @author Maxim Veksler
         */
        public static <K, V> LinkedHashMap<K, V> sortMapByKey(final Map<K, V> map) {
            return sortMapByKey(map, SortingOrder.ASCENDING);
        }

        /**
         * Sort a map by it's values in ascending order.
         *
         * @return new instance of {@link LinkedHashMap} contained sorted
         * entries of supplied map.
         * @author Maxim Veksler
         */
        public static <K, V> LinkedHashMap<K, V> sortMapByValue(final Map<K, V> map) {
            return sortMapByValue(map, SortingOrder.ASCENDING);
        }

        /**
         * Sort a map by it's keys.
         *
         * @param sortingOrder {@link SortingOrder} enum specifying requested
         * sorting order.
         * @return new instance of {@link LinkedHashMap} contained sorted
         * entries of supplied map.
         * @author Maxim Veksler
         */
        public static <K, V> LinkedHashMap<K, V> sortMapByKey(final Map<K, V> map, final SortingOrder sortingOrder) {
            Comparator<Map.Entry<K, V>> comparator = new Comparator<Entry<K, V>>() {
                public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                    return comparableCompare(o1.getKey(), o2.getKey(), sortingOrder);
                }
            };

            return sortMap(map, comparator);
        }

        /**
         * Sort a map by it's values.
         *
         * @param sortingOrder {@link SortingOrder} enum specifying requested
         * sorting order.
         * @return new instance of {@link LinkedHashMap} contained sorted
         * entries of supplied map.
         * @author Maxim Veksler
         */
        public static <K, V> LinkedHashMap<K, V> sortMapByValue(final Map<K, V> map, final SortingOrder sortingOrder) {
            Comparator<Map.Entry<K, V>> comparator = new Comparator<Entry<K, V>>() {
                public int compare(Entry<K, V> o1, Entry<K, V> o2) {
                    return comparableCompare(o1.getValue(), o2.getValue(), sortingOrder);
                }
            };

            return sortMap(map, comparator);
        }

        @SuppressWarnings("unchecked")
        private static <T> int comparableCompare(T o1, T o2, SortingOrder sortingOrder) {
            int compare = ((Comparable<T>) o1).compareTo(o2);

            switch (sortingOrder) {
                case ASCENDING:
                    return compare;
                case DESCENDING:
                    return (-1) * compare;
            }

            return 0;
        }

        /**
         * Sort a map by supplied comparator logic.
         *
         * @return new instance of {@link LinkedHashMap} contained sorted
         * entries of supplied map.
         * @author Maxim Veksler
         */
        public static <K, V> LinkedHashMap<K, V> sortMap(final Map<K, V> map, final Comparator<Map.Entry<K, V>> comparator) {
            // Convert the map into a list of key,value pairs.
            List<Map.Entry<K, V>> mapEntries = new LinkedList<Map.Entry<K, V>>(map.entrySet());

            // Sort the converted list according to supplied comparator.
            Collections.sort(mapEntries, comparator);

            // Build a new ordered map, containing the same entries as the old map.  
            LinkedHashMap<K, V> result = new LinkedHashMap<K, V>(map.size() + (map.size() / 20));
            for (Map.Entry<K, V> entry : mapEntries) {
                // We iterate on the mapEntries list which is sorted by the comparator putting new entries into 
                // the targeted result which is a sorted map. 
                result.put(entry.getKey(), entry.getValue());
            }

            return result;
        }

        private MapUtils() {
        }

        /**
         * Sorting order enum, specifying request result sort behavior.
         *
         * @author Maxim Veksler
         *
         */
        public static enum SortingOrder {

            /**
             * Resulting sort will be from smaller to biggest.
             */
            ASCENDING,
            /**
             * Resulting sort will be from biggest to smallest.
             */
            DESCENDING
        }
    }
}
// -DEAD CODE -
//    public static File createOutFile(DIR type, String parent, String child) throws IOException {
//
//        File parentDir = new File((File) curOutDir.get(type), parent);
//        if (parentDir.mkdir()) {
//            File childFile = new File(parentDir, child); // then create child inside "parent"
//            if (childFile.createNewFile()) {
//                return childFile;
//            }
//        } // create directory inside "type"
//
//        return null;
//
//    }
