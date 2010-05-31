package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.gui.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.plaf.basic.BasicTextUI;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.util.GeneralUtil;

class TextField extends InputField<JTextField> implements
  PropertyChangeListener {

  public TextField(Element e, AORSForm form, LayoutContext context,
    BlockBox box, EventMediator mediator) {
    super(e, form, context, box, mediator);
  }

  @Override
  protected JTextField create2() {

    JTextField textfield = new JTextField() {

      private final static long serialVersionUID = 1L;
      //override getColumnWidth to base on 'o' instead of 'm'.
      //more like other browsers
      int columnWidth = 0;

      @Override
      protected int getColumnWidth() {
        if(columnWidth == 0) {
          FontMetrics metrics = getFontMetrics(getFont());
          columnWidth = metrics.charWidth('o');
        }
        return columnWidth;
      }
    };

    if(hasAttribute("size")) {
      int size = GeneralUtil.parseIntRelaxed(getAttribute("size"));

      // Size of 0 doesn't make any sense, so use default value
      if(size == 0) {
        textfield.setColumns(15);
      } else {
        textfield.setColumns(size);
      }
    } else {
      textfield.setColumns(15);
    }

    textfield.setEditable(!(hasAttribute("readonly") &&
      getAttribute("readonly").equals("true")));

		if(hasAttribute("initialValue")) {
			textfield.setText(getAttribute("initialValue"));
		}

    applyComponentStyle(textfield);

    return textfield;
  }

//  @Override
  protected void applyComponentStyle(JTextField component) {
    super.applyComponentStyle(component);

    CalculatedStyle style = getBox().getStyle();
    BorderPropertySet border = style.getBorder(null);
    boolean disableOSBorder = (border.leftStyle() != null &&
      border.rightStyle() != null || border.topStyle() != null ||
      border.bottomStyle() != null);

    RectPropertySet padding = style.getCachedPadding();

    Integer paddingTop = getLengthValue(style, CSSName.PADDING_TOP);
    Integer paddingLeft = getLengthValue(style, CSSName.PADDING_LEFT);
    Integer paddingBottom = getLengthValue(style, CSSName.PADDING_BOTTOM);
    Integer paddingRight = getLengthValue(style, CSSName.PADDING_RIGHT);


    int top = 2;
		if(paddingTop != null) {
			Math.max(top, paddingTop.intValue());
		}
    int left = 3;
		if(paddingLeft != null) {
			Math.max(left, paddingLeft.intValue());
		}
    int bottom = 2;
		if(paddingBottom != null) {
			Math.max(bottom, paddingBottom.intValue());
		}
    int right = 3;
		if(paddingRight != null) {
			Math.max(right, paddingRight.intValue());
		}

    //if a border is set or a background color is set, then use a special
    //JButton with the BasicButtonUI.
    if(disableOSBorder) {
      //when background color is set, need to use the BasicButtonUI, certainly
      //when using XP l&f
      BasicTextUI ui = new BasicTextFieldUI();
      component.setUI(ui);
      Border fieldBorder = BorderFactory.createEmptyBorder(top, left, bottom,
				right);
      component.setBorder(fieldBorder);
    } else {
      component.setMargin(new Insets(top, left, bottom, right));
    }

    padding.setRight(0);
    padding.setLeft(0);
    padding.setTop(0);
    padding.setBottom(0);

    FSDerivedValue widthValue = style.valueByName(CSSName.WIDTH);
    if(widthValue instanceof LengthValue) {
      intrinsicWidth = new Integer(getBox().getContentWidth() + left + right);
    }

    FSDerivedValue heightValue = style.valueByName(CSSName.HEIGHT);
    if(heightValue instanceof LengthValue) {
      intrinsicHeight = new Integer(getBox().getHeight() + top + bottom);
    }
  }

  @Override
  protected void applyOriginalState2() {
    JTextField textfield = getComponent();

    textfield.setText(getOriginalState().getValue());

    // Make sure we are showing the front of 'value' instead of the end.
    textfield.setCaretPosition(0);
  }


  @Override
  protected String[] getFieldValues() {
    JTextField textfield = getComponent();

    return new String[] {
        textfield.getText()
      };
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if(!this.getComponent().equals(evt.getSource())) {
      this.getComponent().setText(evt.getNewValue().toString());
    }
  }

	@Override
	protected void registerWithMediator() {
    mediator.addReceiver(getAttribute("slot"), this, this.getValue());
    mediator.addSender(getAttribute("name"), this);
		this.getComponent().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        mediator.propertyChange(new PropertyChangeEvent(TextField.this,
          getAttribute("slot"), null, TextField.this.getValue()));
      }
    });
	}

	@Override
  public String getValue() {
    return this.getComponent().getText();
  }
}
