package com.my.project.java;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Rule;
import org.junit.Test;

import com.my.project.java.JavaStream.Person;
import com.my.project.java.JavaStream.Person.Sex;
import com.my.project.java.JavaStream.PersonSupplier;
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
        Stream<String> stream = Stream.of("a", "b", "c");
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
        list.add(new Person(1, "Jerry", Sex.MALE, 20));
        list.add(new Person(2, "Merry", Sex.FEMALE, 20));
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

    /**
     * limit：返回Stream的前面n个元素
     * skip：扔掉前n个元素
     */
    @Test
    public void limitAndSkip() {
        List<Person> persons = new ArrayList<Person>();
        for(int i=1; i<10000; i++) {
            Person person = new Person(i, "name" + i, Sex.MALE, 20);
            persons.add(person);
        }
        List<String> personList = persons.stream().map(Person::getName)
            .limit(10).skip(3).collect(Collectors.toList());
        System.out.println(personList);
        assertEquals(7, personList.size());
    }

    /**
     * sort对limit的影响：将limit/skip放到Stream的sort操作之后，无法达到short-circuiting的目的。
     * 这与sort这个intermediate操作有关：此时系统并不知道Stream排序后的次序如何，所以sorted中的操作看上去
     * 就像完全没有被limit或skip一样
     *
     * 对一个parallel的Stream管道来说，如果其元素是有序的，那么limit操作的成本会比较大，
     * 因为它的返回对象必须是前n个也有一样次序的元素。取而代之的策略是取消元素间的次序，或者不用parallel Stream
     */
    @Test
    public void limitAndSort() {
        List<Person> persons = new ArrayList<>();
        for(int i=5; i>0; i--) {
            Person person = new Person(i, "name" + i, Sex.FEMALE, 20);
            persons.add(person);
        }
        List<Person> personList = persons.stream().sorted(Comparator.comparing(Person::getName))
            .limit(2).collect(Collectors.toList());
        System.out.println(personList);
        assertEquals(2, personList.size());
    }

    /**
     * sort：对Stream进行排序，可以通过map、filter、limit、skip甚至distinct来减少元素数量后再排序
     * 但是：这种优化是有局限性的，即不要求排序后再取值
     */
    @Test
    public void sort() {
        List<Person> persons = new ArrayList<>();
        for(int i=1; i<=5; i++) {
            Person person = new Person(i, "name" + i, Sex.MALE, 20);
            persons.add(person);
        }
        List<Person> personList = persons.stream().limit(2).sorted(Comparator.comparing(Person::getName))
            .collect(Collectors.toList());
        System.out.println(personList);
        assertEquals(2, personList.size());
    }

    /**
     * max
     */
    @Test
    public void max() {
        String lines = "References view - Find All References view includes history of recent searches.\n" +
            "Snippet comment variables - Snippet variables insert correct comment per language.\n" +
            "JS/TS callback display - Now you can see the context of anonymous callbacks.\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(lines.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int longest = reader.lines().mapToInt(String::length).max().getAsInt();
        assertEquals(82, longest);
    }

    /**
     * min
     */
    @Test
    public void min() {
        String lines = "JSDoc Markdown highlighting - Including syntax highlighting for Markdown code blocks in JSDoc.\n" +
            "Simplified debug configuration - Better defaults and Quick Pick UI for initial launch configuration.\n" +
            "Run tasks on folder open - Configure tasks to run when you first open a project folder.\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(lines.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        int shortest = reader.lines().mapToInt(String::length).min().getAsInt();
        assertEquals(87, shortest);
    }

    /**
     * distinct
     */
    @Test
    public void distinct() {
        String lines = "Last month, we added support for multiline search.\n" +
            "This month we improved the search UX to make it easier to use.\n";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(lines.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        List<String> words = reader.lines().flatMap(line -> Stream.of(line.split("[\\s|\\-|\\.|\\,]")))
            .filter(word -> word.length() > 0)
            .map(String::toLowerCase)
            .distinct().sorted().collect(Collectors.toList());
        System.out.println(words);
        assertEquals(17, words.size());
    }

    /**
     * match
     * allMatch：Stream中全部元素符合传入的predicate返回true
     * anyMatch：Stream中只要有一个元素符合传入的predicate返回true
     * noneMatch：Stream中没有一个元素符合传入的predicate返回true
     */
    @Test
    public void match() {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person(1, "name1", Sex.MALE, 10));
        persons.add(new Person(2, "name2", Sex.FEMALE, 21));
        persons.add(new Person(3, "name3", Sex.MALE, 34));
        persons.add(new Person(4, "name4", Sex.FEMALE, 6));
        persons.add(new Person(5, "name5", Sex.MALE, 55));
        boolean isAllAdult = persons.stream().allMatch(p -> p.getAge() > 18);
        assertFalse(isAllAdult);
        boolean isThereAnyChild = persons.stream().anyMatch(p -> p.getAge() < 12);
        assertTrue(isThereAnyChild);
    }

    /**
     * Stream.generate
     * 通过实现Supplier接口，可以控制流的生成，常用于随机数、常量的Stream
     * 默认是串行但无序的，由于是无限的，在管道中必须利用limit之类的操作限制Stream大小
     */
    @Test
    public void generate1() {
        Random seed = new Random();
        Supplier<Integer> random = seed::nextInt;
        Stream.generate(random).limit(10).forEach(System.out::println);
        // OR
        IntStream.generate(() -> (int)(System.nanoTime() % 999))
            .limit(10).forEach(System.out::println);
        assertEquals(20, capture.toString().split("\n").length);
    }

    /**
     * Stream.generate
     * 也可以自己实现Supplier，如在构造海量测试数据时用某种自动的规则给每一个变量赋值，或者依据公式计算Stream的每个元素值
     */
    @Test
    public void generate2() {
        Stream.generate(new PersonSupplier()).limit(10)
            .forEach(System.out::println);
        assertEquals(10, capture.toString().split("\n").length);
    }

    /**
     * Stream.iterate
     * iterate和reduce操作很像，接受一个种子值，和一个UnaryOperator（例如f）。
     * 然后种子值成为Stream的第一个元素，f(seed)为第二个，f(f(seed))为第三个，以此类推。
     * 与Stream.generate类似，在iterate的时候管道必须有limit这样的操作来限制Stream大小
     */
    @Test
    public void iterate() {
        Stream.iterate(0, n -> n + 3).limit(10).forEach(x -> System.out.print(x + " "));
    }

    /**
     * groupingBy/partitioningBy
     * java.utils.stream.Collectors类主要作用是辅助进行各类有用的reduction操作。如将Stream元素进行归组
     */
    @Test
    public void grouping() {
        Map<Integer, List<Person>> personGroups = Stream.generate(new PersonSupplier())
            .limit(100).collect(Collectors.groupingBy(Person::getAge));
        Iterator<Map.Entry<Integer, List<Person>>> it = personGroups.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<Integer, List<Person>> persons = it.next();
            System.out.println("Age " + persons.getKey() + "=" + persons.getValue().size());
        }
        assertTrue(personGroups.size() < 100);
        assertEquals(100, personGroups.values().stream().map(List::size).reduce(Integer::sum).get().intValue());
    }

    /**
     * partitioningBy
     * partitioningBy其实是一种特殊的groupingBy，它依照条件测试的是否两种结果来构造返回的数据结构
     */
    @Test
    public void partitioningBy() {
        Map<Boolean, List<Person>> children = Stream.generate(new PersonSupplier())
            .limit(100).collect(Collectors.partitioningBy(p -> p.getAge() < 18));
        System.out.println("Children number: " + children.get(true).size());
        System.out.println("Adult number: " + children.get(false).size());
        assertEquals(100, children.get(true).size() + children.get(false).size());
    }

}
