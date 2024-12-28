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
package io.github.rtib.cmc.metrics;

import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class MetricValueTest {
    
    public MetricValueTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toString method, of class MetricValue.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        MetricValue instance = new MetricValue(100);
        String expResult = "100.0 " + System.currentTimeMillis();
        String result = instance.toString();
        System.out.println(instance);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of toString method, of class MetricValue.
     */
    @Test
    public void testTimestampToString() {
        System.out.println("toString with Timestamp");
        MetricValue instance = new MetricValue(101, 1734098936777L);
        String expResult = "101.0 1734098936777";
        String result = instance.toString();
        System.out.println(instance);
        assertEquals(expResult, result);
    }
}
