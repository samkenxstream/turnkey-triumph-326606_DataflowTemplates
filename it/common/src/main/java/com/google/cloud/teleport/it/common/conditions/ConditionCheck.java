/*
 * Copyright (C) 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.cloud.teleport.it.common.conditions;

import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link ConditionCheck} class provides a base interface for reusable/common conditions that
 * can be helpful during integration testing.
 */
public abstract class ConditionCheck implements Supplier<Boolean> {

  private static final Logger LOG = LoggerFactory.getLogger(ConditionCheck.class);

  private ConditionCheck next;

  protected abstract String getDescription();

  protected abstract CheckResult check();

  @Override
  public Boolean get() {
    LOG.info("[?] Checking for condition '{}'...", getDescription());

    CheckResult result = check();
    if (!result.success) {
      LOG.info("[✗] Condition '{}' failed! {}", getDescription(), result.message);
      return false;
    }

    LOG.info(
        "[✓] Condition '{}' succeeded! {}",
        getDescription(),
        result.message == null ? "" : result.message);

    if (next != null) {
      return result.success && next.get();
    }

    return true;
  }

  public ConditionCheck and(ConditionCheck next) {
    this.next = next;
    return next;
  }

  public static class CheckResult {
    private boolean success;
    private String message;

    public CheckResult(boolean success) {
      this.success = success;
    }

    public CheckResult(boolean success, String message) {
      this.success = success;
      this.message = message;
    }

    @Override
    public String toString() {
      return "CheckResult{" + "success=" + success + ", message='" + message + '\'' + '}';
    }
  }
}
