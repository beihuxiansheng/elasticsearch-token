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
name|cluster
operator|.
name|ClusterService
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
name|Nullable
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
name|regex
operator|.
name|Regex
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
name|support
operator|.
name|XContentStructure
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
name|Collection
import|;
end_import

begin_comment
comment|/**  */
end_comment

begin_class
DECL|class|IndicesQueryParser
specifier|public
class|class
name|IndicesQueryParser
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
literal|"indices"
decl_stmt|;
annotation|@
name|Nullable
DECL|field|clusterService
specifier|private
specifier|final
name|ClusterService
name|clusterService
decl_stmt|;
annotation|@
name|Inject
DECL|method|IndicesQueryParser
specifier|public
name|IndicesQueryParser
parameter_list|(
annotation|@
name|Nullable
name|ClusterService
name|clusterService
parameter_list|)
block|{
name|this
operator|.
name|clusterService
operator|=
name|clusterService
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
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|Query
name|noMatchQuery
init|=
literal|null
decl_stmt|;
name|boolean
name|queryFound
init|=
literal|false
decl_stmt|;
name|boolean
name|indicesFound
init|=
literal|false
decl_stmt|;
name|boolean
name|currentIndexMatchesIndices
init|=
literal|false
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
name|XContentStructure
operator|.
name|InnerQuery
name|innerQuery
init|=
literal|null
decl_stmt|;
name|XContentStructure
operator|.
name|InnerQuery
name|innerNoMatchQuery
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
name|innerQuery
operator|=
operator|new
name|XContentStructure
operator|.
name|InnerQuery
argument_list|(
name|parseContext
argument_list|,
literal|null
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
literal|"no_match_query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|innerNoMatchQuery
operator|=
operator|new
name|XContentStructure
operator|.
name|InnerQuery
argument_list|(
name|parseContext
argument_list|,
literal|null
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
literal|"[indices] query does not support ["
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
literal|"indices"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|indicesFound
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
literal|"[indices] indices or index already specified"
argument_list|)
throw|;
block|}
name|indicesFound
operator|=
literal|true
expr_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|indices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|parser
operator|.
name|nextToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|String
name|value
init|=
name|parser
operator|.
name|textOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
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
literal|"[indices] no value specified for 'indices' entry"
argument_list|)
throw|;
block|}
name|indices
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|currentIndexMatchesIndices
operator|=
name|matchesIndices
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
name|indices
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|indices
operator|.
name|size
argument_list|()
index|]
argument_list|)
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
literal|"[indices] query does not support ["
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
literal|"index"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
condition|(
name|indicesFound
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
literal|"[indices] indices or index already specified"
argument_list|)
throw|;
block|}
name|indicesFound
operator|=
literal|true
expr_stmt|;
name|currentIndexMatchesIndices
operator|=
name|matchesIndices
argument_list|(
name|parseContext
operator|.
name|index
argument_list|()
operator|.
name|name
argument_list|()
argument_list|,
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
literal|"no_match_query"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|String
name|type
init|=
name|parser
operator|.
name|text
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"all"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|noMatchQuery
operator|=
name|Queries
operator|.
name|newMatchAllQuery
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"none"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|noMatchQuery
operator|=
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|()
expr_stmt|;
block|}
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
literal|"[indices] query does not support ["
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
literal|"[indices] requires 'query' element"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|indicesFound
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
literal|"[indices] requires 'indices' or 'index' element"
argument_list|)
throw|;
block|}
name|Query
name|chosenQuery
decl_stmt|;
if|if
condition|(
name|currentIndexMatchesIndices
condition|)
block|{
name|chosenQuery
operator|=
name|innerQuery
operator|.
name|asQuery
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// If noMatchQuery is set, it means "no_match_query" was "all" or "none"
if|if
condition|(
name|noMatchQuery
operator|!=
literal|null
condition|)
block|{
name|chosenQuery
operator|=
name|noMatchQuery
expr_stmt|;
block|}
else|else
block|{
comment|// There might be no "no_match_query" set, so default to the match_all if not set
if|if
condition|(
name|innerNoMatchQuery
operator|==
literal|null
condition|)
block|{
name|chosenQuery
operator|=
name|Queries
operator|.
name|newMatchAllQuery
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|chosenQuery
operator|=
name|innerNoMatchQuery
operator|.
name|asQuery
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|queryName
operator|!=
literal|null
condition|)
block|{
name|parseContext
operator|.
name|addNamedQuery
argument_list|(
name|queryName
argument_list|,
name|chosenQuery
argument_list|)
expr_stmt|;
block|}
return|return
name|chosenQuery
return|;
block|}
DECL|method|matchesIndices
specifier|protected
name|boolean
name|matchesIndices
parameter_list|(
name|String
name|currentIndex
parameter_list|,
name|String
modifier|...
name|indices
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|concreteIndices
init|=
name|clusterService
operator|.
name|state
argument_list|()
operator|.
name|metaData
argument_list|()
operator|.
name|concreteIndicesIgnoreMissing
argument_list|(
name|indices
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|index
range|:
name|concreteIndices
control|)
block|{
if|if
condition|(
name|Regex
operator|.
name|simpleMatch
argument_list|(
name|index
argument_list|,
name|currentIndex
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

