package sort;

import util.RandomArrayUtil;
import util.StopWatch;

import java.util.Arrays;

/**
 * @className: QuickSort
 * @description: 快速排序
 *
 *              快速排序的基本思想：
 *                  通过一趟排序将待排记录分隔成独立的两部分，其中一部分记录的关键字均比另一部分的关键字小，
 *                  则可分别对这两部分记录继续进行排序，以达到整个序列有序。
 *
 *              算法描述
 *                  快速排序使用分治法来把一个串（list）分为两个子串（sub-lists）。
 *
 *                  具体算法描述如下：
 *                      1. 从数列中挑出一个元素，称为 “基准”（pivot）；
 *                      2. 重新排序数列，所有元素比基准值小的摆放在基准前面，
 *                         所有元素比基准值大的摆在基准的后面（相同的数可以到任一边）。
 *                         在这个分区退出之后，该基准就处于数列的中间位置。
 *                         这个称为分区（partition）操作；
 *                      3. 递归地（recursive）把小于基准值元素的子数列和大于基准值元素的子数列排序。
 *
 *              算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n*log(n))   最好 O(n*log(n))     最坏O(n^2)
 *
 *                  空间复杂度：
 *                          O(log(n))
 *                  稳定性：
 *                          稳定
 *
 * @author: ZSZ
 * @date: 2020/4/17 19:31
 */
public class QuickSort extends BaseSort implements SortedCompared{

    //使用插排的阀值
    private static final int INSERTION_SORT_THRESHOLD = 8;

    /**
     * 标准快排 入口方法
     * @param a 待排数组
     * @param <K> 数组泛型
     */
    public static <K extends Comparable<K>> void quickSort1(K[] a){
        if(a==null)return ;
        quickSort1(a,0,a.length-1);
    }

    /**
     * 快排-挖坑法实现（左右互换） 入口方法
     * @param a 待排数组
     * @param <K> 数组泛型
     */
    public static <K extends Comparable<K>> void quickSort2(K[] a){
        if(a==null)return ;
        quickSort2(a,0,a.length-1);
    }

//---------------------------------标准快排  实现----------------------------------------------

