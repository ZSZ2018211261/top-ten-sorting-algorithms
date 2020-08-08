package sort;

import util.RandomArrayUtil;
import util.StopWatch;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Comparator;

/**
 * @className: MyTimSort
 * @description: 自己实现的TimSort算法，借鉴参考了java11的TimSort源码
 * @author: ZSZ
 * @date: 2020/5/13 21:43
 */
public class MyTimSort<T> extends BaseSort{

    private static final String formatStringWithRandom = "%s method[random]:(%.2f seconds)";

    private static final String formatStringWithDuplicate = "%s method[random+duplicate]:(%.2f seconds)";

    //待排数组
    private final T[] a;

    //比较器
    private final Comparator<? super T> c;

    //使用TimSort算法的最小元素个数
    //如果数组元素少于 32 个，则使用折半插入排序
    private static final int MIN_MERGE = 32;

    //临时数组tmp
    private T[] tmp;
    private int tmpBase;                //tmp切片的基准
    private int tmpLen;                 //tmp切片的长度

    //临时数组tmp的初始化长度
    private static final int INITIAL_TMP_STORAGE_LENGTH = 256;

    //模拟栈
    private int stackSize = 0;          //栈中元素的数量
    private final int[] runBase;
    private final int[] runLen;

    //判断数据顺序连续性的阈值,合并时如果 run1中的元素连续性的小于run2，
    // 则表明run1之后的数据，大部分都小于run2
    private static final int  MIN_GALLOP = 7;

    private int minGallop = MIN_GALLOP;


    public MyTimSort(T[] a, Comparator<? super T> c){
        this.a = a;
        this.c = c;

        int len = a.length;
        //tlen <= INITIAL_TMP_STORAGE_LENGTH
        int tlen = len < 2*INITIAL_TMP_STORAGE_LENGTH ? len >>> 1 : INITIAL_TMP_STORAGE_LENGTH;
        //  0 < len/2 <= tlen <= INITIAL_TMP_STORAGE_LENGTH
        assert tlen <= INITIAL_TMP_STORAGE_LENGTH;

        T[] newArray = (T[]) Array.newInstance(a.getClass().getComponentType(),tlen);

        tmp = newArray;
        tmpBase = 0;
        tmpLen = tlen;

        //初始化栈的大小
        //这部分为源码
        int stackLen = (len <    120  ?  5 :
                        len <   1542  ? 10 :
                        len < 119151  ? 24 : 49);
        runBase = new int[stackLen];
        runLen = new int[stackLen];

    }

