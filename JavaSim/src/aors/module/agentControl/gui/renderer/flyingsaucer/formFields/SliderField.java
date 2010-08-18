package aors.module.agentControl.gui.renderer.flyingsaucer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.Receiver;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.flyingsaucer.AORSForm;
import java.beans.PropertyChangeEvent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

class SliderField extends InputField<JSlider> implements Receiver {

  public SliderField(Element e, AORSForm form, LayoutContext context,
    BlockBox box, EventMediator mediator) {
    super(e, form, context, box, mediator);
  }

  @Override
  protected JSlider create2() {
    int minValue = Integer.parseInt(getAttribute("minValue"));
    int maxValue = Integer.parseInt(getAttribute("maxValue"));
    int value = minValue;
    if(hasAttribute("initialValue")) {
      value = Integer.parseInt(getAttribute("initialValue"));
    }
    if(value < minValue) {
      value = minValue;
    }

    if(value > maxValue) {
      value = maxValue;
    }
    JSlider slider = new JSlider(minValue, maxValue, value);

//    if(hasAttribute("labels") && getAttribute("labels").equals("true")) {
//      Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
//      labels.put(minValue, new JLabel(String.valueOf(minValue)));
//      labels.put(maxValue, new JLabel(String.valueOf(maxValue)));
//      slider.setLabelTable(labels);
//      slider.setPaintLabels(true);
//    }

    applyComponentStyle(slider);

    return slider;
  }

   protected void applyComponentStyle(JSlider slider) {
    super.applyComponentStyle(slider);
		CalculatedStyle style = getBox().getStyle();
		FSDerivedValue widthValue = style.valueByName(CSSName.WIDTH);
		if(widthValue instanceof LengthValue) {
			intrinsicWidth = new Integer(getBox().getContentWidth());
		}

		FSDerivedValue heightValue = style.valueByName(CSSName.HEIGHT);
		if(heightValue instanceof LengthValue) {
			intrinsicHeight = new Integer(getBox().getHeight());
		}
	}

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if(!this.getComponent().equals(evt.getSource())) {
      int intValue = 0;
      JSlider slider = getComponent();
      try {
        intValue = Integer.valueOf(evt.getNewValue().toString());
      } catch(NumberFormatException e) {
        //do nothing; the value is 0
      }
      if(intValue > slider.getMaximum()) {
        slider.setValue(slider.getMaximum());
        return;
      }
      if(intValue < slider.getMinimum()) {
        slider.setValue(slider.getMinimum());
        return;
      }
      slider.setValue(intValue);
      return;
    }
  }

	@Override
	protected void registerWithMediator() {
    mediator.addReceiver(getAttribute(Receiver.RECEIVER_ATTRIBUTE), this,
			this.getValue());
    mediator.addSender(getAttribute(Sender.SENDER_ATTRIBUTE), this);
    this.getComponent().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        mediator.propertyChange(new PropertyChangeEvent(SliderField.this,
          getAttribute(Receiver.RECEIVER_ATTRIBUTE), null,
					SliderField.this.getValue()));
      }
    });
	}

	@Override
  public String getValue() {
    return String.valueOf(this.getComponent().getValue());
  }
}
