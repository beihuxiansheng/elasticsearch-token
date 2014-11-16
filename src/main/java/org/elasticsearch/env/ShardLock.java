begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.env
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|env
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|store
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IOUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|shard
operator|.
name|ShardId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|Arrays
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
name|AtomicBoolean
import|;
end_import

begin_comment
comment|/**  * A shard lock guarantees exclusive access to a shards data  * directory. Internal processes should acquire a lock on a shard  * before executing any write operations on the shards data directory.  *  * @see org.elasticsearch.env.NodeEnvironment  */
end_comment

begin_class
DECL|class|ShardLock
specifier|public
specifier|abstract
class|class
name|ShardLock
implements|implements
name|Closeable
block|{
DECL|field|shardId
specifier|private
specifier|final
name|ShardId
name|shardId
decl_stmt|;
DECL|field|closed
specifier|private
specifier|final
name|AtomicBoolean
name|closed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|method|ShardLock
specifier|public
name|ShardLock
parameter_list|(
name|ShardId
name|id
parameter_list|)
block|{
name|this
operator|.
name|shardId
operator|=
name|id
expr_stmt|;
block|}
comment|/**      * Returns the locks shards Id.      */
DECL|method|getShardId
specifier|public
specifier|final
name|ShardId
name|getShardId
parameter_list|()
block|{
return|return
name|shardId
return|;
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
specifier|final
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|closed
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
condition|)
block|{
name|closeInternal
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|closeInternal
specifier|protected
specifier|abstract
name|void
name|closeInternal
parameter_list|()
function_decl|;
comment|/**      * Returns true if this lock is still open ie. has not been closed yet.      */
DECL|method|isOpen
specifier|public
specifier|final
name|boolean
name|isOpen
parameter_list|()
block|{
return|return
name|closed
operator|.
name|get
argument_list|()
operator|==
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ShardLock{"
operator|+
literal|"shardId="
operator|+
name|shardId
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

