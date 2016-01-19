begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.fielddata
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|fielddata
package|;
end_package

begin_comment
comment|/**  */
end_comment

begin_interface
DECL|interface|IndexNumericFieldData
specifier|public
interface|interface
name|IndexNumericFieldData
extends|extends
name|IndexFieldData
argument_list|<
name|AtomicNumericFieldData
argument_list|>
block|{
DECL|enum|NumericType
specifier|public
specifier|static
enum|enum
name|NumericType
block|{
DECL|enum constant|BOOLEAN
name|BOOLEAN
argument_list|(
literal|false
argument_list|)
block|,
DECL|enum constant|BYTE
name|BYTE
argument_list|(
literal|false
argument_list|)
block|,
DECL|enum constant|SHORT
name|SHORT
argument_list|(
literal|false
argument_list|)
block|,
DECL|enum constant|INT
name|INT
argument_list|(
literal|false
argument_list|)
block|,
DECL|enum constant|LONG
name|LONG
argument_list|(
literal|false
argument_list|)
block|,
DECL|enum constant|FLOAT
name|FLOAT
argument_list|(
literal|true
argument_list|)
block|,
DECL|enum constant|DOUBLE
name|DOUBLE
argument_list|(
literal|true
argument_list|)
block|;
DECL|field|floatingPoint
specifier|private
specifier|final
name|boolean
name|floatingPoint
decl_stmt|;
DECL|method|NumericType
specifier|private
name|NumericType
parameter_list|(
name|boolean
name|floatingPoint
parameter_list|)
block|{
name|this
operator|.
name|floatingPoint
operator|=
name|floatingPoint
expr_stmt|;
block|}
DECL|method|isFloatingPoint
specifier|public
specifier|final
name|boolean
name|isFloatingPoint
parameter_list|()
block|{
return|return
name|floatingPoint
return|;
block|}
block|}
DECL|method|getNumericType
name|NumericType
name|getNumericType
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

