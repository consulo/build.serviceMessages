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

import java.util.Collections;
import org.jetbrains.annotations.NotNull;

public class TestFinished extends BaseTestMessage {
  // reflection
  TestFinished() {
  }

  public TestFinished(@NotNull final String name, int duration) {
    super(ServiceMessageTypes.TEST_FINISHED, name, Collections.singletonMap("duration", String.valueOf(duration)));
  }

  public Integer getTestDuration() {
    try {
      final String duration = getAttributeValue("duration");
      return duration == null ? null : Integer.parseInt(duration);
    } catch (NumberFormatException e) {
      return null;
    }
  }

  @Override
  public void visit(@NotNull ServiceMessageVisitor visitor) {
    visitor.visitTestFinished(this);
  }
}
