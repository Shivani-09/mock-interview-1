package com.example.mockInterview.service;

import com.example.mockInterview.model.PrimeModel;
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

@Component
public class FileParser {

    public List<PrimeModel> parseFile(byte[] fileBytes, String originalFilename) {
        String contentType = determineContentType(originalFilename);
        
        if (contentType == null) {
             throw new IllegalArgumentException("File type could not be determined.");
        }

        if (contentType.equals("text/csv")) {
            return csvToPrime(new ByteArrayInputStream(fileBytes));
        } else if (contentType.equals("text/plain")) {
            return txtToPrime(new ByteArrayInputStream(fileBytes));
        } else if (contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
            return xlsxToPrime(new ByteArrayInputStream(fileBytes));
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileBytes);
        }
    }

    // Existing method, now private
    private List<PrimeModel> csvToPrime(InputStream is) {
        // Your existing logic for CSV parsing
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.builder().setHeader("input").build())) {

            List<PrimeModel> primeList = new ArrayList<>();
            for (org.apache.commons.csv.CSVRecord csvRecord : csvParser) {
                PrimeModel primeNumbers = new PrimeModel();
                int input = Integer.parseInt(csvRecord.get("input"));
                primeNumbers.setInput(input);
                primeNumbers.setPrimeCheck(checkForPrime(input));
                primeList.add(primeNumbers);
            }
            return primeList;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV file: " + e.getMessage(), e);
        }
    }

    // New method for TXT files, now private
    private List<PrimeModel> txtToPrime(InputStream is) {
        // Your new logic for TXT parsing (e.g., line-by-line reading)
        List<PrimeModel> primeList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    int input = Integer.parseInt(line.trim());
                    PrimeModel primeNumber = new PrimeModel();
                    primeNumber.setInput(input);
                    primeNumber.setPrimeCheck(checkForPrime(input));
                    primeList.add(primeNumber);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping invalid number in TXT file: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read TXT file", e);
        }
        return primeList;
    }

    // Method to be implemented for XLSX
    private List<PrimeModel> xlsxToPrime(InputStream is) {
        List<PrimeModel> primeList = new ArrayList<>();
        
        // Create an instance of DataFormatter once outside the loop
        DataFormatter formatter = new DataFormatter();
        
        try (XSSFWorkbook workbook = new XSSFWorkbook(is)) {
            XSSFSheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row == null) {
                    continue; // Skip null rows
                }
                Cell cell = row.getCell(0);
                if (cell == null) {
                    continue; // Skip if the first cell is empty
                }
                
                // Use DataFormatter to get the cell's formatted value as a string
                String cellValue = formatter.formatCellValue(cell);

                int input;
                try {
                    // Try to parse the formatted string to an integer
                    input = Integer.parseInt(cellValue.trim());
                } catch (NumberFormatException e) {
                    // This handles cases where the value is not a valid number
                    System.err.println("Skipping non-numeric cell value: " + cellValue);
                    continue;
                }

                // If a valid number was found, process it
                PrimeModel primeNumbers = new PrimeModel();
                primeNumbers.setInput(input);
                primeNumbers.setPrimeCheck(checkForPrime(input));
                primeList.add(primeNumbers);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse XLSX file: " + e.getMessage(), e);
        }

        return primeList;
    }
    private String determineContentType(String filename) {
        if (filename.endsWith(".csv")) return "text/csv";
        if (filename.endsWith(".txt")) return "text/plain";
        if (filename.endsWith(".xlsx")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        return "application/octet-stream"; // A default for unsupported types
    }

    private boolean checkForPrime(int input) {
        // Your existing prime check logic
        if (input <= 1) return false;
        if (input == 2) return true;
        if (input % 2 == 0) return false;
        for (int i = 3; i * i <= input; i += 2) {
            if (input % i == 0) return false;
        }
        return true;
    }
}