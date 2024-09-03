package com.leoleozhu.itextpdfmodification;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.utils.PdfMerger;
import com.leoleozhu.utils.TestCaseBase;
import org.junit.Test;


public class PdfModifyTest extends TestCaseBase {

    @Test
    public void testPdfAddText() throws Exception {
        String srcPdf = resourceFile("pdf/00064_140x216.pdf");

        String[] colors = {
                "red",
                "green",
                "yellow",
                "aqua",
                "fuchsia",
                "gray",
                "maroon",
                "navy",
                "olive",
                "silver",
                "teal",
                "black",
                "orange",
                "pink",
                "brown",
        };

        Color[] itxColors = {
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{1f, 0f, 0f}), //red
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0f, 1f, 0f}), //green
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{1f, 1f, 0f}), //yellow
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0f, 1f, 1f}), //aqua
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{1f, 0f, 1f}), //fuchsia
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0.5f, 0.5f, 0.5f}), //gray
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0.5f, 0f, 0f}), //maroon
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0f, 0f, 0.5f}), //navy
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0.5f, 0.5f, 0f}), //olive
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0.75f, 0.75f, 0.75f}), //silver
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0f, 0.5f, 0.5f}), //teal
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0f, 0f, 0f}), //black
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{1f, 0.65f, 0f}), //orange
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{1f, 0.75f, 0.8f}), //pink
                Color.makeColor(new PdfDeviceCs.Rgb(), new float[]{0.65f, 0.16f, 0.16f}) //brown
        };

        for (int c = 0; c < colors.length; c++) {
            String destPdf = targetFile(String.format("00064_140x216-%s.pdf", colors[c]));
            try (PdfDocument tgt = new PdfDocument(new PdfWriter(destPdf).setSmartMode(true))) {

                try (PdfDocument src = new PdfDocument(new PdfReader(srcPdf))) {
                    for (int i = 1; i <= src.getNumberOfPages(); i++) {
                        int targetIndex = tgt.getNumberOfPages() + 1;
                        src.copyPagesTo(i, i, tgt, targetIndex);
                        PdfPage page = tgt.getPage(targetIndex);
                        // create page canvas
                        PdfCanvas pdfCanvas = new PdfCanvas(page);

                        pdfCanvas.setFillColor(itxColors[c])
                                .rectangle((page.getTrimBox().getLeft() + page.getTrimBox().getRight()) / 2 - mm2pt(5),
                                        page.getTrimBox().getBottom() + mm2pt(50),
                                        mm2pt(10),
                                        mm2pt(10))
                                .fill();

                        pdfCanvas.release();
                    }

                }

            }
        }


    }


    @Test
    public void testMergePdfMerger() throws Exception {

        String[] srcPdfs = new String[]{
                resourceFile("pdf/4902-out.pdf"),
                resourceFile("pdf/4902-in.pdf"),
        };

        String destPdf = targetFile("modification-MergerSimple.pdf");

        try (PdfDocument tgt = new PdfDocument(new PdfWriter(destPdf).setSmartMode(true))) {
            PdfMerger merger = new PdfMerger(tgt);
            for (String srcPdf : srcPdfs) {
                try (PdfDocument src = new PdfDocument(new PdfReader(srcPdf))) {
                    merger.merge(src, 1, src.getNumberOfPages());
                }
            }
        }


    }

    @Test
    public void testMergePdfAndEdit() throws Exception {

        String[] srcPdfs = new String[]{
                resourceFile("pdf/dummy.pdf"),
                resourceFile("pdf/A Sample PDF.pdf"),
                resourceFile("pdf/4902-out.pdf"),
                resourceFile("pdf/4902-in.pdf"),
        };

        String destPdf = targetFile("modification-MergeAndEdit.pdf");

        try (PdfDocument tgt = new PdfDocument(new PdfWriter(destPdf).setSmartMode(true))) {
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