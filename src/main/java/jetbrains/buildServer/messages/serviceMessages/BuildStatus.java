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

public class BuildStatus extends MessageWithAttributes {
  // used by reflection
  BuildStatus() {
  }

  public BuildStatus(@NotNull String text, @NotNull String status) {
    super(ServiceMessageTypes.BUILD_STATUS, createMap(text, status));
  }

  private static Map<String, String> createMap(@NotNull String text, @NotNull String status) {
    Map<String, String> res = new LinkedHashMap<String, String>();
    res.put("status", status);
    res.put("text", text);
    return res;
  }

  public String getStatus() {
    return getAttributeValue("status");
  }

  public String getText() {
    return getAttributeValue("text");
  }

  @Override
  public void visit(@NotNull ServiceMessageVisitor visitor) {
    visitor.visitBuildStatus(this);
  }
}
