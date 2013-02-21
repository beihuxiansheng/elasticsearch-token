begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Written by Doug Lea with assistance from members of JCP JSR-166  * Expert Group and released to the public domain, as explained at  * http://creativecommons.org/publicdomain/zero/1.0/  */
end_comment

begin_package
DECL|package|jsr166e
package|package
name|jsr166e
package|;
end_package

begin_comment
comment|/**  * A recursive result-bearing {@link ForkJoinTask}.  *  *<p>For a classic example, here is a task computing Fibonacci numbers:  *  *<pre> {@code  * class Fibonacci extends RecursiveTask<Integer> {  *   final int n;  *   Fibonacci(int n) { this.n = n; }  *   Integer compute() {  *     if (n<= 1)  *       return n;  *     Fibonacci f1 = new Fibonacci(n - 1);  *     f1.fork();  *     Fibonacci f2 = new Fibonacci(n - 2);  *     return f2.compute() + f1.join();  *   }  * }}</pre>  *  * However, besides being a dumb way to compute Fibonacci functions  * (there is a simple fast linear algorithm that you'd use in  * practice), this is likely to perform poorly because the smallest  * subtasks are too small to be worthwhile splitting up. Instead, as  * is the case for nearly all fork/join applications, you'd pick some  * minimum granularity size (for example 10 here) for which you always  * sequentially solve rather than subdividing.  *  * @since 1.7  * @author Doug Lea  */
end_comment

begin_class
DECL|class|RecursiveTask
specifier|public
specifier|abstract
class|class
name|RecursiveTask
parameter_list|<
name|V
parameter_list|>
extends|extends
name|ForkJoinTask
argument_list|<
name|V
argument_list|>
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5232453952276485270L
decl_stmt|;
comment|/**      * The result of the computation.      */
DECL|field|result
name|V
name|result
decl_stmt|;
comment|/**      * The main computation performed by this task.      */
DECL|method|compute
specifier|protected
specifier|abstract
name|V
name|compute
parameter_list|()
function_decl|;
DECL|method|getRawResult
specifier|public
specifier|final
name|V
name|getRawResult
parameter_list|()
block|{
return|return
name|result
return|;
block|}
DECL|method|setRawResult
specifier|protected
specifier|final
name|void
name|setRawResult
parameter_list|(
name|V
name|value
parameter_list|)
block|{
name|result
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Implements execution conventions for RecursiveTask.      */
DECL|method|exec
specifier|protected
specifier|final
name|boolean
name|exec
parameter_list|()
block|{
name|result
operator|=
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

