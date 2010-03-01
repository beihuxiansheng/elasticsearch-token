begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.json
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|json
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|mapper
operator|.
name|DocumentMapper
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|JsonMergeContext
specifier|public
class|class
name|JsonMergeContext
block|{
DECL|field|documentMapper
specifier|private
specifier|final
name|JsonDocumentMapper
name|documentMapper
decl_stmt|;
DECL|field|mergeFlags
specifier|private
specifier|final
name|DocumentMapper
operator|.
name|MergeFlags
name|mergeFlags
decl_stmt|;
DECL|field|mergeConflicts
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|mergeConflicts
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
DECL|method|JsonMergeContext
specifier|public
name|JsonMergeContext
parameter_list|(
name|JsonDocumentMapper
name|documentMapper
parameter_list|,
name|DocumentMapper
operator|.
name|MergeFlags
name|mergeFlags
parameter_list|)
block|{
name|this
operator|.
name|documentMapper
operator|=
name|documentMapper
expr_stmt|;
name|this
operator|.
name|mergeFlags
operator|=
name|mergeFlags
expr_stmt|;
block|}
DECL|method|docMapper
specifier|public
name|JsonDocumentMapper
name|docMapper
parameter_list|()
block|{
return|return
name|documentMapper
return|;
block|}
DECL|method|mergeFlags
specifier|public
name|DocumentMapper
operator|.
name|MergeFlags
name|mergeFlags
parameter_list|()
block|{
return|return
name|mergeFlags
return|;
block|}
DECL|method|addConflict
specifier|public
name|void
name|addConflict
parameter_list|(
name|String
name|mergeFailure
parameter_list|)
block|{
name|mergeConflicts
operator|.
name|add
argument_list|(
name|mergeFailure
argument_list|)
expr_stmt|;
block|}
DECL|method|hasConflicts
specifier|public
name|boolean
name|hasConflicts
parameter_list|()
block|{
return|return
operator|!
name|mergeConflicts
operator|.
name|isEmpty
argument_list|()
return|;
block|}
DECL|method|buildConflicts
specifier|public
name|String
index|[]
name|buildConflicts
parameter_list|()
block|{
return|return
name|mergeConflicts
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|mergeConflicts
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
end_class

end_unit

