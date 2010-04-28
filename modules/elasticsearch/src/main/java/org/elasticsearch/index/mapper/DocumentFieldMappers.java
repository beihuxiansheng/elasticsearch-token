begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
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
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
operator|.
name|FieldNameAnalyzer
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
name|concurrent
operator|.
name|Immutable
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
name|gcommon
operator|.
name|collect
operator|.
name|ImmutableList
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
name|gcommon
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|gcommon
operator|.
name|collect
operator|.
name|Iterables
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
name|gcommon
operator|.
name|collect
operator|.
name|UnmodifiableIterator
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

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gcommon
operator|.
name|collect
operator|.
name|Lists
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gcommon
operator|.
name|collect
operator|.
name|Maps
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * @author kimchy (Shay Banon)  */
end_comment

begin_class
annotation|@
name|Immutable
DECL|class|DocumentFieldMappers
specifier|public
class|class
name|DocumentFieldMappers
implements|implements
name|Iterable
argument_list|<
name|FieldMapper
argument_list|>
block|{
DECL|field|fieldMappers
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|FieldMapper
argument_list|>
name|fieldMappers
decl_stmt|;
DECL|field|fullNameFieldMappers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappers
argument_list|>
name|fullNameFieldMappers
decl_stmt|;
DECL|field|nameFieldMappers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappers
argument_list|>
name|nameFieldMappers
decl_stmt|;
DECL|field|indexNameFieldMappers
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappers
argument_list|>
name|indexNameFieldMappers
decl_stmt|;
DECL|field|indexAnalyzer
specifier|private
specifier|final
name|FieldNameAnalyzer
name|indexAnalyzer
decl_stmt|;
DECL|field|searchAnalyzer
specifier|private
specifier|final
name|FieldNameAnalyzer
name|searchAnalyzer
decl_stmt|;
DECL|method|DocumentFieldMappers
specifier|public
name|DocumentFieldMappers
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Iterable
argument_list|<
name|FieldMapper
argument_list|>
name|fieldMappers
parameter_list|)
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappers
argument_list|>
name|tempNameFieldMappers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappers
argument_list|>
name|tempIndexNameFieldMappers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FieldMappers
argument_list|>
name|tempFullNameFieldMappers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|indexAnalyzers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|searchAnalyzers
init|=
name|newHashMap
argument_list|()
decl_stmt|;
for|for
control|(
name|FieldMapper
name|fieldMapper
range|:
name|fieldMappers
control|)
block|{
name|FieldMappers
name|mappers
init|=
name|tempNameFieldMappers
operator|.
name|get
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|name
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mappers
operator|==
literal|null
condition|)
block|{
name|mappers
operator|=
operator|new
name|FieldMappers
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mappers
operator|=
name|mappers
operator|.
name|concat
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
name|tempNameFieldMappers
operator|.
name|put
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|mappers
argument_list|)
expr_stmt|;
name|mappers
operator|=
name|tempIndexNameFieldMappers
operator|.
name|get
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mappers
operator|==
literal|null
condition|)
block|{
name|mappers
operator|=
operator|new
name|FieldMappers
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mappers
operator|=
name|mappers
operator|.
name|concat
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
name|tempIndexNameFieldMappers
operator|.
name|put
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|mappers
argument_list|)
expr_stmt|;
name|mappers
operator|=
name|tempFullNameFieldMappers
operator|.
name|get
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mappers
operator|==
literal|null
condition|)
block|{
name|mappers
operator|=
operator|new
name|FieldMappers
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|mappers
operator|=
name|mappers
operator|.
name|concat
argument_list|(
name|fieldMapper
argument_list|)
expr_stmt|;
block|}
name|tempFullNameFieldMappers
operator|.
name|put
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|fullName
argument_list|()
argument_list|,
name|mappers
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldMapper
operator|.
name|indexAnalyzer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|indexAnalyzers
operator|.
name|put
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|fieldMapper
operator|.
name|searchAnalyzer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|searchAnalyzers
operator|.
name|put
argument_list|(
name|fieldMapper
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|fieldMapper
operator|.
name|searchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|fieldMappers
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|fieldMappers
argument_list|)
expr_stmt|;
name|this
operator|.
name|nameFieldMappers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tempNameFieldMappers
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexNameFieldMappers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tempIndexNameFieldMappers
argument_list|)
expr_stmt|;
name|this
operator|.
name|fullNameFieldMappers
operator|=
name|ImmutableMap
operator|.
name|copyOf
argument_list|(
name|tempFullNameFieldMappers
argument_list|)
expr_stmt|;
name|this
operator|.
name|indexAnalyzer
operator|=
operator|new
name|FieldNameAnalyzer
argument_list|(
name|indexAnalyzers
argument_list|,
name|docMapper
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|searchAnalyzer
operator|=
operator|new
name|FieldNameAnalyzer
argument_list|(
name|searchAnalyzers
argument_list|,
name|docMapper
operator|.
name|searchAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|iterator
annotation|@
name|Override
specifier|public
name|UnmodifiableIterator
argument_list|<
name|FieldMapper
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|fieldMappers
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|name
specifier|public
name|FieldMappers
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|nameFieldMappers
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|indexName
specifier|public
name|FieldMappers
name|indexName
parameter_list|(
name|String
name|indexName
parameter_list|)
block|{
return|return
name|indexNameFieldMappers
operator|.
name|get
argument_list|(
name|indexName
argument_list|)
return|;
block|}
DECL|method|fullName
specifier|public
name|FieldMappers
name|fullName
parameter_list|(
name|String
name|fullName
parameter_list|)
block|{
return|return
name|fullNameFieldMappers
operator|.
name|get
argument_list|(
name|fullName
argument_list|)
return|;
block|}
comment|/**      * Tries to find first based on {@link #fullName(String)}, then by {@link #indexName(String)}.      */
DECL|method|smartName
specifier|public
name|FieldMappers
name|smartName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|FieldMappers
name|fieldMappers
init|=
name|fullName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|!=
literal|null
condition|)
block|{
return|return
name|fieldMappers
return|;
block|}
return|return
name|indexName
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|smartNameFieldMapper
specifier|public
name|FieldMapper
name|smartNameFieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|FieldMappers
name|fieldMappers
init|=
name|smartName
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldMappers
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|fieldMappers
operator|.
name|mapper
argument_list|()
return|;
block|}
comment|/**      * A smart analyzer used for indexing that takes into account specific analyzers configured      * per {@link FieldMapper}.      */
DECL|method|indexAnalyzer
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
comment|/**      * A smart analyzer used for searching that takes into account specific analyzers configured      * per {@link FieldMapper}.      */
DECL|method|searchAnalyzer
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
DECL|method|concat
specifier|public
name|DocumentFieldMappers
name|concat
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|FieldMapper
modifier|...
name|fieldMappers
parameter_list|)
block|{
return|return
name|concat
argument_list|(
name|docMapper
argument_list|,
name|newArrayList
argument_list|(
name|fieldMappers
argument_list|)
argument_list|)
return|;
block|}
DECL|method|concat
specifier|public
name|DocumentFieldMappers
name|concat
parameter_list|(
name|DocumentMapper
name|docMapper
parameter_list|,
name|Iterable
argument_list|<
name|FieldMapper
argument_list|>
name|fieldMappers
parameter_list|)
block|{
return|return
operator|new
name|DocumentFieldMappers
argument_list|(
name|docMapper
argument_list|,
name|Iterables
operator|.
name|concat
argument_list|(
name|this
operator|.
name|fieldMappers
argument_list|,
name|fieldMappers
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

