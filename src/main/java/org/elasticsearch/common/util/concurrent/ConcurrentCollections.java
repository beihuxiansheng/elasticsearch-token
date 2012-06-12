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
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|collect
operator|.
name|MapBackedSet
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
name|ConcurrentHashMap
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
comment|/**  *  */
end_comment

begin_class
DECL|class|ConcurrentCollections
specifier|public
specifier|abstract
class|class
name|ConcurrentCollections
block|{
DECL|field|useNonBlockingMap
specifier|private
specifier|final
specifier|static
name|boolean
name|useNonBlockingMap
init|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"elasticsearch.useNonBlockingMap"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
decl_stmt|;
DECL|method|newConcurrentMap
specifier|public
specifier|static
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|ConcurrentMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|newConcurrentMap
parameter_list|()
block|{
comment|//        if (useNonBlockingMap) {
comment|//            return new NonBlockingHashMap<K, V>();
comment|//        }
return|return
operator|new
name|ConcurrentHashMap
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
argument_list|()
return|;
block|}
DECL|method|newConcurrentMapLong
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|ConcurrentMapLong
argument_list|<
name|V
argument_list|>
name|newConcurrentMapLong
parameter_list|()
block|{
comment|//        if (useNonBlockingMap) {
comment|//            return new NonBlockingHashMapLong<V>();
comment|//        }
return|return
operator|new
name|ConcurrentHashMapLong
argument_list|<
name|V
argument_list|>
argument_list|()
return|;
block|}
DECL|method|newConcurrentSet
specifier|public
specifier|static
parameter_list|<
name|V
parameter_list|>
name|Set
argument_list|<
name|V
argument_list|>
name|newConcurrentSet
parameter_list|()
block|{
comment|//        if (useNonBlockingMap) {
comment|//            return new NonBlockingHashSet<V>();
comment|//        }
return|return
operator|new
name|MapBackedSet
argument_list|<
name|V
argument_list|>
argument_list|(
operator|new
name|ConcurrentHashMap
argument_list|<
name|V
argument_list|,
name|Boolean
argument_list|>
argument_list|()
argument_list|)
return|;
block|}
DECL|method|ConcurrentCollections
specifier|private
name|ConcurrentCollections
parameter_list|()
block|{      }
block|}
end_class

end_unit

