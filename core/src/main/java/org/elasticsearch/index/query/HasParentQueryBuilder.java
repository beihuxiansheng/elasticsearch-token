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
name|BooleanQuery
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
name|QueryInnerHits
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
name|fetch
operator|.
name|innerhits
operator|.
name|InnerHitsSubSearchContext
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
name|Objects
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

begin_comment
comment|/**  * Builder for the 'has_parent' query.  */
end_comment

begin_class
DECL|class|HasParentQueryBuilder
specifier|public
class|class
name|HasParentQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|HasParentQueryBuilder
argument_list|>
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
DECL|field|DEFAULT_SCORE
specifier|public
specifier|static
specifier|final
name|boolean
name|DEFAULT_SCORE
init|=
literal|false
decl_stmt|;
DECL|field|query
specifier|private
specifier|final
name|QueryBuilder
name|query
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|String
name|type
decl_stmt|;
DECL|field|score
specifier|private
name|boolean
name|score
init|=
name|DEFAULT_SCORE
decl_stmt|;
DECL|field|innerHit
specifier|private
name|QueryInnerHits
name|innerHit
decl_stmt|;
comment|/**      * @param type  The parent type      * @param query The query that will be matched with parent documents      */
DECL|method|HasParentQueryBuilder
specifier|public
name|HasParentQueryBuilder
parameter_list|(
name|String
name|type
parameter_list|,
name|QueryBuilder
name|query
parameter_list|)
block|{
if|if
condition|(
name|type
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
literal|"] requires 'parent_type' field"
argument_list|)
throw|;
block|}
if|if
condition|(
name|query
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
literal|"] requires 'query' field"
argument_list|)
throw|;
block|}
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
block|}
DECL|method|HasParentQueryBuilder
specifier|public
name|HasParentQueryBuilder
parameter_list|(
name|String
name|type
parameter_list|,
name|QueryBuilder
name|query
parameter_list|,
name|boolean
name|score
parameter_list|,
name|QueryInnerHits
name|innerHits
parameter_list|)
block|{
name|this
argument_list|(
name|type
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|innerHit
operator|=
name|innerHits
expr_stmt|;
block|}
comment|/**      * Defines if the parent score is mapped into the child documents.      */
DECL|method|score
specifier|public
name|HasParentQueryBuilder
name|score
parameter_list|(
name|boolean
name|score
parameter_list|)
block|{
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Sets inner hit definition in the scope of this query and reusing the defined type and query.      */
DECL|method|innerHit
specifier|public
name|HasParentQueryBuilder
name|innerHit
parameter_list|(
name|QueryInnerHits
name|innerHit
parameter_list|)
block|{
name|this
operator|.
name|innerHit
operator|=
name|innerHit
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns the query to execute.      */
DECL|method|query
specifier|public
name|QueryBuilder
name|query
parameter_list|()
block|{
return|return
name|query
return|;
block|}
comment|/**      * Returns<code>true</code> if the parent score is mapped into the child documents      */
DECL|method|score
specifier|public
name|boolean
name|score
parameter_list|()
block|{
return|return
name|score
return|;
block|}
comment|/**      * Returns the parents type name      */
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|type
return|;
block|}
comment|/**      *  Returns inner hit definition in the scope of this query and reusing the defined type and query.      */
DECL|method|innerHit
specifier|public
name|QueryInnerHits
name|innerHit
parameter_list|()
block|{
return|return
name|innerHit
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
name|IOException
block|{
name|Query
name|innerQuery
decl_stmt|;
name|String
index|[]
name|previousTypes
init|=
name|context
operator|.
name|getTypes
argument_list|()
decl_stmt|;
name|context
operator|.
name|setTypes
argument_list|(
name|type
argument_list|)
expr_stmt|;
try|try
block|{
name|innerQuery
operator|=
name|query
operator|.
name|toQuery
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|setTypes
argument_list|(
name|previousTypes
argument_list|)
expr_stmt|;
block|}
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
name|DocumentMapper
name|parentDocMapper
init|=
name|context
operator|.
name|getMapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|type
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
name|QueryShardException
argument_list|(
name|context
argument_list|,
literal|"["
operator|+
name|NAME
operator|+
literal|"] query configured 'parent_type' ["
operator|+
name|type
operator|+
literal|"] is not a valid type"
argument_list|)
throw|;
block|}
if|if
condition|(
name|innerHit
operator|!=
literal|null
condition|)
block|{
try|try
init|(
name|XContentParser
name|parser
init|=
name|innerHit
operator|.
name|getXcontentParser
argument_list|()
init|)
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
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"start object expected but was: ["
operator|+
name|token
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|InnerHitsSubSearchContext
name|innerHits
init|=
name|context
operator|.
name|getInnerHitsContext
argument_list|(
name|parser
argument_list|)
decl_stmt|;
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
name|getSubSearchContext
argument_list|()
argument_list|,
name|parsedQuery
argument_list|,
literal|null
argument_list|,
name|context
operator|.
name|getMapperService
argument_list|()
argument_list|,
name|parentDocMapper
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|innerHits
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|?
name|innerHits
operator|.
name|getName
argument_list|()
else|:
name|type
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
block|}
block|}
name|Set
argument_list|<
name|String
argument_list|>
name|childTypes
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
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
name|context
operator|.
name|getMapperService
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
operator|&&
name|type
operator|.
name|equals
argument_list|(
name|parentFieldMapper
operator|.
name|type
argument_list|()
argument_list|)
condition|)
block|{
name|childTypes
operator|.
name|add
argument_list|(
name|documentMapper
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|parentChildIndexFieldData
operator|=
name|context
operator|.
name|getForField
argument_list|(
name|parentFieldMapper
operator|.
name|fieldType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|childTypes
operator|.
name|isEmpty
argument_list|()
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
literal|"] no child types found for type ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|Query
name|childrenQuery
decl_stmt|;
if|if
condition|(
name|childTypes
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
name|context
operator|.
name|getMapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|childTypes
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
argument_list|)
decl_stmt|;
name|childrenQuery
operator|=
name|documentMapper
operator|.
name|typeFilter
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|BooleanQuery
operator|.
name|Builder
name|childrenFilter
init|=
operator|new
name|BooleanQuery
operator|.
name|Builder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|childrenTypeStr
range|:
name|childTypes
control|)
block|{
name|DocumentMapper
name|documentMapper
init|=
name|context
operator|.
name|getMapperService
argument_list|()
operator|.
name|documentMapper
argument_list|(
name|childrenTypeStr
argument_list|)
decl_stmt|;
name|childrenFilter
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
name|childrenQuery
operator|=
name|childrenFilter
operator|.
name|build
argument_list|()
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
name|parentDocMapper
operator|.
name|typeFilter
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|HasChildQueryBuilder
operator|.
name|LateParsingQuery
argument_list|(
name|childrenQuery
argument_list|,
name|innerQuery
argument_list|,
name|HasChildQueryBuilder
operator|.
name|DEFAULT_MIN_CHILDREN
argument_list|,
name|HasChildQueryBuilder
operator|.
name|DEFAULT_MAX_CHILDREN
argument_list|,
name|type
argument_list|,
name|score
condition|?
name|ScoreMode
operator|.
name|Max
else|:
name|ScoreMode
operator|.
name|None
argument_list|,
name|parentChildIndexFieldData
argument_list|,
name|context
operator|.
name|getSearchSimilarity
argument_list|()
argument_list|)
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
name|field
argument_list|(
name|HasParentQueryParser
operator|.
name|QUERY_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|.
name|toXContent
argument_list|(
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|HasParentQueryParser
operator|.
name|TYPE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
name|HasParentQueryParser
operator|.
name|SCORE_FIELD
operator|.
name|getPreferredName
argument_list|()
argument_list|,
name|score
argument_list|)
expr_stmt|;
name|printBoostAndQueryName
argument_list|(
name|builder
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerHit
operator|!=
literal|null
condition|)
block|{
name|innerHit
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
name|endObject
argument_list|()
expr_stmt|;
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
DECL|method|HasParentQueryBuilder
specifier|protected
name|HasParentQueryBuilder
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|type
operator|=
name|in
operator|.
name|readString
argument_list|()
expr_stmt|;
name|score
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
name|query
operator|=
name|in
operator|.
name|readQuery
argument_list|()
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|innerHit
operator|=
operator|new
name|QueryInnerHits
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|HasParentQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
operator|new
name|HasParentQueryBuilder
argument_list|(
name|in
argument_list|)
return|;
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
name|type
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|score
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
if|if
condition|(
name|innerHit
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|innerHit
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|doEquals
specifier|protected
name|boolean
name|doEquals
parameter_list|(
name|HasParentQueryBuilder
name|that
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|query
argument_list|,
name|that
operator|.
name|query
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|type
argument_list|,
name|that
operator|.
name|type
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|score
argument_list|,
name|that
operator|.
name|score
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|innerHit
argument_list|,
name|that
operator|.
name|innerHit
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
name|query
argument_list|,
name|type
argument_list|,
name|score
argument_list|,
name|innerHit
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|doRewrite
specifier|protected
name|QueryBuilder
argument_list|<
name|?
argument_list|>
name|doRewrite
parameter_list|(
name|QueryRewriteContext
name|queryShardContext
parameter_list|)
throws|throws
name|IOException
block|{
name|QueryBuilder
name|rewrite
init|=
name|query
operator|.
name|rewrite
argument_list|(
name|queryShardContext
argument_list|)
decl_stmt|;
if|if
condition|(
name|rewrite
operator|!=
name|query
condition|)
block|{
name|HasParentQueryBuilder
name|hasParentQueryBuilder
init|=
operator|new
name|HasParentQueryBuilder
argument_list|(
name|type
argument_list|,
name|rewrite
argument_list|)
decl_stmt|;
name|hasParentQueryBuilder
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|hasParentQueryBuilder
operator|.
name|innerHit
operator|=
name|innerHit
expr_stmt|;
return|return
name|hasParentQueryBuilder
return|;
block|}
return|return
name|this
return|;
block|}
block|}
end_class

end_unit

