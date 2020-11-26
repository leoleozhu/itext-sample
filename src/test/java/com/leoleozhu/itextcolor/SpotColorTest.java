package com.leoleozhu.itextcolor;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.function.PdfFunction;
import com.leoleozhu.utils.TestCaseBase;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.InputStream;

public class SpotColorTest extends TestCaseBase {

    private PdfFont font;
    private int fontsize;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        fontsize = 12;
    }

    @Test
    public void testAddSpotColorWithAlternativeDeviceRGB() throws Exception {
        String text = "Hello, Spot with alt DeviceRGB!";
        String destination = targetFile("spotcolor-AddSpotColorWithAlternativeDeviceRGB.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(mm2pt(160f), mm2pt(160f)));

        Color alternateSpace = new DeviceRgb(18, 18, 135);
        Color color = createSpotColor("Spot-ALT-DEVICE-RGB", alternateSpace);

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.setFillColor(color).setStrokeColor(color);

        pdfCanvas.rectangle(mm2pt(40f), mm2pt(40f), mm2pt(80f), mm2pt(80f)).setLineWidth(mm2pt(1)).stroke();
        pdfCanvas.rectangle(mm2pt(0f), mm2pt(0f), mm2pt(40f), mm2pt(40f)).fillStroke();
        pdfCanvas.beginText().moveText(mm2pt(48f), mm2pt(48f)).setFontAndSize(font, fontsize).showText(text);

        pdfDocument.close();
    }

    @Test
    public void testAddSpotColorWithAlternativeDeviceCMYK() throws Exception {
        String text = "Hello, Spot with alt DeviceCMYK!";
        String destination = targetFile("spotcolor-AddSpotColorWithAlternativeDeviceCMYK.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(mm2pt(160f), mm2pt(160f)));

        Color alternateSpace = new DeviceCmyk(100, 50, 60, 0);
        Color color = createSpotColor("Spot-ALT-DEVICE-CMYK", alternateSpace);

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.setFillColor(color).setStrokeColor(color);

        pdfCanvas.rectangle(mm2pt(40f), mm2pt(40f), mm2pt(80f), mm2pt(80f)).setLineWidth(mm2pt(1)).stroke();
        pdfCanvas.rectangle(mm2pt(0f), mm2pt(0f), mm2pt(40f), mm2pt(40f)).fillStroke();
        pdfCanvas.beginText().moveText(mm2pt(48f), mm2pt(48f)).setFontAndSize(font, fontsize).showText(text);

        pdfDocument.close();
    }

    @Test
    public void testAddSpotColorWithAlternativeDeviceGray() throws Exception {
        String text = "Hello, Spot with alt DeviceGRAY!";
        String destination = targetFile("spotcolor-AddSpotColorWithAlternativeDeviceGray.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(mm2pt(160f), mm2pt(160f)));

        Color alternateSpace = DeviceGray.GRAY;
        Color color = createSpotColor("Spot-ALT-DEVICE-GRAY", alternateSpace);

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.setFillColor(color).setStrokeColor(color);

        pdfCanvas.rectangle(mm2pt(40f), mm2pt(40f), mm2pt(80f), mm2pt(80f)).setLineWidth(mm2pt(1)).stroke();
        pdfCanvas.rectangle(mm2pt(0f), mm2pt(0f), mm2pt(40f), mm2pt(40f)).fillStroke();
        pdfCanvas.beginText().moveText(mm2pt(48f), mm2pt(48f)).setFontAndSize(font, fontsize).showText(text);

        pdfDocument.close();
    }


    @Test
    public void testAddSpotColorWithAlternativeIcc_sRGB() throws Exception {
        String text = "Hello, Spot with alt Icc sRGB!";
        String destination = targetFile("spotcolor-AddSpotColorWithIcc_sRGB.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(mm2pt(160f), mm2pt(160f)));

        InputStream inputStream = new FileInputStream(resourceFile("icc/sRGB_ICC_v4_Appearance.icc"));
        PdfCieBasedCs.IccBased sRGB = new PdfCieBasedCs.IccBased(inputStream);

        Color alternateSpace = new IccBased(sRGB, new float[]{18f / 255, 18f / 255, 135f / 255});
        Color color = createSpotColor("Spot-ALT-ICC-sRGB", alternateSpace);

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.setFillColor(color).setStrokeColor(color);

        pdfCanvas.rectangle(mm2pt(40f), mm2pt(40f), mm2pt(80f), mm2pt(80f)).setLineWidth(mm2pt(1)).stroke();
        pdfCanvas.rectangle(mm2pt(0f), mm2pt(0f), mm2pt(40f), mm2pt(40f)).fillStroke();
        pdfCanvas.beginText().moveText(mm2pt(48f), mm2pt(48f)).setFontAndSize(font, fontsize).showText(text);

        pdfDocument.close();
    }


    private Separation createSpotColor(String name, Color alternateColor) {
        int componentsNum = alternateColor.getNumberOfComponents();
        float[] c0 = new float[componentsNum];
        for (int i = 0; i < c0.length; i++) {
            c0[i] = 1f;
        }

        PdfFunction tintTransform = new PdfFunction.Type2(
                new PdfArray(new float[]{0.0f, 1f}),
                null,
                new PdfArray(c0),
                new PdfArray(alternateColor.getColorValue()),
                new PdfNumber(1f));

        return new Separation(name, alternateColor.getColorSpace(), tintTransform, 1f);
    }

}
