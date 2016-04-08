begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.query
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
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
name|util
operator|.
name|automaton
operator|.
name|Operations
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
name|Strings
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
name|mapper
operator|.
name|MappedFieldType
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
name|support
operator|.
name|QueryParsers
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
comment|/**  * A Query that does fuzzy matching for a specific value.  */
end_comment

begin_class
DECL|class|RegexpQueryBuilder
specifier|public
class|class
name|RegexpQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|RegexpQueryBuilder
argument_list|>
implements|implements
name|MultiTermQueryBuilder
argument_list|<
name|RegexpQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"regexp"
decl_stmt|;
DECL|field|QUERY_NAME_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|QUERY_NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
name|NAME
argument_list|)
decl_stmt|;
DECL|field|DEFAULT_FLAGS_VALUE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_FLAGS_VALUE
init|=
name|RegexpFlag
operator|.
name|ALL
operator|.
name|value
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_MAX_DETERMINIZED_STATES
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_DETERMINIZED_STATES
init|=
name|Operations
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
decl_stmt|;
DECL|field|NAME_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|NAME_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"_name"
argument_list|)
operator|.
name|withAllDeprecated
argument_list|(
literal|"query name is not supported in short version of regexp query"
argument_list|)
decl_stmt|;
DECL|field|FLAGS_VALUE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|FLAGS_VALUE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"flags_value"
argument_list|)
decl_stmt|;
DECL|field|MAX_DETERMINIZED_STATES_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|MAX_DETERMINIZED_STATES_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"max_determinized_states"
argument_list|)
decl_stmt|;
DECL|field|FLAGS_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|FLAGS_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"flags"
argument_list|)
decl_stmt|;
DECL|field|REWRITE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|REWRITE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"rewrite"
argument_list|)
decl_stmt|;
DECL|field|VALUE_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|VALUE_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
DECL|field|fieldName
specifier|private
specifier|final
name|String
name|fieldName
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
DECL|field|flagsValue
specifier|private
name|int
name|flagsValue
init|=
name|DEFAULT_FLAGS_VALUE
decl_stmt|;
DECL|field|maxDeterminizedStates
specifier|private
name|int
name|maxDeterminizedStates
init|=
name|DEFAULT_MAX_DETERMINIZED_STATES
decl_stmt|;
DECL|field|rewrite
specifier|private
name|String
name|rewrite
decl_stmt|;
comment|/**      * Constructs a new regex query.      *      * @param fieldName  The name of the field      * @param value The regular expression      */
DECL|method|RegexpQueryBuilder
specifier|public
name|RegexpQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|Strings
operator|.
name|isEmpty
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"field name is null or empty"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value cannot be null."
argument_list|)
throw|;
block|}
name|this
operator|.
name|fieldName
operator|=
name|fieldName
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Read from a stream.      */
DECL|method|RegexpQueryBuilder
specifier|public
name|RegexpQueryBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|fieldName
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|value
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|flagsValue
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|maxDeterminizedStates
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|rewrite
operator|=
name|in
operator|.
name|readOptionalString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|doWriteTo
specifier|protected
name|void
name|doWriteTo
parameter_list|(
name|StreamOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeString
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeString
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|flagsValue
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|rewrite
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the field name used in this query. */
DECL|method|fieldName
specifier|public
name|String
name|fieldName
parameter_list|()
block|{
return|return
name|this
operator|.
name|fieldName
return|;
block|}
comment|/**      *  Returns the value used in this query.      */
DECL|method|value
specifier|public
name|String
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
DECL|method|flags
specifier|public
name|RegexpQueryBuilder
name|flags
parameter_list|(
name|RegexpFlag
modifier|...
name|flags
parameter_list|)
block|{
if|if
condition|(
name|flags
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|flagsValue
operator|=
name|DEFAULT_FLAGS_VALUE
expr_stmt|;
return|return
name|this
return|;
block|}
name|int
name|value
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|flags
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|value
operator|=
name|RegexpFlag
operator|.
name|ALL
operator|.
name|value
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|RegexpFlag
name|flag
range|:
name|flags
control|)
block|{
name|value
operator||=
name|flag
operator|.
name|value
expr_stmt|;
block|}
block|}
name|this
operator|.
name|flagsValue
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|flags
specifier|public
name|RegexpQueryBuilder
name|flags
parameter_list|(
name|int
name|flags
parameter_list|)
block|{
name|this
operator|.
name|flagsValue
operator|=
name|flags
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|flags
specifier|public
name|int
name|flags
parameter_list|()
block|{
return|return
name|this
operator|.
name|flagsValue
return|;
block|}
comment|/**      * Sets the regexp maxDeterminizedStates.      */
DECL|method|maxDeterminizedStates
specifier|public
name|RegexpQueryBuilder
name|maxDeterminizedStates
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|maxDeterminizedStates
operator|=
name|value
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|maxDeterminizedStates
specifier|public
name|int
name|maxDeterminizedStates
parameter_list|()
block|{
return|return
name|this
operator|.
name|maxDeterminizedStates
return|;
block|}
DECL|method|rewrite
specifier|public
name|RegexpQueryBuilder
name|rewrite
parameter_list|(
name|String
name|rewrite
parameter_list|)
block|{
name|this
operator|.
name|rewrite
operator|=
name|rewrite
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|rewrite
specifier|public
name|String
name|rewrite
parameter_list|()
block|{
return|return
name|this
operator|.
name|rewrite
return|;
block|}
annotation|@
name|Override
DECL|method|doXContent
specifier|protected
name|void
name|doXContent
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
name|startObject
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|VALUE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|this
operator|.
name|value
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|FLAGS_VALUE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|flagsValue
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|MAX_DETERMINIZED_STATES_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|maxDeterminizedStates
argument_list|)
expr_stmt|;
if|if
condition|(
name|rewrite
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|REWRITE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|rewrite
argument_list|)
expr_stmt|;
block|}
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|RegexpQueryBuilder
name|fromXContent
parameter_list|(
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|String
name|rewrite
init|=
literal|null
decl_stmt|;
name|String
name|value
init|=
literal|null
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
name|int
name|flagsValue
init|=
name|RegexpQueryBuilder
operator|.
name|DEFAULT_FLAGS_VALUE
decl_stmt|;
name|int
name|maxDeterminizedStates
init|=
name|RegexpQueryBuilder
operator|.
name|DEFAULT_MAX_DETERMINIZED_STATES
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
name|XContentParser
operator|.
name|Token
name|token
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
name|parseContext
operator|.
name|isDeprecatedSetting
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
comment|// skip
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
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
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
else|else
block|{
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|VALUE_FIELD
argument_list|)
condition|)
block|{
name|value
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|BOOST_FIELD
argument_list|)
condition|)
block|{
name|boost
operator|=
name|parser
operator|.
name|floatValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|REWRITE_FIELD
argument_list|)
condition|)
block|{
name|rewrite
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FLAGS_FIELD
argument_list|)
condition|)
block|{
name|String
name|flags
init|=
name|parser
operator|.
name|textOrNull
argument_list|()
decl_stmt|;
name|flagsValue
operator|=
name|RegexpFlag
operator|.
name|resolveValue
argument_list|(
name|flags
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MAX_DETERMINIZED_STATES_FIELD
argument_list|)
condition|)
block|{
name|maxDeterminizedStates
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|FLAGS_VALUE_FIELD
argument_list|)
condition|)
block|{
name|flagsValue
operator|=
name|parser
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|AbstractQueryBuilder
operator|.
name|NAME_FIELD
argument_list|)
condition|)
block|{
name|queryName
operator|=
name|parser
operator|.
name|text
argument_list|()
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
literal|"[regexp] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|parseContext
operator|.
name|parseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|NAME_FIELD
argument_list|)
condition|)
block|{
name|queryName
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|fieldName
operator|=
name|currentFieldName
expr_stmt|;
name|value
operator|=
name|parser
operator|.
name|textOrNull
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|value
operator|==
literal|null
condition|)
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
literal|"No value specified for regexp query"
argument_list|)
throw|;
block|}
return|return
operator|new
name|RegexpQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
operator|.
name|flags
argument_list|(
name|flagsValue
argument_list|)
operator|.
name|maxDeterminizedStates
argument_list|(
name|maxDeterminizedStates
argument_list|)
operator|.
name|rewrite
argument_list|(
name|rewrite
argument_list|)
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
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
annotation|@
name|Override
DECL|method|doToQuery
specifier|protected
name|Query
name|doToQuery
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|QueryShardException
throws|,
name|IOException
block|{
name|MultiTermQuery
operator|.
name|RewriteMethod
name|method
init|=
name|QueryParsers
operator|.
name|parseRewriteMethod
argument_list|(
name|context
operator|.
name|parseFieldMatcher
argument_list|()
argument_list|,
name|rewrite
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|MappedFieldType
name|fieldType
init|=
name|context
operator|.
name|fieldMapper
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
condition|)
block|{
name|query
operator|=
name|fieldType
operator|.
name|regexpQuery
argument_list|(
name|value
argument_list|,
name|flagsValue
argument_list|,
name|maxDeterminizedStates
argument_list|,
name|method
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|RegexpQuery
name|regexpQuery
init|=
operator|new
name|RegexpQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|fieldName
argument_list|,
name|BytesRefs
operator|.
name|toBytesRef
argument_list|(
name|value
argument_list|)
argument_list|)
argument_list|,
name|flagsValue
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
name|regexpQuery
operator|.
name|setRewriteMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
name|regexpQuery
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|doHashCode
specifier|protected
name|int
name|doHashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|,
name|flagsValue
argument_list|,
name|maxDeterminizedStates
argument_list|,
name|rewrite
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|RegexpQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|,
name|other
operator|.
name|fieldName
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|value
argument_list|,
name|other
operator|.
name|value
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|flagsValue
argument_list|,
name|other
operator|.
name|flagsValue
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|maxDeterminizedStates
argument_list|,
name|other
operator|.
name|maxDeterminizedStates
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|rewrite
argument_list|,
name|other
operator|.
name|rewrite
argument_list|)
return|;
block|}
block|}
end_class

end_unit

