package com.leoleozhu.sample;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.colors.IccBased;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.leoleozhu.utils.TestCaseBase;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class PdfSampleTest extends TestCaseBase {

    private enum ColorType {
        CMYK,
        DeviceRGB,
        IccRGB,
    }

    private Color getRandomColor(ColorType colorType) throws IOException {
        if (colorType == ColorType.DeviceRGB) {
            return new DeviceRgb(
                    new Random().nextFloat(),
                    new Random().nextFloat(),
                    new Random().nextFloat())
                    ;
        }

        if (colorType == ColorType.CMYK) {
            return new DeviceCmyk(
                    new Random().nextFloat(),
                    new Random().nextFloat(),
                    new Random().nextFloat(),
                    new Random().nextFloat()
            );
        }

        try (InputStream inputStream = Files.newInputStream(Paths.get(resourceFile("icc/sRGB_ICC_v4_Appearance.icc")))) {
            PdfCieBasedCs.IccBased sRGB = new PdfCieBasedCs.IccBased(inputStream);

            return new IccBased(sRGB, new float[]{
                    new Random().nextFloat(),
                    new Random().nextFloat(),
                    new Random().nextFloat()});

        }
    }

    private Color getRandomColor(boolean isCmyk) {
        return isCmyk ?
                new DeviceCmyk(
                        new Random().nextFloat(),
                        new Random().nextFloat(),
                        new Random().nextFloat(),
                        new Random().nextFloat()
                )
                :
                new DeviceRgb(
                        new Random().nextFloat(),
                        new Random().nextFloat(),
                        new Random().nextFloat())
                ;
    }


    @Test
    public void testPdfs() throws Exception {
        samplePdf(100, 60, 3);
        samplePdf(100, 60, 3);
        samplePdf(100, 70, 3);
        samplePdf(100, 70, 3);
        samplePdf(100, 80, 3);
        samplePdf(100, 80, 3);
        samplePdf(100, 90, 3);
        samplePdf(100, 90, 3);
        samplePdf(60, 100, 3);
        samplePdf(60, 100, 3);
        samplePdf(70, 100, 3);
        samplePdf(70, 100, 3);
        samplePdf(80, 100, 3);
        samplePdf(80, 100, 3);
        samplePdf(90, 100, 3);
        samplePdf(90, 100, 3);
        samplePdf(100, 100, 3);
    }


    public void samplePdf(int width, int height, int bleed) throws Exception {
        samplePdf(width, height, bleed, ColorType.IccRGB);
        samplePdf(width, height, bleed, ColorType.DeviceRGB);
        samplePdf(width, height, bleed, ColorType.CMYK);
    }

    private void samplePdf(int width, int height, int bleed, ColorType colorType) throws Exception {
        PdfFont font;
        try (InputStream fontInputStream = Files.newInputStream(Paths.get(resourceFile("fonts/Source-Sans-3.046R/SourceSans3-Bold.otf")))) {
            ByteArrayOutputStream fontStream = new ByteArrayOutputStream();
            IOUtils.copy(fontInputStream, fontStream);
            font = PdfFontFactory.createFont(fontStream.toByteArray(), PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        }

        String destination = targetFile(String.format("%dx%d-bleed-%d-%s.pdf", width, height, bleed, colorType.toString()));

        float bleedPt = mm2pt(bleed);
        float contentWidth = mm2pt(width);
        float contentHeight = mm2pt(height);
        float pageWidth = contentWidth + bleedPt * 2;
        float pageHeight = contentHeight + bleedPt * 2;

        int fontSize = 12;

        try (PdfDocument pdfDocument = new PdfDocument((new PdfWriter(destination)))) {

            PdfPage page = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));
            page.setTrimBox(new Rectangle(bleedPt, bleedPt, contentWidth, contentHeight));
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            // bleed color
            for (int i = bleed; i > 0; i--) {
                Color c = getRandomColor(colorType);
                float m = mm2pt(bleed - i);
                pdfCanvas.setFillColor(c);
                pdfCanvas.rectangle(m, m, pageWidth - 2 * m, pageHeight - 2 * m).fill();
            }

            pdfCanvas.setFillColor(getRandomColor(colorType));
            pdfCanvas.rectangle(bleedPt, bleedPt, contentWidth, contentHeight).fill();

            String text = String.format(" %d x %d bleed=%dmm", width, height, bleed);
            pdfCanvas.setFillColor(getRandomColor(colorType)).beginText().moveText(bleedPt + fontSize, bleedPt + fontSize).setFontAndSize(font, fontSize).showText(text).endText();

            pdfCanvas.release();
        }

    }


}
