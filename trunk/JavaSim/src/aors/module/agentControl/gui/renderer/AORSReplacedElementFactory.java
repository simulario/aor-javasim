package aors.module.agentControl.gui.renderer;

import aors.module.agentControl.gui.interaction.EventMediator;
import java.awt.Container;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.DefaultFormSubmissionListener;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.swing.EmptyReplacedElement;
import org.xhtmlrenderer.swing.ImageResourceLoader;
import org.xhtmlrenderer.swing.RepaintListener;
import org.xhtmlrenderer.swing.SwingReplacedElement;
import org.xhtmlrenderer.swing.SwingReplacedElementFactory;

public class AORSReplacedElementFactory extends SwingReplacedElementFactory {

  private FormSubmissionListener formSubmissionListener;

  private EventMediator mediator;
	
	public AORSReplacedElementFactory(EventMediator mediator) {
			this(mediator, ImageResourceLoader.NO_OP_REPAINT_LISTENER);
	}
    
	public  AORSReplacedElementFactory(EventMediator mediator,
		RepaintListener repaintListener) {
			this(mediator, repaintListener, new ImageResourceLoader());
	}

	public AORSReplacedElementFactory(EventMediator mediator,
		final RepaintListener listener, final ImageResourceLoader irl) {
		super(listener, irl);
    this.mediator = mediator;
    this.formSubmissionListener = new DefaultFormSubmissionListener();
    //synchronizes the super class' formSubmissionListener with the current one
    this.setFormSubmissionListener(this.formSubmissionListener);
	}

  @Override
  //needs to be overridden so that the new created componets can be used
  public ReplacedElement createReplacedElement(LayoutContext context,
    BlockBox box, UserAgentCallback uac, int cssWidth, int cssHeight) {
    Element e = box.getElement();

    if (e == null) {
      return null;
    }

    if (context.getNamespaceHandler().isImageElement(e)) {
      return replaceImage(uac, context, e, cssWidth, cssHeight);
    } else {
      //form components
      Element parentForm = getParentForm(e, context);

      //parentForm may be null! No problem! Assume action is this document and
			//method is get.
      AORSForm form = getForm(parentForm);

			if (form == null) {
        //here we introduce our form implementation
        form = new AORSForm(uac, parentForm, formSubmissionListener, mediator);
        addForm(parentForm, form);
      }
      FormField formField = form.addComponent(e, context, box);
      if(formField == null) {
        return null;
      }

			JComponent cc = formField.getComponent();

      if(cc == null) {
        return new EmptyReplacedElement(0, 0);
      }
      SwingReplacedElement result = new SwingReplacedElement(cc);
			result.setIntrinsicSize(formField.getIntrinsicSize());

			if (context.isInteractive()) {
        ((Container)context.getCanvas()).add(cc);
      }
      return result;
    }
	}

	@Override
	protected AORSForm getForm(Element e) {
		if (forms == null) {
				return null;
		}
    return (AORSForm) forms.get(e);
	}
 }
