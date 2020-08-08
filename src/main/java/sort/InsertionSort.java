package sort;

import util.BinarySearch;
import util.RandomArrayUtil;
import util.StopWatch;

import java.util.Arrays;

/**
 * @className: InsertionSort
 * @description: 插入排序（Insertion-Sort）的算法描述是一种简单直观的排序算法。
 *               它的工作原理是通过构建有序序列，对于未排序数据，在已排序序列中从后向前扫描，找到相应位置并插入。
 *
 *
 *              算法描述:
 *                  一般来说，插入排序都采用in-place在数组上实现。具体算法描述如下：
 *
 *                  1. 从第一个元素开始，该元素可以认为已经被排序；
 *                  2. 取出下一个元素，在已经排序的元素序列中从后向前扫描；
 *                  3. 如果该元素（已排序）大于新元素，将该元素移到下一位置；
 *                  4. 重复步骤3，直到找到已排序的元素小于或者等于新元素的位置；
 *                  5. 将新元素插入到该位置后；
 *                  6. 重复步骤2~5。
 *
 *             算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n^2)   最好 O(n)     最坏O(n^2)
 *                  空间复杂度：
 *                          O(1)
 *                  稳定性：
 *                          稳定
 *
 *             插入排序在实现上，通常采用in-place排序（即只需用到O(1)的额外空间的排序），
 *             因而在从后向前扫描过程中，需要反复把已排序元素逐步向后挪位，为最新元素提供插入空间。
 *
 * @author: ZSZ
 * @date: 2020/4/5 23:52
 */
public class InsertionSort extends BaseSort implements SortedCompared{

    /**
     * 标准插入排序
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void insertionSort(K[] a){
        if(a==null)return ;
        int length = a.length;
        if(length==0)return ;

        for(int i=1;i<length;i++){
            K tmp = a[i];
            int j;
            for (j = i; j > 0; j--) {
                if(less(tmp,a[j-1]))a[j]=a[j-1];
                else break;
            }
            a[j] = tmp;
        }
    }

    /**
     * 二分查找+插入排序
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void sortWithBinarySearch(K[] a){
        if(a==null)return ;
        int length = a.length;
        if(length==0)return ;

        for(int i=1;i<length;i++){
            K tmp = a[i];
            int index = getInsertIndex(a,0,i,tmp);
            int j;
            for (j = i; j > index; j--) {
                a[j]=a[j-1];
            }
            a[j] = tmp;
        }
    }

    /**
     * 预处理+折半插入
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void sortWithBinarySearchAndPre(K[] a){
        pretreatment(a);
        sortWithBinarySearch(a);
    }

    /**
     * 二路插入排序算法是在折半插入的基础上进行改进
     * 折半插入在原先直接插入的基础上改进，通过折半查找，以较少的比较次数就找到了要插入的位置
     * 但是在插入的过程中仍然没有减少移动次数，所以2路插入在此基础上改进，减少了移动次数，
     * 但是仍然并没有避免移动记录（如果要避免的话还是得改变存储结构）
     *
     * 因此我们设定一个辅助数组b，大小是原来数组相同的大小
     * 将b[0]设为第一个原数组第一个数，通过设置head和tail指向整个有序序列的最小值和最大值
     * 即为序列的尾部和头部，并且将其设置位一个循环数组，
     * 这样就可以进行双端插入 (之所以能减少移动次数的原因在于可以往2个方向移动记录，故称为2路插入)
     *
     * 具体操作思路：
     *      1. 将原数组第一个元素赋值给b[0],作为标志元素
     *      2. 按顺序依次插入剩下的原数组的元素
     *          (1). 将带插入元素与第一个进行比较，偌大于b[0],则插入b[0]前面的有序序列，否则插入后面的有序序列
     *          (2). 对前面的有序序列或后面的有序序列进行折半查找
     *          (3). 查找到插入位置后进行记录的移动，分别往head方向前移和往tail方向移动
     *          (4). 插入记录
     *      3. 将排序好的b数组的数据从head到tail，按次序赋值回原数组
     *
     * @param a
     * @param <K>
     */
//    @SuppressWarnings("unchecked")
    public static <K extends Comparable<K>> void twoPathInsertSort(K[] a) {
        int len = a.length;
        K[] b = (K[]) new Comparable[len];
        b[0] = a[0];
        // 分别记录temp数组中最大值和最小值的位置
        int i, first, tail, k;
        first = tail = 0;
        for (i = 1; i < len; i++) {
            // 待插入元素比最小的元素小
            if (less(a[i], b[first])) {
                first = (first - 1 + len) % len;
                b[first] = a[i];
            }
            // 待插入元素比最大元素大
            else if (less(b[tail], a[i])) {
                tail = (tail + 1 + len) % len;
                b[tail] = a[i];
            }
            // 插入元素 >= 最小，<= 最大
            else {
                k = (tail + 1 + len) % len;
                // 当插入值比当前值小时，需要移动当前值的位置
                while (less(a[i], b[((k - 1) + len) % len])) {
                    b[(k + len) % len] = b[(k - 1 + len) % len];
                    k = (k - 1 + len) % len;
                }
                // 插入该值
                b[(k + len) % len] = a[i];
                // 因为最大值的位置改变，所以需要实时更新tail的位置
                tail = (tail + 1 + len) % len;
            }
        }
        // 将排序记录复制到原来的顺序表里
        for (k = 0; k < len; k++) {
            a[k] = b[(first + k) % len];
        }
    }


