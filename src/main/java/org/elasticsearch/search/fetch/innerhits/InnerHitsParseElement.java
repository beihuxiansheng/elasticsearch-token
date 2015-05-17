begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.fetch.innerhits
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|fetch
operator|.
name|innerhits
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
name|MatchAllDocsQuery
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
name|DocumentMapper
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
name|mapper
operator|.
name|object
operator|.
name|ObjectMapper
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
name|fetch
operator|.
name|fielddata
operator|.
name|FieldDataFieldsParseElement
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
name|fetch
operator|.
name|script
operator|.
name|ScriptFieldsParseElement
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
name|fetch
operator|.
name|source
operator|.
name|FetchSourceParseElement
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
name|highlight
operator|.
name|HighlighterParseElement
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
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|internal
operator|.
name|SubSearchContext
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
name|sort
operator|.
name|SortParseElement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import static
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
name|InnerHitsQueryParserHelper
operator|.
name|parseCommonInnerHitOptions
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|InnerHitsParseElement
specifier|public
class|class
name|InnerHitsParseElement
implements|implements
name|SearchParseElement
block|{
DECL|field|sortParseElement
specifier|private
specifier|final
name|SortParseElement
name|sortParseElement
decl_stmt|;
DECL|field|sourceParseElement
specifier|private
specifier|final
name|FetchSourceParseElement
name|sourceParseElement
decl_stmt|;
DECL|field|highlighterParseElement
specifier|private
specifier|final
name|HighlighterParseElement
name|highlighterParseElement
decl_stmt|;
DECL|field|fieldDataFieldsParseElement
specifier|private
specifier|final
name|FieldDataFieldsParseElement
name|fieldDataFieldsParseElement
decl_stmt|;
DECL|field|scriptFieldsParseElement
specifier|private
specifier|final
name|ScriptFieldsParseElement
name|scriptFieldsParseElement
decl_stmt|;
DECL|method|InnerHitsParseElement
specifier|public
name|InnerHitsParseElement
parameter_list|(
name|SortParseElement
name|sortParseElement
parameter_list|,
name|FetchSourceParseElement
name|sourceParseElement
parameter_list|,
name|HighlighterParseElement
name|highlighterParseElement
parameter_list|,
name|FieldDataFieldsParseElement
name|fieldDataFieldsParseElement
parameter_list|,
name|ScriptFieldsParseElement
name|scriptFieldsParseElement
parameter_list|)
block|{
name|this
operator|.
name|sortParseElement
operator|=
name|sortParseElement
expr_stmt|;
name|this
operator|.
name|sourceParseElement
operator|=
name|sourceParseElement
expr_stmt|;
name|this
operator|.
name|highlighterParseElement
operator|=
name|highlighterParseElement
expr_stmt|;
name|this
operator|.
name|fieldDataFieldsParseElement
operator|=
name|fieldDataFieldsParseElement
expr_stmt|;
name|this
operator|.
name|scriptFieldsParseElement
operator|=
name|scriptFieldsParseElement
expr_stmt|;
block|}
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
name|searchContext
parameter_list|)
throws|throws
name|Exception
block|{
name|QueryParseContext
name|parseContext
init|=
name|searchContext
operator|.
name|queryParserService
argument_list|()
operator|.
name|getParseContext
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|reset
argument_list|(
name|parser
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
name|innerHitsMap
init|=
name|parseInnerHits
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|,
name|searchContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerHitsMap
operator|!=
literal|null
condition|)
block|{
name|searchContext
operator|.
name|innerHits
argument_list|(
operator|new
name|InnerHitsContext
argument_list|(
name|innerHitsMap
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|parseInnerHits
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
name|parseInnerHits
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
name|innerHitsMap
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
name|IllegalArgumentException
argument_list|(
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" in [inner_hits]: inner_hit definitions must start with the name of the inner_hit."
argument_list|)
throw|;
block|}
specifier|final
name|String
name|innerHitName
init|=
name|parser
operator|.
name|currentName
argument_list|()
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
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Inner hit definition for ["
operator|+
name|innerHitName
operator|+
literal|" starts with a ["
operator|+
name|token
operator|+
literal|"], expected a ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|+
literal|"]."
argument_list|)
throw|;
block|}
name|InnerHitsContext
operator|.
name|BaseInnerHits
name|innerHits
init|=
name|parseInnerHit
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|,
name|searchContext
argument_list|,
name|innerHitName
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerHitsMap
operator|==
literal|null
condition|)
block|{
name|innerHitsMap
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|innerHitsMap
operator|.
name|put
argument_list|(
name|innerHitName
argument_list|,
name|innerHits
argument_list|)
expr_stmt|;
block|}
return|return
name|innerHitsMap
return|;
block|}
DECL|method|parseInnerHit
specifier|private
name|InnerHitsContext
operator|.
name|BaseInnerHits
name|parseInnerHit
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|,
name|String
name|innerHitName
parameter_list|)
throws|throws
name|Exception
block|{
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
name|IllegalArgumentException
argument_list|(
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" inside inner hit definition. Either specify [path] or [type] object"
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
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Inner hit definition for ["
operator|+
name|innerHitName
operator|+
literal|" starts with a ["
operator|+
name|token
operator|+
literal|"], expected a ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|+
literal|"]."
argument_list|)
throw|;
block|}
name|String
name|nestedPath
init|=
literal|null
decl_stmt|;
name|String
name|type
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|fieldName
condition|)
block|{
case|case
literal|"path"
case|:
name|nestedPath
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
break|break;
case|case
literal|"type"
case|:
name|type
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Either path or type object must be defined"
argument_list|)
throw|;
block|}
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
name|FIELD_NAME
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unexpected token "
operator|+
name|token
operator|+
literal|" inside inner hit definition. Either specify [path] or [type] object"
argument_list|)
throw|;
block|}
name|fieldName
operator|=
name|parser
operator|.
name|currentName
argument_list|()
expr_stmt|;
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
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Inner hit definition for ["
operator|+
name|innerHitName
operator|+
literal|" starts with a ["
operator|+
name|token
operator|+
literal|"], expected a ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|START_OBJECT
operator|+
literal|"]."
argument_list|)
throw|;
block|}
specifier|final
name|InnerHitsContext
operator|.
name|BaseInnerHits
name|innerHits
decl_stmt|;
if|if
condition|(
name|nestedPath
operator|!=
literal|null
condition|)
block|{
name|innerHits
operator|=
name|parseNested
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|,
name|searchContext
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
name|innerHits
operator|=
name|parseParentChild
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|,
name|searchContext
argument_list|,
name|fieldName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Either [path] or [type] must be defined"
argument_list|)
throw|;
block|}
comment|// Completely consume all json objects:
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
name|IllegalArgumentException
argument_list|(
literal|"Expected ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|+
literal|"] token, but got a ["
operator|+
name|token
operator|+
literal|"] token."
argument_list|)
throw|;
block|}
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
name|IllegalArgumentException
argument_list|(
literal|"Expected ["
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|+
literal|"] token, but got a ["
operator|+
name|token
operator|+
literal|"] token."
argument_list|)
throw|;
block|}
return|return
name|innerHits
return|;
block|}
DECL|method|parseParentChild
specifier|private
name|InnerHitsContext
operator|.
name|ParentChildInnerHits
name|parseParentChild
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|Exception
block|{
name|ParseResult
name|parseResult
init|=
name|parseSubSearchContext
argument_list|(
name|searchContext
argument_list|,
name|parseContext
argument_list|,
name|parser
argument_list|)
decl_stmt|;
name|DocumentMapper
name|documentMapper
init|=
name|searchContext
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|documentMapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"type ["
operator|+
name|type
operator|+
literal|"] doesn't exist"
argument_list|)
throw|;
block|}
return|return
operator|new
name|InnerHitsContext
operator|.
name|ParentChildInnerHits
argument_list|(
name|parseResult
operator|.
name|context
argument_list|()
argument_list|,
name|parseResult
operator|.
name|query
argument_list|()
argument_list|,
name|parseResult
operator|.
name|childInnerHits
argument_list|()
argument_list|,
name|parseContext
operator|.
name|mapperService
argument_list|()
argument_list|,
name|documentMapper
argument_list|)
return|;
block|}
DECL|method|parseNested
specifier|private
name|InnerHitsContext
operator|.
name|NestedInnerHits
name|parseNested
parameter_list|(
name|XContentParser
name|parser
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|,
name|SearchContext
name|searchContext
parameter_list|,
name|String
name|nestedPath
parameter_list|)
throws|throws
name|Exception
block|{
name|MapperService
operator|.
name|SmartNameObjectMapper
name|smartNameObjectMapper
init|=
name|searchContext
operator|.
name|smartNameObjectMapper
argument_list|(
name|nestedPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|smartNameObjectMapper
operator|==
literal|null
operator|||
operator|!
name|smartNameObjectMapper
operator|.
name|hasMapper
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path ["
operator|+
name|nestedPath
operator|+
literal|"] doesn't exist"
argument_list|)
throw|;
block|}
name|ObjectMapper
name|childObjectMapper
init|=
name|smartNameObjectMapper
operator|.
name|mapper
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|childObjectMapper
operator|.
name|nested
argument_list|()
operator|.
name|isNested
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"path ["
operator|+
name|nestedPath
operator|+
literal|"] isn't nested"
argument_list|)
throw|;
block|}
name|ObjectMapper
name|parentObjectMapper
init|=
name|parseContext
operator|.
name|nestedScope
argument_list|()
operator|.
name|nextLevel
argument_list|(
name|childObjectMapper
argument_list|)
decl_stmt|;
name|ParseResult
name|parseResult
init|=
name|parseSubSearchContext
argument_list|(
name|searchContext
argument_list|,
name|parseContext
argument_list|,
name|parser
argument_list|)
decl_stmt|;
name|parseContext
operator|.
name|nestedScope
argument_list|()
operator|.
name|previousLevel
argument_list|()
expr_stmt|;
return|return
operator|new
name|InnerHitsContext
operator|.
name|NestedInnerHits
argument_list|(
name|parseResult
operator|.
name|context
argument_list|()
argument_list|,
name|parseResult
operator|.
name|query
argument_list|()
argument_list|,
name|parseResult
operator|.
name|childInnerHits
argument_list|()
argument_list|,
name|parentObjectMapper
argument_list|,
name|childObjectMapper
argument_list|)
return|;
block|}
DECL|method|parseSubSearchContext
specifier|private
name|ParseResult
name|parseSubSearchContext
parameter_list|(
name|SearchContext
name|searchContext
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|,
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|Exception
block|{
name|Query
name|query
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
name|childInnerHits
init|=
literal|null
decl_stmt|;
name|SubSearchContext
name|subSearchContext
init|=
operator|new
name|SubSearchContext
argument_list|(
name|searchContext
argument_list|)
decl_stmt|;
name|String
name|fieldName
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
name|START_OBJECT
condition|)
block|{
if|if
condition|(
literal|"query"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|query
operator|=
name|searchContext
operator|.
name|queryParserService
argument_list|()
operator|.
name|parseInnerQuery
argument_list|(
name|parseContext
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"inner_hits"
operator|.
name|equals
argument_list|(
name|fieldName
argument_list|)
condition|)
block|{
name|childInnerHits
operator|=
name|parseInnerHits
argument_list|(
name|parser
argument_list|,
name|parseContext
argument_list|,
name|searchContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|parseCommonInnerHitOptions
argument_list|(
name|parser
argument_list|,
name|token
argument_list|,
name|fieldName
argument_list|,
name|subSearchContext
argument_list|,
name|sortParseElement
argument_list|,
name|sourceParseElement
argument_list|,
name|highlighterParseElement
argument_list|,
name|scriptFieldsParseElement
argument_list|,
name|fieldDataFieldsParseElement
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|parseCommonInnerHitOptions
argument_list|(
name|parser
argument_list|,
name|token
argument_list|,
name|fieldName
argument_list|,
name|subSearchContext
argument_list|,
name|sortParseElement
argument_list|,
name|sourceParseElement
argument_list|,
name|highlighterParseElement
argument_list|,
name|scriptFieldsParseElement
argument_list|,
name|fieldDataFieldsParseElement
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
name|query
operator|=
operator|new
name|MatchAllDocsQuery
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|ParseResult
argument_list|(
name|subSearchContext
argument_list|,
name|query
argument_list|,
name|childInnerHits
argument_list|)
return|;
block|}
DECL|class|ParseResult
specifier|private
specifier|static
specifier|final
class|class
name|ParseResult
block|{
DECL|field|context
specifier|private
specifier|final
name|SubSearchContext
name|context
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|Query
name|query
decl_stmt|;
DECL|field|childInnerHits
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
name|childInnerHits
decl_stmt|;
DECL|method|ParseResult
specifier|private
name|ParseResult
parameter_list|(
name|SubSearchContext
name|context
parameter_list|,
name|Query
name|query
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
name|childInnerHits
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|childInnerHits
operator|=
name|childInnerHits
expr_stmt|;
block|}
DECL|method|context
specifier|public
name|SubSearchContext
name|context
parameter_list|()
block|{
return|return
name|context
return|;
block|}
DECL|method|query
specifier|public
name|Query
name|query
parameter_list|()
block|{
return|return
name|query
return|;
block|}
DECL|method|childInnerHits
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|InnerHitsContext
operator|.
name|BaseInnerHits
argument_list|>
name|childInnerHits
parameter_list|()
block|{
return|return
name|childInnerHits
return|;
block|}
block|}
block|}
end_class

end_unit

