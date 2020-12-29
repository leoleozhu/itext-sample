package com.leoleozhu.itextsvg;

    import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
    import com.itextpdf.svg.SvgConstants;

    import com.itextpdf.svg.renderers.ISvgNodeRenderer;
    import com.itextpdf.svg.renderers.SvgDrawContext;
    import com.itextpdf.svg.renderers.impl.AbstractSvgNodeRenderer;
    import com.itextpdf.svg.utils.DrawUtils;

    /**
     * {@link ISvgNodeRenderer} implementation for the &lt;circle&gt; tag.
     */
    public class CustomCircleSvgNodeRenderer extends AbstractSvgNodeRenderer {

        private float cx;
        private float cy;
        float r;

        @Override
        protected void doDraw(SvgDrawContext context) {
            PdfCanvas cv = context.getCurrentCanvas();
            cv.writeLiteral("% ellipse\n");
            if (setParameters(context)) {
                // Use double type locally to have better precision of the result after applying arithmetic operations
                cv.moveTo((double) cx + (double) r, cy);
                DrawUtils.arc((double) cx - (double) r, (double) cy - (double) r, (double) cx + (double) r,
                        (double) cy + (double) r, 0, 360, cv);
            }
        }


        private boolean setParameters(SvgDrawContext context) {
            cx = 0;
            cy = 0;
            if (getAttribute(SvgConstants.Attributes.CX) != null) {
                cx = parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CX), context.getCurrentViewPort().getWidth(), 0.0f, context);
            }
            if (getAttribute(SvgConstants.Attributes.CY) != null) {
                cy = parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CY), context.getCurrentViewPort().getHeight(), 0.0f, context);
            }
            if (getAttribute(SvgConstants.Attributes.R) != null
                    && parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CY), context.getCurrentViewPort().getHeight(), 0.0f, context) > 0) {
                r = parseAbsoluteLength(getAttribute(SvgConstants.Attributes.CY), context.getCurrentViewPort().getHeight(), 0.0f, context);
            } else {
                return false; //No drawing if rx is absent
            }
            return true;
        }

        @Override
        public ISvgNodeRenderer createDeepCopy() {
            CustomCircleSvgNodeRenderer copy = new CustomCircleSvgNodeRenderer();
            deepCopyAttributesAndStyles(copy);
            return copy;
        }

    }