package jetbrains.buildServer.messages.serviceMessages;

import java.text.ParseException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

public class MapSerializerUtil {
  private static final String STD_EX_SUFFIX =
    "Valid property list format is (name( )*=( )*\'escaped_value\'( )*)* where escape simbol is \"|\"";

  /**
   * performs conversion of string to property map with string name checking.
   * @see #stringToProperties(String, jetbrains.buildServer.util.StringUtil.EscapeInfoProvider, boolean)
   * @param string source string
   * @param escaper escaping rule provider
   * @return the resulted property map
   * @throws java.text.ParseException if parsing of the property sting failed
   */
  @NotNull
  public static Map<String, String> stringToProperties(@NotNull final String string, @NotNull final EscapeInfoProvider escaper) throws ParseException {
    return stringToProperties(string, escaper, true);
  }

  public static String propertiesToString(final Map<String, String> props, final EscapeInfoProvider escaper) {
    StringBuilder builder = new StringBuilder();
    for (Map.Entry<String, String> entry: props.entrySet()) {
      if (builder.length() > 0) builder.append(' ');
      builder.append(escapeStr(entry.getKey(), escaper));
      builder.append('=');
      builder.append('\'').append(escapeStr(entry.getValue(), escaper)).append('\'');
    }

    return builder.toString();
  }

  /**
   * Converts string to property map
   * @param string in a form of "name1='value' name2='value' ..."
   * @param escaper escaping rule provider
   * @param strictNameCheck if true each name is checked to be a valid java identifier, otherwise it's only checked for absense of spaces
   * @return the resulted property map
   * @throws ParseException if parsing of the property sting failed
   */
  @NotNull
  public static Map<String, String> stringToProperties(@NotNull final String string,
                                                       @NotNull final EscapeInfoProvider escaper,
                                                       final boolean strictNameCheck) throws ParseException {
    String currentString = string;
    final HashMap<String, String> result = new LinkedHashMap<String, String>();
    while (currentString.length() > 0) {
      final int nameSep = currentString.indexOf("=");
      if (nameSep == -1) throw new ParseException("Property value not found" + "\n" + STD_EX_SUFFIX, 0);
      final String name= currentString.substring(0, nameSep).trim();
      checkPropName(name, strictNameCheck);
      currentString = currentString.substring(nameSep + 1).trim();

      if (currentString.startsWith("'")) {
        currentString = currentString.substring(1);
        int endOfValue = indexOf(currentString, '\'', escaper);
        if (endOfValue >= 0) {
          String escapedValue = currentString.substring(0, endOfValue);
          currentString = currentString.substring(endOfValue + 1).trim();

          result.put(name, unescapeStr(escapedValue, escaper));

        }
        else {
          throw new ParseException("Value should end with \"'\"" + "\n" + STD_EX_SUFFIX, 0);
        }
      }
      else {
        throw new ParseException("Value should start with \"'\"" + "\n" + STD_EX_SUFFIX, 0);
      }
    }

    return result;
  }

  private static void checkPropName(final String name, boolean strict) throws ParseException {
    final boolean isCorrect = strict ? isValidJavaIdentifier(name) : !hasSpaces(name);
    if (!isCorrect) {
      throw new ParseException("Incorrect property name." + "\n" + STD_EX_SUFFIX, 0);
    }
  }

  private static boolean hasSpaces(final String name) {
    return name.indexOf(' ') >= 0;
  }

  public static int indexOf(final String currentString, final char findWhat, final EscapeInfoProvider escaper) {
    for (int i = 0; i < currentString.length(); i++) {
      final char currentChar = currentString.charAt(i);
      if (escaper.escapeCharacter() == currentChar) {
        i++;
      }
      else if (currentChar == findWhat) {
        return i;
      }
    }

    return -1;

  }

  public static boolean isValidJavaIdentifier(@NotNull final String name) {
    if (name.length() == 0) return false;
    if (!Character.isJavaIdentifierStart(name.charAt(0))) return false;
    for (int i = 1; i < name.length(); i++) {
      if (!Character.isJavaIdentifierPart(name.charAt(i))) return false;
    }

    return true;
  }