    /**
     * 排序区间 [lo,hi）
     *
     * 大致思路：
     *      1. 计算nRemaining(剩余待排数组的长度)
     *      2. 如果nRemaining < MIN_MERGE(32),使用折半插入排序后返回
     *      3. 如果nRemaining >= MIN_MERGE(32),进入TimSort算法的主体
     *      4. 计算 minRun 长度，
     *         minRun的大小应保证 待排数组长度n/minRun 接近2的幂次，这样可以减少合并测次数
     *      5. 计算一个run的长度
     *         a. 自然增长序列   a[k-1] <= a[k] <= a[k+1]
     *         b. 严格递减序列（不包含等于） a[k-1] > a[k] > a[k+1]
     *         保证 自然增长序列 或者 严格递减序列 是为了保证算法的稳定性
     *      6. 一个run不足 minRun的话，使用折半插入填充
     *      7. 将run压入栈
     *      8. 合并
     *         每个run应确保以下规则：
     *            a. run[i] > run[i+1]
     *            b. run[i-1] > run[i] + run[i+1]
     *
     *         Q：为什么要有这个规则？
     *         A: 使用这个规则模拟类似传统归并排序的
     *
     *         合并规则：
     *            a. 当 run[i] <= run[i+1]时，run[i+1]合并到run[i]
     *            b. 当 run[i-1] <= run[i] + run[i+1],
     *               run[i-1]和run[i+1]中小的那个与run[i]合并
     *      9. 最后剩下的run,即为排好序的结果
     *
     * @param a 待排数组
     * @param lo 待排数组左边界,包括
     * @param hi 待排数组右边界，不包括
     * @param c 比较器
     * @param <T> 待排数组类型
     */
    public static <T> void sort(T[] a, int lo, int hi, Comparator<? super T> c){

        assert c != null && a != null && lo >= 0 && lo <= hi && hi <= a.length;

        //计算nRemaining
        int nRemaining = hi - lo;

        //如果长度小于2，则不需要排序
        if(nRemaining < 2) return;

        //如果数组元素个数小于 MIN_MERGE(32) 则使用折半插入
        if(nRemaining < MIN_MERGE){
            int initRunLen = countRunAndMakeAscending(a,lo,hi,c);
            binarySort(a,lo,hi,lo+initRunLen,c);
            return;
        }

        MyTimSort<T> ts = new MyTimSort<>(a, c);
        int minRunLen = minRunLength(nRemaining);
        do{
            int runLen = countRunAndMakeAscending(a,lo,hi,c);
            //如果一个run的长度小于minRunLen，则使用折半插入填充run
            if(runLen < minRunLen){
                //force = min(nRemaining,minRunLen)
                int force = nRemaining<minRunLen ? nRemaining:minRunLen;
                binarySort(a,lo,lo+force,lo+runLen,c);
                runLen = force;
            }

            ts.pushRun(lo,runLen);
            ts.mergeCollapse();

            lo += runLen;
            nRemaining -= runLen;
        }while(nRemaining > 0);

        assert lo == hi;
        ts.mergeForceCollapse();
        assert ts.stackSize == 1;

    }

    /**
     * 折半插入排序 排序区间[lo,start,hi)
     * [lo,start）为已排好序的区间
     * @param a 待排数组
     * @param lo 左边界
     * @param hi 右边界
     * @param start 开始排序位置
     * @param c 比较器
     * @param <T> 泛型类型
     */
    private static <T> void binarySort(T[] a, int lo, int hi, int start,
                                       Comparator<? super T> c){

        assert a != null && c != null && 0 <= lo && lo <= start && start<= hi && lo <= hi && hi <= a.length;

        for(;start<hi;start++){
            int l=lo,r=start;
            //二分搜索右边界
            while(l<r){
                int m = l + ((r-l)>>>1);
                if(c.compare(a[start],a[m])<0)r = m;
                else l = m+1;
            }
            //插入元素
//            T t = a[start];
//            for(int i = start;i>l;i--){
//                a[i] = a[i-1];
//            }
//            a[l]=t;

            assert l==r;
            //使用数组复制，提高效率
            T t = a[start];
            int n = start - l;
            System.arraycopy(a,l,a,l+1,n);
            //源码是这样写的，很巧妙
//            switch (n) {
//                case 2:  a[left + 2] = a[left + 1];
//                case 1:  a[left + 1] = a[left];
//                    break;
//                default: System.arraycopy(a, left, a, left + 1, n);
//            }
            a[l] = t;
        }

    }

    /**
     * 计算run的长度
     *  1. 自然增长序列   a[k-1] <= a[k] <= a[k+1]
     *  2. 严格递减序列（不包含等于） a[k-1] > a[k] > a[k+1]
     *
     * 如果找到的是自然增长序列，则返回
     * 如果找到的是严格递减序列，则反转后返回
     *
     * @param a 数组
     * @param lo 开始下标
     * @param hi 结束下标
     * @param c 比较器
     * @param <T> 泛型类型
     * @return
     */
    private static <T> int countRunAndMakeAscending(T[] a, int lo, int hi,
                                                    Comparator<? super T> c) {
        assert lo <= hi;

        int runHi = lo+1;
        //lo+1 = hi
        if(runHi==hi) return 1;

        if(c.compare(a[runHi++],a[lo])<0){  //a[runHi-1] > a[runHi] > a[runHi+1]
            while(runHi < hi && c.compare(a[runHi],a[runHi-1]) < 0)
                runHi++;
            //反转区间[lo,runHi]中的元素
            reverseRange(a,lo,runHi);
        }else{                              //a[runHi-1] <= a[runHi] <= a[runHi+1]
            while(runHi < hi && c.compare(a[runHi],a[runHi-1]) >= 0)
                runHi++;
        }
        return runHi-lo;
    }

