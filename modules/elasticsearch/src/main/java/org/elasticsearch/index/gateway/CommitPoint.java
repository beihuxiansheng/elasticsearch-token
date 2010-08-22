begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.gateway
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|gateway
package|;
end_package

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
name|ImmutableList
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
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|CommitPoint
specifier|public
class|class
name|CommitPoint
block|{
DECL|class|FileInfo
specifier|public
specifier|static
class|class
name|FileInfo
block|{
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|physicalName
specifier|private
specifier|final
name|String
name|physicalName
decl_stmt|;
DECL|field|length
specifier|private
specifier|final
name|long
name|length
decl_stmt|;
DECL|method|FileInfo
specifier|public
name|FileInfo
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|physicalName
parameter_list|,
name|long
name|length
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|physicalName
operator|=
name|physicalName
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|name
return|;
block|}
DECL|method|physicalName
specifier|public
name|String
name|physicalName
parameter_list|()
block|{
return|return
name|this
operator|.
name|physicalName
return|;
block|}
DECL|method|length
specifier|public
name|long
name|length
parameter_list|()
block|{
return|return
name|length
return|;
block|}
block|}
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
DECL|enum constant|GENERATED
name|GENERATED
block|,
DECL|enum constant|SAVED
name|SAVED
block|}
DECL|field|version
specifier|private
specifier|final
name|long
name|version
decl_stmt|;
DECL|field|name
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
DECL|field|type
specifier|private
specifier|final
name|Type
name|type
decl_stmt|;
DECL|field|indexFiles
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|FileInfo
argument_list|>
name|indexFiles
decl_stmt|;
DECL|field|translogFiles
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|FileInfo
argument_list|>
name|translogFiles
decl_stmt|;
DECL|method|CommitPoint
specifier|public
name|CommitPoint
parameter_list|(
name|long
name|version
parameter_list|,
name|String
name|name
parameter_list|,
name|Type
name|type
parameter_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
name|indexFiles
parameter_list|,
name|List
argument_list|<
name|FileInfo
argument_list|>
name|translogFiles
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
name|this
operator|.
name|indexFiles
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|indexFiles
argument_list|)
expr_stmt|;
name|this
operator|.
name|translogFiles
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|translogFiles
argument_list|)
expr_stmt|;
block|}
DECL|method|version
specifier|public
name|long
name|version
parameter_list|()
block|{
return|return
name|version
return|;
block|}
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|type
specifier|public
name|Type
name|type
parameter_list|()
block|{
return|return
name|this
operator|.
name|type
return|;
block|}
DECL|method|indexFiles
specifier|public
name|ImmutableList
argument_list|<
name|FileInfo
argument_list|>
name|indexFiles
parameter_list|()
block|{
return|return
name|this
operator|.
name|indexFiles
return|;
block|}
DECL|method|translogFiles
specifier|public
name|ImmutableList
argument_list|<
name|FileInfo
argument_list|>
name|translogFiles
parameter_list|()
block|{
return|return
name|this
operator|.
name|translogFiles
return|;
block|}
DECL|method|containPhysicalIndexFile
specifier|public
name|boolean
name|containPhysicalIndexFile
parameter_list|(
name|String
name|physicalName
parameter_list|)
block|{
return|return
name|findPhysicalIndexFile
argument_list|(
name|physicalName
argument_list|)
operator|!=
literal|null
return|;
block|}
DECL|method|findPhysicalIndexFile
specifier|public
name|CommitPoint
operator|.
name|FileInfo
name|findPhysicalIndexFile
parameter_list|(
name|String
name|physicalName
parameter_list|)
block|{
for|for
control|(
name|FileInfo
name|file
range|:
name|indexFiles
control|)
block|{
if|if
condition|(
name|file
operator|.
name|physicalName
argument_list|()
operator|.
name|equals
argument_list|(
name|physicalName
argument_list|)
condition|)
block|{
return|return
name|file
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|findNameFile
specifier|public
name|CommitPoint
operator|.
name|FileInfo
name|findNameFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|CommitPoint
operator|.
name|FileInfo
name|fileInfo
init|=
name|findNameIndexFile
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|fileInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|fileInfo
return|;
block|}
return|return
name|findNameTranslogFile
argument_list|(
name|name
argument_list|)
return|;
block|}
DECL|method|findNameIndexFile
specifier|public
name|CommitPoint
operator|.
name|FileInfo
name|findNameIndexFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|FileInfo
name|file
range|:
name|indexFiles
control|)
block|{
if|if
condition|(
name|file
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|file
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|findNameTranslogFile
specifier|public
name|CommitPoint
operator|.
name|FileInfo
name|findNameTranslogFile
parameter_list|(
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|FileInfo
name|file
range|:
name|translogFiles
control|)
block|{
if|if
condition|(
name|file
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|file
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