    /**
     * 标准快排 实现 排序区间[l,r]
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> void quickSort1(K[] a,int l,int r){
        if(a.length ==0 || l>=r)return;

        //获取分区下标
        int partitionIndex = partition1(a,l,r);

        //注意排序区间是[l,partitionIndex-1] 和 [partitionIndex+1,r]
        quickSort1(a,l,partitionIndex-1);
        quickSort1(a,partitionIndex+1,r);

    }

    /**
     * 标准快排的切分算法 实现 切分区间[l,r]
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    private static <K extends Comparable<K>> int partition1(K[] a,int l,int r){
//       //这样写有问题，用为定位到的i和j是 +1，和-1之后的
//        int i = l+1, j = r;

//        //基准为l
//        K tmp = a[l];
//
//        while(true){
//            //左右移动，跳过有序的位置
//            //定位到 大于tmp的下标
//            while(less(a[i++],tmp)) if(i==r) break;
//            //定位到 小于tmp的下标
//            while(less(tmp,a[j--])) if(j==l) break;
//            if(i>=j)break;
//            //交换下标为i和j的元素
//            swap(a,i,j);
//        }
//        swap(a,l,j);

        //以l 和r+1 作为开始下标，避免上面情况的发生
        int i = l, j = r+1;
        //基准为l
        K tmp = a[l];

        while(true){
            //左右移动，跳过有序的位置
            //定位到 大于tmp的下标
            //先做自增操作，如果是i++，那么交换的下标不是比较的时候的下标
            while(less(a[++i],tmp)) if(i==r) break;
            //定位到 小于tmp的下标
            //先做自增操作，如果是j--，那么交换的下标不是比较的时候的下标
            while(less(tmp,a[--j])) if(j==l) break;
            if(i>=j)break;
            //交换下标为i和j的元素
            swap(a,i,j);
        }

        //此时的a[j] <= a[l]
        swap(a,l,j);

        return j;
    }

//---------------------------快排-挖坑法（左右互换） 实现----------------------------------------------


    /**
     * 快排-挖坑法（左右互换） 实现  排序区间[l,r]
     *
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> void quickSort2(K[] a,int l,int r){
        if(a.length ==0 || l>=r)return;

        //获取分区下标
        int partitionIndex = partition2(a,l,r);

        //注意排序区间是[l,partitionIndex-1] 和 [partitionIndex+1,r]
        quickSort2(a,l,partitionIndex-1);
        quickSort2(a,partitionIndex+1,r);
    }

    /**
     * 快排的切分算法（"挖坑法"-左右互换） 实现 切分区间[l,r]
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    private static <K extends Comparable<K>> int partition2(K[] a,int l,int r){

        int i=l,j=r;
        //基准
        K tmp = a[l];

        while(i<j){
            //相等需要移动，不然可能出现死循环
            // tmp <= a[j] <=> !(tmp > a[j]) <=> !less(a[j],tmp)
            while(i<j && !less(a[j],tmp))j--;
            a[i] = a[j];
            //相等需要移动，不然可能出现死循环
            // tmp >= a[i] <=> !(tmp < a[i]) <=> !less(tmp,a[i])
            while(i<j && !less(tmp,a[i]))i++;
            a[j] = a[i];
        }
        //此时 i==j
        a[j] = tmp;
        return j;
    }

//-------------------------------------快排 优化----------------------------------------------

    /**
     * 快排 优化
     *
     * 针对排序基准轴优化
     *      针对基本快排进行优化的思路主要有以下几个:
     *          1. 在排序之前进行打乱操作(上述代码已经使用)
     *          2. 小数组切换到插入排序: 通常THRESHOLD选择5~15均可
     *          3. 三取样切分, 选择子数组一小部分的中位数来切分(保证尽量均匀)
     *
     * 性能优化原因:
     *          1. 对小规模子数组采用插入排序: 避免了对小规模的数组进行递归而产生过多的小数组甚至是空数组调用栈
     *          2. 三取样切分, 选择子数组一小部分的中位数来切分(保证尽量切分均匀)
     *
     */

    /**
     * 标准快排 优化
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void advanceQuickSort1(K[] a){
        if(a == null) return;
        advanceQuickSort1(a,0,a.length-1);
    }

    /**
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> void advanceQuickSort1(K[] a,int l, int r){
        if(a.length < 2 || l >= r) return;

        //长度小于8，使用插排
        if(r-l < INSERTION_SORT_THRESHOLD){
            insertSort(a,l,r);
            return;
        }

        int partitionIndex = advancePartition1(a,l,r);

        advanceQuickSort1(a,l,partitionIndex-1);
        advanceQuickSort1(a,partitionIndex+1,r);

    }

    /**
     * 标准快排的切分算法 实现 切分区间[l,r] 优化版
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> int advancePartition1(K[] a,int l, int r){

        int i=l, j= r+1, m = median(a, l, l+((r-l)>>1), r);
        swap(a,l,m);

        K tmp = a[l];
        //当a[l]为 数组区间[j,r]的最大元素时
        while(less(a[++i],tmp)){
            if(i==r){
                swap(a,l,r);
                return r;
            }
        }

        //当a[l]为 数组区间[j,r]的最小元素时
        while(less(tmp,a[--j])){
            if(j==l) return l;
        }

        //另外的情况
        while(i<j){
            swap(a,i,j);
            while(less(a[++i],tmp));
            while(less(tmp,a[--j]));
        }

        swap(a,l,j);

        return j;
    }

    /**
     * 快排（挖坑法-左右互换） 优化
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void advanceQuickSort2(K[] a){
        if(a==null) return ;
        advanceQuickSort2(a,0,a.length-1);
    }

    /**
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> void advanceQuickSort2(K[] a,int l, int r){
        if(a.length < 2 || l >= r) return;

        //长度小于8，使用插排
        if(r-l < INSERTION_SORT_THRESHOLD){
            insertSort(a,l,r);
            return;
        }

        int m = median(a, l, l+((r-l)>>1), r);
        swap(a,l,m);

        int partitionIndex = partition2(a,l,r);

        advanceQuickSort2(a,l,partitionIndex-1);
        advanceQuickSort2(a,partitionIndex+1,r);
    }

//-------------------------------------三向切分 快排 实现----------------------------------------------


    /**
     * 三向切分的快排 入口函数
     * @param a
     * @param <K>
     */
    public static <K extends Comparable<K>> void threeWaySort(K[] a){
        if(a==null)return;
        threeWaySort(a,0,a.length-1);
    }