    /**
     * 反转数组 反转区域[lo,hi)
     *
     * @param a 待反转的数组
     * @param lo 反转开始位置，包含
     * @param hi 反转结束位置，不包含
     */
    private static void reverseRange(Object[] a, int lo, int hi) {
        //因为不包含hi，所以减一
        hi--;
        while(lo<hi){
            Object o = a[lo];
            a[lo++] = a[hi];
            a[hi--] = o;
        }
    }

    /**
     * 返回参与合并的最小长度，如果自然排序的长度，小于此长度，那么就通过二分查找排序扩展到
     * 此长度。{@link #binarySort}.
     *
     * 粗略的讲，计算结果是这样的：
     *
     * 如果 n < MIN_MERGE, 直接返回 n。（太小了，不值得做复杂的操作）；
     * 如果 n 正好是2的幂，返回 n / 2；
     * 其它情况下 返回一个数 k，满足 MIN_MERGE/2 <= k <= MIN_MERGE,
     * 这样结果就能保证 n/k 非常接近但小于一个2的幂。
     * 这个数字实际上是一种空间与时间的优化。
     *
     * @param n 参与排序的数组的长度
     * @return 参与归并的最短长度
     * 这段代码写得也很赞
     */
    //直接引用源码计算minRunLength
    private static int minRunLength(int n) {
        assert n >= 0;
        int r = 0;      // Becomes 1 if any 1 bits are shifted off
        while (n >= MIN_MERGE) {
            r |= (n & 1);
            n >>= 1;
        }
        return n + r;
    }

    /**
     * 压入栈
     *
     * 始终有 runBase[i]+runLen[i] = runBase[i+1]
     *
     * @param runBase
     * @param runLen
     */
    private void pushRun(int runBase, int runLen) {
        this.runBase[stackSize] = runBase;
        this.runLen[stackSize] = runLen;
        stackSize++;
    }

    /**
     * 维护栈，合并违反规则的run
     *
     * a. run[n-2] > run[n-1]
     * b. run[n-3] > run[n-2]
     * b. run[n-3] > run[n-2]+ run[n-1]
     */
    private void mergeCollapse() {
        while(stackSize>1){
            //n表示栈中导数第2个下标
            int n = stackSize - 2;
            if(n>0 && runLen[n-1] <= runLen[n]+runLen[n+1]){
                if(runLen[n-1] < runLen[n+1])n--;
            }else if(runLen[n] > runLen[n+1]){
                break;
            }
            mergeAt(n);
        }
    }

    /**
     * 合并剩下的run
     */
    private void mergeForceCollapse() {

        while(stackSize>1){
            //n表示栈中导数第2个下标
            int n = stackSize - 2;
            if(n>0 && runLen[n-1] < runLen[n+1]) n--;
            mergeAt(n);
        }
    }

