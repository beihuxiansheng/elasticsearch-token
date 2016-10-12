begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cluster.health
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cluster
operator|.
name|health
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

begin_enum
DECL|enum|ClusterHealthStatus
specifier|public
enum|enum
name|ClusterHealthStatus
implements|implements
name|Writeable
block|{
DECL|enum constant|GREEN
name|GREEN
argument_list|(
operator|(
name|byte
operator|)
literal|0
argument_list|)
block|,
DECL|enum constant|YELLOW
name|YELLOW
argument_list|(
operator|(
name|byte
operator|)
literal|1
argument_list|)
block|,
DECL|enum constant|RED
name|RED
argument_list|(
operator|(
name|byte
operator|)
literal|2
argument_list|)
block|;
DECL|field|value
specifier|private
name|byte
name|value
decl_stmt|;
DECL|method|ClusterHealthStatus
name|ClusterHealthStatus
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
DECL|method|value
specifier|public
name|byte
name|value
parameter_list|()
block|{
return|return
name|value
return|;
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
name|writeByte
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Read from a stream.      *      * @throws IllegalArgumentException if the value is unrecognized      */
DECL|method|readFrom
specifier|public
specifier|static
name|ClusterHealthStatus
name|readFrom
parameter_list|(
name|StreamInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|fromValue
argument_list|(
name|in
operator|.
name|readByte
argument_list|()
argument_list|)
return|;
block|}
DECL|method|fromValue
specifier|public
specifier|static
name|ClusterHealthStatus
name|fromValue
parameter_list|(
name|byte
name|value
parameter_list|)
throws|throws
name|IOException
block|{
switch|switch
condition|(
name|value
condition|)
block|{
case|case
literal|0
case|:
return|return
name|GREEN
return|;
case|case
literal|1
case|:
return|return
name|YELLOW
return|;
case|case
literal|2
case|:
return|return
name|RED
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No cluster health status for value ["
operator|+
name|value
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
DECL|method|fromString
specifier|public
specifier|static
name|ClusterHealthStatus
name|fromString
parameter_list|(
name|String
name|status
parameter_list|)
block|{
if|if
condition|(
name|status
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"green"
argument_list|)
condition|)
block|{
return|return
name|GREEN
return|;
block|}
elseif|else
if|if
condition|(
name|status
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yellow"
argument_list|)
condition|)
block|{
return|return
name|YELLOW
return|;
block|}
elseif|else
if|if
condition|(
name|status
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"red"
argument_list|)
condition|)
block|{
return|return
name|RED
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"unknown cluster health status ["
operator|+
name|status
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
end_enum

end_unit

