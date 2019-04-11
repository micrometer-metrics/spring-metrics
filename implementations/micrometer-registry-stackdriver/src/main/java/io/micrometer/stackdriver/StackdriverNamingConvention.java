/**
 * Copyright 2018 Pivotal Software, Inc.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micrometer.stackdriver;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.NamingConvention;
import io.micrometer.core.instrument.util.StringUtils;
import io.micrometer.core.lang.Nullable;

import java.util.regex.Pattern;

/**
 * {@link NamingConvention} for Stackdriver.
 *
 * Names are mapped to Stackdriver's metric type names and tag keys are mapped to its metric label names.
 *
 * @see <a href="https://cloud.google.com/monitoring/api/v3/metrics-details">"Naming rules" section on Stackdriver's reference documentation</a>
 *
 * @author Jon Schneider
 * @since 1.1.0
 */
public class StackdriverNamingConvention implements NamingConvention {
    private static final int MAX_NAME_LENGTH = 200;
    private static final int MAX_TAG_KEY_LENGTH = 100;
    private static final Pattern NAME_BLACKLIST = Pattern.compile("[^\\w./_]");
    private static final Pattern TAG_KEY_BLACKLIST = Pattern.compile("[^\\w_]");
    private final NamingConvention nameDelegate;
    private final NamingConvention tagKeyDelegate;

    public StackdriverNamingConvention() {
        this(NamingConvention.slashes, NamingConvention.snakeCase);
    }

    public StackdriverNamingConvention(NamingConvention nameDelegate, NamingConvention tagKeyDelegate) {
        this.nameDelegate = nameDelegate;
        this.tagKeyDelegate = tagKeyDelegate;
    }

    @Override
    public String name(String name, Meter.Type type, @Nullable String baseUnit) {
        return sanitize(nameDelegate.name(name, type, baseUnit), NAME_BLACKLIST, MAX_NAME_LENGTH);
    }

    private String sanitize(String value, Pattern blacklist, int maxLength) {
        return StringUtils.truncate(blacklist.matcher(value).replaceAll("_"), maxLength);
    }

    @Override
    public String tagKey(String key) {
        return sanitize(tagKeyDelegate.tagKey(key), TAG_KEY_BLACKLIST, MAX_TAG_KEY_LENGTH);
    }
}