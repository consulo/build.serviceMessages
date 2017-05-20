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

public abstract class BaseTestMessage extends MessageWithAttributes {
  BaseTestMessage() {
  }

  BaseTestMessage(@NotNull final String messageName, @NotNull String name, @NotNull Map<String, String> attrs) {
    super(messageName, createMap(name, attrs));
  }

  private static Map<String, String> createMap(final String name, final Map<String, String> attrs) {
    Map<String, String> res = new LinkedHashMap<String, String>();
    res.put("name", name);
    res.putAll(attrs);
    return res;
  }

  public String getTestName() {
    return getAttributeValue("name");
  }
}
