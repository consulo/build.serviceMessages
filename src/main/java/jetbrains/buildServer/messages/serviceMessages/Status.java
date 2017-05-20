package jetbrains.buildServer.messages.serviceMessages;

import java.io.Serializable;

public final class Status implements Serializable {
  public static final Status UNKNOWN = new Status(0, "UNKNOWN");
  public static final Status NORMAL = new Status(1, "NORMAL");
  public static final Status WARNING = new Status(2, "WARNING");
  public static final Status FAILURE = new Status(3, "FAILURE");
  public static final Status ERROR = new Status(4, "ERROR");

  private static final Status[] ourAllInstances = new Status[] {
    UNKNOWN, NORMAL, WARNING, FAILURE, ERROR
  };

  private final byte myPriority;
  private final String myStatusName;

  Status(int priority, String name) {
    myPriority = (byte) priority;
    myStatusName = name;
  }

  public byte getPriority() {
    return myPriority;
  }

  public String getText() {
    return myStatusName.equals("NORMAL") ? "SUCCESS" : myStatusName;
  }

  public boolean isSuccessful() {
    return getPriority() <= WARNING.getPriority() && this != UNKNOWN;
  }

  public boolean isFailed() {
    return getPriority() > WARNING.getPriority();
  }

  public boolean isIgnored() {
    return Status.UNKNOWN.equals(this);
  }

  public static Status getStatus(final int priority) {
    switch (priority) {
      case 1: return NORMAL;
      case 2: return WARNING;
      case 3: return FAILURE;
      case 4: return ERROR;
      default: return UNKNOWN;
    }
  }

  public static Status getStatus(final String statusName) {
    for (int i = 0; i < ourAllInstances.length; i++) {
      Status instance = ourAllInstances[i];
      if (instance.myStatusName.equals(statusName) || instance.getText().equals(statusName)) {
        return instance;
      }
    }
    return null;
  }

  public String toString() {
    return myStatusName;
  }

  /**
   * Returns true if this status has higher priority than specified status.
   * @param status status
   * @return see above
   */
  public boolean above(final Status status) {
    return getPriority() > status.getPriority();
  }
}
