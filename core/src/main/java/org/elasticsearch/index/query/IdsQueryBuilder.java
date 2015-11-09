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
name|queries
operator|.
name|TermsQuery
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
name|cluster
operator|.
name|metadata
operator|.
name|MetaData
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
name|index
operator|.
name|mapper
operator|.
name|Uid
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
name|UidFieldMapper
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
name|*
import|;
end_import

begin_comment
comment|/**  * A query that will return only documents matching specific ids (and a type).  */
end_comment

begin_class
DECL|class|IdsQueryBuilder
specifier|public
class|class
name|IdsQueryBuilder
extends|extends
name|AbstractQueryBuilder
argument_list|<
name|IdsQueryBuilder
argument_list|>
block|{
DECL|field|NAME
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"ids"
decl_stmt|;
DECL|field|ids
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|ids
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|types
specifier|private
specifier|final
name|String
index|[]
name|types
decl_stmt|;
DECL|field|PROTOTYPE
specifier|static
specifier|final
name|IdsQueryBuilder
name|PROTOTYPE
init|=
operator|new
name|IdsQueryBuilder
argument_list|()
decl_stmt|;
comment|/**      * Creates a new IdsQueryBuilder without providing the types of the documents to look for      */
DECL|method|IdsQueryBuilder
specifier|public
name|IdsQueryBuilder
parameter_list|()
block|{
name|this
operator|.
name|types
operator|=
operator|new
name|String
index|[
literal|0
index|]
expr_stmt|;
block|}
comment|/**      * Creates a new IdsQueryBuilder by providing the types of the documents to look for      */
DECL|method|IdsQueryBuilder
specifier|public
name|IdsQueryBuilder
parameter_list|(
name|String
modifier|...
name|types
parameter_list|)
block|{
if|if
condition|(
name|types
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[ids] types cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|types
operator|=
name|types
expr_stmt|;
block|}
comment|/**      * Returns the types used in this query      */
DECL|method|types
specifier|public
name|String
index|[]
name|types
parameter_list|()
block|{
return|return
name|this
operator|.
name|types
return|;
block|}
comment|/**      * Adds ids to the query.      */
DECL|method|addIds
specifier|public
name|IdsQueryBuilder
name|addIds
parameter_list|(
name|String
modifier|...
name|ids
parameter_list|)
block|{
if|if
condition|(
name|ids
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"[ids] ids cannot be null"
argument_list|)
throw|;
block|}
name|Collections
operator|.
name|addAll
argument_list|(
name|this
operator|.
name|ids
argument_list|,
name|ids
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**      * Returns the ids for the query.      */
DECL|method|ids
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|ids
parameter_list|()
block|{
return|return
name|this
operator|.
name|ids
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
name|array
argument_list|(
literal|"types"
argument_list|,
name|types
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startArray
argument_list|(
literal|"values"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|value
range|:
name|ids
control|)
block|{
name|builder
operator|.
name|value
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endArray
argument_list|()
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
name|IOException
block|{
name|Query
name|query
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|ids
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|query
operator|=
name|Queries
operator|.
name|newMatchNoDocsQuery
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|typesForQuery
decl_stmt|;
if|if
condition|(
name|types
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|typesForQuery
operator|=
name|context
operator|.
name|queryTypes
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|types
operator|.
name|length
operator|==
literal|1
operator|&&
name|MetaData
operator|.
name|ALL
operator|.
name|equals
argument_list|(
name|types
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|typesForQuery
operator|=
name|context
operator|.
name|getMapperService
argument_list|()
operator|.
name|types
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|typesForQuery
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|Collections
operator|.
name|addAll
argument_list|(
name|typesForQuery
argument_list|,
name|types
argument_list|)
expr_stmt|;
block|}
name|query
operator|=
operator|new
name|TermsQuery
argument_list|(
name|UidFieldMapper
operator|.
name|NAME
argument_list|,
name|Uid
operator|.
name|createUidsForTypesAndIds
argument_list|(
name|typesForQuery
argument_list|,
name|ids
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|query
return|;
block|}
annotation|@
name|Override
DECL|method|doReadFrom
specifier|protected
name|IdsQueryBuilder
name|doReadFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|IdsQueryBuilder
name|idsQueryBuilder
init|=
operator|new
name|IdsQueryBuilder
argument_list|(
name|in
operator|.
name|readStringArray
argument_list|()
argument_list|)
decl_stmt|;
name|idsQueryBuilder
operator|.
name|addIds
argument_list|(
name|in
operator|.
name|readStringArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|idsQueryBuilder
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
name|writeStringArray
argument_list|(
name|types
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeStringArray
argument_list|(
name|ids
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|ids
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
expr_stmt|;
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
name|ids
argument_list|,
name|Arrays
operator|.
name|hashCode
argument_list|(
name|types
argument_list|)
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
name|IdsQueryBuilder
name|other
parameter_list|)
block|{
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|ids
argument_list|,
name|other
operator|.
name|ids
argument_list|)
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|types
argument_list|,
name|other
operator|.
name|types
argument_list|)
return|;
block|}
block|}
end_class

end_unit

