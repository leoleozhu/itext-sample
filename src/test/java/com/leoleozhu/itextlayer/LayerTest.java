package com.leoleozhu.itextlayer;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
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

        try(InputStream fontInputStream = new FileInputStream(resourceFile("fonts/SourceHanSansCN-Bold.otf"))) {
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
        PdfPage page = pdfDocument.addNewPage(new PageSize(mm2pt(160f), mm2pt(160f)));

        PdfCanvas pdfCanvas = new PdfCanvas(page);

        // layers
        PdfLayer shapeLayer = new PdfLayer("Shape", pdfDocument);
        PdfLayer txtLayer = new PdfLayer("Text", pdfDocument);

        // shapes
        pdfCanvas.beginLayer(shapeLayer);

        pdfCanvas.setFillColor( new DeviceRgb(0, 0, 200)).setStrokeColor( new DeviceRgb(0, 0, 200));
        pdfCanvas.rectangle(mm2pt(40f), mm2pt(40f), mm2pt(80f), mm2pt(80f)).setLineWidth(mm2pt(1)).stroke();

        pdfCanvas.endLayer();

        // text
        pdfCanvas.beginLayer(txtLayer);

        pdfCanvas.setFillColor(new DeviceRgb(200, 200, 0)).setStrokeColor(new DeviceRgb(200, 200, 0));
        pdfCanvas.rectangle(mm2pt(10f), mm2pt(10f), mm2pt(140f), mm2pt(20f)).setLineWidth(mm2pt(1)).stroke();
        pdfCanvas.beginText().moveText(mm2pt(40f), mm2pt(20f))
                .setFontAndSize(font, fontsize)
                .showText(text);

        pdfCanvas.endLayer();

        // close doc
        pdfDocument.close();
    }
}