    /**
     * 测试数据规模： 50000
     *
     * 测试结果：
     * insertionSort method[random]:(4.54 seconds)
     * insertionSort method[random+duplicate]:(7.14 seconds)
     *
     * sortWithBinarySearch method[random]:(3.11 seconds)
     * sortWithBinarySearch method[random+duplicate]:(5.03 seconds)
     *
     * sortWithBinarySearchAndPre method[random]:(3.40 seconds)
     * sortWithBinarySearchAndPre method[random+duplicate]:(5.22 seconds)
     *
     * twoPathInsertSort method[random]:(7.88 seconds)
     * twoPathInsertSort method[random+duplicate]:(15.09 seconds)
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, 50000);
        Integer[] a12 = Arrays.copyOf(a11, a11.length);
        Integer[] a13 = Arrays.copyOf(a11, a11.length);
        Integer[] a14 = Arrays.copyOf(a11, a11.length);

        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 100, 50000);
        Integer[] a22 = Arrays.copyOf(a21, a21.length);
        Integer[] a23 = Arrays.copyOf(a21, a21.length);
        Integer[] a24 = Arrays.copyOf(a21, a21.length);

        System.out.println("Array created!");

        StopWatch stopWatch = new StopWatch();
        insertionSort(a11);
        if(isSorted(a11))System.out.println(String.format(formatStringWithRandom, "insertionSort", stopWatch.elapsedTime()));
        insertionSort(a21);
        if(isSorted(a21))System.out.println(String.format(formatStringWithDuplicate, "insertionSort", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        sortWithBinarySearch(a12);
        if(isSorted(a12))System.out.println(String.format(formatStringWithRandom, "sortWithBinarySearch", stopWatch.elapsedTime()));
        sortWithBinarySearch(a22);
        if(isSorted(a22))System.out.println(String.format(formatStringWithDuplicate, "sortWithBinarySearch", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        sortWithBinarySearchAndPre(a13);
        if(isSorted(a13))System.out.println(String.format(formatStringWithRandom, "sortWithBinarySearchAndPre", stopWatch.elapsedTime()));
        sortWithBinarySearchAndPre(a23);
        if(isSorted(a23))System.out.println(String.format(formatStringWithDuplicate, "sortWithBinarySearchAndPre", stopWatch.elapsedTime()));
        System.out.println();

        stopWatch = new StopWatch();
        twoPathInsertSort(a14);
        if(isSorted(a14))System.out.println(String.format(formatStringWithRandom, "twoPathInsertSort", stopWatch.elapsedTime()));
        twoPathInsertSort(a24);
        if(isSorted(a24))System.out.println(String.format(formatStringWithDuplicate, "twoPathInsertSort", stopWatch.elapsedTime()));
        System.out.println();

    }

    /**
     * 使用二分查找，查找待插入位置
     * @param a
     * @param l
     * @param r
     * @param target
     * @param <K>
     * @return
     */
    private static <K extends Comparable<K>> int getInsertIndex(K[] a,int l,int r,K target){
        return BinarySearch.findRightBound(a,l,r,target)+1;
    }
}
