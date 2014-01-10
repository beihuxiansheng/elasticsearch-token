begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.parser
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|parser
package|;
end_package

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
name|section
operator|.
name|IsTrueAssertion
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
comment|/**  * Parser for is_true assert sections  */
end_comment

begin_class
DECL|class|IsTrueParser
specifier|public
class|class
name|IsTrueParser
implements|implements
name|RestTestFragmentParser
argument_list|<
name|IsTrueAssertion
argument_list|>
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|IsTrueAssertion
name|parse
parameter_list|(
name|RestTestSuiteParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|RestTestParseException
block|{
return|return
operator|new
name|IsTrueAssertion
argument_list|(
name|parseContext
operator|.
name|parseField
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