    /**
     * 合并run[i+1]到run[i], i == stackSize-2 || stackSize-3
     *
     * 举例：
     *  我们要将run[i]和run[i+1]这2个run合并，且run[i+1]是较小的run。
     *  因为run[i]和run[i+1]已经分别是排好序的，二分查找会找到run[i+1]的第一个元素(a[runBase[i+1]])在run[i]中何处插入。
     *  同样，run[i]的最后一个元素(a[runBase[i]+runLen[i]-1])找到在run[i+1]的何处插入，
     *  找到以后，run[i]的最后一个元素在这个位置之后的元素就不需要比较了。
     *
     * @param i
     */
    private void mergeAt(int i) {

        assert stackSize >= 2;
        assert i >= 0;
        assert i == stackSize - 2 || i == stackSize - 3;

        int base1 = runBase[i];
        int len1 = runLen[i];
        int base2 = runBase[i+1];
        int len2 = runLen[i+1];

        assert len1 > 0 && len2 > 0;
        assert base1 + len1 == base2;

        runLen[i] = len1+len2;
        //i == stackSize - 3 ,即合并a[i-3]和a[i-2]的情况，复制啊a[i-1]到a[i-2]
        if(i == stackSize - 3){
            runBase[i+1] = runBase[i+2];
            runLen[i+1] = runLen[i+2];
        }
        stackSize--;

        //查找run[i+1]的第一个元素在run[i]中的插入位置（右边界）
//        int k = gallopRight(a[base2],a,base1,len1,c);
        int k = gallopRight(a[base2], a, base1, len1, c);
        assert k >= 0;
        //如果 k==len1,表明run[i+1]的第一个元素比run[i]中所有元素都大，即为有序序列
        base1+=k;
        len1 -= k;
        if(len1==0)return;

        //查找run[i]的最后一个元素在run[i+1]中的插入位置（左边界）
//        len2 = gallopLeft(a[base1+len1-1],a,base2,len2,c);
        len2 = gallopLeft(a[base1 + len1 - 1], a, base2,len2, c);
        assert len2 >= 0;
        //如果 len2==0,表明run[i]的最后一个元素比run[i+1]中所有元素都小，即为有序序列
        if(len2==0)return;

        //合并,那边的合并元素比较少，就copy元素少的到临时数组
        //if len1<len2,则copy a[base1,base1+len1-1]到临时数组tmp
        //if len1>len2,则copy a[base2,base2+len2-1]到临时数组tmp
        //if len1=len2,都可以
        if(len1<len2){
            mergeLo(base1,len1,base2,len2);
        }else{
            mergeHi(base1,len1,base2,len2);
        }

    }

    /**
     * 这里实现的源码不太一样
     * 这里直接使用二分查找寻找边界
     *
     * 寻找左边界
     * @param key
     * @param a
     * @param base 基准
     * @param len 寻找长度
     * @param c
     * @param <T>
     * @return
     */
    private static <T> int gallopLeft(T key, T[] a, int base, int len,
                                      Comparator<? super T> c){
        assert len > 0;

        int lo = base, hi = base+len-1;
        if(c.compare(key,a[hi])>0)return len;
        while(lo<hi){
            int m = lo + ((hi-lo)>>>1);
            if(c.compare(key,a[m])>0)lo=m+1;
            else hi=m;
        }

        assert lo == hi;

        return lo-base;
    }

    /**
     * 这里实现的源码不太一样
     * 这里直接使用二分查找寻找边界
     * @param key
     * @param a
     * @param base
     * @param len
     * @param c
     * @param <T>
     * @return
     */
    private static <T> int gallopRight(T key, T[] a, int base, int len,
                                       Comparator<? super T> c){
        assert len>0;

        int lo = base, hi = base+len-1;
        if(c.compare(key,a[hi])>=0)return len;
        while(lo<hi){
            int m = lo + ((hi-lo)>>>1);
            if(c.compare(key,a[m])<0)hi=m;
            else lo=m+1;
        }

        assert lo==hi;

        return lo-base;
    }

