package com.leoleozhu.itextlayer;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.leoleozhu.utils.TestCaseBase;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

public class LayerTest extends TestCaseBase {

    private PdfFont font;
    private int fontsize;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        try (InputStream fontInputStream = new FileInputStream(resourceFile("fonts/SourceHanSansCN-Bold.otf"))) {
            ByteArrayOutputStream fontStream = new ByteArrayOutputStream();
            IOUtils.copy(fontInputStream, fontStream);
            font = PdfFontFactory.createFont(fontStream.toByteArray(), PdfEncodings.IDENTITY_H, true);
        }

        fontsize = 12;
    }

    @Test
    public void testMultipleLayerPdf() throws Exception {
        String text = "Hello, This text is in a txt layer!";

        String destination = targetFile("layer-MultipleLayerPdf.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        // layers
        PdfLayer shapeLayer = new PdfLayer("Shape", pdfDocument);
        PdfLayer txtLayer = new PdfLayer("Text", pdfDocument);

        for (int i = 0; i < 2; i++) {
            PdfPage page = pdfDocument.addNewPage(new PageSize(mm2pt(160f), mm2pt(160f)));
            addRectToPageLayer(page, shapeLayer, new DeviceRgb(0, 0, 100));
            addTextToPageLayer(page, txtLayer, "Page " + (i + 1), new DeviceRgb(150, 150, 0));
        }

        // close doc
        pdfDocument.close();
    }

    private void addRectToPageLayer(PdfPage page, PdfLayer layer, Color color) {
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.beginLayer(layer);

        pdfCanvas.setFillColor(color)
                .setStrokeColor(color);

        Rectangle pageRect = page.getPageSize();
        float margin = Math.min(pageRect.getWidth(), pageRect.getHeight()) * 0.1f;

        pdfCanvas.rectangle(
                pageRect.getX() + margin,
                pageRect.getY() + margin,
                pageRect.getWidth() - 2 * margin,
                pageRect.getHeight() - 2 * margin)
                .setLineWidth(mm2pt(1)).stroke();

        pdfCanvas.endLayer();
    }

    private void addTextToPageLayer(PdfPage page, PdfLayer layer, String text, Color color) {
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.beginLayer(layer);

        pdfCanvas.setFillColor(color)
                .setStrokeColor(color);

        Rectangle pageRect = page.getPageSize();
        float margin = Math.min(pageRect.getWidth(), pageRect.getHeight()) * 0.3f;

        pdfCanvas.beginText().moveText(pageRect.getX() + margin, pageRect.getY() + margin)
                .setFontAndSize(font, fontsize)
                .showText(text)
                .endText();

        pdfCanvas.endLayer();
    }
}
