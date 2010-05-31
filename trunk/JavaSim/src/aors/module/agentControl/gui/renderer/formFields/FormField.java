package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.Sender;
import aors.module.agentControl.gui.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
//import java.awt.Color;
//import java.awt.Dimension;
//import java.awt.Font;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import org.w3c.dom.Element;
//import org.xhtmlrenderer.css.constants.CSSName;
//import org.xhtmlrenderer.css.parser.FSColor;
//import org.xhtmlrenderer.css.parser.FSRGBColor;
//import org.xhtmlrenderer.css.style.CalculatedStyle;
//import org.xhtmlrenderer.css.style.FSDerivedValue;
//import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
//import org.xhtmlrenderer.render.FSFont;
//import org.xhtmlrenderer.swing.AWTFSFont;

abstract class FormField<T extends JComponent>
	extends org.xhtmlrenderer.simple.extend.form.FormField implements Sender {

	private T _component;
//	private LayoutContext context;
//	private BlockBox box;
//  protected Integer intrinsicWidth;
//  protected Integer intrinsicHeight;
	private boolean flag = false;
	protected EventMediator mediator;
//	private AORSForm parentForm;

	public FormField(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
//    super(e, form);
		super(e, form, context, box);
//		this.context = context;
//		this.box = box;
		this.mediator = mediator;
//		this.parentForm = form;
		this.initialize();
	}

	@Override
	public AORSForm getParentForm() {
		return (AORSForm)super.getParentForm();
	}

	@Override
	public T getComponent() {
		return this._component;
	}

//  public Dimension getIntrinsicSize() {
//    int width = intrinsicWidth == null ? 0 : intrinsicWidth.intValue();
//    int height = intrinsicHeight == null ? 0 : intrinsicHeight.intValue();
//    return new Dimension(width, height);
//  }
	private void initialize() {
		flag = true;
		this._component = create();
		if(this._component != null) {
			registerWithMediator();
			if(this.intrinsicWidth == null) {
				this.intrinsicWidth = new Integer(this._component.getPreferredSize().width);
			}
			if(this.intrinsicHeight == null) {
				this.intrinsicHeight = new Integer(this._component.getPreferredSize().height);
			}
			this._component.setSize(this.getIntrinsicSize());

			if(this.getElement().hasAttribute("disabled") && this.getElement().
				getAttribute("disabled").equalsIgnoreCase("disabled")) {
				this._component.setEnabled(false);
			}
		}
		this.applyOriginalState();
	}

	@Override
	protected final void applyOriginalState() {
		if(flag) {
			this.applyOriginalState2();
		}
	}

	protected void applyOriginalState2() {
		//do nothing;
	}

	@Override
	public final T create() {
		if(flag) {
			return create2();
		}
		return null;
	}

	protected abstract T create2();

	@Override
	protected void applyComponentStyle(JComponent comp) {
		super.applyComponentStyle(comp);
	}



//  public BlockBox getBox() {
//    return this.box;
//  }
//  public LayoutContext getContext() {
//    return context;
//  }
//  public CalculatedStyle getStyle() {
//    return this.getBox().getStyle();
//  }
//  protected void applyComponentStyle(T comp) {
//    Font font = getFont();
//    if (font != null) {
//      comp.setFont(font);
//    }
//
//    CalculatedStyle style = getStyle();
//
//    FSColor foreground = style.getColor();
//    if (foreground != null) {
//      comp.setForeground(toColor(foreground));
//    }
//
//    FSColor background = style.getBackgroundColor();
//    if (background != null) {
//      comp.setBackground(toColor(background));
//    }
//  }
//  private static Color toColor(FSColor color) {
//    if (color instanceof FSRGBColor) {
//      FSRGBColor rgb = (FSRGBColor) color;
//      return new Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue());
//    }
//    throw new RuntimeException("internal error: unsupported color class " +
//      color.getClass().getName());
//  }
//  public Font getFont() {
//    FSFont font = getStyle().getFSFont(getContext());
//    if (font instanceof AWTFSFont) {
//      return ((AWTFSFont) font).getAWTFont();
//    }
//    return null;
//  }
//	protected static Integer getLengthValue(CalculatedStyle style, CSSName cssName) {
//		FSDerivedValue widthValue = style.valueByName(cssName);
//		if(widthValue instanceof LengthValue) {
//			return new Integer((int)widthValue.asFloat());
//		}
//
//		return null;
//	}

	protected abstract void registerWithMediator();

	@Override
	public void addMouseListener(MouseListener mouseListener) {
		this.getComponent().addMouseListener(mouseListener);
	}

	@Override
	public void addKeyListener(KeyListener keyListener) {
		this.getComponent().addKeyListener(keyListener);
	}

	@Override
	public boolean isFocusable() {
		return this.getComponent().isFocusable();
	}

	@Override
	public void setFocusable(boolean focusable) {
		this.getComponent().setFocusable(focusable);
	}

	@Override
	public boolean requestFocusInWindow() {
		return this.getComponent().requestFocusInWindow();
	}
}
