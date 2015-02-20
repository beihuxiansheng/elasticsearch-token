begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
import|;
end_import

begin_comment
comment|/**  * A very simple single object cache that allows non-blocking refresh calls  * triggered by expiry time.  */
end_comment

begin_class
DECL|class|SingleObjectCache
specifier|public
specifier|abstract
class|class
name|SingleObjectCache
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|cached
specifier|private
specifier|volatile
name|T
name|cached
decl_stmt|;
DECL|field|refreshLock
specifier|private
name|Lock
name|refreshLock
init|=
operator|new
name|ReentrantLock
argument_list|()
decl_stmt|;
DECL|field|refreshInterval
specifier|private
specifier|final
name|TimeValue
name|refreshInterval
decl_stmt|;
DECL|field|lastRefreshTimestamp
specifier|protected
name|long
name|lastRefreshTimestamp
init|=
literal|0
decl_stmt|;
DECL|method|SingleObjectCache
specifier|protected
name|SingleObjectCache
parameter_list|(
name|TimeValue
name|refreshInterval
parameter_list|,
name|T
name|initialValue
parameter_list|)
block|{
if|if
condition|(
name|initialValue
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"initialValue must not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|refreshInterval
operator|=
name|refreshInterval
expr_stmt|;
name|cached
operator|=
name|initialValue
expr_stmt|;
block|}
comment|/**      * Returns the currently cached object and potentially refreshes the cache before returning.      */
DECL|method|getOrRefresh
specifier|public
name|T
name|getOrRefresh
parameter_list|()
block|{
if|if
condition|(
name|needsRefresh
argument_list|()
condition|)
block|{
if|if
condition|(
name|refreshLock
operator|.
name|tryLock
argument_list|()
condition|)
block|{
try|try
block|{
if|if
condition|(
name|needsRefresh
argument_list|()
condition|)
block|{
comment|// check again!
name|cached
operator|=
name|refresh
argument_list|()
expr_stmt|;
assert|assert
name|cached
operator|!=
literal|null
assert|;
name|lastRefreshTimestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|refreshLock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
assert|assert
name|cached
operator|!=
literal|null
assert|;
return|return
name|cached
return|;
block|}
comment|/**      * Returns a new instance to cache      */
DECL|method|refresh
specifier|protected
specifier|abstract
name|T
name|refresh
parameter_list|()
function_decl|;
comment|/**      * Returns<code>true</code> iff the cache needs to be refreshed.      */
DECL|method|needsRefresh
specifier|protected
name|boolean
name|needsRefresh
parameter_list|()
block|{
if|if
condition|(
name|refreshInterval
operator|.
name|millis
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|long
name|currentTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
return|return
operator|(
name|currentTime
operator|-
name|lastRefreshTimestamp
operator|)
operator|>
name|refreshInterval
operator|.
name|millis
argument_list|()
return|;
block|}
block|}
end_class

end_unit
