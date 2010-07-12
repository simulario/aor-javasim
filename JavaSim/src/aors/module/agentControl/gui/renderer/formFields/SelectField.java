package aors.module.agentControl.gui.renderer.formFields;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.form.FormFieldState;

import aors.module.agentControl.gui.interaction.EventMediator;
import aors.module.agentControl.gui.interaction.Receiver;
import aors.module.agentControl.gui.interaction.Sender;
import aors.module.agentControl.gui.renderer.AORSForm;

class SelectField extends FormField<JComboBox> implements Receiver {

  public SelectField(Element e, AORSForm form, LayoutContext context,
    BlockBox box, EventMediator mediator) {
    super(e, form, context, box, mediator);
  }

  @Override
  protected JComboBox create2() {
    List<NameValuePair> optionList = createList();

    JComboBox select = new JComboBox(optionList.toArray());

    applyComponentStyle(select);

    select.setEditable(false);
    select.setRenderer(new CellRenderer());
    select.addItemListener(new HeadingItemListener());

    return select;
  }

  @Override
  protected FormFieldState loadOriginalState() {
    ArrayList<Integer> list = new ArrayList<Integer>();

    NodeList options = getElement().getElementsByTagName("option");

    for(int i = 0; i < options.getLength(); i++) {
      Element option = (Element)options.item(i);

      if(option.hasAttribute("selected") && option.getAttribute("selected").
				equalsIgnoreCase("selected")) {
        list.add(new Integer(i));
      }
    }

    return FormFieldState.fromList(list);
  }

  @Override
  protected void applyOriginalState2() {
    JComboBox select = getComponent();

    // This looks strange, but basically since this is a single select, and
    // someone might have put selected="selected" on more than a single option
    // I believe that the correct play here is to select the _last_ option with
    // that attribute.
    int[] indices = getOriginalState().getSelectedIndices();

    if(indices.length == 0) {
      select.setSelectedIndex(0);
    } else {
      select.setSelectedIndex(indices[indices.length - 1]);
    }
  }

  @Override
  protected String[] getFieldValues() {
    JComboBox select = getComponent();

    NameValuePair selectedValue = (NameValuePair)select.getSelectedItem();

    if(selectedValue != null) {
      if(selectedValue.getValue() != null) {
        return new String[] {selectedValue.getValue()};
      }
    }

    return new String[] {};
  }

  private List<NameValuePair> createList() {
    List<NameValuePair> list = new ArrayList<NameValuePair>();
    addChildren(list, getElement(), 0);
    return list;
  }

  private void addChildren(List<NameValuePair> list, Element e, int indent) {
    NodeList children = e.getChildNodes();

    for(int i = 0; i < children.getLength(); i++) {
      if(!(children.item(i) instanceof Element)) {
        continue;
      }
      Element child = (Element)children.item(i);

      if("option".equals(child.getNodeName())) {
        // option tag, add it
        String optionText = AORSForm.collectText(child);
        String optionValue = optionText;

        if(child.hasAttribute("value")) {
          optionValue = child.getAttribute("value");
        }

        list.add(new NameValuePair(optionText, optionValue, indent));

      } else if("optgroup".equals(child.getNodeName())) {
        // optgroup tag, append heading and indent children
        String titleText = child.getAttribute("label");
        list.add(new NameValuePair(titleText, null, indent));
        addChildren(list, child, indent + 1);
      }
    }
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if(!this.getComponent().equals(evt.getSource())) {
      JComboBox select = this.getComponent();
      for(int i = 0; i < select.getItemCount(); i++) {
        if(select.getItemAt(i).toString().equals(evt.getNewValue().toString())) {
          select.setSelectedIndex(i);
        }
      }
    }
  }

