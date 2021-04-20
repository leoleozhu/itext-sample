# iText 7 sample

## Preserve the embedded image

Sometimes you want to crop part of the image and add it to the PDF. If you use `BufferedImage` to manipulate the input images, some information will be lost. For example, exif or color profiles.

To keep the original image file untouched, you could crop the image by using `PdfFormXObject`. Then add the object to a `PDFCanvas`.

Example here <https://github.com/leoleozhu/itext-sample/blob/master/src/test/java/com/leoleozhu/itextimage/ImageColorSpaceTest.java#L84>

## Spot color

In order to create a spot color in iText, we need to bind an alternate color space to the spot color ([Separation](https://api.itextpdf.com/iText7/java/latest/com/itextpdf/kernel/colors/Separation.html)).

Example here <https://github.com/leoleozhu/itext-sample/blob/master/src/test/java/com/leoleozhu/itextcolor/SpotColorTest.java#L32>

## Add image with mask

iText has already provided an example for this topic. But if you apply a mask to a transparent image, you get blacked image.

The reason is that:

* When you create ImageData with `imageData = ImageDataFactory.create`, there is already a mask image in `imageData.getImageMask()`
* When you apply image mask with `imageData.setImageMask(maskData);`, the original image mask got replaced.

In order to correctly place an extra mask on the transparent image, you could combine both the original loaded image mask and the new one.

Example here <https://github.com/leoleozhu/itext-sample/blob/master/src/test/java/com/leoleozhu/itextimage/ImageMaskTest.java#L87>

The mask creation is not very fast. There could be better solutions for this use case.

