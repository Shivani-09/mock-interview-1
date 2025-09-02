//    public List<PrimeModel> parseFile(byte[] fileBytes, String originalFilename) {
//        String contentType = determineContentType(originalFilename);
//        
//        if (contentType == null) {
//             throw new IllegalArgumentException("File type could not be determined.");
//        }
//
//        if (contentType.equals("text/csv")) {
//            return csvToPrime(new ByteArrayInputStream(fileBytes));
//        } else if (contentType.equals("text/plain")) {
//            return txtToPrime(new ByteArrayInputStream(fileBytes));
//        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
//            return xlsxToPrime(new ByteArrayInputStream(fileBytes));
//        } else {
//            throw new IllegalArgumentException("Unsupported file type: " + fileBytes);
//        }
//    }
//
//    // Existing method, now private
//    private List<PrimeModel> csvToPrime(InputStream is) {
//        // Your existing logic for CSV parsing
//        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
//             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader("input").build())) {
//
//            List<PrimeModel> primeList = new ArrayList<>();
//            for (org.apache.commons.csv.CSVRecord csvRecord : csvParser) {
//                PrimeModel primeNumbers = new PrimeModel();
//                int input = Integer.parseInt(csvRecord.get("input"));
//                primeNumbers.setInput(input);
//                primeNumbers.setPrimeCheck(checkForPrime(input));
//                primeList.add(primeNumbers);
//            }
//            return primeList;
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
//        }
//    }
//
//    // New method for TXT files, now private
//    private List<PrimeModel> txtToPrime(InputStream is) {
//        // Your new logic for TXT parsing (e.g., line-by-line reading)
//        List<PrimeModel> primeList = new ArrayList<>();
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                try {
//                    int input = Integer.parseInt(line.trim());
//                    PrimeModel primeNumber = new PrimeModel();
//                    primeNumber.setInput(input);
//                    primeNumber.setPrimeCheck(checkForPrime(input));
//                    primeList.add(primeNumber);
//                } catch (NumberFormatException e) {
//                    System.err.println("Skipping invalid number in TXT file: " + line);
//                }
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to read TXT file", e);
//        }
//        return primeList;
//    }
//
//    // Method to be implemented for XLSX
//    private List<PrimeModel> xlsxToPrime(InputStream is) {
//        List<PrimeModel> primeList = new ArrayList<>();
//        
//        // Create an instance of DataFormatter once outside the loop
//        DataFormatter formatter = new DataFormatter();
//        
//        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
//            XSSFSheet sheet = workbook.getSheetAt(0);
//
//            for (Row row : sheet) {
//                if (row == null) {
//                    continue; // Skip null rows
//                }
//                Cell cell = row.getCell(0);
//                if (cell == null) {
//                    continue; // Skip if the first cell is empty
//                }
//                
//                // Use DataFormatter to get the cell's formatted value as a string
//                String cellValue = formatter.formatCellValue(cell);
//
//                int input;
//                try {
//                    // Try to parse the formatted string to an integer
//                    input = Integer.parseInt(cellValue.trim());
//                } catch (NumberFormatException e) {
//                    // This handles cases where the value is not a valid number
//                    System.err.println("Skipping non-numeric cell value: " + cellValue);
//                    continue;
//                }
//
//                // If a valid number was found, process it
//                PrimeModel primeNumbers = new PrimeModel();
//                primeNumbers.setInput(input);
//                primeNumbers.setPrimeCheck(checkForPrime(input));
//                primeList.add(primeNumbers);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to parse XLSX file: " + e.getMessage(), e);
//        }
//
//        return primeList;
//    }
//    private String determineContentType(String filename) {
//        if (filename.endsWith(".csv")) return "text/csv";
//        if (filename.endsWith(".txt")) return "text/plain";
//        if (filename.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
//        return "application/octet-stream"; // A default for unsupported types
//    }

package com.example.mockInterview.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class FileParser {

	// Make the main method generic by taking a `Function` for the business logic
	public <T> List<T> parseFile(byte[] fileBytes, String originalFilename, Function<Integer, T> processor) {
		String contentType = determineContentType(originalFilename);

		if (contentType == null) {
			throw new IllegalArgumentException("File type could not be determined.");
		}

		if (contentType.equals("text/csv")) {
			return parseNumbersFromCsv(new ByteArrayInputStream(fileBytes), processor);
		} else if (contentType.equals("text/plain")) {
			return parseNumbersFromTxt(new ByteArrayInputStream(fileBytes), processor);
		} else if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
			return parseNumbersFromXlsx(new ByteArrayInputStream(fileBytes), processor);
		} else {
			throw new IllegalArgumentException("Unsupported file type: " + originalFilename);
		}
	}

	// A single, generic method for CSV parsing
	private <T> List<T> parseNumbersFromCsv(InputStream is, Function<Integer, T> processor) {
		List<T> resultList = new ArrayList<>();
		try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				CSVParser csvParser = new CSVParser(fileReader,
						CSVFormat.DEFAULT.builder().setHeader("input").build())) {

			for (org.apache.commons.csv.CSVRecord csvRecord : csvParser) {
				try {
					int input = Integer.parseInt(csvRecord.get("input"));
					resultList.add(processor.apply(input));
				} catch (NumberFormatException e) {
					System.err.println("Skipping non-numeric value in CSV: " + csvRecord.get("input"));
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
		}
		return resultList;
	}

	// A single, generic method for TXT parsing
	private <T> List<T> parseNumbersFromTxt(InputStream is, Function<Integer, T> processor) {
		List<T> resultList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
			String line;
			while ((line = reader.readLine()) != null) {
				try {
					int input = Integer.parseInt(line.trim());
					resultList.add(processor.apply(input));
				} catch (NumberFormatException e) {
					System.err.println("Skipping non-numeric value in TXT: " + line);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to read TXT file", e);
		}
		return resultList;
	}

	// A single, generic method for XLSX parsing
	private <T> List<T> parseNumbersFromXlsx(InputStream is, Function<Integer, T> processor) {
		List<T> resultList = new ArrayList<>();
		DataFormatter formatter = new DataFormatter();

		try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
			XSSFSheet sheet = workbook.getSheetAt(0);

			for (Row row : sheet) {
				Cell cell = row.getCell(0);
				if (cell == null)
					continue;

				String cellValue = formatter.formatCellValue(cell);

				try {
					int input = Integer.parseInt(cellValue.trim());
					resultList.add(processor.apply(input));
				} catch (NumberFormatException e) {
					System.err.println("Skipping non-numeric cell value: " + cellValue);
					continue;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to parse XLSX file: " + e.getMessage(), e);
		}
		return resultList;
	}

	private String determineContentType(String filename) {
		if (filename.endsWith(".csv"))
			return "text/csv";
		if (filename.endsWith(".txt"))
			return "text/plain";
		if (filename.endsWith(".xlsx"))
			return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
		return null;
	}
	
	// A generic parser that takes a BiFunction
    public <T> List<T> parseFileWithTwoNumbers(byte[] fileBytes, String fileName, BiFunction<Integer, Integer, T> processor) throws IOException {
        List<T> resultList = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileBytes)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(","); // Split line by comma
                if (parts.length == 2) {
                    try {
                        int num1 = Integer.parseInt(parts[0].trim());
                        int num2 = Integer.parseInt(parts[1].trim());
                        resultList.add(processor.apply(num1, num2));
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid numeric line in TXT: " + line);
                    }
                } else {
                    System.err.println("Skipping malformed line in TXT: " + line);
                }
            }
        }
        return resultList;
    }
}