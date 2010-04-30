/*************************************************************************************************************
 * AgentObject-Object-Relationship (AOR) Simulation
 * Ontologically well-founded and closer-to-reality simulation of complex discrete event scenarios
 *
 * AOR-JSim v.2
 *
 * Copyright (C) 2008 AOR Team: Daniel Draeger, Adrian Giurca, Emilian Pascalau, Andreas Post, Marco Pehla,
 * Gerd Wagner, Jens Werner, Mircea Diaconescu
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 **************************************************************************************************************/
package aors.module.sound.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import aors.module.GUIModule;
import aors.module.Module;
import aors.module.sound.SoundController;

/**
 * TabSound
 * 
 * @author Mircea Diaconescu
 * @date November 1, 2009
 * @version $Revision: 1.0 $
 */

public class TabSound extends JScrollPane implements GUIModule {

  /**
   * The UID serial version
   */
  private static final long serialVersionUID = 3039620258057804087L;

  /**
   * The title for activate sound for event buttons
   */
  public static String BUTTON_TITLE_ENABLE = "Enable";

  /**
   * The title for disable sound for event buttons
   */
  public static String BUTTON_TITLE_DISABLE = "Disable";

  /**
   * The sound module core component
   */
  private SoundController sound;

  /**
   * The content panel
   */
  private JPanel contentPanel;

  /**
   * The top panel from the TabSound
   */
  private JPanel topPanel;

  /**
   * The center panel from the TabSound
   */
  private JPanel centerPanel;

  /**
   * The sound enable/disable button
   */
  private JButton soundEnableButton;

  /**
   * The events-sounds table
   */
  private JTable tableSoundEvent;

  /**
   * The table column names
   */
  private Vector<String> tableColumns;

  /**
   * The table rows
   */
  private Vector<Object> tableRows;

  /**
   * Create a new TabSound object
   * 
   * @param soundCore
   *          the sound module core component reference
   */
  public TabSound(SoundController soundCore) {
    // set the core component
    this.sound = soundCore;

    // create the enabe/disable button
    this.soundEnableButton = new JButton("Disable Sound");
    this.soundEnableButton.addMouseListener(new MouseAdapter() {
      public void mouseReleased(MouseEvent e) {

        boolean soundState = ((SoundController) getBaseComponent())
            .getEnabled();

        if (soundState) {
          soundEnableButton.setText("Enable Sound");
        } else {
          soundEnableButton.setText("Disable Sound");
        }

        ((SoundController) getBaseComponent()).setEnabled(!soundState);
      }
    });

    // create the content panel
    this.contentPanel = new JPanel();
    this.contentPanel.setLayout(new BorderLayout());

    // define the columns of the events-sounds table
    this.tableColumns = new Vector<String>();
    this.tableColumns.add("Event Type");
    this.tableColumns.add("Sound Type");
    this.tableColumns.add("Active State");

    // create the center panel
    this.centerPanel = new JPanel();
    this.centerPanel.setBorder(new EtchedBorder());
    this.centerPanel.setLayout(new BorderLayout());

    // create the topPanel
    this.topPanel = new JPanel();
    this.topPanel.setSize(100, this.getSize().width);
    this.topPanel.setBorder(new EtchedBorder());
    this.topPanel.setLayout(new BorderLayout());
    this.topPanel.add(BorderLayout.CENTER, new JLabel(
        "Descripton: control sounds used by simulations."));
    this.topPanel.add(BorderLayout.LINE_END, this.soundEnableButton);

    // add panels to TabSound
    this.setViewportView(this.contentPanel);
    this.contentPanel.add(BorderLayout.NORTH, topPanel);
    this.contentPanel.add(BorderLayout.CENTER, centerPanel);
  }

