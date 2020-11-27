# iText 7 sample

## Perserve the embedded image

Some times you want to crop part of the image and add it to the PDF. If you choose BufferedImage to manipulate the input images, some information will be lost. For example, exif or color profiles.

To keep the original image file untouched, you could crop the image by using `PdfFormXObject`. Then add the object to a `PDFCanvas`.

Details here <https://github.com/leoleozhu/itext-sample/blob/3c5d9671cc30daaea355d53ff61484494e72953f/src/test/java/com/leoleozhu/itextimage/ImageColorSpaceTest.java#L84>


## Spot color

In order to create a spot color in iText, we need to bind an alternate color space to the spot color ([Seperation](https://api.itextpdf.com/iText7/java/latest/com/itextpdf/kernel/colors/Separation.html)).

Details here <https://github.com/leoleozhu/itext-sample/blob/3c5d9671cc30daaea355d53ff61484494e72953f/src/test/java/com/leoleozhu/itextcolor/SpotColorTest.java#L32>
