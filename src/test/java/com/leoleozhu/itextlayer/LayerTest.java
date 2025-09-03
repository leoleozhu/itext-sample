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
        String text = "Hello, This text is in a txt layer!";

        String destination = targetFile("layer-MultipleLayerPdf.pdf");
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        String jpeg = resourceFile("images/image-ios-profile.jpg");
        String mask = resourceFile("masks/spark.png");

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
            ImageData maskImageData = makeBlackAndWhitePng(mask);
            maskImageData.makeMask();
            assetImageData.setImageMask(maskImageData);

            PdfImageXObject imageXObject = new PdfImageXObject(assetImageData);
            drawImage(page, whiteLayer, imageBox, imageXObject, false);
        }


        // Draw a picture with white spot
        {
            PdfPage page = pdfDocument.addNewPage(pageSize);
            addBackgroundToPageLayer(page, backgroundLayer, bg);
            addTextToPageLayer(page, textLayer, textBox, "Image with White Spot behind", textColor);

            ImageData artImageData = ImageDataFactory.create(artImg);
            PdfImageXObject artImageXObject = new PdfImageXObject(artImageData);

            ImageData whiteImageData = makeBlackAndWhitePng(whiteImg);
            PdfImageXObject whiteImageXObject = new PdfImageXObject(whiteImageData);
            // set white spot
            whiteImageXObject.put(PdfName.ColorSpace, white.getColorSpace().getPdfObject());
            whiteImageXObject.makeIndirect(pdfDocument);

            drawImage(page, whiteLayer, imageBox, whiteImageXObject, false);
            drawImage(page, artLayer, imageBox, artImageXObject, true);
        }

        // Draw a picture with white spot / with a mask
        {
            PdfPage page = pdfDocument.addNewPage(pageSize);
            addBackgroundToPageLayer(page, backgroundLayer, bg);
            addTextToPageLayer(page, textLayer, textBox, "Image with White Spot behind and with a Mask", textColor);

            ImageData artImageData = ImageDataFactory.create(artImg);
            ImageData maskImageData = makeBlackAndWhitePng(mask);
            maskImageData.makeMask();
            artImageData.setImageMask(maskImageData);
            PdfImageXObject artImageXObject = new PdfImageXObject(artImageData);

            ImageData whiteImageData = makeBlackAndWhitePng(whiteImg);
            whiteImageData.setImageMask(maskImageData);
            PdfImageXObject whiteImageXObject = new PdfImageXObject(whiteImageData);
            // set white spot
            whiteImageXObject.put(PdfName.ColorSpace, white.getColorSpace().getPdfObject());
            whiteImageXObject.makeIndirect(pdfDocument);

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

    private static ImageData makeBlackAndWhitePng(String image) throws IOException {
        BufferedImage bi = ImageIO.read(new File(image));
        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        newBi.getGraphics().drawImage(bi, 0, 0, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newBi, "png", os);
        return ImageDataFactory.create(os.toByteArray());
    }

    private static ImageData makeImageSpot(String image) throws IOException {
        BufferedImage bi = ImageIO.read(new File(image));
        BufferedImage newBi = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        bi.getTransparency();
        newBi.getGraphics().drawImage(bi, 0, 0, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(newBi, "png", os);
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

    private void addRectToPageLayer(PdfPage page, PdfLayer layer, Rectangle rect, Color color, boolean overprint) {
        PdfCanvas pdfCanvas = new PdfCanvas(page);
        pdfCanvas.saveState();
        /// BEGIN Operations
        pdfCanvas.beginLayer(layer);

        pdfCanvas.setFillColor(color)
                .setStrokeColor(color);

        if (overprint) {
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
