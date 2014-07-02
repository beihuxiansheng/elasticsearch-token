begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata.plain
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
operator|.
name|plain
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
name|index
operator|.
name|*
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|IntsRef
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
name|fst
operator|.
name|FST
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
name|fst
operator|.
name|FST
operator|.
name|INPUT_TYPE
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
name|fst
operator|.
name|PositiveIntOutputs
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
name|fst
operator|.
name|Util
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
name|settings
operator|.
name|Settings
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
name|Index
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
name|fielddata
operator|.
name|*
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
name|fielddata
operator|.
name|ordinals
operator|.
name|Ordinals
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
name|fielddata
operator|.
name|ordinals
operator|.
name|OrdinalsBuilder
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
name|MapperService
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
name|settings
operator|.
name|IndexSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|fielddata
operator|.
name|breaker
operator|.
name|CircuitBreakerService
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|FSTBytesIndexFieldData
specifier|public
class|class
name|FSTBytesIndexFieldData
extends|extends
name|AbstractIndexOrdinalsFieldData
block|{
DECL|field|breakerService
specifier|private
specifier|final
name|CircuitBreakerService
name|breakerService
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
implements|implements
name|IndexFieldData
operator|.
name|Builder
block|{
annotation|@
name|Override
DECL|method|build
specifier|public
name|IndexOrdinalsFieldData
name|build
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
argument_list|<
name|?
argument_list|>
name|mapper
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|,
name|MapperService
name|mapperService
parameter_list|)
block|{
return|return
operator|new
name|FSTBytesIndexFieldData
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|mapper
operator|.
name|names
argument_list|()
argument_list|,
name|mapper
operator|.
name|fieldDataType
argument_list|()
argument_list|,
name|cache
argument_list|,
name|breakerService
argument_list|)
return|;
block|}
block|}
DECL|method|FSTBytesIndexFieldData
name|FSTBytesIndexFieldData
parameter_list|(
name|Index
name|index
parameter_list|,
annotation|@
name|IndexSettings
name|Settings
name|indexSettings
parameter_list|,
name|FieldMapper
operator|.
name|Names
name|fieldNames
parameter_list|,
name|FieldDataType
name|fieldDataType
parameter_list|,
name|IndexFieldDataCache
name|cache
parameter_list|,
name|CircuitBreakerService
name|breakerService
parameter_list|)
block|{
name|super
argument_list|(
name|index
argument_list|,
name|indexSettings
argument_list|,
name|fieldNames
argument_list|,
name|fieldDataType
argument_list|,
name|cache
argument_list|,
name|breakerService
argument_list|)
expr_stmt|;
name|this
operator|.
name|breakerService
operator|=
name|breakerService
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|loadDirect
specifier|public
name|AtomicOrdinalsFieldData
name|loadDirect
parameter_list|(
name|AtomicReaderContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|AtomicReader
name|reader
init|=
name|context
operator|.
name|reader
argument_list|()
decl_stmt|;
name|Terms
name|terms
init|=
name|reader
operator|.
name|terms
argument_list|(
name|getFieldNames
argument_list|()
operator|.
name|indexName
argument_list|()
argument_list|)
decl_stmt|;
name|AtomicOrdinalsFieldData
name|data
init|=
literal|null
decl_stmt|;
comment|// TODO: Use an actual estimator to estimate before loading.
name|NonEstimatingEstimator
name|estimator
init|=
operator|new
name|NonEstimatingEstimator
argument_list|(
name|breakerService
operator|.
name|getBreaker
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|terms
operator|==
literal|null
condition|)
block|{
name|data
operator|=
name|AbstractAtomicOrdinalsFieldData
operator|.
name|empty
argument_list|()
expr_stmt|;
name|estimator
operator|.
name|afterLoad
argument_list|(
literal|null
argument_list|,
name|data
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
name|PositiveIntOutputs
name|outputs
init|=
name|PositiveIntOutputs
operator|.
name|getSingleton
argument_list|()
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|Builder
argument_list|<
name|Long
argument_list|>
name|fstBuilder
init|=
operator|new
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|fst
operator|.
name|Builder
argument_list|<>
argument_list|(
name|INPUT_TYPE
operator|.
name|BYTE1
argument_list|,
name|outputs
argument_list|)
decl_stmt|;
specifier|final
name|IntsRef
name|scratch
init|=
operator|new
name|IntsRef
argument_list|()
decl_stmt|;
specifier|final
name|long
name|numTerms
decl_stmt|;
if|if
condition|(
name|regex
operator|==
literal|null
operator|&&
name|frequency
operator|==
literal|null
condition|)
block|{
name|numTerms
operator|=
name|terms
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|numTerms
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|final
name|float
name|acceptableTransientOverheadRatio
init|=
name|fieldDataType
operator|.
name|getSettings
argument_list|()
operator|.
name|getAsFloat
argument_list|(
literal|"acceptable_transient_overhead_ratio"
argument_list|,
name|OrdinalsBuilder
operator|.
name|DEFAULT_ACCEPTABLE_OVERHEAD_RATIO
argument_list|)
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
init|(
name|OrdinalsBuilder
name|builder
init|=
operator|new
name|OrdinalsBuilder
argument_list|(
name|numTerms
argument_list|,
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|,
name|acceptableTransientOverheadRatio
argument_list|)
init|)
block|{
comment|// we don't store an ord 0 in the FST since we could have an empty string in there and FST don't support
comment|// empty strings twice. ie. them merge fails for long output.
name|TermsEnum
name|termsEnum
init|=
name|filter
argument_list|(
name|terms
argument_list|,
name|reader
argument_list|)
decl_stmt|;
name|DocsEnum
name|docsEnum
init|=
literal|null
decl_stmt|;
for|for
control|(
name|BytesRef
name|term
init|=
name|termsEnum
operator|.
name|next
argument_list|()
init|;
name|term
operator|!=
literal|null
condition|;
name|term
operator|=
name|termsEnum
operator|.
name|next
argument_list|()
control|)
block|{
specifier|final
name|long
name|termOrd
init|=
name|builder
operator|.
name|nextOrdinal
argument_list|()
decl_stmt|;
name|fstBuilder
operator|.
name|add
argument_list|(
name|Util
operator|.
name|toIntsRef
argument_list|(
name|term
argument_list|,
name|scratch
argument_list|)
argument_list|,
operator|(
name|long
operator|)
name|termOrd
argument_list|)
expr_stmt|;
name|docsEnum
operator|=
name|termsEnum
operator|.
name|docs
argument_list|(
literal|null
argument_list|,
name|docsEnum
argument_list|,
name|DocsEnum
operator|.
name|FLAG_NONE
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|docId
init|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
init|;
name|docId
operator|!=
name|DocsEnum
operator|.
name|NO_MORE_DOCS
condition|;
name|docId
operator|=
name|docsEnum
operator|.
name|nextDoc
argument_list|()
control|)
block|{
name|builder
operator|.
name|addDoc
argument_list|(
name|docId
argument_list|)
expr_stmt|;
block|}
block|}
name|FST
argument_list|<
name|Long
argument_list|>
name|fst
init|=
name|fstBuilder
operator|.
name|finish
argument_list|()
decl_stmt|;
specifier|final
name|Ordinals
name|ordinals
init|=
name|builder
operator|.
name|build
argument_list|(
name|fieldDataType
operator|.
name|getSettings
argument_list|()
argument_list|)
decl_stmt|;
name|data
operator|=
operator|new
name|FSTBytesAtomicFieldData
argument_list|(
name|fst
argument_list|,
name|ordinals
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|data
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|success
condition|)
block|{
name|estimator
operator|.
name|afterLoad
argument_list|(
literal|null
argument_list|,
name|data
operator|.
name|ramBytesUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