  /**
   * String escaping info provider.
   */
  public interface EscapeInfoProvider {
    /**
     * Converts character to its representation in the final string
     * @param c character to convert
     * @return character representation or 0 if conversion is not applicable to that character
     */
    char escape(char c);

    /**
     * Converts character representation to original character
     * @param c character representation
     * @return see above
     */
    char unescape(char c);

    /**
     * Escape character to use before escaped characters (before character representations generated by {@link #escape(char)}  method)
     * @return see above
     */
    char escapeCharacter();
  }

  public static final EscapeInfoProvider STD_ESCAPER = new EscapeInfoProvider() {
    public char escape(final char c) {
      switch (c) {
        case '\n': return 'n';
        case '\r': return 'r';
        case '\u0085': return 'x'; // next-line character
        case '\u2028': return 'l'; // line-separator character
        case '\u2029': return 'p'; // paragraph-separator character
        case '|': return '|';
        case '\'': return '\'';
        case '[': return '[';
        case ']': return ']';
        default:return 0;
      }
    }

    public char unescape(final char c) {
      switch (c) {
        case 'n': return '\n';
        case 'r': return '\r';
        case 'x': return '\u0085'; // next-line character
        case 'l': return '\u2028'; // line-separator character
        case 'p': return '\u2029'; // paragraph-separator character
        case '\'': return '\'';
        case '|': return '|';
        case '[': return '[';
        case ']': return ']';
        default:return 0;
      }
    }

    public char escapeCharacter() {
      return '|';
    }
  };

  /**
   * Escapes characters specified by provider with '\' and specified character.
   * @param str initial string
   * @param p escape info provider.
   * @return escaped string.
   */
  public static String escapeStr(final String str, EscapeInfoProvider p) {
    if (str == null) return null;
    int finalCount = calcFinalEscapedStringCount(str, p);

    if (str.length() == finalCount) return str;

    char[] resultChars = new char[finalCount];
    int resultPos = 0;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      final char escaped = p.escape(c);
      if (escaped != 0) {
        resultChars[resultPos++] = p.escapeCharacter();
        resultChars[resultPos++] = escaped;
      }
      else {
        resultChars[resultPos++] = c;
      }
    }

    if (resultPos != finalCount) {
      throw new RuntimeException("Incorrect escaping for '" + str + "'");
    }
    return new String(resultChars);
  }

  private static int calcFinalEscapedStringCount(final String name, final EscapeInfoProvider p) {
    int result = 0;
    for (int i = 0; i < name.length(); i++) {
      char c = name.charAt(i);
      if (p.escape(c) != 0) {
        result += 2;
      }
      else {
        result += 1;
      }
    }

    return result;
  }

  /**
   * Unescapes characters specified by provider with '\' and specified character.
   * @param str initial string
   * @param p escape info provider.
   * @return unescaped string.
   */

  public static String unescapeStr(final String str, EscapeInfoProvider p) {
    if (str == null) return null;
    int finalCount = calcFinalUnescapedStringCount(str, p);

    int len = str.length();
    if (len == finalCount) return str;

    char[] resultChars = new char[finalCount];
    int resultPos = 0;
    for (int i = 0; i < len; i++) {
      char c = str.charAt(i);
      if (c == p.escapeCharacter() && i < len - 1) {
        char nextChar = str.charAt(i + 1);
        final char unescaped = p.unescape(nextChar);
        if (unescaped != 0) {
          c = unescaped;
          //noinspection AssignmentToForLoopParameter
          i += 1;
        }
      }

      resultChars[resultPos++] = c;
    }

    if (resultPos != finalCount) {
      throw new RuntimeException("Incorrect unescaping for '" + str + "'");
    }

    return new String(resultChars);

  }

  private static int calcFinalUnescapedStringCount(final String name, final EscapeInfoProvider p) {
    int result = 0;
    int len = name.length();
    for (int i = 0; i < len; i++) {
      char c = name.charAt(i);
      if (c == p.escapeCharacter() && i < len - 1) {
        char nextChar = name.charAt(i + 1);
        if (p.unescape(nextChar) != 0) {
          //noinspection AssignmentToForLoopParameter
          i += 1;
        }
      }

      result += 1;
    }

    return result;
  }
}
