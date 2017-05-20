/*
 * Copyright 2000-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.messages.serviceMessages;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class to parse messages like
 *
 * ##teamcity[&lt;message name> &lt;param name>='&lt;param value>' &lt;param name>='&lt;param value>'...]
 * or
 * ##teamcity[&lt;message name> '&lt; argument>']
 * Argument is optional.
 *
 */
public class ServiceMessage {
  @NotNull public static final String SERVICE_MESSAGE_START = "##teamcity[";
  public static final String TAGS_SEPARATOR = ",";
  @NonNls public static final String ENABLE = "enableServiceMessages";
  @NonNls public static final String DISABLE = "disableServiceMessages";

  private static final String RESERVED_ATTRIBUTE_PREFIX = "tc:";

  public static final String ARG_ATTRIBUTE = RESERVED_ATTRIBUTE_PREFIX + "arg";
  public static final String TAGS_ATRRIBUTE = RESERVED_ATTRIBUTE_PREFIX + "tags";


  @NotNull private static final String SERVICE_MESSAGE_END = "]";

  @NotNull private static final String FORMAT_WITH_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
  @NotNull private static final String FORMAT_WITHOUT_TZ = "yyyy-MM-dd'T'HH:mm:ss.SSS";

  private static final int FORMAT_WITHOUT_TZ_LEN = FORMAT_WITHOUT_TZ.replace("'", "").length();

  @NotNull private static final Map<String, Class<? extends ServiceMessage>> SERVICE_MESSAGE_CLASSES = new HashMap<String, Class<? extends ServiceMessage>>();

  @NotNull private String myMessageName;
  @NotNull private final Map<String, String> myAttributes = new LinkedHashMap<String, String>();
  @Nullable private String myArgument;
  @Nullable private Timestamp myCreationTimestamp;
  @Nullable private String myFlowId;
  @NotNull private List<String> myTags = Collections.emptyList();

