/*
 * Copyright 2024 Tibor Répási <rtib@users.noreply.github.com>.
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
package io.github.rtib.cmc.queryTasks;

/**
 * Exception thrown by collector tasks.
 * 
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class CollectorException extends Exception {

    public CollectorException() {
    }

    public CollectorException(String message) {
        super(message);
    }

    public CollectorException(String message, Throwable cause) {
        super(message, cause);
    }

    public CollectorException(Throwable cause) {
        super(cause);
    }
    
}
