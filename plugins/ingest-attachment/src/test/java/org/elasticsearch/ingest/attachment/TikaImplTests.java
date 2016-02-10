begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
DECL|package|org.elasticsearch.ingest.attachment
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|ingest
operator|.
name|attachment
package|;
end_package

begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|test
operator|.
name|ESTestCase
import|;
end_import

begin_class
DECL|class|TikaImplTests
specifier|public
class|class
name|TikaImplTests
extends|extends
name|ESTestCase
block|{
DECL|method|testTikaLoads
specifier|public
name|void
name|testTikaLoads
parameter_list|()
throws|throws
name|Exception
block|{
name|Class
operator|.
name|forName
argument_list|(
literal|"org.elasticsearch.ingest.attachment.TikaImpl"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

