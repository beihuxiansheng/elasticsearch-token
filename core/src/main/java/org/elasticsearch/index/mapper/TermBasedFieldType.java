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
name|BoostQuery
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
name|Version
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
name|index
operator|.
name|query
operator|.
name|QueryShardContext
import|;
end_import

begin_comment
comment|/** Base {@link MappedFieldType} implementation for a field that is indexed  *  with the inverted index. */
end_comment

begin_class
DECL|class|TermBasedFieldType
specifier|public
specifier|abstract
class|class
name|TermBasedFieldType
extends|extends
name|MappedFieldType
block|{
DECL|method|TermBasedFieldType
specifier|public
name|TermBasedFieldType
parameter_list|()
block|{}
DECL|method|TermBasedFieldType
specifier|protected
name|TermBasedFieldType
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
comment|/** Returns the indexed value used to construct search "values".      *  This method is used for the default implementations of most      *  query factory methods such as {@link #termQuery}. */
DECL|method|indexedValueForSearch
specifier|protected
name|BytesRef
name|indexedValueForSearch
parameter_list|(
name|Object
name|value
parameter_list|)
block|{
return|return
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|value
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|termQuery
specifier|public
name|Query
name|termQuery
parameter_list|(
name|Object
name|value
parameter_list|,
name|QueryShardContext
name|context
parameter_list|)
block|{
name|failIfNotIndexed
argument_list|()
expr_stmt|;
name|TermQuery
name|query
init|=
operator|new
name|TermQuery
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
name|boost
argument_list|()
operator|==
literal|1f
operator|||
operator|(
name|context
operator|!=
literal|null
operator|&&
name|context
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|before
argument_list|(
name|Version
operator|.
name|V_5_0_0_alpha1
argument_list|)
operator|)
condition|)
block|{
return|return
name|query
return|;
block|}
return|return
operator|new
name|BoostQuery
argument_list|(
name|query
argument_list|,
name|boost
argument_list|()
argument_list|)
return|;
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
block|}
end_class

end_unit

