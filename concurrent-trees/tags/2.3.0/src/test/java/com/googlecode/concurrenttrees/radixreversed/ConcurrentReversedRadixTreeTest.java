/**
 * Copyright 2012-2013 Niall Gallagher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.googlecode.concurrenttrees.radixreversed;

import com.googlecode.concurrenttrees.common.Iterables;
import com.googlecode.concurrenttrees.common.PrettyPrinter;
import com.googlecode.concurrenttrees.radix.node.NodeFactory;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Niall Gallagher
 */
public class ConcurrentReversedRadixTreeTest {

    private final NodeFactory nodeFactory = new DefaultCharArrayNodeFactory();

    protected NodeFactory getNodeFactory() {
        return nodeFactory;
    }

    @Test
    public void testGet() throws Exception {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals(Integer.valueOf(2), tree.getValueForExactKey("TEAM"));
    }

    @Test
    public void testPut() throws Exception {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        String expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TS\n" +
                "    ├── ○ AOT (3)\n" +
                "    └── ○ ET (1)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testPutIfAbsent() throws Exception {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory());
        tree.putIfAbsent("TEST", 1);
        tree.putIfAbsent("TEAM", 2);
        tree.putIfAbsent("TOAST", 3);
        tree.putIfAbsent("TEAM", 4); // should be ignored

        String expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TS\n" +
                "    ├── ○ AOT (3)\n" +
                "    └── ○ ET (1)\n";
        String actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testGetKeysEndingWith() throws Exception {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals("[TOAST, TEST]", Iterables.toString(tree.getKeysEndingWith("ST")));
        assertEquals("[TEAM]", Iterables.toString(tree.getKeysEndingWith("M")));
        assertEquals("[TEAM, TOAST, TEST]", Iterables.toString(tree.getKeysEndingWith("")));
        assertEquals("[]", Iterables.toString(tree.getKeysEndingWith("Z")));
    }

    @Test
    public void testGetValuesForKeysEndingWith() throws Exception {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals("[3, 1]", Iterables.toString(tree.getValuesForKeysEndingWith("ST")));
        assertEquals("[2]", Iterables.toString(tree.getValuesForKeysEndingWith("M")));
        assertEquals("[2, 3, 1]", Iterables.toString(tree.getValuesForKeysEndingWith("")));
        assertEquals("[]", Iterables.toString(tree.getValuesForKeysEndingWith("Z")));
    }

    @Test
    public void testGetKeyValuePairsForKeysEndingWith() throws Exception {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        assertEquals("[(TOAST, 3), (TEST, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("ST")));
        assertEquals("[(TEAM, 2)]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("M")));
        assertEquals("[(TEAM, 2), (TOAST, 3), (TEST, 1)]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("")));
        assertEquals("[]", Iterables.toString(tree.getKeyValuePairsForKeysEndingWith("Z")));
    }

    @Test
    public void testRemove() throws Exception {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory());
        tree.put("TEST", 1);
        tree.put("TEAM", 2);
        tree.put("TOAST", 3);

        String expected, actual;
        expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TS\n" +
                "    ├── ○ AOT (3)\n" +
                "    └── ○ ET (1)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);

        tree.remove("TEST");

        expected =
                "○\n" +
                "├── ○ MAET (2)\n" +
                "└── ○ TSAOT (3)\n";
        actual = PrettyPrinter.prettyPrint(tree);
        assertEquals(expected, actual);
    }

    @Test
    public void testRestrictConcurrency() {
        ConcurrentReversedRadixTree<Integer> tree = new ConcurrentReversedRadixTree<Integer>(getNodeFactory(), true);
        assertNotNull(tree);
    }
}
