package com.leoleozhu.itextsvg;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;

public class CustomRendererFactory extends DefaultSvgNodeRendererFactory {

        @Override
        public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
            if (SvgConstants.Tags.CIRCLE.equals(tag.name())) {
                return new CustomCircleSvgNodeRenderer();
            }
            return super.createSvgNodeRendererForTag(tag, parent);
        }
    }
