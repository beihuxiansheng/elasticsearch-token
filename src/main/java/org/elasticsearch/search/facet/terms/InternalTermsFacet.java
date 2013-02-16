begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.search.facet.terms
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
package|;
end_package

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|InternalFacet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
operator|.
name|doubles
operator|.
name|InternalDoubleTermsFacet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
operator|.
name|longs
operator|.
name|InternalLongTermsFacet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|elasticsearch
operator|.
name|search
operator|.
name|facet
operator|.
name|terms
operator|.
name|strings
operator|.
name|InternalStringTermsFacet
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|InternalTermsFacet
specifier|public
specifier|abstract
class|class
name|InternalTermsFacet
extends|extends
name|InternalFacet
implements|implements
name|TermsFacet
block|{
DECL|method|registerStreams
specifier|public
specifier|static
name|void
name|registerStreams
parameter_list|()
block|{
name|InternalStringTermsFacet
operator|.
name|registerStream
argument_list|()
expr_stmt|;
name|InternalLongTermsFacet
operator|.
name|registerStream
argument_list|()
expr_stmt|;
name|InternalDoubleTermsFacet
operator|.
name|registerStream
argument_list|()
expr_stmt|;
block|}
DECL|method|InternalTermsFacet
specifier|protected
name|InternalTermsFacet
parameter_list|()
block|{     }
DECL|method|InternalTermsFacet
specifier|protected
name|InternalTermsFacet
parameter_list|(
name|String
name|facetName
parameter_list|)
block|{
name|super
argument_list|(
name|facetName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getType
specifier|public
specifier|final
name|String
name|getType
parameter_list|()
block|{
return|return
name|TYPE
return|;
block|}
block|}
end_class

end_unit

