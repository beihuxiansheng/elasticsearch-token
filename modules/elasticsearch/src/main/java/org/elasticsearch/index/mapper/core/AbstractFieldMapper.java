begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.core
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|core
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
name|Filter
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
name|FuzzyQuery
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|TermQuery
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
name|TermRangeFilter
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
name|TermRangeQuery
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
name|search
operator|.
name|TermFilter
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
name|field
operator|.
name|data
operator|.
name|FieldDataType
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
name|FieldMapper
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
name|FieldMapperListener
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
name|Mapper
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
name|MergeContext
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
name|ObjectMapperListener
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
name|ParseContext
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
DECL|class|AbstractFieldMapper
specifier|public
specifier|abstract
class|class
name|AbstractFieldMapper
parameter_list|<
name|T
parameter_list|>
implements|implements
name|FieldMapper
argument_list|<
name|T
argument_list|>
implements|,
name|Mapper
block|{
DECL|class|Defaults
specifier|public
specifier|static
class|class
name|Defaults
block|{
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
name|ANALYZED
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
DECL|field|TERM_VECTOR
specifier|public
specifier|static
specifier|final
name|Field
operator|.
name|TermVector
name|TERM_VECTOR
init|=
name|Field
operator|.
name|TermVector
operator|.
name|NO
decl_stmt|;
DECL|field|BOOST
specifier|public
specifier|static
specifier|final
name|float
name|BOOST
init|=
literal|1.0f
decl_stmt|;
DECL|field|OMIT_NORMS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_NORMS
init|=
literal|false
decl_stmt|;
DECL|field|OMIT_TERM_FREQ_AND_POSITIONS
specifier|public
specifier|static
specifier|final
name|boolean
name|OMIT_TERM_FREQ_AND_POSITIONS
init|=
literal|false
decl_stmt|;
block|}
DECL|class|OpenBuilder
specifier|public
specifier|abstract
specifier|static
class|class
name|OpenBuilder
parameter_list|<
name|T
extends|extends
name|Builder
parameter_list|,
name|Y
extends|extends
name|AbstractFieldMapper
parameter_list|>
extends|extends
name|AbstractFieldMapper
operator|.
name|Builder
argument_list|<
name|T
argument_list|,
name|Y
argument_list|>
block|{
DECL|method|OpenBuilder
specifier|protected
name|OpenBuilder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|index
annotation|@
name|Override
specifier|public
name|T
name|index
parameter_list|(
name|Field
operator|.
name|Index
name|index
parameter_list|)
block|{
return|return
name|super
operator|.
name|index
argument_list|(
name|index
argument_list|)
return|;
block|}
DECL|method|store
annotation|@
name|Override
specifier|public
name|T
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
name|T
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
DECL|method|boost
annotation|@
name|Override
specifier|public
name|T
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
return|return
name|super
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
return|;
block|}
DECL|method|omitNorms
annotation|@
name|Override
specifier|public
name|T
name|omitNorms
parameter_list|(
name|boolean
name|omitNorms
parameter_list|)
block|{
return|return
name|super
operator|.
name|omitNorms
argument_list|(
name|omitNorms
argument_list|)
return|;
block|}
DECL|method|omitTermFreqAndPositions
annotation|@
name|Override
specifier|public
name|T
name|omitTermFreqAndPositions
parameter_list|(
name|boolean
name|omitTermFreqAndPositions
parameter_list|)
block|{
return|return
name|super
operator|.
name|omitTermFreqAndPositions
argument_list|(
name|omitTermFreqAndPositions
argument_list|)
return|;
block|}
DECL|method|indexName
annotation|@
name|Override
specifier|public
name|T
name|indexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
return|return
name|super
operator|.
name|indexName
argument_list|(
name|indexName
argument_list|)
return|;
block|}
DECL|method|indexAnalyzer
annotation|@
name|Override
specifier|public
name|T
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
specifier|public
name|T
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
block|}
DECL|class|Builder
specifier|public
specifier|abstract
specifier|static
class|class
name|Builder
parameter_list|<
name|T
extends|extends
name|Builder
parameter_list|,
name|Y
extends|extends
name|AbstractFieldMapper
parameter_list|>
extends|extends
name|Mapper
operator|.
name|Builder
argument_list|<
name|T
argument_list|,
name|Y
argument_list|>
block|{
DECL|field|index
specifier|protected
name|Field
operator|.
name|Index
name|index
init|=
name|Defaults
operator|.
name|INDEX
decl_stmt|;
DECL|field|store
specifier|protected
name|Field
operator|.
name|Store
name|store
init|=
name|Defaults
operator|.
name|STORE
decl_stmt|;
DECL|field|termVector
specifier|protected
name|Field
operator|.
name|TermVector
name|termVector
init|=
name|Defaults
operator|.
name|TERM_VECTOR
decl_stmt|;
DECL|field|boost
specifier|protected
name|float
name|boost
init|=
name|Defaults
operator|.
name|BOOST
decl_stmt|;
DECL|field|omitNorms
specifier|protected
name|boolean
name|omitNorms
init|=
name|Defaults
operator|.
name|OMIT_NORMS
decl_stmt|;
DECL|field|omitTermFreqAndPositions
specifier|protected
name|boolean
name|omitTermFreqAndPositions
init|=
name|Defaults
operator|.
name|OMIT_TERM_FREQ_AND_POSITIONS
decl_stmt|;
DECL|field|indexName
specifier|protected
name|String
name|indexName
decl_stmt|;
DECL|field|indexAnalyzer
specifier|protected
name|NamedAnalyzer
name|indexAnalyzer
decl_stmt|;
DECL|field|searchAnalyzer
specifier|protected
name|NamedAnalyzer
name|searchAnalyzer
decl_stmt|;
DECL|field|includeInAll
specifier|protected
name|Boolean
name|includeInAll
decl_stmt|;
DECL|method|Builder
specifier|protected
name|Builder
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|method|index
specifier|protected
name|T
name|index
parameter_list|(
name|Field
operator|.
name|Index
name|index
parameter_list|)
block|{
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|store
specifier|protected
name|T
name|store
parameter_list|(
name|Field
operator|.
name|Store
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|termVector
specifier|protected
name|T
name|termVector
parameter_list|(
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|)
block|{
name|this
operator|.
name|termVector
operator|=
name|termVector
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|boost
specifier|protected
name|T
name|boost
parameter_list|(
name|float
name|boost
parameter_list|)
block|{
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|omitNorms
specifier|protected
name|T
name|omitNorms
parameter_list|(
name|boolean
name|omitNorms
parameter_list|)
block|{
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|omitTermFreqAndPositions
specifier|protected
name|T
name|omitTermFreqAndPositions
parameter_list|(
name|boolean
name|omitTermFreqAndPositions
parameter_list|)
block|{
name|this
operator|.
name|omitTermFreqAndPositions
operator|=
name|omitTermFreqAndPositions
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|indexName
specifier|protected
name|T
name|indexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
name|this
operator|.
name|indexName
operator|=
name|indexName
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|indexAnalyzer
specifier|protected
name|T
name|indexAnalyzer
parameter_list|(
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|indexAnalyzer
operator|=
name|indexAnalyzer
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|searchAnalyzer
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|searchAnalyzer
operator|=
name|indexAnalyzer
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
DECL|method|searchAnalyzer
specifier|protected
name|T
name|searchAnalyzer
parameter_list|(
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|searchAnalyzer
operator|=
name|searchAnalyzer
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|includeInAll
specifier|protected
name|T
name|includeInAll
parameter_list|(
name|Boolean
name|includeInAll
parameter_list|)
block|{
name|this
operator|.
name|includeInAll
operator|=
name|includeInAll
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|buildNames
specifier|protected
name|Names
name|buildNames
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
operator|new
name|Names
argument_list|(
name|name
argument_list|,
name|buildIndexName
argument_list|(
name|context
argument_list|)
argument_list|,
name|indexName
operator|==
literal|null
condition|?
name|name
else|:
name|indexName
argument_list|,
name|buildFullName
argument_list|(
name|context
argument_list|)
argument_list|)
return|;
block|}
DECL|method|buildIndexName
specifier|protected
name|String
name|buildIndexName
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
name|String
name|actualIndexName
init|=
name|indexName
operator|==
literal|null
condition|?
name|name
else|:
name|indexName
decl_stmt|;
return|return
name|context
operator|.
name|path
argument_list|()
operator|.
name|pathAsText
argument_list|(
name|actualIndexName
argument_list|)
return|;
block|}
DECL|method|buildFullName
specifier|protected
name|String
name|buildFullName
parameter_list|(
name|BuilderContext
name|context
parameter_list|)
block|{
return|return
name|context
operator|.
name|path
argument_list|()
operator|.
name|fullPathAsText
argument_list|(
name|name
argument_list|)
return|;
block|}
block|}
DECL|field|names
specifier|protected
specifier|final
name|Names
name|names
decl_stmt|;
DECL|field|index
specifier|protected
specifier|final
name|Field
operator|.
name|Index
name|index
decl_stmt|;
DECL|field|store
specifier|protected
specifier|final
name|Field
operator|.
name|Store
name|store
decl_stmt|;
DECL|field|termVector
specifier|protected
specifier|final
name|Field
operator|.
name|TermVector
name|termVector
decl_stmt|;
DECL|field|boost
specifier|protected
name|float
name|boost
decl_stmt|;
DECL|field|omitNorms
specifier|protected
name|boolean
name|omitNorms
decl_stmt|;
DECL|field|omitTermFreqAndPositions
specifier|protected
name|boolean
name|omitTermFreqAndPositions
decl_stmt|;
DECL|field|indexAnalyzer
specifier|protected
specifier|final
name|NamedAnalyzer
name|indexAnalyzer
decl_stmt|;
DECL|field|searchAnalyzer
specifier|protected
specifier|final
name|NamedAnalyzer
name|searchAnalyzer
decl_stmt|;
DECL|field|termFactory
specifier|protected
specifier|final
name|Term
name|termFactory
decl_stmt|;
DECL|method|AbstractFieldMapper
specifier|protected
name|AbstractFieldMapper
parameter_list|(
name|Names
name|names
parameter_list|,
name|Field
operator|.
name|Index
name|index
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
parameter_list|,
name|NamedAnalyzer
name|indexAnalyzer
parameter_list|,
name|NamedAnalyzer
name|searchAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|names
operator|=
name|names
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|termVector
operator|=
name|termVector
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|omitNorms
expr_stmt|;
name|this
operator|.
name|omitTermFreqAndPositions
operator|=
name|omitTermFreqAndPositions
expr_stmt|;
if|if
condition|(
name|indexAnalyzer
operator|==
literal|null
operator|&&
operator|!
name|index
operator|.
name|isAnalyzed
argument_list|()
condition|)
block|{
name|this
operator|.
name|indexAnalyzer
operator|=
name|Lucene
operator|.
name|KEYWORD_ANALYZER
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|indexAnalyzer
operator|=
name|indexAnalyzer
expr_stmt|;
block|}
if|if
condition|(
name|searchAnalyzer
operator|==
literal|null
operator|&&
operator|!
name|index
operator|.
name|isAnalyzed
argument_list|()
condition|)
block|{
name|this
operator|.
name|searchAnalyzer
operator|=
name|Lucene
operator|.
name|KEYWORD_ANALYZER
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|searchAnalyzer
operator|=
name|searchAnalyzer
expr_stmt|;
block|}
name|this
operator|.
name|termFactory
operator|=
operator|new
name|Term
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
DECL|method|name
annotation|@
name|Override
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|names
operator|.
name|name
argument_list|()
return|;
block|}
DECL|method|names
annotation|@
name|Override
specifier|public
name|Names
name|names
parameter_list|()
block|{
return|return
name|this
operator|.
name|names
return|;
block|}
DECL|method|index
annotation|@
name|Override
specifier|public
name|Field
operator|.
name|Index
name|index
parameter_list|()
block|{
return|return
name|this
operator|.
name|index
return|;
block|}
DECL|method|store
annotation|@
name|Override
specifier|public
name|Field
operator|.
name|Store
name|store
parameter_list|()
block|{
return|return
name|this
operator|.
name|store
return|;
block|}
DECL|method|stored
annotation|@
name|Override
specifier|public
name|boolean
name|stored
parameter_list|()
block|{
return|return
name|store
operator|==
name|Field
operator|.
name|Store
operator|.
name|YES
return|;
block|}
DECL|method|indexed
annotation|@
name|Override
specifier|public
name|boolean
name|indexed
parameter_list|()
block|{
return|return
name|index
operator|!=
name|Field
operator|.
name|Index
operator|.
name|NO
return|;
block|}
DECL|method|analyzed
annotation|@
name|Override
specifier|public
name|boolean
name|analyzed
parameter_list|()
block|{
return|return
name|index
operator|==
name|Field
operator|.
name|Index
operator|.
name|ANALYZED
return|;
block|}
DECL|method|termVector
annotation|@
name|Override
specifier|public
name|Field
operator|.
name|TermVector
name|termVector
parameter_list|()
block|{
return|return
name|this
operator|.
name|termVector
return|;
block|}
DECL|method|boost
annotation|@
name|Override
specifier|public
name|float
name|boost
parameter_list|()
block|{
return|return
name|this
operator|.
name|boost
return|;
block|}
DECL|method|omitNorms
annotation|@
name|Override
specifier|public
name|boolean
name|omitNorms
parameter_list|()
block|{
return|return
name|this
operator|.
name|omitNorms
return|;
block|}
DECL|method|omitTermFreqAndPositions
annotation|@
name|Override
specifier|public
name|boolean
name|omitTermFreqAndPositions
parameter_list|()
block|{
return|return
name|this
operator|.
name|omitTermFreqAndPositions
return|;
block|}
DECL|method|indexAnalyzer
annotation|@
name|Override
specifier|public
name|Analyzer
name|indexAnalyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexAnalyzer
return|;
block|}
DECL|method|searchAnalyzer
annotation|@
name|Override
specifier|public
name|Analyzer
name|searchAnalyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|searchAnalyzer
return|;
block|}
DECL|method|parse
annotation|@
name|Override
specifier|public
name|void
name|parse
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|Fieldable
name|field
init|=
name|parseCreateField
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|field
operator|.
name|setOmitNorms
argument_list|(
name|omitNorms
argument_list|)
expr_stmt|;
name|field
operator|.
name|setOmitTermFreqAndPositions
argument_list|(
name|omitTermFreqAndPositions
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|customBoost
argument_list|()
condition|)
block|{
name|field
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|listener
argument_list|()
operator|.
name|beforeFieldAdded
argument_list|(
name|this
argument_list|,
name|field
argument_list|,
name|context
argument_list|)
condition|)
block|{
name|context
operator|.
name|doc
argument_list|()
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|MapperParsingException
argument_list|(
literal|"Failed to parse ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|parseCreateField
specifier|protected
specifier|abstract
name|Fieldable
name|parseCreateField
parameter_list|(
name|ParseContext
name|context
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Derived classes can override it to specify that boost value is set by derived classes.      */
DECL|method|customBoost
specifier|protected
name|boolean
name|customBoost
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
DECL|method|traverse
annotation|@
name|Override
specifier|public
name|void
name|traverse
parameter_list|(
name|FieldMapperListener
name|fieldMapperListener
parameter_list|)
block|{
name|fieldMapperListener
operator|.
name|fieldMapper
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
DECL|method|traverse
annotation|@
name|Override
specifier|public
name|void
name|traverse
parameter_list|(
name|ObjectMapperListener
name|objectMapperListener
parameter_list|)
block|{
comment|// nothing to do here...
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
name|valueAsString
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
literal|null
return|;
block|}
DECL|method|useFieldQueryWithQueryString
annotation|@
name|Override
specifier|public
name|boolean
name|useFieldQueryWithQueryString
parameter_list|()
block|{
return|return
literal|false
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
name|TermQuery
argument_list|(
name|termFactory
operator|.
name|createTerm
argument_list|(
name|indexedValue
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|fuzzyQuery
annotation|@
name|Override
specifier|public
name|Query
name|fuzzyQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|String
name|minSim
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|)
block|{
return|return
operator|new
name|FuzzyQuery
argument_list|(
name|termFactory
operator|.
name|createTerm
argument_list|(
name|indexedValue
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
name|Float
operator|.
name|parseFloat
argument_list|(
name|minSim
argument_list|)
argument_list|,
name|prefixLength
argument_list|,
name|maxExpansions
argument_list|)
return|;
block|}
DECL|method|fuzzyQuery
annotation|@
name|Override
specifier|public
name|Query
name|fuzzyQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|double
name|minSim
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|)
block|{
return|return
operator|new
name|FuzzyQuery
argument_list|(
name|termFactory
operator|.
name|createTerm
argument_list|(
name|value
argument_list|)
argument_list|,
operator|(
name|float
operator|)
name|minSim
argument_list|,
name|prefixLength
argument_list|,
name|maxExpansions
argument_list|)
return|;
block|}
DECL|method|fieldFilter
annotation|@
name|Override
specifier|public
name|Filter
name|fieldFilter
parameter_list|(
name|String
name|value
parameter_list|)
block|{
return|return
operator|new
name|TermFilter
argument_list|(
name|termFactory
operator|.
name|createTerm
argument_list|(
name|indexedValue
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
DECL|method|rangeQuery
annotation|@
name|Override
specifier|public
name|Query
name|rangeQuery
parameter_list|(
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
operator|new
name|TermRangeQuery
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|indexedValue
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|upperTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|indexedValue
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
DECL|method|rangeFilter
annotation|@
name|Override
specifier|public
name|Filter
name|rangeFilter
parameter_list|(
name|String
name|lowerTerm
parameter_list|,
name|String
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|)
block|{
return|return
operator|new
name|TermRangeFilter
argument_list|(
name|names
operator|.
name|indexName
argument_list|()
argument_list|,
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|indexedValue
argument_list|(
name|lowerTerm
argument_list|)
argument_list|,
name|upperTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|indexedValue
argument_list|(
name|upperTerm
argument_list|)
argument_list|,
name|includeLower
argument_list|,
name|includeUpper
argument_list|)
return|;
block|}
DECL|method|merge
annotation|@
name|Override
specifier|public
name|void
name|merge
parameter_list|(
name|Mapper
name|mergeWith
parameter_list|,
name|MergeContext
name|mergeContext
parameter_list|)
throws|throws
name|MergeMappingException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|equals
argument_list|(
name|mergeWith
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|mergedType
init|=
name|mergeWith
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
decl_stmt|;
if|if
condition|(
name|mergeWith
operator|instanceof
name|AbstractFieldMapper
condition|)
block|{
name|mergedType
operator|=
operator|(
operator|(
name|AbstractFieldMapper
operator|)
name|mergeWith
operator|)
operator|.
name|contentType
argument_list|()
expr_stmt|;
block|}
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] of different type, current_type ["
operator|+
name|contentType
argument_list|()
operator|+
literal|"], merged_type ["
operator|+
name|mergedType
operator|+
literal|"]"
argument_list|)
expr_stmt|;
comment|// different types, return
return|return;
block|}
name|AbstractFieldMapper
name|fieldMergeWith
init|=
operator|(
name|AbstractFieldMapper
operator|)
name|mergeWith
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|index
operator|.
name|equals
argument_list|(
name|fieldMergeWith
operator|.
name|index
argument_list|)
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different index values"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|store
operator|.
name|equals
argument_list|(
name|fieldMergeWith
operator|.
name|store
argument_list|)
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different store values"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|this
operator|.
name|termVector
operator|.
name|equals
argument_list|(
name|fieldMergeWith
operator|.
name|termVector
argument_list|)
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different term_vector values"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|indexAnalyzer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|fieldMergeWith
operator|.
name|indexAnalyzer
operator|!=
literal|null
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different index_analyzer"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fieldMergeWith
operator|.
name|indexAnalyzer
operator|==
literal|null
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different index_analyzer"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|this
operator|.
name|indexAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldMergeWith
operator|.
name|indexAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different index_analyzer"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|searchAnalyzer
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|fieldMergeWith
operator|.
name|searchAnalyzer
operator|!=
literal|null
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different search_analyzer"
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|fieldMergeWith
operator|.
name|searchAnalyzer
operator|==
literal|null
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different search_analyzer"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|this
operator|.
name|searchAnalyzer
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|fieldMergeWith
operator|.
name|searchAnalyzer
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
name|mergeContext
operator|.
name|addConflict
argument_list|(
literal|"mapper ["
operator|+
name|names
operator|.
name|fullName
argument_list|()
operator|+
literal|"] has different search_analyzer"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|mergeContext
operator|.
name|mergeFlags
argument_list|()
operator|.
name|simulate
argument_list|()
condition|)
block|{
comment|// apply changeable values
name|this
operator|.
name|boost
operator|=
name|fieldMergeWith
operator|.
name|boost
expr_stmt|;
name|this
operator|.
name|omitNorms
operator|=
name|fieldMergeWith
operator|.
name|omitNorms
expr_stmt|;
name|this
operator|.
name|omitTermFreqAndPositions
operator|=
name|fieldMergeWith
operator|.
name|omitTermFreqAndPositions
expr_stmt|;
block|}
block|}
DECL|method|fieldDataType
annotation|@
name|Override
specifier|public
name|FieldDataType
name|fieldDataType
parameter_list|()
block|{
return|return
name|FieldDataType
operator|.
name|DefaultTypes
operator|.
name|STRING
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
name|builder
operator|.
name|startObject
argument_list|(
name|names
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|doXContentBody
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|doXContentBody
specifier|protected
name|void
name|doXContentBody
parameter_list|(
name|XContentBuilder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|contentType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|names
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|names
operator|.
name|indexNameClean
argument_list|()
argument_list|)
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"index_name"
argument_list|,
name|names
operator|.
name|indexNameClean
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|boost
operator|!=
literal|1.0f
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"boost"
argument_list|,
name|boost
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
block|}
DECL|method|contentType
specifier|protected
specifier|abstract
name|String
name|contentType
parameter_list|()
function_decl|;
DECL|method|close
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
block|{
comment|// nothing to do here, sub classes to override if needed
block|}
block|}
end_class

end_unit

