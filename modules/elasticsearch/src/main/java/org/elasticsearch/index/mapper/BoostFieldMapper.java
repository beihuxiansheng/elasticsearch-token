begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.index.mapper
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|index
operator|.
name|mapper
package|;
end_package

begin_comment
comment|/**  * A field mapper that allows to control the boosting of a parsed document. Can be treated as  * any other field mapper by being stored and analyzed, though, by default, it does neither.  *  * @author kimchy (Shay Banon)  */
end_comment

begin_interface
DECL|interface|BoostFieldMapper
specifier|public
interface|interface
name|BoostFieldMapper
extends|extends
name|FieldMapper
argument_list|<
name|Float
argument_list|>
extends|,
name|InternalMapper
block|{  }
end_interface

end_unit

