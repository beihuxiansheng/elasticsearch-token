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
comment|/**  * Exception thrown when an error or other exception is encountered  * in the course of completing a result or task.  *  * @since 1.8  * @author Doug Lea  */
end_comment

begin_class
DECL|class|CompletionException
specifier|public
class|class
name|CompletionException
extends|extends
name|RuntimeException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|7830266012832686185L
decl_stmt|;
comment|/**      * Constructs a {@code CompletionException} with no detail message.      * The cause is not initialized, and may subsequently be      * initialized by a call to {@link #initCause(Throwable) initCause}.      */
DECL|method|CompletionException
specifier|protected
name|CompletionException
parameter_list|()
block|{ }
comment|/**      * Constructs a {@code CompletionException} with the specified detail      * message. The cause is not initialized, and may subsequently be      * initialized by a call to {@link #initCause(Throwable) initCause}.      *      * @param message the detail message      */
DECL|method|CompletionException
specifier|protected
name|CompletionException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a {@code CompletionException} with the specified detail      * message and cause.      *      * @param  message the detail message      * @param  cause the cause (which is saved for later retrieval by the      *         {@link #getCause()} method)      */
DECL|method|CompletionException
specifier|public
name|CompletionException
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**      * Constructs a {@code CompletionException} with the specified cause.      * The detail message is set to {@code (cause == null ? null :      * cause.toString())} (which typically contains the class and      * detail message of {@code cause}).      *      * @param  cause the cause (which is saved for later retrieval by the      *         {@link #getCause()} method)      */
DECL|method|CompletionException
specifier|public
name|CompletionException
parameter_list|(
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

