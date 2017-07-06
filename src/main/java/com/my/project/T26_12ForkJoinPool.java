package com.my.project;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * ForkJoinPool：计算一个大数组的元素和
 * 
 * <pre>
 *      /--> fork --> join
 * task ---> fork --> join --> join --> result
 *      \--> fork --> join
 * </pre>
 * 
 * @author yang
 *
 */
public class T26_12ForkJoinPool {

	static int[] nums = new int[1_000_000];
	static final int MAX_NUM = 50000;
	static Random r = new Random();

	//初始化数组
	static {
		for(int i=0; i<nums.length; i++) {
			nums[i] = r.nextInt(100);
		}
		System.out.println("Arrays.stream(x).sum()计算结果：" + Arrays.stream(nums).sum());
	}

	/**
	 * 加和任务（无返回值）
	 * @author yang
	 */
	static class AddAction extends RecursiveAction {

		private static final long serialVersionUID = 8044697521943770570L;

		int start, end;

		AddAction(int s, int e) {
			this.start = s;
			this.end = e;
		}

		@Override
		protected void compute() {
			if(end-start <= MAX_NUM) {
				long sum = 0L;
				for(int i=start; i<end; i++) {
					sum+=nums[i];
				}
				System.out.println("RecursiveAction计算过程：from:" + start + " to:" + end + " = " + sum);
			} else {
				int middle = start + (end-start)/2;
				AddAction subTask1 = new AddAction(start, middle);
				AddAction subTask2 = new AddAction(middle, end);
				subTask1.fork();//启动新的子任务（线程）
				subTask2.fork();//启动新的子任务（线程）
			}
		}

	}

	/**
	 * 加和任务（有返回值）
	 * @author yang
	 */
	static class AddTask extends RecursiveTask<Long> {

		private static final long serialVersionUID = -8772197419051115598L;

		int start, end;

		AddTask(int s, int e) {
			this.start = s;
			this.end = e;
		}

		@Override
		protected Long compute() {
			if(end-start <= MAX_NUM) {
				long sum = 0L;
				for(int i=start; i<end; i++) {
					sum+=nums[i];
				}
				return sum;
			}

			int middle = start + (end-start)/2;
			AddTask subTask1 = new AddTask(start, middle);
			AddTask subTask2 = new AddTask(middle, end);
			subTask1.fork();//启动新的子任务（线程）
			subTask2.fork();//启动新的子任务（线程）

			return subTask1.join() + subTask2.join();
		}

	}

	public static void main(String[] args) throws IOException {

		//RecursiveAction无返回值
		ForkJoinPool forkJoinPool1 = new ForkJoinPool();
		AddAction task1 = new AddAction(0, nums.length);
		forkJoinPool1.execute(task1);

		//RecursiveTask有返回值
		ForkJoinPool forkJoinPool2 = new ForkJoinPool();
		AddTask task2 = new AddTask(0, nums.length);
		forkJoinPool2.execute(task2);
		long result = task2.join();
		System.out.println("RecursiveTask计算结果：" + result);

		//主线程不阻塞的话看不到输出
		System.in.read();
	}
}
