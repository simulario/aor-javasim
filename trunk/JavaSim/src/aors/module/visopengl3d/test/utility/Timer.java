package aors.module.visopengl3d.test.utility;

/**
 * The Timer class can be used to measure time in milliseconds.
 * 
 * @author Sebastian Mucha
 * @since February 16th, 2010
 */
public class Timer {

  // Starting time
  private long startTime;

  // Elapsed time since the timer was started
  private long pauseTime;

  // Timer status
  private boolean started;
  private boolean paused;

  /**
   * Start the timer. The timer status is changed to 'started' and the starting
   * time is stored.
   */
  public void start() {

    // Timer is started
    started = true;

    // Timer is not paused
    paused = false;

    // Store the current system time
    startTime = System.currentTimeMillis();
  }

  /**
   * Stop the timer. The timer status is reset to default values.
   */
  public void stop() {

    // Timer is not started
    started = false;

    // Timer is not paused
    paused = false;
  }

  /**
   * When the timer is running and is not paused, this method will return the
   * elapsed time since the timer was started. If the timer is paused it will
   * return the elapsed time until the time was paused. Otherwise 0 is returned.
   * 
   * @return elapsed time in milliseconds
   */
  public long getTime() {

    if (started == true) {
      if (paused == true)
        // Return the elapsed time until the timer was paused
        return pauseTime;
      else
        // Return the elapsed time since the timer was started
        return System.currentTimeMillis() - startTime;
    } else {
      // Return 0 if the timer isn't running
      return 0;
    }
  }

  /**
   * Pause the timer. The timer status is set to 'paused' and the elapsed time
   * since the timer was started is stored.
   */
  public void pause() {

    if (started == true && paused == false) {
      // Timer is paused
      paused = true;

      // Store the elapsed time
      pauseTime = System.currentTimeMillis() - startTime;
    }
  }

  /**
   * Resume the timer. The timer status is set to 'started' again and the start
   * time is adjusted.
   */
  public void resume() {

    if (paused == true) {
      // Timer is not paused
      paused = false;

      // Reset the starting time
      startTime = System.currentTimeMillis() - pauseTime;
      pauseTime = 0;
    }
  }

  // Setter & Getter -----------------------------------------------------------

  public boolean isStarted() {
    return started;
  }

  public boolean isPaused() {
    return paused;
  }

}
