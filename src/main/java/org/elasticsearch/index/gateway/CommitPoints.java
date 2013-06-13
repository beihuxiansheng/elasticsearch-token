begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to ElasticSearch and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. ElasticSearch licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
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
name|com
operator|.
name|google
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
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|CollectionUtil
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
name|XContentFactory
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
name|common
operator|.
name|xcontent
operator|.
name|XContentType
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
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
comment|/**  *  */
end_comment

begin_class
DECL|class|CommitPoints
specifier|public
class|class
name|CommitPoints
implements|implements
name|Iterable
argument_list|<
name|CommitPoint
argument_list|>
block|{
DECL|field|commitPoints
specifier|private
specifier|final
name|ImmutableList
argument_list|<
name|CommitPoint
argument_list|>
name|commitPoints
decl_stmt|;
DECL|method|CommitPoints
specifier|public
name|CommitPoints
parameter_list|(
name|List
argument_list|<
name|CommitPoint
argument_list|>
name|commitPoints
parameter_list|)
block|{
name|CollectionUtil
operator|.
name|quickSort
argument_list|(
name|commitPoints
argument_list|,
operator|new
name|Comparator
argument_list|<
name|CommitPoint
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|CommitPoint
name|o1
parameter_list|,
name|CommitPoint
name|o2
parameter_list|)
block|{
return|return
operator|(
name|o2
operator|.
name|version
argument_list|()
operator|<
name|o1
operator|.
name|version
argument_list|()
condition|?
operator|-
literal|1
else|:
operator|(
name|o2
operator|.
name|version
argument_list|()
operator|==
name|o1
operator|.
name|version
argument_list|()
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|this
operator|.
name|commitPoints
operator|=
name|ImmutableList
operator|.
name|copyOf
argument_list|(
name|commitPoints
argument_list|)
expr_stmt|;
block|}
DECL|method|commits
specifier|public
name|ImmutableList
argument_list|<
name|CommitPoint
argument_list|>
name|commits
parameter_list|()
block|{
return|return
name|this
operator|.
name|commitPoints
return|;
block|}
DECL|method|hasVersion
specifier|public
name|boolean
name|hasVersion
parameter_list|(
name|long
name|version
parameter_list|)
block|{
for|for
control|(
name|CommitPoint
name|commitPoint
range|:
name|commitPoints
control|)
block|{
if|if
condition|(
name|commitPoint
operator|.
name|version
argument_list|()
operator|==
name|version
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
name|CommitPoint
name|commitPoint
range|:
name|commitPoints
control|)
block|{
name|CommitPoint
operator|.
name|FileInfo
name|fileInfo
init|=
name|commitPoint
operator|.
name|findPhysicalIndexFile
argument_list|(
name|physicalName
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
for|for
control|(
name|CommitPoint
name|commitPoint
range|:
name|commitPoints
control|)
block|{
name|CommitPoint
operator|.
name|FileInfo
name|fileInfo
init|=
name|commitPoint
operator|.
name|findNameFile
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
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|iterator
specifier|public
name|Iterator
argument_list|<
name|CommitPoint
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|commitPoints
operator|.
name|iterator
argument_list|()
return|;
block|}
DECL|method|toXContent
specifier|public
specifier|static
name|byte
index|[]
name|toXContent
parameter_list|(
name|CommitPoint
name|commitPoint
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentBuilder
name|builder
init|=
name|XContentFactory
operator|.
name|contentBuilder
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|prettyPrint
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"version"
argument_list|,
name|commitPoint
operator|.
name|version
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"name"
argument_list|,
name|commitPoint
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
name|commitPoint
operator|.
name|type
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"index_files"
argument_list|)
expr_stmt|;
for|for
control|(
name|CommitPoint
operator|.
name|FileInfo
name|fileInfo
range|:
name|commitPoint
operator|.
name|indexFiles
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|fileInfo
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"physical_name"
argument_list|,
name|fileInfo
operator|.
name|physicalName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"length"
argument_list|,
name|fileInfo
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileInfo
operator|.
name|checksum
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|field
argument_list|(
literal|"checksum"
argument_list|,
name|fileInfo
operator|.
name|checksum
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startObject
argument_list|(
literal|"translog_files"
argument_list|)
expr_stmt|;
for|for
control|(
name|CommitPoint
operator|.
name|FileInfo
name|fileInfo
range|:
name|commitPoint
operator|.
name|translogFiles
argument_list|()
control|)
block|{
name|builder
operator|.
name|startObject
argument_list|(
name|fileInfo
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"physical_name"
argument_list|,
name|fileInfo
operator|.
name|physicalName
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|field
argument_list|(
literal|"length"
argument_list|,
name|fileInfo
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endObject
argument_list|()
expr_stmt|;
block|}
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
return|return
name|builder
operator|.
name|bytes
argument_list|()
operator|.
name|toBytes
argument_list|()
return|;
block|}
DECL|method|fromXContent
specifier|public
specifier|static
name|CommitPoint
name|fromXContent
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|Exception
block|{
name|XContentParser
name|parser
init|=
name|XContentFactory
operator|.
name|xContent
argument_list|(
name|XContentType
operator|.
name|JSON
argument_list|)
operator|.
name|createParser
argument_list|(
name|data
argument_list|)
decl_stmt|;
try|try
block|{
name|String
name|currentFieldName
init|=
literal|null
decl_stmt|;
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
operator|==
literal|null
condition|)
block|{
comment|// no data...
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No commit point data"
argument_list|)
throw|;
block|}
name|long
name|version
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|name
init|=
literal|null
decl_stmt|;
name|CommitPoint
operator|.
name|Type
name|type
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|CommitPoint
operator|.
name|FileInfo
argument_list|>
name|indexFiles
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|CommitPoint
operator|.
name|FileInfo
argument_list|>
name|translogFiles
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
name|List
argument_list|<
name|CommitPoint
operator|.
name|FileInfo
argument_list|>
name|files
init|=
literal|null
decl_stmt|;
if|if
condition|(
literal|"index_files"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"indexFiles"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|files
operator|=
name|indexFiles
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"translog_files"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"translogFiles"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|files
operator|=
name|translogFiles
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Can't handle object with name ["
operator|+
name|currentFieldName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
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
name|String
name|fileName
init|=
name|currentFieldName
decl_stmt|;
name|String
name|physicalName
init|=
literal|null
decl_stmt|;
name|long
name|size
init|=
operator|-
literal|1
decl_stmt|;
name|String
name|checksum
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
operator|.
name|isValue
argument_list|()
condition|)
block|{
if|if
condition|(
literal|"physical_name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
operator|||
literal|"physicalName"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|physicalName
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
literal|"length"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|size
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"checksum"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|checksum
operator|=
name|parser
operator|.
name|text
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|physicalName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Malformed commit, missing physical_name for ["
operator|+
name|fileName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Malformed commit, missing length for ["
operator|+
name|fileName
operator|+
literal|"]"
argument_list|)
throw|;
block|}
name|files
operator|.
name|add
argument_list|(
operator|new
name|CommitPoint
operator|.
name|FileInfo
argument_list|(
name|fileName
argument_list|,
name|physicalName
argument_list|,
name|size
argument_list|,
name|checksum
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"version"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|version
operator|=
name|parser
operator|.
name|longValue
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
literal|"name"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|name
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
literal|"type"
operator|.
name|equals
argument_list|(
name|currentFieldName
argument_list|)
condition|)
block|{
name|type
operator|=
name|CommitPoint
operator|.
name|Type
operator|.
name|valueOf
argument_list|(
name|parser
operator|.
name|text
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|version
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Malformed commit, missing version"
argument_list|)
throw|;
block|}
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Malformed commit, missing name"
argument_list|)
throw|;
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
name|IOException
argument_list|(
literal|"Malformed commit, missing type"
argument_list|)
throw|;
block|}
return|return
operator|new
name|CommitPoint
argument_list|(
name|version
argument_list|,
name|name
argument_list|,
name|type
argument_list|,
name|indexFiles
argument_list|,
name|translogFiles
argument_list|)
return|;
block|}
finally|finally
block|{
name|parser
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

