package com.leoleozhu.itextbookmark;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutline;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.leoleozhu.utils.TestCaseBase;

import org.junit.Test;

import java.util.List;


public class PdfBookmarkReaderTest extends TestCaseBase {


    @Test
    public void testConvertPdfToTiff() throws Exception {

        String fileName = resourceFile("pdf/PDF-page-bookmark.pdf");

        try (PdfReader reader = new PdfReader(fileName)) {
            PdfDocument doc = new PdfDocument(reader);
            PdfOutline outline = doc.getOutlines(false);
            List<PdfOutline> outlines = outline.getAllChildren();
            for(PdfOutline bookmark : outlines) {
                PdfDestination destination = bookmark.getDestination();
                String title = bookmark.getTitle();
                System.out.println(title);
            }
        }

    }
}
