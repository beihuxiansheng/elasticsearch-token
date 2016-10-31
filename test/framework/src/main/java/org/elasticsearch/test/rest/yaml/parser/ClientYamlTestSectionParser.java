begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.test.rest.yaml.parser
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|rest
operator|.
name|yaml
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
name|common
operator|.
name|xcontent
operator|.
name|XContentParser
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
name|yaml
operator|.
name|section
operator|.
name|ClientYamlTestSection
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
comment|/**  * Parser for a complete test section  */
end_comment

begin_class
DECL|class|ClientYamlTestSectionParser
specifier|public
class|class
name|ClientYamlTestSectionParser
implements|implements
name|ClientYamlTestFragmentParser
argument_list|<
name|ClientYamlTestSection
argument_list|>
block|{
annotation|@
name|Override
DECL|method|parse
specifier|public
name|ClientYamlTestSection
name|parse
parameter_list|(
name|ClientYamlTestSuiteParseContext
name|parseContext
parameter_list|)
throws|throws
name|IOException
throws|,
name|ClientYamlTestParseException
block|{
name|XContentParser
name|parser
init|=
name|parseContext
operator|.
name|parser
argument_list|()
decl_stmt|;
name|parseContext
operator|.
name|advanceToFieldName
argument_list|()
expr_stmt|;
name|ClientYamlTestSection
name|testSection
init|=
operator|new
name|ClientYamlTestSection
argument_list|(
name|parser
operator|.
name|currentName
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|testSection
operator|.
name|setSkipSection
argument_list|(
name|parseContext
operator|.
name|parseSkipSection
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|parser
operator|.
name|currentToken
argument_list|()
operator|!=
name|XContentParser
operator|.
name|Token
operator|.
name|END_ARRAY
condition|)
block|{
name|parseContext
operator|.
name|advanceToFieldName
argument_list|()
expr_stmt|;
name|testSection
operator|.
name|addExecutableSection
argument_list|(
name|parseContext
operator|.
name|parseExecutableSection
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
assert|assert
name|parser
operator|.
name|currentToken
argument_list|()
operator|==
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|:
literal|"malformed section ["
operator|+
name|testSection
operator|.
name|getName
argument_list|()
operator|+
literal|"] expected "
operator|+
name|XContentParser
operator|.
name|Token
operator|.
name|END_OBJECT
operator|+
literal|" but was "
operator|+
name|parser
operator|.
name|currentToken
argument_list|()
assert|;
name|parser
operator|.
name|nextToken
argument_list|()
expr_stmt|;
return|return
name|testSection
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ClientYamlTestParseException
argument_list|(
literal|"Error parsing test named ["
operator|+
name|testSection
operator|.
name|getName
argument_list|()
operator|+
literal|"]"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

