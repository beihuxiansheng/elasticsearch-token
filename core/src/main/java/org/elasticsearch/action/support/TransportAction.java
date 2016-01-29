begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.support
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionFuture
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
operator|.
name|IndexNameExpressionResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|ParseFieldMatcher
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|component
operator|.
name|AbstractComponent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|logging
operator|.
name|ESLogger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|Task
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|tasks
operator|.
name|TaskManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|threadpool
operator|.
name|ThreadPool
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|PlainActionFuture
operator|.
name|newFuture
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportAction
specifier|public
specifier|abstract
class|class
name|TransportAction
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
extends|extends
name|AbstractComponent
block|{
DECL|field|threadPool
specifier|protected
specifier|final
name|ThreadPool
name|threadPool
decl_stmt|;
DECL|field|actionName
specifier|protected
specifier|final
name|String
name|actionName
decl_stmt|;
DECL|field|filters
specifier|private
specifier|final
name|ActionFilter
index|[]
name|filters
decl_stmt|;
DECL|field|parseFieldMatcher
specifier|protected
specifier|final
name|ParseFieldMatcher
name|parseFieldMatcher
decl_stmt|;
DECL|field|indexNameExpressionResolver
specifier|protected
specifier|final
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
decl_stmt|;
DECL|field|taskManager
specifier|protected
specifier|final
name|TaskManager
name|taskManager
decl_stmt|;
DECL|method|TransportAction
specifier|protected
name|TransportAction
parameter_list|(
name|Settings
name|settings
parameter_list|,
name|String
name|actionName
parameter_list|,
name|ThreadPool
name|threadPool
parameter_list|,
name|ActionFilters
name|actionFilters
parameter_list|,
name|IndexNameExpressionResolver
name|indexNameExpressionResolver
parameter_list|,
name|TaskManager
name|taskManager
parameter_list|)
block|{
name|super
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadPool
operator|=
name|threadPool
expr_stmt|;
name|this
operator|.
name|actionName
operator|=
name|actionName
expr_stmt|;
name|this
operator|.
name|filters
operator|=
name|actionFilters
operator|.
name|filters
argument_list|()
expr_stmt|;
name|this
operator|.
name|parseFieldMatcher
operator|=
operator|new
name|ParseFieldMatcher
argument_list|(
name|settings
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexNameExpressionResolver
operator|=
name|indexNameExpressionResolver
expr_stmt|;
name|this
operator|.
name|taskManager
operator|=
name|taskManager
expr_stmt|;
block|}
DECL|method|execute
specifier|public
specifier|final
name|ActionFuture
argument_list|<
name|Response
argument_list|>
name|execute
parameter_list|(
name|Request
name|request
parameter_list|)
block|{
name|PlainActionFuture
argument_list|<
name|Response
argument_list|>
name|future
init|=
name|newFuture
argument_list|()
decl_stmt|;
name|execute
argument_list|(
name|request
argument_list|,
name|future
argument_list|)
expr_stmt|;
return|return
name|future
return|;
block|}
DECL|method|execute
specifier|public
specifier|final
name|Task
name|execute
parameter_list|(
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
return|return
name|execute
argument_list|(
name|request
argument_list|,
operator|new
name|TaskListener
argument_list|<
name|Response
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Task
name|task
parameter_list|,
name|Response
name|response
parameter_list|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Task
name|task
parameter_list|,
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|execute
specifier|public
specifier|final
name|Task
name|execute
parameter_list|(
name|Request
name|request
parameter_list|,
name|TaskListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|Task
name|task
init|=
name|taskManager
operator|.
name|register
argument_list|(
literal|"transport"
argument_list|,
name|actionName
argument_list|,
name|request
argument_list|)
decl_stmt|;
name|execute
argument_list|(
name|task
argument_list|,
name|request
argument_list|,
operator|new
name|ActionListener
argument_list|<
name|Response
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
name|taskManager
operator|.
name|unregister
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onResponse
argument_list|(
name|task
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
name|taskManager
operator|.
name|unregister
argument_list|(
name|task
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onFailure
argument_list|(
name|task
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|task
return|;
block|}
DECL|method|execute
specifier|private
specifier|final
name|void
name|execute
parameter_list|(
name|Task
name|task
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|ActionRequestValidationException
name|validationException
init|=
name|request
operator|.
name|validate
argument_list|()
decl_stmt|;
if|if
condition|(
name|validationException
operator|!=
literal|null
condition|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|validationException
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|filters
operator|.
name|length
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|doExecute
argument_list|(
name|task
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Error during transport action execution."
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|RequestFilterChain
name|requestFilterChain
init|=
operator|new
name|RequestFilterChain
argument_list|<>
argument_list|(
name|this
argument_list|,
name|logger
argument_list|)
decl_stmt|;
name|requestFilterChain
operator|.
name|proceed
argument_list|(
name|task
argument_list|,
name|actionName
argument_list|,
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|doExecute
specifier|protected
name|void
name|doExecute
parameter_list|(
name|Task
name|task
parameter_list|,
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
block|{
name|doExecute
argument_list|(
name|request
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
DECL|method|doExecute
specifier|protected
specifier|abstract
name|void
name|doExecute
parameter_list|(
name|Request
name|request
parameter_list|,
name|ActionListener
argument_list|<
name|Response
argument_list|>
name|listener
parameter_list|)
function_decl|;
DECL|class|RequestFilterChain
specifier|private
specifier|static
class|class
name|RequestFilterChain
parameter_list|<
name|Request
extends|extends
name|ActionRequest
parameter_list|<
name|Request
parameter_list|>
parameter_list|,
name|Response
extends|extends
name|ActionResponse
parameter_list|>
implements|implements
name|ActionFilterChain
block|{
DECL|field|action
specifier|private
specifier|final
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|AtomicInteger
name|index
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|method|RequestFilterChain
specifier|private
name|RequestFilterChain
parameter_list|(
name|TransportAction
argument_list|<
name|Request
argument_list|,
name|Response
argument_list|>
name|action
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|action
operator|=
name|action
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|proceed
specifier|public
name|void
name|proceed
parameter_list|(
name|Task
name|task
parameter_list|,
name|String
name|actionName
parameter_list|,
name|ActionRequest
name|request
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
name|int
name|i
init|=
name|index
operator|.
name|getAndIncrement
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|i
operator|<
name|this
operator|.
name|action
operator|.
name|filters
operator|.
name|length
condition|)
block|{
name|this
operator|.
name|action
operator|.
name|filters
index|[
name|i
index|]
operator|.
name|apply
argument_list|(
name|task
argument_list|,
name|actionName
argument_list|,
name|request
argument_list|,
name|listener
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
name|this
operator|.
name|action
operator|.
name|filters
operator|.
name|length
condition|)
block|{
name|this
operator|.
name|action
operator|.
name|doExecute
argument_list|(
name|task
argument_list|,
operator|(
name|Request
operator|)
name|request
argument_list|,
operator|new
name|FilteredActionListener
argument_list|<
name|Response
argument_list|>
argument_list|(
name|actionName
argument_list|,
name|listener
argument_list|,
operator|new
name|ResponseFilterChain
argument_list|(
name|this
operator|.
name|action
operator|.
name|filters
argument_list|,
name|logger
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"proceed was called too many times"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Error during transport action execution."
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|proceed
specifier|public
name|void
name|proceed
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionResponse
name|response
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
assert|assert
literal|false
operator|:
literal|"request filter chain should never be called on the response side"
assert|;
block|}
block|}
DECL|class|ResponseFilterChain
specifier|private
specifier|static
class|class
name|ResponseFilterChain
implements|implements
name|ActionFilterChain
block|{
DECL|field|filters
specifier|private
specifier|final
name|ActionFilter
index|[]
name|filters
decl_stmt|;
DECL|field|index
specifier|private
specifier|final
name|AtomicInteger
name|index
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|ESLogger
name|logger
decl_stmt|;
DECL|method|ResponseFilterChain
specifier|private
name|ResponseFilterChain
parameter_list|(
name|ActionFilter
index|[]
name|filters
parameter_list|,
name|ESLogger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|filters
operator|=
name|filters
expr_stmt|;
name|this
operator|.
name|index
operator|=
operator|new
name|AtomicInteger
argument_list|(
name|filters
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|logger
operator|=
name|logger
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|proceed
specifier|public
name|void
name|proceed
parameter_list|(
name|Task
name|task
parameter_list|,
name|String
name|action
parameter_list|,
name|ActionRequest
name|request
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
assert|assert
literal|false
operator|:
literal|"response filter chain should never be called on the request side"
assert|;
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|proceed
specifier|public
name|void
name|proceed
parameter_list|(
name|String
name|action
parameter_list|,
name|ActionResponse
name|response
parameter_list|,
name|ActionListener
name|listener
parameter_list|)
block|{
name|int
name|i
init|=
name|index
operator|.
name|decrementAndGet
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
name|filters
index|[
name|i
index|]
operator|.
name|apply
argument_list|(
name|action
argument_list|,
name|response
argument_list|,
name|listener
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
operator|-
literal|1
condition|)
block|{
name|listener
operator|.
name|onResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|IllegalStateException
argument_list|(
literal|"proceed was called too many times"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|logger
operator|.
name|trace
argument_list|(
literal|"Error during transport action execution."
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|listener
operator|.
name|onFailure
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|FilteredActionListener
specifier|private
specifier|static
class|class
name|FilteredActionListener
parameter_list|<
name|Response
extends|extends
name|ActionResponse
parameter_list|>
implements|implements
name|ActionListener
argument_list|<
name|Response
argument_list|>
block|{
DECL|field|actionName
specifier|private
specifier|final
name|String
name|actionName
decl_stmt|;
DECL|field|listener
specifier|private
specifier|final
name|ActionListener
name|listener
decl_stmt|;
DECL|field|chain
specifier|private
specifier|final
name|ResponseFilterChain
name|chain
decl_stmt|;
DECL|method|FilteredActionListener
specifier|private
name|FilteredActionListener
parameter_list|(
name|String
name|actionName
parameter_list|,
name|ActionListener
name|listener
parameter_list|,
name|ResponseFilterChain
name|chain
parameter_list|)
block|{
name|this
operator|.
name|actionName
operator|=
name|actionName
expr_stmt|;
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|chain
operator|=
name|chain
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onResponse
specifier|public
name|void
name|onResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
name|chain
operator|.
name|proceed
argument_list|(
name|actionName
argument_list|,
name|response
argument_list|,
name|listener
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|onFailure
specifier|public
name|void
name|onFailure
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|listener
operator|.
name|onFailure
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

