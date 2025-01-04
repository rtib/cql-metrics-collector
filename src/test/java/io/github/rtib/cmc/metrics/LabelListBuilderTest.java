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
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Tibor Répási <rtib@users.noreply.github.com>
 */
public class LabelListBuilderTest {
    
    public LabelListBuilderTest() {
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
     * Test of addLabel method, of class LabelListBuilder.
     */
    @Test
    public void testBuildLabelList() throws Exception {
        System.out.println("Build List<Label>");
        List<Label> list = new LabelListBuilder()
                .addLabel("purpose", "test")
                .addLabel("number", "2")
                .build();
        String expResult = "[purpose=\"test\", number=\"2\"]";
        assertEquals(expResult, list.toString());
    }

    @Test
    public void testListEquality() throws Exception {
        System.out.println("Equality of List<Label>");
        List<Label> list1 = new LabelListBuilder()
                .addLabel("purpose", "test")
                .addLabel("number", "2")
                .build();
        List<Label> list2 = new LabelListBuilder()
                .addLabel("purpose", "test")
                .addLabel("number", "2")
                .build();
        assertTrue("Lists are equal", list1.equals(list2));
    }

    @Test
    public void testListHashCode() throws Exception {
        System.out.println("Equality of List<Label> hashCodes");
        List<Label> list1 = new LabelListBuilder()
                .addLabel("purpose", "test")
                .addLabel("number", "2")
                .build();
        List<Label> list2 = new LabelListBuilder()
                .addLabel("purpose", "test")
                .addLabel("number", "2")
                .build();
        assertEquals(list1.hashCode(), list2.hashCode());
    }
}
