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
name|elasticsearch
operator|.
name|ElasticsearchException
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
name|io
operator|.
name|stream
operator|.
name|Writeable
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
comment|/** Specifies how a geo query should be run. */
end_comment

begin_enum
DECL|enum|GeoExecType
specifier|public
enum|enum
name|GeoExecType
implements|implements
name|Writeable
block|{
DECL|enum constant|MEMORY
DECL|enum constant|INDEXED
name|MEMORY
argument_list|(
literal|0
argument_list|)
block|,
name|INDEXED
argument_list|(
literal|1
argument_list|)
block|;
DECL|field|ordinal
specifier|private
specifier|final
name|int
name|ordinal
decl_stmt|;
DECL|method|GeoExecType
name|GeoExecType
parameter_list|(
name|int
name|ordinal
parameter_list|)
block|{
name|this
operator|.
name|ordinal
operator|=
name|ordinal
expr_stmt|;
block|}
DECL|method|readFromStream
specifier|public
specifier|static
name|GeoExecType
name|readFromStream
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|ord
init|=
name|in
operator|.
name|readVInt
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|ord
condition|)
block|{
case|case
operator|(
literal|0
operator|)
case|:
return|return
name|MEMORY
return|;
case|case
operator|(
literal|1
operator|)
case|:
return|return
name|INDEXED
return|;
block|}
throw|throw
operator|new
name|ElasticsearchException
argument_list|(
literal|"unknown serialized type ["
operator|+
name|ord
operator|+
literal|"]"
argument_list|)
throw|;
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
name|out
operator|.
name|writeVInt
argument_list|(
name|this
operator|.
name|ordinal
argument_list|)
expr_stmt|;
block|}
DECL|method|fromString
specifier|public
specifier|static
name|GeoExecType
name|fromString
parameter_list|(
name|String
name|typeName
parameter_list|)
block|{
if|if
condition|(
name|typeName
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"cannot parse type from null string"
argument_list|)
throw|;
block|}
for|for
control|(
name|GeoExecType
name|type
range|:
name|GeoExecType
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|type
operator|.
name|name
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|typeName
argument_list|)
condition|)
block|{
return|return
name|type
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"no type can be parsed from ordinal "
operator|+
name|typeName
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit

