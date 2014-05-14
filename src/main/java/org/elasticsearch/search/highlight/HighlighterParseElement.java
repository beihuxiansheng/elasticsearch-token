begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.highlight
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|highlight
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
name|collect
operator|.
name|Lists
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
name|Sets
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
name|vectorhighlight
operator|.
name|SimpleBoundaryScanner
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
name|collect
operator|.
name|Tuple
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
name|search
operator|.
name|SearchParseElement
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
name|SearchParseException
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
name|internal
operator|.
name|SearchContext
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
name|List
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

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayList
import|;
end_import

begin_comment
comment|/**  *<pre>  * highlight : {  *  tags_schema : "styled",  *  pre_tags : ["tag1", "tag2"],  *  post_tags : ["tag1", "tag2"],  *  order : "score",  *  highlight_filter : true,  *  fields : {  *      field1 : {  },  *      field2 : { fragment_size : 100, number_of_fragments : 2 },  *      field3 : { number_of_fragments : 5, order : "simple", tags_schema : "styled" },  *      field4 : { number_of_fragments: 0, pre_tags : ["openingTagA", "openingTagB"], post_tags : ["closingTag"] }  *  }  * }  *</pre>  */
end_comment

begin_class
DECL|class|HighlighterParseElement
specifier|public
class|class
name|HighlighterParseElement
implements|implements
name|SearchParseElement
block|{
DECL|field|DEFAULT_PRE_TAGS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DEFAULT_PRE_TAGS
init|=
operator|new
name|String
index|[]
block|{
literal|"<em>"
block|}
decl_stmt|;
DECL|field|DEFAULT_POST_TAGS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DEFAULT_POST_TAGS
init|=
operator|new
name|String
index|[]
block|{
literal|"</em>"
block|}
decl_stmt|;
DECL|field|STYLED_PRE_TAG
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|STYLED_PRE_TAG
init|=
block|{
literal|"<em class=\"hlt1\">"
block|,
literal|"<em class=\"hlt2\">"
block|,
literal|"<em class=\"hlt3\">"
block|,
literal|"<em class=\"hlt4\">"
block|,
literal|"<em class=\"hlt5\">"
block|,
literal|"<em class=\"hlt6\">"
block|,
literal|"<em class=\"hlt7\">"
block|,
literal|"<em class=\"hlt8\">"
block|,
literal|"<em class=\"hlt9\">"
block|,
literal|"<em class=\"hlt10\">"
block|}
decl_stmt|;
DECL|field|STYLED_POST_TAGS
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|STYLED_POST_TAGS
init|=
block|{
literal|"</em>"
block|}
decl_stmt|;
annotation|@
name|Override
DECL|method|parse
specifier|public
name|void
name|parse
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|String
name|topLevelFieldName
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Tuple
argument_list|<
name|String
argument_list|,
name|SearchContextHighlight
operator|.
name|FieldOptions
operator|.
name|Builder
argument_list|>
argument_list|>
name|fieldsOptions
init|=
name|newArrayList
argument_list|()
decl_stmt|;
name|SearchContextHighlight
operator|.
name|FieldOptions
operator|.
name|Builder
name|globalOptionsBuilder
init|=
operator|new
name|SearchContextHighlight
operator|.
name|FieldOptions
operator|.
name|Builder
argument_list|()
operator|.
name|preTags
argument_list|(
name|DEFAULT_PRE_TAGS
argument_list|)
operator|.
name|postTags
argument_list|(
name|DEFAULT_POST_TAGS
argument_list|)
operator|.
name|scoreOrdered
argument_list|(
literal|false
argument_list|)
operator|.
name|highlightFilter
argument_list|(
literal|false
argument_list|)
operator|.
name|requireFieldMatch
argument_list|(
literal|false
argument_list|)
operator|.
name|forceSource
argument_list|(
literal|false
argument_list|)
operator|.
name|fragmentCharSize
argument_list|(
literal|100
argument_list|)
operator|.
name|numberOfFragments
argument_list|(
literal|5
argument_list|)
operator|.
name|encoder
argument_list|(
literal|"default"
argument_list|)
operator|.
name|boundaryMaxScan
argument_list|(
name|SimpleBoundaryScanner
operator|.
name|DEFAULT_MAX_SCAN
argument_list|)
operator|.
name|boundaryChars
argument_list|(
name|SimpleBoundaryScanner
operator|.
name|DEFAULT_BOUNDARY_CHARS
argument_list|)
operator|.
name|noMatchSize
argument_list|(
literal|0
argument_list|)
operator|.
name|phraseLimit
argument_list|(
literal|256
argument_list|)
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
name|topLevelFieldName
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
literal|"pre_tags"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"preTags"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|preTagsList
init|=
name|Lists
operator|.
name|newArrayList
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
name|END_ARRAY
condition|)
block|{
name|preTagsList
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|globalOptionsBuilder
operator|.
name|preTags
argument_list|(
name|preTagsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|preTagsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"post_tags"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"postTags"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|postTagsList
init|=
name|Lists
operator|.
name|newArrayList
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
name|END_ARRAY
condition|)
block|{
name|postTagsList
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|globalOptionsBuilder
operator|.
name|postTags
argument_list|(
name|postTagsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|postTagsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"fields"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
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
name|highlightFieldName
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
if|if
condition|(
name|highlightFieldName
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"If highlighter fields is an array it must contain objects containing a single field"
argument_list|)
throw|;
block|}
name|highlightFieldName
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
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|fieldsOptions
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|highlightFieldName
argument_list|,
name|parseFields
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"If highlighter fields is an array it must contain objects containing a single field"
argument_list|)
throw|;
block|}
block|}
block|}
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
literal|"order"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|scoreOrdered
argument_list|(
literal|"score"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"tags_schema"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"tagsSchema"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|String
name|schema
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"styled"
operator|.
name|equals
argument_list|(
name|schema
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|preTags
argument_list|(
name|STYLED_PRE_TAG
argument_list|)
expr_stmt|;
name|globalOptionsBuilder
operator|.
name|postTags
argument_list|(
name|STYLED_POST_TAGS
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
literal|"highlight_filter"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"highlightFilter"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|highlightFilter
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"fragment_size"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"fragmentSize"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|fragmentCharSize
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"number_of_fragments"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"numberOfFragments"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|numberOfFragments
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"encoder"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|encoder
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
literal|"require_field_match"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"requireFieldMatch"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|requireFieldMatch
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boundary_max_scan"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"boundaryMaxScan"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|boundaryMaxScan
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boundary_chars"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"boundaryChars"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|char
index|[]
name|charsArr
init|=
name|parser
operator|.
name|text
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|Character
index|[]
name|globalBoundaryChars
init|=
operator|new
name|Character
index|[
name|charsArr
operator|.
name|length
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
name|charsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|globalBoundaryChars
index|[
name|i
index|]
operator|=
name|charsArr
index|[
name|i
index|]
expr_stmt|;
block|}
name|globalOptionsBuilder
operator|.
name|boundaryChars
argument_list|(
name|globalBoundaryChars
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"type"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|highlighterType
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
literal|"fragmenter"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|fragmenter
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
literal|"no_match_size"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"noMatchSize"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|noMatchSize
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"force_source"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"forceSource"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|forceSource
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"phrase_limit"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"phraseLimit"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|phraseLimit
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
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
operator|&&
literal|"options"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|options
argument_list|(
name|parser
operator|.
name|map
argument_list|()
argument_list|)
expr_stmt|;
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
if|if
condition|(
literal|"fields"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|String
name|highlightFieldName
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
name|highlightFieldName
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
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
name|fieldsOptions
operator|.
name|add
argument_list|(
name|Tuple
operator|.
name|tuple
argument_list|(
name|highlightFieldName
argument_list|,
name|parseFields
argument_list|(
name|parser
argument_list|,
name|context
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
literal|"highlight_query"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
operator|||
literal|"highlightQuery"
operator|.
name|equals
argument_list|(
name|topLevelFieldName
argument_list|)
condition|)
block|{
name|globalOptionsBuilder
operator|.
name|highlightQuery
argument_list|(
name|context
operator|.
name|queryParserService
argument_list|()
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
operator|.
name|query
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|SearchContextHighlight
operator|.
name|FieldOptions
name|globalOptions
init|=
name|globalOptionsBuilder
operator|.
name|build
argument_list|()
decl_stmt|;
if|if
condition|(
name|globalOptions
operator|.
name|preTags
argument_list|()
operator|!=
literal|null
operator|&&
name|globalOptions
operator|.
name|postTags
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SearchParseException
argument_list|(
name|context
argument_list|,
literal|"Highlighter global preTags are set, but global postTags are not set"
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|SearchContextHighlight
operator|.
name|Field
argument_list|>
name|fields
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
comment|// now, go over and fill all fieldsOptions with default values from the global state
for|for
control|(
name|Tuple
argument_list|<
name|String
argument_list|,
name|SearchContextHighlight
operator|.
name|FieldOptions
operator|.
name|Builder
argument_list|>
name|tuple
range|:
name|fieldsOptions
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
operator|new
name|SearchContextHighlight
operator|.
name|Field
argument_list|(
name|tuple
operator|.
name|v1
argument_list|()
argument_list|,
name|tuple
operator|.
name|v2
argument_list|()
operator|.
name|merge
argument_list|(
name|globalOptions
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|highlight
argument_list|(
operator|new
name|SearchContextHighlight
argument_list|(
name|fields
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|parseFields
specifier|private
name|SearchContextHighlight
operator|.
name|FieldOptions
operator|.
name|Builder
name|parseFields
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|SearchContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|SearchContextHighlight
operator|.
name|FieldOptions
operator|.
name|Builder
name|fieldOptionsBuilder
init|=
operator|new
name|SearchContextHighlight
operator|.
name|FieldOptions
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|String
name|fieldName
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
name|fieldName
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
literal|"pre_tags"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"preTags"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|preTagsList
init|=
name|Lists
operator|.
name|newArrayList
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
name|END_ARRAY
condition|)
block|{
name|preTagsList
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fieldOptionsBuilder
operator|.
name|preTags
argument_list|(
name|preTagsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|preTagsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"post_tags"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"postTags"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|postTagsList
init|=
name|Lists
operator|.
name|newArrayList
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
name|END_ARRAY
condition|)
block|{
name|postTagsList
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fieldOptionsBuilder
operator|.
name|postTags
argument_list|(
name|postTagsList
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|postTagsList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"matched_fields"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"matchedFields"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|matchedFields
init|=
name|Sets
operator|.
name|newHashSet
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
name|END_ARRAY
condition|)
block|{
name|matchedFields
operator|.
name|add
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fieldOptionsBuilder
operator|.
name|matchedFields
argument_list|(
name|matchedFields
argument_list|)
expr_stmt|;
block|}
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
literal|"fragment_size"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"fragmentSize"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|fragmentCharSize
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"number_of_fragments"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"numberOfFragments"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|numberOfFragments
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"fragment_offset"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"fragmentOffset"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|fragmentOffset
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"highlight_filter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"highlightFilter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|highlightFilter
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"order"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|scoreOrdered
argument_list|(
literal|"score"
operator|.
name|equals
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"require_field_match"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"requireFieldMatch"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|requireFieldMatch
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boundary_max_scan"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"boundaryMaxScan"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|boundaryMaxScan
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"boundary_chars"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"boundaryChars"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|char
index|[]
name|charsArr
init|=
name|parser
operator|.
name|text
argument_list|()
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
name|Character
index|[]
name|boundaryChars
init|=
operator|new
name|Character
index|[
name|charsArr
operator|.
name|length
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
name|charsArr
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|boundaryChars
index|[
name|i
index|]
operator|=
name|charsArr
index|[
name|i
index|]
expr_stmt|;
block|}
name|fieldOptionsBuilder
operator|.
name|boundaryChars
argument_list|(
name|boundaryChars
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"type"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|highlighterType
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
literal|"fragmenter"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|fragmenter
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
literal|"no_match_size"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"noMatchSize"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|noMatchSize
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"force_source"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"forceSource"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|forceSource
argument_list|(
name|parser
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"phrase_limit"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"phraseLimit"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|phraseLimit
argument_list|(
name|parser
operator|.
name|intValue
argument_list|()
argument_list|)
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
if|if
condition|(
literal|"highlight_query"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
operator|||
literal|"highlightQuery"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|highlightQuery
argument_list|(
name|context
operator|.
name|queryParserService
argument_list|()
operator|.
name|parse
argument_list|(
name|parser
argument_list|)
operator|.
name|query
argument_list|()
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"options"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|fieldOptionsBuilder
operator|.
name|options
argument_list|(
name|parser
operator|.
name|map
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|fieldOptionsBuilder
return|;
block|}
block|}
end_class

end_unit

