begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
package|;
end_package

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|Name
import|;
end_import

begin_import
import|import
name|com
operator|.
name|carrotsearch
operator|.
name|randomizedtesting
operator|.
name|annotations
operator|.
name|ParametersFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|parser
operator|.
name|RestTestParseException
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
comment|/** Rest API tests subset 6 */
end_comment

begin_class
DECL|class|Rest6IT
specifier|public
class|class
name|Rest6IT
extends|extends
name|ESRestTestCase
block|{
DECL|method|Rest6IT
specifier|public
name|Rest6IT
parameter_list|(
annotation|@
name|Name
argument_list|(
literal|"yaml"
argument_list|)
name|RestTestCandidate
name|testCandidate
parameter_list|)
block|{
name|super
argument_list|(
name|testCandidate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ParametersFactory
DECL|method|parameters
specifier|public
specifier|static
name|Iterable
argument_list|<
name|Object
index|[]
argument_list|>
name|parameters
parameter_list|()
throws|throws
name|IOException
throws|,
name|RestTestParseException
block|{
return|return
name|createParameters
argument_list|(
literal|6
argument_list|,
literal|8
argument_list|)
return|;
block|}
block|}
end_class

end_unit

