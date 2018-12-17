package com.my.project.java;

import java.util.stream.IntStream;

/**
 * Java 8 Stream 操作
 * https://www.ibm.com/developerworks/cn/java/j-lo-java8streamapi/
 *
 * <h1>一、介绍</h1>
 *
 * Stream API专注于对集合对象进行便利、高效的聚合操作，提供两种串行和并行两种操作模式。
 * 其中并行模式能够充分利用多核处理器的优势，使用fork/join并行方式来拆分任务，只是此时原有元素的次序没法保证。
 *
 * <h1>二、与Iterator的区别</h1>
 *
 * Stream不是集合，不保存数据，是有关算法和计算的，更像一个高级版本的Iterator。
 * - Iterator: 只能显式的遍历元素执行操作。
 * - Stream: 只需要给出需要对元素执行的操作，如过滤掉长度大于10的字符串、获取每个字符串的首字母等，Stream会隐式地在内部进行遍历，做相应的数据转换。
 * 与Iterator不同的是，Stream可以并行化操作，且数据源本身可以是无限的。
 *
 * <h1>三、基本步骤</h1>
 *
 * 数据源(source) -> 数据转换 -> 执行操作获取结果
 *
 * <h1>四、Stream Source</h1>
 *
 * 从Collection和Arrays
 * - java.util.Collection.stream()
 * - java.util.Collection.parallelStream()
 * - Arrays.stream(T array)
 * - Stream.of()
 * 从BufferedReader
 * - java.io.BufferedReader.lines()
 * 静态工厂
 * - java.util.stream.IntStream.range()
 * - java.nio.file.Files.walk()
 * 自己构建
 * - java.util.Spliterator
 * 其他
 * Random.ints()
 * BitSet.stream()
 * Pattern.splitAsStream(java.lang.CharSequence)
 * JarFile.stream()
 *
 * <h1>五、操作类型</h1>
 *
 * - Intermediate: 一个流以后面跟随零个或多个intermediate操作，目的是打开流，做某种程度的数据映射/过滤，然后返回一个新的流，交给下一个操作使用。
 * 这类操作都是惰性化的(lazy)，仅仅调用到这类方法，并没有真正开始流的遍历。
 *
 *   map(mapToInt, flatMap), filter, distinct, sorted, peek, limit, skip, parallel, sequential, unordered
 *
 * - Terminal: 一个流只能有一个Terminal操作，当这个操作执行后，流就被使用"光"了。
 * 这必定是流的最后一个操作，Terminal操作的执行，才会真正开始流的遍历并生成一个结果或者side effect
 *
 *   forEach, forEachOrdered, toArray, reduce, collect, min, max, count, anyMatch, allMatch, noneMatch, findFirst, findAny, iterator
 *
 * - Short-circuiting: 对于一个intermediate操作，如果它接受的是一个无限大的Stream，但返回一个有限的新Stream，
 * 或对于一个terminal操作，如果它接受的是一个无限大的Stream，但能在有限的时间计算出结果。
 *
 *   anyMatch, allMatch, noneMatch, findFirst, findAny, limit
 *
 * 在对一个Stream进行多次intermediate操作时，所有操作都是lazy的，多个转换操作只会在Terminal操作的时候融合起来，一次循环完成。
 * 可以简单理解：Stream里有个操作函数的集合，每次转换操作就是把转换函数放入这个集合中，在Terminal操作的时候循环Stream对应的集合，然后对每个元素执行所有的函数。
 *
 * e.g.: int sum = widgets.stream().filter(w -> w.getColor() == RED).mapToInt(w -> w.getWeight()).sum();
 * 上例中：stream()获取当前widget的source，filter和mapToInt为intermediate操作，进行筛选和转换，最后sum()为terminal操作，对符合条件的widget重量求和。
 *
 * <h1>六、总结</h1>
 *
 * - 不是数据结构
 * - 没有内部存储，只是用操作管理从source抓取数据
 * - 不修改所封装的底层数据结构的数据，每次操作会产生一个新的Stream
 * - 所有操作必须以lambda表达式为参数
 * - 不支持索引访问
 * - 很容易生成数组或者List
 * - 惰性化
 * - 很多Stream操作是向后延迟的，一直到它弄清楚最后需要多少数据才会开始
 * - Intermediate操作永远是惰性化的
 * - 并行能力
 * - 当一个Stream是并行化的，就不需要再写多线程代码，所有对它的操作会自动并行进行
 * - 可以是无限的。集合有固定大小，Stream则不必，limit(n)和findFirst()这类的short-circuiting操作可以对无限的Stream进行运算并很快完成
 *
 */
public class JavaStream {

    /**
     * 分解质因数
     * @param n 待分解的数
     * @return 因子列表
     */
    public static IntStream factorize(int n) {
        return IntStream.range(2, n)
                .filter(x -> n % x == 0)
                .mapToObj(x -> IntStream.concat(IntStream.of(x), factorize(n / x)))
                .findFirst()
                .orElse(IntStream.of(n));
    }

    public static class Person {
        public enum Sex {
            MALE, FEMALE
        }

        public Person() { }

        public Person(String name, Sex gender) {
            this.name = name;
            this.gender = gender;
        }

        private String name;
        private Sex gender;

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Sex getGender() {
            return gender;
        }
        public void setGender(Sex gender) {
            this.gender = gender;
        }
    }

}
