package com.leoleozhu.itextpdfmodification;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.leoleozhu.utils.TestCaseBase;
import org.junit.Test;


public class PdfModifyTest extends TestCaseBase {

    @Test
    public void testMergePdfAndEdit() throws Exception {

        String[] srcPdfs = new String[]{
                resourceFile("pdf/dummy.pdf"),
                resourceFile("pdf/A Sample PDF.pdf"),
        };

        String destPdf = targetFile("modification-MergeAndEdit.pdf");

        try (PdfDocument tgt = new PdfDocument(new PdfWriter(destPdf))) {
            for (String srcPdf : srcPdfs) {
                try (PdfDocument src = new PdfDocument(new PdfReader(srcPdf))) {
                    for (int i = 1; i <= src.getNumberOfPages(); i++) {
                        int targetIndex = tgt.getNumberOfPages() + 1;
                        src.copyPagesTo(i, i, tgt, targetIndex);
                        PdfPage page = tgt.getPage(targetIndex);

                        // create page canvas
                        PdfCanvas pdfCanvas = new PdfCanvas(page);
                        pdfCanvas.setFillColorGray(0.5f)
                                .rectangle(page.getPageSize().getWidth() * 4 / 10,
                                        page.getPageSize().getHeight() * 4 / 10,
                                        page.getPageSize().getWidth() * 2 / 10,
                                        page.getPageSize().getHeight() * 2 / 10)
                                .fill();

                        pdfCanvas.release();
                    }

                }
            }
        }
    }
}