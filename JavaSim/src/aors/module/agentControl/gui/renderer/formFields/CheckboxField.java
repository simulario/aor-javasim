package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.gui.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JCheckBox;

import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;

class CheckboxField extends InputField<JCheckBox> implements
  PropertyChangeListener {

  public CheckboxField(Element e, AORSForm form, LayoutContext context,
    BlockBox box, EventMediator mediator) {
    super(e, form, context, box, mediator);
  }

  @Override
  protected JCheckBox create2() {
    final JCheckBox checkbox = new JCheckBox();

    checkbox.setText("");
    checkbox.setOpaque(false);
    checkbox.setSelected(hasAttribute("checked") &&
      getAttribute("checked").equals("true"));
    
    return checkbox;
  }

  @Override
  protected FormFieldState loadOriginalState() {
    return FormFieldState.fromBoolean(
      getAttribute("checked").equalsIgnoreCase("checked"));
  }

  @Override
  protected void applyOriginalState2() {
    JCheckBox button = getComponent();

    button.setSelected(getOriginalState().isChecked());
  }

  @Override
  protected String[] getFieldValues() {
    final JCheckBox button = getComponent();

    if(button.isSelected()) {
      return new String[] {
          hasAttribute("value") ? getAttribute("value") : "on"
        };
    } else {
      return new String[] {};
    }
  }

	@Override
	protected void registerWithMediator() {
    mediator.addReceiver(getAttribute("slot"), this, this.getValue());
    mediator.addSender(getAttribute("name"), this);
		this.getComponent().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        mediator.propertyChange(new PropertyChangeEvent(CheckboxField.this,
          getAttribute("slot"), null, CheckboxField.this.getValue()));
      }
    });
	}

	@Override
  public void propertyChange(PropertyChangeEvent evt) {
    if(getAttribute("value").equals(evt.getNewValue().toString())) {
      this.getComponent().setSelected(true);
    }
  }

  @Override
  public String getValue() {
    if(this.getComponent().isSelected()) {
      return getAttribute("value");
    }
    return "";
  }
}