  static {
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.PROGRESS_MESSAGE, ProgressMessage.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.PROGRESS_START, ProgressStart.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.PROGRESS_FINISH, ProgressFinish.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.PUBLISH_ARTIFACTS, PublishArtifacts.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_SUITE_STARTED, TestSuiteStarted.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_SUITE_FINISHED, TestSuiteFinished.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_STARTED, TestStarted.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_FAILED, TestFailed.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_FINISHED, TestFinished.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_IGNORED, TestIgnored.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_STD_OUT, TestStdOut.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_STD_ERR, TestStdErr.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.BUILD_STATUS, BuildStatus.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.BUILD_NUMBER, BuildNumber.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.BUILD_STATISTIC_VALUE, BuildStatisticValue.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.TEST_NAVIGATION_INFO, TestNavigationInfo.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.BLOCK_OPENED, BlockOpened.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.BLOCK_CLOSED, BlockClosed.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.COMPILATION_STARTED, CompilationStarted.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.COMPILATION_FINISHED, CompilationFinished.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.MESSAGE, Message.class);
    SERVICE_MESSAGE_CLASSES.put(ServiceMessageTypes.INTERNAL_ERROR, InternalErrorMessage.class);
  }

  ServiceMessage() {
  }

  protected ServiceMessage(@NotNull final String messageName) {
    myMessageName = messageName;
  }

  protected ServiceMessage(@NotNull final String messageName, final String argument) {
    myMessageName = messageName;
    myArgument = argument;
  }

  protected ServiceMessage(@NotNull final String messageName,
                           @NotNull final Map<String, String> attributes) {
    myMessageName = messageName;
    try {
      populateAttributes(attributes);
    } catch (ParseException e) {
      throw new IllegalArgumentException(e);
    }
  }

  /** If message is text message according to the pattern ##teamcity[key...], return parsed message. Otherwise, return null
   *  Throws ParseException if message is a service message but its arguments cannot be parsed.
   * @param text text to parse
   * @return see above
   * @throws java.text.ParseException if parse failed
   **/
  @Nullable
  public static ServiceMessage parse(@NotNull final String text) throws ParseException {
    if (text.startsWith(SERVICE_MESSAGE_START) && text.endsWith(SERVICE_MESSAGE_END)) {
      return doParse(text.substring(SERVICE_MESSAGE_START.length(), text.length() - SERVICE_MESSAGE_END.length()).trim());
    }
    return null;
  }

  /**
   * Accepts mixed text (text that can contain service messages inside) and parser callback.
   * Notifies parser callback on any occurence of the service message or regular text.
   * @param mixedText text with service messages
   * @param parserCallback callback
   * @since 6.0
   */
  public static void parse(@NotNull String mixedText, @NotNull ServiceMessageParserCallback parserCallback) {
    for (Pair<String, Boolean> parsed: splitTextToServiceMessagesAndRegularText(mixedText)) {
      if (parsed.getSecond()) {
        try {
          parserCallback.serviceMessage(ServiceMessage.doParse(parsed.getFirst()));
        } catch (ParseException e) {
          parserCallback.parseException(e, appendPrefixAndSuffix(parsed.getFirst()));
        }
      } else {
        parserCallback.regularText(parsed.getFirst());
      }
    }
  }

  /**
   * Finds "##teamcity[...]" substrings in the specified text and splits the text to potential service
   * messages (doesn't parse them) and regular text between them
   * @param text specified text
   * @return splitted text
   *
   * @deprecated will be removed in further versions
   */
  @NotNull
  public static List<String> splitTextToPotentialMessages(@NotNull final String text) {
    final List<Pair<String, Boolean>> pairs = splitTextToServiceMessagesAndRegularText(text);
    final List<String> result = new ArrayList<String>();
    for (final Pair<String, Boolean> pair : pairs) {
      final String body = pair.getFirst();
      result.add(pair.getSecond() ? appendPrefixAndSuffix(body) : body);
    }
    return result;
  }

  private static boolean isReservedName(@NotNull final String name) {
    return name.startsWith(RESERVED_ATTRIBUTE_PREFIX) && MapSerializerUtil.isValidJavaIdentifier(
      name.substring(RESERVED_ATTRIBUTE_PREFIX.length()));
  }

  /** @return name part of the service message */
  @NotNull
  public String getMessageName() {
    return myMessageName;
  }

  /** @return argument text for message ##teamcity[&lt;message name> '&lt; argument>'] or null
   **/
  @Nullable
  public String getArgument() {
    return myArgument;
  }

  /**
   * Returns service message creation timestamp in case it was specified with attribute "timestamp".
   * If it was not returns null. The format of timestamp has to be "yyyy-MM-dd'T'HH:mm:ss.SSSZ" or "yyyy-MM-dd'T'HH:mm:ss.SSS".
   * Returns null if attribute value does not match the format.
   * @return service message creation timestamp in case it was correctly specified with attribute "timestamp", null otherwise.
   */
  @Nullable
  public Timestamp getCreationTimestamp() {
    return myCreationTimestamp;
  }

  /**
   * Returns service message flow id in case it was specified and null otherwise
   * @return service message flow id in case it was specified and null otherwise
   */
  @Nullable
  public String getFlowId() {
    return myFlowId;
  }

  @NotNull
  public Collection<String> getTags() {
    return myTags;
  }

  /**
   * Returns map of parameters of the message in format ##teamcity[&lt;message name> &lt;param name>='&lt;param value>' &lt;param name>='&lt;param value>'...]
   * if parameters were specified, and empty map otherwise
   * @return see above
   **/
  @NotNull
  public Map<String, String> getAttributes() {
    return Collections.unmodifiableMap(myAttributes);
  }

  /**
   * Depending on this service message type calls corresponding method in the supplied visitor.
   * @param visitor visitor
   */
  public void visit(@NotNull final ServiceMessageVisitor visitor) {
    visitor.visitServiceMessage(this);
  }

  public void setTimestamp(@NotNull Date timestamp) {
    myAttributes.put("timestamp", new SimpleDateFormat(FORMAT_WITH_TZ).format(timestamp));
    myCreationTimestamp = new Timestamp(timestamp, true);
  }

  public void setFlowId(@NotNull String flowId) {
    myAttributes.put("flowId", flowId);
    myFlowId = flowId;
  }

  public static class Timestamp {
    private final Date myTimestamp;
    private final boolean myTimeZoneWasSpecified;

    public Date getTimestamp() {
      return myTimestamp;
    }
    public boolean isTimeZoneSpecified() {
      return myTimeZoneWasSpecified;
    }

    public Timestamp(final Date timestamp, final boolean timeZoneWasSpecified) {
      myTimestamp = timestamp;
      myTimeZoneWasSpecified = timeZoneWasSpecified;
    }
  }

  @Nullable
  protected String getAttributeValue(@NotNull final String attrName) {
    return getAttributes().get(attrName);
  }

  /**
   * Finds "##teamcity[...]" substrings in the specified text and splits the text to service
   * messages (doesn't parse them) and regular text between them
   * @param text specified text
   * @return splitted text as pairs: first element is a piece of the text and the second is true if
   * the first element is a service message (and false if regular text)
   *
   * @since 5.1.x
   */
  @NotNull
  private static List<Pair<String, Boolean>> splitTextToServiceMessagesAndRegularText(@NotNull final String text) {
    final List<Pair<String, Boolean>> pairs = new ArrayList<Pair<String, Boolean>>();

    int currentIndex = 0;
    while (currentIndex < text.length()) {
      int messageStartPos = text.indexOf(SERVICE_MESSAGE_START, currentIndex);
      if (messageStartPos == -1) {
        addMessageToListIfNotEmpty(pairs, text.substring(currentIndex), false);
        return pairs;
      }
      int messageEndPos = findMessageEnd(text, messageStartPos);
      if (messageEndPos == -1) {
        addMessageToListIfNotEmpty(pairs, text.substring(currentIndex), false);
        return pairs;
      }
      addMessageToListIfNotEmpty(pairs, text.substring(currentIndex, messageStartPos), false);
      addMessageToListIfNotEmpty(pairs, text.substring(messageStartPos + SERVICE_MESSAGE_START.length(), messageEndPos - SERVICE_MESSAGE_END.length()).trim(), true);
      currentIndex = messageEndPos;
    }

    return pairs;
  }

  private void parseArgument(@Nullable final String argumentsStr) throws ParseException {
    myArgument = argumentsStr == null ? null : stringToText(argumentsStr);
  }

  private void parseAttributes(@Nullable final String argumentsStr) throws ParseException {
    reset();

    if (argumentsStr != null) {
      final Map<String, String> parsedAttributes = MapSerializerUtil.stringToProperties(argumentsStr, MapSerializerUtil.STD_ESCAPER, false);
      for (String attrName : parsedAttributes.keySet()) {
        if(!isReservedName(attrName) && !MapSerializerUtil.isValidJavaIdentifier(attrName))
          throw new ParseException("Invalid attribute name: " + attrName, 0);
      }
      populateAttributes(parsedAttributes);
    }
  }

  private void populateAttributes(final Map<String, String> attributes) throws ParseException {
    myAttributes.putAll(attributes);

    if(myAttributes.containsKey(ARG_ATTRIBUTE)) {
      myArgument = myAttributes.get(ARG_ATTRIBUTE);
      myAttributes.remove(ARG_ATTRIBUTE);
    }

    if(myAttributes.containsKey(TAGS_ATRRIBUTE)) {
      final String tagsStr = myAttributes.get(TAGS_ATRRIBUTE);
      myTags = splitTags(tagsStr);
      myAttributes.remove(TAGS_ATRRIBUTE);
    }

    parseCreationTimestamp();
    parseFlowId();
  }

  private void reset() {
    myAttributes.clear();
    myCreationTimestamp = null;
    myFlowId = null;
    myTags = Collections.emptyList();
    myArgument = null;
  }

  private static List<String> splitTags(final String tagsStr) {
    StringTokenizer tok = new StringTokenizer(tagsStr, TAGS_SEPARATOR);
    ArrayList<String> result = new ArrayList<String>(tok.countTokens());
    while(tok.hasMoreTokens())
      result.add(tok.nextToken().trim());

    return Collections.unmodifiableList(result);
  }

  private void parseFlowId() {
    myFlowId = getAttributeValue("flowId");
  }

  private void parseCreationTimestamp() throws ParseException {
    String creationTimestampStr = getAttributeValue("timestamp");
    if (creationTimestampStr == null) {
      myCreationTimestamp = null;
      return;
    }

    creationTimestampStr = creationTimestampStr.replace("'T'", "T");

    synchronized (this) {
      if (creationTimestampStr.length() == FORMAT_WITHOUT_TZ_LEN) {
        myCreationTimestamp = new Timestamp(new SimpleDateFormat(FORMAT_WITHOUT_TZ).parse(creationTimestampStr), false);
      } else {
        myCreationTimestamp = new Timestamp(new SimpleDateFormat(FORMAT_WITH_TZ).parse(creationTimestampStr), true);
      }
    }
  }

  @NotNull
  private static ServiceMessage doParse(@NotNull final String text) throws ParseException {
    final int sepIndex = text.indexOf(" ");
    final String key = sepIndex == -1 ? text : text.substring(0, sepIndex);
    final String argumentsStr = sepIndex == -1 ? null : text.substring(sepIndex).trim();

    Class<? extends ServiceMessage> clazz = SERVICE_MESSAGE_CLASSES.get(key);
    if (clazz == null) clazz = ServiceMessage.class;

    try {
      final ServiceMessage msg = clazz.newInstance();
      msg.init(key, argumentsStr);
      return msg;
    } catch (ParseException e) {
      throw e;
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  private static String appendPrefixAndSuffix(final String text) {
    return SERVICE_MESSAGE_START + text + SERVICE_MESSAGE_END;
  }

  private static void addMessageToListIfNotEmpty(@NotNull final List<Pair<String, Boolean>> pairs,
                                                 @NotNull final String message,
                                                 final boolean isServiceMessage) {
    if (message.length() != 0) {
      pairs.add(Pair.create(message, isServiceMessage));
    }
  }

  private static int findMessageEnd(@NotNull final String text, final int messageStartPos) {
    int pos = messageStartPos;

    do {
      pos = text.indexOf(SERVICE_MESSAGE_END, pos + 1);
    } while (pos != -1 && text.charAt(pos - 1) == MapSerializerUtil.STD_ESCAPER.escapeCharacter());

    return pos == -1 ? -1 : pos + 1;
  }

  @NotNull
  private static String stringToText(@NotNull String message) throws ParseException {
    message = message.trim();

    if (message.startsWith("'") && message.endsWith("'")) {
      return MapSerializerUtil.unescapeStr(message.substring(1, message.length() - 1), MapSerializerUtil.STD_ESCAPER);
    }
    else {
      throw new ParseException("Cannot extract text message [" + message + "]", 0);
    }
  }

  private void init(@NotNull final String key, @Nullable final String argumentsStr) throws ParseException {
    myMessageName = key;
    if (argumentsStr != null && argumentsStr.trim().startsWith("'")) {
      parseArgument(argumentsStr);
    } else {
      parseAttributes(argumentsStr);
    }
  }

  /**
   * Converts this service message to service message string
   * @return serialized service message object with ##teamcity[ prefix
   * @since 6.0
   */
  @NotNull
  public String asString() {
    if (myArgument != null && myTags.isEmpty())
      //noinspection ConstantConditions
      return asString(myMessageName, myArgument);

    final Map<String, String> fullAttrMap = new LinkedHashMap<String, String>();
    if(myArgument != null)
      fullAttrMap.put(ARG_ATTRIBUTE, myArgument);

    if(!myTags.isEmpty()) {
      StringBuilder tagsStr = new StringBuilder();
      for (String tag : myTags) {
        if(tagsStr.length() > 0)
          tagsStr.append(TAGS_SEPARATOR);
        tagsStr.append(tag);
      }

      fullAttrMap.put(TAGS_ATRRIBUTE, tagsStr.toString());
    }

    fullAttrMap.putAll(myAttributes);

    return asString(myMessageName, fullAttrMap);
  }

  /**
   * Returns a string representation of a service message with attribute map.
   * This method is useful when there is a need of outputting service text without creating a ServiceMessage instance.
   * @param messageName name of the message
   * @param attributes attribute map
   * @return service message string
   * @since 6.0
   */
  @NotNull
  public static String asString(@NotNull final String messageName, @NotNull final Map<String, String> attributes) {
    StringBuilder text = new StringBuilder();
    appendMessageName(text, messageName);
    if (!attributes.isEmpty()) {
      text.append(' ');
      text.append(MapSerializerUtil.propertiesToString(attributes, MapSerializerUtil.STD_ESCAPER));
    }
    text.append(SERVICE_MESSAGE_END);
    return text.toString();
  }

  /**
   * Returns a string representation of a service message with a single argument.
   * This method is useful when there is a need of outputting service text without creating a ServiceMessage instance.
   * @param messageName name of the message
   * @param argument the argument
   * @return service message string
   * @since 6.0
   */
  @NotNull
  public static String asString(@NotNull final String messageName, @NotNull final String argument) {
    StringBuilder text = new StringBuilder();
    appendMessageName(text, messageName);

    text.append(" '");
    text.append(MapSerializerUtil.escapeStr(argument, MapSerializerUtil.STD_ESCAPER));
    text.append("'");

    text.append(SERVICE_MESSAGE_END);
    return text.toString();
  }

  private static void appendMessageName(final StringBuilder target, final String messageName) {
    target.append(SERVICE_MESSAGE_START);
    target.append(messageName);
  }

  @Override
  public String toString() {
    return asString();
  }

  private static class Pair<T1, T2> {
    private final T1 myFirst;
    private final T2 mySecond;

    private Pair(final T1 first, final T2 second) {
      myFirst = first;
      mySecond = second;
    }

    public T1 getFirst() {
      return myFirst;
    }

    public T2 getSecond() {
      return mySecond;
    }

    public static <T1, T2> Pair<T1, T2> create(T1 first, T2 second) {
      return new Pair<T1, T2>(first, second);
    }
  }

}
