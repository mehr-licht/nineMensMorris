import java.io.PrintWriter;
import java.io.StringWriter;


public class Log {


  /** Critical errors. The application may no longer work correctly. */
  static public final int LEVEL_ERROR = 5;
  /** Important warnings. The application will continue to work correctly. */
  static public final int LEVEL_WARN = 4;
  /** Informative messages. Typically used for deployment. */
  static public final int LEVEL_INFO = 3;
  /** Debug messages. This level is useful during development. */
  static public final int LEVEL_DEBUG = 2;
  /** Trace messages. A lot of information is logged, so this level is usually only needed when debugging a problem. */
  static public final int LEVEL_TRACE = 1;

  /** The level of messages that will be logged. Compiling this and the booleans below as "final" will cause the compiler to
   * remove all "if (Log.info) ..." type statements below the set level. */
  static private int level = LEVEL_INFO;

  /** True when the ERROR level will be logged. */
  static public boolean ERROR = level <= LEVEL_ERROR;
  /** True when the WARN level will be logged. */
  static public boolean WARN = level <= LEVEL_WARN;
  /** True when the INFO level will be logged. */
  static public boolean INFO = level <= LEVEL_INFO;
  /** True when the DEBUG level will be logged. */
  static public boolean DEBUG = level <= LEVEL_DEBUG;
  /** True when the TRACE level will be logged. */
  static public boolean TRACE = level <= LEVEL_TRACE;

  /** Sets the level to Log. If a version of this class is being used that has a final Log level, this has no affect. */
  static public void set (int level) {
    // Comment out method contents when compiling fixed level JARs.
    Log.level = level;
    ERROR = level <= LEVEL_ERROR;
    WARN = level <= LEVEL_WARN;
    INFO = level <= LEVEL_INFO;
    DEBUG = level <= LEVEL_DEBUG;
    TRACE = level <= LEVEL_TRACE;
  }

  static private logger logger = new logger();

  static public void error (String message) {
    if (ERROR) logger.log(LEVEL_ERROR, null, message, null);
  }

  static public void warn (String message) {
    if (WARN) logger.log(LEVEL_WARN, null, message, null);
  }

  static public void info (String message) {
    if (INFO) logger.log(LEVEL_INFO, null, message, null);
  }

  private Log() {
  }

  /** Performs the actual logging. Default implementation logs to System.out. Extended and use {@link Log#logger} set to handle
   * logging differently. */
  static public class logger {
    private final long firstlogTime = System.currentTimeMillis();

    public void log (int level, String category, String message, Throwable ex) {
      StringBuilder builder = new StringBuilder(256);

      long time = System.currentTimeMillis() - firstlogTime;
      long minutes = time / (1000 * 60);
      long seconds = time / (1000) % 60;
      if (minutes <= 9) builder.append('0');
      builder.append(minutes);
      builder.append(':');
      if (seconds <= 9) builder.append('0');
      builder.append(seconds);

      switch (level) {
        case LEVEL_ERROR:
          builder.append(" ERROR: ");
          break;
        case LEVEL_WARN:
          builder.append("  WARN: ");
          break;
        case LEVEL_INFO:
          builder.append("  INFO: ");
          break;
        case LEVEL_DEBUG:
          builder.append(" DEBUG: ");
          break;
        case LEVEL_TRACE:
          builder.append(" TRACE: ");
          break;
      }

      if (category != null) {
        builder.append('[');
        builder.append(category);
        builder.append("] ");
      }

      builder.append(message);

      if (ex != null) {
        StringWriter writer = new StringWriter(256);
        ex.printStackTrace(new PrintWriter(writer));
        builder.append('\n');
        builder.append(writer.toString().trim());
      }

      print(builder.toString());
    }

    /** Prints the message to System.out. Called by the default implementation of {@link #log(int, String, String, Throwable)}. */
    protected void print (String message) {
      System.out.println(message);
    }
  }
}
