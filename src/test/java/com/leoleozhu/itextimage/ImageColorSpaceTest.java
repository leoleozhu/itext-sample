package com.leoleozhu.itextimage;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.leoleozhu.utils.TestCaseBase;
import org.junit.Test;

public class ImageColorSpaceTest extends TestCaseBase {

    /**
     * Add the image to the center of the display area.
     *
     * @throws Exception
     */
    @Test
    public void testAddImageToPdfWithPdfCanvas() throws Exception {

        String imageWithIcc = resourceFile("images/image-ios-profile.jpg");
        String destination = targetFile("image-colorspace-AddImageToPdfWithPdfCanvas.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(160f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));

        ImageData imageData = ImageDataFactory.create(imageWithIcc);

        float imgWidth = imageData.getWidth();
        float imgHeight = imageData.getHeight();

        float dspOffsetX = (dspWidth / dspHeight > imgWidth / imgHeight) ?
                (dspWidth - (dspHeight / imgHeight * imgWidth)) / 2 : 0;

        float dspOffsetY = (dspWidth / dspHeight < imgWidth / imgHeight) ?
                (dspHeight - (dspWidth / imgWidth * imgHeight)) / 2 : 0;

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
        pdfCanvas.addImageWithTransformationMatrix(imageData, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        pdfCanvas.release();
        pdfDocument.close();
    }


    /**
     * Crop center of the image with the display area and add to pdf page
     *
     * @throws Exception
     */
    @Test
    public void testAddImageToPdfWithPdfCanvasCropCenter() throws Exception {

        String imageWithIcc = resourceFile("images/image-ios-profile.jpg");
        String destination = targetFile("image-colorspace-AddImageToPdfWithPdfCanvasCropCenter.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(160f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));
        PdfPage page = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));

        // create cropped image xObject
        ImageData imageData = ImageDataFactory.create(imageWithIcc);
        Image img = new Image(imageData);

        float imgWidth = imageData.getWidth();
        float imgHeight = imageData.getHeight();
        float cropX, cropY, cropWidth, cropHeight;

        if (imgWidth / dspWidth > imgHeight / dspHeight) {
            cropHeight = imgHeight;
            cropWidth = imgHeight / dspHeight * dspWidth;
            cropX = (imgWidth - cropWidth) / 2;
            cropY = 0;
        } else {
            cropWidth = imgWidth;
            cropHeight = imgWidth / dspWidth * dspHeight;
            cropX = 0;
            cropY = (imgHeight - cropHeight) / 2;
        }

        img.setFixedPosition(-cropX, -cropY);

        PdfFormXObject croppedImage = new PdfFormXObject(new Rectangle(cropWidth, cropHeight));

        Canvas canvas = new Canvas(croppedImage, pdfDocument);
        canvas.add(img);

        // create page canvas
        PdfCanvas pdfCanvas = new PdfCanvas(page);

        // create AT
        AffineTransform at = AffineTransform.getTranslateInstance(margin, margin);
        at.concatenate(AffineTransform.getScaleInstance(dspWidth / croppedImage.getWidth(), dspHeight / croppedImage.getHeight()));

        float[] matrix = new float[6];
        at.getMatrix(matrix);

        // add cropped image
        pdfCanvas.addXObjectWithTransformationMatrix(croppedImage, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        pdfCanvas.release();
        pdfDocument.close();
    }

}
