package com.my.project.java;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import org.junit.Test;

/**
 * CompletableFuture使用示例
 * https://www.ibm.com/developerworks/cn/java/j-cf-of-jdk8/index.html
 */
public class CompletableFutureTest {

    /**
     * 创建完整的CompletableFuture
     */
    @Test
    public void completableFuture() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message");
        assertTrue(cf.isDone());
        // getNow()返回计算结果或者null
        assertEquals("message", cf.getNow(null));
    }

    /**
     * 简单的异步场景：CompletableFuture是使用ForkJoinPool实现异步执行，这种方式使用了daemon线程执行Runnable任务
     */
    @Test
    public void runAsync() {
        CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
            assertTrue(Thread.currentThread().isDaemon());
            sleep(1);
        });
        assertFalse(cf.isDone());
        sleep(2);
        assertTrue(cf.isDone());
    }

    /**
     * 同步执行：在异步计算正常完成的前提下执行指定的动作
     */
    @Test
    public void thenApply() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApply(s -> {
            assertFalse(Thread.currentThread().isDaemon());
            return s.toUpperCase();
        });
        assertEquals("MESSAGE", cf.getNow(null));
    }

    /**
     * 异步执行
     */
    @Test
    public void thenApplyAsync() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().isDaemon());
            sleep(1);
            return s.toUpperCase();
        });
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    /**
     * 使用固定线程池完成异步执行
     */
    @Test
    public void thenApplyAsyncWithExecutor() {
        ExecutorService executor = Executors.newFixedThreadPool(3, new ThreadFactory() {
            int count = 1;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "custom-executor-" + count++);
            }
        });
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(s -> {
            assertTrue(Thread.currentThread().getName().startsWith("custom-executor-"));
            assertFalse(Thread.currentThread().isDaemon());
            sleep(1);
            return s.toUpperCase();
        }, executor);
        assertNull(cf.getNow(null));
        assertEquals("MESSAGE", cf.join());
    }

    /**
     * 消费者消费计算结果：消费者是同步执行的，所以不需要在CompletableFuture里对结果进行合并
     */
    @Test
    public void thenAccept() {
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture("thenAccept message").thenAccept(s -> result.append(s));
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * 异步消费
     */
    @Test
    public void thenAcceptAsync() {
        StringBuilder result = new StringBuilder();
        CompletableFuture<Void> cf = CompletableFuture.completedFuture("thenAcceptAsync message")
            .thenAcceptAsync(s -> result.append(s));
        cf.join();
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * (require java version >= 9)
     * 
     * 计算过程中的异常示例
     *
     * 首先创建一个CompletableFuture(计算完毕)，然后调用thenApplyAsync返回一个新的CompletableFuture，
     * 接着通过使用delayedExecutor(timeout, timeUnit)方法延迟1秒钟执行。然后创建一个handler(exceptionHandler)，
     * 它会处理异常，返回另一个字符串"message upon cancel"。接下来进入join()方法，执行大写转换操作，并且抛出CompletionException异常
     */
    /*
    @Test
    public void completeExceptionally() {
        CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(
            String::toUpperCase, CompletableFuture.delayedExecutor(1, TimeUnit.SECONDS));
        CompletableFuture<String> exceptionHandler = cf.handle((s, th) -> (th != null) ? "message upon cancel" : "");
        cf.completeExceptionally(new RuntimeException("completed exceptionally"));
        assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());

        try {
            cf.join();
            fail("Should have thrown an exception");
        } catch (CompletionException e) {
            assertEquals("completed exceptionally", e.getCause().getMessage());
        }

        assertEquals("message upon cancel", exceptionHandler.join());
    }
    */

    /**
     * (require java version >= 9)
     * 
     * 取消计算任务，与异常处理类似，可以通过调用cancel(mayInterruptedIfRunning)方法取消计算任务
     * 
     * 此外，cancel()方法与completeExceptionally(new CancellationException())等价
     */
    /*
    @Test
    public void cancel() {
    	CompletableFuture<String> cf = CompletableFuture.completedFuture("message").thenApplyAsync(
    		String::toUpperCase, CompletableFuture.delayedExecutor(1,  TimeUnit.SECONDS));
    	CompletableFuture<String> exceptionHandler = cf.exceptionally(throwable -> "canceled message");
    	assertTrue("Was not canceled", cf.cancel(true));
    	assertTrue("Was not completed exceptionally", cf.isCompletedExceptionally());
    	assertEquals("canceled message", exceptionHandler.join());
    }
    */

    /**
     * 一个CompletableFuture VS 两个异步计算，创建一个CompletableFuture接收两个异步计算结果
     */
    @Test
    public void applyToEither() {
    	String original = "Message";
    	CompletableFuture<String> cf1 = CompletableFuture.completedFuture(original)
            .thenApplyAsync(s -> delayedUpperCase(s));
        CompletableFuture<String> cf2 = CompletableFuture.completedFuture(original)
            .thenApplyAsync(s -> delayedUpperCase(s));
    	CompletableFuture<String> cf = cf1.applyToEither(
    	    cf2, s -> s + " from applyToEither");
    	assertTrue(cf.join().endsWith(" from applyToEither"));
    }

    /**
     * 消费上一步异步计算的结果
     */
    @Test
    public void acceptEither() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture<String> cf1 = CompletableFuture.completedFuture(original)
            .thenApplyAsync(s -> delayedUpperCase(s));
        CompletableFuture<String> cf2 = CompletableFuture.completedFuture(original)
            .thenApplyAsync(s -> delayedUpperCase(s));
        CompletableFuture<Void> cf = cf1.acceptEither(
            cf2, s -> result.append(s).append("acceptEither"));
        cf.join();
        assertTrue("Result was empty", result.toString().endsWith("acceptEither"));
    }

    /**
     * 运行两个阶段后执行
     */
    @Test
    public void runAfterBoth() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture(original).thenApply(String::toUpperCase).runAfterBoth(
            CompletableFuture.completedFuture(original).thenApply(String::toLowerCase),
            () -> result.append("done")
        );
        assertEquals("done", result.toString());
    }

    /**
     * 也可以通过如下方式处理异步计算结果
     */
    @Test
    public void thenAcceptBoth() {
        String original = "Message";
        StringBuilder result = new StringBuilder();
        CompletableFuture.completedFuture(original).thenApply(String::toUpperCase).thenAcceptBoth(
            CompletableFuture.completedFuture(original).thenApply(String::toLowerCase),
            (s1, s2) -> result.append(s1 + s2)
        );
        assertEquals("MESSAGEmessage", result.toString());
    }

    /**
     * 整合两个计算结果(同步)
     */
    @Test
    public void thenCombine() {
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture.completedFuture(original).thenApply(s -> delayedUpperCase(s)).thenCombine(
            CompletableFuture.completedFuture(original).thenApply(s -> delayedLowerCase(s)),
            (s1, s2) -> s1 + s2
        );
        assertEquals("MESSAGEmessage", cf.getNow(null));
    }

    /**
     * 整合计算结果(异步)，由于不确定哪一个方法最终执行完毕，所以需要调用join()方法等待
     */
    @Test
    public void thenCombineAsync() {
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture.completedFuture(original)
            .thenApplyAsync(s -> delayedUpperCase(s))
            .thenCombine(
                CompletableFuture.completedFuture(original).thenApplyAsync(s -> delayedLowerCase(s)),
                (s1, s2) -> s1 + s2
            );
        assertEquals("MESSAGEmessage", cf.join());
    }

    /**
     * thenCompose()方法也可以实现两个方法执行后返回结果的连接
     */
    @Test
    public void thenCompose() {
        String original = "Message";
        CompletableFuture<String> cf = CompletableFuture.completedFuture(original).thenApply(s -> delayedUpperCase(s))
            .thenCompose(upper -> CompletableFuture.completedFuture(original).thenApply(s -> delayedLowerCase(s))
            .thenApply(s -> upper + s));
        assertEquals("MESSAGEmessage", cf.join());
    }

    /**
     * 在几个计算过程中任意一个完成后创建CompletableFuture
     */
    @Test
    public void anyOf() {
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture<String>> futures = messages.stream()
            .map(msg -> CompletableFuture.completedFuture(msg).thenApply(s -> delayedUpperCase(s)))
            .collect(Collectors.toList());
        CompletableFuture.anyOf(futures.toArray(new CompletableFuture<?>[futures.size()]))
            .whenComplete((res, throwable) -> {
                if(throwable == null) {
                    assertTrue(isUpperCase((String)res));
                    result.append(res);
                }
            });
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * 在所有计算过程都完成后，创建一个CompletableFuture
     */
    @Test
    public void allOf() {
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture<String>> futures = messages.stream()
            .map(msg -> CompletableFuture.completedFuture(msg).thenApply(s -> delayedUpperCase(s)))
            .collect(Collectors.toList());
        CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]))
            .whenComplete((v, throwable) -> {
                futures.forEach(cf -> assertTrue(isUpperCase(cf.getNow(null))));
                result.append("done");
            });
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * 异步执行allOfSync
     */
    @Test
    public void allOfAsync() {
        StringBuilder result = new StringBuilder();
        List<String> messages = Arrays.asList("a", "b", "c");
        List<CompletableFuture<String>> futures = messages.stream()
            .map(msg -> CompletableFuture.completedFuture(msg).thenApplyAsync(s -> delayedUpperCase(s)))
            .collect(Collectors.toList());
        CompletableFuture<?> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[futures.size()]))
            .whenComplete((v, throwable) -> {
                futures.forEach(cf -> assertTrue(isUpperCase(cf.getNow(null))));
                result.append("done");
            });
        allOf.join();
        assertTrue("Result was empty", result.length() > 0);
    }

    /**
     * CompletableFuture实例
     */
    @Test
    public void sample() {
        Car.cars().thenCompose(cars -> {
            List<CompletionStage<Car>> updatedCars = cars.stream()
                    .map(car -> rating(car.manufacturerId).thenApply(r -> {
                        car.rating = r;
                        return car;
                    })).collect(Collectors.toList());
            CompletableFuture<Void> done = CompletableFuture.allOf(updatedCars.toArray(new CompletableFuture[updatedCars.size()]));
            return done.thenApply(v -> updatedCars.stream().map(CompletionStage::toCompletableFuture)
                    .map(CompletableFuture::join).collect(Collectors.toList()));
        }).whenComplete((cars, throwable) -> {
            if(throwable == null) {
                cars.forEach(System.out::println);
            } else {
                throw new RuntimeException(throwable);
            }
        }).toCompletableFuture().join();
    }

    /**
     * sleep several seconds
     * @param seconds
     */
    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String delayedUpperCase(String s) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return s.toUpperCase();
    }

    private String delayedLowerCase(String s) {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return s.toLowerCase();
    }

    private boolean isUpperCase(String s) {
        if(s.equals(s.toUpperCase())) {
            return true;
        }
        return false;
    }

    public static class Car {
        String manufacturerId;
        double rating = 0.0;
        @Override
		public String toString() {
			return "Car [manufacturerId=" + manufacturerId + ", rating=" + rating + "]";
		}
		public static CompletableFuture<List<Car>> cars() {
            List<Car> list = new ArrayList<>();
            for(int i=0; i<10; i++) {
                Car c = new Car();
                c.manufacturerId = "m" + i;
                list.add(c);
            }
            return CompletableFuture.completedFuture(list);
        }
    }

    public CompletableFuture<Double> rating(String manufacturerId) {
        return CompletableFuture.completedFuture(Math.round(new Random().nextDouble() * 100)/100.0);
    }

}
