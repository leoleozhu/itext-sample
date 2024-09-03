package com.leoleozhu.pdfboxtest.image;

import com.leoleozhu.utils.TestCaseBase;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.IOException;

public class PdfWithImageTest extends TestCaseBase {

    @Test
    public void testCross() throws Exception {
        String destination = targetFile("PdfBox-image-cross.pdf");

        try (PDDocument document = new PDDocument()) {


            PDPage page = new PDPage(new PDRectangle(0, 0, mm2pt(3), mm2pt(3)));
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.setLineWidth(1f);

                contentStream.drawLine(0, mm2pt(1.5f), mm2pt(3), mm2pt(1.5f));

                contentStream.drawLine(mm2pt(1.5f), 0, mm2pt(1.5f), mm2pt(3));

                contentStream.fillAndStroke();
            }

            document.save(new File(destination));
        }
    }


    @Test
    public void testCardsWithDiffSize() throws Exception {
        String image = resourceFile("images/cmyk.jpg");

        float[][] sizes = {
                {
                        140.1f, 90.0f, 0f, 0f
                }, {
                39.9f, 90.0f, 0f, 0f
        }, {
                340.1f, 90.0f, 0f, 0f
        }, {
                99.8f, 90.0f, 0f, 0f
        }, {
                610.0f, 90.0f, 0f, 0f
        }, {
                190.1f, 90.0f, 0f, 0f
        }, {
                239.9f, 90.0f, 0f, 0f
        }, {
                30.0f, 90.0f, 0f, 0f
        }, {
                99.8f, 90.0f, 0f, 0f
        }, {
                90.0f, 90.0f, 0f, 0f
        }, {
                119.9f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                39.9f, 90.0f, 0f, 0f
        }, {
                30.0f, 90.0f, 0f, 0f
        }, {
                30.0f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        }, {
                140.1f, 90.0f, 0f, 0f
        },
        };


        for (float[] size : sizes) {
            try (PDDocument document = new PDDocument()) {
                String destination = targetFile(String.format("card (%.1fx%f)-bleed(%.1fmm)-out(%.1fmm).pdf", size[0], size[1], size[2], size[3]));

                PDImageXObject pdImage = PDImageXObject.createFromFile(image, document);

                float margin = mm2pt(size[3]);
                float bleed = mm2pt(size[2]);
                float trimX = margin + bleed;
                float trimY = margin + bleed;
                float trimWidth = mm2pt(size[0]);
                float trimHeight = mm2pt(size[1]);

                float bleedWidth = trimWidth + bleed * 2;
                float bleedHeight = trimHeight + bleed * 2;

                float pageWidth = trimWidth + margin * 2 + bleed * 2;
                float pageHeight = trimHeight + margin * 2 + bleed * 2;

                PDPage page = new PDPage(new PDRectangle(0, 0, pageWidth, pageHeight));
                document.addPage(page);

                page.setArtBox(new PDRectangle(margin, margin, bleedWidth, bleedHeight));
                page.setBleedBox(new PDRectangle(margin, margin, bleedWidth, bleedHeight));
                page.setTrimBox(new PDRectangle(trimX, trimY, trimWidth, trimHeight));

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setNonStrokingColor(Color.lightGray);
                    contentStream.addRect(0, 0, pageWidth, pageHeight);
                    contentStream.fill();

                    contentStream.setNonStrokingColor(Color.GRAY);
                    contentStream.addRect(margin, margin, bleedWidth, bleedHeight);
                    contentStream.fill();

                    contentStream.setNonStrokingColor(Color.yellow);
                    contentStream.addRect(margin, margin, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(margin + bleedWidth - mm2pt(1f), margin, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(margin + bleedWidth - mm2pt(1f), margin + bleedHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(margin, margin + bleedHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.fill();

                    // image
                    AffineTransform at = AffineTransform.getTranslateInstance(margin + bleed, margin + bleed);
                    at.concatenate(AffineTransform.getScaleInstance(trimWidth, trimHeight));
                    contentStream.drawImage(pdImage, new Matrix(at));

                    contentStream.setNonStrokingColor(Color.green);
                    contentStream.addRect(trimX, trimY, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth - mm2pt(1f), trimY, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth - mm2pt(1f), trimY + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX, trimY + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.fill();

                    contentStream.setNonStrokingColor(Color.blue);
                    contentStream.addRect(trimX - mm2pt(3), trimY - mm2pt(3), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth + mm2pt(3) - mm2pt(1f), trimY - mm2pt(3), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth + mm2pt(3) - mm2pt(1f), trimY + mm2pt(3) + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX - mm2pt(3), trimY + mm2pt(3) + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.fill();
                }

                document.save(new File(destination));
            }

        }
    }

    @Test
    public void testBizCardWithDifferentBleeds() throws Exception {
        String image = resourceFile("images/cmyk.jpg");

        float[][] sizes = {
                {
                        70f, 50f, 3f, 0f
                },
                {
                        70f, 50f, 3f, 0f
                }, {
                70f, 50f, 5f, 0f
        }, {
                70f, 50f, 10f, 0f
        },
                {
                        70f, 50f, 3f, 10f
                },
                {
                        70f, 50f, 3f, 10f
                }, {
                70f, 50f, 5f, 10f
        }, {
                70f, 50f, 10f, 10f
        },
        };


        for (float[] size : sizes) {
            try (PDDocument document = new PDDocument()) {
                String destination = targetFile(String.format("card (%.1fx%f)-bleed(%.1fmm)-out(%.1fmm).pdf", size[0], size[1], size[2], size[3]));

                PDImageXObject pdImage = PDImageXObject.createFromFile(image, document);

                float margin = mm2pt(size[3]);
                float bleed = mm2pt(size[2]);
                float trimX = margin + bleed;
                float trimY = margin + bleed;
                float trimWidth = mm2pt(size[0]);
                float trimHeight = mm2pt(size[1]);

                float bleedWidth = trimWidth + bleed * 2;
                float bleedHeight = trimHeight + bleed * 2;

                float pageWidth = trimWidth + margin * 2 + bleed * 2;
                float pageHeight = trimHeight + margin * 2 + bleed * 2;

                PDPage page = new PDPage(new PDRectangle(0, 0, pageWidth, pageHeight));
                document.addPage(page);

                page.setArtBox(new PDRectangle(margin, margin, bleedWidth, bleedHeight));
                page.setBleedBox(new PDRectangle(margin, margin, bleedWidth, bleedHeight));
                page.setTrimBox(new PDRectangle(trimX, trimY, trimWidth, trimHeight));

                try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                    contentStream.setNonStrokingColor(Color.lightGray);
                    contentStream.addRect(0, 0, pageWidth, pageHeight);
                    contentStream.fill();

                    contentStream.setNonStrokingColor(Color.GRAY);
                    contentStream.addRect(margin, margin, bleedWidth, bleedHeight);
                    contentStream.fill();

                    contentStream.setNonStrokingColor(Color.yellow);
                    contentStream.addRect(margin, margin, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(margin + bleedWidth - mm2pt(1f), margin, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(margin + bleedWidth - mm2pt(1f), margin + bleedHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(margin, margin + bleedHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.fill();

                    // image
                    AffineTransform at = AffineTransform.getTranslateInstance(margin + bleed, margin + bleed);
                    at.concatenate(AffineTransform.getScaleInstance(trimWidth, trimHeight));
                    contentStream.drawImage(pdImage, new Matrix(at));

                    contentStream.setNonStrokingColor(Color.green);
                    contentStream.addRect(trimX, trimY, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth - mm2pt(1f), trimY, mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth - mm2pt(1f), trimY + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX, trimY + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.fill();

                    contentStream.setNonStrokingColor(Color.blue);
                    contentStream.addRect(trimX - mm2pt(3), trimY - mm2pt(3), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth + mm2pt(3) - mm2pt(1f), trimY - mm2pt(3), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX + trimWidth + mm2pt(3) - mm2pt(1f), trimY + mm2pt(3) + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.addRect(trimX - mm2pt(3), trimY + mm2pt(3) + trimHeight - mm2pt(1f), mm2pt(1f), mm2pt(1f));
                    contentStream.fill();
                }

                document.save(new File(destination));
            }

        }
    }

    @Test
    public void testPositionImageInPage() throws Exception {
        String imageWithIcc = resourceFile("images/image-ios-profile.jpg");
        String destination = targetFile("PdfBox-image-PositionImageInPage.pdf");

        try (PDDocument document = new PDDocument()) {

            PDImageXObject pdImage = PDImageXObject.createFromFile(imageWithIcc, document);

            float imgWidth = pdImage.getWidth();
            float imgHeight = pdImage.getHeight();

            float margin = mm2pt(10);
            float dspWidth = mm2pt(160f);
            float dspHeight = mm2pt(160f);
            float pageWidth = dspWidth + margin * 2;
            float pageHeight = dspHeight + margin * 2;

            float dspOffsetX = (dspWidth / dspHeight > imgWidth / imgHeight) ?
                    (dspWidth - (dspHeight / imgHeight * imgWidth)) / 2 : 0;

            float dspOffsetY = (dspWidth / dspHeight < imgWidth / imgHeight) ?
                    (dspHeight - (dspWidth / imgWidth * imgHeight)) / 2 : 0;

            PDPage page = new PDPage(new PDRectangle(0, 0, pageWidth, pageHeight));
            document.addPage(page);

            AffineTransform at = AffineTransform.getTranslateInstance(margin + dspOffsetX, margin + dspOffsetY);
            at.concatenate(AffineTransform.getScaleInstance(dspWidth - 2 * dspOffsetX, dspHeight - 2 * dspOffsetY));

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setNonStrokingColor(Color.GRAY);
                contentStream.addRect(margin, margin, dspWidth, dspHeight);
                contentStream.fill();
                page.setTrimBox(new PDRectangle(margin, margin, dspWidth, dspHeight));
                contentStream.drawImage(pdImage, new Matrix(at));
            }

            document.save(new File(destination));
        }
    }


    @Test
    public void testFullPageImage() throws Exception {
        String imageWithIcc = resourceFile("images/image-ios-profile.jpg");
        String destination = targetFile("PdfBox-image-FullPageImage.pdf");

        try (PDDocument document = new PDDocument()) {

            PDImageXObject pdImage = PDImageXObject.createFromFile(imageWithIcc, document);

            addFullImagePage(document, mm2pt(100), pdImage, mm2pt(3));

            document.save(new File(destination));
        }
    }

    public void addFullImagePage(PDDocument document, float shortEdgeSize, PDImageXObject pdImage, float bleed) throws
            IOException {
        float imgWidth = pdImage.getWidth();
        float imgHeight = pdImage.getHeight();

        float trimWidth;
        float trimHeight;

        if (imgWidth > imgHeight) {
            trimHeight = shortEdgeSize;
            trimWidth = shortEdgeSize * imgWidth / imgHeight;
        } else {
            trimWidth = shortEdgeSize;
            trimHeight = shortEdgeSize * imgHeight / imgWidth;
        }

        float pageWidth = trimWidth + 2 * bleed;
        float pageHeight = trimHeight + 2 * bleed;

        PDPage page = new PDPage(new PDRectangle(0, 0, pageWidth, pageHeight));
        page.setTrimBox(new PDRectangle(bleed, bleed, trimWidth, trimHeight));

        AffineTransform at = AffineTransform.getScaleInstance(pageWidth, pageHeight);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.drawImage(pdImage, new Matrix(at));
        }

        document.addPage(page);
    }

}
