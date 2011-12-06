begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.lucene.docset
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|lucene
operator|.
name|docset
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
name|search
operator|.
name|DocIdSetIterator
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
name|FixedBitSet
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
name|RamUsage
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
comment|/**  *  */
end_comment

begin_class
DECL|class|FixedBitDocSet
specifier|public
class|class
name|FixedBitDocSet
extends|extends
name|DocSet
block|{
DECL|field|set
specifier|private
specifier|final
name|FixedBitSet
name|set
decl_stmt|;
DECL|method|FixedBitDocSet
specifier|public
name|FixedBitDocSet
parameter_list|(
name|FixedBitSet
name|set
parameter_list|)
block|{
name|this
operator|.
name|set
operator|=
name|set
expr_stmt|;
block|}
DECL|method|FixedBitDocSet
specifier|public
name|FixedBitDocSet
parameter_list|(
name|int
name|numBits
parameter_list|)
block|{
name|this
operator|.
name|set
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|numBits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isCacheable
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|length
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|set
operator|.
name|length
argument_list|()
return|;
block|}
DECL|method|set
specifier|public
name|FixedBitSet
name|set
parameter_list|()
block|{
return|return
name|set
return|;
block|}
annotation|@
name|Override
DECL|method|get
specifier|public
name|boolean
name|get
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
return|return
name|set
operator|.
name|get
argument_list|(
name|doc
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|DocIdSetIterator
name|iterator
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|set
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|sizeInBytes
specifier|public
name|long
name|sizeInBytes
parameter_list|()
block|{
return|return
name|set
operator|.
name|getBits
argument_list|()
operator|.
name|length
operator|*
name|RamUsage
operator|.
name|NUM_BYTES_LONG
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_ARRAY_HEADER
operator|+
name|RamUsage
operator|.
name|NUM_BYTES_INT
comment|/* wlen */
return|;
block|}
block|}
end_class

end_unit

