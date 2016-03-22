begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.sort
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|sort
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
name|search
operator|.
name|SortField
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
name|ParseField
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
name|ParseFieldMatcher
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
name|ParsingException
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
name|index
operator|.
name|query
operator|.
name|QueryParseContext
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * A sort builder allowing to sort by score.  */
end_comment

begin_class
DECL|class|ScoreSortBuilder
specifier|public
class|class
name|ScoreSortBuilder
extends|extends
name|SortBuilder
argument_list|<
name|ScoreSortBuilder
argument_list|>
implements|implements
name|SortBuilderParser
argument_list|<
name|ScoreSortBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|private
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"_score"
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|ScoreSortBuilder
name|PROTOTYPE
init|=
operator|new
name|ScoreSortBuilder
argument_list|()
decl_stmt|;
DECL|field|REVERSE_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|REVERSE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"reverse"
argument_list|)
decl_stmt|;
DECL|field|ORDER_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|ORDER_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"order"
argument_list|)
decl_stmt|;
DECL|field|SORT_SCORE
specifier|private
specifier|static
specifier|final
name|SortField
name|SORT_SCORE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|)
decl_stmt|;
DECL|field|SORT_SCORE_REVERSE
specifier|private
specifier|static
specifier|final
name|SortField
name|SORT_SCORE_REVERSE
init|=
operator|new
name|SortField
argument_list|(
literal|null
argument_list|,
name|SortField
operator|.
name|Type
operator|.
name|SCORE
argument_list|,
literal|true
argument_list|)
decl_stmt|;
DECL|method|ScoreSortBuilder
specifier|public
name|ScoreSortBuilder
parameter_list|()
block|{
comment|// order defaults to desc when sorting on the _score
name|order
argument_list|(
name|SortOrder
operator|.
name|DESC
argument_list|)
expr_stmt|;
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
name|NAME
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|ORDER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|order
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
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|ScoreSortBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|context
parameter_list|,
name|String
name|elementName
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|context
operator|.
name|parser
argument_list|()
decl_stmt|;
name|ParseFieldMatcher
name|matcher
init|=
name|context
operator|.
name|parseFieldMatcher
argument_list|()
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|currentName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|ScoreSortBuilder
name|result
init|=
operator|new
name|ScoreSortBuilder
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
name|currentName
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
name|matcher
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|REVERSE_FIELD
argument_list|)
condition|)
block|{
if|if
condition|(
name|parser
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|result
operator|.
name|order
argument_list|(
name|SortOrder
operator|.
name|ASC
argument_list|)
expr_stmt|;
block|}
comment|// else we keep the default DESC
block|}
elseif|else
if|if
condition|(
name|matcher
operator|.
name|match
argument_list|(
name|currentName
argument_list|,
name|ORDER_FIELD
argument_list|)
condition|)
block|{
name|result
operator|.
name|order
argument_list|(
name|SortOrder
operator|.
name|fromString
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|NAME
operator|+
literal|"] failed to parse field ["
operator|+
name|currentName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ParsingException
argument_list|(
name|parser
operator|.
name|getTokenLocation
argument_list|()
argument_list|,
literal|"["
operator|+
name|NAME
operator|+
literal|"] unexpected token ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
return|return
name|result
return|;
block|}
DECL|method|build
specifier|public
name|SortField
name|build
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|order
operator|==
name|SortOrder
operator|.
name|DESC
condition|)
block|{
return|return
name|SORT_SCORE
return|;
block|}
else|else
block|{
return|return
name|SORT_SCORE_REVERSE
return|;
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
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|object
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|object
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ScoreSortBuilder
name|other
init|=
operator|(
name|ScoreSortBuilder
operator|)
name|object
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|order
argument_list|,
name|other
operator|.
name|order
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
name|this
operator|.
name|order
argument_list|)
return|;
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
name|order
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|ScoreSortBuilder
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreSortBuilder
name|builder
init|=
operator|new
name|ScoreSortBuilder
argument_list|()
operator|.
name|order
argument_list|(
name|SortOrder
operator|.
name|readOrderFrom
argument_list|(
name|in
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|getWriteableName
specifier|public
name|String
name|getWriteableName
parameter_list|()
block|{
return|return
name|NAME
return|;
block|}
block|}
end_class

end_unit

