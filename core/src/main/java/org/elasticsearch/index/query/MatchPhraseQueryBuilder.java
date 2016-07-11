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
name|search
operator|.
name|Query
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
name|search
operator|.
name|MatchQuery
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Match query is a query that analyzes the text and constructs a phrase query  * as the result of the analysis.  */
end_comment

begin_class
DECL|class|MatchPhraseQueryBuilder
specifier|public
class|class
name|MatchPhraseQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|MatchPhraseQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"match_phrase"
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
DECL|field|SLOP_FIELD
specifier|public
specifier|static
specifier|final
name|ParseField
name|SLOP_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"slop"
argument_list|,
literal|"phrase_slop"
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
name|Object
name|value
decl_stmt|;
DECL|field|analyzer
specifier|private
name|String
name|analyzer
decl_stmt|;
DECL|field|slop
specifier|private
name|int
name|slop
init|=
name|MatchQuery
operator|.
name|DEFAULT_PHRASE_SLOP
decl_stmt|;
DECL|method|MatchPhraseQueryBuilder
specifier|public
name|MatchPhraseQueryBuilder
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
if|if
condition|(
name|fieldName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"["
operator|+
name|NAME
operator|+
literal|"] requires fieldName"
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
literal|"["
operator|+
name|NAME
operator|+
literal|"] requires query value"
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
DECL|method|MatchPhraseQueryBuilder
specifier|public
name|MatchPhraseQueryBuilder
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
name|readGenericValue
argument_list|()
expr_stmt|;
name|slop
operator|=
name|in
operator|.
name|readVInt
argument_list|()
expr_stmt|;
name|analyzer
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
name|writeGenericValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeOptionalString
argument_list|(
name|analyzer
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
comment|/** Returns the value used in this query. */
DECL|method|value
specifier|public
name|Object
name|value
parameter_list|()
block|{
return|return
name|this
operator|.
name|value
return|;
block|}
comment|/**      * Explicitly set the analyzer to use. Defaults to use explicit mapping      * config for the field, or, if not set, the default search analyzer.      */
DECL|method|analyzer
specifier|public
name|MatchPhraseQueryBuilder
name|analyzer
parameter_list|(
name|String
name|analyzer
parameter_list|)
block|{
name|this
operator|.
name|analyzer
operator|=
name|analyzer
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Get the analyzer to use, if previously set, otherwise<tt>null</tt> */
DECL|method|analyzer
specifier|public
name|String
name|analyzer
parameter_list|()
block|{
return|return
name|this
operator|.
name|analyzer
return|;
block|}
comment|/** Sets a slop factor for phrase queries */
DECL|method|slop
specifier|public
name|MatchPhraseQueryBuilder
name|slop
parameter_list|(
name|int
name|slop
parameter_list|)
block|{
if|if
condition|(
name|slop
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No negative slop allowed."
argument_list|)
throw|;
block|}
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/** Get the slop factor for phrase queries. */
DECL|method|slop
specifier|public
name|int
name|slop
parameter_list|()
block|{
return|return
name|this
operator|.
name|slop
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
name|MatchQueryBuilder
operator|.
name|QUERY_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
name|analyzer
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
name|MatchQueryBuilder
operator|.
name|ANALYZER_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|field
argument_list|(
name|SLOP_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|slop
argument_list|)
expr_stmt|;
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
name|IOException
block|{
comment|// validate context specific fields
if|if
condition|(
name|analyzer
operator|!=
literal|null
operator|&&
name|context
operator|.
name|getAnalysisService
argument_list|()
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"["
operator|+
name|NAME
operator|+
literal|"] analyzer ["
operator|+
name|analyzer
operator|+
literal|"] not found"
argument_list|)
throw|;
block|}
name|MatchQuery
name|matchQuery
init|=
operator|new
name|MatchQuery
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|matchQuery
operator|.
name|setAnalyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|setPhraseSlop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
return|return
name|matchQuery
operator|.
name|parse
argument_list|(
name|MatchQuery
operator|.
name|Type
operator|.
name|PHRASE
argument_list|,
name|fieldName
argument_list|,
name|value
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
name|MatchPhraseQueryBuilder
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
name|analyzer
argument_list|,
name|other
operator|.
name|analyzer
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|slop
argument_list|,
name|other
operator|.
name|slop
argument_list|)
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
name|analyzer
argument_list|,
name|slop
argument_list|)
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|Optional
argument_list|<
name|MatchPhraseQueryBuilder
argument_list|>
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
name|XContentParser
operator|.
name|Token
name|token
init|=
name|parser
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|FIELD_NAME
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
literal|"["
operator|+
name|NAME
operator|+
literal|"] query malformed, no field"
argument_list|)
throw|;
block|}
name|String
name|fieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
name|Object
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
name|String
name|analyzer
init|=
literal|null
decl_stmt|;
name|int
name|slop
init|=
name|MatchQuery
operator|.
name|DEFAULT_PHRASE_SLOP
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
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
name|String
name|currentFieldName
init|=
literal|null
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
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MatchQueryBuilder
operator|.
name|QUERY_FIELD
argument_list|)
condition|)
block|{
name|value
operator|=
name|parser
operator|.
name|objectText
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|MatchQueryBuilder
operator|.
name|ANALYZER_FIELD
argument_list|)
condition|)
block|{
name|analyzer
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|parseContext
operator|.
name|getParseFieldMatcher
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
name|getParseFieldMatcher
argument_list|()
operator|.
name|match
argument_list|(
name|currentFieldName
argument_list|,
name|SLOP_FIELD
argument_list|)
condition|)
block|{
name|slop
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
name|getParseFieldMatcher
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
literal|"["
operator|+
name|NAME
operator|+
literal|"] query does not support ["
operator|+
name|currentFieldName
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
literal|"] unknown token ["
operator|+
name|token
operator|+
literal|"] after ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
name|parser
operator|.
name|objectText
argument_list|()
expr_stmt|;
comment|// move to the next token
name|token
operator|=
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
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
literal|"["
operator|+
name|NAME
operator|+
literal|"] query parsed in simplified form, with direct field name, "
operator|+
literal|"but included more options than just the field name, possibly use its 'options' form, with 'query' element?"
argument_list|)
throw|;
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
literal|"No text specified for text query"
argument_list|)
throw|;
block|}
name|MatchPhraseQueryBuilder
name|matchQuery
init|=
operator|new
name|MatchPhraseQueryBuilder
argument_list|(
name|fieldName
argument_list|,
name|value
argument_list|)
decl_stmt|;
name|matchQuery
operator|.
name|analyzer
argument_list|(
name|analyzer
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|slop
argument_list|(
name|slop
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|queryName
argument_list|(
name|queryName
argument_list|)
expr_stmt|;
name|matchQuery
operator|.
name|boost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|matchQuery
argument_list|)
return|;
block|}
block|}
end_class

end_unit

