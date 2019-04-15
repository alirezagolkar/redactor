package com.redactor.redactor.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

import com.itextpdf.text.DocumentException;
import com.redactor.redactor.exception.StorageFileNotFoundException;
import com.redactor.redactor.model.View;
import com.redactor.redactor.service.PDFService;
import com.redactor.redactor.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import javax.servlet.http.HttpServletResponse;

/**
 * Controller to handle files
 */
@Controller
public class FileUploadController {

    private final StorageService storageService;

    @Autowired
    public FileUploadController(StorageService storageService) {
        this.storageService = storageService;
    }

    @Autowired
    PDFService pdfService;

    /**
     * Returns a list of all uploaded files
     *
     * @param model
     * @return
     * @throws IOException
     */
    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", storageService.loadAll().map(
                path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                        "serveFile", path.getFileName().toString()).build().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    /**
     * Load a single file
     *
     * @param filename
     * @return
     */
    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = storageService.loadAsResource(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    /**
     * Handle file upload
     *
     * @param file
     * @param replaceText
     * @param filePath
     * @param response
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   @RequestParam("replaceText") String replaceText,
                                   @RequestParam("filePath") String filePath,
                                   HttpServletResponse response) throws IOException, DocumentException {

        storageService.store(file);
        pdfService.replaceText(file.getInputStream(), filePath, replaceText);

        response.setContentType("application/pdf");
        response.setHeader("Cache-Control", "private, max-age=5");

        response.setHeader("Redacted file", "");
        if (file.getSize()>0) {
            response.setContentLength((int) file.getSize());
        }
        response.getOutputStream().write(file.getBytes());
        response.getOutputStream().flush();
        response.getOutputStream().close();

        return "uploadForm";

    }

    /**
     * Return a file view
     *
     * @param model
     * @return
     */
    @GetMapping("/view")
    public String getFileView(Model model) {
        model.addAttribute("fileView", getView());
        return "fileView";
    }

    /**
     * Create a file view
     *
     * @return
     */
    public View getView() {
        //dummy report
        View view = new View();
        view.setName("File View");
        view.setContent("This is the file viewer");
        view.setDate(LocalDateTime.now());
        return view;
    }

    /**
     * Handles exceptions for the controller
     *
     * @param exc
     * @return
     */
    @ExceptionHandler(StorageFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(StorageFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }
}
