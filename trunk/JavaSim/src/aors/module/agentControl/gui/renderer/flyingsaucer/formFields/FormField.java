package aors.module.agentControl.gui.renderer.flyingsaucer.formFields;

import aors.module.agentControl.gui.GUIComponent;
import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.flyingsaucer.AORSForm;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import javax.swing.JComponent;
import org.w3c.dom.Element;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;

abstract class FormField<T extends JComponent>
	extends org.xhtmlrenderer.simple.extend.form.FormField implements Sender {

	private T _component;
	private boolean flag = false;
	protected EventMediator mediator;

	public FormField(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
		super(e, form, context, box);
		this.mediator = mediator;
		this.initialize();
	}

	@Override
	public AORSForm getParentForm() {
		return (AORSForm)super.getParentForm();
	}

	@Override
	public T getComponent() {
		return this._component;
	}

	@Override
	public GUIComponent getGUIComponent() {
		return (GUIComponent)this.getComponent();
	}

	private void initialize() {
		flag = true;
		this._component = create();
		if(this._component != null) {
			registerWithMediator();
			if(this.intrinsicWidth == null) {
				this.intrinsicWidth = new Integer(this._component.getPreferredSize().width);
			}
			if(this.intrinsicHeight == null) {
				this.intrinsicHeight = new Integer(this._component.getPreferredSize().height);
			}
			this._component.setSize(this.getIntrinsicSize());

			if(this.getElement().hasAttribute("disabled") && this.getElement().
				getAttribute("disabled").equalsIgnoreCase("disabled")) {
				this._component.setEnabled(false);
			}
		}
		this.applyOriginalState();
	}

	@Override
	protected final void applyOriginalState() {
		if(flag) {
			this.applyOriginalState2();
		}
	}

	protected void applyOriginalState2() {
		//do nothing;
	}

	@Override
	public final T create() {
		if(flag) {
			return create2();
		}
		return null;
	}

	protected abstract T create2();

	@Override
	protected void applyComponentStyle(JComponent comp) {
		super.applyComponentStyle(comp);
	}

	protected abstract void registerWithMediator();

	@Override
	public void addMouseListener(MouseListener mouseListener) {
		this.getComponent().addMouseListener(mouseListener);
	}

	@Override
	public void addKeyListener(KeyListener keyListener) {
		this.getComponent().addKeyListener(keyListener);
	}

	@Override
	public boolean isFocusable() {
		return this.getComponent().isFocusable();
	}

	@Override
	public void setFocusable(boolean focusable) {
		this.getComponent().setFocusable(focusable);
	}

	@Override
	public boolean requestFocusInWindow() {
		return this.getComponent().requestFocusInWindow();
	}

	public EventMediator getEventMediator() {
		return this.mediator;
	}
}
