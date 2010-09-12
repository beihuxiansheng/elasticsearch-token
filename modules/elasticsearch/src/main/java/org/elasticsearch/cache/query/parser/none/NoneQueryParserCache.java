begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.cache.query.parser.none
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|query
operator|.
name|parser
operator|.
name|none
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|queryParser
operator|.
name|QueryParserSettings
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
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|cache
operator|.
name|query
operator|.
name|parser
operator|.
name|QueryParserCache
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
name|inject
operator|.
name|Inject
import|;
end_import

begin_comment
comment|/**  * @author kimchy (shay.banon)  */
end_comment

begin_class
DECL|class|NoneQueryParserCache
specifier|public
class|class
name|NoneQueryParserCache
implements|implements
name|QueryParserCache
block|{
DECL|method|NoneQueryParserCache
annotation|@
name|Inject
specifier|public
name|NoneQueryParserCache
parameter_list|()
block|{     }
DECL|method|get
annotation|@
name|Override
specifier|public
name|Query
name|get
parameter_list|(
name|QueryParserSettings
name|queryString
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
DECL|method|put
annotation|@
name|Override
specifier|public
name|void
name|put
parameter_list|(
name|QueryParserSettings
name|queryString
parameter_list|,
name|Query
name|query
parameter_list|)
block|{     }
DECL|method|clear
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{     }
block|}
end_class

end_unit

