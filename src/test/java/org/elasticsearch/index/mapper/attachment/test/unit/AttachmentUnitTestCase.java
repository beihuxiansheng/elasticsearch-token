begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.attachment.test.unit
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|attachment
operator|.
name|test
operator|.
name|unit
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
name|settings
operator|.
name|ImmutableSettings
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|common
operator|.
name|settings
operator|.
name|Settings
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
name|ElasticsearchTestCase
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|attachment
operator|.
name|test
operator|.
name|MapperTestUtils
operator|.
name|assumeCorrectLocale
import|;
end_import

begin_class
DECL|class|AttachmentUnitTestCase
specifier|public
class|class
name|AttachmentUnitTestCase
extends|extends
name|ElasticsearchTestCase
block|{
comment|/**      * We can have issues with some JVMs and Locale      * See https://github.com/elasticsearch/elasticsearch-mapper-attachments/issues/105      */
annotation|@
name|BeforeClass
DECL|method|checkLocale
specifier|public
specifier|static
name|void
name|checkLocale
parameter_list|()
block|{
name|assumeCorrectLocale
argument_list|()
expr_stmt|;
block|}
DECL|field|testSettings
specifier|protected
name|Settings
name|testSettings
decl_stmt|;
annotation|@
name|Before
DECL|method|createSettings
specifier|public
name|void
name|createSettings
parameter_list|()
throws|throws
name|Exception
block|{
name|testSettings
operator|=
name|ImmutableSettings
operator|.
name|builder
argument_list|()
operator|.
name|put
argument_list|(
literal|"path.home"
argument_list|,
name|createTempDir
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

