package com.standardChartered;

import java.util.List;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.standardChartered.scanner.DigitalNumberScanner;
import com.standardChartered.scanner.DigitalNumberScannerException;

@SpringBootApplication
public class App {
	
	/**
	 * Future changes
	 * 
	 * 1. Are leading zeroes valid?
	 * 2. Are all zeroes valid?
	 * 3. Read in digit pattern match map from config
	 */
	
	final static String MULTIPLE_VALID_FILE = "multipleChunks";
	final static String MULTIPLE_INVALID_FILE = "multipleChunksWithIllegalRow";
	final static String SINGLE_FILE = "singleChunk";
    public static void main( String[] args ) {
		try {
			DigitalNumberScanner scanner =  new DigitalNumberScanner();
			final List<String> results = scanner.parse(MULTIPLE_VALID_FILE);
			results.forEach(r-> System.out.println(r));
		} catch (DigitalNumberScannerException e) {
			System.out.println(e.getMessage());
		}
    }
}
