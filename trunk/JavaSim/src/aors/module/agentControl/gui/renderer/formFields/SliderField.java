package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

class SliderField extends InputField<JSlider> implements
	PropertyChangeListener {

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

    if(hasAttribute("labels") && getAttribute("labels").equals("true")) {
      Hashtable<Integer, JLabel> labels = new Hashtable<Integer, JLabel>();
      labels.put(minValue, new JLabel(String.valueOf(minValue)));
      labels.put(maxValue, new JLabel(String.valueOf(maxValue)));
      slider.setLabelTable(labels);
      slider.setPaintLabels(true);
    }

    applyComponentStyle(slider);

    return slider;
  }

//  @Override
  protected void applyComponentStyle(JSlider slider) {
    super.applyComponentStyle(slider);
    //TODO: style for slider labels
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
    mediator.addReceiver(getAttribute("slot"), this, this.getValue());
    mediator.addSender(getAttribute("name"), this);
    this.getComponent().addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        mediator.propertyChange(new PropertyChangeEvent(SliderField.this,
          getAttribute("slot"), null, SliderField.this.getValue()));
      }
    });
	}

	@Override
  public String getValue() {
    return String.valueOf(this.getComponent().getValue());
  }
}
