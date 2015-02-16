begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
import|;
end_import

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
name|Collections2
import|;
end_import

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
name|ForwardingSet
import|;
end_import

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
name|Maps
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
name|AnalysisService
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
name|java
operator|.
name|util
operator|.
name|Collection
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
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|DocumentFieldMappers
specifier|public
specifier|final
class|class
name|DocumentFieldMappers
extends|extends
name|ForwardingSet
argument_list|<
name|FieldMapper
argument_list|<
name|?
argument_list|>
argument_list|>
block|{
DECL|field|fieldMappers
specifier|private
specifier|final
name|FieldMappersLookup
name|fieldMappers
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
DECL|field|searchQuoteAnalyzer
specifier|private
specifier|final
name|FieldNameAnalyzer
name|searchQuoteAnalyzer
decl_stmt|;
DECL|method|DocumentFieldMappers
specifier|public
name|DocumentFieldMappers
parameter_list|(
name|AnalysisService
name|analysisService
parameter_list|)
block|{
name|this
argument_list|(
operator|new
name|FieldMappersLookup
argument_list|()
argument_list|,
operator|new
name|FieldNameAnalyzer
argument_list|(
name|analysisService
operator|.
name|defaultIndexAnalyzer
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FieldNameAnalyzer
argument_list|(
name|analysisService
operator|.
name|defaultSearchAnalyzer
argument_list|()
argument_list|)
argument_list|,
operator|new
name|FieldNameAnalyzer
argument_list|(
name|analysisService
operator|.
name|defaultSearchQuoteAnalyzer
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|DocumentFieldMappers
specifier|private
name|DocumentFieldMappers
parameter_list|(
name|FieldMappersLookup
name|fieldMappers
parameter_list|,
name|FieldNameAnalyzer
name|indexAnalyzer
parameter_list|,
name|FieldNameAnalyzer
name|searchAnalyzer
parameter_list|,
name|FieldNameAnalyzer
name|searchQuoteAnalyzer
parameter_list|)
block|{
name|this
operator|.
name|fieldMappers
operator|=
name|fieldMappers
expr_stmt|;
name|this
operator|.
name|indexAnalyzer
operator|=
name|indexAnalyzer
expr_stmt|;
name|this
operator|.
name|searchAnalyzer
operator|=
name|searchAnalyzer
expr_stmt|;
name|this
operator|.
name|searchQuoteAnalyzer
operator|=
name|searchQuoteAnalyzer
expr_stmt|;
block|}
DECL|method|copyAndAllAll
specifier|public
name|DocumentFieldMappers
name|copyAndAllAll
parameter_list|(
name|Collection
argument_list|<
name|?
extends|extends
name|FieldMapper
argument_list|<
name|?
argument_list|>
argument_list|>
name|newMappers
parameter_list|)
block|{
name|FieldMappersLookup
name|fieldMappers
init|=
name|this
operator|.
name|fieldMappers
operator|.
name|copyAndAddAll
argument_list|(
name|newMappers
argument_list|)
decl_stmt|;
name|FieldNameAnalyzer
name|indexAnalyzer
init|=
name|this
operator|.
name|indexAnalyzer
operator|.
name|copyAndAddAll
argument_list|(
name|Collections2
operator|.
name|transform
argument_list|(
name|newMappers
argument_list|,
operator|new
name|Function
argument_list|<
name|FieldMapper
argument_list|<
name|?
argument_list|>
argument_list|,
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|apply
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|input
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|input
operator|.
name|indexAnalyzer
argument_list|()
argument_list|)
return|;
block|}
block|}
block|)
block|)
class|;
end_class

begin_decl_stmt
name|FieldNameAnalyzer
name|searchAnalyzer
init|=
name|this
operator|.
name|searchAnalyzer
operator|.
name|copyAndAddAll
argument_list|(
name|Collections2
operator|.
name|transform
argument_list|(
name|newMappers
argument_list|,
operator|new
name|Function
argument_list|<
name|FieldMapper
argument_list|<
name|?
argument_list|>
argument_list|,
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|apply
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|input
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|input
operator|.
name|searchAnalyzer
argument_list|()
argument_list|)
return|;
block|}
block|}
end_decl_stmt

begin_empty_stmt
unit|))
empty_stmt|;
end_empty_stmt

