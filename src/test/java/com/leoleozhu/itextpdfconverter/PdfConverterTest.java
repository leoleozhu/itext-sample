package com.leoleozhu.itextpdfconverter;

import com.leoleozhu.utils.TestCaseBase;
import org.apache.commons.io.FileUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.time.Instant;


public class PdfConverterTest extends TestCaseBase {


    @Test
    public void testConvertPdfToTiff() throws Exception {

        String fileName = resourceFile("pdf/dummy.pdf");
        String format = "tiff";
        String targetFolder = targetFile("tiff-" + Instant.now().toString());
        FileUtils.forceMkdir(new File(targetFolder));

        File pdfFile = new File(fileName);

        try (
                // PdfBox document, which is to create images
                PDDocument document = PDDocument.load(pdfFile, MemoryUsageSetting.setupTempFileOnly())
        ) {
            PDFRenderer renderer = new PDFRenderer(document);
            int pdf_pageCount = document.getPages().getCount();
            for (int i = 0; i < pdf_pageCount; i++) {
                // buffered image from the pdf
                BufferedImage bufferedImage;
                bufferedImage = renderer.renderImageWithDPI(i, 300);

                try (FileOutputStream baos = new FileOutputStream(String.format(targetFolder + "/%2d.%s", i+1, format))) {
                    ImageIO.write(bufferedImage, format, baos);
                }
            }
        }

    }
}
