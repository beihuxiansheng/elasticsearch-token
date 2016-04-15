begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.metadata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|metadata
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|ElasticsearchParseException
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
name|AbstractDiffable
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
operator|.
name|Custom
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
name|settings
operator|.
name|Settings
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
name|settings
operator|.
name|loader
operator|.
name|SettingsLoader
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
name|ToXContent
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
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

begin_comment
comment|/**  * Contains metadata about registered snapshot repositories  */
end_comment

begin_class
DECL|class|RepositoriesMetaData
specifier|public
class|class
name|RepositoriesMetaData
extends|extends
name|AbstractDiffable
argument_list|<
name|Custom
argument_list|>
implements|implements
name|MetaData
operator|.
name|Custom
block|{
DECL|field|TYPE
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"repositories"
decl_stmt|;
DECL|field|PROTO
specifier|public
specifier|static
specifier|final
name|RepositoriesMetaData
name|PROTO
init|=
operator|new
name|RepositoriesMetaData
argument_list|()
decl_stmt|;
DECL|field|repositories
specifier|private
specifier|final
name|List
argument_list|<
name|RepositoryMetaData
argument_list|>
name|repositories
decl_stmt|;
comment|/**      * Constructs new repository metadata      *      * @param repositories list of repositories      */
DECL|method|RepositoriesMetaData
specifier|public
name|RepositoriesMetaData
parameter_list|(
name|RepositoryMetaData
modifier|...
name|repositories
parameter_list|)
block|{
name|this
operator|.
name|repositories
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|repositories
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns list of currently registered repositories      *      * @return list of repositories      */
DECL|method|repositories
specifier|public
name|List
argument_list|<
name|RepositoryMetaData
argument_list|>
name|repositories
parameter_list|()
block|{
return|return
name|this
operator|.
name|repositories
return|;
block|}
comment|/**      * Returns a repository with a given name or null if such repository doesn't exist      *      * @param name name of repository      * @return repository metadata      */
DECL|method|repository
specifier|public
name|RepositoryMetaData
name|repository
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|RepositoryMetaData
name|repository
range|:
name|repositories
control|)
block|{
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|repository
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|repository
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
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
name|RepositoriesMetaData
name|that
init|=
operator|(
name|RepositoriesMetaData
operator|)
name|o
decl_stmt|;
return|return
name|repositories
operator|.
name|equals
argument_list|(
name|that
operator|.
name|repositories
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
name|repositories
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|type
specifier|public
name|String
name|type
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|Custom
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|RepositoryMetaData
index|[]
name|repository
init|=
operator|new
name|RepositoryMetaData
index|[
name|in
operator|.
name|readVInt
argument_list|()
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
name|repository
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|repository
index|[
name|i
index|]
operator|=
name|RepositoryMetaData
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RepositoriesMetaData
argument_list|(
name|repository
argument_list|)
return|;
block|}
comment|/**      * {@inheritDoc}      */
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
name|out
operator|.
name|writeVInt
argument_list|(
name|repositories
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|RepositoryMetaData
name|repository
range|:
name|repositories
control|)
block|{
name|repository
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * {@inheritDoc}      */
annotation|@
name|Override
DECL|method|fromXContent
specifier|public
name|RepositoriesMetaData
name|fromXContent
parameter_list|(
name|XContentParser
name|parser
parameter_list|)
throws|throws
name|IOException
block|{
name|XContentParser
operator|.
name|Token
name|token
decl_stmt|;
name|List
argument_list|<
name|RepositoryMetaData
argument_list|>
name|repository
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|String
name|name
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
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
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse repository [{}], expected object"
argument_list|,
name|name
argument_list|)
throw|;
block|}
name|String
name|type
init|=
literal|null
decl_stmt|;
name|Settings
name|settings
init|=
name|Settings
operator|.
name|EMPTY
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
name|String
name|currentFieldName
init|=
name|parser
operator|.
name|currentName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
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
name|VALUE_STRING
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse repository [{}], unknown type"
argument_list|,
name|name
argument_list|)
throw|;
block|}
name|type
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
literal|"settings"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
if|if
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
name|START_OBJECT
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse repository [{}], incompatible params"
argument_list|,
name|name
argument_list|)
throw|;
block|}
name|settings
operator|=
name|Settings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
name|SettingsLoader
operator|.
name|Helper
operator|.
name|loadNestedFromMap
argument_list|(
name|parser
operator|.
name|mapOrdered
argument_list|()
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse repository [{}], unknown field [{}]"
argument_list|,
name|name
argument_list|,
name|currentFieldName
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse repository [{}]"
argument_list|,
name|name
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse repository [{}], missing repository type"
argument_list|,
name|name
argument_list|)
throw|;
block|}
name|repository
operator|.
name|add
argument_list|(
operator|new
name|RepositoryMetaData
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|settings
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|ElasticsearchParseException
argument_list|(
literal|"failed to parse repositories"
argument_list|)
throw|;
block|}
block|}
return|return
operator|new
name|RepositoriesMetaData
argument_list|(
name|repository
operator|.
name|toArray
argument_list|(
operator|new
name|RepositoryMetaData
index|[
name|repository
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * {@inheritDoc}      */
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
name|ToXContent
operator|.
name|Params
name|params
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|RepositoryMetaData
name|repository
range|:
name|repositories
control|)
block|{
name|toXContent
argument_list|(
name|repository
argument_list|,
name|builder
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
return|return
name|builder
return|;
block|}
annotation|@
name|Override
DECL|method|context
specifier|public
name|EnumSet
argument_list|<
name|MetaData
operator|.
name|XContentContext
argument_list|>
name|context
parameter_list|()
block|{
return|return
name|MetaData
operator|.
name|API_AND_GATEWAY
return|;
block|}
comment|/**      * Serializes information about a single repository      *      * @param repository repository metadata      * @param builder    XContent builder      * @param params     serialization parameters      */
DECL|method|toXContent
specifier|public
specifier|static
name|void
name|toXContent
parameter_list|(
name|RepositoryMetaData
name|repository
parameter_list|,
name|XContentBuilder
name|builder
parameter_list|,
name|ToXContent
operator|.
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
name|repository
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"type"
argument_list|,
name|repository
operator|.
name|type
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"settings"
argument_list|)
expr_stmt|;
name|repository
operator|.
name|settings
argument_list|()
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
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

