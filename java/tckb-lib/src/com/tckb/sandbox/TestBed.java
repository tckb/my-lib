/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tckb.sandbox;

import com.tckb.util.Utility;
import java.io.File;

/**
 *
 * @author tckb
 */
public class TestBed {
    public static void main(String[] args) {
        
       File someFile = Utility.getFileFromUI(null);
       Utility.makeDuplicate(someFile);
    }
    
}
