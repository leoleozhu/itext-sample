package com.leoleozhu.itextimage;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.leoleozhu.utils.TestCaseBase;
import org.junit.Test;

public class ImageReuseTest extends TestCaseBase {

    @Test
    public void testReusePage() throws Exception {
        String original = resourceFile("pdf/PdfWithImage.pdf");
        String destination = targetFile("image-reuse-ReusePage.pdf");

        try (
                PdfDocument input = new PdfDocument(new PdfReader(original));
                PdfDocument output = new PdfDocument(new PdfWriter(destination))
        ) {
            PdfPage page = input.getPage(1);

            for (int i = 0; i < 100; i++) {
                PdfPage newPage = output.addNewPage(new PageSize(page.getPageSize()));

                // copy boxes
                newPage.setMediaBox(page.getMediaBox());
                newPage.setTrimBox(page.getTrimBox());
                newPage.setBleedBox(page.getBleedBox());
                newPage.setArtBox(page.getArtBox());
                newPage.setCropBox(page.getCropBox());

                PdfCanvas newCanvas = new PdfCanvas(newPage);
                newCanvas.addXObject(page.copyAsFormXObject(output));
                newCanvas.release();
            }
        }

    }


    @Test
    public void testSameImageInPages() throws Exception {

        String imagePath = resourceFile("images/image-ios-profile.jpg");
        String destination = targetFile("image-reuse-SameImageInPages.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(120f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        ImageData imageData = ImageDataFactory.create(imagePath);
        float imgWidth = imageData.getWidth();
        float imgHeight = imageData.getHeight();

        float dspOffsetX = (dspWidth / dspHeight > imgWidth / imgHeight) ?
                (dspWidth - (dspHeight / imgHeight * imgWidth)) / 2 : 0;

        float dspOffsetY = (dspWidth / dspHeight < imgWidth / imgHeight) ?
                (dspHeight - (dspWidth / imgWidth * imgHeight)) / 2 : 0;

        PdfImageXObject imageXObject = new PdfImageXObject(imageData);

        for (int i = 0; i < 100; i++) {
            addImageIntoNewPage(pdfDocument, imageXObject, pageWidth, pageHeight, margin, dspWidth, dspHeight, dspOffsetX, dspOffsetY);
        }
        pdfDocument.close();
    }


    private PdfPage addImageIntoNewPage(PdfDocument pdfDocument, PdfImageXObject imageXObject,
                                     float pageWidth, float pageHeight,
                                     float margin, float dspWidth, float dspHeight, float dspOffsetX, float dspOffsetY) {

        PdfPage page = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));

        // create page canvas
        PdfCanvas pdfCanvas = new PdfCanvas(page);

        // draw a rect
        pdfCanvas.rectangle(new Rectangle(margin, margin, dspWidth, dspHeight))
                .setFillColorGray(0.6f)
                .fill();

        // create AT
        AffineTransform at = AffineTransform.getTranslateInstance(margin + dspOffsetX, margin + dspOffsetY);
        at.concatenate(AffineTransform.getScaleInstance(dspWidth - 2 * dspOffsetX, dspHeight - 2 * dspOffsetY));

        float[] matrix = new float[6];
        at.getMatrix(matrix);

        // add image data
        pdfCanvas.addXObjectWithTransformationMatrix(imageXObject, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        pdfCanvas.release();
        return page;
    }
}
