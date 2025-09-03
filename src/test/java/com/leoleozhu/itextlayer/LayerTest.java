package com.leoleozhu.itextlayer;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.*;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.function.PdfFunction;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.leoleozhu.utils.Geo;
import com.leoleozhu.utils.TestCaseBase;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class LayerTest extends TestCaseBase {


    private PdfFont font;
    private int fontsize;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        try (InputStream fontInputStream = new FileInputStream(resourceFile("fonts/SourceHanSansCN-Bold.otf"))) {
            ByteArrayOutputStream fontStream = new ByteArrayOutputStream();
            IOUtils.copy(fontInputStream, fontStream);
            font = PdfFontFactory.createFont(fontStream.toByteArray(), PdfEncodings.IDENTITY_H);
        }

        fontsize = 12;
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

    @Test
    public void testMultipleLayerPdf() throws Exception {
        String destination = targetFile("layer-MultipleLayerPdf.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        String jpeg = resourceFile("images/image-ios-profile.jpg");
        String mask = resourceFile("masks/circle.png");

        String artImg = resourceFile("images/Artwork.png");
        String whiteImg = resourceFile("images/Artwork_Yellow.png");
        // layers
        PdfLayer backgroundLayer = new PdfLayer("Background", pdfDocument);
        PdfLayer shapeLayer = new PdfLayer("Shape", pdfDocument);
        PdfLayer whiteLayer = new PdfLayer("White", pdfDocument);
        PdfLayer artLayer = new PdfLayer("Art", pdfDocument);
        PdfLayer textLayer = new PdfLayer("Text", pdfDocument);

        Color bg = new DeviceGray(0.5f);
        Color white = createSpotColor("WHITE", new DeviceRgb(0, 138, 0));
        Color gold = createSpotColor("GOLD", new DeviceRgb(235, 137, 43));
        Color textColor = new DeviceCmyk(0, 1, 0, 0f);

        PageSize pageSize = new PageSize(mm2pt(160f), mm2pt(180f));
        float textBoxHeight = mm2pt(20f);
        Rectangle imageBox = new Rectangle(0, textBoxHeight, pageSize.getWidth(), pageSize.getHeight() - textBoxHeight);
        Rectangle textBox = new Rectangle(mm2pt(0), mm2pt(0), pageSize.getWidth(), textBoxHeight);

        // Draw a picture with mask
        {
            PdfPage page = pdfDocument.addNewPage(pageSize);
            addBackgroundToPageLayer(page, backgroundLayer, bg);
            addTextToPageLayer(page, textLayer, textBox, "Image with Mask Image", textColor);

            // Image and Mask
            ImageData assetImageData = ImageDataFactory.create(jpeg);
            ImageData maskImageData = createOpaqueBitmask(mask, true);
            maskImageData.makeMask();
            assetImageData.setImageMask(maskImageData);

            PdfImageXObject imageXObject = new PdfImageXObject(assetImageData);

            drawImage(page, artLayer, imageBox, imageXObject, false);
        }

        // Draw a picture with white spot from the image itself
        {
            PdfPage page = pdfDocument.addNewPage(pageSize);
            addBackgroundToPageLayer(page, backgroundLayer, bg);
            addTextToPageLayer(page, textLayer, textBox, "Image with generated White Spot from itself", textColor);

            // Create white image and also use the same object as a mask
            ImageData whiteImageData = createOpaqueBitmask(artImg, true);
            ImageData whiteImageDataAsMask = createOpaqueBitmask(artImg, true);
            whiteImageDataAsMask.makeMask();
            whiteImageData.setImageMask(whiteImageDataAsMask);
            PdfImageXObject whiteImageXObject = new PdfImageXObject(whiteImageData);
            // set white spot
            whiteImageXObject.put(PdfName.ColorSpace, white.getColorSpace().getPdfObject());

            ImageData artImageData = ImageDataFactory.create(artImg);
            PdfImageXObject artImageXObject = new PdfImageXObject(artImageData);

            // overPrint property for WHITE object is not important, since normally it's the backend object
            drawImage(page, whiteLayer, imageBox, whiteImageXObject, false);
            // draw art image above WHITE object (without overPrint property, the white will not be printed)
            drawImage(page, artLayer, imageBox, artImageXObject, true);
        }

        // Draw a picture with external white spot
        {
            PdfPage page = pdfDocument.addNewPage(pageSize);
            addBackgroundToPageLayer(page, backgroundLayer, bg);
            addTextToPageLayer(page, textLayer, textBox, "Image with extra White Spot", textColor);

            // external white img
            ImageData whiteImageData = createOpaqueBitmask(whiteImg, false);
            ImageData whiteImageDataAsMask = createOpaqueBitmask(whiteImg, false);
            whiteImageDataAsMask.makeMask();
            whiteImageData.setImageMask(whiteImageDataAsMask);
            PdfImageXObject whiteImageXObject = new PdfImageXObject(whiteImageData);
            // set white spot
            whiteImageXObject.put(PdfName.ColorSpace, white.getColorSpace().getPdfObject());

            ImageData artImageData = ImageDataFactory.create(artImg);
            PdfImageXObject artImageXObject = new PdfImageXObject(artImageData);

            drawImage(page, whiteLayer, imageBox, whiteImageXObject, false);
            drawImage(page, artLayer, imageBox, artImageXObject, true);
        }

        // Draw a Rect
        {
            PdfPage page = pdfDocument.addNewPage(pageSize);
            addBackgroundToPageLayer(page, backgroundLayer, bg);
            addTextToPageLayer(page, textLayer, textBox, "Draw a box", textColor);

            addRectToPageLayer(page, shapeLayer, imageBox, gold, false);
        }


        // close doc
        pdfDocument.close();
    }


    private void addBackgroundToPageLayer(PdfPage page, PdfLayer layer, Color color) {

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.saveState();
        /// BEGIN Operations
        pdfCanvas.beginLayer(layer);

        pdfCanvas.setFillColor(color)
                .setStrokeColor(color);

        pdfCanvas.setLineWidth(0f);

        Rectangle pageRect = page.getPageSize();

        pdfCanvas.rectangle(
                        pageRect.getX(),
                        pageRect.getY(),
                        pageRect.getWidth(),
                        pageRect.getHeight())
                .fill();

        pdfCanvas.endLayer();
        /// END Operations
        pdfCanvas.restoreState();
        pdfCanvas.release();
    }

    /**
     * Creates a bitmask image where opaque pixels are 1 and transparent pixels are 0.
     *
     * @param imagePath              Path to the original image.
     * @param includeSemiTransparent Include pixels which are semi transparent
     * @return ImageData representing the bitmask image.
     * @throws IOException If an error occurs while reading or writing the image.
     */
    private static ImageData createOpaqueBitmask(String imagePath, boolean includeSemiTransparent) throws IOException {
        BufferedImage originalImage = ImageIO.read(new File(imagePath));
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Create a new BufferedImage for the bitmask
        BufferedImage bitmaskImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixel = originalImage.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xFF; // Extract alpha channel

                // Set pixel to white (1) if opaque, black (0) if transparent
                int bitmaskValue = (alpha == 255 || (alpha > 0 && includeSemiTransparent)) ? 0xFFFFFF : 0x000000;
                bitmaskImage.setRGB(x, y, bitmaskValue);
            }
        }

        // Convert the bitmask image to ImageData
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bitmaskImage, "png", os);
        return ImageDataFactory.create(os.toByteArray());
    }

    private void drawImage(
            PdfPage page,
            PdfLayer layer,
            Rectangle box,
            PdfImageXObject imageXObject,
            boolean overPrint) {

        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.saveState();
        /// BEGIN Operations
        pdfCanvas.beginLayer(layer);

        if (overPrint) {
            // Draw whiteData with the specified color
            PdfExtGState state = new PdfExtGState();
            state.setFillOverPrintFlag(true);
            state.setStrokeOverPrintFlag(true);
            pdfCanvas.setExtGState(state);
        }


        Rectangle targetRect = Geo.getRectToPlaceContentCenterAndKeepAspectRatio(box,
                imageXObject.getWidth(), imageXObject.getHeight());

        // Create transformation matrix for imageXObject
        AffineTransform atArt = AffineTransform.getTranslateInstance(targetRect.getX(), targetRect.getY());
        atArt.concatenate(AffineTransform.getScaleInstance(targetRect.getWidth(), targetRect.getHeight()));
        float[] matrixArt = new float[6];
        atArt.getMatrix(matrixArt);
        // draw imageXObject
        pdfCanvas.addXObjectWithTransformationMatrix(imageXObject, matrixArt[0], matrixArt[1], matrixArt[2], matrixArt[3], matrixArt[4], matrixArt[5]);

        pdfCanvas.endLayer();
        /// END Operations
        pdfCanvas.restoreState();
        pdfCanvas.release();
    }

    private void addRectToPageLayer(PdfPage page, PdfLayer layer, Rectangle rect, Color color, boolean overPrint) {
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.saveState();
        /// BEGIN Operations
        pdfCanvas.beginLayer(layer);

        pdfCanvas.setFillColor(color)
                .setStrokeColor(color);

        if (overPrint) {
            PdfExtGState state = new PdfExtGState();
            state.setFillOverPrintFlag(true);
            state.setStrokeOverPrintFlag(true);
            pdfCanvas.setExtGState(state);
        }

        pdfCanvas.rectangle(rect).setLineWidth(mm2pt(1)).fillStroke();

        pdfCanvas.endLayer();

        /// END Operations
        pdfCanvas.restoreState();
        pdfCanvas.release();
    }

    private void addTextToPageLayer(PdfPage page, PdfLayer layer, Rectangle textBox, String text, Color color) {
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.saveState();
        /// BEGIN Operations
        pdfCanvas.beginLayer(layer);

        pdfCanvas.setFillColor(color)
                .setStrokeColor(color);

        Rectangle pageRect = page.getPageSize();

        float marginX = (float) fontsize * 5;
        float marginY = (textBox.getHeight() - fontsize) / 2;

        pdfCanvas.beginText().moveText(textBox.getX() + marginX, pageRect.getY() + marginY)
                .setFontAndSize(font, fontsize)
                .showText(text)
                .endText();

        pdfCanvas.endLayer();

        /// END Operations
        pdfCanvas.restoreState();
        pdfCanvas.release();
    }
}
