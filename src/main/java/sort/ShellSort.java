package sort;

import util.RandomArrayUtil;
import util.StopWatch;

import java.sql.SQLOutput;
import java.util.Arrays;

/**
 * @className: ShellSort
 * @description: 希尔排序
 *
 *              1959年Shell发明，第一个突破O(n2)的排序算法，是简单插入排序的改进版。
 *              它与插入排序的不同之处在于，它会优先比较距离较远的元素。
 *              希尔排序又叫缩小增量排序。
 *
 *              算法描述
 *                  先将整个待排序的记录序列分割成为若干子序列分别进行直接插入排序，具体算法描述：
 *
 *                  1. 选择一个增量序列t1，t2，…，tk，其中ti>tj，tk=1；
 *                  2. 按增量序列个数k，对序列进行k 趟排序；
 *                  3. 每趟排序，根据对应的增量ti，将待排序列分割成若干长度为m 的子序列，
 *                     分别对各子表进行直接插入排序。
 *                     仅增量因子为1 时，整个序列作为一个表来处理，表长度即为整个序列的长度。
 *
 *              算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n(log(n))^2)   最好 O(n*log(n))     最坏O(n(log(n))^2)
 *                  空间复杂度：
 *                          O(1)
 *                  稳定性：
 *                          不稳定
 *
 *              步长序列        最坏情况下复杂度
 *               n/2^i             O(n^2)
 *              2^k - 1           O(n^(1.5))
 *             2^i * 3^j       O(n * log(n)^2)
 *
 *             目前已知最好的步长序列：1、5、19、41、109、209、505、929、2161、3905、8929、16001 。。。
 *                                  根据 9*4^i - 9*2^i +1 和 2^(i+2) * (2^(i+2) - 3) +1 计算
 *
 *              "比较在希尔排序中是最主要的操作，而不是交换。”
 *              用这样步长序列的希尔排序比插入排序要快，甚至在小数组中比快速排序和堆排序还快，但是在涉及大量数据时希尔排序还是比快速排序慢。
 *
 *              希尔排序高效的原因是：权衡了子数组的规模和有序性
 *
 *              希尔排序的核心在于间隔序列的设定。
 *              既可以提前设定好间隔序列，也可以动态的定义间隔序列。
 *              动态定义间隔序列的算法是《算法（第4版）》的合著者Robert Sedgewick提出的。　
 *
 * @author: ZSZ
 * @date: 2020/4/14 13:41
 */
public class ShellSort extends BaseSort implements SortedCompared{

    private String  FORMAT_WITH_LENGTH_AND_STEP = "Array Length: %s , Shell sort max group:%s";

    /**
     * 步长为 2 的希尔排序
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void sort(K[] a){
        shellSort(a,3);
    }

    /**
     * 标准希尔排序
     *
     * 使用递增序列 1、4、13、40、121、364… 的希尔排序所需的比较次数不会超过 N 的若干倍乘以递增序列的长度。
     *
     * Q：如何通过h递增序列优化？
     * A：在实际应用中，group 的取值使用以上递增序列基本就足够了。
     *    但是我们为了追求性能的提升，也使用以下的序列，使性能提高 20%-40% 。
     *    1、5、19、41、109、209、505、929、2161、3905、8929、16001、
     *    36289、64769、146305、260609
     *
     * @param a 待排序数组
     * @param <K>
     */
    public static <K extends Comparable<K>> void shellSort(K[] a, int step){
        if(a==null)return ;
        int length = a.length;
        if(length==0)return;

        //分组group 步长step
        for(int group = length/step; group > 0; group = group/step){
            for(int i=group; i<length ;i++){
                int j = i;
                K cur = a[i];
                while(j - group>=0 && less(cur,a[j - group])){
                    //使用覆盖，减少开销
                    a[j] = a[j-group];
                    j -= group;
                }
                a[j] = cur;
            }

            //当 0<group<step时 ，为了避免 此时 group/step == 0 跳过group为1的分组
            if(group/step>0 && group/step < step)group = step;
        }
    }

    /**
     * 在希尔排序之前进行预处理
     * @param a
     * @param step
     * @param <K>
     */
    public static <K extends Comparable<K>> void shellSortWithPretreatment(K[] a, int step){
        pretreatment(a);
        shellSort(a,step);
    }



