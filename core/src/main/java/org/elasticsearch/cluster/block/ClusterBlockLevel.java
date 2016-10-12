begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.block
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|block
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_enum
DECL|enum|ClusterBlockLevel
specifier|public
enum|enum
name|ClusterBlockLevel
block|{
DECL|enum constant|READ
name|READ
argument_list|(
literal|0
argument_list|)
block|,
DECL|enum constant|WRITE
name|WRITE
argument_list|(
literal|1
argument_list|)
block|,
DECL|enum constant|METADATA_READ
name|METADATA_READ
argument_list|(
literal|2
argument_list|)
block|,
DECL|enum constant|METADATA_WRITE
name|METADATA_WRITE
argument_list|(
literal|3
argument_list|)
block|;
DECL|field|ALL
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|ClusterBlockLevel
argument_list|>
name|ALL
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|READ
argument_list|,
name|WRITE
argument_list|,
name|METADATA_READ
argument_list|,
name|METADATA_WRITE
argument_list|)
decl_stmt|;
DECL|field|READ_WRITE
specifier|public
specifier|static
specifier|final
name|EnumSet
argument_list|<
name|ClusterBlockLevel
argument_list|>
name|READ_WRITE
init|=
name|EnumSet
operator|.
name|of
argument_list|(
name|READ
argument_list|,
name|WRITE
argument_list|)
decl_stmt|;
DECL|field|id
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
DECL|method|ClusterBlockLevel
name|ClusterBlockLevel
parameter_list|(
name|int
name|id
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
DECL|method|id
specifier|public
name|int
name|id
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|fromId
specifier|static
name|ClusterBlockLevel
name|fromId
parameter_list|(
name|int
name|id
parameter_list|)
block|{
if|if
condition|(
name|id
operator|==
literal|0
condition|)
block|{
return|return
name|READ
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|1
condition|)
block|{
return|return
name|WRITE
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|2
condition|)
block|{
return|return
name|METADATA_READ
return|;
block|}
elseif|else
if|if
condition|(
name|id
operator|==
literal|3
condition|)
block|{
return|return
name|METADATA_WRITE
return|;
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No cluster block level matching ["
operator|+
name|id
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
end_enum

end_unit

