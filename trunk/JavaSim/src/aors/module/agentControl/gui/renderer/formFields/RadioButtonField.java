package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JRadioButton;

import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;

class RadioButtonField extends InputField<JRadioButton> implements
	PropertyChangeListener {

	public RadioButtonField(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
		super(e, form, context, box, mediator);
	}

	@Override
	protected JRadioButton create2() {
		final JRadioButton radio = new JRadioButton();

		radio.setText("");
		radio.setOpaque(false);
		radio.setSelected(hasAttribute("checked") &&
			getAttribute("checked").equals("true"));

		String groupName = getAttribute("name");

		// Add to the group for mutual exclusivity
		getParentForm().addButtonToGroup(groupName, radio);

		return radio;
	}

	@Override
	protected FormFieldState loadOriginalState() {
		return FormFieldState.fromBoolean(
			getAttribute("checked").equalsIgnoreCase("checked"));
	}

	@Override
	protected void applyOriginalState2() {
		JRadioButton button = getComponent();
		button.setSelected(getOriginalState().isChecked());
	}

	@Override
	protected String[] getFieldValues() {
		JRadioButton button = getComponent();

		if(button.isSelected()) {
			return new String[] {
					hasAttribute("value") ? getAttribute("value") : ""
				};
		} else {
			return new String[] {};
		}

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(!this.getComponent().equals(evt.getSource())) {
			this.getComponent().setSelected(getAttribute("value").
				equals(evt.getNewValue().toString()));
		}
	}

	@Override
	protected void registerWithMediator() {
	  mediator.addReceiver(getAttribute("slot"), this, this.getValue());
    mediator.addSender(getAttribute("name"), this);
    this.getComponent().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        if(RadioButtonField.this.getComponent().isSelected()) {
          mediator.propertyChange(new PropertyChangeEvent(RadioButtonField.this,
            getAttribute("slot"), null, RadioButtonField.this.
            getAttribute("value")));
        }
      }
    });
	}

	@Override
	public String getValue() {
		return this.getParentForm().getButtonGroupValue(getAttribute("name"));
	}
}
