package aors.module.agentControl.gui.renderer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.AORSForm;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

class VizualisationField extends FormField<JComponent> {

	public VizualisationField(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
		super(e, form, context, box, mediator);
	}

	@Override
	protected JComponent create2() {
		JPanel viz = new JPanel();
		viz.add(new JLabel("Here should be a visualization!"));
		applyComponentStyle(viz);
		return viz;
//		System.out.println("create");
//		System.out.println(mediator.getVizGui());
//		return (JComponent)mediator.getVizGui();
//		JPanel panel = new JPanel();
//		applyComponentStyle(panel);
//		panel.add((JComponent)mediator.getVizGui());
//		System.out.println("create");
//		return panel;
	}

//	@Override
//	protected void applyComponentStyle(JPanel panel) {
//		super.applyComponentStyle(panel);
//	}

//	@Override
//	public void propertyChange(PropertyChangeEvent evt) {
//		System.out.println(evt);
//		System.out.println(this.getComponent());
//		if(this.mediator.equals(evt.getSource()) &&
//			evt.getNewValue() instanceof JComponent) {
//			this.getComponent().removeAll();
//			this.getComponent().add((JComponent)evt.getNewValue());
//			this.getComponent().revalidate();
//		}
//		System.out.println(this.getComponent());
//	}

	@Override
	protected String[] getFieldValues() {
		return new String[] {"vizualisation panel"};
	}

	@Override
	protected void registerWithMediator() {
//		mediator.addReceiver(getAttribute("__vizualisation"), this, null);
	}

	@Override
	public String getValue() {
		return null;
	}
}
