package sort;

import javax.swing.text.html.HTMLDocument;
import java.util.*;

/**
 * @className: BucketSort
 * @description: 桶排序
 *
 *              桶排序是计数排序的升级版, 它利用了函数的映射关系，
 *              高效与否的关键就在于: 这个映射函数的确定
 *
 *              桶排序 (Bucket sort)的工作原理:
 *                  假设输入数据服从均匀分布，将数据分到有限数量的桶里，
 *                  每个桶再分别排序（有可能再使用别的排序算法或是以递归方式继续使用桶排序进行排）
 *
 *             算法描述
 *                  1. 设置一个定量的数组当作空桶；
 *                  2. 遍历输入数据，并且把数据一个一个放到对应的桶里去；
 *                  3. 对每个不是空的桶进行排序；
 *                  4. 从不是空的桶里把排好序的数据拼接起来
 *
 *             算法复杂度分析：
 *                  时间复杂度：
 *                          平均 O(n+k)   最好 O(n)     最坏O(n^2)
 *                  空间复杂度：
 *                          O(n)
 *                  稳定性：
 *                          稳定
 *
 *              评价:
 *                  桶排序最好情况下使用线性时间O(n)
 *
 *                  桶排序的时间复杂度取决于对各个桶之间数据进行排序的时间复杂度,
 *                  因为其它部分的时间复杂度都为O(n)
 *
 *                  很显然，桶划分的越小，各个桶之间的数据越少，排序所用的时间也会越少。
 *                  但相应的空间消耗就会增大
 *
 *                  最坏时间: O(N^2) 所有的数据都放入了一个桶内，桶内自排序算法为插入排序
 *
 *                  最好时间: O(N) 桶的数量越多，理论上分到每个桶中的元素就越少，
 *                  桶内数据的排序就越简单，其时间复杂度就越接近于线性
 *
 *                  极端情况下，区间小到只有1，即桶内只存放一种元素
 *                  此时桶内的元素不再需要排序，因为它们都是相同的元素，这时桶排序差不多就和计数排序一样了
 *
 *
 * @author: ZSZ
 * @date: 2020/4/26 23:05
 */
public class BucketSort extends BaseSort{

    /** * 设置桶的默认数量为5 */
    private static final int DEFAULT_BUCKET_SIZE = 5;

    /**
     * 这里仅仅作为演示, 排序要求:
     * 输入的桶大小bucketSize, 应当大于待排序浮点数的整数部分, 且浮点数均大于零才行!
     * (因为在getBucketIndex方法中仅仅取了浮点数的整数部分作为桶的index)
     * @param arr 待排序数组
     * @param bucketSize 桶大小(在本例中为浮点数整数部分最大值+1)
     */
     public static void sort(double[] arr, int bucketSize) {

         if(arr==null)return ;
         int length = arr.length;
         if(length<2) return;

         List<LinkedList<Double>> buckets = new ArrayList<>();
         bucketSize = Math.max(bucketSize,DEFAULT_BUCKET_SIZE);

         //新建桶
         for(int i=0;i<bucketSize;i++){
             //选用链表作为桶的数据结构
             buckets.add(new LinkedList<>());
         }

         //把数组元素放入桶中
         for(int i=0;i<length;i++){
             //选择桶
             int index = getBucketIndex(arr[i]);
             //往桶中插入元素
             insertSort(buckets.get(index),arr[i]);
         }

         //遍历桶，把数据输出到原数组
         int index = 0;
         for(List<Double> bucket:buckets){
             for (Double data:bucket) {
                 arr[index++] = data;
             }
         }
     }


    /**
     * 计算应该在哪一个桶内
     * @param data
     * @return
     */
     private static int getBucketIndex(double data){
         // 这里例子写的比较简单，仅使用浮点数的整数部分作为其桶的索引值
         // 实际开发中需要根据场景具体设计
         return (int)data;
     }

    /**
     * 选用插入排序作为桶内元素排序方法
     * @param bucket 桶
     * @param data 待插入数据
     */
     private static void insertSort(List<Double> bucket, double data){
         boolean isInsert = false;
         ListIterator iterator = bucket.listIterator();
         while(iterator.hasNext()){
             if(data <= (double)iterator.next()){
                 //游标往前移，指向前一个元素
                 iterator.previous();
                 //插入数据
                 iterator.add(data);
                 isInsert = true;
                 break;
             }
         }
         //如果之前没有插入数据，表明该元素最大，直接插入末尾
         if(!isInsert) bucket.add(data);
     }

}
