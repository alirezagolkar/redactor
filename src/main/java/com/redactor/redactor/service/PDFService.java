package com.redactor.redactor.service;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Pdf handler service
 */
public interface PDFService {

    void replaceText(InputStream inputStream, String filePath, String text) throws IOException, DocumentException;

    void redact(InputStream input, String filePath) throws IOException, DocumentException;
}
