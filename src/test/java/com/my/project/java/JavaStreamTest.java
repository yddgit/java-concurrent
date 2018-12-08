package com.my.project.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

/**
 * Java Stream API Sample
 */
public class JavaStreamTest {

    /**
     * 并行操作时Stream不能保证元素的次序
     */
    @Test
    public void parallel() {
        StringBuilder buffer1 = new StringBuilder();
        IntStream.range(1, 10).parallel().map(i -> i*i).forEach(buffer1::append);
        StringBuilder buffer2 = new StringBuilder();
        IntStream.range(1, 10).forEach(buffer2::append);
        assertNotEquals(buffer2.toString(), buffer1.toString());
    }

    /**
     * 构造流的几种方式
     */
    @Test
    public void create1() {
        // Individuals values
        Stream stream = Stream.of("a", "b", "c");
        assertEquals(3, stream.count());
        // Arrays
        String[] strArrays = new String[]{"a", "b", "c"};
        stream = Stream.of(strArrays);
        assertEquals(3, stream.count());
        stream = Arrays.stream(strArrays);
        assertEquals(3, stream.count());
        // Collections
        List<String> list = Arrays.asList(strArrays);
        stream = list.stream();
        assertEquals(3, stream.count());
    }

    /**
     * 数值流：IntStream, LongStream, DoubleStream
     * 也可以用Stream<Integer>, Stream<Long>, Stream<Double>
     */
    @Test
    public void create2() {
        assertEquals(6, IntStream.of(new int[]{1, 2, 3}).sum());
        assertEquals(3, IntStream.range(1, 3).sum());
        assertEquals(6, IntStream.rangeClosed(1, 3).sum());
    }

    /**
     * 流转换为其他数据结构
     */
    @Test
    public void create3() {
        // Array
        String[] strArray = Stream.of("a", "b", "c").toArray(String[]::new);
        assertEquals(3, strArray.length);
        // Collection
        List<String> list1 = Stream.of("a", "b", "c").collect(Collectors.toList());
        List<String> list2 = Stream.of("a", "b", "c").collect(Collectors.toCollection(ArrayList::new));
        assertEquals(list1.size(), list2.size());
        assertEquals(list1.get(0), list2.get(0));
        Set<String> set = Stream.of("a", "b", "c").collect(Collectors.toSet());
        assertTrue(set.contains("a") && set.contains("b") && set.contains("c"));
        Stack<String> stack = Stream.of("a", "b", "c").collect(Collectors.toCollection(Stack::new));
        assertEquals(3, stack.size());
        // String
        String str = Stream.of("a", "b", "c").collect(Collectors.joining());
        assertEquals("abc", str);
    }
}
