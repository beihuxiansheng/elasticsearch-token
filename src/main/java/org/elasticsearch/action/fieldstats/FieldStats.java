begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.fieldstats
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|fieldstats
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
name|io
operator|.
name|stream
operator|.
name|StreamInput
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
name|io
operator|.
name|stream
operator|.
name|StreamOutput
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
name|io
operator|.
name|stream
operator|.
name|Streamable
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
name|joda
operator|.
name|FormatDateTimeFormatter
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
name|joda
operator|.
name|Joda
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
name|ToXContent
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
name|XContentBuilderString
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

begin_class
DECL|class|FieldStats
specifier|public
specifier|abstract
class|class
name|FieldStats
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Streamable
implements|,
name|ToXContent
block|{
DECL|field|type
specifier|private
name|byte
name|type
decl_stmt|;
DECL|field|maxDoc
specifier|private
name|long
name|maxDoc
decl_stmt|;
DECL|field|docCount
specifier|private
name|long
name|docCount
decl_stmt|;
DECL|field|sumDocFreq
specifier|private
name|long
name|sumDocFreq
decl_stmt|;
DECL|field|sumTotalTermFreq
specifier|private
name|long
name|sumTotalTermFreq
decl_stmt|;
DECL|field|minValue
specifier|protected
name|T
name|minValue
decl_stmt|;
DECL|field|maxValue
specifier|protected
name|T
name|maxValue
decl_stmt|;
DECL|method|FieldStats
specifier|protected
name|FieldStats
parameter_list|()
block|{     }
DECL|method|FieldStats
specifier|protected
name|FieldStats
parameter_list|(
name|int
name|type
parameter_list|,
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
operator|(
name|byte
operator|)
name|type
expr_stmt|;
name|this
operator|.
name|maxDoc
operator|=
name|maxDoc
expr_stmt|;
name|this
operator|.
name|docCount
operator|=
name|docCount
expr_stmt|;
name|this
operator|.
name|sumDocFreq
operator|=
name|sumDocFreq
expr_stmt|;
name|this
operator|.
name|sumTotalTermFreq
operator|=
name|sumTotalTermFreq
expr_stmt|;
block|}
DECL|method|getType
name|byte
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      * @return the total number of documents.      *      * Note that, documents marked as deleted that haven't yet been merged way aren't taken into account.      */
DECL|method|getMaxDoc
specifier|public
name|long
name|getMaxDoc
parameter_list|()
block|{
return|return
name|maxDoc
return|;
block|}
comment|/**      * @return the number of documents that have at least one term for this field, or -1 if this measurement isn't available.      *      * Note that, documents marked as deleted that haven't yet been merged way aren't taken into account.      */
DECL|method|getDocCount
specifier|public
name|long
name|getDocCount
parameter_list|()
block|{
return|return
name|docCount
return|;
block|}
comment|/**      * @return The percentage of documents that have at least one value for this field.      *      * This is a derived statistic and is based on: 'doc_count / max_doc'      */
DECL|method|getDensity
specifier|public
name|int
name|getDensity
parameter_list|()
block|{
if|if
condition|(
name|docCount
operator|<
literal|0
operator|||
name|maxDoc
operator|<=
literal|0
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
return|return
call|(
name|int
call|)
argument_list|(
name|docCount
operator|*
literal|100
operator|/
name|maxDoc
argument_list|)
return|;
block|}
comment|/**      * @return the sum of each term's document frequency in this field, or -1 if this measurement isn't available.      * Document frequency is the number of documents containing a particular term.      *      * Note that, documents marked as deleted that haven't yet been merged way aren't taken into account.      */
DECL|method|getSumDocFreq
specifier|public
name|long
name|getSumDocFreq
parameter_list|()
block|{
return|return
name|sumDocFreq
return|;
block|}
comment|/**      * @return the sum of the term frequencies of all terms in this field across all documents, or -1 if this measurement      * isn't available. Term frequency is the total number of occurrences of a term in a particular document and field.      *      * Note that, documents marked as deleted that haven't yet been merged way aren't taken into account.      */
DECL|method|getSumTotalTermFreq
specifier|public
name|long
name|getSumTotalTermFreq
parameter_list|()
block|{
return|return
name|sumTotalTermFreq
return|;
block|}
comment|/**      * @return the lowest value in the field represented as a string.      *      * Note that, documents marked as deleted that haven't yet been merged way aren't taken into account.      */
DECL|method|getMinValue
specifier|public
specifier|abstract
name|String
name|getMinValue
parameter_list|()
function_decl|;
comment|/**      * @return the highest value in the field represented as a string.      *      * Note that, documents marked as deleted that haven't yet been merged way aren't taken into account.      */
DECL|method|getMaxValue
specifier|public
specifier|abstract
name|String
name|getMaxValue
parameter_list|()
function_decl|;
comment|/**      * Merges the provided stats into this stats instance.      */
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|FieldStats
name|stats
parameter_list|)
block|{
name|this
operator|.
name|maxDoc
operator|+=
name|stats
operator|.
name|maxDoc
expr_stmt|;
if|if
condition|(
name|stats
operator|.
name|docCount
operator|==
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|docCount
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|docCount
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|docCount
operator|+=
name|stats
operator|.
name|docCount
expr_stmt|;
block|}
if|if
condition|(
name|stats
operator|.
name|sumDocFreq
operator|==
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|sumDocFreq
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|sumDocFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|sumDocFreq
operator|+=
name|stats
operator|.
name|sumDocFreq
expr_stmt|;
block|}
if|if
condition|(
name|stats
operator|.
name|sumTotalTermFreq
operator|==
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|sumTotalTermFreq
operator|=
operator|-
literal|1
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|this
operator|.
name|sumTotalTermFreq
operator|!=
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|sumTotalTermFreq
operator|+=
name|stats
operator|.
name|sumTotalTermFreq
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toXContent
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
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_DOC
argument_list|,
name|maxDoc
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DOC_COUNT
argument_list|,
name|docCount
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|DENSITY
argument_list|,
name|getDensity
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUM_DOC_FREQ
argument_list|,
name|sumDocFreq
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|SUM_TOTAL_TERM_FREQ
argument_list|,
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
name|toInnerXContent
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
DECL|method|toInnerXContent
specifier|protected
name|void
name|toInnerXContent
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
name|Fields
operator|.
name|MIN_VALUE
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_VALUE
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|maxDoc
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
name|docCount
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|sumDocFreq
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|sumTotalTermFreq
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVLong
argument_list|(
name|maxDoc
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|docCount
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|sumDocFreq
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
block|}
DECL|class|Long
specifier|public
specifier|static
class|class
name|Long
extends|extends
name|FieldStats
argument_list|<
name|java
operator|.
name|lang
operator|.
name|Long
argument_list|>
block|{
DECL|method|Long
specifier|public
name|Long
parameter_list|()
block|{         }
DECL|method|Long
specifier|public
name|Long
parameter_list|(
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|minValue
parameter_list|,
name|long
name|maxValue
parameter_list|)
block|{
name|this
argument_list|(
literal|0
argument_list|,
name|maxDoc
argument_list|,
name|docCount
argument_list|,
name|sumDocFreq
argument_list|,
name|sumTotalTermFreq
argument_list|,
name|minValue
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
DECL|method|Long
specifier|protected
name|Long
parameter_list|(
name|int
name|type
parameter_list|,
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|minValue
parameter_list|,
name|long
name|maxValue
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|,
name|maxDoc
argument_list|,
name|docCount
argument_list|,
name|sumDocFreq
argument_list|,
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
name|this
operator|.
name|minValue
operator|=
name|minValue
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|maxValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinValue
specifier|public
name|String
name|getMinValue
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|minValue
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxValue
specifier|public
name|String
name|getMaxValue
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|maxValue
operator|.
name|longValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|FieldStats
name|stats
parameter_list|)
block|{
name|super
operator|.
name|append
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|Long
name|other
init|=
operator|(
name|Long
operator|)
name|stats
decl_stmt|;
name|this
operator|.
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|other
operator|.
name|minValue
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|other
operator|.
name|maxValue
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|minValue
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|maxValue
operator|=
name|in
operator|.
name|readLong
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeLong
argument_list|(
name|maxValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Float
specifier|public
specifier|static
specifier|final
class|class
name|Float
extends|extends
name|FieldStats
argument_list|<
name|java
operator|.
name|lang
operator|.
name|Float
argument_list|>
block|{
DECL|method|Float
specifier|public
name|Float
parameter_list|()
block|{         }
DECL|method|Float
specifier|public
name|Float
parameter_list|(
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|float
name|minValue
parameter_list|,
name|float
name|maxValue
parameter_list|)
block|{
name|super
argument_list|(
literal|1
argument_list|,
name|maxDoc
argument_list|,
name|docCount
argument_list|,
name|sumDocFreq
argument_list|,
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
name|this
operator|.
name|minValue
operator|=
name|minValue
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|maxValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinValue
specifier|public
name|String
name|getMinValue
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|minValue
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxValue
specifier|public
name|String
name|getMaxValue
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|maxValue
operator|.
name|floatValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|FieldStats
name|stats
parameter_list|)
block|{
name|super
operator|.
name|append
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|Float
name|other
init|=
operator|(
name|Float
operator|)
name|stats
decl_stmt|;
name|this
operator|.
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|other
operator|.
name|minValue
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|other
operator|.
name|maxValue
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|minValue
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|maxValue
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeFloat
argument_list|(
name|maxValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Double
specifier|public
specifier|static
specifier|final
class|class
name|Double
extends|extends
name|FieldStats
argument_list|<
name|java
operator|.
name|lang
operator|.
name|Double
argument_list|>
block|{
DECL|method|Double
specifier|public
name|Double
parameter_list|()
block|{         }
DECL|method|Double
specifier|public
name|Double
parameter_list|(
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|double
name|minValue
parameter_list|,
name|double
name|maxValue
parameter_list|)
block|{
name|super
argument_list|(
literal|2
argument_list|,
name|maxDoc
argument_list|,
name|docCount
argument_list|,
name|sumDocFreq
argument_list|,
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
name|this
operator|.
name|minValue
operator|=
name|minValue
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|maxValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinValue
specifier|public
name|String
name|getMinValue
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|minValue
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxValue
specifier|public
name|String
name|getMaxValue
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|maxValue
operator|.
name|doubleValue
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|FieldStats
name|stats
parameter_list|)
block|{
name|super
operator|.
name|append
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|Double
name|other
init|=
operator|(
name|Double
operator|)
name|stats
decl_stmt|;
name|this
operator|.
name|minValue
operator|=
name|Math
operator|.
name|min
argument_list|(
name|other
operator|.
name|minValue
argument_list|,
name|minValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|Math
operator|.
name|max
argument_list|(
name|other
operator|.
name|maxValue
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|minValue
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|maxValue
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|maxValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Text
specifier|public
specifier|static
specifier|final
class|class
name|Text
extends|extends
name|FieldStats
argument_list|<
name|BytesRef
argument_list|>
block|{
DECL|method|Text
specifier|public
name|Text
parameter_list|()
block|{         }
DECL|method|Text
specifier|public
name|Text
parameter_list|(
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|BytesRef
name|minValue
parameter_list|,
name|BytesRef
name|maxValue
parameter_list|)
block|{
name|super
argument_list|(
literal|3
argument_list|,
name|maxDoc
argument_list|,
name|docCount
argument_list|,
name|sumDocFreq
argument_list|,
name|sumTotalTermFreq
argument_list|)
expr_stmt|;
name|this
operator|.
name|minValue
operator|=
name|minValue
expr_stmt|;
name|this
operator|.
name|maxValue
operator|=
name|maxValue
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinValue
specifier|public
name|String
name|getMinValue
parameter_list|()
block|{
return|return
name|minValue
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxValue
specifier|public
name|String
name|getMaxValue
parameter_list|()
block|{
return|return
name|maxValue
operator|.
name|utf8ToString
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|append
specifier|public
name|void
name|append
parameter_list|(
name|FieldStats
name|stats
parameter_list|)
block|{
name|super
operator|.
name|append
argument_list|(
name|stats
argument_list|)
expr_stmt|;
name|Text
name|other
init|=
operator|(
name|Text
operator|)
name|stats
decl_stmt|;
if|if
condition|(
name|other
operator|.
name|minValue
operator|.
name|compareTo
argument_list|(
name|minValue
argument_list|)
operator|<
literal|0
condition|)
block|{
name|minValue
operator|=
name|other
operator|.
name|minValue
expr_stmt|;
block|}
if|if
condition|(
name|other
operator|.
name|maxValue
operator|.
name|compareTo
argument_list|(
name|maxValue
argument_list|)
operator|>
literal|0
condition|)
block|{
name|maxValue
operator|=
name|other
operator|.
name|maxValue
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|toInnerXContent
specifier|protected
name|void
name|toInnerXContent
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
name|Fields
operator|.
name|MIN_VALUE
argument_list|,
name|getMinValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_VALUE
argument_list|,
name|getMaxValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|minValue
operator|=
name|in
operator|.
name|readBytesRef
argument_list|()
expr_stmt|;
name|maxValue
operator|=
name|in
operator|.
name|readBytesRef
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesRef
argument_list|(
name|minValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytesRef
argument_list|(
name|maxValue
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|Date
specifier|public
specifier|static
specifier|final
class|class
name|Date
extends|extends
name|Long
block|{
DECL|field|dateFormatter
specifier|private
name|FormatDateTimeFormatter
name|dateFormatter
decl_stmt|;
DECL|method|Date
specifier|public
name|Date
parameter_list|()
block|{         }
DECL|method|Date
specifier|public
name|Date
parameter_list|(
name|long
name|maxDoc
parameter_list|,
name|long
name|docCount
parameter_list|,
name|long
name|sumDocFreq
parameter_list|,
name|long
name|sumTotalTermFreq
parameter_list|,
name|long
name|minValue
parameter_list|,
name|long
name|maxValue
parameter_list|,
name|FormatDateTimeFormatter
name|dateFormatter
parameter_list|)
block|{
name|super
argument_list|(
literal|4
argument_list|,
name|maxDoc
argument_list|,
name|docCount
argument_list|,
name|sumDocFreq
argument_list|,
name|sumTotalTermFreq
argument_list|,
name|minValue
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
name|this
operator|.
name|dateFormatter
operator|=
name|dateFormatter
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getMinValue
specifier|public
name|String
name|getMinValue
parameter_list|()
block|{
return|return
name|dateFormatter
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|minValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getMaxValue
specifier|public
name|String
name|getMaxValue
parameter_list|()
block|{
return|return
name|dateFormatter
operator|.
name|printer
argument_list|()
operator|.
name|print
argument_list|(
name|maxValue
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toInnerXContent
specifier|protected
name|void
name|toInnerXContent
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
name|Fields
operator|.
name|MIN_VALUE
argument_list|,
name|getMinValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_VALUE
argument_list|,
name|getMaxValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|dateFormatter
operator|=
name|Joda
operator|.
name|forPattern
argument_list|(
name|in
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|writeTo
specifier|public
name|void
name|writeTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|dateFormatter
operator|.
name|format
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|public
specifier|static
name|FieldStats
name|read
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|FieldStats
name|stats
decl_stmt|;
name|byte
name|type
init|=
name|in
operator|.
name|readByte
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
literal|0
case|:
name|stats
operator|=
operator|new
name|Long
argument_list|()
expr_stmt|;
break|break;
case|case
literal|1
case|:
name|stats
operator|=
operator|new
name|Float
argument_list|()
expr_stmt|;
break|break;
case|case
literal|2
case|:
name|stats
operator|=
operator|new
name|Double
argument_list|()
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|stats
operator|=
operator|new
name|Text
argument_list|()
expr_stmt|;
break|break;
case|case
literal|4
case|:
name|stats
operator|=
operator|new
name|Date
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|stats
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|stats
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|stats
return|;
block|}
DECL|class|Fields
specifier|private
specifier|final
specifier|static
class|class
name|Fields
block|{
DECL|field|MAX_DOC
specifier|final
specifier|static
name|XContentBuilderString
name|MAX_DOC
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"max_doc"
argument_list|)
decl_stmt|;
DECL|field|DOC_COUNT
specifier|final
specifier|static
name|XContentBuilderString
name|DOC_COUNT
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"doc_count"
argument_list|)
decl_stmt|;
DECL|field|DENSITY
specifier|final
specifier|static
name|XContentBuilderString
name|DENSITY
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"density"
argument_list|)
decl_stmt|;
DECL|field|SUM_DOC_FREQ
specifier|final
specifier|static
name|XContentBuilderString
name|SUM_DOC_FREQ
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"sum_doc_freq"
argument_list|)
decl_stmt|;
DECL|field|SUM_TOTAL_TERM_FREQ
specifier|final
specifier|static
name|XContentBuilderString
name|SUM_TOTAL_TERM_FREQ
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"sum_total_term_freq"
argument_list|)
decl_stmt|;
DECL|field|MIN_VALUE
specifier|final
specifier|static
name|XContentBuilderString
name|MIN_VALUE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"min_value"
argument_list|)
decl_stmt|;
DECL|field|MAX_VALUE
specifier|final
specifier|static
name|XContentBuilderString
name|MAX_VALUE
init|=
operator|new
name|XContentBuilderString
argument_list|(
literal|"max_value"
argument_list|)
decl_stmt|;
block|}
block|}
end_class

end_unit

