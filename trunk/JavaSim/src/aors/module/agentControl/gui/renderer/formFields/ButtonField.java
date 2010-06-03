package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
import javax.swing.JButton;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

class ButtonField extends AbstractButtonField<JButton> {

	public ButtonField(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
		super(e, form, context, box, mediator);
	}

	@Override
	protected JButton create2() {
		final JButton button = new JButton();

		String value = getAttribute("title");
		if(value == null || value.length() == 0) {
			value = "Button";    //otherwise we get a very short button
		}

		applyComponentStyle(button);

    button.setText(value);

		return button;
	}

	@Override
	public boolean includeInSubmission(JComponent source) {
		return false;
	}

	@Override
	protected void registerWithMediator() {
		mediator.addSender(this.getAttribute("name"), this);
	}

	@Override
	public String getValue() {
		return null;
	}
}
