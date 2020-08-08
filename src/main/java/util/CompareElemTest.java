package util;

import sort.BaseSort;

import java.util.Arrays;

/**
 * @className: CompareElemTest
 * @description:
 *              测试随机数的比较时间与相同数字的比较时间
 *
 *              结果：
 *                  [random]:0.04 seconds
 *                  [duplicate]:0.03 seconds
 * @author: ZSZ
 * @date: 2020/4/5 22:36
 */
public class CompareElemTest {

    public static void main(String[] args) {
        int testNum = 50000000;
        Integer[] a1 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, testNum);
        Integer[] a2 = new Integer[testNum];
        Arrays.fill(a2,1000);

        StopWatch stopWatch = new StopWatch();
        for (int i = 0; i < testNum-1; i++) {
            BaseSort.less(a1[i],a1[i+1]);
        }
        System.out.println(String.format("[random]:%.2f seconds",stopWatch.elapsedTime()));

        stopWatch = new StopWatch();
        for (int i = 0; i < testNum-1; i++) {
            BaseSort.less(a2[i],a1[i+1]);
        }
        System.out.println(String.format("[duplicate]:%.2f seconds",stopWatch.elapsedTime()));

    }

}
