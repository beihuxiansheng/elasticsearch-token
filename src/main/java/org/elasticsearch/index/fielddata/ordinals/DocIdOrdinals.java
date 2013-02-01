begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.ordinals
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|ordinals
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
name|RamUsage
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
name|fielddata
operator|.
name|util
operator|.
name|IntArrayRef
import|;
end_import

begin_comment
comment|/**  * Ordinals that effectively are single valued and map "one to one" to the  * doc ids. Note, the docId is incremented by 1 to get the ordinal, since 0  * denotes an empty value.  */
end_comment

begin_class
DECL|class|DocIdOrdinals
specifier|public
class|class
name|DocIdOrdinals
implements|implements
name|Ordinals
block|{
DECL|field|numDocs
specifier|private
specifier|final
name|int
name|numDocs
decl_stmt|;
comment|/**      * Constructs a new doc id ordinals.      */
DECL|method|DocIdOrdinals
specifier|public
name|DocIdOrdinals
parameter_list|(
name|int
name|numDocs
parameter_list|)
block|{
name|this
operator|.
name|numDocs
operator|=
name|numDocs
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hasSingleArrayBackingStorage
specifier|public
name|boolean
name|hasSingleArrayBackingStorage
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getBackingStorage
specifier|public
name|Object
name|getBackingStorage
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getMemorySizeInBytes
specifier|public
name|long
name|getMemorySizeInBytes
parameter_list|()
block|{
return|return
name|RamUsage
operator|.
name|NUM_BYTES_OBJECT_REF
return|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|getNumOrds
specifier|public
name|int
name|getNumOrds
parameter_list|()
block|{
return|return
name|numDocs
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
name|int
name|getMaxOrd
parameter_list|()
block|{
return|return
name|numDocs
operator|+
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|ordinals
specifier|public
name|Ordinals
operator|.
name|Docs
name|ordinals
parameter_list|()
block|{
return|return
operator|new
name|Docs
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|class|Docs
specifier|public
specifier|static
class|class
name|Docs
implements|implements
name|Ordinals
operator|.
name|Docs
block|{
DECL|field|parent
specifier|private
specifier|final
name|DocIdOrdinals
name|parent
decl_stmt|;
DECL|field|intsScratch
specifier|private
specifier|final
name|IntArrayRef
name|intsScratch
init|=
operator|new
name|IntArrayRef
argument_list|(
operator|new
name|int
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
DECL|field|iter
specifier|private
specifier|final
name|SingleValueIter
name|iter
init|=
operator|new
name|SingleValueIter
argument_list|()
decl_stmt|;
DECL|method|Docs
specifier|public
name|Docs
parameter_list|(
name|DocIdOrdinals
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|ordinals
specifier|public
name|Ordinals
name|ordinals
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
annotation|@
name|Override
DECL|method|getNumDocs
specifier|public
name|int
name|getNumDocs
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getNumDocs
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumOrds
specifier|public
name|int
name|getNumOrds
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getNumOrds
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxOrd
specifier|public
name|int
name|getMaxOrd
parameter_list|()
block|{
return|return
name|parent
operator|.
name|getMaxOrd
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isMultiValued
specifier|public
name|boolean
name|isMultiValued
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|getOrd
specifier|public
name|int
name|getOrd
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|docId
operator|+
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getOrds
specifier|public
name|IntArrayRef
name|getOrds
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
name|intsScratch
operator|.
name|values
index|[
literal|0
index|]
operator|=
name|docId
operator|+
literal|1
expr_stmt|;
return|return
name|intsScratch
return|;
block|}
annotation|@
name|Override
DECL|method|getIter
specifier|public
name|Iter
name|getIter
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
name|iter
operator|.
name|reset
argument_list|(
name|docId
operator|+
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|forEachOrdinalInDoc
specifier|public
name|void
name|forEachOrdinalInDoc
parameter_list|(
name|int
name|docId
parameter_list|,
name|OrdinalInDocProc
name|proc
parameter_list|)
block|{
name|proc
operator|.
name|onOrdinal
argument_list|(
name|docId
argument_list|,
name|docId
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

