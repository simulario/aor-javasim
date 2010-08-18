package aors.module.agentControl.gui.renderer.flyingsaucer;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.renderer.flyingsaucer.formFields.AORSFormFieldFactory;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.DefaultFormSubmissionListener;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.simple.extend.XhtmlForm;
import org.xhtmlrenderer.simple.extend.form.FormField;
import org.xhtmlrenderer.simple.extend.form.FormFieldFactory;
import org.xhtmlrenderer.util.XRLog;

// Unfortunately vital components of the XhtmlForm are private and cannot be
// accessed from subclasses. So we have no other choice than duplicating the
// super class and extending / modifying it with our stuff.
public class AORSForm extends XhtmlForm {

	private static final String FS_DEFAULT_GROUP = "__fs_default_group_";
	private static int _defaultGroupCount = 1;
	private UserAgentCallback _userAgentCallback;
	private Map<Element, FormField> _componentCache;
	private Map<String, ButtonGroupWrapper> _buttonGroups;
	private EventMediator mediator;
//    private Element _parentFormElement;
//    private FormSubmissionListener _formSubmissionListener;

	public AORSForm(UserAgentCallback uac, Element e,
		FormSubmissionListener fsListener, EventMediator mediator) {
		super(uac, e, fsListener);
		this._userAgentCallback = uac;
		this._buttonGroups = new HashMap<String, ButtonGroupWrapper>();
		this._componentCache = new LinkedHashMap<Element, FormField>();
		this.mediator = mediator;
//			this._parentFormElement = e;
//			this._formSubmissionListener = fsListener;

		//sets listeners
	}

	public AORSForm(UserAgentCallback uac, Element e,
		FormSubmissionListener fsListener) {
		this(uac, e, fsListener, new EventMediator(null));
	}

	public AORSForm(UserAgentCallback uac, Element e, EventMediator mediator) {
		this(uac, e, new DefaultFormSubmissionListener(), mediator);
	}

	public AORSForm(UserAgentCallback uac, Element e) {
		this(uac, e, new DefaultFormSubmissionListener());
	}

	@Override
	public UserAgentCallback getUserAgentCallback() {
		return _userAgentCallback;
	}

	@Override
	public void addButtonToGroup(String groupName, AbstractButton button) {
		this.addButtonToGroup(groupName, button, null);
	}

	public void addButtonToGroup(String groupName, AbstractButton button,
		String value) {
		if(groupName == null) {
			groupName = createNewDefaultGroupName();
		}
		ButtonGroupWrapper group = _buttonGroups.get(groupName);
		if(group == null) {
			group = new ButtonGroupWrapper();
			_buttonGroups.put(groupName, group);
		}
		group.add(button, value);
	}

	private static String createNewDefaultGroupName() {
		return FS_DEFAULT_GROUP + ++_defaultGroupCount;
	}

	private static class ButtonGroupWrapper {

		private ButtonGroup _group;
		private AbstractButton _dummy;
		private Map<AbstractButton, String> _valueMap;

		public ButtonGroupWrapper() {
			_group = new ButtonGroup();
			_dummy = new JRadioButton();
			_valueMap = new HashMap<AbstractButton, String>();
			add(_dummy, null);
		}

		public void add(AbstractButton b, String v) {
			_group.add(b);
			_valueMap.put(b, v);
		}

		public void clearSelection() {
			_group.setSelected(_dummy.getModel(), true);
		}

		public String getSelectedValue() {
			Enumeration<AbstractButton> buttons = _group.getElements();
			AbstractButton button = null;
			while(buttons.hasMoreElements()) {
				button = buttons.nextElement();
				if(button.isSelected()) {
					break;
				}
			}
			if(button != null) {
				return _valueMap.get(button);
			}
			return "";
		}
	}

	public String getButtonGroupValue(String groupName) {
		if(_buttonGroups.containsKey(groupName)) {
			return _buttonGroups.get(groupName).getSelectedValue();
		}
		return "";
	}

	@Deprecated
	private static boolean isOldFormField(Element e) {
		String nodeName = e.getNodeName();
		return nodeName.equals("input") ||
			nodeName.equals("select") ||
			nodeName.equals("textarea");
	}

	private static boolean isFormField(Element e) {
		String nodeName = e.getNodeName();
		return nodeName.equals("button") ||
			nodeName.equals("checkbox") ||
			nodeName.equals("dialog") ||
			nodeName.equals("radiobutton") ||
			nodeName.equals("select") ||
			nodeName.equals("slider") ||
			nodeName.equals("textarea") ||
			nodeName.equals("textfield") ||
//			|| nodeName.equals("visualization");
			nodeName.equals("updateableArea");
	}

//	@Override
//	@Deprecated
//	public JComponent addComponent(Element e) {
//		if(this._componentCache.containsKey(e)) {
//			return this._componentCache.get(e).getComponent();
//		}
//		if(!isOldFormField(e)) {
//			return null;
//		}
//		FormField field = FormFieldFactory.create(e, this);
//		if(field == null) {
//			XRLog.layout("Unknown field type: " + e.getNodeName());
//			return null;
//		}
//		_componentCache.put(e, field);
//		return field.getComponent();
//	}

	@Override
	public FormField addComponent(Element e, LayoutContext context, BlockBox box) {

		if(_componentCache.containsKey(e)) {
			return _componentCache.get(e);
		}

		// introduce our FormFieldFactory to handle the new elements
		if(isFormField(e)) {
			FormField field = AORSFormFieldFactory.create(this, context, box,
				this.mediator);
			if(field == null) {
				XRLog.layout("Unknown field type: " + e.getNodeName());
				return null;
			}
			_componentCache.put(e, field);
			return field;
		}

		// try old stuff
		if(!isOldFormField(e)) {
			FormField field = FormFieldFactory.create(this, context, box);
			if(field == null) {
				XRLog.layout("Unknown field type: " + e.getNodeName());
				return null;
			}
			_componentCache.put(e, field);
		}
		return null;
	}

	@Override
	public void reset() {
		super.reset();
		for(ButtonGroupWrapper bgw : _buttonGroups.values()) {
			bgw.clearSelection();
		}
		for(FormField field : _componentCache.values()) {
			field.reset();
		}
	}

	@Override
	public void submit(JComponent source) {
		// We provide a special treatment for submissions, so do nothing.
//		// If we don't have a <form> to tell us what to do, don't
//		// do anything.
//		if(_parentFormElement == null) {
//			return;
//		}
//
//		StringBuffer data = new StringBuffer();
//		String action = _parentFormElement.getAttribute("action");
//		data.append(action).append("?");
//		Iterator fields = _componentCache.entrySet().iterator();
//		boolean first = true;
//		while(fields.hasNext()) {
//			Map.Entry entry = (Map.Entry)fields.next();
//
//			FormField field = (FormField)entry.getValue();
//
//			if(field.includeInSubmission(source)) {
//				String[] dataStrings = field.getFormDataStrings();
//
//				for(int i = 0; i < dataStrings.length; i++) {
//					if(!first) {
//						data.append('&');
//					}
//
//					data.append(dataStrings[i]);
//					first = false;
//				}
//			}
//		}
//
//		if(_formSubmissionListener != null) {
//			_formSubmissionListener.submit(data.toString());
//		}
	}

	public static String collectText(Element e) {
		return XhtmlForm.collectText(e);
	}
}