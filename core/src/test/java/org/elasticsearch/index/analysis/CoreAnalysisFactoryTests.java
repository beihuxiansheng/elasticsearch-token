begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.analysis
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|analysis
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|indices
operator|.
name|analysis
operator|.
name|AnalysisFactoryTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|plugins
operator|.
name|AnalysisPlugin
import|;
end_import

begin_comment
comment|/**  * Checks on the analysis components that are part of core to make sure that any that are added  * to lucene are either enabled or explicitly not enabled. During the migration of analysis  * components to the {@code analysis-common} module this test ignores many components that are  * available to es-core but mapping in {@code analysis-common}. When the migration is complete  * no such ignoring will be needed because the analysis components won't be available to core.  */
end_comment

begin_class
DECL|class|CoreAnalysisFactoryTests
specifier|public
class|class
name|CoreAnalysisFactoryTests
extends|extends
name|AnalysisFactoryTestCase
block|{
DECL|method|CoreAnalysisFactoryTests
specifier|public
name|CoreAnalysisFactoryTests
parameter_list|()
block|{
comment|// Use an empty plugin that doesn't define anything so the test doesn't need a ton of null checks.
name|super
argument_list|(
operator|new
name|AnalysisPlugin
argument_list|()
block|{}
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

