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

import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Message extends MessageWithAttributes {
  // reflection
  Message() {
  }

  public Message(@NotNull String text, @NotNull String status, @Nullable String errDetails) {
    super(ServiceMessageTypes.MESSAGE, createMap(text, status, errDetails));
  }

  private static Map<String, String> createMap(@NotNull String text, @NotNull String status, @Nullable String errDetails) {
    Map<String, String> res = new LinkedHashMap<String, String>();
    res.put("text", text);
    res.put("status", status);
    if (errDetails != null) {
      res.put("errorDetails", errDetails);
    }
    return res;
  }

  public String getStatus() {
    String status = getAttributeValue("status");
    if (status != null) {
      return status;
    }
    return "NORMAL";
  }

  public String getText() {
    return getAttributeValue("text");
  }

  public String getErrorDetails() {
    return getAttributeValue("errorDetails");
  }

  @Override
  public void visit(@NotNull final ServiceMessageVisitor visitor) {
    visitor.visitMessageWithStatus(this);
  }
}