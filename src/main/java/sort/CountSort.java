package sort;

import util.RandomArrayUtil;
import util.StopWatch;

/**
 * @className: CountSort
 * @description: 计数排序 非比较类排序
 *
 *              计数排序不是基于比较的排序算法
 *              其核心在于将输入的数据值转化为键存储在额外开辟的数组空间中
 *              作为一种线性时间复杂度的排序
 *              计数排序要求: 输入的数据必须是有确定范围的整数
 *
 *              算法步骤:
 *                  1. 找出待排序的数组中最大和最小的元素；
 *                  2. 统计数组中每个值为i的元素出现的次数，存入数组C的第i项；
 *                  3. 对所有的计数累加（从C中的第一个元素开始，每一项和前一项相加）;
 *                  4. 反向填充目标数组：将每个元素i放在新数组的第C(i)项，每放一个元素就将C(i)减去1
 *
 *             算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n+k)   最好 O(n+k)     最坏O(n+k)
 *                  空间复杂度：
 *                          O(n+k)
 *                  稳定性：
 *                          稳定
 *
 *              评价:
 *                  计数排序是一个稳定的排序算法
 *                  当输入的元素是n个0到k之间的整数时: 时间复杂度是O(n+k),空间复杂度也是O(n+k),其排序速度快于任何比较排序算法
 *                  当k不是很大并且序列比较集中时，计数排序是一个很有效的排序算法
 *
 * @author: ZSZ
 * @date: 2020/4/24 17:54
 */
public class CountSort extends BaseSort implements SortedCompared{

    public static void countSort(int[] a){
        if(a==null)return ;
        int length = a.length;
        if(length<2)return;

        countSort(a,0,length-1);
    }

    public static void countSort(int[] a,int l,int r){
        if(l >= r)return;

        //第一遍：
        //找到最大最小值，确定范围
        int min_value=a[0], max_value=a[0];
        for(int i=1;i<a.length;i++){
            if(a[i] > max_value) max_value = a[i];
            else if(a[i] < min_value) min_value = a[i];
        }

        //新建数组，[min_value,max_value],length: max_value - min_value + 1
        int[] aux = new int[max_value - min_value + 1];

        //第二次遍历：
//        for(int i=0;i<a.length;i++){
//            aux[a[i]-min_value] += 1;
//        }
        for (int i:a) {
            aux[i-min_value] += 1;
        }

        //将计数结果输入到原数组
        int index=0;
        for(int i=0;i<aux.length;i++){
            for(int j=0;j<aux[i];j++){
                a[index++] = i+min_value;
            }
        }
    }

    /**
     *
     * 测试规模：100000000 一亿
     *
     * 计数排序是一个稳定的排序算法
     * 当输入的元素是n个0到k之间的整数时: 时间复杂度是O(n+k),空间复杂度也是O(n+k),其排序速度快于任何比较排序算法
     * 当k不是很大并且序列比较集中时，计数排序是一个很有效的排序算法
     *
     * 测试结果：
     * countSort method[random]:(0.53 seconds)
     * countSort method[random+duplicate]:(0.86 seconds)
     *
     */
    @Override
    public void sortingComparison() {
        // 正常随机数组
        int[] a11 = RandomArrayUtil.getRandomIntArray(0, 1000000, 100000000);

        // 大量重复数组
        int[] a21 = RandomArrayUtil.getRandomIntArray(0, 100, 100000000);

        System.out.println("Array created!");

        StopWatch stopWatch = new StopWatch();
        countSort(a11);
        if(isSorted(a11))System.out.println(String.format(formatStringWithRandom, "countSort", stopWatch.elapsedTime()));
        countSort(a21);
        if(isSorted(a21))System.out.println(String.format(formatStringWithDuplicate, "countSort", stopWatch.elapsedTime()));
        System.out.println();
    }
}
