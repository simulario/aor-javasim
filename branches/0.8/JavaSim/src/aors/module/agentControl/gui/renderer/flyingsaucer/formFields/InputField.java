package aors.module.agentControl.gui.renderer.flyingsaucer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.flyingsaucer.AORSForm;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;

abstract class InputField<T extends JComponent> extends FormField<T> {

    public InputField(Element e, AORSForm form, LayoutContext context,
      BlockBox box, EventMediator mediator) {
        super(e, form, context, box, mediator);
    }

    @Override
    protected FormFieldState loadOriginalState() {
        return FormFieldState.fromString(getAttribute("initialValue"));
    }

    @Override
    protected String[] getFieldValues() {
        return new String [] {
                hasAttribute("initialValue") ? getAttribute("initialValue") : ""
        };
    }
}
