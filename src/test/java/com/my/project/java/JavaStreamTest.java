package com.my.project.java;

import static org.junit.Assert.*;

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
     * 使用Stream API计算一组数字的质因数
     */
    @Test
    public void sample1() {
        int[] result = Arrays.stream(new int[] {10, 87, 97, 43, 121, 20})
                .flatMap(JavaStream::factorize)
                .distinct()
                .sorted()
                .toArray();
        assertArrayEquals(new int[]{2, 3, 5, 11, 29, 43, 97}, result);
    }

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
        String[] expected = new String[]{"a", "b", "c"};
        // Individuals values
        Stream stream = Stream.of("a", "b", "c");
        assertArrayEquals(expected, stream.toArray());
        // Arrays
        String[] strArrays = new String[]{"a", "b", "c"};
        stream = Stream.of(strArrays);
        assertArrayEquals(expected, stream.toArray());
        stream = Arrays.stream(strArrays);
        assertArrayEquals(expected, stream.toArray());
        // Collections
        List<String> list = Arrays.asList(strArrays);
        stream = list.stream();
        assertArrayEquals(expected, stream.toArray());
    }

    /**
     * 数值流：IntStream, LongStream, DoubleStream
     * 也可以用Stream<Integer>, Stream<Long>, Stream<Double>
     */
    @Test
    public void create2() {
        assertArrayEquals(new int[]{1, 2, 3}, IntStream.of(new int[]{1, 2, 3}).toArray());
        assertArrayEquals(new int[]{1, 2}, IntStream.range(1, 3).toArray());
        assertArrayEquals(new int[]{1, 2, 3}, IntStream.rangeClosed(1, 3).toArray());
    }

    /**
     * 流转换为其他数据结构
     */
    @Test
    public void create3() {
        String[] expected = new String[]{"a", "b", "c"};
        // Array
        String[] strArray = Stream.of("a", "b", "c").toArray(String[]::new);
        assertArrayEquals(expected, strArray);
        // Collection
        List<String> list1 = Stream.of("a", "b", "c").collect(Collectors.toList());
        List<String> list2 = Stream.of("a", "b", "c").collect(Collectors.toCollection(ArrayList::new));
        assertArrayEquals(expected, list2.toArray(new String[list1.size()]));
        assertArrayEquals(expected, list2.toArray(new String[list2.size()]));
        Set<String> set = Stream.of("a", "b", "c").collect(Collectors.toSet());
        assertTrue(set.contains("a") && set.contains("b") && set.contains("c"));
        Stack<String> stack = Stream.of("a", "b", "c").collect(Collectors.toCollection(Stack::new));
        assertArrayEquals(expected, stack.toArray(new String[stack.size()]));
        // String
        String str = Stream.of("a", "b", "c").collect(Collectors.joining());
        assertEquals("abc", str);
    }
}
