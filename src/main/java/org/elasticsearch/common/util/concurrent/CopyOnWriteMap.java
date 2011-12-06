begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.util.concurrent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|util
operator|.
name|concurrent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|ConcurrentMap
import|;
end_import

begin_comment
comment|/**  * Provides the semantics of a thread safe copy on write map.  *  *  */
end_comment

begin_class
annotation|@
name|ThreadSafe
DECL|class|CopyOnWriteMap
specifier|public
class|class
name|CopyOnWriteMap
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
implements|implements
name|ConcurrentMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
block|{
DECL|field|map
specifier|private
specifier|volatile
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|clear
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|containsKey
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|containsValue
specifier|public
name|boolean
name|containsValue
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|map
operator|.
name|containsValue
argument_list|(
name|value
argument_list|)
return|;
block|}
DECL|method|entrySet
specifier|public
name|Set
argument_list|<
name|Entry
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|>
name|entrySet
parameter_list|()
block|{
return|return
name|map
operator|.
name|entrySet
argument_list|()
return|;
block|}
DECL|method|get
specifier|public
name|V
name|get
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
return|;
block|}
DECL|method|isEmpty
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|map
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|keySet
specifier|public
name|Set
argument_list|<
name|K
argument_list|>
name|keySet
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
return|;
block|}
DECL|method|put
specifier|public
name|V
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
init|=
name|copyMap
argument_list|()
decl_stmt|;
name|V
name|put
init|=
name|copyMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|map
operator|=
name|copyMap
expr_stmt|;
return|return
name|put
return|;
block|}
block|}
DECL|method|putAll
specifier|public
specifier|synchronized
name|void
name|putAll
parameter_list|(
name|Map
argument_list|<
name|?
extends|extends
name|K
argument_list|,
name|?
extends|extends
name|V
argument_list|>
name|t
parameter_list|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
init|=
name|copyMap
argument_list|()
decl_stmt|;
name|copyMap
operator|.
name|putAll
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|map
operator|=
name|copyMap
expr_stmt|;
block|}
DECL|method|remove
specifier|public
specifier|synchronized
name|V
name|remove
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
init|=
name|copyMap
argument_list|()
decl_stmt|;
name|V
name|remove
init|=
name|copyMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|map
operator|=
name|copyMap
expr_stmt|;
return|return
name|remove
return|;
block|}
DECL|method|size
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|map
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|values
specifier|public
name|Collection
argument_list|<
name|V
argument_list|>
name|values
parameter_list|()
block|{
return|return
name|map
operator|.
name|values
argument_list|()
return|;
block|}
DECL|method|copyMap
specifier|private
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
parameter_list|()
block|{
return|return
operator|new
name|HashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|(
name|map
argument_list|)
return|;
block|}
DECL|method|putIfAbsent
specifier|public
specifier|synchronized
name|V
name|putIfAbsent
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|V
name|v
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|==
literal|null
condition|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
init|=
name|copyMap
argument_list|()
decl_stmt|;
name|copyMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|map
operator|=
name|copyMap
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
DECL|method|remove
specifier|public
specifier|synchronized
name|boolean
name|remove
parameter_list|(
name|Object
name|key
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
name|V
name|v
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
init|=
name|copyMap
argument_list|()
decl_stmt|;
name|copyMap
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|map
operator|=
name|copyMap
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|replace
specifier|public
specifier|synchronized
name|V
name|replace
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
name|V
name|v
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
init|=
name|copyMap
argument_list|()
decl_stmt|;
name|copyMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|map
operator|=
name|copyMap
expr_stmt|;
block|}
return|return
name|v
return|;
block|}
DECL|method|replace
specifier|public
specifier|synchronized
name|boolean
name|replace
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|oldValue
parameter_list|,
name|V
name|newValue
parameter_list|)
block|{
name|V
name|v
init|=
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|v
operator|!=
literal|null
operator|&&
name|v
operator|.
name|equals
argument_list|(
name|oldValue
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|copyMap
init|=
name|copyMap
argument_list|()
decl_stmt|;
name|copyMap
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|newValue
argument_list|)
expr_stmt|;
name|map
operator|=
name|copyMap
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

