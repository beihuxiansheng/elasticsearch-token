begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.shard
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|message
operator|.
name|ParameterizedMessage
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|util
operator|.
name|Supplier
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SearchContext
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * An listener for search, fetch and context events.  */
end_comment

begin_interface
DECL|interface|SearchOperationListener
specifier|public
interface|interface
name|SearchOperationListener
block|{
comment|/**      * Executed before the query phase is executed      * @param searchContext the current search context      */
DECL|method|onPreQueryPhase
specifier|default
name|void
name|onPreQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed if a query phased failed.      * @param searchContext the current search context      */
DECL|method|onFailedQueryPhase
specifier|default
name|void
name|onFailedQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed after the query phase successfully finished.      * Note: this is not invoked if the query phase execution failed.      * @param searchContext the current search context      * @param tookInNanos the number of nanoseconds the query execution took      *      * @see #onFailedQueryPhase(SearchContext)      */
DECL|method|onQueryPhase
specifier|default
name|void
name|onQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed before the fetch phase is executed      * @param searchContext the current search context      */
DECL|method|onPreFetchPhase
specifier|default
name|void
name|onPreFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed if a fetch phased failed.      * @param searchContext the current search context      */
DECL|method|onFailedFetchPhase
specifier|default
name|void
name|onFailedFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed after the fetch phase successfully finished.      * Note: this is not invoked if the fetch phase execution failed.      * @param searchContext the current search context      * @param tookInNanos the number of nanoseconds the fetch execution took      *      * @see #onFailedFetchPhase(SearchContext)      */
DECL|method|onFetchPhase
specifier|default
name|void
name|onFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed when a new search context was created      * @param context the created context      */
DECL|method|onNewContext
specifier|default
name|void
name|onNewContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed when a previously created search context is freed.      * This happens either when the search execution finishes, if the      * execution failed or if the search context as idle for and needs to be      * cleaned up.      * @param context the freed search context      */
DECL|method|onFreeContext
specifier|default
name|void
name|onFreeContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed when a new scroll search {@link SearchContext} was created      * @param context the created search context      */
DECL|method|onNewScrollContext
specifier|default
name|void
name|onNewScrollContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * Executed when a scroll search {@link SearchContext} is freed.      * This happens either when the scroll search execution finishes, if the      * execution failed or if the search context as idle for and needs to be      * cleaned up.      * @param context the freed search context      */
DECL|method|onFreeScrollContext
specifier|default
name|void
name|onFreeScrollContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{}
empty_stmt|;
comment|/**      * A Composite listener that multiplexes calls to each of the listeners methods.      */
DECL|class|CompositeListener
specifier|final
class|class
name|CompositeListener
implements|implements
name|SearchOperationListener
block|{
DECL|field|listeners
specifier|private
specifier|final
name|List
argument_list|<
name|SearchOperationListener
argument_list|>
name|listeners
decl_stmt|;
DECL|field|logger
specifier|private
specifier|final
name|Logger
name|logger
decl_stmt|;
DECL|method|CompositeListener
specifier|public
name|CompositeListener
parameter_list|(
name|List
argument_list|<
name|SearchOperationListener
argument_list|>
name|listeners
parameter_list|,
name|Logger
name|logger
parameter_list|)
block|{
name|this
operator|.
name|listeners
operator|=
name|listeners
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
DECL|method|onPreQueryPhase
specifier|public
name|void
name|onPreQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onPreQueryPhase
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onPreQueryPhase listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onFailedQueryPhase
specifier|public
name|void
name|onFailedQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onFailedQueryPhase
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onFailedQueryPhase listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onQueryPhase
specifier|public
name|void
name|onQueryPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onQueryPhase
argument_list|(
name|searchContext
argument_list|,
name|tookInNanos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onQueryPhase listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onPreFetchPhase
specifier|public
name|void
name|onPreFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onPreFetchPhase
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onPreFetchPhase listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onFailedFetchPhase
specifier|public
name|void
name|onFailedFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onFailedFetchPhase
argument_list|(
name|searchContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onFailedFetchPhase listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onFetchPhase
specifier|public
name|void
name|onFetchPhase
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|long
name|tookInNanos
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onFetchPhase
argument_list|(
name|searchContext
argument_list|,
name|tookInNanos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onFetchPhase listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onNewContext
specifier|public
name|void
name|onNewContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onNewContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onNewContext listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onFreeContext
specifier|public
name|void
name|onFreeContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onFreeContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onFreeContext listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onNewScrollContext
specifier|public
name|void
name|onNewScrollContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onNewScrollContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onNewScrollContext listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|onFreeScrollContext
specifier|public
name|void
name|onFreeScrollContext
parameter_list|(
name|SearchContext
name|context
parameter_list|)
block|{
for|for
control|(
name|SearchOperationListener
name|listener
range|:
name|listeners
control|)
block|{
try|try
block|{
name|listener
operator|.
name|onFreeScrollContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|warn
argument_list|(
call|(
name|Supplier
argument_list|<
name|?
argument_list|>
call|)
argument_list|()
operator|->
operator|new
name|ParameterizedMessage
argument_list|(
literal|"onFreeScrollContext listener [{}] failed"
argument_list|,
name|listener
argument_list|)
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_interface

end_unit
