package sort;

import util.RandomArrayUtil;
import util.StopWatch;

import java.util.ArrayList;
import java.util.List;

/**
 * @className: RadixSort
 * @description: 基数排序
 *
 *              基数排序是按照低位先排序，然后收集；再按照高位排序，然后再收集；依次类推，直到最高位。
 *              有时候有些属性是有优先级顺序的，先按低优先级排序，再按高优先级排序。
 *              最后的次序就是高优先级高的在前，高优先级相同的低优先级高的在前。
 *
 *              算法描述：
 *                  1. 取得数组中的最大数，并取得位数；
 *                  2. arr为原始数组，从最低位开始取每个位组成radix数组；
 *                  3. 对radix进行计数排序（利用计数排序适用于小范围数的特点）；
 *
 *             算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n*k)   最好 O(n*k)     最坏O(n*k)
 *                  空间复杂度：
 *                          O(n)
 *                  稳定性：
 *                          稳定
 *
 *              分析：
 *                  基数排序基于分别排序，分别收集，所以是稳定的。
 *                  但基数排序的性能比桶排序要略差，每一次关键字的桶分配都需要O(n)的时间复杂度，
 *                  而且分配之后得到新的关键字序列又需要O(n)的时间复杂度。
 *
 *                  假如待排数据可以分为d个关键字，则基数排序的时间复杂度将是O(d*2n) ，
 *                  当然d要远远小于n，因此基本上还是线性级别的。
 *
 *                  基数排序的空间复杂度为O(n+k)，其中k为桶的数量。
 *                  一般来说n>>k，因此额外空间需要大概n个左右。
 *
 * @author: ZSZ
 * @date: 2020/4/28 17:23
 */
public class RadixSort extends BaseSort implements SortedCompared{

    //这里仅对整数进行排序，int最多为10位
    private static final int[] RADIX_DICT = {1,10,100,1000,10000,100000,1000000,10000000,100000000,1000000000};

    /**
     * 基数排序入口函数
     * @param a 待排数组
     */
    public static void radixSort1(int[] a){
        if(a == null) return;
        int max = getMax(a);
        radixSort1(a,getLength(max),0,a.length-1);
    }

    /**
     *
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     */
    public static void radixSort1(int[] a,int max,int l,int r){
        if(a == null) return;
        if(l >= r) return;

        int length = a.length;
        int[] count = new int[10];
        int[] bucket = new int[length];

        for(int k=1;k<=max;k++){

            //将count计数数组，置零
            for(int i=0;i<10;i++) count[i] = 0;

            //计算数组在第k位，数值分别为0,1,2,3,4,5,6,7,8,9的个数
            for(int i=0;i<length;i++) count[getIndexNum(a[i],k)]++;

            //bucket中数值分别为0,1,2,3,4,5,6,7,8,9的右边界
            for(int i=1;i<10;i++) count[i] += count[i-1];

            //把数值放入bucket中,注意，从后往前遍历
            for(int i=length-1;i>=0;i--){
                int j = getIndexNum(a[i],k);
                bucket[count[j]-1] = a[i];
                count[j]--;
            }

            //把值赋给原数组a
            for(int i=0;i<length;i++) a[i] = bucket[i];
        }
    }

    /**
     * 基数排序入口函数
     * @param a 待排数组
     */
    public static void radixSort2(int[] a){
        if(a == null) return;
        int max = getMax(a);
        radixSort2(a,getLength(max),0,a.length-1);
    }

    /**
     *
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     */
    public static void radixSort2(int[] a,int max,int l,int r){
        if(a == null) return;
        if(l >= r) return;

        int length = a.length;
        List<List<Integer>> buckets = new ArrayList<>();
        //构建10个bucket，使用ArrayList作为桶的数据结构
        for(int i=0;i<10;i++)buckets.add(new ArrayList<>());

        for(int k=1;k<=max;k++){

            //清除桶里的元素，避免干扰，设置新的桶
            for(int i=0;i<10;i++)buckets.set(i,new ArrayList<>());

            //把数值放入bucket中
            for(int i=0;i<length;i++){
                int j = getIndexNum(a[i],k);
                buckets.get(j).add(a[i]);
            }

            //把桶的数据，赋给原数组a
            int index = 0;
            for (List<Integer> bucket:buckets) {
                for (Integer value:bucket) {
                    a[index] = value;
                    index++;
                }
            }
        }
    }


    /**
     * 返回一个数的第index位的值
     * @param num
     * @param index
     * @return
     */
    private static int getIndexNum(int num,int index){
        return (num/RADIX_DICT[index-1])%10;
    }

    /**
     * 返回最大值
     * @param a
     * @return
     */
    private static int getMax(int[] a){
        int max = a[0];
        for(int i=1;i<a.length;i++){
            if(a[i] > max) max = a[i];
        }
        return max;
    }

    /**
     * 返回一个数的位数
     * @param num
     * @return
     */
    private static int getLength(int num){
        int i = 1;
        while((num/=10)>0) i++;
        return i;
    }

    /**
     * 测试规模： 100000000 一亿
     *
     * 基数排序适合数值位数少的排序
     *
     * 测试结果：
     * Array created!
     * radixSort1 method[random]:(6.47 seconds)
     * radixSort1 method[random+duplicate]:(8.61 seconds)
     *
     * radixSort2 method[random]:(91.21 seconds)
     * radixSort2 method[random+duplicate]:(99.11 seconds)
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        int[] a11 = RandomArrayUtil.getRandomIntArray(0, 1000000, 100000000);
        int[] a12 = RandomArrayUtil.getRandomIntArray(0, 1000000, 100000000);

        // 大量重复数组
        int[] a21 = RandomArrayUtil.getRandomIntArray(0, 100, 100000000);
        int[] a22 = RandomArrayUtil.getRandomIntArray(0, 100, 100000000);
        System.out.println("Array created!");

        StopWatch stopWatch = new StopWatch();
        radixSort1(a11);
        if(isSorted(a11))System.out.println(String.format(formatStringWithRandom, "radixSort1", stopWatch.elapsedTime()));
        radixSort1(a21);
        if(isSorted(a21))System.out.println(String.format(formatStringWithDuplicate, "radixSort1", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        radixSort2(a12);
        if(isSorted(a12))System.out.println(String.format(formatStringWithRandom, "radixSort2", stopWatch.elapsedTime()));
        radixSort2(a22);
        if(isSorted(a22))System.out.println(String.format(formatStringWithDuplicate, "radixSort2", stopWatch.elapsedTime()));
    }
}
