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
name|BooleanClause
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
name|Filter
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
name|FilteredQuery
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
name|NotFilter
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
name|XBooleanFilter
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
name|CustomQueryWrappingFilter
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
name|ParentConstantScoreQuery
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
name|ParentQuery
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|query
operator|.
name|QueryParserUtils
operator|.
name|ensureNotDeleteByQuery
import|;
end_import

begin_class
DECL|class|HasParentQueryParser
specifier|public
class|class
name|HasParentQueryParser
implements|implements
name|QueryParser
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"has_parent"
decl_stmt|;
DECL|field|innerHitsQueryParserHelper
specifier|private
specifier|final
name|InnerHitsQueryParserHelper
name|innerHitsQueryParserHelper
decl_stmt|;
annotation|@
name|Inject
DECL|method|HasParentQueryParser
specifier|public
name|HasParentQueryParser
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
name|NAME
block|,
name|Strings
operator|.
name|toCamelCase
argument_list|(
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
name|QueryParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|QueryParsingException
block|{
name|ensureNotDeleteByQuery
argument_list|(
name|NAME
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
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
literal|1.0f
decl_stmt|;
name|String
name|parentType
init|=
literal|null
decl_stmt|;
name|boolean
name|score
init|=
literal|false
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
literal|"query"
operator|.
name|equals
argument_list|(
name|currentFieldName
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
name|parentType
operator|==
literal|null
condition|?
literal|null
else|:
operator|new
name|String
index|[]
block|{
name|parentType
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
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] query does not support ["
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
literal|"parent_type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"parentType"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|parentType
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
name|String
name|scoreTypeValue
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"score"
operator|.
name|equals
argument_list|(
name|scoreTypeValue
argument_list|)
condition|)
block|{
name|score
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|scoreTypeValue
argument_list|)
condition|)
block|{
name|score
operator|=
literal|false
expr_stmt|;
block|}
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
name|String
name|scoreModeValue
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"score"
operator|.
name|equals
argument_list|(
name|scoreModeValue
argument_list|)
condition|)
block|{
name|score
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|scoreModeValue
argument_list|)
condition|)
block|{
name|score
operator|=
literal|false
expr_stmt|;
block|}
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
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] query does not support ["
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
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] query requires 'query' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|parentType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] query requires 'parent_type' field"
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
name|parentType
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
name|Query
name|query
init|=
name|createParentQuery
argument_list|(
name|innerQuery
argument_list|,
name|parentType
argument_list|,
name|score
argument_list|,
name|parseContext
argument_list|,
name|innerHits
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|query
operator|.
name|setBoost
argument_list|(
name|boost
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedFilter
argument_list|(
name|queryName
argument_list|,
operator|new
name|CustomQueryWrappingFilter
argument_list|(
name|query
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
DECL|method|createParentQuery
specifier|static
name|Query
name|createParentQuery
parameter_list|(
name|Query
name|innerQuery
parameter_list|,
name|String
name|parentType
parameter_list|,
name|boolean
name|score
parameter_list|,
name|QueryParseContext
name|parseContext
parameter_list|,
name|Tuple
argument_list|<
name|String
argument_list|,
name|SubSearchContext
argument_list|>
name|innerHits
parameter_list|)
block|{
name|DocumentMapper
name|parentDocMapper
init|=
name|parseContext
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
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] query configured 'parent_type' ["
operator|+
name|parentType
operator|+
literal|"] is not a valid type"
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
name|innerQuery
argument_list|,
literal|null
argument_list|,
name|parentDocMapper
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
name|parentType
decl_stmt|;
name|parseContext
operator|.
name|addInnerHits
argument_list|(
name|name
argument_list|,
name|parentChildInnerHits
argument_list|)
expr_stmt|;
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|parentTypes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
name|parentTypes
operator|.
name|add
argument_list|(
name|parentDocMapper
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|ParentChildIndexFieldData
name|parentChildIndexFieldData
init|=
literal|null
decl_stmt|;
for|for
control|(
name|DocumentMapper
name|documentMapper
range|:
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|docMappers
argument_list|(
literal|false
argument_list|)
control|)
block|{
name|ParentFieldMapper
name|parentFieldMapper
init|=
name|documentMapper
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
condition|)
block|{
name|DocumentMapper
name|parentTypeDocumentMapper
init|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|parentFieldMapper
operator|.
name|type
argument_list|()
argument_list|)
decl_stmt|;
name|parentChildIndexFieldData
operator|=
name|parseContext
operator|.
name|getForField
argument_list|(
name|parentFieldMapper
argument_list|)
expr_stmt|;
if|if
condition|(
name|parentTypeDocumentMapper
operator|==
literal|null
condition|)
block|{
comment|// Only add this, if this parentFieldMapper (also a parent)  isn't a child of another parent.
name|parentTypes
operator|.
name|add
argument_list|(
name|parentFieldMapper
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|parentChildIndexFieldData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|QueryParsingException
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
argument_list|,
literal|"[has_parent] no _parent field configured"
argument_list|)
throw|;
block|}
name|Filter
name|parentFilter
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|parentTypes
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
name|DocumentMapper
name|documentMapper
init|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|parentTypes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|documentMapper
operator|!=
literal|null
condition|)
block|{
name|parentFilter
operator|=
name|documentMapper
operator|.
name|typeFilter
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|XBooleanFilter
name|parentsFilter
init|=
operator|new
name|XBooleanFilter
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|parentTypeStr
range|:
name|parentTypes
control|)
block|{
name|DocumentMapper
name|documentMapper
init|=
name|parseContext
operator|.
name|mapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|parentTypeStr
argument_list|)
decl_stmt|;
if|if
condition|(
name|documentMapper
operator|!=
literal|null
condition|)
block|{
name|parentsFilter
operator|.
name|add
argument_list|(
name|documentMapper
operator|.
name|typeFilter
argument_list|()
argument_list|,
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
block|}
name|parentFilter
operator|=
name|parentsFilter
expr_stmt|;
block|}
if|if
condition|(
name|parentFilter
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// wrap the query with type query
name|innerQuery
operator|=
operator|new
name|FilteredQuery
argument_list|(
name|innerQuery
argument_list|,
name|parseContext
operator|.
name|cacheFilter
argument_list|(
name|parentDocMapper
operator|.
name|typeFilter
argument_list|()
argument_list|,
literal|null
argument_list|,
name|parseContext
operator|.
name|autoFilterCachePolicy
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Filter
name|childrenFilter
init|=
name|parseContext
operator|.
name|cacheFilter
argument_list|(
operator|new
name|NotFilter
argument_list|(
name|parentFilter
argument_list|)
argument_list|,
literal|null
argument_list|,
name|parseContext
operator|.
name|autoFilterCachePolicy
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|score
condition|)
block|{
return|return
operator|new
name|ParentQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
name|innerQuery
argument_list|,
name|parentDocMapper
operator|.
name|type
argument_list|()
argument_list|,
name|childrenFilter
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|ParentConstantScoreQuery
argument_list|(
name|parentChildIndexFieldData
argument_list|,
name|innerQuery
argument_list|,
name|parentDocMapper
operator|.
name|type
argument_list|()
argument_list|,
name|childrenFilter
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

