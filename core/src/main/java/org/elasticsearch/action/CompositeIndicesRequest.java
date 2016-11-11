begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elasticsearch under one or more contributor  * license agreements. See the NOTICE file distributed with  * this work for additional information regarding copyright  * ownership. Elasticsearch licenses this file to you under  * the Apache License, Version 2.0 (the "License"); you may  * not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.action
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|action
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Needs to be implemented by all {@link org.elasticsearch.action.ActionRequest} subclasses that are composed of multiple subrequests  * which relate to one or more indices. Allows to retrieve those subrequests and reason about them separately. A composite request is  * executed by its own transport action class (e.g. {@link org.elasticsearch.action.search.TransportMultiSearchAction}), which goes  * through all the subrequests and delegates their exection to the appropriate transport action (e.g.  * {@link org.elasticsearch.action.search.TransportSearchAction}) for each single item.  */
end_comment

begin_interface
DECL|interface|CompositeIndicesRequest
specifier|public
interface|interface
name|CompositeIndicesRequest
block|{
comment|/**      * Returns the subrequests that a composite request is composed of      */
DECL|method|subRequests
name|List
argument_list|<
name|?
extends|extends
name|IndicesRequest
argument_list|>
name|subRequests
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

