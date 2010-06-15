begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent.xson
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
operator|.
name|xson
package|;
end_package

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_enum
DECL|enum|XsonType
specifier|public
enum|enum
name|XsonType
block|{
DECL|enum constant|START_ARRAY
name|START_ARRAY
argument_list|(
operator|(
name|byte
operator|)
literal|0x01
argument_list|)
block|,
DECL|enum constant|END_ARRAY
name|END_ARRAY
argument_list|(
operator|(
name|byte
operator|)
literal|0x02
argument_list|)
block|,
DECL|enum constant|START_OBJECT
name|START_OBJECT
argument_list|(
operator|(
name|byte
operator|)
literal|0x03
argument_list|)
block|,
DECL|enum constant|END_OBJECT
name|END_OBJECT
argument_list|(
operator|(
name|byte
operator|)
literal|0x04
argument_list|)
block|,
DECL|enum constant|FIELD_NAME
name|FIELD_NAME
argument_list|(
operator|(
name|byte
operator|)
literal|0x05
argument_list|)
block|,
DECL|enum constant|VALUE_STRING
name|VALUE_STRING
argument_list|(
operator|(
name|byte
operator|)
literal|0x06
argument_list|)
block|,
DECL|enum constant|VALUE_BINARY
name|VALUE_BINARY
argument_list|(
operator|(
name|byte
operator|)
literal|0x07
argument_list|)
block|,
DECL|enum constant|VALUE_INTEGER
name|VALUE_INTEGER
argument_list|(
operator|(
name|byte
operator|)
literal|0x08
argument_list|)
block|,
DECL|enum constant|VALUE_LONG
name|VALUE_LONG
argument_list|(
operator|(
name|byte
operator|)
literal|0x09
argument_list|)
block|,
DECL|enum constant|VALUE_FLOAT
name|VALUE_FLOAT
argument_list|(
operator|(
name|byte
operator|)
literal|0x0A
argument_list|)
block|,
DECL|enum constant|VALUE_DOUBLE
name|VALUE_DOUBLE
argument_list|(
operator|(
name|byte
operator|)
literal|0x0B
argument_list|)
block|,
DECL|enum constant|VALUE_BOOLEAN
name|VALUE_BOOLEAN
argument_list|(
operator|(
name|byte
operator|)
literal|0x0C
argument_list|)
block|,
DECL|enum constant|VALUE_NULL
name|VALUE_NULL
argument_list|(
operator|(
name|byte
operator|)
literal|0x0D
argument_list|)
block|,;
DECL|field|HEADER
specifier|public
specifier|static
specifier|final
name|int
name|HEADER
init|=
literal|0x00
decl_stmt|;
DECL|field|code
specifier|private
specifier|final
name|byte
name|code
decl_stmt|;
DECL|method|XsonType
name|XsonType
parameter_list|(
name|byte
name|code
parameter_list|)
block|{
name|this
operator|.
name|code
operator|=
name|code
expr_stmt|;
block|}
DECL|method|code
specifier|public
name|byte
name|code
parameter_list|()
block|{
return|return
name|code
return|;
block|}
block|}
end_enum

end_unit

