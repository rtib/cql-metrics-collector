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
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class LabelTest {
    
    public LabelTest() {
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
     * Test of toString method, of class Label.
     */
    @Test
    public void testValidToString() throws MetricException {
        System.out.println("Testing toString with valid values");
        Label instance = new Label("cluster", "Test Cluster");
        String expResult = "cluster=\"Test Cluster\"";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
    
    /**
     * Test instantiating with invalid name.
     */
    @Test(expected = MetricException.class)
    public void testInvalidName() throws MetricException {
        System.out.println("Testing with invalid name");
        new Label("clus.ter", "Test Cluster");
    }
    
    /**
     * Test instantiating with reserved name.
     */
    @Test(expected = MetricException.class)
    public void testReservedName() throws MetricException {
        System.out.println("Testing with reserved name");
        Label instance = new Label("__cluster", "Test Cluster");
        System.out.println(instance);
    }
    
    @Test
    public void testEquals() throws MetricException {
        Label label1 = new Label("testLabel", "test1");
        Label label2 = new Label("testLabel", "test1");
        assertTrue("Labels are equal.", label1.equals(label2));
    }
    
    @Test
    public void testHashCode() throws MetricException {
        System.out.println("HashCodes are equal.");
        Label label1 = new Label("testLabel", "test1");
        Label label2 = new Label("testLabel", "test1");
        assertEquals(label1.hashCode(), label2.hashCode());
    }
}
