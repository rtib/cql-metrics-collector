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

import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tibor Répási {@literal <rtib@users.noreply.github.com>}
 */
public class MetricTest {

    private Metric testMetric1;
    private List<Label> testLabels2;
    private Metric testMetric2;
    private List<Label> testLabels1;
    
    public MetricTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws MetricException {
        this.testMetric1 = new Metric.Builder()
                .withName("test_metric_1")
                .withHelp("Test metric #1")
                .withType(MetricType.GAUGE)
                .withCommonLabel("purpose", "test")
                .withCommonLabel("number", "1")
                .build();
        this.testMetric2 = new Metric.Builder()
                .withName("test_metric_2")
                .withHelp("Test metric #2")
                .withType(MetricType.GAUGE)
                .withCommonLabel("purpose", "test")
                .withCommonLabel("number", "1")
                .build();
        this.testLabels1 = new LabelListBuilder()
                .addLabel("keyspace", "test")
                .addLabel("table", "tab1")
                .build();
        this.testLabels2 = new LabelListBuilder()
                .addLabel("keyspace", "test")
                .addLabel("table", "tab2")
                .build();
        this.testMetric2.addInstance(this.testLabels1);
        this.testMetric2.addInstance(this.testLabels2);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addInstance method, of class Metric.
     */
    @Test
    public void testAddInstance() throws MetricException {
        System.out.println("addInstance");
        List<Label> labels = this.testLabels2;
        Metric instance = this.testMetric1;
        instance.addInstance(labels);
    }

    /**
     * Test of removeInstance method, of class Metric.
     */
    @Test
    public void testRemoveInstance() {
        System.out.println("removeInstance");
        Metric instance = this.testMetric2;
        instance.setValue(this.testLabels1, 123);
        instance.setValue(this.testLabels2, 345);
        instance.removeInstance(this.testLabels1);
        String expResult = 
                "# HELP test_metric_2 Test metric #2\n" +
                "# TYPE test_metric_2 gauge\n" +
                "test_metric_2{purpose=\"test\",number=\"1\",keyspace=\"test\",table=\"tab2\"} 345.0 " +
                System.currentTimeMillis() +
                "\n";
        String result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getHelp method, of class Metric.
     */
    @Test
    public void testGetHelp() {
        System.out.println("getHelp");
        Metric instance = this.testMetric1;
        String expResult = "# HELP test_metric_1 Test metric #1\n";
        String result = instance.getHelp();
        assertEquals(expResult, result);
    }

    /**
     * Test of getType method, of class Metric.
     */
    @Test
    public void testGetType() {
        System.out.println("getType");
        Metric instance = this.testMetric1;
        String expResult = "# TYPE test_metric_1 gauge\n";
        String result = instance.getType();
        assertEquals(expResult, result);
    }

    /**
     * Test of getInstance method, of class Metric.
     */
    @Test
    public void testGetInstances() {
        System.out.println("getInstance");
        Metric instance = this.testMetric2;
        instance.setValue(this.testLabels1, 123);
        instance.setValue(this.testLabels2, 345);
        instance.setValue(this.testLabels1, 234);
        String expResult = 
                "test_metric_2{purpose=\"test\",number=\"1\",keyspace=\"test\",table=\"tab1\"} 234.0 " +
                System.currentTimeMillis() +
                "\n" +
                "test_metric_2{purpose=\"test\",number=\"1\",keyspace=\"test\",table=\"tab2\"} 345.0 " +
                System.currentTimeMillis() +
                "\n";
        String result = instance.getInstances();
        assertEquals(expResult, result);
    }

    /**
     * Test of toString method, of class Metric.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Metric instance = this.testMetric2;
        instance.setValue(this.testLabels1, 123);
        instance.setValue(this.testLabels2, 345);
        instance.setValue(this.testLabels1, 234);
        String expResult = 
                "# HELP test_metric_2 Test metric #2\n" +
                "# TYPE test_metric_2 gauge\n" +
                "test_metric_2{purpose=\"test\",number=\"1\",keyspace=\"test\",table=\"tab1\"} 234.0 " +
                System.currentTimeMillis() +
                "\n" +
                "test_metric_2{purpose=\"test\",number=\"1\",keyspace=\"test\",table=\"tab2\"} 345.0 " +
                System.currentTimeMillis() +
                "\n";
        String result = instance.toString();
        assertEquals(expResult, result);
    }
}
