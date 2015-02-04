begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.discovery.azure
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|discovery
operator|.
name|azure
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|Instance
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cloud
operator|.
name|azure
operator|.
name|management
operator|.
name|AzureComputeServiceImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_class
DECL|class|AzureInstanceXmlParserTest
specifier|public
class|class
name|AzureInstanceXmlParserTest
block|{
DECL|method|build
specifier|private
name|Instance
name|build
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|privateIpAddress
parameter_list|,
name|String
name|publicIpAddress
parameter_list|,
name|String
name|publicPort
parameter_list|,
name|Instance
operator|.
name|Status
name|status
parameter_list|)
block|{
name|Instance
name|instance
init|=
operator|new
name|Instance
argument_list|()
decl_stmt|;
name|instance
operator|.
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|instance
operator|.
name|setPrivateIp
argument_list|(
name|privateIpAddress
argument_list|)
expr_stmt|;
name|instance
operator|.
name|setPublicIp
argument_list|(
name|publicIpAddress
argument_list|)
expr_stmt|;
name|instance
operator|.
name|setPublicPort
argument_list|(
name|publicPort
argument_list|)
expr_stmt|;
name|instance
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
return|return
name|instance
return|;
block|}
annotation|@
name|Test
DECL|method|testReadXml
specifier|public
name|void
name|testReadXml
parameter_list|()
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|XPathExpressionException
throws|,
name|IOException
block|{
name|InputStream
name|inputStream
init|=
name|AzureInstanceXmlParserTest
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"/org/elasticsearch/azure/test/services.xml"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Instance
argument_list|>
name|instances
init|=
name|AzureComputeServiceImpl
operator|.
name|buildInstancesFromXml
argument_list|(
name|inputStream
argument_list|,
literal|"elasticsearch"
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Instance
argument_list|>
name|expected
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|build
argument_list|(
literal|"es-windows2008"
argument_list|,
literal|"10.53.250.55"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|Instance
operator|.
name|Status
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|expected
operator|.
name|add
argument_list|(
name|build
argument_list|(
literal|"myesnode1"
argument_list|,
literal|"10.53.218.75"
argument_list|,
literal|"137.116.213.150"
argument_list|,
literal|"9300"
argument_list|,
name|Instance
operator|.
name|Status
operator|.
name|STARTED
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertArrayEquals
argument_list|(
name|expected
operator|.
name|toArray
argument_list|()
argument_list|,
name|instances
operator|.
name|toArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

