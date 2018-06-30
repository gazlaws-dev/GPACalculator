package com.gazlaws.nitcgpa;

//import org.apache.pdfbox.io.RandomAccessFile;
import com.tom_roush.pdfbox.io.RandomAccessFile;

//import org.apache.pdfbox.pdfparser.PDFParser;
import com.tom_roush.pdfbox.pdfparser.PDFParser;

//import org.apache.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.pdmodel.PDDocument;

//import org.apache.pdfbox.text.PDFTextStripper;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.File;


public class PDFService {

    private String inputPDF;

    public PDFService(String inputPDF) {
        this.inputPDF = inputPDF;
    }

    public String extractText() {
        try {
            PDFParser pdfparser = new PDFParser(new RandomAccessFile(new File(inputPDF), "r"));
            pdfparser.parse();

            return new PDFTextStripper().getText(new PDDocument(pdfparser.getDocument()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
