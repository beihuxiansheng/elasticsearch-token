begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.cache
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|cache
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
name|util
operator|.
name|Objects
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|ToLongBiFunction
import|;
end_import

begin_class
DECL|class|CacheBuilder
specifier|public
class|class
name|CacheBuilder
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
DECL|field|maximumWeight
specifier|private
name|long
name|maximumWeight
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|expireAfterAccessNanos
specifier|private
name|long
name|expireAfterAccessNanos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|expireAfterWriteNanos
specifier|private
name|long
name|expireAfterWriteNanos
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|weigher
specifier|private
name|ToLongBiFunction
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|weigher
decl_stmt|;
DECL|field|removalListener
specifier|private
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|removalListener
decl_stmt|;
DECL|method|builder
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|builder
parameter_list|()
block|{
return|return
operator|new
name|CacheBuilder
argument_list|<>
argument_list|()
return|;
block|}
DECL|method|CacheBuilder
specifier|private
name|CacheBuilder
parameter_list|()
block|{     }
DECL|method|setMaximumWeight
specifier|public
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|setMaximumWeight
parameter_list|(
name|long
name|maximumWeight
parameter_list|)
block|{
if|if
condition|(
name|maximumWeight
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maximumWeight< 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maximumWeight
operator|=
name|maximumWeight
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the amount of time before an entry in the cache expires after it was last accessed.      *      * @param expireAfterAccess The amount of time before an entry expires after it was last accessed. Must not be {@code null} and must      *                          be greater than 0.      */
DECL|method|setExpireAfterAccess
specifier|public
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|setExpireAfterAccess
parameter_list|(
name|TimeValue
name|expireAfterAccess
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|expireAfterAccess
argument_list|)
expr_stmt|;
specifier|final
name|long
name|expireAfterAccessNanos
init|=
name|expireAfterAccess
operator|.
name|getNanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|expireAfterAccessNanos
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"expireAfterAccess<= 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|expireAfterAccessNanos
operator|=
name|expireAfterAccessNanos
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets the amount of time before an entry in the cache expires after it was written.      *      * @param expireAfterWrite The amount of time before an entry expires after it was written. Must not be {@code null} and must be      *                         greater than 0.      */
DECL|method|setExpireAfterWrite
specifier|public
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|setExpireAfterWrite
parameter_list|(
name|TimeValue
name|expireAfterWrite
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|expireAfterWrite
argument_list|)
expr_stmt|;
specifier|final
name|long
name|expireAfterWriteNanos
init|=
name|expireAfterWrite
operator|.
name|getNanos
argument_list|()
decl_stmt|;
if|if
condition|(
name|expireAfterWriteNanos
operator|<=
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"expireAfterWrite<= 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|expireAfterWriteNanos
operator|=
name|expireAfterWriteNanos
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|weigher
specifier|public
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|weigher
parameter_list|(
name|ToLongBiFunction
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|weigher
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|weigher
argument_list|)
expr_stmt|;
name|this
operator|.
name|weigher
operator|=
name|weigher
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|removalListener
specifier|public
name|CacheBuilder
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|removalListener
parameter_list|(
name|RemovalListener
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|removalListener
parameter_list|)
block|{
name|Objects
operator|.
name|requireNonNull
argument_list|(
name|removalListener
argument_list|)
expr_stmt|;
name|this
operator|.
name|removalListener
operator|=
name|removalListener
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build
specifier|public
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|build
parameter_list|()
block|{
name|Cache
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|cache
init|=
operator|new
name|Cache
argument_list|()
decl_stmt|;
if|if
condition|(
name|maximumWeight
operator|!=
operator|-
literal|1
condition|)
block|{
name|cache
operator|.
name|setMaximumWeight
argument_list|(
name|maximumWeight
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expireAfterAccessNanos
operator|!=
operator|-
literal|1
condition|)
block|{
name|cache
operator|.
name|setExpireAfterAccessNanos
argument_list|(
name|expireAfterAccessNanos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expireAfterWriteNanos
operator|!=
operator|-
literal|1
condition|)
block|{
name|cache
operator|.
name|setExpireAfterWriteNanos
argument_list|(
name|expireAfterWriteNanos
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|weigher
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|setWeigher
argument_list|(
name|weigher
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|removalListener
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|setRemovalListener
argument_list|(
name|removalListener
argument_list|)
expr_stmt|;
block|}
return|return
name|cache
return|;
block|}
block|}
end_class

end_unit