    /**
     * 三向切分的快排(信息量最优的快速排序):
     *      适用于大量重复元素的排序
     *
     *      1. 从左至右遍历数组一次:
     *          a. 指针lt使得a[l…i-1]中的元素都小于key
     *          b. 指针gt使得a[j+1…r]中的元素都大于key
     *          c. 指针i使得a[i…k-1]中的元素都等于key
     *          d. 而a[i…gt]中的元素为未确定
     *      2. 处理时一开始i和lo相等, 进行三向比较:
     *          a. a[k] < key: 将a[i]和a[k]交换, 将i和k加一;
     *          b. a[k] > key: 将a[j]和a[k]交换, 将j减一;
     *          c. a[k] = key: 将k加一;
     *
     *     上述操作均会保证数组元素不变并且缩小j-k的值(这样循环才会结束)
     *
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    public static <K extends Comparable<K>> void threeWaySort(K[] a,int l, int r){
        if(a.length<2 || r<=l)return;
        if(r-l<INSERTION_SORT_THRESHOLD){
            insertSort(a,l,r);
            return;
        }

        int m = median(a,l,l+((r-l)>>1),r);
        swap(a,l,m);

        int i=l,j=r,k=l+1;
        K tmp = a[l];

        while(k<=j){
            int cmp = a[k].compareTo(tmp);
            //这里k++，是因为从下标为i，交换到下标为k的元素a[i] 等于 tmp，可以跳过
            // if(cmp < 0)swap(a,i++,k)也是可以的，会多做一次比较
            if(cmp < 0)swap(a,i++,k++);
            //这里k++，是因为从下标为j，交换到下标为k的元素a[j] 可能大于/小于/等于 tmp，不可以跳过
            else if(cmp > 0)swap(a,j--,k);
            else k++;
        }

        threeWaySort(a,l,i-1);
        threeWaySort(a,j+1,r);
    }


    /**
     * 获取 a[l],a[mid],a[r]的中位数
     * @param a
     * @param l
     * @param mid
     * @param r
     * @param <K>
     * @return
     */
    public static <K extends Comparable<K>> int median(K[] a,int l,int mid,int r){
        return less(a[l],a[r]) ?
                (less(a[l],a[mid]) ? (less(a[mid],a[r]) ? mid : r) : l):
                (less(a[r],a[mid]) ? (less(a[mid],a[l]) ? mid : l) : r);
    }

//-------------------------------------插排 实现----------------------------------------------

    /**
     * 对于小规模数据，使用插入排序
     * @param a 待排数组
     * @param l 左边界
     * @param r 右边界
     * @param <K> 泛型类型
     */
    private static <K extends Comparable<K>> void insertSort(K[] a,int l,int r){
        for(int i=l+1; i <= r;i++){
            K tmp = a[i];
            int j=i;
            for(; j > l && less(tmp,a[j-1]);j--) a[j] = a[j-1];
            a[j] = tmp;
        }
    }


