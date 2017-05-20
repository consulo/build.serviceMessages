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

/**
 * Abstract register for service message handlers
 * Typical usage: register your handler from plugin initialization code
 * @since 4.0
 */
public interface ServiceMessagesRegister {
  /**
   * Register service message handler
   * @param messageName service message name
   * @param handler service message handler to be registered
   * @since 4.0
   */
  void registerHandler(@NotNull String messageName, @NotNull ServiceMessageHandler handler);

  /**
   * Remove service message handler
   * Does nothing if handler for messageType wasn't registered
   * @param messageName service message name
   * @since 4.0
   */
  void removeHandler(@NotNull String messageName);
}
