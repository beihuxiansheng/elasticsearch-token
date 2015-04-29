begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.common.xcontent
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|xcontent
package|;
end_package

begin_comment
comment|/**  * Simple data structure representing the line and column number of a position  * in some XContent e.g. JSON. Locations are typically used to communicate the  * position of a parsing error to end users and consequently have line and  * column numbers starting from 1.  */
end_comment

begin_class
DECL|class|XContentLocation
specifier|public
class|class
name|XContentLocation
block|{
DECL|field|lineNumber
specifier|public
specifier|final
name|int
name|lineNumber
decl_stmt|;
DECL|field|columnNumber
specifier|public
specifier|final
name|int
name|columnNumber
decl_stmt|;
DECL|method|XContentLocation
specifier|public
name|XContentLocation
parameter_list|(
name|int
name|lineNumber
parameter_list|,
name|int
name|columnNumber
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|lineNumber
operator|=
name|lineNumber
expr_stmt|;
name|this
operator|.
name|columnNumber
operator|=
name|columnNumber
expr_stmt|;
block|}
block|}
end_class

end_unit

