package aors.module.visopengl.gui;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.media.opengl.awt.GLCanvas;

import aors.logger.model.SimulationStep;
import aors.module.evt.ModuleEventSlowDownSimulation;
import aors.module.evt.ModuleEventSpeedUpSimulation;
import aors.module.visopengl.Visualization;
import aors.module.visopengl.engine.UpdateManager;
import aors.module.visopengl.utility.Timer;

public class VisualizationThread extends Thread {

  // Visualization panel
  private VisualizationPanel visPanel;

  // OpenGL canvas
  private GLCanvas canvas;

  // Thread state
  private boolean visRunning = true;
  private boolean visPaused;

  // a reference to the parent of this thread.
  private Visualization parent = null;

  // Maximal frame rate
  private final int MAX_FRAME_RATE = 100;

  // Simulation step buffer
  private Queue<SimulationStep> buffer = new ConcurrentLinkedQueue<SimulationStep>();

  // Update manager
  private UpdateManager uptManager;

  /**
   * Creates visualization thread.
   * 
   * @param guiComponent
   */
  public VisualizationThread(GUIComponent guiComponent) {
    visPanel = guiComponent.getContent().getVisualizationPanel();
    visPanel.setVisThread(this);
    canvas = guiComponent.getContent().getDrawingPanel().getCanvas();
    this.parent = (Visualization) guiComponent.getBaseComponent();
  }

  public void run() {
    // Frame counter
    int frameCount = 0;

    // Timers used to cap the frame rate and update labels
    Timer frameTimer = new Timer();
    Timer updateTimer = new Timer();

    // Elements inside of the step buffer
    int bufferSize = 0;

    // current step
    long currentStep = 0;

    // Fire up the update timer
    updateTimer.start();

    while (visRunning) {

      /*
       * When the simulation is not paused, rendering will be performed,
       * otherwise the thread will sleep to free up the CPU.
       */
      if (!visPaused) {
        // Fire up the frame timer
        frameTimer.start();

        if (!buffer.isEmpty()) {
          // Get the buffer size
          bufferSize = buffer.size();

          // Update buffer size label
          visPanel.updateBufferSizeLabel(bufferSize);

          /*
           * When drawing of all steps is enabled, only one element will be
           * removed from the step buffer, otherwise all elements are removed.
           */
          if (visPanel.drawAllSteps()) {
            currentStep = uptManager.readSimulationSteps(1, buffer);
            visPanel.updateSimStepLabel(currentStep);
          } else {
            currentStep = uptManager.readSimulationSteps(bufferSize, buffer);
            visPanel.updateSimStepLabel(currentStep);
          }

          // try to keep the buffer in limit of 20-30 steps. If more steps are
          // stored, then ask for simulation slow down, otherwise ask for speed
          // up. Do this for each 10 steps not on each step for more
          // performance.
          if (visPanel.isIntelliBufferOptionActiveState() && currentStep != 0
              && currentStep % 10 == 0) {
            if (bufferSize > 20) {
              this.parent
                  .notifyVisualizationModuleEvent(new ModuleEventSlowDownSimulation(this));
            }
            if (bufferSize < 32) {
              this.parent
                  .notifyVisualizationModuleEvent(new ModuleEventSpeedUpSimulation(this));
            }
          }
        } else {
          // Pause the thread for a short time to free the CPU
          Thread.yield();
        }

        // Render a frame
        canvas.display();

        // Update the frame rate label (once every second)
        if (updateTimer.getTime() > 1000) {
          visPanel.updateFrameRateLabel(frameCount);

          // Reset frame counter and update timer
          frameCount = 0;
          updateTimer.start();
        }

        // Get the time it took to render the frame
        long elapsedTime = frameTimer.getTime();

        // Cap the frame rate
        if (elapsedTime < 1000 / MAX_FRAME_RATE) {
          try {
            Thread.sleep(1000 / MAX_FRAME_RATE - elapsedTime);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }

        frameCount++;
      } else {
        // Sleep to free the CPU
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public void setVisRunning(boolean visRunning) {
    this.visRunning = visRunning;
  }

  public void setVisPaused(boolean visPaused) {
    this.visPaused = visPaused;
  }

  public Queue<SimulationStep> getBuffer() {
    return buffer;
  }

  public UpdateManager getUptManager() {
    return uptManager;
  }

  public void setUptManager(UpdateManager uptManager) {
    this.uptManager = uptManager;
  }

  public boolean isVisRunning() {
    return visRunning;
  }

  public boolean isVisPaused() {
    return visPaused;
  }

  public void setBuffer(Queue<SimulationStep> buffer) {
    this.buffer = buffer;
  }

  public GLCanvas getCanvas() {
    return canvas;
  }

  public void setCanvas(GLCanvas canvas) {
    this.canvas = canvas;
  }
}
