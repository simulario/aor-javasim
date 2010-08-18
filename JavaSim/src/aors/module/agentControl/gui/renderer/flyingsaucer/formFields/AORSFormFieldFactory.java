package aors.module.agentControl.gui.renderer.flyingsaucer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.flyingsaucer.AORSForm;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.simple.extend.form.FormFieldFactory;

public class AORSFormFieldFactory {

  private AORSFormFieldFactory() {}

  public static FormField create(AORSForm form,	LayoutContext context,
		BlockBox box, EventMediator mediator) {
		Element e = box.getElement();
    String typeKey = e.getNodeName();
    if(typeKey.equals("button")) {
      return new ButtonField(e, form, context, box, mediator);
    }
    if(typeKey.equals("checkbox")) {
      return new CheckBoxField(e, form, context, box, mediator);
    }
    if(typeKey.equals("dialog")) {
    }
    if(typeKey.equals("radiobutton")) {
      return new RadioButtonField(e, form, context, box, mediator);
    }
    if(typeKey.equals("select")) {
      return new SelectField(e, form, context, box, mediator);
    }
    if(typeKey.equals("slider")) {
      return new SliderField(e, form, context, box, mediator);
    }
    if(typeKey.equals("textarea")) {
      return new TextAreaField(e, form, context, box, mediator);
    }
    if(typeKey.equals("textfield")) {
      return new TextField(e, form, context, box, mediator);
    }

		if(typeKey.equals("updateableArea")) {
			return new UpdateableArea(e, form, context, box, mediator);
		}
    return FormFieldFactory.create(form, context, box);
  }
}