    /**
     * 测试规模：5000000 五百万
     *
     * advancedSort性能最优(因为优化的最好)
     * threeWaySort处理重复数组的能力与advancedSort几乎相同, 且优于普通的快排
     * 对于sort2系列, 由于采用的是挖坑法, 且没有对重复子数组进行特殊处理, 所以很容易陷入N^2复杂度!
     *
     *
     * 测试结果：
     * quickSort1 method[random]:(2.23 seconds)
     * quickSort1 method[random+duplicate]:(4.06 seconds)
     *
     * quickSort2 挖坑版 method[random]:(2.24 seconds)
     * quickSort2 挖坑版 method[random+duplicate]:(79.89 seconds)
     *
     * advanceQuickSort1 method[random]:(1.87 seconds)
     * advanceQuickSort1 method[random+duplicate]:(3.54 seconds)
     *
     * advanceQuickSort2 挖坑版 method[random]:(2.05 seconds)
     * advanceQuickSort2 挖坑版 method[random+duplicate]:(88.53 seconds)
     *
     * threeWaySort method[random]:(4.08 seconds)
     * threeWaySort method[random+duplicate]:(6.16 seconds)
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 10000000, 5000000);
        Integer[] a12 = Arrays.copyOf(a11, a11.length);
        Integer[] a13 = Arrays.copyOf(a11, a11.length);
        Integer[] a14 = Arrays.copyOf(a11, a11.length);
        Integer[] a15 = Arrays.copyOf(a11, a11.length);

        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000, 5000000);
        Integer[] a22 = Arrays.copyOf(a21, a21.length);
        Integer[] a23 = Arrays.copyOf(a21, a21.length);
        Integer[] a24 = Arrays.copyOf(a21, a21.length);
        Integer[] a25 = Arrays.copyOf(a21, a21.length);

        System.out.println("Array created!");

        // quickSort1
        StopWatch stopWatch = new StopWatch();
        quickSort1(a11);
        if(isSorted(a11))System.out.println(String.format(formatStringWithRandom, "quickSort1", stopWatch.elapsedTime()));
        quickSort1(a21);
        if(isSorted(a21))System.out.println(String.format(formatStringWithDuplicate, "quickSort1", stopWatch.elapsedTime()));
        System.out.println();

        // quickSort2 挖坑版
        stopWatch = new StopWatch();
        quickSort2(a12);
        if(isSorted(a12))System.out.println(String.format(formatStringWithRandom, "quickSort2 挖坑版", stopWatch.elapsedTime()));
        quickSort2(a22);
        if(isSorted(a22))System.out.println(String.format(formatStringWithDuplicate, "quickSort2 挖坑版", stopWatch.elapsedTime()));
        System.out.println();

        // 优化 advanceQuickSort1
        stopWatch = new StopWatch();
        advanceQuickSort1(a13);
        if(isSorted(a13))System.out.println(String.format(formatStringWithRandom, "advanceQuickSort1", stopWatch.elapsedTime()));
        advanceQuickSort1(a23);
        if(isSorted(a23))System.out.println(String.format(formatStringWithDuplicate, "advanceQuickSort1", stopWatch.elapsedTime()));
        System.out.println();

        // 优化 advanceQuickSort2 挖坑版
        stopWatch = new StopWatch();
        advanceQuickSort2(a14);
        if(isSorted(a14))System.out.println(String.format(formatStringWithRandom, "advanceQuickSort2 挖坑版", stopWatch.elapsedTime()));
        advanceQuickSort2(a24);
        if(isSorted(a24))System.out.println(String.format(formatStringWithDuplicate, "advanceQuickSort2 挖坑版", stopWatch.elapsedTime()));
        System.out.println();

        // 优化 threeWaySort
        stopWatch = new StopWatch();
        threeWaySort(a15);
        if(isSorted(a15))System.out.println(String.format(formatStringWithRandom, "threeWaySort", stopWatch.elapsedTime()));
        threeWaySort(a25);
        if(isSorted(a25))System.out.println(String.format(formatStringWithDuplicate, "threeWaySort", stopWatch.elapsedTime()));
        System.out.println();
    }
}
