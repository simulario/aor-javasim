package aors.module.agentControl.gui.renderer.flyingsaucer.formFields;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.Receiver;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.flyingsaucer.AORSForm;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTextAreaUI;
import javax.swing.plaf.basic.BasicTextUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.css.constants.CSSName;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.css.style.FSDerivedValue;
import org.xhtmlrenderer.css.style.derived.BorderPropertySet;
import org.xhtmlrenderer.css.style.derived.LengthValue;
import org.xhtmlrenderer.css.style.derived.RectPropertySet;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;
import org.xhtmlrenderer.util.GeneralUtil;

class TextAreaField extends FormField<JComponent> implements Receiver {

	private JTextArea _textarea;

	public TextAreaField(Element e, AORSForm form, LayoutContext context,
		BlockBox box, EventMediator mediator) {
		super(e, form, context, box, mediator);
	}

	@Override
	protected JComponent create2() {
		int rows = 4;
		int cols = 10;

		if(hasAttribute("rows")) {
			int parsedRows = GeneralUtil.parseIntRelaxed(getAttribute("rows"));

			if(parsedRows > 0) {
				rows = parsedRows;
			}
		}

		if(hasAttribute("cols")) {
			int parsedCols = GeneralUtil.parseIntRelaxed(getAttribute("cols"));

			if(parsedCols > 0) {
				cols = parsedCols;
			}
		}

		_textarea = new JTextArea(rows, cols) {

			private final static long serialVersionUID = 1L;
			int columnWidth = 0;

			//override getColumnWidth to base on 'o' instead of 'm'.  more like other
			//browsers
			@Override
			protected int getColumnWidth() {
				if(columnWidth == 0) {
					FontMetrics metrics = getFontMetrics(getFont());
					columnWidth = metrics.charWidth('o');
				}
				return columnWidth;
			}

			//Avoid Swing bug #5042886.   This bug was fixed in java6
			@Override
			public Dimension getPreferredScrollableViewportSize() {
				Dimension size = super.getPreferredScrollableViewportSize();
				size = (size == null) ? new Dimension(400, 400) : size;
				Insets insets = getInsets();

				size.width = (getColumns() == 0) ? size.width : getColumns() *
					getColumnWidth() + insets.left + insets.right;
				size.height = (getRows() == 0) ? size.height : getRows() *
					getRowHeight() + insets.top + insets.bottom;
				return size;
			}
		};

		_textarea.setWrapStyleWord(true);
		_textarea.setLineWrap(true);

		_textarea.setEditable(!(hasAttribute("readonly") &&
			getAttribute("readonly").equals("true")));

		JScrollPane scrollpane = new JScrollPane(_textarea);
		scrollpane.setVerticalScrollBarPolicy(ScrollPaneConstants.
			VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.
			HORIZONTAL_SCROLLBAR_AS_NEEDED);

		applyComponentStyle(_textarea, scrollpane);

		return scrollpane;
	}

	protected void applyComponentStyle(JTextArea textArea,
		JScrollPane scrollpane) {
		super.applyComponentStyle(textArea);

		CalculatedStyle style = getBox().getStyle();
		BorderPropertySet border = style.getBorder(null);
		boolean disableOSBorder = (border.leftStyle() != null &&
			border.rightStyle() != null || border.topStyle() != null ||
			border.bottomStyle() != null);

		RectPropertySet padding = style.getCachedPadding();

		Integer paddingTop = getLengthValue(style, CSSName.PADDING_TOP);
		Integer paddingLeft = getLengthValue(style, CSSName.PADDING_LEFT);
		Integer paddingBottom = getLengthValue(style, CSSName.PADDING_BOTTOM);
		Integer paddingRight = getLengthValue(style, CSSName.PADDING_RIGHT);

		int top = 2;
		if(paddingTop != null) {
			Math.max(top, paddingTop.intValue());
		}
		int left = 3;
		if(paddingLeft != null) {
			Math.max(left, paddingLeft.intValue());
		}
		int bottom = 2;
		if(paddingBottom != null) {
			Math.max(bottom, paddingBottom.intValue());
		}
		int right = 3;
		if(paddingRight != null) {
			Math.max(right, paddingRight.intValue());
		}

		//if a border is set or a background color is set, then use a special
		//JButton with the BasicButtonUI.
		if(disableOSBorder) {
			//when background color is set, need to use the BasicButtonUI, certainly
			//when using XP l&f
			BasicTextUI ui = new BasicTextAreaUI();
			textArea.setUI(ui);
			scrollpane.setBorder(null);
		}

		textArea.setMargin(new Insets(top, left, bottom, right));

		padding.setRight(0);
		padding.setLeft(0);
		padding.setTop(0);
		padding.setBottom(0);

		FSDerivedValue widthValue = style.valueByName(CSSName.WIDTH);
		if(widthValue instanceof LengthValue) {
			intrinsicWidth = new Integer(getBox().getContentWidth() + left + right);
		}

		FSDerivedValue heightValue = style.valueByName(CSSName.HEIGHT);
		if(heightValue instanceof LengthValue) {
			intrinsicHeight = new Integer(getBox().getHeight() + top + bottom);
		}
	}

	@Override
	protected FormFieldState loadOriginalState() {
		return FormFieldState.fromString(
			AORSForm.collectText(getElement()));
	}

	@Override
	protected void applyOriginalState2() {
		_textarea.setText(getOriginalState().getValue());
	}

	@Override
	protected String[] getFieldValues() {
		JTextArea textarea = (JTextArea)((JScrollPane)getComponent()).getViewport().
			getView();
		return new String[] {
				textarea.getText()
			};
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(!this.getComponent().equals(evt.getSource())) {
			this._textarea.setText(evt.getNewValue().toString());
		}
	}

	@Override
	protected void registerWithMediator() {
		mediator.addReceiver(getAttribute(Receiver.RECEIVER_ATTRIBUTE), this,
			this.getValue());
		mediator.addSender(getAttribute(Sender.SENDER_ATTRIBUTE), this);
		_textarea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				notifyMediator();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				notifyMediator();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				notifyMediator();
			}

			private void notifyMediator() {
				mediator.propertyChange(new PropertyChangeEvent(TextAreaField.this,
					getAttribute(Receiver.RECEIVER_ATTRIBUTE), null, TextAreaField.this.getValue()));
			}
		});
	}

	@Override
	public String getValue() {
		Document doc = this._textarea.getDocument();
		try {
			return doc.getText(0, doc.getLength());
		} catch(BadLocationException e) {
			return "";
		}
	}
}