    /**
     * 测试数据规模： 1000000
     *
     * 测试结果：
     * 在数据量大的情况下，经过预处理的排序，有更出色的表现
     *
     * Array created!
     * Step:2
     * Array Length: 1000000 , Shell sort max group:500001
     * shellSort method[random]:(1.76 seconds)
     * Array Length: 1000000 , Shell sort max group:500001
     * shellSort method[random+duplicate]:(2.12 seconds)
     *
     * Step:3
     * Array Length: 1000000 , Shell sort max group:333334
     * shellSort method[random]:(1.23 seconds)
     * Array Length: 1000000 , Shell sort max group:333334
     * shellSort method[random+duplicate]:(1.50 seconds)
     *
     * Step:7
     * Array Length: 1000000 , Shell sort max group:142858
     * shellSort method[random]:(1.42 seconds)
     * Array Length: 1000000 , Shell sort max group:142858
     * shellSort method[random+duplicate]:(1.73 seconds)
     *
     * Step:19
     * Array Length: 1000000 , Shell sort max group:52632
     * shellSort method[random]:(2.04 seconds)
     * Array Length: 1000000 , Shell sort max group:52632
     * shellSort method[random+duplicate]:(2.61 seconds)
     *
     * Step:97
     * Array Length: 1000000 , Shell sort max group:10310
     * shellSort method[random]:(12.75 seconds)
     * Array Length: 1000000 , Shell sort max group:10310
     * shellSort method[random+duplicate]:(15.73 seconds)
     *
     * Step:100000
     * Array Length: 1000000 , Shell sort max group:11
     * shellSort method[random]:(636.45 seconds)
     * Array Length: 1000000 , Shell sort max group:11
     * shellSort method[random+duplicate]:(895.19 seconds)
     *
     * Step:7
     * Array Length: 1000000 , Shell sort max group:142858
     * shellSortWithPretreatment method[random]:(1.47 seconds)
     * Array Length: 1000000 , Shell sort max group:142858
     * shellSortWithPretreatment method[random+duplicate]:(1.78 seconds)
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, 1000000);
        Integer[] a12 = Arrays.copyOf(a11, a11.length);
        Integer[] a13 = Arrays.copyOf(a11, a11.length);
        Integer[] a14 = Arrays.copyOf(a11, a11.length);
        Integer[] a15 = Arrays.copyOf(a11, a11.length);
        Integer[] a16 = Arrays.copyOf(a11, a11.length);
        Integer[] a17 = Arrays.copyOf(a11, a11.length);

        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 100, 1000000);
        Integer[] a22 = Arrays.copyOf(a21, a21.length);
        Integer[] a23 = Arrays.copyOf(a21, a21.length);
        Integer[] a24 = Arrays.copyOf(a21, a21.length);
        Integer[] a25 = Arrays.copyOf(a21, a21.length);
        Integer[] a26 = Arrays.copyOf(a21, a21.length);
        Integer[] a27 = Arrays.copyOf(a21, a21.length);

        System.out.println("Array created!");

        // Step = 2
        StopWatch stopWatch = new StopWatch();
        shellSort(a11,2);
        System.out.println("Step:"+2);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a11.length,a11.length/2+1));
        System.out.println(String.format(formatStringWithRandom, "shellSort", stopWatch.elapsedTime()));
        shellSort(a21,2);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a21.length,a11.length/2+1));
        System.out.println(String.format(formatStringWithDuplicate, "shellSort", stopWatch.elapsedTime()));
        System.out.println(isSorted(a11));
        System.out.println(isSorted(a21));
        System.out.println();

        // Step = 3
        stopWatch = new StopWatch();
        shellSort(a12,3);
        System.out.println("Step:"+3);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a12.length,a12.length/3+1));
        System.out.println(String.format(formatStringWithRandom, "shellSort", stopWatch.elapsedTime()));
        shellSort(a22,3);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a22.length,a22.length/3+1));
        System.out.println(String.format(formatStringWithDuplicate, "shellSort", stopWatch.elapsedTime()));
        System.out.println(isSorted(a12));
        System.out.println(isSorted(a22));
        System.out.println();

        // Step = 7
        stopWatch = new StopWatch();
        shellSort(a13,7);
        System.out.println("Step:"+7);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a13.length,a23.length/7+1));
        System.out.println(String.format(formatStringWithRandom, "shellSort", stopWatch.elapsedTime()));
        shellSort(a23,7);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a23.length,a23.length/7+1));
        System.out.println(String.format(formatStringWithDuplicate, "shellSort", stopWatch.elapsedTime()));
        System.out.println(isSorted(a13));
        System.out.println(isSorted(a23));
        System.out.println();

        // Step = 19
        stopWatch = new StopWatch();
        shellSort(a14,19);
        System.out.println("Step:"+19);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a14.length,a14.length/19+1));
        System.out.println(String.format(formatStringWithRandom, "shellSort", stopWatch.elapsedTime()));
        shellSort(a24,19);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a24.length,a24.length/19+1));
        System.out.println(String.format(formatStringWithDuplicate, "shellSort", stopWatch.elapsedTime()));
        System.out.println(isSorted(a14));
        System.out.println(isSorted(a24));
        System.out.println();

        // Step = 97
        stopWatch = new StopWatch();
        shellSort(a15,97);
        System.out.println("Step:"+97);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a15.length,a15.length/97+1));
        System.out.println(String.format(formatStringWithRandom, "shellSort", stopWatch.elapsedTime()));
        shellSort(a25,97);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a25.length,a25.length/97+1));
        System.out.println(String.format(formatStringWithDuplicate, "shellSort", stopWatch.elapsedTime()));
        System.out.println(isSorted(a15));
        System.out.println(isSorted(a25));
        System.out.println();

        // Step = 100000
//        stopWatch = new StopWatch();
//        shellSort(a16,100000);
//        System.out.println("Step:"+100000);
//        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a16.length,a16.length/100000+1));
//        System.out.println(String.format(formatStringWithRandom, "shellSort", stopWatch.elapsedTime()));
//        shellSort(a26,100000);
//        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a26.length,a26.length/100000+1));
//        System.out.println(String.format(formatStringWithDuplicate, "shellSort", stopWatch.elapsedTime()));
//        System.out.println(isSorted(a16));
//        System.out.println(isSorted(a26));
//        System.out.println();

        //经过预处理的希尔排序
        stopWatch = new StopWatch();
        System.out.println("Step:"+7);
        shellSortWithPretreatment(a17,7);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a17.length,a17.length/7+1));
        System.out.println(String.format(formatStringWithRandom, "shellSortWithPretreatment", stopWatch.elapsedTime()));
        shellSortWithPretreatment(a27,7);
        System.out.println(String.format(FORMAT_WITH_LENGTH_AND_STEP,a27.length,a27.length/7+1));
        System.out.println(String.format(formatStringWithDuplicate, "shellSortWithPretreatment", stopWatch.elapsedTime()));
        System.out.println(isSorted(a17));
        System.out.println(isSorted(a27));
        System.out.println();
    }
}
