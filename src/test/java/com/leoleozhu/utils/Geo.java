package com.leoleozhu.utils;

import com.itextpdf.kernel.geom.Rectangle;

public class Geo {

    public static Rectangle getRectToPlaceContentCenterAndKeepAspectRatio(
            Rectangle box,
            float contentWidth, float contentHeight) {
        float x = box.getX();
        float y = box.getY();
        float w = box.getWidth();
        float h = box.getHeight();


        float dspOffsetX = (w / h > contentWidth / contentHeight) ? (w - (h / contentHeight * contentWidth)) / 2 : 0;

        float dspOffsetY = (w / h < contentWidth / contentHeight) ? (h - (w / contentWidth * contentHeight)) / 2 : 0;

        return new Rectangle(x + dspOffsetX, y + dspOffsetY, w - 2 * dspOffsetX, h - 2 * dspOffsetY);
    }
}
