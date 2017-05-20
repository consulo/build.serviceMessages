package jetbrains.buildServer.messages.serviceMessages;

import java.text.ParseException;
import org.jetbrains.annotations.NotNull;

/**
 * Service message parser callback.
 * @since 6.0
 */
public interface ServiceMessageParserCallback {
  /**
   * Called for the regular text, i.e. for the text that does not contain service messages in it.
   * @param text
   */
  void regularText(@NotNull String text);

  /**
   * Called for each parsed service message
   * @param message parsed service message
   */
  void serviceMessage(@NotNull ServiceMessage message);

  /**
   * Called when text looked like service message but parser failed to create service message object from it for some reason.
   * @param parseException exception
   * @param text text that parser failed to parse
   */
  void parseException(@NotNull ParseException parseException, @NotNull String text);
}
