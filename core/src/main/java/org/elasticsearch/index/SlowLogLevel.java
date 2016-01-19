begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_enum
DECL|enum|SlowLogLevel
specifier|public
enum|enum
name|SlowLogLevel
block|{
DECL|enum constant|WARN
DECL|enum constant|TRACE
DECL|enum constant|INFO
DECL|enum constant|DEBUG
name|WARN
block|,
name|TRACE
block|,
name|INFO
block|,
name|DEBUG
block|;
DECL|method|parse
specifier|public
specifier|static
name|SlowLogLevel
name|parse
parameter_list|(
name|String
name|level
parameter_list|)
block|{
return|return
name|valueOf
argument_list|(
name|level
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ROOT
argument_list|)
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