    /**
     * 复制run[i-1]到tmp
     * 合并run[i]到run[i-1]
     *
     * @param base1 run1的基准
     * @param len1  run1中待合并的元素个数
     * @param base2 run2的基准
     * @param len2  run2中待合并的元素个数
     */
    private void mergeLo(int base1, int len1, int base2, int len2) {
        assert len1 > 0 && len2 > 0 && base1 + len1 == base2;
        T[] a = this.a;
        T[] tmp = ensureCapacity(len1);
        int cursor1 = tmpBase;
        int cursor2 = base2;
        int dest = base1;
        System.arraycopy(a, base1, tmp, cursor1, len1);

        //当run2中合并的元素只有一个的情况
        a[dest++] = a[cursor2++];
        if(--len2==0){
            System.arraycopy(tmp,cursor1,a,dest,len1);
            return;
        }
        //run1的最后一个元素一定大于run2[base2,base2+len2-1]中的所有元素
        //当run1中合并的元素只有一个的情况
        if(len1==1){
            System.arraycopy(a,cursor2,a,dest,len2);
            a[dest+len2] = tmp[cursor1];
            return;
        }

        Comparator<? super T> c = this.c;
        int minGallop = this.minGallop;
        outer:
            while(true){
                int count1 = 0;             //计算run1中连续小于run2中元素的次数
                int count2 = 0;             //计算run1中连续小于run2中元素的次数

                do{
                    assert len1 > 1 && len2 > 0;
                    if(c.compare(tmp[cursor1],a[cursor2])<=0){
                        a[dest++] = tmp[cursor1++];
//                        len1--;
                        count1++;
                        count2=0;
                        if(--len1==1)break outer;
                    }else{
                        a[dest++] = a[cursor2++];
                        count2++;
                        count1=0;
                        if(--len2==0)break outer;
                    }
                //这里的写法很不错，参考源码。count1和count2中其中一个一定为零，
                //一个数和零做或操作，大小不变。简化了（count1 < minGallop && count2 < minGallop）
                }while((count1|count2) < minGallop );

                /**
                 * 一个run中的元素，连续性的小于另一个run中某个元素，表明这个run的元素
                 * 大部分都连续的小于另一个run中某个元素，进入Galloping mode。
                 *
                 * 同时减少阀值，更容易进入Galloping mode
                 */
                do{
                    assert len1 > 1 && len2 > 0;
                    count1 = gallopRight(a[cursor2],tmp,cursor1,len1,c);
                    if(count1 !=0){
                        System.arraycopy(tmp,cursor1,a,dest,count1);
                        cursor1+=count1;
                        dest+=count1;
                        len1-=count1;
//                        System.out.println(count1);
//                        System.out.println(len1);
                        if(len1<=1)break outer;
                    }
                    a[dest++] =a[cursor2++];
                    if(--len2==0)break outer;

                    count2 = gallopLeft(tmp[cursor1],a,cursor2,len2,c);
                    if (count2!=0){
                        System.arraycopy(a,cursor2,a,dest,count2);
                        cursor2+=count2;
                        dest+=count2;
                        len2-=count2;
                        if(len2==0)break outer;
                    }

                    a[dest++] = tmp[cursor1++];
                    if(--len1==1)break outer;
                    //减少阀值，更容易进入Galloping mode
                    minGallop--;
                }while(count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);

                if (minGallop < 0)
                    minGallop = 0;
                minGallop += 2;  // 退出Galloping mode，增加阀值大小
            }
        this.minGallop = minGallop < 1 ? 1 : minGallop;  // Write back to field

        if (len1 == 1) {
            assert len2 > 0;
            System.arraycopy(a, cursor2, a, dest, len2);
            a[dest + len2] = tmp[cursor1];
        } else if (len1 == 0) {
            throw new IllegalArgumentException(
                    "Comparison method violates its general contract!");
        } else {
            assert len2 == 0;
            assert len1 > 1;
            System.arraycopy(tmp, cursor1, a, dest, len1);
        }
    }

