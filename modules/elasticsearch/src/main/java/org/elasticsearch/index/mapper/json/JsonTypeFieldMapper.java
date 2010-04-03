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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|TypeFieldMapper
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|json
operator|.
name|JsonBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|lucene
operator|.
name|Lucene
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
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
DECL|class|JsonTypeFieldMapper
specifier|public
class|class
name|JsonTypeFieldMapper
extends|extends
name|JsonFieldMapper
argument_list|<
name|String
argument_list|>
implements|implements
name|TypeFieldMapper
block|{
DECL|field|JSON_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|JSON_TYPE
init|=
literal|"_type"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|JsonFieldMapper
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
name|TypeFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|INDEX_NAME
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_NAME
init|=
name|TypeFieldMapper
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
DECL|field|STORE
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|Store
name|STORE
init|=
name|Field
operator|.
name|Store
operator|.
name|NO
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
name|JsonFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|JsonTypeFieldMapper
argument_list|>
block|{
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
name|indexName
operator|=
name|Defaults
operator|.
name|INDEX_NAME
expr_stmt|;
name|index
operator|=
name|Defaults
operator|.
name|INDEX
expr_stmt|;
name|store
operator|=
name|Defaults
operator|.
name|STORE
expr_stmt|;
name|omitNorms
operator|=
name|Defaults
operator|.
name|OMIT_NORMS
expr_stmt|;
name|omitTermFreqAndPositions
operator|=
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
expr_stmt|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|JsonTypeFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|JsonTypeFieldMapper
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|store
argument_list|,
name|termVector
argument_list|,
name|boost
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|)
return|;
block|}
block|}
DECL|method|JsonTypeFieldMapper
specifier|protected
name|JsonTypeFieldMapper
parameter_list|()
block|{
name|this
argument_list|(
name|Defaults
operator|.
name|NAME
argument_list|,
name|Defaults
operator|.
name|INDEX_NAME
argument_list|)
expr_stmt|;
block|}
DECL|method|JsonTypeFieldMapper
specifier|protected
name|JsonTypeFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|indexName
argument_list|,
name|Defaults
operator|.
name|STORE
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
argument_list|)
expr_stmt|;
block|}
DECL|method|JsonTypeFieldMapper
specifier|public
name|JsonTypeFieldMapper
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|indexName
parameter_list|,
name|Field
operator|.
name|Store
name|store
parameter_list|,
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|,
name|float
name|boost
parameter_list|,
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|omitTermFreqAndPositions
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
name|store
argument_list|,
name|termVector
argument_list|,
name|boost
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
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
DECL|method|value
annotation|@
name|Override
specifier|public
name|String
name|value
parameter_list|(
name|Document
name|document
parameter_list|)
block|{
name|Fieldable
name|field
init|=
name|document
operator|.
name|getFieldable
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|field
operator|==
literal|null
condition|?
literal|null
else|:
name|value
argument_list|(
name|field
argument_list|)
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|String
name|value
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
name|value
argument_list|(
name|field
argument_list|)
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
name|value
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
name|value
argument_list|)
return|;
block|}
DECL|method|parseCreateField
annotation|@
name|Override
specifier|protected
name|Field
name|parseCreateField
parameter_list|(
name|JsonParseContext
name|jsonContext
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|Field
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|jsonContext
operator|.
name|type
argument_list|()
argument_list|,
name|store
argument_list|,
name|index
argument_list|)
return|;
block|}
DECL|method|jsonType
annotation|@
name|Override
specifier|protected
name|String
name|jsonType
parameter_list|()
block|{
return|return
name|JSON_TYPE
return|;
block|}
DECL|method|toJson
annotation|@
name|Override
specifier|public
name|void
name|toJson
parameter_list|(
name|JsonBuilder
name|builder
parameter_list|,
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|JSON_TYPE
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"store"
argument_list|,
name|store
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|JsonMapper
name|mergeWith
parameter_list|,
name|JsonMergeContext
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

