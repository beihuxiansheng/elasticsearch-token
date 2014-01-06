begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.codec.postingsformat
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|codec
operator|.
name|postingsformat
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
name|codecs
operator|.
name|FieldsConsumer
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
name|codecs
operator|.
name|FieldsProducer
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
name|codecs
operator|.
name|PostingsFormat
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
name|codecs
operator|.
name|TermsConsumer
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
name|codecs
operator|.
name|lucene41
operator|.
name|Lucene41PostingsFormat
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
name|FieldInfo
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
name|SegmentReadState
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
name|SegmentWriteState
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
name|util
operator|.
name|BloomFilter
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
name|codec
operator|.
name|postingsformat
operator|.
name|BloomFilterPostingsFormat
operator|.
name|BloomFilteredFieldsConsumer
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
name|internal
operator|.
name|UidFieldMapper
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
comment|/**  * This is the default postings format for Elasticsearch that special cases  * the<tt>_uid</tt> field to use a bloom filter while all other fields  * will use a {@link Lucene41PostingsFormat}. This format will reuse the underlying  * {@link Lucene41PostingsFormat} and its files also for the<tt>_uid</tt> saving up to  * 5 files per segment in the default case.  */
end_comment

begin_class
DECL|class|Elasticsearch090PostingsFormat
specifier|public
specifier|final
class|class
name|Elasticsearch090PostingsFormat
extends|extends
name|PostingsFormat
block|{
DECL|field|bloomPostings
specifier|private
specifier|final
name|BloomFilterPostingsFormat
name|bloomPostings
decl_stmt|;
DECL|method|Elasticsearch090PostingsFormat
specifier|public
name|Elasticsearch090PostingsFormat
parameter_list|()
block|{
name|super
argument_list|(
literal|"es090"
argument_list|)
expr_stmt|;
name|bloomPostings
operator|=
operator|new
name|BloomFilterPostingsFormat
argument_list|(
operator|new
name|Lucene41PostingsFormat
argument_list|()
argument_list|,
name|BloomFilter
operator|.
name|Factory
operator|.
name|DEFAULT
argument_list|)
expr_stmt|;
block|}
DECL|method|getDefaultWrapped
specifier|public
name|PostingsFormat
name|getDefaultWrapped
parameter_list|()
block|{
return|return
name|bloomPostings
operator|.
name|getDelegate
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsConsumer
specifier|public
name|FieldsConsumer
name|fieldsConsumer
parameter_list|(
name|SegmentWriteState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BloomFilteredFieldsConsumer
name|fieldsConsumer
init|=
name|bloomPostings
operator|.
name|fieldsConsumer
argument_list|(
name|state
argument_list|)
decl_stmt|;
return|return
operator|new
name|FieldsConsumer
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|fieldsConsumer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|TermsConsumer
name|addField
parameter_list|(
name|FieldInfo
name|field
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|UidFieldMapper
operator|.
name|NAME
operator|.
name|equals
argument_list|(
name|field
operator|.
name|name
argument_list|)
condition|)
block|{
comment|// only go through bloom for the UID field
return|return
name|fieldsConsumer
operator|.
name|addField
argument_list|(
name|field
argument_list|)
return|;
block|}
return|return
name|fieldsConsumer
operator|.
name|getDelegate
argument_list|()
operator|.
name|addField
argument_list|(
name|field
argument_list|)
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|fieldsProducer
specifier|public
name|FieldsProducer
name|fieldsProducer
parameter_list|(
name|SegmentReadState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
comment|// we can just return the delegate here since we didn't record bloom filters for
comment|// the other fields.
return|return
name|bloomPostings
operator|.
name|fieldsProducer
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
end_class

end_unit

