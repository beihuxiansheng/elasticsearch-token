begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
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
name|IndexCommit
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
name|store
operator|.
name|Directory
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
name|Collection
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

begin_comment
comment|/**  * A simple delegate that delegates all {@link IndexCommit} calls to a delegated  * {@link IndexCommit}.  *  *  */
end_comment

begin_class
DECL|class|IndexCommitDelegate
specifier|public
specifier|abstract
class|class
name|IndexCommitDelegate
extends|extends
name|IndexCommit
block|{
DECL|field|delegate
specifier|protected
specifier|final
name|IndexCommit
name|delegate
decl_stmt|;
comment|/**      * Constructs a new {@link IndexCommit} that will delegate all calls      * to the provided delegate.      *      * @param delegate The delegate      */
DECL|method|IndexCommitDelegate
specifier|public
name|IndexCommitDelegate
parameter_list|(
name|IndexCommit
name|delegate
parameter_list|)
block|{
name|this
operator|.
name|delegate
operator|=
name|delegate
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getSegmentsFileName
specifier|public
name|String
name|getSegmentsFileName
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSegmentsFileName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getFileNames
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getFileNames
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getFileNames
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getDirectory
specifier|public
name|Directory
name|getDirectory
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getDirectory
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|delete
specifier|public
name|void
name|delete
parameter_list|()
block|{
name|delegate
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isDeleted
specifier|public
name|boolean
name|isDeleted
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|isDeleted
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getSegmentCount
specifier|public
name|int
name|getSegmentCount
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getSegmentCount
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
return|return
name|delegate
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getGeneration
specifier|public
name|long
name|getGeneration
parameter_list|()
block|{
return|return
name|delegate
operator|.
name|getGeneration
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getUserData
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|getUserData
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|delegate
operator|.
name|getUserData
argument_list|()
return|;
block|}
block|}
end_class

end_unit

