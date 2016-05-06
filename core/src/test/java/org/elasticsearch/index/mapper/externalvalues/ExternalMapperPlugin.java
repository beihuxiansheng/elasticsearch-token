begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper.externalvalues
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
operator|.
name|externalvalues
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
name|IndicesModule
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
name|Plugin
import|;
end_import

begin_class
DECL|class|ExternalMapperPlugin
specifier|public
class|class
name|ExternalMapperPlugin
extends|extends
name|Plugin
block|{
DECL|field|EXTERNAL
specifier|public
specifier|static
specifier|final
name|String
name|EXTERNAL
init|=
literal|"external"
decl_stmt|;
DECL|field|EXTERNAL_BIS
specifier|public
specifier|static
specifier|final
name|String
name|EXTERNAL_BIS
init|=
literal|"external_bis"
decl_stmt|;
DECL|field|EXTERNAL_UPPER
specifier|public
specifier|static
specifier|final
name|String
name|EXTERNAL_UPPER
init|=
literal|"external_upper"
decl_stmt|;
annotation|@
name|Override
DECL|method|name
specifier|public
name|String
name|name
parameter_list|()
block|{
return|return
literal|"external-mappers"
return|;
block|}
annotation|@
name|Override
DECL|method|description
specifier|public
name|String
name|description
parameter_list|()
block|{
return|return
literal|"External Mappers Plugin"
return|;
block|}
DECL|method|onModule
specifier|public
name|void
name|onModule
parameter_list|(
name|IndicesModule
name|indicesModule
parameter_list|)
block|{
name|indicesModule
operator|.
name|registerMetadataMapper
argument_list|(
name|ExternalMetadataMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|ExternalMetadataMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
name|indicesModule
operator|.
name|registerMapper
argument_list|(
name|EXTERNAL
argument_list|,
operator|new
name|ExternalMapper
operator|.
name|TypeParser
argument_list|(
name|EXTERNAL
argument_list|,
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesModule
operator|.
name|registerMapper
argument_list|(
name|EXTERNAL_BIS
argument_list|,
operator|new
name|ExternalMapper
operator|.
name|TypeParser
argument_list|(
name|EXTERNAL_BIS
argument_list|,
literal|"bar"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesModule
operator|.
name|registerMapper
argument_list|(
name|EXTERNAL_UPPER
argument_list|,
operator|new
name|ExternalMapper
operator|.
name|TypeParser
argument_list|(
name|EXTERNAL_UPPER
argument_list|,
literal|"FOO BAR"
argument_list|)
argument_list|)
expr_stmt|;
name|indicesModule
operator|.
name|registerMapper
argument_list|(
name|FakeStringFieldMapper
operator|.
name|CONTENT_TYPE
argument_list|,
operator|new
name|FakeStringFieldMapper
operator|.
name|TypeParser
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