begin_decl_stmt
name|FieldNameAnalyzer
name|searchQuoteAnalyzer
init|=
name|this
operator|.
name|searchQuoteAnalyzer
operator|.
name|copyAndAddAll
argument_list|(
name|Collections2
operator|.
name|transform
argument_list|(
name|newMappers
argument_list|,
operator|new
name|Function
argument_list|<
name|FieldMapper
argument_list|<
name|?
argument_list|>
argument_list|,
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Analyzer
argument_list|>
name|apply
parameter_list|(
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|Maps
operator|.
name|immutableEntry
argument_list|(
name|input
operator|.
name|names
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|,
name|input
operator|.
name|searchQuoteAnalyzer
argument_list|()
argument_list|)
return|;
block|}
block|}
end_decl_stmt

begin_empty_stmt
unit|))
empty_stmt|;
end_empty_stmt

begin_return
return|return
operator|new
name|DocumentFieldMappers
argument_list|(
name|fieldMappers
argument_list|,
name|indexAnalyzer
argument_list|,
name|searchAnalyzer
argument_list|,
name|searchQuoteAnalyzer
argument_list|)
return|;
end_return

begin_comment
unit|}
comment|// TODO: replace all uses of this with fullName, or change the meaning of name to be fullName
end_comment

begin_function
DECL|method|name
unit|public
name|FieldMappers
name|name
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fieldMappers
operator|.
name|fullName
argument_list|(
name|name
argument_list|)
return|;
block|}
end_function

begin_function
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
name|fieldMappers
operator|.
name|indexName
argument_list|(
name|indexName
argument_list|)
return|;
block|}
end_function

begin_function
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
name|fieldMappers
operator|.
name|fullName
argument_list|(
name|fullName
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|simpleMatchToIndexNames
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|simpleMatchToIndexNames
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
return|return
name|fieldMappers
operator|.
name|simpleMatchToIndexNames
argument_list|(
name|pattern
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|simpleMatchToFullName
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|simpleMatchToFullName
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
return|return
name|fieldMappers
operator|.
name|simpleMatchToFullName
argument_list|(
name|pattern
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * Tries to find first based on {@link #fullName(String)}, then by {@link #indexName(String)}, and last      * by {@link #name(String)}.      */
end_comment

begin_function
DECL|method|smartName
specifier|public
name|FieldMappers
name|smartName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fieldMappers
operator|.
name|smartName
argument_list|(
name|name
argument_list|)
return|;
block|}
end_function

begin_function
DECL|method|smartNameFieldMapper
specifier|public
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|smartNameFieldMapper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fieldMappers
operator|.
name|smartNameFieldMapper
argument_list|(
name|name
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * A smart analyzer used for indexing that takes into account specific analyzers configured      * per {@link FieldMapper}.      */
end_comment

begin_function
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
end_function

begin_comment
comment|/**      * A smart analyzer used for indexing that takes into account specific analyzers configured      * per {@link FieldMapper} with a custom default analyzer for no explicit field analyzer.      */
end_comment

begin_function
DECL|method|indexAnalyzer
specifier|public
name|Analyzer
name|indexAnalyzer
parameter_list|(
name|Analyzer
name|defaultAnalyzer
parameter_list|)
block|{
return|return
operator|new
name|FieldNameAnalyzer
argument_list|(
name|indexAnalyzer
operator|.
name|analyzers
argument_list|()
argument_list|,
name|defaultAnalyzer
argument_list|)
return|;
block|}
end_function

begin_comment
comment|/**      * A smart analyzer used for searching that takes into account specific analyzers configured      * per {@link FieldMapper}.      */
end_comment

begin_function
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
end_function

begin_function
DECL|method|searchQuoteAnalyzer
specifier|public
name|Analyzer
name|searchQuoteAnalyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|searchQuoteAnalyzer
return|;
block|}
end_function

begin_function
annotation|@
name|Override
DECL|method|delegate
specifier|protected
name|Set
argument_list|<
name|FieldMapper
argument_list|<
name|?
argument_list|>
argument_list|>
name|delegate
parameter_list|()
block|{
return|return
name|fieldMappers
return|;
block|}
end_function

unit|}
end_unit

