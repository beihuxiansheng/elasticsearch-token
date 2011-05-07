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
name|analysis
operator|.
name|Analyzer
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|Query
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
name|lucene
operator|.
name|all
operator|.
name|AllField
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
name|all
operator|.
name|AllTermQuery
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
name|analysis
operator|.
name|NamedAnalyzer
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
name|query
operator|.
name|xcontent
operator|.
name|QueryParseContext
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
DECL|class|AllFieldMapper
specifier|public
class|class
name|AllFieldMapper
extends|extends
name|AbstractFieldMapper
argument_list|<
name|Void
argument_list|>
implements|implements
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|AllFieldMapper
block|{
DECL|field|CONTENT_TYPE
specifier|public
specifier|static
specifier|final
name|String
name|CONTENT_TYPE
init|=
literal|"_all"
decl_stmt|;
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
extends|extends
name|AbstractFieldMapper
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|AllFieldMapper
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|AllFieldMapper
operator|.
name|NAME
decl_stmt|;
DECL|field|ENABLED
specifier|public
specifier|static
specifier|final
name|boolean
name|ENABLED
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
name|AbstractFieldMapper
operator|.
name|Builder
argument_list|<
name|Builder
argument_list|,
name|AllFieldMapper
argument_list|>
block|{
DECL|field|enabled
specifier|private
name|boolean
name|enabled
init|=
name|Defaults
operator|.
name|ENABLED
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
name|builder
operator|=
name|this
expr_stmt|;
name|indexName
operator|=
name|Defaults
operator|.
name|INDEX_NAME
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|Builder
name|enabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|store
annotation|@
name|Override
specifier|public
name|Builder
name|store
parameter_list|(
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
return|return
name|super
operator|.
name|store
argument_list|(
name|store
argument_list|)
return|;
block|}
DECL|method|termVector
annotation|@
name|Override
specifier|public
name|Builder
name|termVector
parameter_list|(
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|)
block|{
return|return
name|super
operator|.
name|termVector
argument_list|(
name|termVector
argument_list|)
return|;
block|}
DECL|method|indexAnalyzer
annotation|@
name|Override
specifier|protected
name|Builder
name|indexAnalyzer
parameter_list|(
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|)
block|{
return|return
name|super
operator|.
name|indexAnalyzer
argument_list|(
name|indexAnalyzer
argument_list|)
return|;
block|}
DECL|method|searchAnalyzer
annotation|@
name|Override
specifier|protected
name|Builder
name|searchAnalyzer
parameter_list|(
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|)
block|{
return|return
name|super
operator|.
name|searchAnalyzer
argument_list|(
name|searchAnalyzer
argument_list|)
return|;
block|}
DECL|method|build
annotation|@
name|Override
specifier|public
name|AllFieldMapper
name|build
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|AllFieldMapper
argument_list|(
name|name
argument_list|,
name|store
argument_list|,
name|termVector
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|,
name|enabled
argument_list|)
return|;
block|}
block|}
DECL|field|enabled
specifier|private
name|boolean
name|enabled
decl_stmt|;
DECL|method|AllFieldMapper
specifier|public
name|AllFieldMapper
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
name|STORE
argument_list|,
name|Defaults
operator|.
name|TERM_VECTOR
argument_list|,
name|Defaults
operator|.
name|OMIT_NORMS
argument_list|,
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Defaults
operator|.
name|ENABLED
argument_list|)
expr_stmt|;
block|}
DECL|method|AllFieldMapper
specifier|protected
name|AllFieldMapper
parameter_list|(
name|String
name|name
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
name|boolean
name|omitNorms
parameter_list|,
name|boolean
name|omitTermFreqAndPositions
parameter_list|,
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|,
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|,
name|boolean
name|enabled
parameter_list|)
block|{
name|super
argument_list|(
operator|new
name|Names
argument_list|(
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|,
name|name
argument_list|)
argument_list|,
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
argument_list|,
name|store
argument_list|,
name|termVector
argument_list|,
literal|1.0f
argument_list|,
name|omitNorms
argument_list|,
name|omitTermFreqAndPositions
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|)
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
DECL|method|enabled
specifier|public
name|boolean
name|enabled
parameter_list|()
block|{
return|return
name|this
operator|.
name|enabled
return|;
block|}
DECL|method|queryStringTermQuery
annotation|@
name|Override
specifier|public
name|Query
name|queryStringTermQuery
parameter_list|(
name|Term
name|term
parameter_list|)
block|{
return|return
operator|new
name|AllTermQuery
argument_list|(
name|term
argument_list|)
return|;
block|}
DECL|method|fieldQuery
annotation|@
name|Override
specifier|public
name|Query
name|fieldQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|QueryParseContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|AllTermQuery
argument_list|(
name|termFactory
operator|.
name|createTerm
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
DECL|method|parseCreateField
annotation|@
name|Override
specifier|protected
name|Fieldable
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
operator|!
name|enabled
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// reset the entries
name|context
operator|.
name|allEntries
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
name|Analyzer
name|analyzer
init|=
name|findAnalyzer
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
operator|new
name|AllField
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|store
argument_list|,
name|termVector
argument_list|,
name|context
operator|.
name|allEntries
argument_list|()
argument_list|,
name|analyzer
argument_list|)
return|;
block|}
DECL|method|findAnalyzer
specifier|private
name|Analyzer
name|findAnalyzer
parameter_list|(
name|ParseContext
name|context
parameter_list|)
block|{
name|Analyzer
name|analyzer
init|=
name|indexAnalyzer
decl_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|context
operator|.
name|analyzer
argument_list|()
expr_stmt|;
if|if
condition|(
name|analyzer
operator|==
literal|null
condition|)
block|{
name|analyzer
operator|=
name|Lucene
operator|.
name|STANDARD_ANALYZER
expr_stmt|;
block|}
block|}
return|return
name|analyzer
return|;
block|}
DECL|method|value
annotation|@
name|Override
specifier|public
name|Void
name|value
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|valueFromString
annotation|@
name|Override
specifier|public
name|Void
name|valueFromString
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
literal|null
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
literal|null
return|;
block|}
DECL|method|valueForSearch
annotation|@
name|Override
specifier|public
name|Object
name|valueForSearch
parameter_list|(
name|Fieldable
name|field
parameter_list|)
block|{
return|return
literal|null
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
name|XContentBuilder
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
comment|// if all are defaults, no need to write it at all
if|if
condition|(
name|enabled
operator|==
name|Defaults
operator|.
name|ENABLED
operator|&&
name|store
operator|==
name|Defaults
operator|.
name|STORE
operator|&&
name|termVector
operator|==
name|Defaults
operator|.
name|TERM_VECTOR
operator|&&
name|indexAnalyzer
operator|==
literal|null
operator|&&
name|searchAnalyzer
operator|==
literal|null
condition|)
block|{
return|return
name|builder
return|;
block|}
name|builder
operator|.
name|startObject
argument_list|(
name|CONTENT_TYPE
argument_list|)
expr_stmt|;
if|if
condition|(
name|enabled
operator|!=
name|Defaults
operator|.
name|ENABLED
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"enabled"
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|store
operator|!=
name|Defaults
operator|.
name|STORE
condition|)
block|{
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
block|}
if|if
condition|(
name|termVector
operator|!=
name|Defaults
operator|.
name|TERM_VECTOR
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"term_vector"
argument_list|,
name|termVector
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indexAnalyzer
operator|!=
literal|null
operator|&&
name|searchAnalyzer
operator|!=
literal|null
operator|&&
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|searchAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
operator|&&
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
comment|// same analyzers, output it once
name|builder
operator|.
name|field
argument_list|(
literal|"analyzer"
argument_list|,
name|indexAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|indexAnalyzer
operator|!=
literal|null
operator|&&
operator|!
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index_analyzer"
argument_list|,
name|indexAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|searchAnalyzer
operator|!=
literal|null
operator|&&
operator|!
name|searchAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"search_analyzer"
argument_list|,
name|searchAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
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