	@Override
	protected void registerWithMediator() {
    mediator.addReceiver(getAttribute(Receiver.RECEIVER_ATTRIBUTE), this,
			this.getValue());
    mediator.addSender(getAttribute(Sender.SENDER_ATTRIBUTE), this);
    this.getComponent().addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        mediator.propertyChange(new PropertyChangeEvent(SelectField.this,
          getAttribute(Receiver.RECEIVER_ATTRIBUTE), null,
					SelectField.this.getValue()));
      }
    });
	}

  @Override
  public String getValue() {
    return this.getComponent().getSelectedItem().toString();
  }

  /**
   * Provides a simple container for name/value data, such as that used
   * by the &lt;option&gt; elements in a &lt;select&gt; list.
   * <p>
   * When the value is {@code null}, this pair is used as a heading and
   * should not be selected by itself.
   * <p>
   * The indent property was added to support indentation of items as
   * children below headings.
   */
  private static class NameValuePair {

    private String _name;
    private String _value;
    private int _indent;

    public NameValuePair(String name, String value, int indent) {
      _name = name;
      _value = value;
      _indent = indent;
    }

    public String getName() {
      return _name;
    }

    public String getValue() {
      return _value;
    }

    public int getIndent() {
      return _indent;
    }

    @Override
    public String toString() {
      String txt = getName();
      for(int i = 0; i < getIndent(); i++) {
        txt = "    " + txt;
      }
      return txt;
    }
  }

  /**
   * Renderer for ordinary items and headings in a List.
   */
  private static class CellRenderer extends DefaultListCellRenderer {

    private final static long serialVersionUID = 1L;

    @Override
    public Component getListCellRendererComponent(JList list, Object value,
      int index, boolean isSelected, boolean cellHasFocus) {
      NameValuePair pair = (NameValuePair)value;

      if(pair != null && pair.getValue() == null) {
        // render as heading as such
        super.getListCellRendererComponent(list, value, index, false, false);
        Font fold = getFont();
        Font fnew = new Font(fold.getName(), Font.BOLD | Font.ITALIC,
					fold.getSize());
        setFont(fnew);
      } else {
        // other items as usuall
        super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
      }

      return this;
    }
  }

  /**
   * Helper class that makes headings inside a list unselectable
   * <p>
   * This is an {@linkplain ItemListener} for comboboxes, and a
   * {@linkplain ListSelectionListener} for lists.
   */
  private static class HeadingItemListener implements ItemListener,
		ListSelectionListener {

    private Object oldSelection = null;
    private int[] oldSelections = new int[0];

    @Override
    public void itemStateChanged(ItemEvent e) {
      if(e.getStateChange() != ItemEvent.SELECTED) {
        return;
      }
      // only for comboboxes
      if(!(e.getSource() instanceof JComboBox)) {
        return;
      }
      JComboBox combo = (JComboBox)e.getSource();

      if(((NameValuePair)e.getItem()).getValue() == null) {
        // header selected: revert to old selection
        combo.setSelectedItem(oldSelection);
      } else {
        // store old selection
        oldSelection = e.getItem();
      }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
      // only for lists
      if(!(e.getSource() instanceof JList)) {
        return;
      }
      JList list = (JList)e.getSource();
      ListModel model = list.getModel();

      // deselect all headings
      for(int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
        if(!list.isSelectedIndex(i)) {
          continue;
        }
        NameValuePair pair = (NameValuePair)model.getElementAt(i);
        if(pair != null && pair.getValue() == null) {
          // We have a heading, remove it. As this handler is called
          // as a result of the resulting removal and we do process
          // the events while the value is adjusting, we don't need
          // to process any other headings here.
          // BUT if there'll be no selection anymore because by selecting
          // this one the old selection was cleared, restore the old
          // selection.
          if(list.getSelectedIndices().length == 1) {
            list.setSelectedIndices(oldSelections);
          } else {
            list.removeSelectionInterval(i, i);
          }
          return;
        }
      }

      // if final selection: store it
      if(!e.getValueIsAdjusting()) {
        oldSelections = list.getSelectedIndices();
      }
    }
  }
}
