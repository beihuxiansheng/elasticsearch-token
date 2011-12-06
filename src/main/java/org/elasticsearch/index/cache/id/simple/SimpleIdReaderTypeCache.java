begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.cache.id.simple
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|cache
operator|.
name|id
operator|.
name|simple
package|;
end_package

begin_import
import|import
name|gnu
operator|.
name|trove
operator|.
name|ExtTObjectIntHasMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|BytesWrap
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
name|cache
operator|.
name|id
operator|.
name|IdReaderTypeCache
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|SimpleIdReaderTypeCache
specifier|public
class|class
name|SimpleIdReaderTypeCache
implements|implements
name|IdReaderTypeCache
block|{
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|idToDoc
specifier|private
specifier|final
name|ExtTObjectIntHasMap
argument_list|<
name|BytesWrap
argument_list|>
name|idToDoc
decl_stmt|;
DECL|field|parentIdsValues
specifier|private
specifier|final
name|BytesWrap
index|[]
name|parentIdsValues
decl_stmt|;
DECL|field|parentIdsOrdinals
specifier|private
specifier|final
name|int
index|[]
name|parentIdsOrdinals
decl_stmt|;
DECL|method|SimpleIdReaderTypeCache
specifier|public
name|SimpleIdReaderTypeCache
parameter_list|(
name|String
name|type
parameter_list|,
name|ExtTObjectIntHasMap
argument_list|<
name|BytesWrap
argument_list|>
name|idToDoc
parameter_list|,
name|BytesWrap
index|[]
name|parentIdsValues
parameter_list|,
name|int
index|[]
name|parentIdsOrdinals
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|idToDoc
operator|=
name|idToDoc
expr_stmt|;
name|this
operator|.
name|idToDoc
operator|.
name|trimToSize
argument_list|()
expr_stmt|;
name|this
operator|.
name|parentIdsValues
operator|=
name|parentIdsValues
expr_stmt|;
name|this
operator|.
name|parentIdsOrdinals
operator|=
name|parentIdsOrdinals
expr_stmt|;
block|}
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
DECL|method|parentIdByDoc
specifier|public
name|BytesWrap
name|parentIdByDoc
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|parentIdsValues
index|[
name|parentIdsOrdinals
index|[
name|docId
index|]
index|]
return|;
block|}
DECL|method|docById
specifier|public
name|int
name|docById
parameter_list|(
name|BytesWrap
name|id
parameter_list|)
block|{
return|return
name|idToDoc
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
comment|/**      * Returns an already stored instance if exists, if not, returns null;      */
DECL|method|canReuse
specifier|public
name|BytesWrap
name|canReuse
parameter_list|(
name|BytesWrap
name|id
parameter_list|)
block|{
return|return
name|idToDoc
operator|.
name|key
argument_list|(
name|id
argument_list|)
return|;
block|}
block|}
end_class

end_unit

