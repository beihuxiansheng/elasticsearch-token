begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to Elastic Search and Shay Banon under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership. Elastic Search licenses this  * file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *    http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
DECL|package|org.elasticsearch.util.gnu.trove
package|package
name|org
operator|.
name|elasticsearch
operator|.
name|util
operator|.
name|gnu
operator|.
name|trove
package|;
end_package

begin_comment
comment|/**  * Interface for functions that accept and return one Object reference.  *<p/>  * Created: Mon Nov  5 22:19:36 2001  *  * @author Eric D. Friedman  * @version $Id: TObjectFunction.java,v 1.3 2006/11/10 23:27:56 robeden Exp $  */
end_comment

begin_interface
DECL|interface|TObjectFunction
specifier|public
interface|interface
name|TObjectFunction
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
block|{
comment|/**      * Execute this function with<tt>value</tt>      *      * @param value an<code>Object</code> input      * @return an<code>Object</code> result      */
DECL|method|execute
specifier|public
name|R
name|execute
parameter_list|(
name|T
name|value
parameter_list|)
function_decl|;
block|}
end_interface

begin_comment
comment|// TObjectFunction
end_comment

end_unit

