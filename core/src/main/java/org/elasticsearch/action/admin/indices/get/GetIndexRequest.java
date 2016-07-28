begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action.admin.indices.get
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|admin
operator|.
name|indices
operator|.
name|get
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|ActionRequestValidationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|action
operator|.
name|support
operator|.
name|master
operator|.
name|info
operator|.
name|ClusterInfoRequest
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
name|util
operator|.
name|ArrayUtils
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
name|Arrays
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
comment|/**  * A request to delete an index. Best created with {@link org.elasticsearch.client.Requests#deleteIndexRequest(String)}.  */
end_comment

begin_class
DECL|class|GetIndexRequest
specifier|public
class|class
name|GetIndexRequest
extends|extends
name|ClusterInfoRequest
argument_list|<
name|GetIndexRequest
argument_list|>
block|{
DECL|enum|Feature
specifier|public
specifier|static
enum|enum
name|Feature
block|{
DECL|enum constant|ALIASES
name|ALIASES
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|,
literal|"_aliases"
argument_list|,
literal|"_alias"
argument_list|)
block|,
DECL|enum constant|MAPPINGS
name|MAPPINGS
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|,
literal|"_mappings"
argument_list|,
literal|"_mapping"
argument_list|)
block|,
DECL|enum constant|SETTINGS
name|SETTINGS
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|,
literal|"_settings"
argument_list|)
block|;
DECL|field|FEATURES
specifier|private
specifier|static
specifier|final
name|Feature
index|[]
name|FEATURES
init|=
operator|new
name|Feature
index|[
name|Feature
operator|.
name|values
argument_list|()
operator|.
name|length
index|]
decl_stmt|;
static|static
block|{
for|for
control|(
name|Feature
name|feature
range|:
name|Feature
operator|.
name|values
argument_list|()
control|)
block|{
assert|assert
name|feature
operator|.
name|id
argument_list|()
operator|<
name|FEATURES
operator|.
name|length
operator|&&
name|feature
operator|.
name|id
argument_list|()
operator|>=
literal|0
assert|;
name|FEATURES
index|[
name|feature
operator|.
name|id
index|]
operator|=
name|feature
expr_stmt|;
block|}
block|}
DECL|field|validNames
specifier|private
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|validNames
decl_stmt|;
DECL|field|preferredName
specifier|private
specifier|final
name|String
name|preferredName
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
DECL|method|Feature
specifier|private
name|Feature
parameter_list|(
name|byte
name|id
parameter_list|,
name|String
modifier|...
name|validNames
parameter_list|)
block|{
assert|assert
name|validNames
operator|!=
literal|null
operator|&&
name|validNames
operator|.
name|length
operator|>
literal|0
assert|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|validNames
operator|=
name|Arrays
operator|.
name|asList
argument_list|(
name|validNames
argument_list|)
expr_stmt|;
name|this
operator|.
name|preferredName
operator|=
name|validNames
index|[
literal|0
index|]
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|byte
name|id
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|preferredName
specifier|public
name|String
name|preferredName
parameter_list|()
block|{
return|return
name|preferredName
return|;
block|}
DECL|method|validName
specifier|public
name|boolean
name|validName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|this
operator|.
name|validNames
operator|.
name|contains
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|fromName
specifier|public
specifier|static
name|Feature
name|fromName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|Feature
name|feature
range|:
name|Feature
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|feature
operator|.
name|validName
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|feature
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No feature for name ["
operator|+
name|name
operator|+
literal|"]"
argument_list|)
throw|;
block|}
DECL|method|fromId
specifier|public
specifier|static
name|Feature
name|fromId
parameter_list|(
name|byte
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|<
literal|0
operator|||
name|id
operator|>=
name|FEATURES
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No mapping for id ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
return|return
name|FEATURES
index|[
name|id
index|]
return|;
block|}
DECL|method|convertToFeatures
specifier|public
specifier|static
name|Feature
index|[]
name|convertToFeatures
parameter_list|(
name|String
modifier|...
name|featureNames
parameter_list|)
block|{
name|Feature
index|[]
name|features
init|=
operator|new
name|Feature
index|[
name|featureNames
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
name|featureNames
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|features
index|[
name|i
index|]
operator|=
name|Feature
operator|.
name|fromName
argument_list|(
name|featureNames
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|features
return|;
block|}
block|}
DECL|field|DEFAULT_FEATURES
specifier|private
specifier|static
specifier|final
name|Feature
index|[]
name|DEFAULT_FEATURES
init|=
operator|new
name|Feature
index|[]
block|{
name|Feature
operator|.
name|ALIASES
block|,
name|Feature
operator|.
name|MAPPINGS
block|,
name|Feature
operator|.
name|SETTINGS
block|}
decl_stmt|;
DECL|field|features
specifier|private
name|Feature
index|[]
name|features
init|=
name|DEFAULT_FEATURES
decl_stmt|;
DECL|field|humanReadable
specifier|private
name|boolean
name|humanReadable
init|=
literal|false
decl_stmt|;
DECL|method|features
specifier|public
name|GetIndexRequest
name|features
parameter_list|(
name|Feature
modifier|...
name|features
parameter_list|)
block|{
if|if
condition|(
name|features
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"features cannot be null"
argument_list|)
throw|;
block|}
else|else
block|{
name|this
operator|.
name|features
operator|=
name|features
expr_stmt|;
block|}
return|return
name|this
return|;
block|}
DECL|method|addFeatures
specifier|public
name|GetIndexRequest
name|addFeatures
parameter_list|(
name|Feature
modifier|...
name|features
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|features
operator|==
name|DEFAULT_FEATURES
condition|)
block|{
return|return
name|features
argument_list|(
name|features
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|features
argument_list|(
name|ArrayUtils
operator|.
name|concat
argument_list|(
name|features
argument_list|()
argument_list|,
name|features
argument_list|,
name|Feature
operator|.
name|class
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|features
specifier|public
name|Feature
index|[]
name|features
parameter_list|()
block|{
return|return
name|features
return|;
block|}
annotation|@
name|Override
DECL|method|validate
specifier|public
name|ActionRequestValidationException
name|validate
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
DECL|method|humanReadable
specifier|public
name|GetIndexRequest
name|humanReadable
parameter_list|(
name|boolean
name|humanReadable
parameter_list|)
block|{
name|this
operator|.
name|humanReadable
operator|=
name|humanReadable
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|humanReadable
specifier|public
name|boolean
name|humanReadable
parameter_list|()
block|{
return|return
name|humanReadable
return|;
block|}
annotation|@
name|Override
DECL|method|readFrom
specifier|public
name|void
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|super
operator|.
name|readFrom
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
name|features
operator|=
operator|new
name|Feature
index|[
name|size
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|features
index|[
name|i
index|]
operator|=
name|Feature
operator|.
name|fromId
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|humanReadable
operator|=
name|in
operator|.
name|readBoolean
argument_list|()
expr_stmt|;
block|}
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
name|super
operator|.
name|writeTo
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeVInt
argument_list|(
name|features
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Feature
name|feature
range|:
name|features
control|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
name|feature
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|writeBoolean
argument_list|(
name|humanReadable
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

