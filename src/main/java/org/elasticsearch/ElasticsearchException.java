begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch
package|package
name|org
operator|.
name|elasticsearch
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|rest
operator|.
name|RestStatus
import|;
end_import

begin_comment
comment|/**  * A base class for all elasticsearch exceptions.  */
end_comment

begin_class
DECL|class|ElasticsearchException
specifier|public
class|class
name|ElasticsearchException
extends|extends
name|RuntimeException
block|{
comment|/**      * Construct a<code>ElasticsearchException</code> with the specified detail message.      *      * @param msg the detail message      */
DECL|method|ElasticsearchException
specifier|public
name|ElasticsearchException
parameter_list|(
name|String
name|msg
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|)
expr_stmt|;
block|}
comment|/**      * Construct a<code>ElasticsearchException</code> with the specified detail message      * and nested exception.      *      * @param msg   the detail message      * @param cause the nested exception      */
DECL|method|ElasticsearchException
specifier|public
name|ElasticsearchException
parameter_list|(
name|String
name|msg
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|super
argument_list|(
name|msg
argument_list|,
name|cause
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the rest status code associated with this exception.      */
DECL|method|status
specifier|public
name|RestStatus
name|status
parameter_list|()
block|{
name|Throwable
name|cause
init|=
name|unwrapCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|==
name|this
condition|)
block|{
return|return
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
return|;
block|}
elseif|else
if|if
condition|(
name|cause
operator|instanceof
name|ElasticsearchException
condition|)
block|{
return|return
operator|(
operator|(
name|ElasticsearchException
operator|)
name|cause
operator|)
operator|.
name|status
argument_list|()
return|;
block|}
elseif|else
if|if
condition|(
name|cause
operator|instanceof
name|IllegalArgumentException
condition|)
block|{
return|return
name|RestStatus
operator|.
name|BAD_REQUEST
return|;
block|}
else|else
block|{
return|return
name|RestStatus
operator|.
name|INTERNAL_SERVER_ERROR
return|;
block|}
block|}
comment|/**      * Unwraps the actual cause from the exception for cases when the exception is a      * {@link ElasticsearchWrapperException}.      *      * @see org.elasticsearch.ExceptionsHelper#unwrapCause(Throwable)      */
DECL|method|unwrapCause
specifier|public
name|Throwable
name|unwrapCause
parameter_list|()
block|{
return|return
name|ExceptionsHelper
operator|.
name|unwrapCause
argument_list|(
name|this
argument_list|)
return|;
block|}
comment|/**      * Return the detail message, including the message from the nested exception      * if there is one.      */
DECL|method|getDetailedMessage
specifier|public
name|String
name|getDetailedMessage
parameter_list|()
block|{
if|if
condition|(
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
if|if
condition|(
name|getCause
argument_list|()
operator|instanceof
name|ElasticsearchException
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
operator|(
operator|(
name|ElasticsearchException
operator|)
name|getCause
argument_list|()
operator|)
operator|.
name|getDetailedMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * Retrieve the innermost cause of this exception, if none, returns the current exception.      */
DECL|method|getRootCause
specifier|public
name|Throwable
name|getRootCause
parameter_list|()
block|{
name|Throwable
name|rootCause
init|=
name|this
decl_stmt|;
name|Throwable
name|cause
init|=
name|getCause
argument_list|()
decl_stmt|;
while|while
condition|(
name|cause
operator|!=
literal|null
operator|&&
name|cause
operator|!=
name|rootCause
condition|)
block|{
name|rootCause
operator|=
name|cause
expr_stmt|;
name|cause
operator|=
name|cause
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
name|rootCause
return|;
block|}
comment|/**      * Retrieve the most specific cause of this exception, that is,      * either the innermost cause (root cause) or this exception itself.      *<p>Differs from {@link #getRootCause()} in that it falls back      * to the present exception if there is no root cause.      *      * @return the most specific cause (never<code>null</code>)      */
DECL|method|getMostSpecificCause
specifier|public
name|Throwable
name|getMostSpecificCause
parameter_list|()
block|{
name|Throwable
name|rootCause
init|=
name|getRootCause
argument_list|()
decl_stmt|;
return|return
operator|(
name|rootCause
operator|!=
literal|null
condition|?
name|rootCause
else|:
name|this
operator|)
return|;
block|}
comment|/**      * Check whether this exception contains an exception of the given type:      * either it is of the given class itself or it contains a nested cause      * of the given type.      *      * @param exType the exception type to look for      * @return whether there is a nested exception of the specified type      */
DECL|method|contains
specifier|public
name|boolean
name|contains
parameter_list|(
name|Class
name|exType
parameter_list|)
block|{
if|if
condition|(
name|exType
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|exType
operator|.
name|isInstance
argument_list|(
name|this
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|Throwable
name|cause
init|=
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|==
name|this
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|cause
operator|instanceof
name|ElasticsearchException
condition|)
block|{
return|return
operator|(
operator|(
name|ElasticsearchException
operator|)
name|cause
operator|)
operator|.
name|contains
argument_list|(
name|exType
argument_list|)
return|;
block|}
else|else
block|{
while|while
condition|(
name|cause
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|exType
operator|.
name|isInstance
argument_list|(
name|cause
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|cause
operator|.
name|getCause
argument_list|()
operator|==
name|cause
condition|)
block|{
break|break;
block|}
name|cause
operator|=
name|cause
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