    /**
     * 复制run[i]到tmp
     * 合并run[i]到run[i-1]
     *
     * @param base1 run1的基准
     * @param len1  run1中待合并的元素个数
     * @param base2 run2的基准
     * @param len2  run2中待合并的元素个数
     */
    private void mergeHi(int base1, int len1, int base2, int len2) {
        assert len1 > 0 && len2 > 0 && base1 + len1 == base2;

        T[] a = this.a;
        T[] tmp = ensureCapacity(len2);
        int tmpBase = this.tmpBase;

        int cursor1 = base1 + len1 - 1;         //run1的指针
        int cursor2 = tmpBase + len2 - 1;       //tmp（即run2）的指针
        int dest = base2 + len2 - 1;
        System.arraycopy(a, base2, tmp, tmpBase, len2);

        //当run2中合并的元素只有一个的情况
        //run2的第一个元素一定小于run1[base1,base1+len1-1]中的所有元素
//        if(len2==1){
//            cursor1 -= len1;
//            dest -= len1;
//            System.arraycopy(a,cursor1+1,a,dest+1,len1);
//            a[dest] = tmp[cursor2];
//            return;
//        }
//
//        //当run1中合并的元素只有一个的情况
//        if(len1==1){
//            a[dest--] = tmp[cursor1--];
//            System.arraycopy(tmp,tmpBase,a,dest-(len2-1),len2);
//            return;
//        }

        a[dest--] = a[cursor1--];
        if (--len1 == 0) {
            System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
            return;
        }
        if (len2 == 1) {
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
            return;
        }

        Comparator<? super T> c = this.c;
        int minGallop = this.minGallop;

        outer:
            while(true){

                int count1 = 0;
                int count2 = 0;

                do{
                    assert len1 > 0 && len2 > 1;
                    if(c.compare(tmp[cursor2],a[cursor1])<0){
                        a[dest--] = a[cursor1--];
                        count1++;
                        count2=0;
                        if(--len1==0)break outer;
                    }else{
                        a[dest--] = tmp[cursor2--];
                        count2++;
                        count1=0;
                        if(--len2==1) break outer;
                    }
                }while((count1|count2)<minGallop);

                //进入Gallop mode
                do{
                    assert len1 > 0 && len2 > 1;
                    count1 =len1 - gallopRight(tmp[cursor2],a,base1,len1,c);
                    if(count1!=0){
                        len1 -= count1;
                        cursor1 -= count1;
                        dest -= count1;
                        System.arraycopy(a,cursor1+1,a,dest+1,count1);
                        if(len1==0)break outer;
                    }
                    a[dest--] = tmp[cursor2--];
                    if(--len2==1)break outer;

                    count2 = len2 - gallopLeft(a[cursor1],tmp,tmpBase,len2,c);
                    if(count2!=0){
                        dest -= count2;
                        len2 -= count2;
                        cursor2 -= count2;
                        System.arraycopy(tmp,cursor2+1,a,dest+1,count2);
                        if(len2<=1)break outer;
                    }
                    a[dest--] = a[cursor1--];
                    if(--len1==0)break  outer;

                    minGallop--;
                }while(count1>=MIN_GALLOP | count2>=MIN_GALLOP);

                if(minGallop<0)minGallop = 0;
                minGallop+=2;
            }

        this.minGallop = minGallop < 1 ? 1 : minGallop;  // Write back to field

        if (len2 == 1) {
            assert len1 > 0;
            dest -= len1;
            cursor1 -= len1;
            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
            a[dest] = tmp[cursor2];
        } else if (len2 == 0) {
            throw new IllegalArgumentException(
                    "Comparison method violates its general contract!");
        } else {
            assert len1 == 0;
            assert len2 > 0;
//            System.out.println(dest);
            System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
        }

    }



    private T[] ensureCapacity(int minCapacity) {
        if (tmpLen < minCapacity) {
            int newSize = -1 >>> Integer.numberOfLeadingZeros(minCapacity);
            newSize++;

            if (newSize < 0)
                newSize = minCapacity;
            else
                newSize = Math.min(newSize, a.length >>> 1);

            @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
            T[] newArray = (T[])java.lang.reflect.Array.newInstance
                    (a.getClass().getComponentType(), newSize);
            tmp = newArray;
            tmpLen = newSize;
            tmpBase = 0;
        }
        return tmp;
    }

