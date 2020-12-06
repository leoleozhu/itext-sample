package com.leoleozhu.itextimage;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.leoleozhu.utils.TestCaseBase;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class ImageMaskTest extends TestCaseBase {

    @Test
    public void testPngMaskOnJpeg() throws Exception {
        String jpeg = resourceFile("images/image-ios-profile.jpg");
        String mask = resourceFile("masks/spark.png");
        String destination = targetFile("image-mask-PngMaskOnJpeg.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(160f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        ImageData assetImageData = ImageDataFactory.create(jpeg);

        ImageData maskImageData = makeBlackAndWhitePng(mask);
        maskImageData.makeMask();

        PdfPage page1 = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));
        displayImageAndMaskOnPage(assetImageData, null, margin, margin, dspWidth, dspHeight, page1);

        PdfPage page2 = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));
        displayImageAndMaskOnPage(assetImageData, maskImageData, margin, margin, dspWidth, dspHeight, page2);

        pdfDocument.close();
    }


    /**
     * Please be noted that part of the png is display black in this example
     */
    @Test
    public void testPngMaskOnPng() throws Exception {
        String png = resourceFile("png/PNG_transparency_demonstration_1.png");
        String mask = resourceFile("masks/spark.png");
        String destination = targetFile("image-mask-PngMaskOnPng.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(160f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        ImageData assetImageData = ImageDataFactory.create(png);

        ImageData maskImageData = makeBlackAndWhitePng(mask);
        maskImageData.makeMask();

        PdfPage page1 = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));
        displayImageAndMaskOnPage(assetImageData, null, margin, margin, dspWidth, dspHeight, page1);

        PdfPage page2 = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));
        displayImageAndMaskOnPage(assetImageData, maskImageData, margin, margin, dspWidth, dspHeight, page2);

        pdfDocument.close();
    }


    @Test
    public void testPngMaskOnPngFix() throws Exception {
        String png = resourceFile("png/PNG_transparency_demonstration_1.png");
        String mask = resourceFile("masks/spark.png");
        String destination = targetFile("image-mask-PngMaskOnPng-Fix.pdf");

        float margin = mm2pt(10f);
        float dspWidth = mm2pt(160f);
        float dspHeight = mm2pt(160f);
        float pageWidth = dspWidth + margin * 2;
        float pageHeight = dspHeight + margin * 2;

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        ImageData assetImageData = getImageDataWithMask(png, null);

        ImageData assetImageDataWithMask = getImageDataWithMask(png, mask);

        PdfPage page1 = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));
        displayImageAndMaskOnPage(assetImageData, null, margin, margin, dspWidth, dspHeight, page1);

        PdfPage page2 = pdfDocument.addNewPage(new PageSize(pageWidth, pageHeight));
        displayImageAndMaskOnPage(assetImageDataWithMask, null, margin, margin, dspWidth, dspHeight, page2);

        pdfDocument.close();
    }


    private void displayImageAndMaskOnPage(ImageData imageData, ImageData maskData, float x, float y, float w, float h, PdfPage page) {

        // create mask
        if (maskData != null) {
            // apply mask to image data
            imageData.setImageMask(maskData);
        }

        float imgWidth = imageData.getWidth();
        float imgHeight = imageData.getHeight();

        float dspOffsetX = (w / h > imgWidth / imgHeight) ?
                (w - (h / imgHeight * imgWidth)) / 2 : 0;

        float dspOffsetY = (w / h < imgWidth / imgHeight) ?
                (h - (w / imgWidth * imgHeight)) / 2 : 0;

        // create page canvas
        PdfCanvas pdfCanvas = new PdfCanvas(page);

        // draw a rect
        pdfCanvas.rectangle(page.getPageSize())
                .setFillColorGray(0.6f)
                .fill();

        // create AT
        AffineTransform at = AffineTransform.getTranslateInstance(x + dspOffsetX, y + dspOffsetY);
        at.concatenate(AffineTransform.getScaleInstance(w - 2 * dspOffsetX, h - 2 * dspOffsetY));

        float[] matrix = new float[6];
        at.getMatrix(matrix);

        // add image data
        pdfCanvas.addImageWithTransformationMatrix(imageData, matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);

        pdfCanvas.release();
    }


    /**
     * Make image mask data from iText website
     *
     * @param image mask path
     * @return Mask image data
     * @throws IOException
     */
    private static ImageData makeBlackAndWhitePng(String image) throws IOException {
        BufferedImage bi = ImageIO.read(new File(image));
        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        newBi.getGraphics().drawImage(bi, 0, 0, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newBi, "png", os);
        return ImageDataFactory.create(os.toByteArray());
    }

    /**
     * Get the image data and also apply a mask to the input image.
     * This will re-use the alpha component from the input image.
     *
     * @param imagePath The path of original image where the mask will apply to
     * @param maskPath  The path of the mask
     * @return The image data of the input image, with the mask applied
     * @throws IOException
     */
    private static ImageData getImageDataWithMask(String imagePath, String maskPath) throws IOException {

        // no image
        if (StringUtils.isEmpty(imagePath)) {
            return null;
        }

        ImageData originalImageData = ImageDataFactory.create(imagePath);

        // no mask
        if (StringUtils.isEmpty(maskPath)) {
            return originalImageData;
        }

        BufferedImage maskBufferedImage = ImageIO.read(new File(maskPath));

        // mask size error
        if (maskBufferedImage.getWidth() <= 0 || maskBufferedImage.getHeight() <= 0) {
            return originalImageData;
        }

        // no alpha in mask image
        if (!maskBufferedImage.getColorModel().hasAlpha()) {
            return originalImageData;
        }

        // Get mask data
        int[][] maskData;

        if (originalImageData.getImageMask() == null) {
            // original image without mask, so we create mask data from the mask image
            maskData = getMaskAlpha(maskBufferedImage);
        } else {
            // original image also contains a mask, we will combine them
            BufferedImage originalBufferedImage = ImageIO.read(new File(imagePath));
            maskData = getMaskAlpha(originalBufferedImage, maskBufferedImage);
        }

//        // create mask buffer image
//        BufferedImage mask = new BufferedImage(maskData[0].length, maskData.length, BufferedImage.TYPE_BYTE_GRAY);
//        ColorModel maskColorModel = mask.getColorModel();
//        for (int y = 0; y < mask.getHeight(); y++) {
//            for (int x = 0; x < mask.getWidth(); x++) {
//                mask.setRGB(x, y, maskColorModel.getRGB(maskData[y][x]));

        int maskHeight = maskData.length;
        int maskWidth = maskData[0].length;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        for(int y = 0; y < maskData.length; y++) {
            for(int x= 0; x < maskData[y].length; x++) {
                byteArrayOutputStream.write(maskData[y][x]);
            }
        }

        BufferedImage mask = new BufferedImage(maskWidth, maskHeight, BufferedImage.TYPE_BYTE_GRAY);
        mask.getRaster().setDataElements(0, 0, maskWidth, maskHeight, byteArrayOutputStream.toByteArray());

        // create image data from mask buffer image
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(mask, "png", os);

        ImageData maskImageData = ImageDataFactory.create(os.toByteArray());
        // make it a mask
        maskImageData.makeMask();

        // apply mask
        originalImageData.setImageMask(maskImageData);

        return originalImageData;
    }

    private static int[][] getMaskAlpha(BufferedImage mask) {

        final int w = mask.getWidth();
        final int h = mask.getHeight();
        int[][] maskData = new int[h][w];

        for (int y = 0; y < maskData.length; y++) {
            for (int x = 0; x < maskData[y].length; x++) {
                maskData[y][x] = new Color(mask.getRGB(x, y), true).getAlpha();
            }
        }

        return maskData;
    }

    private static int[][] getMaskAlpha(BufferedImage mask1, BufferedImage mask2) {

        int[][] maskData1 = getMaskAlpha(mask1);
        int[][] maskData2 = getMaskAlpha(mask2);

        final int h1 = maskData1.length;
        final int w1 = maskData1[0].length;
        final int h2 = maskData2.length;
        final int w2 = maskData2[0].length;

        int x2, y2;

        for (int y1 = 0; y1 < h1; y1++) {
            y2 = Math.floorDiv(y1 * h2, h1);
            for (int x1 = 0; x1 < maskData1[y1].length; x1++) {
                x2 = Math.floorDiv(x1 * w2, w1);
                maskData1[y1][x1] = Math.floorDiv(maskData1[y1][x1] * maskData2[y2][x2], 255);
            }
        }

        return maskData1;
    }

}
