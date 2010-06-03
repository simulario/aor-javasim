package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
import java.awt.Color;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.basic.BasicButtonUI;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSRGBColor;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

abstract class AbstractButtonField<T extends JButton> extends InputField<T> {

	public AbstractButtonField(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
		super(e, form, context, box, mediator);
	}

	protected void applyComponentStyle(T button) {
		super.applyComponentStyle(button);

		CalculatedStyle style = getBox().getStyle();
		BorderPropertySet border = style.getBorder(null);
		boolean disableOSBorder = (border.leftStyle() != null &&
			border.rightStyle() != null || border.topStyle() != null ||
			border.bottomStyle() != null);

		FSColor backgroundColor = style.getBackgroundColor();

		//if a border is set or a background color is set, then use a special
		//JButton with the BasicButtonUI.
		if(disableOSBorder || backgroundColor instanceof FSRGBColor) {
			//when background color is set, need to use the BasicButtonUI, certainly
			//when using XP l&f
			BasicButtonUI ui = new BasicButtonUI();
			button.setUI(ui);

			if(backgroundColor instanceof FSRGBColor) {
				FSRGBColor rgb = (FSRGBColor)backgroundColor;
				button.setBackground(new Color(rgb.getRed(), rgb.getGreen(),
					rgb.getBlue()));
			}

			if(disableOSBorder) {
				button.setBorder(new BasicBorders.MarginBorder());
			} else {
				button.setBorder(BasicBorders.getButtonBorder());
			}
		}

		Integer paddingTop = getLengthValue(style, CSSName.PADDING_TOP);
		Integer paddingLeft = getLengthValue(style, CSSName.PADDING_LEFT);
		Integer paddingBottom = getLengthValue(style, CSSName.PADDING_BOTTOM);
		Integer paddingRight = getLengthValue(style, CSSName.PADDING_RIGHT);

		int top = 2;
		if(paddingTop != null) {
			Math.max(top, paddingTop.intValue());
		}
		int left = 12;
		if(paddingLeft != null) {
			Math.max(left, paddingLeft.intValue());
		}
		int bottom = 2;
		if(paddingBottom != null) {
			Math.max(bottom, paddingBottom.intValue());
		}
		int right = 12;
		if(paddingRight != null) {
			Math.max(right, paddingRight.intValue());
		}

		button.setMargin(new Insets(top, left, bottom, right));

		RectPropertySet padding = style.getCachedPadding();
		padding.setRight(0);
		padding.setLeft(0);
		padding.setTop(0);
		padding.setBottom(0);

		FSDerivedValue widthValue = style.valueByName(CSSName.WIDTH);
		if(widthValue instanceof LengthValue) {
			intrinsicWidth = new Integer(getBox().getContentWidth());
		}

		FSDerivedValue heightValue = style.valueByName(CSSName.HEIGHT);
		if(heightValue instanceof LengthValue) {
			intrinsicHeight = new Integer(getBox().getHeight());
		}
	}
}
