begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Written by Doug Lea with assistance from members of JCP JSR-166  * Expert Group and released to the public domain, as explained at  * http://creativecommons.org/publicdomain/zero/1.0/  */
end_comment

begin_package
DECL|package|jsr166y
package|package
name|jsr166y
package|;
end_package

begin_comment
comment|/**  * A recursive resultless {@link ForkJoinTask}.  This class  * establishes conventions to parameterize resultless actions as  * {@code Void} {@code ForkJoinTask}s. Because {@code null} is the  * only valid value of type {@code Void}, methods such as {@code join}  * always return {@code null} upon completion.  *  *<p><b>Sample Usages.</b> Here is a simple but complete ForkJoin  * sort that sorts a given {@code long[]} array:  *  *<pre> {@code  * static class SortTask extends RecursiveAction {  *   final long[] array; final int lo, hi;  *   SortTask(long[] array, int lo, int hi) {  *     this.array = array; this.lo = lo; this.hi = hi;  *   }  *   SortTask(long[] array) { this(array, 0, array.length); }  *   protected void compute() {  *     if (hi - lo< THRESHOLD)  *       sortSequentially(lo, hi);  *     else {  *       int mid = (lo + hi)>>> 1;  *       invokeAll(new SortTask(array, lo, mid),  *                 new SortTask(array, mid, hi));  *       merge(lo, mid, hi);  *     }  *   }  *   // implementation details follow:  *   final static int THRESHOLD = 1000;  *   void sortSequentially(int lo, int hi) {  *     Arrays.sort(array, lo, hi);  *   }  *   void merge(int lo, int mid, int hi) {  *     long[] buf = Arrays.copyOfRange(array, lo, mid);  *     for (int i = 0, j = lo, k = mid; i< buf.length; j++)  *       array[j] = (k == hi || buf[i]< array[k]) ?  *         buf[i++] : array[k++];  *   }  * }}</pre>  *  * You could then sort {@code anArray} by creating {@code new  * SortTask(anArray)} and invoking it in a ForkJoinPool.  As a more  * concrete simple example, the following task increments each element  * of an array:  *<pre> {@code  * class IncrementTask extends RecursiveAction {  *   final long[] array; final int lo, hi;  *   IncrementTask(long[] array, int lo, int hi) {  *     this.array = array; this.lo = lo; this.hi = hi;  *   }  *   protected void compute() {  *     if (hi - lo< THRESHOLD) {  *       for (int i = lo; i< hi; ++i)  *         array[i]++;  *     }  *     else {  *       int mid = (lo + hi)>>> 1;  *       invokeAll(new IncrementTask(array, lo, mid),  *                 new IncrementTask(array, mid, hi));  *     }  *   }  * }}</pre>  *  *<p>The following example illustrates some refinements and idioms  * that may lead to better performance: RecursiveActions need not be  * fully recursive, so long as they maintain the basic  * divide-and-conquer approach. Here is a class that sums the squares  * of each element of a double array, by subdividing out only the  * right-hand-sides of repeated divisions by two, and keeping track of  * them with a chain of {@code next} references. It uses a dynamic  * threshold based on method {@code getSurplusQueuedTaskCount}, but  * counterbalances potential excess partitioning by directly  * performing leaf actions on unstolen tasks rather than further  * subdividing.  *  *<pre> {@code  * double sumOfSquares(ForkJoinPool pool, double[] array) {  *   int n = array.length;  *   Applyer a = new Applyer(array, 0, n, null);  *   pool.invoke(a);  *   return a.result;  * }  *  * class Applyer extends RecursiveAction {  *   final double[] array;  *   final int lo, hi;  *   double result;  *   Applyer next; // keeps track of right-hand-side tasks  *   Applyer(double[] array, int lo, int hi, Applyer next) {  *     this.array = array; this.lo = lo; this.hi = hi;  *     this.next = next;  *   }  *  *   double atLeaf(int l, int h) {  *     double sum = 0;  *     for (int i = l; i< h; ++i) // perform leftmost base step  *       sum += array[i] * array[i];  *     return sum;  *   }  *  *   protected void compute() {  *     int l = lo;  *     int h = hi;  *     Applyer right = null;  *     while (h - l> 1&& getSurplusQueuedTaskCount()<= 3) {  *        int mid = (l + h)>>> 1;  *        right = new Applyer(array, mid, h, right);  *        right.fork();  *        h = mid;  *     }  *     double sum = atLeaf(l, h);  *     while (right != null) {  *        if (right.tryUnfork()) // directly calculate if not stolen  *          sum += right.atLeaf(right.lo, right.hi);  *       else {  *          right.join();  *          sum += right.result;  *        }  *        right = right.next;  *      }  *     result = sum;  *   }  * }}</pre>  *  * @since 1.7  * @author Doug Lea  */
end_comment

begin_class
DECL|class|RecursiveAction
specifier|public
specifier|abstract
class|class
name|RecursiveAction
extends|extends
name|ForkJoinTask
argument_list|<
name|Void
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5232453952276485070L
decl_stmt|;
comment|/**      * The main computation performed by this task.      */
DECL|method|compute
specifier|protected
specifier|abstract
name|void
name|compute
parameter_list|()
function_decl|;
comment|/**      * Always returns {@code null}.      *      * @return {@code null} always      */
DECL|method|getRawResult
specifier|public
specifier|final
name|Void
name|getRawResult
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Requires null completion value.      */
DECL|method|setRawResult
specifier|protected
specifier|final
name|void
name|setRawResult
parameter_list|(
name|Void
name|mustBeNull
parameter_list|)
block|{ }
comment|/**      * Implements execution conventions for RecursiveActions.      */
DECL|method|exec
specifier|protected
specifier|final
name|boolean
name|exec
parameter_list|()
block|{
name|compute
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

