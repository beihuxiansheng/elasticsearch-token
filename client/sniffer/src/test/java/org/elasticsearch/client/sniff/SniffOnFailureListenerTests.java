begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.client.sniff
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|sniff
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpHost
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RestClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|client
operator|.
name|RestClientTestCase
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
DECL|class|SniffOnFailureListenerTests
specifier|public
class|class
name|SniffOnFailureListenerTests
extends|extends
name|RestClientTestCase
block|{
DECL|method|testSetSniffer
specifier|public
name|void
name|testSetSniffer
parameter_list|()
throws|throws
name|Exception
block|{
name|SniffOnFailureListener
name|listener
init|=
operator|new
name|SniffOnFailureListener
argument_list|()
decl_stmt|;
try|try
block|{
name|listener
operator|.
name|onFailure
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"sniffer was not set, unable to sniff on failure"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|listener
operator|.
name|setSniffer
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"sniffer must not be null"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|RestClient
name|restClient
init|=
name|RestClient
operator|.
name|builder
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
literal|9200
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
try|try
init|(
name|Sniffer
name|sniffer
init|=
name|Sniffer
operator|.
name|builder
argument_list|(
name|restClient
argument_list|)
operator|.
name|setHostsSniffer
argument_list|(
operator|new
name|MockHostsSniffer
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
init|)
block|{
name|listener
operator|.
name|setSniffer
argument_list|(
name|sniffer
argument_list|)
expr_stmt|;
try|try
block|{
name|listener
operator|.
name|setSniffer
argument_list|(
name|sniffer
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"should have failed"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"sniffer can only be set once"
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|listener
operator|.
name|onFailure
argument_list|(
operator|new
name|HttpHost
argument_list|(
literal|"localhost"
argument_list|,
literal|9200
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit
