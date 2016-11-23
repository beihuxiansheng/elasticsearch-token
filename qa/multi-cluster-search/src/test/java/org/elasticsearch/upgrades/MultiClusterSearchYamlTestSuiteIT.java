begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.upgrades
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|upgrades
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
name|ParametersFactory
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
name|TimeoutSuite
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|TimeUnits
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
name|ClientYamlTestCandidate
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
name|ESClientYamlSuiteTestCase
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
name|parser
operator|.
name|ClientYamlTestParseException
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

begin_class
annotation|@
name|TimeoutSuite
argument_list|(
name|millis
operator|=
literal|5
operator|*
name|TimeUnits
operator|.
name|MINUTE
argument_list|)
comment|// to account for slow as hell VMs
DECL|class|MultiClusterSearchYamlTestSuiteIT
specifier|public
class|class
name|MultiClusterSearchYamlTestSuiteIT
extends|extends
name|ESClientYamlSuiteTestCase
block|{
annotation|@
name|Override
DECL|method|preserveIndicesUponCompletion
specifier|protected
name|boolean
name|preserveIndicesUponCompletion
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|MultiClusterSearchYamlTestSuiteIT
specifier|public
name|MultiClusterSearchYamlTestSuiteIT
parameter_list|(
name|ClientYamlTestCandidate
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
name|ClientYamlTestParseException
block|{
return|return
name|createParameters
argument_list|()
return|;
block|}
block|}
end_class

end_unit

