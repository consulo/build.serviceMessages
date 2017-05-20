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

public class BuildStatisticValue extends MessageWithAttributes {
  // used by reflection
  BuildStatisticValue() {
  }

  public BuildStatisticValue(@NotNull String key, int value) {
    super(ServiceMessageTypes.BUILD_STATISTIC_VALUE, createMap(key, String.valueOf(value)));
  }

  public BuildStatisticValue(@NotNull String key, float value) {
    super(ServiceMessageTypes.BUILD_STATISTIC_VALUE, createMap(key, String.valueOf(value)));
  }

  private static Map<String, String> createMap(@NotNull String key, @NotNull String value) {
    Map<String, String> res = new LinkedHashMap<String, String>();
    res.put("key", key);
    res.put("value", value);
    return res;
  }

  public String getKey() {
    return getAttributeValue("key");
  }

  public String getValue() {
    return getAttributeValue("value");
  }

  @Override
  public void visit(@NotNull ServiceMessageVisitor visitor) {
    visitor.visitBuildStatisticValue(this);
  }
}
