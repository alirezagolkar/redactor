package com.redactor.redactor.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.pdfcleanup.PdfCleanUpLocation;
import com.itextpdf.text.pdf.pdfcleanup.PdfCleanUpProcessor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to handle Pdf files
 */
@Service("PDFService")
public class PDFServiceImpl implements PDFService {

    public static final String replacePattern = "###########";

    /**
     * Replace a particular phrase with a given text in Pdf file
     *
     * @param inputStream
     * @param filePath
     * @param text
     * @throws IOException
     * @throws DocumentException
     */
    public void replaceText(InputStream inputStream, String filePath, String text) throws IOException, DocumentException
    {
        File directory = new File(String.valueOf(filePath));

        if (!directory.exists()) {
            directory.mkdirs();
        }

        PdfReader reader = new PdfReader(inputStream);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePath));

        for (int i = 1; i <= reader.getNumberOfPages(); i++)
        {
            PdfDictionary dict = reader.getPageN(i);
            PdfArray refs = null;
            if (dict.get(PdfName.CONTENTS).isArray()) {
                refs = dict.getAsArray(PdfName.CONTENTS);
            } else if (dict.get(PdfName.CONTENTS).isIndirect()) {
                refs = new PdfArray(dict.get(PdfName.CONTENTS));
            }
            for (int j = 0; j < (refs != null ? refs.size() : 0); j++) {
                PRStream stream = (PRStream) refs.getDirectObject(j);
                byte[] data = PdfReader.getStreamBytes(stream);
                stream.setData(new String(data).replaceAll(text, replacePattern).getBytes("UTF-8"));
            }
        }

        stamper.close();
        reader.close();
    }

    /**
     * Handle redaction in a given Pfd file
     *
     * @param input
     * @param filePath
     * @throws IOException
     * @throws DocumentException
     */
    public void redact(InputStream input, String filePath)
            throws IOException, DocumentException {
        PdfReader reader = new PdfReader(input);
        PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(filePath));
        List cleanUpLocations =
                new ArrayList();
        cleanUpLocations.add(new PdfCleanUpLocation(
                1, new Rectangle(97f, 405f, 480f, 445f), BaseColor.GRAY));
        PdfCleanUpProcessor cleaner =
                new PdfCleanUpProcessor(cleanUpLocations, stamper);
        cleaner.cleanUp();
        stamper.close();
        reader.close();
    }
}
