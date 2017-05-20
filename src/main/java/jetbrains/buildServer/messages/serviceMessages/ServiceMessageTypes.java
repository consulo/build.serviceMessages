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

import org.jetbrains.annotations.NonNls;

/**
 * Contains known service message types
 */
public interface ServiceMessageTypes {
  @NonNls String PUBLISH_ARTIFACTS = "publishArtifacts";
  @NonNls String TEST_SUITE_STARTED = "testSuiteStarted";
  @NonNls String TEST_SUITE_FINISHED = "testSuiteFinished";
  @NonNls String TEST_STARTED = "testStarted";
  @NonNls String TEST_FINISHED = "testFinished";
  @NonNls String TEST_IGNORED = "testIgnored";
  @NonNls String TEST_STD_OUT = "testStdOut";
  @NonNls String TEST_STD_ERR = "testStdErr";
  @NonNls String TEST_FAILED = "testFailed";
  @NonNls String PROGRESS_MESSAGE = "progressMessage";
  @NonNls String PROGRESS_START = "progressStart";
  @NonNls String PROGRESS_FINISH = "progressFinish";
  @NonNls String BUILD_STATUS = "buildStatus";
  @NonNls String BUILD_NUMBER = "buildNumber";

  @NonNls String BUILD_PARAMETER = "buildParameter";
  @NonNls String BUILD_ENVIRONMENT = "buildEnvironment";

  @NonNls String BUILD_SET_PARAMETER = "setParameter";

  @NonNls String BUILD_STATISTIC_VALUE = "buildStatisticValue";
  @NonNls String TEST_NAVIGATION_INFO = "testNavigationInfo";
  @NonNls String BLOCK_OPENED = "blockOpened";
  @NonNls String BLOCK_CLOSED = "blockClosed";
  @NonNls String COMPILATION_STARTED = "compilationStarted";
  @NonNls String COMPILATION_FINISHED = "compilationFinished";
  @NonNls String MESSAGE = "message";
  @NonNls String INTERNAL_ERROR = "internalError";
}
