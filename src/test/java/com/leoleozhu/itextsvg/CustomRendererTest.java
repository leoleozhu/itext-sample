package com.leoleozhu.itextsvg;

import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.leoleozhu.utils.TestCaseBase;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;

public class CustomRendererTest extends TestCaseBase {


    @Test
    public void test_svg_CustomCircleRenderer() throws Exception {
        SvgConverterProperties properties = new SvgConverterProperties();
        properties.setRendererFactory(new CustomRendererFactory());

        String relativePosition = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 80 600\">\n" +
                "    <circle cx=\"50%\" cy=\"50%\" r=\"100\" fill=\"rgb(10, 200, 200)\"/>-->\n" +
                "</svg>\n";

        String destination = targetFile("svg-itext_SVG2PDF1.pdf");


        try(InputStream svgStream = new ByteArrayInputStream( relativePosition.getBytes())) {
            try(OutputStream pdfStream = Files.newOutputStream(new File(destination).toPath())) {
                SvgConverter.createPdf(svgStream, pdfStream, properties);
            }
        }

        String absolutePosition = "<svg xmlns=\"http://www.w3.org/2000/svg\" version=\"1.1\" viewBox=\"0 0 800 600\">\n" +
                "    <circle cx=\"400\" cy=\"300\" r=\"100\" fill=\"rgb(200, 10, 200)\"/>-->\n" +
                "</svg>\n";

        String destination2 = targetFile("svg-itext_SVG2PDF2.pdf");

        try(InputStream svgStream = new ByteArrayInputStream(absolutePosition.getBytes())) {
            try(OutputStream pdfStream = Files.newOutputStream(new File(destination2).toPath())) {
                SvgConverter.createPdf(svgStream, pdfStream, properties);
            }
        }
    }

}
