package stirling.software.SPDF.controller.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;

import stirling.software.SPDF.model.api.PDFFile;
import stirling.software.SPDF.service.PdfImageRemovalService;
import stirling.software.SPDF.utils.WebResponseUtils;

@RestController
@RequestMapping("/api/v1/general")
public class PdfImageRemovalController {

    @Autowired private PdfImageRemovalService pdfImageRemovalService;

    // Constructor injection for PdfImageRemover service
    public PdfImageRemovalController(PdfImageRemovalService pdfImageRemovalService) {
        this.pdfImageRemovalService = pdfImageRemovalService;
    }

    @PostMapping(consumes = "multipart/form-data", value = "/remove-image-pdf")
    @Operation(
            summary = "Remove images from file to reduce the file size.",
            description =
                    "This endpoint remove images from file to reduce the file size.Input:PDF Output:PDF Type:MISO")
    public ResponseEntity<byte[]> removeImages(@ModelAttribute PDFFile file) throws IOException {

        MultipartFile pdf = file.getFileInput();
        byte[] pdfBytes = pdf.getBytes();
        PDDocument document = Loader.loadPDF(pdfBytes);

        PDDocument modifiedDocument = pdfImageRemovalService.removeImagesFromPdf(document);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        modifiedDocument.save(outputStream);
        modifiedDocument.close();

        String mergedFileName =
                pdf.getOriginalFilename().replaceFirst("[.][^.]+$", "") + "_removed_images.pdf";

        return WebResponseUtils.bytesToWebResponse(outputStream.toByteArray(), mergedFileName);
    }
}
