/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.util.cmd;

import com.tckb.util.Utility;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author tckb
 */
public class GenericCmdv2 extends GenericCmd {

    private LinkedHashMap<String, String> flags = new LinkedHashMap<String, String>();

    public GenericCmdv2() {
        super("command");


    }

    public GenericCmdv2(String name) {
        super(name);

    }

    /**
     * Adds a flag ( -flag value ) to the command.<br/> - null value for 'flag'
     * and or 'value' is <b> NOT </b> acceptable. <br/> <br/> For example,
     * <br/><br/>
     * <code>
     *...<br/>
     * // To run "java -version" <br/>
     * command1.setCommand("java"); <br/>
     * command1.setFlag("version","");<br/>
     * command1.runCommand(false);<br/>
     * ...<br/> 
     * // To run "ls /Users/tckb" <br/>
     * command2.setCommand("ls"); <br/>
     * command2.setFlag("","/Users/tckb/"); <br/>
     * command2.runCommand(true); // 'batch' mode of executing command<br/>
     * ...
     * </code>
     *
     * @param flag
     * @param value
     */
    public void addFlag(String flag, String value) {
        if (!flag.isEmpty()) {
            flags.put(flag, value);
        } else {

            flags.put("_$" + Double.toString(new java.util.Random().nextGaussian()), value);

        }

    }

    @Override
    protected void runNormal() {
        myLog.writeln("==============================================");
        try {
            String tmp;

            // Add  main command
            flagList.add(cmd);


            addToFlagList(flags);




            myLog.writeln("Executing command: " + flagList);
            ProcessBuilder processb = new ProcessBuilder(flagList);

            // merge error & output 
            processb.redirectErrorStream(true);
            Process cmdProcess = processb.start();


            myLog.writeln("Output:");
            BufferedReader br = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));

            while ((tmp = br.readLine()) != null) {
                myLog.writeln(tmp);
            }


            int returnVal = cmdProcess.waitFor();
            myLog.writeln("Command executed with return value: " + returnVal);
            myLog.writeln("==============================================");

        } catch (Exception ex) {
            myLog.writeln("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {

            myLog.done();
        }
    }

    @Override
    protected void runCommandAsBatch() {
        String tmp;
        myLog.writeln("==============================================");
        try {
            File tmpLogFile = Utility.createTmpFile(this.getName(), ".tmp");
            File tmpBatchFile = Utility.createTmpFile(this.getName(), ".bat");
            tmpBatchFile.setExecutable(true);

            // prepare command lines
            // ArrayList<String> flagList = new ArrayList<String>();
            flagList.add(cmd);

            addToFlagList(flags);

            flagList.add(" >> " + tmpLogFile.getPath());
            String str = "";
            for (String s : flagList) {
                str = str + " " + s;
            }

            // Load commandlines to the batchFile
            Utility.loadTextToFile(str, tmpBatchFile);

            // Execute the batchFile
            myLog.writeln("Executing command: " + cmd + flags);
            myLog.writeln(str);

            //   Process cmdProcess = Runtime.getRuntime().exec(new String[]{"cmd", "/c", tmpBatchFile.getAbsolutePath()});
            Process cmdProcess = Runtime.getRuntime().exec(tmpBatchFile.getAbsolutePath());

            System.out.println("Executing this command: " + tmpBatchFile.getAbsolutePath());


            BufferedReader br = new BufferedReader(new InputStreamReader(cmdProcess.getErrorStream()));
            while ((tmp = br.readLine()) != null) // logText += br.readLine();
            {
                myLog.writeln(tmp);

            }


            System.out.println("Output:");
            br = new BufferedReader(new InputStreamReader(cmdProcess.getInputStream()));

            while ((tmp = br.readLine()) != null) // logText += br.readLine();
            {
                myLog.writeln(tmp);
            }



            // Now load the tmpLogFile to the log file

            myLog.loadFromFile(tmpLogFile);




            int returnVal = cmdProcess.waitFor();
            myLog.writeln("Command executed with return value: " + returnVal);
            myLog.writeln("==============================================");




            //  System.out.println("Command executed succesfully: " + cmdProcess.waitFor());



        } catch (Exception ex) {
            myLog.writeln("Error: " + ex.getMessage());
        } finally {

            myLog.done();
        }

    }

    private void addToFlagList(HashMap<String, String> flags) {
        if (!flags.isEmpty()) {


            // Add flags
            for (Map.Entry e : flags.entrySet()) {

                if (!((String) e.getKey()).startsWith("_$")) {
                    flagList.add("-" + e.getKey());
                    flagList.add((String) e.getValue());
                } else {
                    flagList.add((String) e.getValue());
                }


            }

        }

    }
}
// -DEAD CODE-
// if (!inputs.isEmpty() && OFmt != null && OFname != null) {
//
//
//                // Inputs
//                for (Map.Entry e : inputs.entrySet()) {
//                    flags += "-" + e.getKey() + " " + e.getValue() + " ";
//
//                    flagList.add("-" + e.getKey());
//                    flagList.add((String) e.getValue());
//
//
//
//                }
//                // outputs
//                flags += "-" + this.OFmt + " " + this.OFname + " ";
//                flagList.add("-" + this.OFmt);
//                flagList.add(this.OFname);
//            }

