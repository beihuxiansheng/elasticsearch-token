begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|xcontent
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
name|document
operator|.
name|Field
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
name|document
operator|.
name|Fieldable
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
name|Term
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
name|lucene
operator|.
name|Lucene
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
name|xcontent
operator|.
name|builder
operator|.
name|XContentBuilder
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
name|MapperParsingException
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
name|MergeMappingException
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
name|Uid
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|XContentUidFieldMapper
specifier|public
class|class
name|XContentUidFieldMapper
extends|extends
name|XContentFieldMapper
argument_list|<
name|Uid
argument_list|>
implements|implements
name|UidFieldMapper
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_uid"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|XContentFieldMapper
operator|.
name|Defaults
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
name|UidFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|INDEX
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Index
name|INDEX
init|=
name|Field
operator|.
name|Index
operator|.
name|NOT_ANALYZED
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_NORMS
init|=
literal|true
decl_stmt|;
DECL|field|OMIT_TERM_FREQ_AND_POSITIONS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_TERM_FREQ_AND_POSITIONS
init|=
literal|true
decl_stmt|;
block|}
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
extends|extends
name|XContentMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|XContentUidFieldMapper
argument_list|>
block|{
DECL|field|indexName
specifier|protected
name|String
name|indexName
decl_stmt|;
DECL|method|Builder
specifier|public
name|Builder
parameter_list|()
block|{
name|super
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexName
operator|=
name|name
expr_stmt|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|XContentUidFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|XContentUidFieldMapper
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|)
return|;
block|}
block|}
DECL|method|XContentUidFieldMapper
specifier|protected
name|XContentUidFieldMapper
parameter_list|()
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|XContentUidFieldMapper
specifier|protected
name|XContentUidFieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|XContentUidFieldMapper
specifier|protected
name|XContentUidFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Names
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|indexName
argument_list|,
name|name
argument_list|)
argument_list|,
name|Defaults
operator|.
name|INDEX
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|YES
argument_list|,
name|Defaults
operator|.
name|TERM_VECTOR
argument_list|,
name|Defaults
operator|.
name|BOOST
argument_list|,
name|Defaults
operator|.
name|OMIT_NORMS
argument_list|,
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|,
name|Lucene
operator|.
name|KEYWORD_ANALYZER
argument_list|)
expr_stmt|;
block|}
DECL|method|parseCreateField
annotation|@
name|Override
specifier|protected
name|Field
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|context
operator|.
name|id
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"No id found while parsing the content source"
argument_list|)
throw|;
block|}
name|context
operator|.
name|uid
argument_list|(
name|Uid
operator|.
name|createUid
argument_list|(
name|context
operator|.
name|stringBuilder
argument_list|()
argument_list|,
name|context
operator|.
name|type
argument_list|()
argument_list|,
name|context
operator|.
name|id
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|new
name|Field
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|context
operator|.
name|uid
argument_list|()
argument_list|,
name|store
argument_list|,
name|index
argument_list|)
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|Uid
name|value
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|Uid
operator|.
name|createUid
argument_list|(
name|field
operator|.
name|stringValue
argument_list|()
argument_list|)
return|;
block|}
DECL|method|valueAsString
annotation|@
name|Override
specifier|public
name|String
name|valueAsString
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
name|field
operator|.
name|stringValue
argument_list|()
return|;
block|}
DECL|method|indexedValue
annotation|@
name|Override
specifier|public
name|String
name|indexedValue
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
name|value
return|;
block|}
DECL|method|term
annotation|@
name|Override
specifier|public
name|Term
name|term
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|id
parameter_list|)
block|{
return|return
name|term
argument_list|(
name|Uid
operator|.
name|createUid
argument_list|(
name|type
argument_list|,
name|id
argument_list|)
argument_list|)
return|;
block|}
DECL|method|term
annotation|@
name|Override
specifier|public
name|Term
name|term
parameter_list|(
name|String
name|uid
parameter_list|)
block|{
return|return
operator|new
name|Term
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|uid
argument_list|)
return|;
block|}
DECL|method|contentType
annotation|@
name|Override
specifier|protected
name|String
name|contentType
parameter_list|()
block|{
return|return
name|CONTENT_TYPE
return|;
block|}
DECL|method|toXContent
annotation|@
name|Override
specifier|public
name|void
name|toXContent
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
comment|// for now, don't output it at all
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|XContentMapper
name|mergeWith
parameter_list|,
name|MergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
comment|// do nothing here, no merging, but also no exception
block|}
block|}
end_class

end_unit

