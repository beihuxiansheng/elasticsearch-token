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
name|java
operator|.
name|util
operator|.
name|List
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
name|queries
operator|.
name|TermsQuery
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
name|MultiTermQuery
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
name|PrefixQuery
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
name|RegexpQuery
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|BytesRefs
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
name|unit
operator|.
name|Fuzziness
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
name|QueryShardContext
import|;
end_import

begin_comment
comment|/** Base class for {@link MappedFieldType} implementations that use the same  * representation for internal index terms as the external representation so  * that partial matching queries such as prefix, wildcard and fuzzy queries  * can be implemented. */
end_comment

begin_class
DECL|class|StringFieldType
specifier|public
specifier|abstract
class|class
name|StringFieldType
extends|extends
name|TermBasedFieldType
block|{
DECL|method|StringFieldType
specifier|public
name|StringFieldType
parameter_list|()
block|{}
DECL|method|StringFieldType
specifier|protected
name|StringFieldType
parameter_list|(
name|MappedFieldType
name|ref
parameter_list|)
block|{
name|super
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|termsQuery
specifier|public
name|Query
name|termsQuery
parameter_list|(
name|List
argument_list|<
name|?
argument_list|>
name|values
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
block|{
name|failIfNotIndexed
argument_list|()
expr_stmt|;
name|BytesRef
index|[]
name|bytesRefs
init|=
operator|new
name|BytesRef
index|[
name|values
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bytesRefs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|bytesRefs
index|[
name|i
index|]
operator|=
name|indexedValueForSearch
argument_list|(
name|values
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|TermsQuery
argument_list|(
name|name
argument_list|()
argument_list|,
name|bytesRefs
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|fuzzyQuery
specifier|public
specifier|final
name|Query
name|fuzzyQuery
parameter_list|(
name|Object
name|value
parameter_list|,
name|Fuzziness
name|fuzziness
parameter_list|,
name|int
name|prefixLength
parameter_list|,
name|int
name|maxExpansions
parameter_list|,
name|boolean
name|transpositions
parameter_list|)
block|{
name|failIfNotIndexed
argument_list|()
expr_stmt|;
return|return
operator|new
name|FuzzyQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|()
argument_list|,
name|indexedValueForSearch
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
name|fuzziness
operator|.
name|asDistance
argument_list|(
name|BytesRefs
operator|.
name|toString
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
name|prefixLength
argument_list|,
name|maxExpansions
argument_list|,
name|transpositions
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|prefixQuery
specifier|public
specifier|final
name|Query
name|prefixQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
block|{
name|failIfNotIndexed
argument_list|()
expr_stmt|;
name|PrefixQuery
name|query
init|=
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|()
argument_list|,
name|indexedValueForSearch
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|regexpQuery
specifier|public
specifier|final
name|Query
name|regexpQuery
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|flags
parameter_list|,
name|int
name|maxDeterminizedStates
parameter_list|,
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
block|{
name|failIfNotIndexed
argument_list|()
expr_stmt|;
name|RegexpQuery
name|query
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|()
argument_list|,
name|indexedValueForSearch
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
name|flags
argument_list|,
name|maxDeterminizedStates
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|query
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|rangeQuery
specifier|public
name|Query
name|rangeQuery
parameter_list|(
name|Object
name|lowerTerm
parameter_list|,
name|Object
name|upperTerm
parameter_list|,
name|boolean
name|includeLower
parameter_list|,
name|boolean
name|includeUpper
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
block|{
name|failIfNotIndexed
argument_list|()
expr_stmt|;
return|return
operator|new
name|TermRangeQuery
argument_list|(
name|name
argument_list|()
argument_list|,
name|lowerTerm
operator|==
literal|null
condition|?
literal|null
else|:
name|indexedValueForSearch
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
name|indexedValueForSearch
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
block|}
end_class

end_unit

