package aors.module.agentControl.gui.renderer.flyingsaucer.formFields;

import aors.module.agentControl.controller.ModuleAgentController;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.Receiver;
import aors.module.agentControl.gui.renderer.flyingsaucer.AORSForm;
import java.awt.Color;
import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.FSScrollPane;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;

class UpdateableArea extends FormField<JScrollPane> implements Receiver {

	private boolean updated = false;
	private List<JComponent> content;
	private JPanel panel;
	private final static JPanel DUMMY = new JPanel();
	private final static Color TRANSPARENT = new Color(0, 0, 0, 0);


	public UpdateableArea(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
		super(e, form, context, box, mediator);
		this.content = new ArrayList<JComponent>();
		this.panel = new JPanel(new GridLayout(0, 1));
		this.panel.setBackground(UpdateableArea.TRANSPARENT);
		this.panel.setOpaque(false);
	}

	@Override
	protected FormFieldState loadOriginalState() {
		return FormFieldState.fromString(getAttribute("initialValue"));
	}

	@Override
	protected String[] getFieldValues() {
		return new String[] {
				hasAttribute("initialValue") ? getAttribute("initialValue") : ""
			};
	}

	@Override
	protected JScrollPane create2() {
		JScrollPane scrollpane = new JScrollPane();
		applyComponentStyle(scrollpane);
		return scrollpane;
	}

	protected void applyComponentStyle(JScrollPane scrollpane) {
		super.applyComponentStyle(scrollpane);
		if(!updated) {
			scrollpane.setHorizontalScrollBarPolicy(
				FSScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			scrollpane.setVerticalScrollBarPolicy(
				FSScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		}
		this.setSize(scrollpane);
		scrollpane.setBorder(null);
		scrollpane.setOpaque(false);
	}

	private void setSize(JScrollPane scrollpane) {
		this.intrinsicWidth = 0;
		this.intrinsicHeight = 0;
		if(this.updated) {
			this.intrinsicWidth = this.getBox().getContainingBlock().getContentWidth();
			this.intrinsicHeight = this.getBox().getContainingBlock().getHeight();
		}
		scrollpane.setSize(intrinsicWidth, intrinsicHeight);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(this.panel == null) {
			return;
		}
		if(!this.getComponent().equals(evt.getSource())) {

			// a new perception for the current step. add it to the perception list
			if(!ModuleAgentController.END_OF_PERCEPTIONS.equals(evt.getNewValue())) {
				if(evt.getNewValue() instanceof JComponent) {
					this.content.add((JComponent)evt.getNewValue());
				}
				return;
			}

			// all perceptions of the current step are received. now print them
			this.panel.removeAll();
			this.updated = !this.content.isEmpty();
			this.setSize(this.getComponent());
			for(JComponent component : this.content) {
				component.setBackground(UpdateableArea.TRANSPARENT);
				component.setVisible(true);
				this.panel.add(component);
			}
			super.applyComponentStyle(panel);
			this.getComponent().getViewport().setOpaque(false);
			this.getComponent().setViewportView(UpdateableArea.DUMMY);
			this.getComponent().setViewportView(panel);
			this.content.clear();
		}
	}

	@Override
	protected void registerWithMediator() {
		this.mediator.addReceiver(getAttribute(Receiver.RECEIVER_ATTRIBUTE), this,
			null);
	}

	@Override
	public String getValue() {
		return null;
	}
}
