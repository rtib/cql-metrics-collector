/*
 * Copyright 2024-2025 Tibor Répási <rtib@users.noreply.github.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.rtib.cmc.metrics;

/**
 * Exception to be thrown from metrics package.
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class MetricException extends Exception {

    /**
     * Create the instance.
     */
    public MetricException() {
    }

    /**
     * Create the instance.
     * @param message exception message
     */
    public MetricException(String message) {
        super(message);
    }

    /**
     * Create the instance.
     * @param message exception message
     * @param cause wrapped exception
     */
    public MetricException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Create the instance.
     * @param cause wrapped exception
     */
    public MetricException(Throwable cause) {
        super(cause);
    }
    
}
