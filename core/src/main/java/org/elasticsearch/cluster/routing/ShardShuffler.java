begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.routing
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|routing
package|;
end_package

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
comment|/**  * A shuffler for shards whose primary goal is to balance load.  */
end_comment

begin_class
DECL|class|ShardShuffler
specifier|public
specifier|abstract
class|class
name|ShardShuffler
block|{
comment|/**      * Return a new seed.      */
DECL|method|nextSeed
specifier|public
specifier|abstract
name|int
name|nextSeed
parameter_list|()
function_decl|;
comment|/**      * Return a shuffled view over the list of shards. The behavior of this method must be deterministic: if the same list and the same seed      * are provided twice, then the result needs to be the same.      */
DECL|method|shuffle
specifier|public
specifier|abstract
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shuffle
parameter_list|(
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
parameter_list|,
name|int
name|seed
parameter_list|)
function_decl|;
comment|/**      * Equivalent to calling<code>shuffle(shards, nextSeed())</code>.      */
DECL|method|shuffle
specifier|public
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shuffle
parameter_list|(
name|List
argument_list|<
name|ShardRouting
argument_list|>
name|shards
parameter_list|)
block|{
return|return
name|shuffle
argument_list|(
name|shards
argument_list|,
name|nextSeed
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

