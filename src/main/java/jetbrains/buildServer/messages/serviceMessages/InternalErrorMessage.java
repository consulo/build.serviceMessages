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


import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;


/**
 * Internal error is a case when build has failed to start because of
 * problems with build environment.
 * This system message will cause TeamCity to treat this build as not started properly.
 */
public class InternalErrorMessage extends MessageWithAttributes {

  // used by reflection
  InternalErrorMessage() {
  }

  public InternalErrorMessage(@NotNull String cause) {
    super(ServiceMessageTypes.INTERNAL_ERROR, createMap(cause));
  }

  private static Map<String, String> createMap(@NotNull String cause) {
    return Collections.singletonMap("cause", cause);
  }

  public String getCause() {
    return getAttributeValue("cause");
  }

  @Override
  public void visit(@NotNull ServiceMessageVisitor visitor) {
    visitor.visitServiceMessage(this);
  }
  
}