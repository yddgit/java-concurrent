package com.my.project.java;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;

import com.my.project.java.JavaStream.Person;
import com.my.project.java.JavaStream.Person.Sex;
import org.springframework.boot.test.rule.OutputCapture;

/**
 * Java Stream API Sample
 */
public class JavaStreamTest {

    /** 捕获控制台输出的内容 */
    @Rule
    public OutputCapture capture = new OutputCapture();

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

    /**
     * map/flatMap：将input Stream的每一个元素映射成output Stream的另外一个元素
     */
    @Test
    public void map() {
        // map: to upper case
        List<String> wordList = Arrays.asList("hello", "java8", "stream", "api");
        List<String> output = wordList.stream().map(String::toUpperCase).collect(Collectors.toList());
        assertArrayEquals(new String[]{"HELLO", "JAVA8", "STREAM", "API"}, output.toArray(new String[output.size()]));

        // map: square
        List<Integer> nums = Arrays.asList(1, 2, 3, 4);
        List<Integer> squareNums = nums.stream().map(n -> n*n).collect(Collectors.toList());
        assertArrayEquals(new Integer[]{1, 4, 9, 16}, squareNums.toArray(new Integer[squareNums.size()]));

        // flatMap: 将inputStream中的层级结构扁平化，最终的Stream里面已经没有List了，都是直接的数字
        Stream<List<Integer>> inputStream = Stream.of(
            Arrays.asList(1),
            Arrays.asList(2, 3),
            Arrays.asList(4, 5, 6)
        );
        Stream<Integer> outputStream = inputStream.flatMap((childList) -> childList.stream());
        assertArrayEquals(new Integer[]{1,2,3,4,5,6}, outputStream.collect(Collectors.toList()).toArray(new Integer[6]));
    }

    /**
     * filter：对原始Stream进行某项测试，通过测试的元素被留下来生成一个新Stream
     */
    @Test
    public void filter() {
        // even number
        Integer[] sixNums = {1, 2, 3, 4, 5, 6};
        Integer[] evens = Stream.of(sixNums).filter(n -> n%2 == 0).toArray(Integer[]::new);
        assertArrayEquals(new Integer[]{2, 4, 6}, evens);

        // words
        String lines = "Multiline search improvements - Easily create multiline search patterns without using regex.\n" +
            "Custom title bar on Linux - The custom title and menu bar is now the default on Linux.\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(lines.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> output =  reader.lines()
            .flatMap(line -> Stream.of(line.split("[\\s|\\-|\\.]")))
            .filter(word -> word.length() > 0)
            .collect(Collectors.toList());
        assertEquals(28, output.size());
    }

    /**
     * forEach: terminal操作，执行后Stream元素就被消费了，无法对一个Stream进行两次terminal运算
     *
     * 下面的代码是错误的：
     * stream.forEach(element -> doOneThing(element));
     * stream.forEach(element -> doAnotherThing(element));
     *
     * forEach不能修改自己包含的本地变量值，也不能用break/return之类的关键字提前结束循环
     */
    @Test
    public void forEach() {
        List<Person> list = new ArrayList<>();
        list.add(new Person("Jerry", Sex.MALE));
        list.add(new Person("Merry", Sex.FEMALE));
        // java 8
        list.stream()
            .filter(p -> p.getGender() == Sex.MALE)
            .forEach(p -> System.out.println(p.getName()));
        assertThat(capture.toString(), containsString("Jerry"));
        // pre-java 8
        for(Person p : list) {
            if(p.getGender() == Sex.FEMALE) {
                System.out.println(p.getName());
            }
        }
        assertThat(capture.toString(), containsString("Merry"));
    }

    /**
     * peek：可以实现与forEach类似的功能，但它是一个Intermediate操作
     */
    @Test
    public void peek() {
        Stream.of("one", "two", "three", "four")
            .filter(e -> e.length() > 3)
            .peek(e -> System.out.println("Filtered value: " + e))
            .map(String::toUpperCase)
            .peek(e -> System.out.println("Mapped value: " + e))
            .collect(Collectors.toList());
        assertThat(capture.toString(), containsString("Filtered value: three"));
        assertThat(capture.toString(), containsString("Mapped value: THREE"));
        assertThat(capture.toString(), containsString("Filtered value: four"));
        assertThat(capture.toString(), containsString("Mapped value: FOUR"));
    }

    /**
     * Optional：一个容器，它可能含有某值，或者不包含。目的是尽可能避免NPE问题
     *
     * 与if(xx != null)相比，Optional代码的可读性更好，而且它提供的是编译时检查，能极大降低NPE问题
     * Stream中的findAny, max/min, reduce等方法都返回Optional值。还有如：IntStream.average()返回OptionalDouble等
     */
    @Test
    public void optional() {
        // Optional
        String a = "abcd", b = null;
        print(a);
        print("");
        print(b);
        System.out.println(getLength(a));
        System.out.println(getLength(""));
        System.out.println(getLength(b));
    }

    private static void print(String text) {
        // java 8
        Optional.ofNullable(text).ifPresent(System.out::println);
        // pre-java 8
        if(text != null) {
            System.out.println(text);
        }
    }

    private static int getLength(String text) {
        // java 8
        return Optional.ofNullable(text).map(String::length).orElse(-1);
        // pre-java 8
        //return (text != null) ? text.length() : -1;
    }

    /**
     * findFirst: terminal兼short-circuiting操作，它总是返回Stream第一个元素或者空
     */
    @Test
    public void findFirst() {
        assertTrue(1 == Stream.of(1, 2, 3, 4).findFirst().get());
    }

    /**
     * reduce：把Stream元素组合起来，提供一个起始值，然后依照运算规则和Stream的第1个、第2个、第n个元素组合。
     * 从这个意义上说，字符串拼接、数值sum、min、max、average都是特殊的reduce
     *
     * 在没有起始值的情况，会把Stream的前面两个元素组合起来，返回的是Optional
     *
     * 对于有起始值的reduce()都返回具体的对象，而对于没有起始值的reduce()，返回的是Optional
     */
    @Test
    public void reduce() {
        // 字符串连接
        String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);
        assertEquals("ABCD", concat);
        // 求最小值
        double minValue = Stream.of(-1.5, 1.0, -3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);
        assertEquals(-3.0, minValue, 0.0000001);
        // 求和，有起始值
        int sumValue = Stream.of(1, 2, 3, 4).reduce(0, Integer::sum);
        assertEquals(10, sumValue);
        // 求和，无起始值
        sumValue = Stream.of(1, 2, 3, 4).reduce(Integer::sum).get();
        assertEquals(10, sumValue);
        // 过滤
        concat = Stream.of("a", "B", "c", "D", "e", "F")
            .filter(x -> x.compareTo("Z") > 0)
            .reduce("", String::concat);
        assertEquals("ace", concat);
    }

}