    /**
     * 测试规模：
     *
     * 测试结果：
     */
//    @Override
    public static void sortingComparison() {
        // 正常随机数组
        Integer[] a11 = RandomArrayUtil.getRandomBoxedIntArray(0, 1000000, 1000000);

        // 大量重复数组
        Integer[] a21 = RandomArrayUtil.getRandomBoxedIntArray(0, 100, 1000000);

        System.out.println("Array created!");

        StopWatch stopWatch = new StopWatch();
        sort(a11,0,a11.length,(v1,v2)->v1-v2);
        assert isSorted(a11);
        System.out.println(String.format(formatStringWithRandom, "insertionSort", stopWatch.elapsedTime()));
        assert isSorted(a21);
        System.out.println(String.format(formatStringWithDuplicate, "insertionSort", stopWatch.elapsedTime()));

    }

//    private void mergeLo(int base1, int len1, int base2, int len2) {
//        assert len1 > 0 && len2 > 0 && base1 + len1 == base2;
//
//        // Copy first run into temp array
//        T[] a = this.a; // For performance
//        T[] tmp = ensureCapacity(len1);
//        int cursor1 = tmpBase; // Indexes into tmp array
//        int cursor2 = base2;   // Indexes int a
//        int dest = base1;      // Indexes int a
//        System.arraycopy(a, base1, tmp, cursor1, len1);
//
//        // Move first element of second run and deal with degenerate cases
//        a[dest++] = a[cursor2++];
//        if (--len2 == 0) {
//            System.arraycopy(tmp, cursor1, a, dest, len1);
//            return;
//        }
//        if (len1 == 1) {
//            System.arraycopy(a, cursor2, a, dest, len2);
//            a[dest + len2] = tmp[cursor1]; // Last elt of run 1 to end of merge
//            return;
//        }
//
//        Comparator<? super T> c = this.c;  // Use local variable for performance
//        int minGallop = this.minGallop;    //  "    "       "     "      "
//        outer:
//        while (true) {
//            int count1 = 0; // Number of times in a row that first run won
//            int count2 = 0; // Number of times in a row that second run won
//
//            /*
//             * Do the straightforward thing until (if ever) one run starts
//             * winning consistently.
//             */
//            do {
//                assert len1 > 1 && len2 > 0;
//                if (c.compare(a[cursor2], tmp[cursor1]) < 0) {
//                    a[dest++] = a[cursor2++];
//                    count2++;
//                    count1 = 0;
//                    if (--len2 == 0)
//                        break outer;
//                } else {
//                    a[dest++] = tmp[cursor1++];
//                    count1++;
//                    count2 = 0;
//                    if (--len1 == 1)
//                        break outer;
//                }
//            } while ((count1 | count2) < minGallop);
//
//            /*
//             * One run is winning so consistently that galloping may be a
//             * huge win. So try that, and continue galloping until (if ever)
//             * neither run appears to be winning consistently anymore.
//             */
//            do {
//                assert len1 > 1 && len2 > 0;
//                count1 = gallopRight(a[cursor2], tmp, cursor1, len1, c);
//                if (count1 != 0) {
//                    System.arraycopy(tmp, cursor1, a, dest, count1);
//                    dest += count1;
//                    cursor1 += count1;
//                    len1 -= count1;
//                    if (len1 <= 1) // len1 == 1 || len1 == 0
//                        break outer;
//                }
//                a[dest++] = a[cursor2++];
//                if (--len2 == 0)
//                    break outer;
//
//                count2 = gallopLeft(tmp[cursor1], a, cursor2, len2, c);
//                if (count2 != 0) {
//                    System.arraycopy(a, cursor2, a, dest, count2);
//                    dest += count2;
//                    cursor2 += count2;
//                    len2 -= count2;
//                    if (len2 == 0)
//                        break outer;
//                }
//                a[dest++] = tmp[cursor1++];
//                if (--len1 == 1)
//                    break outer;
//                minGallop--;
//            } while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
//            if (minGallop < 0)
//                minGallop = 0;
//            minGallop += 2;  // Penalize for leaving gallop mode
//        }  // End of "outer" loop
//        this.minGallop = minGallop < 1 ? 1 : minGallop;  // Write back to field
//
//        if (len1 == 1) {
//            assert len2 > 0;
//            System.arraycopy(a, cursor2, a, dest, len2);
//            a[dest + len2] = tmp[cursor1]; //  Last elt of run 1 to end of merge
//        } else if (len1 == 0) {
//            throw new IllegalArgumentException(
//                    "Comparison method violates its general contract!");
//        } else {
//            assert len2 == 0;
//            assert len1 > 1;
//            System.arraycopy(tmp, cursor1, a, dest, len1);
//        }
//    }
//
//    private void mergeHi(int base1, int len1, int base2, int len2) {
//        assert len1 > 0 && len2 > 0 && base1 + len1 == base2;
//
//        // Copy second run into temp array
//        T[] a = this.a; // For performance
//        T[] tmp = ensureCapacity(len2);
//        int tmpBase = this.tmpBase;
//        System.arraycopy(a, base2, tmp, tmpBase, len2);
//
//        int cursor1 = base1 + len1 - 1;  // Indexes into a
//        int cursor2 = tmpBase + len2 - 1; // Indexes into tmp array
//        int dest = base2 + len2 - 1;     // Indexes into a
//
//        // Move last element of first run and deal with degenerate cases
//        a[dest--] = a[cursor1--];
//        if (--len1 == 0) {
//            System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
//            return;
//        }
//        if (len2 == 1) {
//            dest -= len1;
//            cursor1 -= len1;
//            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
//            a[dest] = tmp[cursor2];
//            return;
//        }
//
//        Comparator<? super T> c = this.c;  // Use local variable for performance
//        int minGallop = this.minGallop;    //  "    "       "     "      "
//        outer:
//        while (true) {
//            int count1 = 0; // Number of times in a row that first run won
//            int count2 = 0; // Number of times in a row that second run won
//
//            /*
//             * Do the straightforward thing until (if ever) one run
//             * appears to win consistently.
//             */
//            do {
//                assert len1 > 0 && len2 > 1;
//                if (c.compare(tmp[cursor2], a[cursor1]) < 0) {
//                    a[dest--] = a[cursor1--];
//                    count1++;
//                    count2 = 0;
//                    if (--len1 == 0)
//                        break outer;
//                } else {
//                    a[dest--] = tmp[cursor2--];
//                    count2++;
//                    count1 = 0;
//                    if (--len2 == 1)
//                        break outer;
//                }
//            } while ((count1 | count2) < minGallop);
//
//            /*
//             * One run is winning so consistently that galloping may be a
//             * huge win. So try that, and continue galloping until (if ever)
//             * neither run appears to be winning consistently anymore.
//             */
//            do {
//                assert len1 > 0 && len2 > 1;
//                count1 = len1 - gallopRight(tmp[cursor2], a, base1, len1, c);
//                if (count1 != 0) {
//                    dest -= count1;
//                    cursor1 -= count1;
//                    len1 -= count1;
//                    System.arraycopy(a, cursor1 + 1, a, dest + 1, count1);
//                    if (len1 == 0)
//                        break outer;
//                }
//                a[dest--] = tmp[cursor2--];
//                if (--len2 == 1)
//                    break outer;
//
//                count2 = len2 - gallopLeft(a[cursor1], tmp, tmpBase, len2, c);
//                if (count2 != 0) {
//                    dest -= count2;
//                    cursor2 -= count2;
//                    len2 -= count2;
//                    System.arraycopy(tmp, cursor2 + 1, a, dest + 1, count2);
//                    if (len2 <= 1)  // len2 == 1 || len2 == 0
//                        break outer;
//                }
//                a[dest--] = a[cursor1--];
//                if (--len1 == 0)
//                    break outer;
//                minGallop--;
//            } while (count1 >= MIN_GALLOP | count2 >= MIN_GALLOP);
//            if (minGallop < 0)
//                minGallop = 0;
//            minGallop += 2;  // Penalize for leaving gallop mode
//        }  // End of "outer" loop
//        this.minGallop = minGallop < 1 ? 1 : minGallop;  // Write back to field
//
//        if (len2 == 1) {
//            assert len1 > 0;
//            dest -= len1;
//            cursor1 -= len1;
//            System.arraycopy(a, cursor1 + 1, a, dest + 1, len1);
//            a[dest] = tmp[cursor2];  // Move first elt of run2 to front of merge
//        } else if (len2 == 0) {
//            throw new IllegalArgumentException(
//                    "Comparison method violates its general contract!");
//        } else {
//            assert len1 == 0;
//            assert len2 > 0;
//            System.arraycopy(tmp, tmpBase, a, dest - (len2 - 1), len2);
//        }
//    }


}
