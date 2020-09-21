package com.standardChartered.scanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.util.ResourceUtils;

public class DigitalNumberScanner {

	private static final Map<String, String> DIGIT_MAP = new HashMap<String, String>();

	public List<String> parse(final String filename) throws DigitalNumberScannerException {
		init();
		String input = readFile(filename);
		if (input != null) {
			return processFile(input);			
		} else {
			throw new DigitalNumberScannerException("Error reading file");
		}
	}

	private void init() {
		//Can populate this from config later in case we need to add letters
		DIGIT_MAP.put(" _ | ||_|", "0");
		DIGIT_MAP.put("     |  |", "1");
		DIGIT_MAP.put(" _  _||_ ", "2");
		DIGIT_MAP.put(" _  _| _|", "3");
		DIGIT_MAP.put("   |_|  |", "4");
		DIGIT_MAP.put(" _ |_  _|", "5");
		DIGIT_MAP.put(" _ |_ |_|", "6");
		DIGIT_MAP.put(" _   |  |", "7");
		DIGIT_MAP.put(" _ |_||_|", "8");
		DIGIT_MAP.put(" _ |_| _|", "9");
	}

	private String readFile(final String filename) {
		File file = null;
		String input = null;
		try {
			file = ResourceUtils.getFile("classpath:sample-input/" + filename);
			input = new String(Files.readAllBytes(file.toPath()));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return input;
	}
	
	private List<String> processFile(final String input) throws DigitalNumberScannerException {
		final String REGEX_WHITE_SPACE = "^\\s*$";
		final int LINE_LIMIT = 1200;
		final int ROW_SIZE = 3;

		final List<String> results = new ArrayList<String>();
		try {
			//Remove blank rows
			List<String> lines = Arrays.asList(input.split("\n")).stream()
				.filter(s -> !s.matches(REGEX_WHITE_SPACE))
				.collect(Collectors.toList());
			
			if (lines.size() > LINE_LIMIT) {
				throw new DigitalNumberScannerException("File has more that 400 digit lines");
			}
			
			//Process every 3 rows
			IntStream.range(0, lines.size())
				.filter(i -> i % ROW_SIZE == 0)
	        	.mapToObj(i -> lines.subList(i, Math.min(i + ROW_SIZE, lines.size())))
	        	.forEach(row -> {
	        		results.add(processLines(row));
	        	});
		} catch (Exception e) {
			e.printStackTrace();
			throw new DigitalNumberScannerException("Error processing file");
		}
		return results;
	}

	private String processLines(final List<String> rows) {
		final int ROW_SIZE = 3;
		final int TOTAL_DIGITS = 9;
		final String INVALID_CHAR_LENGTH = "?????????ILL";
		final String INVALID_DIGIT = "?";
		final String INVALID_NUMBER = "ILL";
		
		List<List<String>> lines = new ArrayList<List<String>>();
		//Validate lines
		for (String row: rows) {
			if (hasValidLength(row)) {
				lines.add(Arrays.asList(row.split("")));
			} else {
				System.out.println("Line has invalid number of characters");
			}			
		}

		//Abort is character length is invalid
		if (lines.size() != ROW_SIZE) {
			return INVALID_CHAR_LENGTH;
		}			

		//Split rows into columns
		List<List<List<String>>> columns = partitionBySize(lines);
		StringBuffer digit =  new StringBuffer();
		for (int i = 0; i < TOTAL_DIGITS; i++ ) {
			final StringBuffer key = new StringBuffer(String.join("", columns.get(0).get(i)));
			key.append(String.join("", columns.get(1).get(i)));
			key.append(String.join("", columns.get(2).get(i)));
			digit.append(getDigit(key.toString()));
		}
		
		if (digit.toString().contains(INVALID_DIGIT)) digit.append(INVALID_NUMBER);
		return digit.toString();
	}
	
	private String getDigit(final String key) {
		return DIGIT_MAP.get(key) != null && hasValidCharacters(key) ? DIGIT_MAP.get(key) : "?";
	}
	
	private static List<List<List<String>>> partitionBySize(final List<List<String>> rows) {
		final int COL_SIZE = 3;
		List<List<List<String>>> subLists = new ArrayList<>();
	    rows.forEach(row -> {
	    	//Split rows into columns of 3
	    	List<List<String>> sublist = IntStream.range(0, row.size())
	    		    .filter(i -> i % COL_SIZE == 0)
	    		    .mapToObj(i -> row.subList(i, Math.min(i + COL_SIZE, row.size())))
	    		    .collect(Collectors.toList());
	    	subLists.add(sublist);
	    });
	    return subLists;
	}
	
	public boolean hasValidLength(final String line) {
		final int VALID_STRING_LENGTH = 27;
		return line.length() == VALID_STRING_LENGTH ? true : false;
	}
	
	public boolean hasValidCharacters(final String line) {
		final String REGEX_VALID_CHARS = "^[\\s\\|_]*$";
		Matcher matcher = Pattern.compile(REGEX_VALID_CHARS).matcher(line);
		return matcher.matches();
	}
}