  /**
   * Initialize the table with events-sounds
   */
  public void initSoundEventTable(HashMap<String, String> rows,
      HashMap<String, Boolean> activeState) {

    // remove the old table
    this.centerPanel.removeAll();

    // define rows for events-sounds table
    this.tableRows = new Vector<Object>();
    DefaultTableModel tableModel = new DefaultTableModel(this.tableRows,
        this.tableColumns) {
      /**
       * Serial UID
       */
      private static final long serialVersionUID = 9196262637610156965L;

      /**
       * The cell centent can be changed or not ?
       */
      public boolean isCellEditable(int row, int col) {
        if (col == 2) {
          return true;
        }
        return false;
      }
    };

    // keys iterator
    Iterator<String> iter = rows.keySet().iterator();

    // initialize table rows
    while (iter.hasNext()) {
      String key = iter.next();
      Vector<Object> row = new Vector<Object>();
      row.add(key);
      row.add(rows.get(key));
      row.add(activeState.get(key));
      tableModel.addRow(row);
    }

    // create the events-sounds table
    this.tableSoundEvent = new JTable(tableModel);

    // events-sounds table is extending to fulfill all width of the view
    this.tableSoundEvent.setFillsViewportHeight(true);

    // add events-sounds table in a scroll panel - it may needs scroll bars
    JScrollPane tableScrollPanel = new JScrollPane(this.tableSoundEvent);

    // header of the events-sounds table is set to BOLD
    Font font = this.tableSoundEvent.getTableHeader().getFont();
    font = font.deriveFont(Font.BOLD);
    this.tableSoundEvent.getTableHeader().setFont(font);

    // the events-sounds table column for active state is set to use check boxes
    this.tableSoundEvent.getColumn(this.tableColumns.get(2)).setCellRenderer(
        new CheckBoxCellRenderer());
    this.tableSoundEvent.getColumn(this.tableColumns.get(2)).setCellEditor(
        new CheckBoxCellEditor());

    // set the cell width for the activate/disable sound column
    this.tableSoundEvent.setRowSelectionAllowed(false);

    // add the scroll bar panel with the table to the center panel
    this.centerPanel.add(tableScrollPanel);
  }

  @Override
  public Module getBaseComponent() {
    return this.sound;
  }

  /**
   * Define the check box cell renderer
   * 
   * @author Mircea Diaconescu
   * @date November 1, 2009
   * @version $Revision: 1.0 $
   */
  private class CheckBoxCellRenderer extends JCheckBox implements
      TableCellRenderer {
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -1173990311389399859L;

    /**
     * Create a new cell box renderer object
     */
    public CheckBoxCellRenderer() {
      super();
      setOpaque(true);
      setHorizontalAlignment(SwingConstants.CENTER);
      setSelected(true);
      setBackground(Color.white);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
        boolean isSelected, boolean hasFocus, int row, int column) {

      if (value instanceof Boolean) {
        setSelected(((Boolean) value).booleanValue());
      } else {
        return null;
      }

      return this;
    }

  }

  /**
   * Define the check box cell editor
   * 
   * @author Mircea Diaconescu
   * @date November 1, 2009
   * @version $Revision: 1.0 $
   */
  private class CheckBoxCellEditor extends AbstractCellEditor implements
      TableCellEditor {
    protected JCheckBox checkBox;

    /**
     * Serial UID
     */
    private static final long serialVersionUID = 7420521726471244826L;

    /**
     * Create a new CheckBox cell editor object
     */
    public CheckBoxCellEditor() {
      checkBox = new JCheckBox();
      checkBox.setHorizontalAlignment(SwingConstants.CENTER);
      checkBox.setBackground(Color.white);
      checkBox.setSelected(true);

      checkBox.addMouseListener(new MouseListener() {

        @Override
        public void mouseClicked(MouseEvent e) {
          // TODO Auto-generated method stub

        }

        @Override
        public void mouseEntered(MouseEvent e) {
          // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
          // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent e) {
          // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent e) {

          int row = tableSoundEvent.getSelectedRow();
          int column = tableSoundEvent.getSelectedColumn();

          if (column == 2 && row >= 0) {
            Boolean enabled = !checkBox.isSelected();
            String eventTypeName = (String) tableSoundEvent.getModel()
                .getValueAt(row, 0);

            ((SoundController) getBaseComponent()).enableSoundForEvent(
                eventTypeName, !enabled);

          }
        }

      });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
        boolean isSelected, int row, int column) {

      return checkBox;
    }

    @Override
    public Object getCellEditorValue() {
      return Boolean.valueOf(checkBox.isSelected());
    }
  }

}
