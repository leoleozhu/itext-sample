package com.leoleozhu.pdfboxtest.text;

import com.leoleozhu.utils.TestCaseBase;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.*;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfWithTextTest extends TestCaseBase {

    @Test
    public void testTextRotation() throws Exception {
        String destination = targetFile("PdfBox-text-TextRotation.pdf");
        String fontPath = resourceFile("fonts/SourceHanSansCN-Bold.otf");

        float margin = mm2pt(10);
        float contentWidth = mm2pt(100);
        float contentHeight = mm2pt(80);
        float pageWidth = contentWidth + margin * 2;
        float pageHeight = contentHeight + margin * 2;

        int fontSize = 12;

        try (PDDocument document = new PDDocument();
             InputStream fontStream = new FileInputStream(fontPath)) {


            PDPage page = new PDPage(new PDRectangle(0, 0, pageWidth, pageHeight));

            String text = "Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!" +
                    "Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!Foo!";
            OTFParser otfParser = new OTFParser();
            OpenTypeFont otf = otfParser.parse(fontStream);
            PDFont font = PDType0Font.load(document, otf, false);

            AffineTransform at = AffineTransform.getTranslateInstance(margin, margin + (pageHeight / 2 - margin));
            at.concatenate(AffineTransform.getRotateInstance(Math.PI / 8));

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                contentStream.saveGraphicsState();
                // add bg
                contentStream.setNonStrokingColor(Color.gray);
                contentStream.addRect(margin, margin, contentWidth, contentHeight);
                contentStream.fill();

                contentStream.restoreGraphicsState();
                // add text at top
                contentStream.beginText();
                contentStream.setFont(font, fontSize);
                contentStream.setNonStrokingColor(Color.orange);
                contentStream.setTextMatrix(at);
                contentStream.showText(text);
                contentStream.endText();
                contentStream.restoreGraphicsState();
            }

            document.addPage(page);

            document.save(new File(destination));
        }
    }

    private void centerText(PDDocument document, float pageWidth, float pageHeight, PDFont font, int fontSize, String text) throws IOException {
        PDPage page = new PDPage(new PDRectangle(0, 0, pageWidth, pageHeight));



    }
}
