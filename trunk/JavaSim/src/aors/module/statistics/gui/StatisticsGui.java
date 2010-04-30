package aors.module.statistics.gui;

import javax.swing.JScrollPane;

import aors.module.GUIModule;
import aors.module.Module;
import aors.module.statistics.StatisticsCore;

/**
 * StatisticsGui
 * 
 * This class implements the GuiComponent of the Statistics-Module
 * 
 * @author Daniel Draeger
 * @since 02.11.2009
 */
public class StatisticsGui extends JScrollPane implements GUIModule {

  /** define required serial ID of this component **/
  private static final long serialVersionUID = -984318532159303363L;

  /** the Base module component **/
  private final StatisticsCore statsModule;
  private final TabStatistics statsPanel;
  private final String moduleName = "Statistical Analysis";

  /**
   * Create a new {@code StatisticsGui}.
   * 
   * @param module
   *          StatisticsCore
   */
  public StatisticsGui(StatisticsCore module) {
    this.statsModule = module;
    // prepare GUI components
    this.statsPanel = new TabStatistics(statsModule);
    this.statsPanel.setSize(this.getViewportBorderBounds().width, this
        .getViewportBorderBounds().height);
    setViewportView(this.statsPanel);
    this.setName(moduleName);
    this.setVisible(true);
  }

  /**
   * Usage: return the main window of the Statistics-Module
   * 
   * @return TabStatistics
   */
  public TabStatistics getTabStatistics() {
    return this.statsPanel;
  }

  @Override
  public Module getBaseComponent() {
    return this.statsModule;
  }

}
