begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.merge
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|merge
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
name|index
operator|.
name|IndexWriter
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
name|index
operator|.
name|MergeScheduler
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

begin_comment
comment|/**  * A wrapper of another {@link org.apache.lucene.index.MergeScheduler} that allows  * to explicitly enable merge and disable on a thread local basis. The default is  * to have merges disabled.  *<p/>  * This merge scheduler can be used to get around the fact that even though a merge  * policy can control that no new merges will be created as a result of a segment flush  * (during indexing operation for example), the {@link #merge(org.apache.lucene.index.IndexWriter)}  * call will still be called, and can result in stalling indexing.  */
end_comment

begin_class
DECL|class|EnableMergeScheduler
specifier|public
class|class
name|EnableMergeScheduler
extends|extends
name|MergeScheduler
block|{
DECL|field|mergeScheduler
specifier|private
specifier|final
name|MergeScheduler
name|mergeScheduler
decl_stmt|;
DECL|field|enabled
specifier|private
specifier|final
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
name|enabled
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|Boolean
name|initialValue
parameter_list|()
block|{
return|return
name|Boolean
operator|.
name|FALSE
return|;
block|}
block|}
decl_stmt|;
DECL|method|EnableMergeScheduler
specifier|public
name|EnableMergeScheduler
parameter_list|(
name|MergeScheduler
name|mergeScheduler
parameter_list|)
block|{
name|this
operator|.
name|mergeScheduler
operator|=
name|mergeScheduler
expr_stmt|;
block|}
comment|/**      * Enable merges on the current thread.      */
DECL|method|enableMerge
name|void
name|enableMerge
parameter_list|()
block|{
assert|assert
operator|!
name|enabled
operator|.
name|get
argument_list|()
assert|;
name|enabled
operator|.
name|set
argument_list|(
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Disable merges on the current thread.      */
DECL|method|disableMerge
name|void
name|disableMerge
parameter_list|()
block|{
assert|assert
name|enabled
operator|.
name|get
argument_list|()
assert|;
name|enabled
operator|.
name|set
argument_list|(
name|Boolean
operator|.
name|FALSE
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|merge
specifier|public
name|void
name|merge
parameter_list|(
name|IndexWriter
name|writer
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|enabled
operator|.
name|get
argument_list|()
condition|)
block|{
name|mergeScheduler
operator|.
name|merge
argument_list|(
name|writer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|mergeScheduler
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|clone
specifier|public
name|MergeScheduler
name|clone
parameter_list|()
block|{
comment|// Lucene IW makes a clone internally but since we hold on to this instance
comment|// the clone will just be the identity.
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

