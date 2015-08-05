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
name|IndexReader
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
name|MultiDocValues
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
name|search
operator|.
name|join
operator|.
name|BitDocIdSetFilter
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
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|join
operator|.
name|JoinUtil
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
name|join
operator|.
name|ScoreMode
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
name|inject
operator|.
name|Inject
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
name|search
operator|.
name|Queries
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
name|fielddata
operator|.
name|IndexParentChildFieldData
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
name|plain
operator|.
name|ParentChildIndexFieldData
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
name|internal
operator|.
name|ParentFieldMapper
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
name|InnerHitsQueryParserHelper
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
name|XContentStructure
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
name|child
operator|.
name|ChildrenConstantScoreQuery
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
name|child
operator|.
name|ChildrenQuery
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
name|child
operator|.
name|ScoreType
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
name|innerhits
operator|.
name|InnerHitsContext
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|HasChildQueryParser
specifier|public
class|class
name|HasChildQueryParser
extends|extends
name|BaseQueryParserTemp
block|{
DECL|field|QUERY_FIELD
specifier|private
specifier|static
specifier|final
name|ParseField
name|QUERY_FIELD
init|=
operator|new
name|ParseField
argument_list|(
literal|"query"
argument_list|,
literal|"filter"
argument_list|)
decl_stmt|;
DECL|field|innerHitsQueryParserHelper
specifier|private
specifier|final
name|InnerHitsQueryParserHelper
name|innerHitsQueryParserHelper
decl_stmt|;
annotation|@
name|Inject
DECL|method|HasChildQueryParser
specifier|public
name|HasChildQueryParser
parameter_list|(
name|InnerHitsQueryParserHelper
name|innerHitsQueryParserHelper
parameter_list|)
block|{
name|this
operator|.
name|innerHitsQueryParserHelper
operator|=
name|innerHitsQueryParserHelper
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|names
specifier|public
name|String
index|[]
name|names
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
name|HasChildQueryBuilder
operator|.
name|NAME
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
name|HasChildQueryBuilder
operator|.
name|NAME
argument_list|)
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|parse
specifier|public
name|Query
name|parse
parameter_list|(
name|QueryShardContext
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|QueryParseContext
name|parseContext
init|=
name|context
operator|.
name|parseContext
argument_list|()
decl_stmt|;
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|boolean
name|queryFound
init|=
literal|false
decl_stmt|;
name|float
name|boost
init|=
name|AbstractQueryBuilder
operator|.
name|DEFAULT_BOOST
decl_stmt|;
name|String
name|childType
init|=
literal|null
decl_stmt|;
name|ScoreType
name|scoreType
init|=
name|ScoreType
operator|.
name|NONE
decl_stmt|;
name|int
name|minChildren
init|=
literal|0
decl_stmt|;
name|int
name|maxChildren
init|=
literal|0
decl_stmt|;
name|int
name|shortCircuitParentDocSet
init|=
literal|8192
decl_stmt|;
name|String
name|queryName
init|=
literal|null
decl_stmt|;
name|Tuple
argument_list|<
name|String
argument_list|,
name|SubSearchContext
argument_list|>
name|innerHits
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
name|XContentStructure
operator|.
name|InnerQuery
name|iq
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
comment|// Usually, the query would be parsed here, but the child
comment|// type may not have been extracted yet, so use the
comment|// XContentStructure.<type> facade to parse if available,
comment|// or delay parsing if not.
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
name|QUERY_FIELD
argument_list|)
condition|)
block|{
name|iq
operator|=
operator|new
name|XContentStructure
operator|.
name|InnerQuery
argument_list|(
name|parseContext
argument_list|,
name|childType
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|String
index|[]
block|{
name|childType
block|}
argument_list|)
expr_stmt|;
name|queryFound
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"inner_hits"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|innerHits
operator|=
name|innerHitsQueryParserHelper
operator|.
name|parse
argument_list|(
name|parseContext
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
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
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"child_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"childType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|childType
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
literal|"score_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"scoreType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scoreType
operator|=
name|ScoreType
operator|.
name|fromString
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
literal|"score_mode"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"scoreMode"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|scoreType
operator|=
name|ScoreType
operator|.
name|fromString
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
literal|"boost"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
literal|"min_children"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"minChildren"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|minChildren
operator|=
name|parser
operator|.
name|intValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"max_children"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"maxChildren"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|maxChildren
operator|=
name|parser
operator|.
name|intValue
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"short_circuit_cutoff"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|shortCircuitParentDocSet
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
literal|"_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child] query does not support ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|queryFound
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child] requires 'query' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|childType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child] requires 'type' field"
argument_list|)
throw|;
block|}
name|Query
name|innerQuery
init|=
name|iq
operator|.
name|asQuery
argument_list|(
name|childType
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerQuery
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|innerQuery
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
name|DocumentMapper
name|childDocMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|childType
argument_list|)
decl_stmt|;
if|if
condition|(
name|childDocMapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child] No mapping for for type ["
operator|+
name|childType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|ParentFieldMapper
name|parentFieldMapper
init|=
name|childDocMapper
operator|.
name|parentFieldMapper
argument_list|()
decl_stmt|;
if|if
condition|(
name|parentFieldMapper
operator|.
name|active
argument_list|()
operator|==
literal|false
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child] _parent field has no parent type configured"
argument_list|)
throw|;
block|}
if|if
condition|(
name|innerHits
operator|!=
literal|null
condition|)
block|{
name|ParsedQuery
name|parsedQuery
init|=
operator|new
name|ParsedQuery
argument_list|(
name|innerQuery
argument_list|,
name|context
operator|.
name|copyNamedQueries
argument_list|()
argument_list|)
decl_stmt|;
name|InnerHitsContext
operator|.
name|ParentChildInnerHits
name|parentChildInnerHits
init|=
operator|new
name|InnerHitsContext
operator|.
name|ParentChildInnerHits
argument_list|(
name|innerHits
operator|.
name|v2
argument_list|()
argument_list|,
name|parsedQuery
argument_list|,
literal|null
argument_list|,
name|context
operator|.
name|mapperService
argument_list|()
argument_list|,
name|childDocMapper
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|innerHits
operator|.
name|v1
argument_list|()
operator|!=
literal|null
condition|?
name|innerHits
operator|.
name|v1
argument_list|()
else|:
name|childType
decl_stmt|;
name|context
operator|.
name|addInnerHits
argument_list|(
name|name
argument_list|,
name|parentChildInnerHits
argument_list|)
expr_stmt|;
block|}
name|String
name|parentType
init|=
name|parentFieldMapper
operator|.
name|type
argument_list|()
decl_stmt|;
name|DocumentMapper
name|parentDocMapper
init|=
name|context
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|parentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|parentDocMapper
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child]  Type ["
operator|+
name|childType
operator|+
literal|"] points to a non existent parent type ["
operator|+
name|parentType
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxChildren
operator|>
literal|0
operator|&&
name|maxChildren
operator|<
name|minChildren
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
argument_list|,
literal|"[has_child] 'max_children' is less than 'min_children'"
argument_list|)
throw|;
block|}
name|BitDocIdSetFilter
name|nonNestedDocsFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentDocMapper
operator|.
name|hasNestedObjects
argument_list|()
condition|)
block|{
name|nonNestedDocsFilter
operator|=
name|context
operator|.
name|bitsetFilter
argument_list|(
name|Queries
operator|.
name|newNonNestedFilter
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// wrap the query with type query
name|innerQuery
operator|=
name|Queries
operator|.
name|filtered
argument_list|(
name|innerQuery
argument_list|,
name|childDocMapper
operator|.
name|typeFilter
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Query
name|query
decl_stmt|;
specifier|final
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
init|=
name|context
operator|.
name|getForField
argument_list|(
name|parentFieldMapper
operator|.
name|fieldType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|indexVersionCreated
argument_list|()
operator|.
name|onOrAfter
argument_list|(
name|Version
operator|.
name|V_2_0_0_beta1
argument_list|)
condition|)
block|{
name|query
operator|=
name|joinUtilHelper
argument_list|(
name|parentType
argument_list|,
name|parentChildIndexFieldData
argument_list|,
name|parentDocMapper
operator|.
name|typeFilter
argument_list|()
argument_list|,
name|scoreType
argument_list|,
name|innerQuery
argument_list|,
name|minChildren
argument_list|,
name|maxChildren
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// TODO: use the query API
name|Filter
name|parentFilter
init|=
operator|new
name|QueryWrapperFilter
argument_list|(
name|parentDocMapper
operator|.
name|typeFilter
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|minChildren
operator|>
literal|1
operator|||
name|maxChildren
operator|>
literal|0
operator|||
name|scoreType
operator|!=
name|ScoreType
operator|.
name|NONE
condition|)
block|{
name|query
operator|=
operator|new
name|ChildrenQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|parentFilter
argument_list|,
name|innerQuery
argument_list|,
name|scoreType
argument_list|,
name|minChildren
argument_list|,
name|maxChildren
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
name|nonNestedDocsFilter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|query
operator|=
operator|new
name|ChildrenConstantScoreQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
name|innerQuery
argument_list|,
name|parentType
argument_list|,
name|childType
argument_list|,
name|parentFilter
argument_list|,
name|shortCircuitParentDocSet
argument_list|,
name|nonNestedDocsFilter
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
return|return
name|query
return|;
block|}
DECL|method|joinUtilHelper
specifier|public
specifier|static
name|Query
name|joinUtilHelper
parameter_list|(
name|String
name|parentType
parameter_list|,
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
parameter_list|,
name|Query
name|toQuery
parameter_list|,
name|ScoreType
name|scoreType
parameter_list|,
name|Query
name|innerQuery
parameter_list|,
name|int
name|minChildren
parameter_list|,
name|int
name|maxChildren
parameter_list|)
throws|throws
name|IOException
block|{
name|ScoreMode
name|scoreMode
decl_stmt|;
comment|// TODO: move entirely over from ScoreType to org.apache.lucene.join.ScoreMode, when we drop the 1.x parent child code.
switch|switch
condition|(
name|scoreType
condition|)
block|{
case|case
name|NONE
case|:
name|scoreMode
operator|=
name|ScoreMode
operator|.
name|None
expr_stmt|;
break|break;
case|case
name|MIN
case|:
name|scoreMode
operator|=
name|ScoreMode
operator|.
name|Min
expr_stmt|;
break|break;
case|case
name|MAX
case|:
name|scoreMode
operator|=
name|ScoreMode
operator|.
name|Max
expr_stmt|;
break|break;
case|case
name|SUM
case|:
name|scoreMode
operator|=
name|ScoreMode
operator|.
name|Total
expr_stmt|;
break|break;
case|case
name|AVG
case|:
name|scoreMode
operator|=
name|ScoreMode
operator|.
name|Avg
expr_stmt|;
break|break;
default|default:
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"score type ["
operator|+
name|scoreType
operator|+
literal|"] not supported"
argument_list|)
throw|;
block|}
comment|// 0 in pre 2.x p/c impl means unbounded
if|if
condition|(
name|maxChildren
operator|==
literal|0
condition|)
block|{
name|maxChildren
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
return|return
operator|new
name|LateParsingQuery
argument_list|(
name|toQuery
argument_list|,
name|innerQuery
argument_list|,
name|minChildren
argument_list|,
name|maxChildren
argument_list|,
name|parentType
argument_list|,
name|scoreMode
argument_list|,
name|parentChildIndexFieldData
argument_list|)
return|;
block|}
DECL|class|LateParsingQuery
specifier|final
specifier|static
class|class
name|LateParsingQuery
extends|extends
name|Query
block|{
DECL|field|toQuery
specifier|private
specifier|final
name|Query
name|toQuery
decl_stmt|;
DECL|field|innerQuery
specifier|private
specifier|final
name|Query
name|innerQuery
decl_stmt|;
DECL|field|minChildren
specifier|private
specifier|final
name|int
name|minChildren
decl_stmt|;
DECL|field|maxChildren
specifier|private
specifier|final
name|int
name|maxChildren
decl_stmt|;
DECL|field|parentType
specifier|private
specifier|final
name|String
name|parentType
decl_stmt|;
DECL|field|scoreMode
specifier|private
specifier|final
name|ScoreMode
name|scoreMode
decl_stmt|;
DECL|field|parentChildIndexFieldData
specifier|private
specifier|final
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
decl_stmt|;
DECL|field|identity
specifier|private
specifier|final
name|Object
name|identity
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|method|LateParsingQuery
name|LateParsingQuery
parameter_list|(
name|Query
name|toQuery
parameter_list|,
name|Query
name|innerQuery
parameter_list|,
name|int
name|minChildren
parameter_list|,
name|int
name|maxChildren
parameter_list|,
name|String
name|parentType
parameter_list|,
name|ScoreMode
name|scoreMode
parameter_list|,
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
parameter_list|)
block|{
name|this
operator|.
name|toQuery
operator|=
name|toQuery
expr_stmt|;
name|this
operator|.
name|innerQuery
operator|=
name|innerQuery
expr_stmt|;
name|this
operator|.
name|minChildren
operator|=
name|minChildren
expr_stmt|;
name|this
operator|.
name|maxChildren
operator|=
name|maxChildren
expr_stmt|;
name|this
operator|.
name|parentType
operator|=
name|parentType
expr_stmt|;
name|this
operator|.
name|scoreMode
operator|=
name|scoreMode
expr_stmt|;
name|this
operator|.
name|parentChildIndexFieldData
operator|=
name|parentChildIndexFieldData
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|rewrite
specifier|public
name|Query
name|rewrite
parameter_list|(
name|IndexReader
name|reader
parameter_list|)
throws|throws
name|IOException
block|{
name|SearchContext
name|searchContext
init|=
name|SearchContext
operator|.
name|current
argument_list|()
decl_stmt|;
if|if
condition|(
name|searchContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Search context is required to be set"
argument_list|)
throw|;
block|}
name|String
name|joinField
init|=
name|ParentFieldMapper
operator|.
name|joinField
argument_list|(
name|parentType
argument_list|)
decl_stmt|;
name|IndexReader
name|indexReader
init|=
name|searchContext
operator|.
name|searcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
decl_stmt|;
name|IndexSearcher
name|indexSearcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|IndexParentChildFieldData
name|indexParentChildFieldData
init|=
name|parentChildIndexFieldData
operator|.
name|loadGlobal
argument_list|(
name|indexReader
argument_list|)
decl_stmt|;
name|MultiDocValues
operator|.
name|OrdinalMap
name|ordinalMap
init|=
name|ParentChildIndexFieldData
operator|.
name|getOrdinalMap
argument_list|(
name|indexParentChildFieldData
argument_list|,
name|parentType
argument_list|)
decl_stmt|;
return|return
name|JoinUtil
operator|.
name|createJoinQuery
argument_list|(
name|joinField
argument_list|,
name|innerQuery
argument_list|,
name|toQuery
argument_list|,
name|indexSearcher
argument_list|,
name|scoreMode
argument_list|,
name|ordinalMap
argument_list|,
name|minChildren
argument_list|,
name|maxChildren
argument_list|)
return|;
block|}
comment|// Even though we only cache rewritten queries it is good to let all queries implement hashCode() and equals():
comment|// We can't check for actually equality here, since we need to IndexReader for this, but
comment|// that isn't available on all cases during query parse time, so instead rely on identity:
annotation|@
name|Override
DECL|method|equals
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|super
operator|.
name|equals
argument_list|(
name|o
argument_list|)
condition|)
return|return
literal|false
return|;
name|LateParsingQuery
name|that
init|=
operator|(
name|LateParsingQuery
operator|)
name|o
decl_stmt|;
return|return
name|identity
operator|.
name|equals
argument_list|(
name|that
operator|.
name|identity
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
name|int
name|result
init|=
name|super
operator|.
name|hashCode
argument_list|()
decl_stmt|;
name|result
operator|=
literal|31
operator|*
name|result
operator|+
name|identity
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|toString
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
return|return
literal|"LateParsingQuery {parentType="
operator|+
name|parentType
operator|+
literal|"}"
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|getBuilderPrototype
specifier|public
name|HasChildQueryBuilder
name|getBuilderPrototype
parameter_list|()
block|{
return|return
name|HasChildQueryBuilder
operator|.
name|PROTOTYPE
return|;
block|}
block|}
end_class

end_unit

