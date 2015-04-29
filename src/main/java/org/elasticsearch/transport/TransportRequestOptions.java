begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.transport
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|transport
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
name|unit
operator|.
name|TimeValue
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TransportRequestOptions
specifier|public
class|class
name|TransportRequestOptions
block|{
DECL|field|EMPTY
specifier|public
specifier|static
specifier|final
name|TransportRequestOptions
name|EMPTY
init|=
name|options
argument_list|()
decl_stmt|;
DECL|method|options
specifier|public
specifier|static
name|TransportRequestOptions
name|options
parameter_list|()
block|{
return|return
operator|new
name|TransportRequestOptions
argument_list|()
return|;
block|}
DECL|enum|Type
specifier|public
specifier|static
enum|enum
name|Type
block|{
DECL|enum constant|RECOVERY
name|RECOVERY
block|,
DECL|enum constant|BULK
name|BULK
block|,
DECL|enum constant|REG
name|REG
block|,
DECL|enum constant|STATE
name|STATE
block|,
DECL|enum constant|PING
name|PING
block|;
DECL|method|fromString
specifier|public
specifier|static
name|Type
name|fromString
parameter_list|(
name|String
name|type
parameter_list|)
block|{
if|if
condition|(
literal|"bulk"
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|BULK
return|;
block|}
elseif|else
if|if
condition|(
literal|"reg"
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|REG
return|;
block|}
elseif|else
if|if
condition|(
literal|"state"
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|STATE
return|;
block|}
elseif|else
if|if
condition|(
literal|"recovery"
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|RECOVERY
return|;
block|}
elseif|else
if|if
condition|(
literal|"ping"
operator|.
name|equalsIgnoreCase
argument_list|(
name|type
argument_list|)
condition|)
block|{
return|return
name|PING
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"failed to match transport type for ["
operator|+
name|type
operator|+
literal|"]"
argument_list|)
throw|;
block|}
block|}
block|}
DECL|field|timeout
specifier|private
name|TimeValue
name|timeout
decl_stmt|;
DECL|field|compress
specifier|private
name|boolean
name|compress
decl_stmt|;
DECL|field|type
specifier|private
name|Type
name|type
init|=
name|Type
operator|.
name|REG
decl_stmt|;
DECL|method|withTimeout
specifier|public
name|TransportRequestOptions
name|withTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
return|return
name|withTimeout
argument_list|(
name|TimeValue
operator|.
name|timeValueMillis
argument_list|(
name|timeout
argument_list|)
argument_list|)
return|;
block|}
DECL|method|withTimeout
specifier|public
name|TransportRequestOptions
name|withTimeout
parameter_list|(
name|TimeValue
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withCompress
specifier|public
name|TransportRequestOptions
name|withCompress
parameter_list|(
name|boolean
name|compress
parameter_list|)
block|{
name|this
operator|.
name|compress
operator|=
name|compress
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|withType
specifier|public
name|TransportRequestOptions
name|withType
parameter_list|(
name|Type
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|timeout
specifier|public
name|TimeValue
name|timeout
parameter_list|()
block|{
return|return
name|this
operator|.
name|timeout
return|;
block|}
DECL|method|compress
specifier|public
name|boolean
name|compress
parameter_list|()
block|{
return|return
name|this
operator|.
name|compress
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
block|}
end_class

end_unit

