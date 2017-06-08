begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
package|;
end_package

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
name|XContentParser
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|Objects
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|XContentParserUtils
operator|.
name|ensureExpectedToken
import|;
end_import

begin_class
DECL|class|SearchHits
specifier|public
specifier|final
class|class
name|SearchHits
implements|implements
name|Streamable
implements|,
name|ToXContent
implements|,
name|Iterable
argument_list|<
name|SearchHit
argument_list|>
block|{
DECL|method|empty
specifier|public
specifier|static
name|SearchHits
name|empty
parameter_list|()
block|{
comment|// We shouldn't use static final instance, since that could directly be returned by native transport clients
return|return
operator|new
name|SearchHits
argument_list|(
name|EMPTY
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
return|;
block|}
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|SearchHit
index|[]
name|EMPTY
init|=
operator|new
name|SearchHit
index|[
literal|0
index|]
decl_stmt|;
DECL|field|hits
specifier|private
name|SearchHit
index|[]
name|hits
decl_stmt|;
DECL|field|totalHits
specifier|public
name|long
name|totalHits
decl_stmt|;
DECL|field|maxScore
specifier|private
name|float
name|maxScore
decl_stmt|;
DECL|method|SearchHits
name|SearchHits
parameter_list|()
block|{      }
DECL|method|SearchHits
specifier|public
name|SearchHits
parameter_list|(
name|SearchHit
index|[]
name|hits
parameter_list|,
name|long
name|totalHits
parameter_list|,
name|float
name|maxScore
parameter_list|)
block|{
name|this
operator|.
name|hits
operator|=
name|hits
expr_stmt|;
name|this
operator|.
name|totalHits
operator|=
name|totalHits
expr_stmt|;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
block|}
DECL|method|shardTarget
specifier|public
name|void
name|shardTarget
parameter_list|(
name|SearchShardTarget
name|shardTarget
parameter_list|)
block|{
for|for
control|(
name|SearchHit
name|hit
range|:
name|hits
control|)
block|{
name|hit
operator|.
name|shard
argument_list|(
name|shardTarget
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * The total number of hits that matches the search request.      */
DECL|method|getTotalHits
specifier|public
name|long
name|getTotalHits
parameter_list|()
block|{
return|return
name|totalHits
return|;
block|}
comment|/**      * The maximum score of this query.      */
DECL|method|getMaxScore
specifier|public
name|float
name|getMaxScore
parameter_list|()
block|{
return|return
name|maxScore
return|;
block|}
comment|/**      * The hits of the search request (based on the search type, and from / size provided).      */
DECL|method|getHits
specifier|public
name|SearchHit
index|[]
name|getHits
parameter_list|()
block|{
return|return
name|this
operator|.
name|hits
return|;
block|}
comment|/**      * Return the hit as the provided position.      */
DECL|method|getAt
specifier|public
name|SearchHit
name|getAt
parameter_list|(
name|int
name|position
parameter_list|)
block|{
return|return
name|hits
index|[
name|position
index|]
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|SearchHit
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|getHits
argument_list|()
argument_list|)
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|internalHits
specifier|public
name|SearchHit
index|[]
name|internalHits
parameter_list|()
block|{
return|return
name|this
operator|.
name|hits
return|;
block|}
DECL|class|Fields
specifier|public
specifier|static
specifier|final
class|class
name|Fields
block|{
DECL|field|HITS
specifier|public
specifier|static
specifier|final
name|String
name|HITS
init|=
literal|"hits"
decl_stmt|;
DECL|field|TOTAL
specifier|public
specifier|static
specifier|final
name|String
name|TOTAL
init|=
literal|"total"
decl_stmt|;
DECL|field|MAX_SCORE
specifier|public
specifier|static
specifier|final
name|String
name|MAX_SCORE
init|=
literal|"max_score"
decl_stmt|;
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
argument_list|(
name|Fields
operator|.
name|HITS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|TOTAL
argument_list|,
name|totalHits
argument_list|)
expr_stmt|;
if|if
condition|(
name|Float
operator|.
name|isNaN
argument_list|(
name|maxScore
argument_list|)
condition|)
block|{
name|builder
operator|.
name|nullField
argument_list|(
name|Fields
operator|.
name|MAX_SCORE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|MAX_SCORE
argument_list|,
name|maxScore
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|Fields
operator|.
name|HITS
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|()
expr_stmt|;
for|for
control|(
name|SearchHit
name|hit
range|:
name|hits
control|)
block|{
name|hit
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
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|builder
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|SearchHits
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|ensureExpectedToken
argument_list|(
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
argument_list|,
name|parser
operator|.
name|currentToken
argument_list|()
argument_list|,
name|parser
operator|::
name|getTokenLocation
argument_list|)
expr_stmt|;
block|}
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|currentToken
argument_list|()
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|SearchHit
argument_list|>
name|hits
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|long
name|totalHits
init|=
literal|0
decl_stmt|;
name|float
name|maxScore
init|=
literal|0f
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
name|Fields
operator|.
name|TOTAL
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|totalHits
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|Fields
operator|.
name|MAX_SCORE
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxScore
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
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
name|VALUE_NULL
condition|)
block|{
if|if
condition|(
name|Fields
operator|.
name|MAX_SCORE
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxScore
operator|=
name|Float
operator|.
name|NaN
expr_stmt|;
comment|// NaN gets rendered as null-field
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
name|START_ARRAY
condition|)
block|{
if|if
condition|(
name|Fields
operator|.
name|HITS
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
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
name|END_ARRAY
condition|)
block|{
name|hits
operator|.
name|add
argument_list|(
name|SearchHit
operator|.
name|fromXContent
argument_list|(
name|parser
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|parser
operator|.
name|skipChildren
argument_list|()
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
name|parser
operator|.
name|skipChildren
argument_list|()
expr_stmt|;
block|}
block|}
name|SearchHits
name|searchHits
init|=
operator|new
name|SearchHits
argument_list|(
name|hits
operator|.
name|toArray
argument_list|(
operator|new
name|SearchHit
index|[
name|hits
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|,
name|totalHits
argument_list|,
name|maxScore
argument_list|)
decl_stmt|;
return|return
name|searchHits
return|;
block|}
DECL|method|readSearchHits
specifier|public
specifier|static
name|SearchHits
name|readSearchHits
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|SearchHits
name|hits
init|=
operator|new
name|SearchHits
argument_list|()
decl_stmt|;
name|hits
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|hits
return|;
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
specifier|final
name|boolean
name|hasTotalHits
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
condition|)
block|{
name|hasTotalHits
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|hasTotalHits
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|hasTotalHits
condition|)
block|{
name|totalHits
operator|=
name|in
operator|.
name|readVLong
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|totalHits
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|maxScore
operator|=
name|in
operator|.
name|readFloat
argument_list|()
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|hits
operator|=
name|EMPTY
expr_stmt|;
block|}
else|else
block|{
name|hits
operator|=
operator|new
name|SearchHit
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|hits
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|hits
index|[
name|i
index|]
operator|=
name|SearchHit
operator|.
name|readSearchHit
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
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
specifier|final
name|boolean
name|hasTotalHits
decl_stmt|;
if|if
condition|(
name|out
operator|.
name|getVersion
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_6_0_0_alpha3
argument_list|)
condition|)
block|{
name|hasTotalHits
operator|=
name|totalHits
operator|>=
literal|0
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|hasTotalHits
argument_list|)
expr_stmt|;
block|}
else|else
block|{
assert|assert
name|totalHits
operator|>=
literal|0
assert|;
name|hasTotalHits
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|hasTotalHits
condition|)
block|{
name|out
operator|.
name|writeVLong
argument_list|(
name|totalHits
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeFloat
argument_list|(
name|maxScore
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|hits
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|hits
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|SearchHit
name|hit
range|:
name|hits
control|)
block|{
name|hit
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SearchHits
name|other
init|=
operator|(
name|SearchHits
operator|)
name|obj
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|totalHits
argument_list|,
name|other
operator|.
name|totalHits
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|maxScore
argument_list|,
name|other
operator|.
name|maxScore
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|hits
argument_list|,
name|other
operator|.
name|hits
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|totalHits
argument_list|,
name|maxScore
argument_list|,
name|Arrays
operator|.
name|hashCode
argument_list|(
name|hits
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

