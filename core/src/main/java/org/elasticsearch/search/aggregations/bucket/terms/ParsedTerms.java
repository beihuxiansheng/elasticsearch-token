begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.aggregations.bucket.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|terms
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|CheckedBiConsumer
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
name|CheckedFunction
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
name|ObjectParser
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|XContentParserUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|Aggregation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|Aggregations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|ParsedMultiBucketAggregation
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|function
operator|.
name|Supplier
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|terms
operator|.
name|InternalTerms
operator|.
name|DOC_COUNT_ERROR_UPPER_BOUND_FIELD_NAME
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|aggregations
operator|.
name|bucket
operator|.
name|terms
operator|.
name|InternalTerms
operator|.
name|SUM_OF_OTHER_DOC_COUNTS
import|;
end_import

begin_class
DECL|class|ParsedTerms
specifier|public
specifier|abstract
class|class
name|ParsedTerms
extends|extends
name|ParsedMultiBucketAggregation
argument_list|<
name|ParsedTerms
operator|.
name|ParsedBucket
argument_list|>
implements|implements
name|Terms
block|{
DECL|field|docCountErrorUpperBound
specifier|protected
name|long
name|docCountErrorUpperBound
decl_stmt|;
DECL|field|sumOtherDocCount
specifier|protected
name|long
name|sumOtherDocCount
decl_stmt|;
annotation|@
name|Override
DECL|method|getDocCountError
specifier|public
name|long
name|getDocCountError
parameter_list|()
block|{
return|return
name|docCountErrorUpperBound
return|;
block|}
annotation|@
name|Override
DECL|method|getSumOfOtherDocCounts
specifier|public
name|long
name|getSumOfOtherDocCounts
parameter_list|()
block|{
return|return
name|sumOtherDocCount
return|;
block|}
annotation|@
name|Override
DECL|method|getBuckets
specifier|public
name|List
argument_list|<
name|?
extends|extends
name|Terms
operator|.
name|Bucket
argument_list|>
name|getBuckets
parameter_list|()
block|{
return|return
name|buckets
return|;
block|}
annotation|@
name|Override
DECL|method|getBucketByKey
specifier|public
name|Terms
operator|.
name|Bucket
name|getBucketByKey
parameter_list|(
name|String
name|term
parameter_list|)
block|{
for|for
control|(
name|Terms
operator|.
name|Bucket
name|bucket
range|:
name|getBuckets
argument_list|()
control|)
block|{
if|if
condition|(
name|bucket
operator|.
name|getKeyAsString
argument_list|()
operator|.
name|equals
argument_list|(
name|term
argument_list|)
condition|)
block|{
return|return
name|bucket
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|doXContentBody
specifier|protected
name|XContentBuilder
name|doXContentBody
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
name|field
argument_list|(
name|DOC_COUNT_ERROR_UPPER_BOUND_FIELD_NAME
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|getDocCountError
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|SUM_OF_OTHER_DOC_COUNTS
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|getSumOfOtherDocCounts
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
name|CommonFields
operator|.
name|BUCKETS
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Terms
operator|.
name|Bucket
name|bucket
range|:
name|getBuckets
argument_list|()
control|)
block|{
name|bucket
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|declareParsedTermsFields
specifier|static
name|void
name|declareParsedTermsFields
parameter_list|(
specifier|final
name|ObjectParser
argument_list|<
name|?
extends|extends
name|ParsedTerms
argument_list|,
name|Void
argument_list|>
name|objectParser
parameter_list|,
specifier|final
name|CheckedFunction
argument_list|<
name|XContentParser
argument_list|,
name|ParsedBucket
argument_list|,
name|IOException
argument_list|>
name|bucketParser
parameter_list|)
block|{
name|declareMultiBucketAggregationFields
argument_list|(
name|objectParser
argument_list|,
name|bucketParser
operator|::
name|apply
argument_list|,
name|bucketParser
operator|::
name|apply
argument_list|)
expr_stmt|;
name|objectParser
operator|.
name|declareLong
argument_list|(
parameter_list|(
name|parsedTerms
parameter_list|,
name|value
parameter_list|)
lambda|->
name|parsedTerms
operator|.
name|docCountErrorUpperBound
operator|=
name|value
argument_list|,
name|DOC_COUNT_ERROR_UPPER_BOUND_FIELD_NAME
argument_list|)
expr_stmt|;
name|objectParser
operator|.
name|declareLong
argument_list|(
parameter_list|(
name|parsedTerms
parameter_list|,
name|value
parameter_list|)
lambda|->
name|parsedTerms
operator|.
name|sumOtherDocCount
operator|=
name|value
argument_list|,
name|SUM_OF_OTHER_DOC_COUNTS
argument_list|)
expr_stmt|;
block|}
DECL|class|ParsedBucket
specifier|public
specifier|abstract
specifier|static
class|class
name|ParsedBucket
extends|extends
name|ParsedMultiBucketAggregation
operator|.
name|ParsedBucket
implements|implements
name|Terms
operator|.
name|Bucket
block|{
DECL|field|showDocCountError
name|boolean
name|showDocCountError
init|=
literal|false
decl_stmt|;
DECL|field|docCountError
specifier|protected
name|long
name|docCountError
decl_stmt|;
annotation|@
name|Override
DECL|method|getDocCountError
specifier|public
name|long
name|getDocCountError
parameter_list|()
block|{
return|return
name|docCountError
return|;
block|}
annotation|@
name|Override
DECL|method|toXContent
specifier|public
specifier|final
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
argument_list|()
expr_stmt|;
name|keyToXContent
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|CommonFields
operator|.
name|DOC_COUNT
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|getDocCount
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|showDocCountError
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|DOC_COUNT_ERROR_UPPER_BOUND_FIELD_NAME
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|getDocCountError
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|getAggregations
argument_list|()
operator|.
name|toXContentInternal
argument_list|(
name|builder
argument_list|,
name|params
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
DECL|method|parseTermsBucketXContent
specifier|static
parameter_list|<
name|B
extends|extends
name|ParsedBucket
parameter_list|>
name|B
name|parseTermsBucketXContent
parameter_list|(
specifier|final
name|XContentParser
name|parser
parameter_list|,
specifier|final
name|Supplier
argument_list|<
name|B
argument_list|>
name|bucketSupplier
parameter_list|,
specifier|final
name|CheckedBiConsumer
argument_list|<
name|XContentParser
argument_list|,
name|B
argument_list|,
name|IOException
argument_list|>
name|keyConsumer
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|B
name|bucket
init|=
name|bucketSupplier
operator|.
name|get
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Aggregation
argument_list|>
name|aggregations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
operator|)
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
condition|)
block|{
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
condition|)
block|{
name|currentFieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|token
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
name|CommonFields
operator|.
name|KEY_AS_STRING
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|bucket
operator|.
name|setKeyAsString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CommonFields
operator|.
name|KEY
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|keyConsumer
operator|.
name|accept
argument_list|(
name|parser
argument_list|,
name|bucket
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|CommonFields
operator|.
name|DOC_COUNT
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|bucket
operator|.
name|setDocCount
argument_list|(
name|parser
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|DOC_COUNT_ERROR_UPPER_BOUND_FIELD_NAME
operator|.
name|getPreferredName
argument_list|()
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|bucket
operator|.
name|docCountError
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
name|bucket
operator|.
name|showDocCountError
operator|=
literal|true
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|token
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|XContentParserUtils
operator|.
name|parseTypedKeysObject
argument_list|(
name|parser
argument_list|,
name|Aggregation
operator|.
name|TYPED_KEYS_DELIMITER
argument_list|,
name|Aggregation
operator|.
name|class
argument_list|,
name|aggregations
operator|::
name|add
argument_list|)
expr_stmt|;
block|}
block|}
name|bucket
operator|.
name|setAggregations
argument_list|(
operator|new
name|Aggregations
argument_list|(
name|aggregations
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|bucket
return|;
block|}
block|}
block|}
end_class

end_unit

