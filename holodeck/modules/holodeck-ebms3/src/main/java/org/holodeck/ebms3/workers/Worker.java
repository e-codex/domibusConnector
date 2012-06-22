package org.holodeck.ebms3.workers;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;
import org.holodeck.common.soap.Util;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementMap;
import org.simpleframework.xml.Root;

/**
 * @author Hamid Ben Malek
 */
@Root(name="Worker")
public class Worker implements Runnable
{
//  private static final Log log = LogFactory.getLog(Worker.class.getName());
  private static final Logger log = Logger.getLogger(Worker.class.getName());

  @Attribute
  protected String name;

  @Attribute(required=false)
  protected boolean activate = false;

  @Attribute(required=false)
  protected long interval= 30000;

  @Attribute(required=false)
  protected int total = - 1;

  @Attribute(required=false)
  protected int groupSize = 0;

  @Attribute(required=false)
  protected long pause = interval;

  @Attribute(required=false)
  protected String workerClass = null;

  @ElementMap(entry="parameter", key="name", attribute=true, inline=true, required=false)
  protected Map<String, String> parameters;

  protected int counter = 0;
  protected Timer timer = null;
  //protected Task task = null;
  protected Runnable task = null;

  protected String state = Worker.VIRGIN;
  protected static final String VIRGIN = "virgin";
  protected static final String RUNNING = "running";
  protected static final String CANCELLED = "cancelled";
  protected TimerTask timerTask = null;

  public void start() { start(true); }
  public void start(boolean cycle)
  {
    if ( state.equals(Worker.RUNNING) || !activate ) return;
    getTask();
    try
    {
      if ( timer == null ) timer = new Timer();
      if ( cycle && total > 0 && counter >= total ) counter = 0;
      timer.schedule( getTimerTask() , interval, interval );
      state = Worker.RUNNING;
    }
    catch(Exception e) { e.printStackTrace(); }
  }

 /**
  *  Subclasses should override this method
  */
  public void task() {}

  public final void run()
  {
    if ( !activate ) return;
    if ( total == 0 || (total > 0 && counter >= total) )
    {
      terminate();
      return;
    }
    try
    {
      if ( counter > 0 && groupSize > 0 &&
           (counter % groupSize) == 0 && pause > 0 &&
            pause - interval > 0 )
      Thread.sleep(pause - interval);
    }
    catch(Exception e) { e.printStackTrace(); log.debug(e.getMessage()); }
    counter++;

    try
    {
      if ( getTask() != null ) task.run();
      else task();
    }
    catch(Exception ex)
    {
      ex.printStackTrace();
      log.debug(ex.getMessage());
      terminate();
      start(false);
    }
  }

  public void terminate()
  {
    if ( !state.equals(Worker.RUNNING) || state.equals(Worker.VIRGIN) )
          return;
    timerTask.cancel();
    timer.purge();
    state = Worker.CANCELLED;
  }

  public void update(Worker worker)
  {
    if ( worker == null ) return;
    this.interval = worker.getInterval();
    this.total = worker.getTotal();
    this.groupSize = worker.getGroupSize();
    this.pause = worker.getPause();
    this.workerClass = worker.getWorkerClass();
    this.parameters = worker.getParameters();
    if ( activate && !worker.isActivate() )
    {
      this.activate = worker.isActivate();
      terminate();
    }
    else if ( !activate && worker.isActivate() )
    {
      this.activate = worker.isActivate();
      start();
    }
    else this.activate = worker.isActivate();
  }

  public String getName() { return name; }
  public void setName(String name) { this.name = name; }

  public boolean isActivate() { return activate; }
  public void setActivate(boolean activate) { this.activate = activate; }

  public long getInterval() { return interval; }
  public void setInterval(long interval) { this.interval = interval; }

  public int getTotal() { return total; }
  public void setTotal(int total) { this.total = total; }

  public int getGroupSize() { return groupSize; }
  public void setGroupSize(int groupSize) { this.groupSize = groupSize; }

  public long getPause() { return pause; }
  public void setPause(long pause) { this.pause = pause; }

  public String getWorkerClass() { return workerClass; }
  public void setWorkerClass(String workerClass)
  {
    this.workerClass = workerClass;
  }

  public void setParameters(Map<String, String> parameters)
  {
    this.parameters = parameters;
  }
  public Map<String, String> getParameters() { return parameters; }

  public Runnable getTask()
  {
    if ( task != null ) return task;
    if ( workerClass == null || workerClass.trim().equals("") ) return null;
    try
    {
      task = (Runnable) Util.createInstance(workerClass);
      if ( task instanceof Task ) ((Task)task).setParameters(parameters);
    }
    catch(Exception ex) { log.debug(ex.getMessage()); }
    return task;
  }

  public TimerTask getTimerTask()
  {
    if ( timerTask != null && !state.equals(Worker.CANCELLED) )
         return timerTask;
    timerTask = new TimerTaskWrapper(this);
    return timerTask;
  }
}