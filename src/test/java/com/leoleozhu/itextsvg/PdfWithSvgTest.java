package com.leoleozhu.itextsvg;

import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.svg.converter.SvgConverter;
import com.leoleozhu.utils.TestCaseBase;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class PdfWithSvgTest extends TestCaseBase {

    @Test
    public void testSimpleSvgInPdf() throws Exception {
        String svgImage = resourceFile("svg/aa.svg");
        String destination = targetFile("svg-SimpleSvgInPdf.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(160f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));

        PdfFormXObject xObject = SvgConverter.convertToXObject(new FileInputStream(new File(svgImage)), pdfDocument);

        // create page canvas
        PdfCanvas pdfCanvas = new PdfCanvas(page);

        // create AT
        AffineTransform at = AffineTransform.getTranslateInstance(margin, margin);
        at.concatenate(AffineTransform.getScaleInstance(dspWidth / xObject.getWidth(), dspHeight / xObject.getHeight()));

        float[] matrix = new float[6];
        at.getMatrix(matrix);

        // add cropped image
        pdfCanvas.addXObjectWithTransformationMatrix(xObject, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        pdfCanvas.release();
        pdfDocument.close();

    }


    /**
     * Replace the existing fill color in svg with a icc-color.
     * However, the color space of the object in PDF is DeviceRGB, need to improvement (TODO)
     * @throws Exception
     */
    @Test
    public void testChangeSvgColorInPdf() throws Exception {
        String svgImage = resourceFile("svg/aa.svg");
        String destination = targetFile("svg-ChangeSvgColorInPdf.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(160f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));

        Map<String, String> colorReplacement = new HashMap<>();
        colorReplacement.put("#2a376e", "#8F6102 icc-color(#CMYK, 26%, 50%, 99%, 24%)"); // CMYK
        colorReplacement.put("#bf2426", "rgb(0,151,57) icc-color(#SpotColor, 'PANTONE  355 C', 1.0)"); // Spot

        String svgContent = new String(Files.readAllBytes(Paths.get(svgImage)), StandardCharsets.UTF_8);
        for(String key: colorReplacement.keySet()) {
            svgContent = svgContent.replace(key, colorReplacement.get(key));
        }

        PdfFormXObject xObject = SvgConverter.convertToXObject(svgContent, pdfDocument);

        // create page canvas
        PdfCanvas pdfCanvas = new PdfCanvas(page);

        // create AT
        AffineTransform at = AffineTransform.getTranslateInstance(margin, margin);
        at.concatenate(AffineTransform.getScaleInstance(dspWidth / xObject.getWidth(), dspHeight / xObject.getHeight()));

        float[] matrix = new float[6];
        at.getMatrix(matrix);

        // add cropped image
        pdfCanvas.addXObjectWithTransformationMatrix(xObject, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        pdfCanvas.release();
        pdfDocument.close();

    }





}
